import os
os.chdir(r"C:\Users\bkyil\AndroidStudioProjects\NeuroComet")
for f in ["whatsnew-en-US.txt", "whatsnew-en-GB.txt", "whatsnew-tr-TR.txt"]:
    content = open(f, encoding="utf-8").read().rstrip()
    print(f"{f}: {len(content)} chars (limit 500)")

