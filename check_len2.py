import os
os.chdir(r"C:\Users\bkyil\AndroidStudioProjects\NeuroComet")
for f in ["whatsnew-en-US.txt", "whatsnew-en-GB.txt", "whatsnew-tr-TR.txt"]:
    raw = open(f, encoding="utf-8").read()
    content = raw.rstrip()
    blen = len(content.encode("utf-8"))
    print(f"{f}: {len(content)} chars, {blen} bytes")

