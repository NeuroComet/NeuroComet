import re
with open('lib/l10n/app_localizations.dart', 'r', encoding='utf-8') as f:
    text = f.read()

# Check tagline across locales
for m in re.finditer(r"'authAppTagline': '([^']*)'", text):
    print(m.group(1))

