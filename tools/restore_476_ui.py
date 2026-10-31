with open("android/app/src/main/kotlin/com/israeltv/plus/ui/nuvio/NuvioUi.kt", "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace(
    "var enabled by mutableStateOf(true)",
    "var enabled by mutableStateOf(BuildConfig.DEBUG)"
)
if "import com.israeltv.plus.BuildConfig" not in c:
    c = c.replace("import androidx.compose.runtime.setValue", "import androidx.compose.runtime.setValue\nimport com.israeltv.plus.BuildConfig")

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/nuvio/NuvioUi.kt", "w", encoding="utf-8") as f:
    f.write(c)

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/theme/Dimensions.kt", "r", encoding="utf-8") as f:
    d = f.read()

d = d.replace("posterWidth = 126.dp", "posterWidth = 120.dp")
d = d.replace("navHeight = 52.dp", "navHeight = 64.dp")
d = d.replace("heroBottomFade = 240.dp", "heroBottomFade = 220.dp")

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/theme/Dimensions.kt", "w", encoding="utf-8") as f:
    f.write(d)

print("Updated NuvioUi.kt and Dimensions.kt back to 4.7.6 baseline.")

