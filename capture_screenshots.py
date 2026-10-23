import os
from playwright.sync_api import sync_playwright

html_path = os.path.abspath('index.html').replace('\\', '/')
file_url = f'file:///{html_path}'

os.makedirs('docs/screenshots', exist_ok=True)

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page(viewport={'width': 1920, 'height': 1080})
    
    # 1. OLED Dark Theme Home View
    page.goto(file_url)
    page.wait_for_timeout(1000)
    page.screenshot(path='docs/screenshots/01_oled_home.png')
    print('Captured 01_oled_home.png')

    # 2. Hebrew Kids Category
    # Click on "ילדים ונוער (בעברית)"
    page.evaluate('''() => {
        const items = document.querySelectorAll('.category-item');
        for (const it of items) {
            if (it.textContent.includes('ילדים')) {
                it.click();
                break;
            }
        }
    }''')
    page.wait_for_timeout(800)
    page.screenshot(path='docs/screenshots/02_hebrew_kids.png')
    print('Captured 02_hebrew_kids.png')

    # 3. Light Mode Theme
    page.evaluate('''() => {
        document.getElementById('btn-theme-toggle').click();
    }''')
    page.wait_for_timeout(800)
    page.screenshot(path='docs/screenshots/03_light_mode.png')
    print('Captured 03_light_mode.png')

    # 4. Light Mode All Channels
    page.evaluate('''() => {
        const items = document.querySelectorAll('.category-item');
        if (items[0]) items[0].click();
    }''')
    page.wait_for_timeout(800)
    page.screenshot(path='docs/screenshots/04_light_home.png')
    print('Captured 04_light_home.png')

    # 5. Fullscreen Player with OSD
    page.evaluate('''() => {
        // switch back to OLED
        document.getElementById('btn-theme-toggle').click();
        const cards = document.querySelectorAll('.channel-card');
        if (cards[0]) cards[0].click();
    }''')
    page.wait_for_timeout(800)
    page.screenshot(path='docs/screenshots/05_player_osd.png')
    print('Captured 05_player_osd.png')

    browser.close()

print('All screenshots captured successfully!')
