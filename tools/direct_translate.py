#!/usr/bin/env python3
"""
Direct translation script - translates strings without API by using a comprehensive dictionary.
This provides accurate translations for common UI strings.
"""

import os
import re
from pathlib import Path

# Base path
BASE_PATH = Path(__file__).parent.parent / "app" / "src" / "main" / "res"

# Comprehensive translations dictionary
# Format: { "english_text": { "lang_code": "translation", ... }, ... }
TRANSLATIONS = {
    # Navigation
    "Feed": {"es": "Inicio", "fr": "Fil", "de": "Feed", "it": "Feed", "pt": "Feed", "ru": "Лента", "ja": "フィード", "ko": "피드", "zh": "动态", "ar": "التغذية", "hi": "फ़ीड", "tr": "Akış", "nl": "Feed", "pl": "Aktualności", "sv": "Flöde", "da": "Feed", "fi": "Syöte", "nb": "Feed", "cs": "Příspěvky", "el": "Ροή", "he": "פיד", "hu": "Hírfolyam", "id": "Beranda", "ms": "Suapan", "ro": "Flux", "th": "ฟีด", "uk": "Стрічка", "vi": "Bảng tin", "is": "Straumur", "ur": "فیڈ"},
    "Explore": {"es": "Explorar", "fr": "Explorer", "de": "Entdecken", "it": "Esplora", "pt": "Explorar", "ru": "Поиск", "ja": "検索", "ko": "탐색", "zh": "探索", "ar": "استكشاف", "hi": "खोजें", "tr": "Keşfet", "nl": "Ontdekken", "pl": "Odkrywaj", "sv": "Utforska", "da": "Udforsk", "fi": "Tutustu", "nb": "Utforsk", "cs": "Prozkoumat", "el": "Εξερεύνηση", "he": "גלה", "hu": "Felfedezés", "id": "Jelajahi", "ms": "Teroka", "ro": "Explorează", "th": "สำรวจ", "uk": "Дослідити", "vi": "Khám phá", "is": "Skoða", "ur": "دریافت کریں"},
    "Messages": {"es": "Mensajes", "fr": "Messages", "de": "Nachrichten", "it": "Messaggi", "pt": "Mensagens", "ru": "Сообщения", "ja": "メッセージ", "ko": "메시지", "zh": "消息", "ar": "الرسائل", "hi": "संदेश", "tr": "Mesajlar", "nl": "Berichten", "pl": "Wiadomości", "sv": "Meddelanden", "da": "Beskeder", "fi": "Viestit", "nb": "Meldinger", "cs": "Zprávy", "el": "Μηνύματα", "he": "הודעות", "hu": "Üzenetek", "id": "Pesan", "ms": "Mesej", "ro": "Mesaje", "th": "ข้อความ", "uk": "Повідомлення", "vi": "Tin nhắn", "is": "Skilaboð", "ur": "پیغامات"},
    "Alerts": {"es": "Alertas", "fr": "Alertes", "de": "Benachrichtigungen", "it": "Avvisi", "pt": "Alertas", "ru": "Уведомления", "ja": "通知", "ko": "알림", "zh": "通知", "ar": "التنبيهات", "hi": "सूचनाएं", "tr": "Bildirimler", "nl": "Meldingen", "pl": "Powiadomienia", "sv": "Aviseringar", "da": "Notifikationer", "fi": "Ilmoitukset", "nb": "Varsler", "cs": "Upozornění", "el": "Ειδοποιήσεις", "he": "התראות", "hu": "Értesítések", "id": "Notifikasi", "ms": "Pemberitahuan", "ro": "Alerte", "th": "การแจ้งเตือน", "uk": "Сповіщення", "vi": "Thông báo", "is": "Tilkynningar", "ur": "اطلاعات"},
    "Settings": {"es": "Configuración", "fr": "Paramètres", "de": "Einstellungen", "it": "Impostazioni", "pt": "Configurações", "ru": "Настройки", "ja": "設定", "ko": "설정", "zh": "设置", "ar": "الإعدادات", "hi": "सेटिंग्स", "tr": "Ayarlar", "nl": "Instellingen", "pl": "Ustawienia", "sv": "Inställningar", "da": "Indstillinger", "fi": "Asetukset", "nb": "Innstillinger", "cs": "Nastavení", "el": "Ρυθμίσεις", "he": "הגדרות", "hu": "Beállítások", "id": "Pengaturan", "ms": "Tetapan", "ro": "Setări", "th": "การตั้งค่า", "uk": "Налаштування", "vi": "Cài đặt", "is": "Stillingar", "ur": "ترتیبات"},
    
    # Auth
    "Sign In": {"es": "Iniciar sesión", "fr": "Se connecter", "de": "Anmelden", "it": "Accedi", "pt": "Entrar", "ru": "Войти", "ja": "ログイン", "ko": "로그인", "zh": "登录", "ar": "تسجيل الدخول", "hi": "साइन इन करें", "tr": "Giriş Yap", "nl": "Inloggen", "pl": "Zaloguj się", "sv": "Logga in", "da": "Log ind", "fi": "Kirjaudu sisään", "nb": "Logg inn", "cs": "Přihlásit se", "el": "Σύνδεση", "he": "התחבר", "hu": "Bejelentkezés", "id": "Masuk", "ms": "Log masuk", "ro": "Conectare", "th": "เข้าสู่ระบบ", "uk": "Увійти", "vi": "Đăng nhập", "is": "Skrá inn", "ur": "سائن ان کریں"},
    "Sign Up": {"es": "Registrarse", "fr": "S'inscrire", "de": "Registrieren", "it": "Registrati", "pt": "Cadastrar", "ru": "Регистрация", "ja": "新規登録", "ko": "회원가입", "zh": "注册", "ar": "إنشاء حساب", "hi": "साइन अप करें", "tr": "Kayıt Ol", "nl": "Registreren", "pl": "Zarejestruj się", "sv": "Registrera", "da": "Tilmeld dig", "fi": "Rekisteröidy", "nb": "Registrer deg", "cs": "Zaregistrovat se", "el": "Εγγραφή", "he": "הירשם", "hu": "Regisztráció", "id": "Daftar", "ms": "Daftar", "ro": "Înregistrare", "th": "สมัครสมาชิก", "uk": "Зареєструватися", "vi": "Đăng ký", "is": "Nýskrá", "ur": "سائن اپ کریں"},
    "Email": {"es": "Correo electrónico", "fr": "E-mail", "de": "E-Mail", "it": "Email", "pt": "E-mail", "ru": "Эл. почта", "ja": "メールアドレス", "ko": "이메일", "zh": "电子邮件", "ar": "البريد الإلكتروني", "hi": "ईमेल", "tr": "E-posta", "nl": "E-mail", "pl": "E-mail", "sv": "E-post", "da": "E-mail", "fi": "Sähköposti", "nb": "E-post", "cs": "E-mail", "el": "Email", "he": "אימייל", "hu": "E-mail", "id": "Email", "ms": "E-mel", "ro": "E-mail", "th": "อีเมล", "uk": "Ел. пошта", "vi": "Email", "is": "Netfang", "ur": "ای میل"},
    "Password": {"es": "Contraseña", "fr": "Mot de passe", "de": "Passwort", "it": "Password", "pt": "Senha", "ru": "Пароль", "ja": "パスワード", "ko": "비밀번호", "zh": "密码", "ar": "كلمة المرور", "hi": "पासवर्ड", "tr": "Şifre", "nl": "Wachtwoord", "pl": "Hasło", "sv": "Lösenord", "da": "Adgangskode", "fi": "Salasana", "nb": "Passord", "cs": "Heslo", "el": "Κωδικός", "he": "סיסמה", "hu": "Jelszó", "id": "Kata sandi", "ms": "Kata laluan", "ro": "Parolă", "th": "รหัสผ่าน", "uk": "Пароль", "vi": "Mật khẩu", "is": "Lykilorð", "ur": "پاس ورڈ"},
    "Confirm Password": {"es": "Confirmar contraseña", "fr": "Confirmer le mot de passe", "de": "Passwort bestätigen", "it": "Conferma password", "pt": "Confirmar senha", "ru": "Подтвердить пароль", "ja": "パスワードを確認", "ko": "비밀번호 확인", "zh": "确认密码", "ar": "تأكيد كلمة المرور", "hi": "पासवर्ड की पुष्टि करें", "tr": "Şifreyi Onayla", "nl": "Wachtwoord bevestigen", "pl": "Potwierdź hasło", "sv": "Bekräfta lösenord", "da": "Bekræft adgangskode", "fi": "Vahvista salasana", "nb": "Bekreft passord", "cs": "Potvrdit heslo", "el": "Επιβεβαίωση κωδικού", "he": "אשר סיסמה", "hu": "Jelszó megerősítése", "id": "Konfirmasi kata sandi", "ms": "Sahkan kata laluan", "ro": "Confirmă parola", "th": "ยืนยันรหัสผ่าน", "uk": "Підтвердити пароль", "vi": "Xác nhận mật khẩu", "is": "Staðfesta lykilorð", "ur": "پاس ورڈ کی تصدیق کریں"},
    "Create Account": {"es": "Crear cuenta", "fr": "Créer un compte", "de": "Konto erstellen", "it": "Crea account", "pt": "Criar conta", "ru": "Создать аккаунт", "ja": "アカウント作成", "ko": "계정 만들기", "zh": "创建账户", "ar": "إنشاء حساب", "hi": "खाता बनाएं", "tr": "Hesap Oluştur", "nl": "Account aanmaken", "pl": "Utwórz konto", "sv": "Skapa konto", "da": "Opret konto", "fi": "Luo tili", "nb": "Opprett konto", "cs": "Vytvořit účet", "el": "Δημιουργία λογαριασμού", "he": "צור חשבון", "hu": "Fiók létrehozása", "id": "Buat akun", "ms": "Buat akaun", "ro": "Creează cont", "th": "สร้างบัญชี", "uk": "Створити обліковий запис", "vi": "Tạo tài khoản", "is": "Búa til reikning", "ur": "اکاؤنٹ بنائیں"},
    "Logout": {"es": "Cerrar sesión", "fr": "Déconnexion", "de": "Abmelden", "it": "Esci", "pt": "Sair", "ru": "Выйти", "ja": "ログアウト", "ko": "로그아웃", "zh": "退出登录", "ar": "تسجيل الخروج", "hi": "लॉग आउट", "tr": "Çıkış Yap", "nl": "Uitloggen", "pl": "Wyloguj się", "sv": "Logga ut", "da": "Log ud", "fi": "Kirjaudu ulos", "nb": "Logg ut", "cs": "Odhlásit se", "el": "Αποσύνδεση", "he": "התנתק", "hu": "Kijelentkezés", "id": "Keluar", "ms": "Log keluar", "ro": "Deconectare", "th": "ออกจากระบบ", "uk": "Вийти", "vi": "Đăng xuất", "is": "Skrá út", "ur": "لاگ آؤٹ"},
    
    # Common actions
    "Cancel": {"es": "Cancelar", "fr": "Annuler", "de": "Abbrechen", "it": "Annulla", "pt": "Cancelar", "ru": "Отмена", "ja": "キャンセル", "ko": "취소", "zh": "取消", "ar": "إلغاء", "hi": "रद्द करें", "tr": "İptal", "nl": "Annuleren", "pl": "Anuluj", "sv": "Avbryt", "da": "Annuller", "fi": "Peruuta", "nb": "Avbryt", "cs": "Zrušit", "el": "Ακύρωση", "he": "ביטול", "hu": "Mégse", "id": "Batal", "ms": "Batal", "ro": "Anulează", "th": "ยกเลิก", "uk": "Скасувати", "vi": "Hủy", "is": "Hætta við", "ur": "منسوخ کریں"},
    "Save": {"es": "Guardar", "fr": "Enregistrer", "de": "Speichern", "it": "Salva", "pt": "Salvar", "ru": "Сохранить", "ja": "保存", "ko": "저장", "zh": "保存", "ar": "حفظ", "hi": "सहेजें", "tr": "Kaydet", "nl": "Opslaan", "pl": "Zapisz", "sv": "Spara", "da": "Gem", "fi": "Tallenna", "nb": "Lagre", "cs": "Uložit", "el": "Αποθήκευση", "he": "שמור", "hu": "Mentés", "id": "Simpan", "ms": "Simpan", "ro": "Salvează", "th": "บันทึก", "uk": "Зберегти", "vi": "Lưu", "is": "Vista", "ur": "محفوظ کریں"},
    "Delete": {"es": "Eliminar", "fr": "Supprimer", "de": "Löschen", "it": "Elimina", "pt": "Excluir", "ru": "Удалить", "ja": "削除", "ko": "삭제", "zh": "删除", "ar": "حذف", "hi": "हटाएं", "tr": "Sil", "nl": "Verwijderen", "pl": "Usuń", "sv": "Radera", "da": "Slet", "fi": "Poista", "nb": "Slett", "cs": "Smazat", "el": "Διαγραφή", "he": "מחק", "hu": "Törlés", "id": "Hapus", "ms": "Padam", "ro": "Șterge", "th": "ลบ", "uk": "Видалити", "vi": "Xóa", "is": "Eyða", "ur": "حذف کریں"},
    "Edit": {"es": "Editar", "fr": "Modifier", "de": "Bearbeiten", "it": "Modifica", "pt": "Editar", "ru": "Редактировать", "ja": "編集", "ko": "편집", "zh": "编辑", "ar": "تعديل", "hi": "संपादित करें", "tr": "Düzenle", "nl": "Bewerken", "pl": "Edytuj", "sv": "Redigera", "da": "Rediger", "fi": "Muokkaa", "nb": "Rediger", "cs": "Upravit", "el": "Επεξεργασία", "he": "ערוך", "hu": "Szerkesztés", "id": "Edit", "ms": "Edit", "ro": "Editează", "th": "แก้ไข", "uk": "Редагувати", "vi": "Chỉnh sửa", "is": "Breyta", "ur": "ترمیم کریں"},
    "Back": {"es": "Atrás", "fr": "Retour", "de": "Zurück", "it": "Indietro", "pt": "Voltar", "ru": "Назад", "ja": "戻る", "ko": "뒤로", "zh": "返回", "ar": "رجوع", "hi": "वापस", "tr": "Geri", "nl": "Terug", "pl": "Wstecz", "sv": "Tillbaka", "da": "Tilbage", "fi": "Takaisin", "nb": "Tilbake", "cs": "Zpět", "el": "Πίσω", "he": "חזור", "hu": "Vissza", "id": "Kembali", "ms": "Kembali", "ro": "Înapoi", "th": "กลับ", "uk": "Назад", "vi": "Quay lại", "is": "Til baka", "ur": "واپس"},
    "Close": {"es": "Cerrar", "fr": "Fermer", "de": "Schließen", "it": "Chiudi", "pt": "Fechar", "ru": "Закрыть", "ja": "閉じる", "ko": "닫기", "zh": "关闭", "ar": "إغلاق", "hi": "बंद करें", "tr": "Kapat", "nl": "Sluiten", "pl": "Zamknij", "sv": "Stäng", "da": "Luk", "fi": "Sulje", "nb": "Lukk", "cs": "Zavřít", "el": "Κλείσιμο", "he": "סגור", "hu": "Bezárás", "id": "Tutup", "ms": "Tutup", "ro": "Închide", "th": "ปิด", "uk": "Закрити", "vi": "Đóng", "is": "Loka", "ur": "بند کریں"},
    "Continue": {"es": "Continuar", "fr": "Continuer", "de": "Weiter", "it": "Continua", "pt": "Continuar", "ru": "Продолжить", "ja": "続ける", "ko": "계속", "zh": "继续", "ar": "متابعة", "hi": "जारी रखें", "tr": "Devam Et", "nl": "Doorgaan", "pl": "Kontynuuj", "sv": "Fortsätt", "da": "Fortsæt", "fi": "Jatka", "nb": "Fortsett", "cs": "Pokračovat", "el": "Συνέχεια", "he": "המשך", "hu": "Folytatás", "id": "Lanjutkan", "ms": "Teruskan", "ro": "Continuă", "th": "ดำเนินการต่อ", "uk": "Продовжити", "vi": "Tiếp tục", "is": "Halda áfram", "ur": "جاری رکھیں"},
    "Skip": {"es": "Omitir", "fr": "Passer", "de": "Überspringen", "it": "Salta", "pt": "Pular", "ru": "Пропустить", "ja": "スキップ", "ko": "건너뛰기", "zh": "跳过", "ar": "تخطي", "hi": "छोड़ें", "tr": "Atla", "nl": "Overslaan", "pl": "Pomiń", "sv": "Hoppa över", "da": "Spring over", "fi": "Ohita", "nb": "Hopp over", "cs": "Přeskočit", "el": "Παράλειψη", "he": "דלג", "hu": "Kihagyás", "id": "Lewati", "ms": "Langkau", "ro": "Sari", "th": "ข้าม", "uk": "Пропустити", "vi": "Bỏ qua", "is": "Sleppa", "ur": "چھوڑ دیں"},
    "Send": {"es": "Enviar", "fr": "Envoyer", "de": "Senden", "it": "Invia", "pt": "Enviar", "ru": "Отправить", "ja": "送信", "ko": "보내기", "zh": "发送", "ar": "إرسال", "hi": "भेजें", "tr": "Gönder", "nl": "Verzenden", "pl": "Wyślij", "sv": "Skicka", "da": "Send", "fi": "Lähetä", "nb": "Send", "cs": "Odeslat", "el": "Αποστολή", "he": "שלח", "hu": "Küldés", "id": "Kirim", "ms": "Hantar", "ro": "Trimite", "th": "ส่ง", "uk": "Надіслати", "vi": "Gửi", "is": "Senda", "ur": "بھیجیں"},
    "Reply": {"es": "Responder", "fr": "Répondre", "de": "Antworten", "it": "Rispondi", "pt": "Responder", "ru": "Ответить", "ja": "返信", "ko": "답장", "zh": "回复", "ar": "رد", "hi": "जवाब दें", "tr": "Yanıtla", "nl": "Beantwoorden", "pl": "Odpowiedz", "sv": "Svara", "da": "Svar", "fi": "Vastaa", "nb": "Svar", "cs": "Odpovědět", "el": "Απάντηση", "he": "השב", "hu": "Válasz", "id": "Balas", "ms": "Balas", "ro": "Răspunde", "th": "ตอบกลับ", "uk": "Відповісти", "vi": "Trả lời", "is": "Svara", "ur": "جواب دیں"},
    "Share": {"es": "Compartir", "fr": "Partager", "de": "Teilen", "it": "Condividi", "pt": "Compartilhar", "ru": "Поделиться", "ja": "共有", "ko": "공유", "zh": "分享", "ar": "مشاركة", "hi": "साझा करें", "tr": "Paylaş", "nl": "Delen", "pl": "Udostępnij", "sv": "Dela", "da": "Del", "fi": "Jaa", "nb": "Del", "cs": "Sdílet", "el": "Κοινοποίηση", "he": "שתף", "hu": "Megosztás", "id": "Bagikan", "ms": "Kongsi", "ro": "Distribuie", "th": "แชร์", "uk": "Поділитися", "vi": "Chia sẻ", "is": "Deila", "ur": "شیئر کریں"},
    "Copy": {"es": "Copiar", "fr": "Copier", "de": "Kopieren", "it": "Copia", "pt": "Copiar", "ru": "Копировать", "ja": "コピー", "ko": "복사", "zh": "复制", "ar": "نسخ", "hi": "कॉपी करें", "tr": "Kopyala", "nl": "Kopiëren", "pl": "Kopiuj", "sv": "Kopiera", "da": "Kopier", "fi": "Kopioi", "nb": "Kopier", "cs": "Kopírovat", "el": "Αντιγραφή", "he": "העתק", "hu": "Másolás", "id": "Salin", "ms": "Salin", "ro": "Copiază", "th": "คัดลอก", "uk": "Копіювати", "vi": "Sao chép", "is": "Afrita", "ur": "کاپی کریں"},
    "Report": {"es": "Reportar", "fr": "Signaler", "de": "Melden", "it": "Segnala", "pt": "Denunciar", "ru": "Пожаловаться", "ja": "報告", "ko": "신고", "zh": "举报", "ar": "إبلاغ", "hi": "रिपोर्ट करें", "tr": "Şikayet Et", "nl": "Rapporteren", "pl": "Zgłoś", "sv": "Rapportera", "da": "Anmeld", "fi": "Ilmoita", "nb": "Rapporter", "cs": "Nahlásit", "el": "Αναφορά", "he": "דווח", "hu": "Jelentés", "id": "Laporkan", "ms": "Laporkan", "ro": "Raportează", "th": "รายงาน", "uk": "Поскаржитися", "vi": "Báo cáo", "is": "Tilkynna", "ur": "رپورٹ کریں"},
    "Block": {"es": "Bloquear", "fr": "Bloquer", "de": "Blockieren", "it": "Blocca", "pt": "Bloquear", "ru": "Заблокировать", "ja": "ブロック", "ko": "차단", "zh": "屏蔽", "ar": "حظر", "hi": "ब्लॉक करें", "tr": "Engelle", "nl": "Blokkeren", "pl": "Zablokuj", "sv": "Blockera", "da": "Bloker", "fi": "Estä", "nb": "Blokker", "cs": "Zablokovat", "el": "Αποκλεισμός", "he": "חסום", "hu": "Letiltás", "id": "Blokir", "ms": "Sekat", "ro": "Blochează", "th": "บล็อก", "uk": "Заблокувати", "vi": "Chặn", "is": "Loka á", "ur": "بلاک کریں"},
    "Mute": {"es": "Silenciar", "fr": "Masquer", "de": "Stummschalten", "it": "Silenzia", "pt": "Silenciar", "ru": "Отключить звук", "ja": "ミュート", "ko": "음소거", "zh": "静音", "ar": "كتم", "hi": "म्यूट करें", "tr": "Sessize Al", "nl": "Dempen", "pl": "Wycisz", "sv": "Tysta", "da": "Slå lyd fra", "fi": "Mykistä", "nb": "Demp", "cs": "Ztlumit", "el": "Σίγαση", "he": "השתק", "hu": "Némítás", "id": "Bisukan", "ms": "Bisukan", "ro": "Dezactivează sunetul", "th": "ปิดเสียง", "uk": "Вимкнути звук", "vi": "Tắt tiếng", "is": "Þagga", "ur": "خاموش کریں"},
    
    # Status
    "Online": {"es": "En línea", "fr": "En ligne", "de": "Online", "it": "Online", "pt": "Online", "ru": "В сети", "ja": "オンライン", "ko": "온라인", "zh": "在线", "ar": "متصل", "hi": "ऑनलाइन", "tr": "Çevrimiçi", "nl": "Online", "pl": "Online", "sv": "Online", "da": "Online", "fi": "Paikalla", "nb": "Pålogget", "cs": "Online", "el": "Σε σύνδεση", "he": "מקוון", "hu": "Online", "id": "Daring", "ms": "Dalam talian", "ro": "Online", "th": "ออนไลน์", "uk": "В мережі", "vi": "Trực tuyến", "is": "Á netinu", "ur": "آن لائن"},
    "Offline": {"es": "Desconectado", "fr": "Hors ligne", "de": "Offline", "it": "Offline", "pt": "Offline", "ru": "Не в сети", "ja": "オフライン", "ko": "오프라인", "zh": "离线", "ar": "غير متصل", "hi": "ऑफलाइन", "tr": "Çevrimdışı", "nl": "Offline", "pl": "Offline", "sv": "Offline", "da": "Offline", "fi": "Poissa", "nb": "Frakoblet", "cs": "Offline", "el": "Εκτός σύνδεσης", "he": "לא מקוון", "hu": "Offline", "id": "Luring", "ms": "Luar talian", "ro": "Offline", "th": "ออฟไลน์", "uk": "Не в мережі", "vi": "Ngoại tuyến", "is": "Ótengt", "ur": "آف لائن"},
    "Away": {"es": "Ausente", "fr": "Absent", "de": "Abwesend", "it": "Assente", "pt": "Ausente", "ru": "Отошёл", "ja": "離席中", "ko": "자리 비움", "zh": "离开", "ar": "بعيد", "hi": "दूर", "tr": "Uzakta", "nl": "Afwezig", "pl": "Nieobecny", "sv": "Borta", "da": "Væk", "fi": "Poissa", "nb": "Borte", "cs": "Pryč", "el": "Λείπει", "he": "לא נמצא", "hu": "Távol", "id": "Pergi", "ms": "Tiada", "ro": "Plecat", "th": "ไม่อยู่", "uk": "Відійшов", "vi": "Vắng mặt", "is": "Í burtu", "ur": "دور"},
    "Blocked": {"es": "Bloqueado", "fr": "Bloqué", "de": "Blockiert", "it": "Bloccato", "pt": "Bloqueado", "ru": "Заблокирован", "ja": "ブロック済み", "ko": "차단됨", "zh": "已屏蔽", "ar": "محظور", "hi": "ब्लॉक किया गया", "tr": "Engellendi", "nl": "Geblokkeerd", "pl": "Zablokowany", "sv": "Blockerad", "da": "Blokeret", "fi": "Estetty", "nb": "Blokkert", "cs": "Zablokován", "el": "Αποκλεισμένος", "he": "חסום", "hu": "Letiltva", "id": "Diblokir", "ms": "Disekat", "ro": "Blocat", "th": "ถูกบล็อก", "uk": "Заблоковано", "vi": "Đã chặn", "is": "Útilokað", "ur": "بلاک شدہ"},
    "Muted": {"es": "Silenciado", "fr": "Masqué", "de": "Stummgeschaltet", "it": "Silenziato", "pt": "Silenciado", "ru": "Отключён", "ja": "ミュート済み", "ko": "음소거됨", "zh": "已静音", "ar": "مكتوم", "hi": "म्यूट किया गया", "tr": "Sessize Alındı", "nl": "Gedempt", "pl": "Wyciszony", "sv": "Tystad", "da": "Lydløs", "fi": "Mykistetty", "nb": "Dempet", "cs": "Ztlumen", "el": "Σε σίγαση", "he": "מושתק", "hu": "Némítva", "id": "Dibisukan", "ms": "Dibisukan", "ro": "Sunet dezactivat", "th": "ปิดเสียงแล้ว", "uk": "Вимкнено", "vi": "Đã tắt tiếng", "is": "Þaggað", "ur": "خاموش"},
    
    # Comments
    "Comments": {"es": "Comentarios", "fr": "Commentaires", "de": "Kommentare", "it": "Commenti", "pt": "Comentários", "ru": "Комментарии", "ja": "コメント", "ko": "댓글", "zh": "评论", "ar": "التعليقات", "hi": "टिप्पणियाँ", "tr": "Yorumlar", "nl": "Reacties", "pl": "Komentarze", "sv": "Kommentarer", "da": "Kommentarer", "fi": "Kommentit", "nb": "Kommentarer", "cs": "Komentáře", "el": "Σχόλια", "he": "תגובות", "hu": "Hozzászólások", "id": "Komentar", "ms": "Komen", "ro": "Comentarii", "th": "ความคิดเห็น", "uk": "Коментарі", "vi": "Bình luận", "is": "Athugasemdir", "ur": "تبصرے"},
    
    # Profile
    "Profile": {"es": "Perfil", "fr": "Profil", "de": "Profil", "it": "Profilo", "pt": "Perfil", "ru": "Профиль", "ja": "プロフィール", "ko": "프로필", "zh": "个人资料", "ar": "الملف الشخصي", "hi": "प्रोफ़ाइल", "tr": "Profil", "nl": "Profiel", "pl": "Profil", "sv": "Profil", "da": "Profil", "fi": "Profiili", "nb": "Profil", "cs": "Profil", "el": "Προφίλ", "he": "פרופיל", "hu": "Profil", "id": "Profil", "ms": "Profil", "ro": "Profil", "th": "โปรไฟล์", "uk": "Профіль", "vi": "Hồ sơ", "is": "Prófíll", "ur": "پروفائل"},
    "Followers": {"es": "Seguidores", "fr": "Abonnés", "de": "Follower", "it": "Follower", "pt": "Seguidores", "ru": "Подписчики", "ja": "フォロワー", "ko": "팔로워", "zh": "粉丝", "ar": "المتابعون", "hi": "फ़ॉलोअर्स", "tr": "Takipçiler", "nl": "Volgers", "pl": "Obserwujący", "sv": "Följare", "da": "Følgere", "fi": "Seuraajat", "nb": "Følgere", "cs": "Sledující", "el": "Ακόλουθοι", "he": "עוקבים", "hu": "Követők", "id": "Pengikut", "ms": "Pengikut", "ro": "Urmăritori", "th": "ผู้ติดตาม", "uk": "Підписники", "vi": "Người theo dõi", "is": "Fylgjendur", "ur": "فالورز"},
    "Following": {"es": "Siguiendo", "fr": "Abonnements", "de": "Folge ich", "it": "Seguiti", "pt": "Seguindo", "ru": "Подписки", "ja": "フォロー中", "ko": "팔로잉", "zh": "关注", "ar": "المتابَعون", "hi": "फ़ॉलो कर रहे हैं", "tr": "Takip Edilenler", "nl": "Volgend", "pl": "Obserwowani", "sv": "Följer", "da": "Følger", "fi": "Seurataan", "nb": "Følger", "cs": "Sledovaní", "el": "Ακολουθεί", "he": "עוקב אחרי", "hu": "Követett", "id": "Mengikuti", "ms": "Mengikuti", "ro": "Urmărește", "th": "กำลังติดตาม", "uk": "Підписки", "vi": "Đang theo dõi", "is": "Fylgist með", "ur": "فالو کر رہے ہیں"},
    "Follow": {"es": "Seguir", "fr": "Suivre", "de": "Folgen", "it": "Segui", "pt": "Seguir", "ru": "Подписаться", "ja": "フォロー", "ko": "팔로우", "zh": "关注", "ar": "متابعة", "hi": "फ़ॉलो करें", "tr": "Takip Et", "nl": "Volgen", "pl": "Obserwuj", "sv": "Följ", "da": "Følg", "fi": "Seuraa", "nb": "Følg", "cs": "Sledovat", "el": "Ακολούθηση", "he": "עקוב", "hu": "Követés", "id": "Ikuti", "ms": "Ikut", "ro": "Urmărește", "th": "ติดตาม", "uk": "Підписатися", "vi": "Theo dõi", "is": "Fylgja", "ur": "فالو کریں"},
    "Unfollow": {"es": "Dejar de seguir", "fr": "Ne plus suivre", "de": "Entfolgen", "it": "Smetti di seguire", "pt": "Deixar de seguir", "ru": "Отписаться", "ja": "フォロー解除", "ko": "팔로우 취소", "zh": "取消关注", "ar": "إلغاء المتابعة", "hi": "अनफ़ॉलो करें", "tr": "Takibi Bırak", "nl": "Ontvolgen", "pl": "Przestań obserwować", "sv": "Sluta följa", "da": "Stop med at følge", "fi": "Lopeta seuraaminen", "nb": "Slutt å følge", "cs": "Přestat sledovat", "el": "Διακοπή παρακολούθησης", "he": "הפסק לעקוב", "hu": "Követés megszüntetése", "id": "Berhenti mengikuti", "ms": "Nyahikut", "ro": "Nu mai urmări", "th": "เลิกติดตาม", "uk": "Відписатися", "vi": "Bỏ theo dõi", "is": "Hætta að fylgja", "ur": "ان فالو کریں"},
    
    # Themes
    "Dark Mode": {"es": "Modo oscuro", "fr": "Mode sombre", "de": "Dunkelmodus", "it": "Modalità scura", "pt": "Modo escuro", "ru": "Тёмный режим", "ja": "ダークモード", "ko": "다크 모드", "zh": "深色模式", "ar": "الوضع الداكن", "hi": "डार्क मोड", "tr": "Karanlık Mod", "nl": "Donkere modus", "pl": "Tryb ciemny", "sv": "Mörkt läge", "da": "Mørk tilstand", "fi": "Tumma tila", "nb": "Mørk modus", "cs": "Tmavý režim", "el": "Σκούρο θέμα", "he": "מצב כהה", "hu": "Sötét mód", "id": "Mode gelap", "ms": "Mod gelap", "ro": "Mod întunecat", "th": "โหมดมืด", "uk": "Темний режим", "vi": "Chế độ tối", "is": "Dökkt þema", "ur": "ڈارک موڈ"},
    "Light Mode": {"es": "Modo claro", "fr": "Mode clair", "de": "Hellmodus", "it": "Modalità chiara", "pt": "Modo claro", "ru": "Светлый режим", "ja": "ライトモード", "ko": "라이트 모드", "zh": "浅色模式", "ar": "الوضع الفاتح", "hi": "लाइट मोड", "tr": "Aydınlık Mod", "nl": "Lichte modus", "pl": "Tryb jasny", "sv": "Ljust läge", "da": "Lys tilstand", "fi": "Vaalea tila", "nb": "Lys modus", "cs": "Světlý režim", "el": "Φωτεινό θέμα", "he": "מצב בהיר", "hu": "Világos mód", "id": "Mode terang", "ms": "Mod cerah", "ro": "Mod luminos", "th": "โหมดสว่าง", "uk": "Світлий режим", "vi": "Chế độ sáng", "is": "Ljóst þema", "ur": "لائٹ موڈ"},
    "Enabled": {"es": "Activado", "fr": "Activé", "de": "Aktiviert", "it": "Attivato", "pt": "Ativado", "ru": "Включено", "ja": "有効", "ko": "활성화됨", "zh": "已启用", "ar": "مفعّل", "hi": "सक्षम", "tr": "Etkin", "nl": "Ingeschakeld", "pl": "Włączone", "sv": "Aktiverat", "da": "Aktiveret", "fi": "Käytössä", "nb": "Aktivert", "cs": "Povoleno", "el": "Ενεργοποιημένο", "he": "מופעל", "hu": "Engedélyezve", "id": "Diaktifkan", "ms": "Diaktifkan", "ro": "Activat", "th": "เปิดใช้งาน", "uk": "Увімкнено", "vi": "Đã bật", "is": "Virkt", "ur": "فعال"},
    "Disabled": {"es": "Desactivado", "fr": "Désactivé", "de": "Deaktiviert", "it": "Disattivato", "pt": "Desativado", "ru": "Отключено", "ja": "無効", "ko": "비활성화됨", "zh": "已禁用", "ar": "معطّل", "hi": "अक्षम", "tr": "Devre Dışı", "nl": "Uitgeschakeld", "pl": "Wyłączone", "sv": "Inaktiverat", "da": "Deaktiveret", "fi": "Pois käytöstä", "nb": "Deaktivert", "cs": "Zakázáno", "el": "Απενεργοποιημένο", "he": "מושבת", "hu": "Letiltva", "id": "Dinonaktifkan", "ms": "Dinyahaktifkan", "ro": "Dezactivat", "th": "ปิดใช้งาน", "uk": "Вимкнено", "vi": "Đã tắt", "is": "Óvirkt", "ur": "غیر فعال"},
    
    # Loading/Status
    "Loading...": {"es": "Cargando...", "fr": "Chargement...", "de": "Laden...", "it": "Caricamento...", "pt": "Carregando...", "ru": "Загрузка...", "ja": "読み込み中...", "ko": "로딩 중...", "zh": "加载中...", "ar": "جارٍ التحميل...", "hi": "लोड हो रहा है...", "tr": "Yükleniyor...", "nl": "Laden...", "pl": "Ładowanie...", "sv": "Laddar...", "da": "Indlæser...", "fi": "Ladataan...", "nb": "Laster...", "cs": "Načítání...", "el": "Φόρτωση...", "he": "טוען...", "hu": "Betöltés...", "id": "Memuat...", "ms": "Memuatkan...", "ro": "Se încarcă...", "th": "กำลังโหลด...", "uk": "Завантаження...", "vi": "Đang tải...", "is": "Hleður...", "ur": "لوڈ ہو رہا ہے..."},
    "Sending...": {"es": "Enviando...", "fr": "Envoi...", "de": "Senden...", "it": "Invio...", "pt": "Enviando...", "ru": "Отправка...", "ja": "送信中...", "ko": "전송 중...", "zh": "发送中...", "ar": "جارٍ الإرسال...", "hi": "भेज रहा है...", "tr": "Gönderiliyor...", "nl": "Verzenden...", "pl": "Wysyłanie...", "sv": "Skickar...", "da": "Sender...", "fi": "Lähetetään...", "nb": "Sender...", "cs": "Odesílání...", "el": "Αποστολή...", "he": "שולח...", "hu": "Küldés...", "id": "Mengirim...", "ms": "Menghantar...", "ro": "Se trimite...", "th": "กำลังส่ง...", "uk": "Надсилання...", "vi": "Đang gửi...", "is": "Sendir...", "ur": "بھیج رہا ہے..."},
    "Failed": {"es": "Error", "fr": "Échec", "de": "Fehlgeschlagen", "it": "Non riuscito", "pt": "Falhou", "ru": "Ошибка", "ja": "失敗", "ko": "실패", "zh": "失败", "ar": "فشل", "hi": "विफल", "tr": "Başarısız", "nl": "Mislukt", "pl": "Niepowodzenie", "sv": "Misslyckades", "da": "Mislykket", "fi": "Epäonnistui", "nb": "Mislyktes", "cs": "Selhalo", "el": "Αποτυχία", "he": "נכשל", "hu": "Sikertelen", "id": "Gagal", "ms": "Gagal", "ro": "Eșuat", "th": "ล้มเหลว", "uk": "Помилка", "vi": "Thất bại", "is": "Mistókst", "ur": "ناکام"},
    "Success": {"es": "Éxito", "fr": "Succès", "de": "Erfolg", "it": "Successo", "pt": "Sucesso", "ru": "Успешно", "ja": "成功", "ko": "성공", "zh": "成功", "ar": "نجاح", "hi": "सफल", "tr": "Başarılı", "nl": "Gelukt", "pl": "Sukces", "sv": "Lyckades", "da": "Succes", "fi": "Onnistui", "nb": "Vellykket", "cs": "Úspěch", "el": "Επιτυχία", "he": "הצלחה", "hu": "Sikeres", "id": "Berhasil", "ms": "Berjaya", "ro": "Succes", "th": "สำเร็จ", "uk": "Успішно", "vi": "Thành công", "is": "Tókst", "ur": "کامیاب"},
    "Error": {"es": "Error", "fr": "Erreur", "de": "Fehler", "it": "Errore", "pt": "Erro", "ru": "Ошибка", "ja": "エラー", "ko": "오류", "zh": "错误", "ar": "خطأ", "hi": "त्रुटि", "tr": "Hata", "nl": "Fout", "pl": "Błąd", "sv": "Fel", "da": "Fejl", "fi": "Virhe", "nb": "Feil", "cs": "Chyba", "el": "Σφάλμα", "he": "שגיאה", "hu": "Hiba", "id": "Kesalahan", "ms": "Ralat", "ro": "Eroare", "th": "ข้อผิดพลาด", "uk": "Помилка", "vi": "Lỗi", "is": "Villa", "ur": "خرابی"},
    "Copied": {"es": "Copiado", "fr": "Copié", "de": "Kopiert", "it": "Copiato", "pt": "Copiado", "ru": "Скопировано", "ja": "コピーしました", "ko": "복사됨", "zh": "已复制", "ar": "تم النسخ", "hi": "कॉपी किया गया", "tr": "Kopyalandı", "nl": "Gekopieerd", "pl": "Skopiowano", "sv": "Kopierat", "da": "Kopieret", "fi": "Kopioitu", "nb": "Kopiert", "cs": "Zkopírováno", "el": "Αντιγράφηκε", "he": "הועתק", "hu": "Másolva", "id": "Disalin", "ms": "Disalin", "ro": "Copiat", "th": "คัดลอกแล้ว", "uk": "Скопійовано", "vi": "Đã sao chép", "is": "Afritað", "ur": "کاپی ہو گیا"},
    
    # Time
    "Just now": {"es": "Ahora mismo", "fr": "À l'instant", "de": "Gerade eben", "it": "Proprio ora", "pt": "Agora mesmo", "ru": "Только что", "ja": "たった今", "ko": "방금", "zh": "刚刚", "ar": "الآن", "hi": "अभी अभी", "tr": "Az önce", "nl": "Zojuist", "pl": "Przed chwilą", "sv": "Nyss", "da": "Lige nu", "fi": "Juuri nyt", "nb": "Akkurat nå", "cs": "Právě teď", "el": "Μόλις τώρα", "he": "הרגע", "hu": "Épp most", "id": "Baru saja", "ms": "Baru sahaja", "ro": "Chiar acum", "th": "เมื่อสักครู่", "uk": "Щойно", "vi": "Vừa xong", "is": "Núna", "ur": "ابھی ابھی"},
    "Today": {"es": "Hoy", "fr": "Aujourd'hui", "de": "Heute", "it": "Oggi", "pt": "Hoje", "ru": "Сегодня", "ja": "今日", "ko": "오늘", "zh": "今天", "ar": "اليوم", "hi": "आज", "tr": "Bugün", "nl": "Vandaag", "pl": "Dzisiaj", "sv": "Idag", "da": "I dag", "fi": "Tänään", "nb": "I dag", "cs": "Dnes", "el": "Σήμερα", "he": "היום", "hu": "Ma", "id": "Hari ini", "ms": "Hari ini", "ro": "Astăzi", "th": "วันนี้", "uk": "Сьогодні", "vi": "Hôm nay", "is": "Í dag", "ur": "آج"},
    "Yesterday": {"es": "Ayer", "fr": "Hier", "de": "Gestern", "it": "Ieri", "pt": "Ontem", "ru": "Вчера", "ja": "昨日", "ko": "어제", "zh": "昨天", "ar": "أمس", "hi": "कल", "tr": "Dün", "nl": "Gisteren", "pl": "Wczoraj", "sv": "Igår", "da": "I går", "fi": "Eilen", "nb": "I går", "cs": "Včera", "el": "Χθες", "he": "אתמול", "hu": "Tegnap", "id": "Kemarin", "ms": "Semalam", "ro": "Ieri", "th": "เมื่อวาน", "uk": "Вчора", "vi": "Hôm qua", "is": "Í gær", "ur": "کل"},
    
    # Text sizes
    "Small": {"es": "Pequeño", "fr": "Petit", "de": "Klein", "it": "Piccolo", "pt": "Pequeno", "ru": "Маленький", "ja": "小", "ko": "작게", "zh": "小", "ar": "صغير", "hi": "छोटा", "tr": "Küçük", "nl": "Klein", "pl": "Mały", "sv": "Liten", "da": "Lille", "fi": "Pieni", "nb": "Liten", "cs": "Malý", "el": "Μικρό", "he": "קטן", "hu": "Kicsi", "id": "Kecil", "ms": "Kecil", "ro": "Mic", "th": "เล็ก", "uk": "Малий", "vi": "Nhỏ", "is": "Lítið", "ur": "چھوٹا"},
    "Medium": {"es": "Mediano", "fr": "Moyen", "de": "Mittel", "it": "Medio", "pt": "Médio", "ru": "Средний", "ja": "中", "ko": "보통", "zh": "中", "ar": "متوسط", "hi": "मध्यम", "tr": "Orta", "nl": "Gemiddeld", "pl": "Średni", "sv": "Medium", "da": "Medium", "fi": "Keskikokoinen", "nb": "Medium", "cs": "Střední", "el": "Μεσαίο", "he": "בינוני", "hu": "Közepes", "id": "Sedang", "ms": "Sederhana", "ro": "Mediu", "th": "กลาง", "uk": "Середній", "vi": "Trung bình", "is": "Miðlungs", "ur": "درمیانہ"},
    "Large": {"es": "Grande", "fr": "Grand", "de": "Groß", "it": "Grande", "pt": "Grande", "ru": "Большой", "ja": "大", "ko": "크게", "zh": "大", "ar": "كبير", "hi": "बड़ा", "tr": "Büyük", "nl": "Groot", "pl": "Duży", "sv": "Stor", "da": "Stor", "fi": "Suuri", "nb": "Stor", "cs": "Velký", "el": "Μεγάλο", "he": "גדול", "hu": "Nagy", "id": "Besar", "ms": "Besar", "ro": "Mare", "th": "ใหญ่", "uk": "Великий", "vi": "Lớn", "is": "Stórt", "ur": "بڑا"},
    "X-Large": {"es": "Extra grande", "fr": "Très grand", "de": "Sehr groß", "it": "Extra grande", "pt": "Extra grande", "ru": "Очень большой", "ja": "特大", "ko": "아주 크게", "zh": "超大", "ar": "كبير جداً", "hi": "अतिरिक्त बड़ा", "tr": "Çok Büyük", "nl": "Extra groot", "pl": "Bardzo duży", "sv": "Extra stor", "da": "Ekstra stor", "fi": "Erittäin suuri", "nb": "Ekstra stor", "cs": "Velmi velký", "el": "Πολύ μεγάλο", "he": "גדול מאוד", "hu": "Extra nagy", "id": "Sangat besar", "ms": "Sangat besar", "ro": "Extra mare", "th": "ใหญ่พิเศษ", "uk": "Дуже великий", "vi": "Rất lớn", "is": "Mjög stórt", "ur": "بہت بڑا"},
}

