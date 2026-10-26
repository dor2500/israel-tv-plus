package com.israeltv.plus.data

import android.annotation.SuppressLint
import android.net.ssl.SSLSockets
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509TrustManager

/*
 * A small adb client, enough to put the watch app on a watch from the phone: Android 11+ "Wireless debugging"
 * pairing (a six-digit code), the TLS connection and `cmd package install`. The same protocol as `adb pair`,
 * `adb connect` and `adb install` (AOSP packages/modules/adb: pairing_auth, pairing_connection, adb protocol.md),
 * with BoringSSL's SPAKE2 (crypto/curve25519/spake25519.c) written out below.
 */

class AdbException(message: String) : Exception(message)

// ---------------------------------------------------------------- the phone's adb identity

/** This phone's adb key (RSA 2048, kept in the app's files) and a self-signed certificate for the TLS handshakes. */
class AdbKey private constructor(val privateKey: PrivateKey, val publicKey: RSAPublicKey, val certificate: X509Certificate) {

    /** The key as adb stores it in adb_keys: base64(android_pubkey) + " name" + NUL. */
    val adbPublicKey: ByteArray by lazy {
        val n = publicKey.modulus
        val r32 = BigInteger.ONE.shiftLeft(32)
        val n0inv = n.mod(r32).modInverse(r32).negate().mod(r32)
        val rr = BigInteger.ONE.shiftLeft(4096).mod(n)
        val b = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN)
        b.putInt(64)
        b.putInt(n0inv.toInt())
        b.put(le(n, 256))
        b.put(le(rr, 256))
        b.putInt(publicKey.publicExponent.toInt())
        Base64.encode(b.array(), Base64.NO_WRAP) + " israeltvplus@phone\u0000".toByteArray()
    }

    /** Signs an AUTH token the old way (adb before Android 11: RSA PKCS#1 over a SHA-1 DigestInfo of the token). */
    fun signToken(token: ByteArray): ByteArray {
        val c = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        c.init(Cipher.ENCRYPT_MODE, privateKey)
        return c.doFinal(SHA1_PREFIX + token)
    }

    fun sslContext(): SSLContext {
        val km = object : X509ExtendedKeyManager() {
            override fun getClientAliases(keyType: String?, issuers: Array<out java.security.Principal>?) = arrayOf("adb")
            override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out java.security.Principal>?, socket: Socket?) = "adb"
            override fun getServerAliases(keyType: String?, issuers: Array<out java.security.Principal>?) = null
            override fun chooseServerAlias(keyType: String?, issuers: Array<out java.security.Principal>?, socket: Socket?) = null
            override fun getCertificateChain(alias: String?) = arrayOf(certificate)
            override fun getPrivateKey(alias: String?) = privateKey
        }
        // the watch's certificate is not checked: who it is was settled by the pairing code (as adb does)
        @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
        val tm = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }
        return SSLContext.getInstance("TLSv1.3").apply { init(arrayOf<KeyManager>(km), arrayOf(tm), SecureRandom()) }
    }

    companion object {
        private val SHA1_PREFIX = byteArrayOf(0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e, 0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14)

        fun load(dir: File): AdbKey {
            val file = File(dir, "adbkey.pk8")
            val factory = KeyFactory.getInstance("RSA")
            val priv: PrivateKey
            val pub: RSAPublicKey
            if (file.isFile) {
                priv = factory.generatePrivate(PKCS8EncodedKeySpec(file.readBytes()))
                val crt = priv as java.security.interfaces.RSAPrivateCrtKey
                pub = factory.generatePublic(java.security.spec.RSAPublicKeySpec(crt.modulus, crt.publicExponent)) as RSAPublicKey
            } else {
                val kp = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
                priv = kp.private
                pub = kp.public as RSAPublicKey
                file.writeBytes(priv.encoded)
            }
            return AdbKey(priv, pub, selfSigned(priv, pub))
        }

        /** A minimal X.509 v3 certificate (CN=00) signed with the key itself – all a TLS client certificate needs here. */
        private fun selfSigned(priv: PrivateKey, pub: RSAPublicKey): X509Certificate {
            val sha256Rsa = seq(oid(byteArrayOf(0x2a, 0x86.toByte(), 0x48, 0x86.toByte(), 0xf7.toByte(), 0x0d, 0x01, 0x01, 0x0b)), byteArrayOf(0x05, 0x00))
            val name = seq(tlv(0x31, seq(oid(byteArrayOf(0x55, 0x04, 0x03)), tlv(0x0c, "00".toByteArray()))))
            val validity = seq(tlv(0x17, "200101000000Z".toByteArray()), tlv(0x18, "20500101000000Z".toByteArray()))
            val tbs = seq(tlv(0xa0, tlv(0x02, byteArrayOf(2))), tlv(0x02, byteArrayOf(1)), sha256Rsa, name, validity, name, pub.encoded)
            val sig = Signature.getInstance("SHA256withRSA").run { initSign(priv); update(tbs); sign() }
            val der = seq(tbs, sha256Rsa, tlv(0x03, byteArrayOf(0) + sig))
            return CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(der)) as X509Certificate
        }

        private fun oid(body: ByteArray) = tlv(0x06, body)
        private fun seq(vararg parts: ByteArray) = tlv(0x30, parts.fold(ByteArray(0)) { a, b -> a + b })
        private fun tlv(tag: Int, body: ByteArray): ByteArray {
            val len = when {
                body.size < 0x80 -> byteArrayOf(body.size.toByte())
                body.size < 0x100 -> byteArrayOf(0x81.toByte(), body.size.toByte())
                else -> byteArrayOf(0x82.toByte(), (body.size shr 8).toByte(), body.size.toByte())
            }
            return byteArrayOf(tag.toByte()) + len + body
        }
    }
}

