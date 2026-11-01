with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/CatchupSheet.kt", "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace("import coil.compose.AsyncImage", "import coil3.compose.AsyncImage\nimport androidx.compose.foundation.layout.heightIn")
with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/CatchupSheet.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)
print("CatchupSheet.kt fixed")

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerControls.kt", "r", encoding="utf-8") as f:
    text = f.read()

# Add import androidx.compose.ui.unit.sp
if "import androidx.compose.ui.unit.sp" not in text:
    text = text.replace("import androidx.compose.ui.unit.dp", "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp")

text = text.replace("fontSize = 11.5.sp", "fontSize = 12.sp")
with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerControls.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)
print("PlayerControls.kt fixed")

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt", "r", encoding="utf-8") as f:
    text = f.read()

if "import kotlinx.coroutines.launch" not in text:
    text = text.replace("import androidx.compose.runtime.rememberCoroutineScope", "import androidx.compose.runtime.rememberCoroutineScope\nimport kotlinx.coroutines.launch")

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)
print("PlayerScreen.kt fixed")