# Map language codes to values folder names
LANG_TO_FOLDER = {
    "es": "values-es", "fr": "values-fr", "de": "values-de", "it": "values-it",
    "pt": "values-pt", "ru": "values-ru", "ja": "values-ja", "ko": "values-ko",
    "zh": "values-zh", "ar": "values-ar", "hi": "values-hi", "tr": "values-tr",
    "nl": "values-nl", "pl": "values-pl", "sv": "values-sv", "da": "values-da",
    "fi": "values-fi", "nb": "values-nb", "cs": "values-cs", "el": "values-el",
    "he": "values-iw", "hu": "values-hu", "id": "values-in", "ms": "values-ms",
    "ro": "values-ro", "th": "values-th", "uk": "values-uk", "vi": "values-vi",
    "is": "values-is", "ur": "values-ur"
}

def escape_xml(text):
    """Escape special characters for XML."""
    text = text.replace("&", "&amp;")
    text = text.replace("<", "&lt;")
    text = text.replace(">", "&gt;")
    text = text.replace('"', '\\"')
    # Handle apostrophes - escape if not already escaped
    if "'" in text and "\\'" not in text and "&apos;" not in text:
        text = text.replace("'", "\\'")
    return text

def get_english_strings():
    """Read all English strings."""
    path = BASE_PATH / "values" / "strings.xml"
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Parse strings with their full attributes
    pattern = r'<string name="([^"]+)"([^>]*)>([^<]*)</string>'
    strings = {}
    for match in re.finditer(pattern, content):
        name, attrs, value = match.groups()
        if 'translatable="false"' not in attrs:
            strings[name] = value
    return strings