private fun le(v: BigInteger, size: Int): ByteArray {
    val be = v.toByteArray()
    val out = ByteArray(size)
    for (i in 0 until minOf(size, be.size)) out[i] = be[be.size - 1 - i]
    return out
}

private fun fromLe(b: ByteArray): BigInteger = BigInteger(1, b.reversedArray())

// ---------------------------------------------------------------- Ed25519 points and BoringSSL's SPAKE2

private object Ed25519 {
    val p: BigInteger = BigInteger.ONE.shiftLeft(255).subtract(BigInteger.valueOf(19))
    val l: BigInteger = BigInteger.ONE.shiftLeft(252).add(BigInteger("27742317777372353535851937790883648493"))
    val d: BigInteger = BigInteger.valueOf(-121665).multiply(BigInteger.valueOf(121666).modInverse(p)).mod(p)
    private val sqrtM1 = BigInteger.valueOf(2).modPow(p.subtract(BigInteger.ONE).shiftRight(2), p)

    data class Pt(val x: BigInteger, val y: BigInteger)

    val zero = Pt(BigInteger.ZERO, BigInteger.ONE)
    val base: Pt = decode(le(BigInteger.valueOf(4).multiply(BigInteger.valueOf(5).modInverse(p)).mod(p), 32))!!

    fun add(a: Pt, b: Pt): Pt {
        val t = d.multiply(a.x).multiply(b.x).multiply(a.y).multiply(b.y).mod(p)
        val x = a.x.multiply(b.y).add(b.x.multiply(a.y)).multiply(BigInteger.ONE.add(t).modInverse(p)).mod(p)
        val y = a.y.multiply(b.y).add(a.x.multiply(b.x)).multiply(BigInteger.ONE.subtract(t).mod(p).modInverse(p)).mod(p)
        return Pt(x, y)
    }

    fun neg(a: Pt) = Pt(a.x.negate().mod(p), a.y)

    fun mul(pt: Pt, k: BigInteger): Pt {
        var r = zero
        var q = pt
        for (i in 0 until k.bitLength()) {
            if (k.testBit(i)) r = add(r, q)
            q = add(q, q)
        }
        return r
    }

