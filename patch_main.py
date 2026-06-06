import re
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()
patch = """                            onStartCall = { userId, displayName, avatar, isVideo ->
                                if (feedState.isMockInterfaceEnabled) {
                                    com.kyilmaz.neurocomet.MockCallManager.startCall(
                                        recipientId = userId,
                                        recipientName = displayName,
                                        recipientAvatar = avatar,
                                        callType = if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE
                                    )
                                } else {
                                    com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(
                                        recipientId = userId,
                                        recipientName = displayName,
                                        recipientAvatar = avatar,
                                        callType = if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE
                                    )
                                }
                            },
"""
text = re.sub(
    r'(onOpenPracticeCall \= \{\s+navController\.navigate\(Screen\.PracticeCallSelection\.route\)\s+\})',
    r'\1,\n' + patch.rstrip(','),
    text
)
text = re.sub(
    r'(onProfileClick \= \{ userId \->\s+navController\.navigate\(Screen\.Profile\.route\(userId\)\)\s+\})',
    r'\1,\n' + patch.rstrip(','),
    text
)
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
print("Patched.")