def update_language(lang_code, english_strings):
    """Update a language file with translations."""
    folder = LANG_TO_FOLDER.get(lang_code)
    if not folder:
        return 0
    
    path = BASE_PATH / folder / "strings.xml"
    if not path.exists():
        return 0
    
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Get existing translations
    pattern = r'<string name="([^"]+)"[^>]*>([^<]*)</string>'
    existing = dict(re.findall(pattern, content))
    
    added = 0
    new_strings = []
    
    for key, en_value in english_strings.items():
        if key in existing:
            continue
        
        # Look for translation
        translation = None
        if en_value in TRANSLATIONS and lang_code in TRANSLATIONS[en_value]:
            translation = TRANSLATIONS[en_value][lang_code]
        
        if translation:
            escaped = escape_xml(translation)
            new_strings.append(f'    <string name="{key}">{escaped}</string>')
            added += 1
    
    if new_strings:
        # Insert before closing </resources>
        insert_point = content.rfind('</resources>')
        if insert_point > 0:
            new_content = content[:insert_point] + '\n'.join(new_strings) + '\n' + content[insert_point:]
            with open(path, 'w', encoding='utf-8') as f:
                f.write(new_content)
    
    return added

def main():
    print("Direct Translation Script")
    print("=" * 60)
    
    english = get_english_strings()
    print(f"Found {len(english)} English strings")
    print(f"Dictionary has {len(TRANSLATIONS)} common translations")
    print()
    
    total_added = 0
    for lang_code in LANG_TO_FOLDER.keys():
        added = update_language(lang_code, english)
        if added > 0:
            print(f"  {lang_code.upper()}: Added {added} translations")
            total_added += added
    
    print()
    print(f"Total: Added {total_added} translations across all languages")

if __name__ == "__main__":
    main()