    fun encode(a: Pt): ByteArray = le(if (a.x.testBit(0)) a.y.setBit(255) else a.y, 32)

    /** null when the bytes are not a point of the curve (as x25519_ge_frombytes_vartime). */
    fun decode(s: ByteArray): Pt? {
        val v = fromLe(s)
        val y = v.clearBit(255)
        if (y >= p) return null
        val u = y.multiply(y).subtract(BigInteger.ONE).mod(p)
        val w = d.multiply(y).multiply(y).add(BigInteger.ONE).mod(p)
        val xx = u.multiply(w.modInverse(p)).mod(p)
        var x = xx.modPow(p.add(BigInteger.valueOf(3)).shiftRight(3), p)
        if (x.multiply(x).subtract(xx).mod(p).signum() != 0) x = x.multiply(sqrtM1).mod(p)
        if (x.multiply(x).subtract(xx).mod(p).signum() != 0) return null
        if (x.signum() == 0 && v.testBit(255)) return null
        if (x.testBit(0) != v.testBit(255)) x = p.subtract(x)
        return Pt(x, y)
    }
}

/** SPAKE2 as BoringSSL does it (spake25519.c), from the "alice" side: adb's pairing client. */
private class Spake2(password: ByteArray) {
    private val myName = "adb pair client\u0000".toByteArray()
    private val theirName = "adb pair server\u0000".toByteArray()
    private val secret: BigInteger
    private val passwordHash = MessageDigest.getInstance("SHA-512").digest(password)
    private val passwordScalar: BigInteger
    val message: ByteArray

    init {
        val r = ByteArray(64).also { SecureRandom().nextBytes(it) }
        secret = fromLe(r).mod(Ed25519.l).shiftLeft(3)   // times the cofactor
        var w = fromLe(passwordHash).mod(Ed25519.l)
        // BoringSSL makes the password scalar a multiple of eight by adding multiples of the group order
        if (w.testBit(0)) w = w.add(Ed25519.l)
        if (w.testBit(1)) w = w.add(Ed25519.l.shiftLeft(1))
        if (w.testBit(2)) w = w.add(Ed25519.l.shiftLeft(2))
        passwordScalar = w
        val pStar = Ed25519.add(Ed25519.mul(Ed25519.base, secret), Ed25519.mul(M, w))
        message = Ed25519.encode(pStar)
    }

    fun process(theirs: ByteArray): ByteArray {
        val q = Ed25519.decode(theirs) ?: throw AdbException("bad pairing message")
        val unmasked = Ed25519.add(q, Ed25519.neg(Ed25519.mul(N, passwordScalar)))
        val shared = Ed25519.encode(Ed25519.mul(unmasked, secret))
        val h = MessageDigest.getInstance("SHA-512")
        for (part in listOf(myName, theirName, message, theirs, shared, passwordHash)) {
            h.update(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(part.size.toLong()).array())
            h.update(part)
        }
        return h.digest()
    }

