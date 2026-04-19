import hashlib

fp = "google/tokay/tokay:16/CP1A.260305.018/14887507:user/release-keys"
board = "tokay"
brand = "google"
model = "Pixel 9"
android_id = "5b2abf9e907a7bc3"

sep = "|"
raw = fp + sep + board + sep + brand + sep + model + sep + android_id

h = hashlib.sha256(raw.encode("utf-8")).hexdigest()
print("RAW:", repr(raw))
print("HASH:", h)

