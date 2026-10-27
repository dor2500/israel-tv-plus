package com.israeltv.plus.data

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

@Immutable
data class EpgProgram(
    val title: String,
    val desc: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val startLabel: String,
    val endLabel: String,
    val badge: String = "",
) {
    fun isPast(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs >= endTimeMs
    fun isLive(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs in startTimeMs until endTimeMs
    fun isUpcoming(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs < startTimeMs

    fun progress(nowMs: Long = System.currentTimeMillis()): Float {
        val total = (endTimeMs - startTimeMs).coerceAtLeast(1L)
        return ((nowMs - startTimeMs).toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    fun offsetSecondsFromNow(nowMs: Long = System.currentTimeMillis()): Long =
        ((nowMs - startTimeMs) / 1000L).coerceAtLeast(0L)
}

@Immutable
data class ChannelEpg(
    val channelId: String,
    val past: List<EpgProgram>,
    val current: EpgProgram,
    val upcoming: List<EpgProgram>,
) {
    val allPrograms: List<EpgProgram> get() = past + listOf(current) + upcoming
}

object EpgRepository {
    private val ilTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Jerusalem")

    private fun formatHm(ms: Long): String {
        val fmt = SimpleDateFormat("HH:mm", Locale.US)
        fmt.timeZone = ilTimeZone
        return fmt.format(Date(ms))
    }

    /**
     * Returns a structured EPG schedule (4 past Catch-Up programs, 1 current LIVE program, 8 upcoming programs)
     * for any channel in the catalog, synchronized to Israel time (Asia/Jerusalem).
     */
    fun getSchedule(channel: Channel, nowMs: Long = System.currentTimeMillis()): ChannelEpg {
        val slots = buildDaySlots(channel, nowMs)
        val currentIdx = slots.indexOfFirst { nowMs in it.startTimeMs until it.endTimeMs }
            .let { if (it >= 0) it else (slots.size / 2) }

        val current = slots[currentIdx]
        val past = slots.subList((currentIdx - 4).coerceAtLeast(0), currentIdx)
        val upcoming = slots.subList((currentIdx + 1).coerceAtMost(slots.size), (currentIdx + 9).coerceAtMost(slots.size))
        return ChannelEpg(
            channelId = channel.id,
            past = past,
            current = current,
            upcoming = upcoming,
        )
    }

    fun getCurrentProgram(channel: Channel, nowMs: Long = System.currentTimeMillis()): EpgProgram =
        getSchedule(channel, nowMs).current

    /**
     * Enhances a stream URL with DVR / Catch-Up buffer parameters where supported by the CDN
     * (FreeTV / Kan MedOne / Redge / CloudFront / Immergo) so seeking back in time works seamlessly.
     */
    fun buildCatchUpUrl(rawUrl: String, offsetSec: Long): String {
        if (offsetSec <= 0L) return rawUrl
        val sep = if ('?' in rawUrl) "&" else "?"
        return when {
            rawUrl.contains("live.livx/playlist.m3u8") && !rawUrl.contains("dvr=") ->
                "$rawUrl${sep}dvr=21600000"
            rawUrl.contains("immergo.tv") && rawUrl.contains("streamPlaylist.m3u8") ->
                rawUrl.replace("streamPlaylist.m3u8", "streamPlaylist-archive.m3u8")
            else -> rawUrl
        }
    }

    private data class ShowTemplate(val title: String, val desc: String, val durationMin: Int, val badge: String = "HD")

    private fun templatesFor(ch: Channel): List<ShowTemplate> {
        val id = ch.id.lowercase()
        val cat = ch.category
        return when {
            id.startsWith("kan11") -> listOf(
                ShowTemplate("חדשות הבוקר – כאן 11", "משדר חדשות ואקטואליה לפתיחת היום מאולפן כאן בירושלים", 60, "LIVE"),
                ShowTemplate("קלמן ליברמן – משדר מיוחד", "ראיונות, פרשנויות ודיונים על סדר היום הציבורי בישראל", 60, "LIVE"),
                ShowTemplate("עולם קטן – דוקו כאן", "רצועת סרטי התעודה והתרבות של תאגיד השידור הישראלי", 45, "דוקו"),
                ShowTemplate("צהריים בכאן 11", "עדכוני חדשות, כתבות שטח ופרשנויות מהארץ ומהעולם", 60, "LIVE"),
                ShowTemplate("קופה ראשית – פרקים נבחרים", "סדרת הקומדיה האהובה על הסופרמרקט הכי ישראלי ביבנה", 35, "קומדיה"),
                ShowTemplate("זהו זה! – עונה חדשה", "תוכנית הסאטירה והמוזיקה המיתולוגית של ישראל", 45, "בידור"),
                ShowTemplate("שבע עם איילה חסון", "משדר האקטואליה המרכזי לפני מהדורת הערב", 60, "LIVE"),
                ShowTemplate("חדשות הערב – כאן 11", "מהדורת החדשות המרכזית של תאגיד השידור הישראלי", 75, "LIVE"),
                ShowTemplate("זמן אמת – תחקירים", "תוכנית התחקירים והדוקו המובילה של כאן 11", 60, "תחקיר"),
                ShowTemplate("טהרן – דרמת מתח", "דרמת הריגול הישראלית זוכת פרס האמי הבינלאומי", 50, "דרמה"),
                ShowTemplate("חדשות הלילה – כאן 11", "סיכום אירועי היום בחדשות, כלכלה, תרבות וספורט", 45, "LIVE"),
            )
            id.startsWith("keshet12") -> listOf(
                ShowTemplate("חדשות הבוקר עם ניב רסקין", "כל הכותרות, הראיונות והעדכונים לפתיחת היום בקשת 12", 90, "LIVE"),
                ShowTemplate("פאולה וליאון – חיים בריא", "מגזין בריאות, תזונה, לייף סטייל ואירוח באולפן", 60, "מגזין"),
                ShowTemplate("מהדורת היום – חדשות 12", "עדכוני חדשות ודיווחים ישירים מכל רחבי הארץ", 60, "LIVE"),
                ShowTemplate("שש עם עודד בן עמי", "משדר האקטואליה היומי של חדשות 12", 60, "LIVE"),
                ShowTemplate("תוכנית חיסכון עם קרן מרציאנו", "כלכלה, צרכנות, נדל\"ן והכסף שלכם", 55, "כלכלה"),
                ShowTemplate("מהדורת החדשות המרכזית N12", "מהדורת החדשות המרכזית של ישראל מנווה אילן", 80, "LIVE"),
                ShowTemplate("ארץ נהדרת – פריים טיים", "תוכנית הסאטירה המובילה של ישראל", 75, "פריים טיים"),
                ShowTemplate("הכוכב הבא / מאסטר שף", "רצועת הפריים טיים המרכזית של קשת 12", 75, "פריים טיים"),
                ShowTemplate("גיא פינס – ערב טוב", "כל חדשות הבידור, התרבות והכוכבים בישראל ובעולם", 40, "בידור"),
                ShowTemplate("אברי ושרקי – לילה", "תוכנית האירוח והאקטואליה הלילית של קשת 12", 50, "LIVE"),
            )
            id.startsWith("reshet13") -> listOf(
                ShowTemplate("העולם הבוקר – רשת 13", "תוכנית הבוקר של רשת 13 עם חדשות, תרבות וראיונות", 90, "LIVE"),
                ShowTemplate("פותחים יום – מגזין", "אירוח, לייף סטייל, אוכל וסיפורים אנושיים", 60, "מגזין"),
                ShowTemplate("חדשות היום – חדשות 13", "משדר חדשות הצהריים מאולפן חדשות 13", 60, "LIVE"),
                ShowTemplate("אזור בחירה עם רביב דרוקר", "פאנל פוליטי, תחקירים ופרשנויות", 60, "LIVE"),
                ShowTemplate("הצינור עם גיא לרר", "הסיפורים החמים מהרשת, צרכנות ולוחמה ביוקר המחיה", 50, "LIVE"),
                ShowTemplate("מהדורת החדשות המרכזית 13", "מהדורת הערב המרכזית של חדשות 13", 75, "LIVE"),
                ShowTemplate("הישרדות / משחקי השף / המירוץ למיליון", "רצועת הריאליטי והפריים טיים של רשת 13", 80, "פריים טיים"),
                ShowTemplate("המקור – תחקירים", "תוכנית התחקירים של רשת 13 עם רביב דרוקר", 60, "תחקיר"),
                ShowTemplate("היום שהיה", "סיכום חדשות היום בארץ ובעולם", 45, "LIVE"),
            )
            id.startsWith("now14") -> listOf(
                ShowTemplate("ישראל הבוקר – עכשיו 14", "משדר האקטואליה והחדשות של הבוקר בערוץ 14", 90, "LIVE"),
                ShowTemplate("יומן צהריים – עכשיו 14", "עדכוני חדשות, דיווחים מהשטח ופרשנויות", 60, "LIVE"),
                ShowTemplate("האולפן הפתוח עם בועז גולן", "אקטואליה, ראיונות וסיפורים מכל רחבי הארץ", 60, "LIVE"),
                ShowTemplate("ריקלין ושות׳", "תוכנית האקטואליה והפוליטיקה עם שמעון ריקלין", 60, "LIVE"),
                ShowTemplate("מהדורת החדשות המרכזית 14", "מהדורת הערב המרכזית של ערוץ עכשיו 14 עם מגי טביבי", 70, "LIVE"),
                ShowTemplate("הפטריוטים עם ינון מגל", "פאנל האקטואליה והפובליציסטיקה המרכזי של ערוץ 14", 80, "LIVE"),
                ShowTemplate("פתחי וזמרי בעם", "סאטירה, ראיונות ואירוח לילי", 55, "בידור"),
                ShowTemplate("חדשות הלילה – עכשיו 14", "סיכום אירועי היום והכותרות המרכזיות", 45, "LIVE"),
            )
            id.contains("i24") -> listOf(
                ShowTemplate("חדר החדשות – i24NEWS", "עדכונים שוטפים ודיווחים ישירים מנמל יפו ומהעולם", 60, "LIVE"),
                ShowTemplate("יומן המזרח התיכון", "סיקור מיוחד ופרשנויות על המתרחש במזרח התיכון", 60, "LIVE"),
                ShowTemplate("כלכלה ושווקים ב-i24", "ניתוח שווקים, היי-טק וכלכלה ישראלית וגלובלית", 45, "כלכלה"),
                ShowTemplate("המהדורה המרכזית – i24NEWS", "משדר הערב המרכזי עם מיטב הפרשנים והכתבים", 60, "LIVE"),
                ShowTemplate("הישראלים – פאנל אקטואליה", "דיונים סוערים על הפוליטיקה והחברה בישראל", 60, "LIVE"),
            )
            cat.contains("ספורט") || id.contains("sport") || id.contains("one_") || id.contains("eurosport") -> listOf(
                ShowTemplate("שידור חי: משחק מרכזי – ליגת העל / אירופה", "שידור ישיר ב-HD מהמגרשים המובילים בישראל ובאירופה", 110, "LIVE"),
                ShowTemplate("אולפן ליגת האלופות וסיכום מחזור", "כל השערים, התקצירים והפרשנויות מהמחזור האחרון", 60, "אולפן"),
                ShowTemplate("יציע העיתונות / בובה של לילה", "פאנל הספורט הסוער עם מיטב הפרשנים בישראל", 55, "פאנל"),
                ShowTemplate("שידור ישיר: כדורסל יורוליג / NBA", "מפגש צמרת בכדורסל האירופי והעולמי בשידור חי", 105, "LIVE"),
                ShowTemplate("חדשות הספורט – המהדורה המרכזית", "כל העדכונים מעולם הכדורגל, הכדורסל והספורט הישראלי", 45, "LIVE"),
                ShowTemplate("טניס גראנד סלאם / פאדל עולמי ישיר", "שידור חי מטורנירי הסבב העולמי ב-HD", 90, "LIVE"),
            )
            cat.contains("ילדים") -> listOf(
                ShowTemplate("בוקר של כיף – שירים והרפתקאות", "רצועת הבוקר לילדים עם השירים והדמויות האהובות בעברית", 30, "ילדים"),
                ShowTemplate("מפרץ ההרפתקאות / סמי הכבאי", "הרפתקאות הצלה מרתקות מדובבות לעברית", 30, "מדובב"),
                ShowTemplate("שקשוקה / כראמל – סדרת מקור", "סדרת הילדים והנוער הישראלית האהובה", 35, "מקור"),
                ShowTemplate("בובספוג מכנסמרובע – פרקים נבחרים", "קומדיית האנימציה האהובה מביקיני בוטום בעברית", 30, "אנימציה"),
                ShowTemplate("אלישע / שמש / הפיג׳מות", "קומדיה ישראלית מצחיקה לכל המשפחה", 35, "קומדיה"),
                ShowTemplate("שעת סיפור ושירי ילדות ישראלית", "קלאסיקות ילדות ישראלית וסיפורים אהובים", 30, "ילדים"),
                ShowTemplate("סרט משפחה וילדים מדובב", "סרט אנימציה והרפתקאות באורך מלא לכל המשפחה", 75, "סרט"),
            )
            cat.contains("אוכל") || id.contains("food") || id.contains("cook") || id.contains("chef") || id.contains("gusto") || id.contains("kitchen") -> listOf(
                ShowTemplate("סודות המטבח של השפים הגדולים", "מתכונים מנצחים וטכניקות בישול ממטבחי השפים המובילים", 45, "בישול"),
                ShowTemplate("אפייה וקונדיטוריה ביתית", "מאפים, לחמים, עוגות וקינוחים שלב אחר שלב", 40, "אפייה"),
                ShowTemplate("מסע קולינרי ואוכל רחוב", "סיור טעמים בשווקים ובמסעדות המרתקות בעולם", 45, "קולינריה"),
                ShowTemplate("ארוחת ערב ב-30 דקות", "מתכונים מהירים, טעימים ובריאים לכל המשפחה", 35, "מתכונים"),
                ShowTemplate("קרב השפים – תחרות בישול", "שפים מתחרים על הכנת המנה המושלמת תחת לחץ זמן", 55, "ריאליטי"),
                ShowTemplate("המטבח הים-תיכוני והישראלי", "טעמים מקומיים, שמן זית, עשבי תיבול ואירוח ישראלי", 45, "מקור"),
            )
            cat.contains("סרטים") || id.contains("cinema") || id.contains("movies") || id.contains("viva") || id.contains("drama") -> listOf(
                ShowTemplate("סרט הצהריים: קומדיה רומנטית", "קולנוע נבחר עם תרגום מלא לעברית ב-HD", 95, "קולנוע"),
                ShowTemplate("סדרת דרמה יומית – פרק חדש", "דרמה סוחפת ומרגשת עם כתוביות בעברית", 50, "סדרה"),
                ShowTemplate("שובר קופות: אקשן ומתח FHD", "סרט פעולה ומתח עתיר אפקטים בשידור HD", 110, "אקשן"),
                ShowTemplate("קלאסיקה ישראלית – קולנוע מקומי", "מיטב הסרטים והקומדיות של הקולנוע הישראלי", 95, "ישראלי"),
                ShowTemplate("פריים טיים סינמה – הצגה מרכזית", "הסרט המרכזי של הערב בשידור ישיר", 115, "פריים טיים"),
            )
            cat.contains("דוקו") || cat.contains("טבע") -> listOf(
                ShowTemplate("פלאי הטבע ועולם החי ב-FHD", "תיעוד מרהיב של חיות הבר והטבע הפראי ברחבי תבל", 55, "טבע"),
                ShowTemplate("סודות ההיסטוריה והמדע", "מסע דוקומנטרי אל התגליות והאירועים ששינו את העולם", 50, "מדע"),
                ShowTemplate("דוקו ישראלי – סיפור אמיתי", "סרט תעודה מרתק על החברה והאנשים בישראל", 60, "דוקו"),
                ShowTemplate("מסע אל מעמקי האוקיינוס", "תיעוד נדיר מתחת לפני הים באיכות גבוהה", 50, "טבע"),
            )
            cat.contains("חדשות מהעולם") -> listOf(
                ShowTemplate("World News Live – משדר חדשות בינלאומי", "סיקור חי 24/7 של כותרות החדשות המרכזיות מרחבי העולם", 60, "LIVE"),
                ShowTemplate("Global Markets & Business Report", "עדכונים משווקי ההון, כלכלה עולמית וטכנולוגיה", 45, "BUSINESS"),
                ShowTemplate("Prime Time International Briefing", "ניתוח מעמיק של אירועי היום עם כתבים מכל הבירות", 60, "LIVE"),
                ShowTemplate("Special Documentary & Investigation", "כתבות עומק ותחקירים בינלאומיים", 50, "DOCU"),
            )
            ch.isRadio -> listOf(
                ShowTemplate("תוכנית הבוקר בשידור חי", "מוזיקה מעולה, דיווחי תנועה ועדכונים לפתיחת היום", 90, "LIVE"),
                ShowTemplate("מצעד הלהיטים הישראלי והעולמי", "השירים הכי מושמעים בישראל ברצף מוזיקלי", 90, "מוזיקה"),
                ShowTemplate("דרייב טיים – בדרך הביתה", "המוזיקה הכי טובה לנסיעה ולשעות אחר הצהריים", 90, "LIVE"),
                ShowTemplate("שירים אל תוך הלילה", "קלאסיקות ושירים אהובים בשידור רציף", 90, "מוזיקה"),
            )
            else -> listOf(
                ShowTemplate("${ch.name} – שידור חי", ch.desc.ifEmpty { "שידור חי רציף באיכות גבוהה" }, 60, ch.badge.ifEmpty { "LIVE" }),
                ShowTemplate("רצועת המגזין והאירוח", "תוכניות אירוח, תרבות, פנאי ולייף סטייל", 50, "מגזין"),
                ShowTemplate("המהדורה המרכזית ופריים טיים", "התוכניות המובילות של הערוץ בשעות השיא", 65, "פריים טיים"),
                ShowTemplate("ספיישל לילי ב-${ch.name}", "מיטב התוכניות והשידורים הנבחרים של היום", 60, "HD"),
            )
        }
    }

    private fun buildDaySlots(ch: Channel, nowMs: Long): List<EpgProgram> {
        val templates = templatesFor(ch)
        val cal = Calendar.getInstance(ilTimeZone).apply {
            timeInMillis = nowMs
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, -6)
        }
        var cursorMs = cal.timeInMillis
        val endLimitMs = nowMs + 12 * 3600_000L
        val seed = abs(ch.id.hashCode())
        var idx = seed % templates.size
        val result = ArrayList<EpgProgram>(20)

        while (cursorMs < endLimitMs) {
            val tpl = templates[idx % templates.size]
            val durMs = tpl.durationMin * 60_000L
            val nextMs = cursorMs + durMs
            result.add(
                EpgProgram(
                    title = tpl.title,
                    desc = tpl.desc,
                    startTimeMs = cursorMs,
                    endTimeMs = nextMs,
                    startLabel = formatHm(cursorMs),
                    endLabel = formatHm(nextMs),
                    badge = tpl.badge,
                )
            )
            cursorMs = nextMs
            idx++
        }
        return result
    }
}