    private companion object {
        // the points BoringSSL derives from SHA-256("edwards25519 point generation seed (M)" / "(N)")
        val M = Ed25519.decode(hex("5ada7e4bf6ddd9adb6626d32131c6b5c51a1e347a3478f53cfcf441b88eed12e"))!!
        val N = Ed25519.decode(hex("10e3df0ae37d8e7a99b5fe74b44672103dbddcbd06af680d71329a11693bc778"))!!
        fun hex(s: String) = ByteArray(s.length / 2) { s.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
    }
}

// ---------------------------------------------------------------- pairing (adb pair)

/**
 * Pairs this phone with a watch in "Wireless debugging → Pair new device": TLS 1.3, the six-digit code + the TLS
 * keying material through SPAKE2, then both sides swap their keys encrypted with AES-128-GCM.
 * Needs Android 10 (exporting the TLS keying material). Returns the watch's name.
 */
@androidx.annotation.RequiresApi(29)
fun adbPair(key: AdbKey, host: String, port: Int, code: String): String {
    val raw = Socket().apply { connect(InetSocketAddress(host, port), 10_000); soTimeout = 15_000 }
    val ssl = key.sslContext().socketFactory.createSocket(raw, host, port, true) as SSLSocket
    ssl.use {
        it.useClientMode = true
        it.startHandshake()
        val ekm = SSLSockets.exportKeyingMaterial(it, "adb-label\u0000", null, 64)
            ?: throw AdbException("TLS keying material")
        val spake = Spake2(code.trim().toByteArray() + ekm)
        val out = it.outputStream
        val inp = DataInputStream(it.inputStream)
        writePairingPacket(out, 0, spake.message)
        val theirs = readPairingPacket(inp, 0)
        val secret = spake.process(theirs)
        val aes = hkdfSha256(secret, "adb pairing_auth aes-128-gcm key".toByteArray(), 16)
        // PeerInfo: one type byte (0 = RSA public key) + 8191 bytes of data
        val peer = ByteArray(8192).also { p -> key.adbPublicKey.copyInto(p, 1, 0, minOf(8191, key.adbPublicKey.size)) }
        writePairingPacket(out, 1, gcm(Cipher.ENCRYPT_MODE, aes, peer))
        val reply = readPairingPacket(inp, 1)
        val info = try { gcm(Cipher.DECRYPT_MODE, aes, reply) } catch (e: Exception) { throw AdbException("wrong code") }
        return String(info, 1, info.size - 1).substringBefore('\u0000')
    }
}

private fun writePairingPacket(out: OutputStream, type: Int, payload: ByteArray) {
    out.write(ByteBuffer.allocate(6 + payload.size).put(1).put(type.toByte()).putInt(payload.size).put(payload).array())
    out.flush()
}

private fun readPairingPacket(inp: DataInputStream, type: Int): ByteArray {
    val version = inp.readUnsignedByte()
    val t = inp.readUnsignedByte()
    val size = inp.readInt()
    if (version != 1 || t != type || size !in 1..16384) throw AdbException("wrong code")
    return ByteArray(size).also { inp.readFully(it) }
}

/** The first (and only) message each way: nonce = sequence number 0. */
private fun gcm(mode: Int, key: ByteArray, data: ByteArray): ByteArray =
    Cipher.getInstance("AES/GCM/NoPadding").run {
        init(mode, SecretKeySpec(key, "AES"), GCMParameterSpec(128, ByteArray(12)))
        doFinal(data)
    }

private fun hkdfSha256(ikm: ByteArray, info: ByteArray, size: Int): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(ByteArray(32), "HmacSHA256"))
    val prk = mac.doFinal(ikm)
    mac.init(SecretKeySpec(prk, "HmacSHA256"))
    mac.update(info)
    mac.update(1)
    return mac.doFinal().copyOf(size)
}

// ---------------------------------------------------------------- the adb connection and install

private const val CNXN = 0x4e584e43
private const val AUTH = 0x48545541
private const val OPEN = 0x4e45504f
private const val OKAY = 0x59414b4f
private const val CLSE = 0x45534c43
private const val WRTE = 0x45545257
private const val STLS = 0x534c5453

private class Msg(val cmd: Int, val arg0: Int, val arg1: Int, val data: ByteArray)

/**
 * One adb connection to the watch: TLS (Wireless debugging, Android 11+) or, for watches with the older
 * "Debug over Wi-Fi" (port 5555), the RSA AUTH handshake – the watch then asks once to allow this phone.
 */
class AdbConnection private constructor(private var socket: Socket, private val key: AdbKey) : AutoCloseable {
    private lateinit var inp: DataInputStream
    private lateinit var out: OutputStream
    private var maxData = 4096
    /** The device's model from the connect banner ("device::ro.product.model=Galaxy Watch6;..."). */
    var model = ""
        private set

    private fun streams() {
        inp = DataInputStream(socket.getInputStream().buffered(64 * 1024))
        out = socket.getOutputStream().buffered(64 * 1024)
    }

