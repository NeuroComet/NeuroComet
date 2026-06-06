import re
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "r", encoding="utf-8") as f:
    orig = f.read()
text = orig.replace("""                        onOpenPracticeCall = {
                            navController.navigate(Screen.PracticeCallSelection.route)
                        }
                    )""", """                        onOpenPracticeCall = {
                            navController.navigate(Screen.PracticeCallSelection.route)
                        },
                        onStartCall = { userId, displayName, avatar, isVideo ->
                            if (feedState.isMockInterfaceEnabled) {
                                com.kyilmaz.neurocomet.MockCallManager.startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE)
                            } else {
                                com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE)
                            }
                        }
                    )""")
text = text.replace("""                            onOpenPracticeCall = {
                                navController.navigate(Screen.PracticeCallSelection.route)
                            }
                        )""", """                            onOpenPracticeCall = {
                                navController.navigate(Screen.PracticeCallSelection.route)
                            },
                            onStartCall = { userId, displayName, avatar, isVideo ->
                                if (feedState.isMockInterfaceEnabled) {
                                    com.kyilmaz.neurocomet.MockCallManager.startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE)
                                } else {
                                    com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE)
                                }
                            }
                        )""")
text = text.replace("""                            onProfileClick = { userId ->
                                navController.navigate(Screen.Profile.route(userId))
                            }
                        )""", """                            onProfileClick = { userId ->
                                navController.navigate(Screen.Profile.route(userId))
                            },
                            onStartCall = { userId, displayName, avatar, isVideo ->
                                if (feedState.isMockInterfaceEnabled) {
                                    com.kyilmaz.neurocomet.MockCallManager.startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.CallType.VIDEO else com.kyilmaz.neurocomet.CallType.VOICE)
                                } else {
                                    com.kyilmaz.neurocomet.calling.WebRTCCallManager.getInstance().startCall(userId, displayName, avatar, if (isVideo) com.kyilmaz.neurocomet.calling.CallType.VIDEO else com.kyilmaz.neurocomet.calling.CallType.VOICE)
                                }
                            }
                        )""")
with open("app/src/main/java/com/kyilmaz/neurocomet/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)
print("Replaces completed", orig != text)
