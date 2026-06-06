import re, json
path = 'lib/l10n/app_localizations.dart'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()
# 1. extract all keys in 'en'
en_block = re.search(r"'en': \{(.*?)\},\n    'es': \{", text, re.DOTALL)
if not en_block:
    print('en block not found')
    exit(1)
existing_keys = set(re.findall(r"'([a-zA-Z0-9_]+)':", en_block.group(1)))
# 2. get requested keys
req_keys = set()
with open('all_l10n_keys.txt', 'r', encoding='utf-16') as f:
    for line in f:
        k = line.strip().replace('l10n.', '')
        if k and k not in ('get', 'locale', 'supportedLocales', 'delegate'):
            req_keys.add(k)
with open('all_l10n_gets.txt', 'r', encoding='utf-16') as f:
    for line in f:
        k = line.strip()
        if k: req_keys.add(k)
missing_keys = req_keys - existing_keys
# 3. title cap
def to_title(k):
    if k == 'authWelcomeTitle': return 'Welcome to NeuroComet'
    if k == 'authWelcomeBody': return 'Your journey begins here'
    if k == 'postsCount': return '{count} Posts'
    name = re.sub('([a-z0-9])([A-Z])', r'\1 \2', k)
    return name.title()
additions = ""
for k in sorted(missing_keys):
    display = to_title(k).replace("'", "\\'")
    additions += f"      '{k}': '{display}',\n"
new_en = en_block.group(1) + additions
text = text.replace(en_block.group(0), f"'en': {{{new_en}}},\n    'es': {{")
# 4. get all existing getters
getter_matches = re.finditer(r"String get ([a-zA-Z0-9_]+) =>", text)
existing_getters = set(m.group(1) for m in getter_matches)
missing_getters = req_keys - existing_getters
getters_add = ""
for k in sorted(missing_getters):
    getters_add += f"  String get {k} => get('{k}');\n"
text = text.replace('\n}', f"\n{getters_add}}}")
with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print(f'Added {len(missing_keys)} keys to map and {len(missing_getters)} getters')