    private fun send(cmd: Int, a0: Int, a1: Int, data: ByteArray = ByteArray(0)) {
        var sum = 0
        for (b in data) sum += b.toInt() and 0xff
        val h = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(cmd).putInt(a0).putInt(a1).putInt(data.size).putInt(sum).putInt(cmd.inv())
        out.write(h.array())
        out.write(data)
        out.flush()
    }

    private fun read(): Msg {
        val h = ByteArray(24).also { inp.readFully(it) }
        val b = ByteBuffer.wrap(h).order(ByteOrder.LITTLE_ENDIAN)
        val cmd = b.int
        val a0 = b.int
        val a1 = b.int
        val n = b.int
        if (n < 0 || n > 16 * 1024 * 1024) throw AdbException("bad adb message")
        return Msg(cmd, a0, a1, ByteArray(n).also { inp.readFully(it) })
    }

    private fun handshake(host: String, port: Int, onAllowPrompt: () -> Unit) {
        streams()
        send(CNXN, 0x01000001, 1024 * 1024, "host::\u0000".toByteArray())
        var m = read()
        if (m.cmd == STLS) {
            send(STLS, 0x01000000, 0)
            val ssl = key.sslContext().socketFactory.createSocket(socket, host, port, true) as SSLSocket
            ssl.useClientMode = true
            ssl.startHandshake()
            socket = ssl
            streams()
            m = read()
        }
        var signed = false
        while (m.cmd == AUTH) {
            if (!signed) {
                signed = true
                send(AUTH, 2, 0, key.signToken(m.data))
            } else {
                // the watch does not know this phone yet: it shows "Allow debugging?" – wait for the tap
                onAllowPrompt()
                socket.soTimeout = 60_000
                send(AUTH, 3, 0, key.adbPublicKey)
            }
            m = read()
        }
        if (m.cmd != CNXN) throw AdbException("no connection")
        maxData = m.arg1.coerceIn(4096, 1024 * 1024)
        model = Regex("ro\\.product\\.model=([^;\\u0000]+)").find(String(m.data))?.groupValues?.get(1)?.trim().orEmpty()
        socket.soTimeout = 30_000
    }

    /** `cmd package install -S`: the APK is streamed in; the watch answers "Success" or "Failure [...]". */
    fun install(apk: InputStream, size: Long, progress: (Float) -> Unit): String {
        send(OPEN, 1, 0, "exec:cmd package install -S $size\u0000".toByteArray())
        var m = read()
        if (m.cmd != OKAY) throw AdbException("install refused")
        val remote = m.arg0
        val result = ByteArrayOutputStream()
        val buf = ByteArray(minOf(maxData, 256 * 1024))
        var sent = 0L
        socket.soTimeout = 120_000
        while (sent < size) {
            val n = apk.read(buf)
            if (n < 0) break
            send(WRTE, 1, remote, buf.copyOf(n))
            sent += n
            progress(sent.toFloat() / size)
            while (true) {
                m = read()
                when (m.cmd) {
                    OKAY -> break
                    WRTE -> { result.write(m.data); send(OKAY, 1, remote) }
                    CLSE -> return result.toString()
                }
            }
        }
        while (true) {
            m = read()
            when (m.cmd) {
                WRTE -> { result.write(m.data); send(OKAY, 1, remote) }
                CLSE -> { runCatching { send(CLSE, 1, remote) }; return result.toString() }
            }
        }
    }

    override fun close() { runCatching { socket.close() } }

    companion object {
        fun open(key: AdbKey, host: String, port: Int, onAllowPrompt: () -> Unit = {}): AdbConnection {
            val s = Socket().apply { connect(InetSocketAddress(host, port), 10_000); soTimeout = 15_000; tcpNoDelay = true }
            return AdbConnection(s, key).also {
                try { it.handshake(host, port, onAllowPrompt) } catch (e: Exception) { it.close(); throw e }
            }
        }
    }
}
