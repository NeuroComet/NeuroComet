import re
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()
startCallPatch = """                            onStartCall = { userId, displayName, avatar, isVideo ->
                                if (feedState.isMockInterfaceEnabled) {
                                    com.kyilmaz.neurocomet.MockCallManager.startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE)
                                } else {
                                    com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE)
                                }
                            }"""
text = re.sub(
    r'(onOpenPracticeCall \= \{\s+navController\.navigate\(Screen\.PracticeCallSelection\.route\)\s+\}\n\s*)\)',
    r'\1,\n' + startCallPatch + '\n                        )',
    text
)
text = re.sub(
    r'(onSimulatedReply \= \{\s+conversationId, senderId, content \->\s+messagesViewModel\.receiveMockReply\(conversationId, senderId, content\)\s+\}\n\s*)\)',
    r'\1,\n' + startCallPatch + '\n                        )',
    text
)
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
print("Added onStartCall")
