import os

path = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\nuvio\NuvioShell.kt"
with open(path, "r", encoding="utf-8") as f:
    c = f.read()

old_tab = """@Composable
private fun NuvioSettingsTab(
    prefs: AppPrefs,
) {
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = top, bottom = bottom),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "settings_heading") {
            Text(
                text = "הגדרות",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
        item(key = "settings_info") {
            Text(
                text = "ישראל TV+ • ממשק Nuvio",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
    }
}"""

new_tab = """@Composable
private fun NuvioSettingsTab(
    prefs: AppPrefs,
) {
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = top, bottom = bottom),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "settings_heading") {
            Text(
                text = "הגדרות",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
        item(key = "settings_gdrive") {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = d.gutter),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "☁️",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "חשבון Google & הקלטות ב-Google Drive",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Text(
                        text = "חברו את חשבון ה-Google שלכם להקלטת פרקי סדרות VOD ושידורים חיים ישירות לתיקיית 'ישראל TV+ - הקלטות' בענן.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item(key = "settings_info") {
            Text(
                text = "ישראל TV+ • ממשק Nuvio • גרסה 4.8.16",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
    }
}"""

if old_tab in c:
    c = c.replace(old_tab, new_tab)
    with open(path, "w", encoding="utf-8") as f:
        f.write(c)
    print("NuvioShell.kt updated successfully with Google Drive card!")
else:
    print("old_tab not matched in NuvioShell.kt")

