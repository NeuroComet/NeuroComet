#!/usr/bin/env python3
"""
Translation generator for NeuroComet Android strings.
Generates translations for strings that are still in English.
"""

import os
import re
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import Element

RES_DIR = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'res')

def parse_strings_xml(filepath):
    """Parse strings.xml and return ordered list of (name, text, translatable) tuples."""
    tree = ET.parse(filepath)
    root = tree.getroot()
    strings = []
    for elem in root.findall('.//string'):
        name = elem.get('name')
        text = elem.text or ''
        translatable = elem.get('translatable', 'true') != 'false'
        strings.append((name, text, translatable))
    return strings, tree, root


def get_untranslated_strings(base_strings, lang_strings_dict):
    """Find strings that are identical to English (untranslated)."""
    untranslated = []
    for name, eng_text, translatable in base_strings:
        if not translatable:
            continue
        if name in lang_strings_dict and lang_strings_dict[name] == eng_text:
            # Skip app_name and empty strings
            if name in ['app_name'] or eng_text.strip() == '':
                continue
            # Skip format-only strings
            if re.match(r'^%\d+\$[ds]$', eng_text.strip()):
                continue
            untranslated.append((name, eng_text))
    return untranslated


def update_translations(lang_dir, translations_dict):
    """Update translations in a language file."""
    lang_file = os.path.join(RES_DIR, lang_dir, 'strings.xml')

    # Read the file content
    with open(lang_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace each translation
    for name, new_text in translations_dict.items():
        # Escape special characters for XML
        escaped_text = escape_xml(new_text)

        # Pattern to find and replace the string value
        pattern = rf'(<string name="{re.escape(name)}">)([^<]*)(</string>)'
        replacement = rf'\g<1>{escaped_text}\g<3>'
        content = re.sub(pattern, replacement, content)

    # Write back
    with open(lang_file, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Updated {len(translations_dict)} strings in {lang_dir}")


def escape_xml(text):
    """Escape special characters for Android XML."""
    if text is None:
        return ''
    # Escape apostrophes (but not already escaped ones)
    text = re.sub(r"(?<!\\)'", r"\\'", text)
    return text


# Pre-built translations for common languages
# These are high-quality translations for the app's UI strings

TRANSLATIONS = {}

# Turkish (tr) - 279 strings needed, ~80% done
TRANSLATIONS['values-tr'] = {
    # Auth strings
    "auth_app_tagline": "Her zihin için güvenli bir alan ✨",
    "auth_email_placeholder": "ornek@email.com",
    "auth_confirm_password_label": "Şifreyi Onayla",
    "auth_password_requirements": "🔒 Şifre büyük harf, küçük harf, rakam ve sembol içeren 12+ karakter olmalıdır.",
    "auth_passwords_not_match": "Şifreler eşleşmiyor",
    "auth_password_weak": "Şifre gereksinimleri karşılamıyor",
    "auth_skip": "Şimdilik geç",
    "auth_skip_dev": "Şimdilik geç (geliştirici)",
    "auth_data_secure": "Verileriniz şifrelenmiş ve güvende",
    "auth_2fa_title": "İki Faktörlü Doğrulama",
    "auth_2fa_description": "Cihazınıza gönderilen 6 haneli kodu girin",
    "auth_2fa_code_label": "6 Haneli Kod",
    "auth_2fa_verify": "Doğrula",
    "auth_hide_password": "Şifreyi gizle",
    "auth_show_password": "Şifreyi göster",
    "auth_tutorial_welcome_title": "Hoş Geldiniz 💜",
    "auth_tutorial_welcome_desc": "NeuroComet, nöroçeşitli ve LGBTQ+ bireyler için güvenli, destekleyici bir alandır. Olduğunuz gibi buraya aitsiniz—maske gerekmez.",
    "auth_tutorial_comfort_title": "Konforunuz Önemli ∞",
    "auth_tutorial_comfort_desc_anim": "Görsel hareketi azaltmak için sonsuzluk sembolüne dokunun. Duyusal dostu gezinme için tasarlandı.",
    "auth_tutorial_comfort_desc_static": "Mükemmel! Sakin modu etkinleştirdiniz. Daha fazla veya daha az hareket istediğinizde tekrar dokunun.",
    "auth_tutorial_themes_title": "25+ Nöro-Durum Teması 🎨",
    "auth_tutorial_themes_desc": "ADHD odaklanma, otizm konforu, anksiyete rahatlama, renk körlüğü erişilebilirliği ve ruh hali desteği için tasarlanmış temalar seçin.",
    "auth_tutorial_fonts_title": "Disleksi Dostu Yazı Tipleri 📖",
    "auth_tutorial_fonts_desc": "OpenDyslexic, Lexend ve Atkinson Hyperlegible dahil 12+ erişilebilirlik yazı tipi. Beyninizia en uygun şekilde ayarlayın.",
    "auth_tutorial_community_title": "Topluluk ve Gurur 🌈",
    "auth_tutorial_community_desc": "Anlayan insanlarla bağlantı kurun. Nöroçeşitli, LGBTQ+ ve destekçiler—tüm beyinler, tüm kimlikler, hepsi güzel.",
    "auth_tutorial_safety_title": "Güvenlik İçin Tasarlandı 🛡️",
    "auth_tutorial_safety_desc": "İçerik filtreleme, ebeveyn kontrolleri, ekran süresi sınırları ve uyku modu refahınızı korur.",
    "auth_tutorial_ready_title": "Başlamaya Hazır! ✨",
    "auth_tutorial_ready_desc": "Binlerce nöroçeşitli ve LGBTQ+ bireye katılmak için giriş yapın veya hesap oluşturun. Bu SİZİN alanınız.",
    "auth_tutorial_skip": "Geç",
    "auth_tutorial_continue": "Devam",
    "auth_tutorial_lets_go": "Başlayalım! ✨",
    "auth_tutorial_tap_here": "Buraya dokunun",

    # Splash messages
    "splash_tagline_default": "Sizin için tasarlanmış bir alan",
    "splash_tagline_hyperfocus": "Dikkat dağıtıcısız bölge",
    "splash_tagline_overload": "Düşük uyaran modu",
    "splash_tagline_calm": "Huzurlu alanınız",
    "splash_tagline_adhd_energized": "Dalgayı sür",
    "splash_tagline_adhd_low_dopamine": "Nazik motivasyon",
    "splash_tagline_adhd_task_mode": "Minimum dikkat dağıtıcı",
    "splash_tagline_autism_routine": "Öngörülebilir ve güvenli",
    "splash_tagline_autism_sensory_seek": "Zengin deneyimler sizi bekliyor",
    "splash_tagline_autism_low_stim": "Minimum uyaran",
    "splash_tagline_anxiety_soothe": "Zihninizi sakinleştiriyor",
    "splash_tagline_anxiety_grounding": "Şimdide köklü",
    "splash_tagline_dyslexia_friendly": "Netlik için tasarlandı",
    "splash_tagline_mood_tired": "Dinlenmek sorun değil",
    "splash_tagline_mood_anxious": "Bizimle nefes alın",
    "splash_tagline_mood_happy": "Sizi kutlayalım",
    "splash_tagline_mood_overwhelmed": "Yavaşça ilerliyoruz",
    "splash_tagline_mood_creative": "Fikirlerin akmasına izin verin",
    "splash_tagline_rainbow_brain": "Gökkuşağı beyninizi kucaklayın",

    # Splash messages
    "splash_msg_default_1": "Tekrar hoş geldiniz",
    "splash_msg_default_2": "Alanınız sizi bekliyor",
    "splash_msg_default_3": "Başlayalım",
    "splash_msg_hyperfocus_1": "Odak modu",
    "splash_msg_hyperfocus_2": "Berrak zihin önde",
    "splash_msg_hyperfocus_3": "Derine dalmaya hazır",
    "splash_msg_overload_1": "Yavaş ilerliyoruz",
    "splash_msg_overload_2": "Benimle nefes al",
    "splash_msg_overload_3": "Nazik tempo",
    "splash_msg_calm_1": "Huzur bekliyor",
    "splash_msg_calm_2": "Dinginlik modu",
    "splash_msg_calm_3": "Sakin sular",
    "splash_msg_adhd_energized_1": "Hadi başlayalım! ⚡",
    "splash_msg_adhd_energized_2": "Enerji açıldı",
    "splash_msg_adhd_energized_3": "Parlama zamanı",
    "splash_msg_adhd_low_dopamine_1": "Isınıyoruz",
    "splash_msg_adhd_low_dopamine_2": "Kıvılcımınızı buluyoruz",
    "splash_msg_adhd_low_dopamine_3": "Başarabilirsiniz",
    "splash_msg_adhd_task_mode_1": "Görev modu",
    "splash_msg_adhd_task_mode_2": "Bir seferde bir şey",
    "splash_msg_adhd_task_mode_3": "Odak aktif",
    "splash_msg_autism_routine_1": "Her zamanki gibi",
    "splash_msg_autism_routine_2": "Tanıdık desenler",
    "splash_msg_autism_routine_3": "Rutinde konfor",
    "splash_msg_autism_sensory_seek_1": "Duyusal keyif",
    "splash_msg_autism_sensory_seek_2": "Desenleri hisset",
    "splash_msg_autism_sensory_seek_3": "Tatmin edici havalar",
    "splash_msg_autism_low_stim_1": "Sessiz mod",
    "splash_msg_autism_low_stim_2": "Yumuşak ve nazik",
    "splash_msg_autism_low_stim_3": "Duyularınızı dinlendirin",
    "splash_msg_anxiety_soothe_1": "Burada güvendesiniz",
    "splash_msg_anxiety_soothe_2": "Nefes al, nefes ver",
    "splash_msg_anxiety_soothe_3": "Her şey yolunda",
    "splash_msg_anxiety_grounding_1": "Ayaklar yerde",
    "splash_msg_anxiety_grounding_2": "Şimdiki an",
    "splash_msg_anxiety_grounding_3": "Kararlı ve güvenli",
    "splash_msg_dyslexia_friendly_1": "Net ve okunabilir",
    "splash_msg_dyslexia_friendly_2": "Sizin yolunuz",
    "splash_msg_dyslexia_friendly_3": "Kolay okuma önde",
    "splash_msg_mood_tired_1": "Rahat olun",
    "splash_msg_mood_tired_2": "Acele yok",
    "splash_msg_mood_tired_3": "Nazik başlangıç",
    "splash_msg_mood_anxious_1": "İyisiniz",
    "splash_msg_mood_anxious_2": "Bu da geçecek",
    "splash_msg_mood_anxious_3": "Güvenli alan",
    "splash_msg_mood_happy_1": "Merhaba güneş! ☀️",
    "splash_msg_mood_happy_2": "Harika havalar",
    "splash_msg_mood_happy_3": "Neşe bekliyor",
    "splash_msg_mood_overwhelmed_1": "Bir nefes",
    "splash_msg_mood_overwhelmed_2": "Yavaşlıyoruz",
    "splash_msg_mood_overwhelmed_3": "Yanınızdayız",
    "splash_msg_mood_creative_1": "İlham geliyor",
    "splash_msg_mood_creative_2": "Özgürce yarat",
    "splash_msg_mood_creative_3": "Hayal gücü modu",
    "splash_msg_rainbow_1": "🦄 Sırrı buldunuz!",
    "splash_msg_rainbow_2": "🌈 Eşsiz zihninizi kutlayın",
    "splash_msg_rainbow_3": "✨ Nöroçeşitlilik sihirdir",
    "splash_msg_rainbow_4": "🧠 Beyniniz güzel",
    "splash_msg_rainbow_5": "💜 Farklı olmak güçtür",

    # Neuro categories and states
    "neuro_category_basic": "Temel Temalar",
    "neuro_category_adhd": "DEHB Temaları",
    "neuro_category_autism": "Otizm Temaları",
    "neuro_category_anxiety": "Anksiyete/OKB Temaları",
    "neuro_category_accessibility": "Erişilebilirlik",
    "neuro_category_colorblind": "Renk Körlüğü Dostu",
    "neuro_category_blind": "Görme Engelli ve Az Gören",
    "neuro_category_mood": "Nasıl Hissediyorsunuz?",
    "neuro_category_secret": "🦄 Gizli Temalar",
    "neuro_state_default": "Varsayılan",
    "neuro_state_hyperfocus": "Hiper Odak",
    "neuro_state_overload": "Duyusal Aşırı Yük",
    "neuro_state_calm": "Sakin",
    "neuro_state_adhd_energized": "DEHB - Enerjik",
    "neuro_state_adhd_low_dopamine": "DEHB - Düşük Dopamin",
    "neuro_state_adhd_task_mode": "DEHB - Görev Modu",
    "neuro_state_autism_routine": "Otizm - Rutin",
    "neuro_state_autism_sensory_seek": "Otizm - Duyusal Arayış",
    "neuro_state_autism_low_stim": "Otizm - Düşük Uyaran",
    "neuro_state_anxiety_soothe": "Anksiyete - Yatıştırma",
    "neuro_state_anxiety_grounding": "Anksiyete - Topraklama",
    "neuro_state_dyslexia_friendly": "Disleksi Dostu",
    "neuro_state_mood_tired": "Yorgun Hissediyorum",
    "neuro_state_mood_anxious": "Endişeli Hissediyorum",
    "neuro_state_mood_happy": "Mutlu Hissediyorum",
    "neuro_state_mood_overwhelmed": "Bunalmış Hissediyorum",
    "neuro_state_mood_creative": "Yaratıcı Hissediyorum",
    "neuro_state_rainbow_brain": "Gökkuşağı Beyin",

    # State descriptions
    "neuro_state_default_desc": "Dengeli renklerle standart tema",
    "neuro_state_hyperfocus_desc": "Derin konsantrasyon için yüksek kontrast",
    "neuro_state_overload_desc": "Uyaranı azaltmak için yumuşak, sakin renkler",
    "neuro_state_calm_desc": "Rahatlama için yumuşak, yatıştırıcı renkler",
    "neuro_state_adhd_energized_desc": "Verimli günler için parlak, çekici renkler",
    "neuro_state_adhd_low_dopamine_desc": "Ruh halini artırmak için sıcak, uyarıcı renkler",
    "neuro_state_adhd_task_mode_desc": "Minimum dikkat dağıtıcı, odak artırıcı palet",
    "neuro_state_autism_routine_desc": "Öngörülebilir, tutarlı renk desenleri",
    "neuro_state_autism_sensory_seek_desc": "Zengin dokular ve tatmin edici kontrastlar",
    "neuro_state_autism_low_stim_desc": "Çok yumuşak, nazik renkler",
    "neuro_state_anxiety_soothe_desc": "Endişeyi hafifletmek için serin, güven verici renkler",
    "neuro_state_anxiety_grounding_desc": "Merkezleme için toprak, kararlı renkler",
    "neuro_state_dyslexia_friendly_desc": "Optimum kontrastla yüksek okunabilirlik",
    "neuro_state_mood_tired_desc": "Gözleri yormayan nazik renkler",
    "neuro_state_mood_anxious_desc": "Stresi azaltmak için sakinleştirici palet",
    "neuro_state_mood_happy_desc": "Ruh halinize uygun neşeli renkler",
    "neuro_state_mood_overwhelmed_desc": "Basitleştirilmiş, sessiz palet",
    "neuro_state_mood_creative_desc": "Hayal gücünü besleyen ilham verici renkler",
    "neuro_state_rainbow_brain_desc": "Güzelce eşsiz nöroçeşitli zihninizi kutlayın! 🌈🧠",

    # Accessibility
    "accessibility_selected": "seçildi",
    "accessibility_not_selected": "seçilmedi",
    "expand_category": "%1$s genişlet",
    "collapse_category": "%1$s daralt",
    "easter_egg_secret_unlocked": "🎊 Gizli Açıldı! 🎊",
    "easter_egg_welcome_back": "🦄 Tekrar Hoş Geldiniz! 🦄",

    # DM strings
    "dm_title": "Mesajlar",
    "dm_search_placeholder": "Mesaj ara",
    "dm_empty_title": "Henüz konuşma yok",
    "dm_empty_subtitle": "Biri size mesaj attığında, burada göreceksiniz.",
    "dm_no_conversations": "Konuşma yok",
    "dm_conversation_count": "%d konuşma",
    "dm_conversations_count": "%d konuşma",
    "dm_new_message_hint": "Birinin profilinden yeni bir konuşma başlatın! 💬",
    "dm_new_message": "Yeni Mesaj",
    "messages_empty_title": "Henüz konuşma yok",
    "messages_empty_subtitle": "Birine mesaj attığınızda, burada görünecek.",
    "messages_empty_hint": "İpucu: Keşfet → bir profile dokunun → Mesaj (mock).",
    "messages_no_results_title": "Sonuç yok",
    "messages_no_results_subtitle": "Farklı bir arama deneyin veya Tümü'ne geri dönün.",
    "messages_filtering_unread": "Okunmamış olanlara filtreliyorsunuz.",
    "dm_kids_disabled_title": "Mesajlar kapatıldı",
    "dm_kids_disabled_body": "Çocuk modu için (13 yaş altı) Direkt Mesajlar devre dışı bırakıldı.",
    "dm_conversation_options": "Konuşma seçenekleri",
    "dm_safety_title": "Güvenlik",
    "dm_safety_body": "Bir şey yanlış hissediyorsa, kötüye kullanım mesajlarını bildirin.",
    "dm_message_placeholder": "Mesaj",
    "dm_send": "Gönder",
    "dm_back": "Geri",
    "dm_report": "Bildir",
    "dm_flagged_badge": "İşaretlendi",
    "dm_flagged_explainer": "İnceleme için işaretlendi",
    "dm_slow_down": "Yavaşlayın…",
    "dm_copy": "Kopyala",
    "dm_block_user": "Kullanıcıyı engelle",
    "dm_unblock_user": "Engeli kaldır",
    "dm_mute_user": "Sessize al",
    "dm_unmute_user": "Sessizden çıkar",
    "dm_block_confirm_title": "%s engellensin mi?",
    "dm_block_confirm_body": "Size mesaj gönderemezler ve bu konuşma gizlenecek.",
    "dm_unblock_confirm_title": "%s engeli kaldırılsın mı?",
    "dm_unblock_confirm_body": "Size tekrar mesaj gönderebilecekler.",
    "dm_copied": "Kopyalandı",
    "dm_sending": "Gönderiliyor…",
    "dm_failed": "Başarısız",
    "dm_tap_to_retry": "Yeniden denemek için dokunun",
    "dm_blocked_banner": "Bu kullanıcıyı engellediniz.",
    "dm_rate_limited": "Çok fazla mesaj. Lütfen yavaşlayın.",
    "dm_send_failed": "Mesaj gönderilemedi. Dokunun ve yeniden deneyin.",

    # Status
    "status_online": "Çevrimiçi",
    "status_offline": "Çevrimdışı",
    "status_blocked": "Engellendi",
    "status_muted": "Sessize alındı",
    "status_away": "Uzakta",
}

# Swedish (sv) - 414 strings needed, ~71% done
TRANSLATIONS['values-sv'] = {
    "auth_app_tagline": "Ett tryggt utrymme för varje sinne ✨",
    "auth_email_placeholder": "din@email.com",
    "auth_confirm_password_label": "Bekräfta lösenord",
    "auth_password_requirements": "🔒 Lösenordet måste vara 12+ tecken med versaler, gemener, siffror och symboler.",
    "auth_passwords_not_match": "Lösenorden matchar inte",
    "auth_password_weak": "Lösenordet uppfyller inte kraven",
    "auth_skip": "Hoppa över för nu",
    "auth_skip_dev": "Hoppa över för nu (dev)",
    "auth_data_secure": "Din data är krypterad och säker",
    "auth_2fa_title": "Tvåfaktorsautentisering",
    "auth_2fa_description": "Ange den 6-siffriga koden som skickats till din enhet",
    "auth_2fa_code_label": "6-siffrig kod",
    "auth_2fa_verify": "Verifiera",
    "auth_hide_password": "Dölj lösenord",
    "auth_show_password": "Visa lösenord",
    "auth_tutorial_welcome_title": "Välkommen Hem 💜",
    "auth_tutorial_welcome_desc": "NeuroComet är ett tryggt, bekräftande utrymme för neurodivergenta och LGBTQ+ sinnen. Du hör hemma här precis som du är.",
    "auth_tutorial_comfort_title": "Din Komfort Spelar Roll ∞",
    "auth_tutorial_comfort_desc_anim": "Tryck på oändlighetssymbolen när som helst för att minska visuell rörelse. Vi designade detta för sensorivänlig surfning.",
    "auth_tutorial_comfort_desc_static": "Perfekt! Du har aktiverat lugnt läge. Tryck igen när du behöver mer eller mindre rörelse.",
    "auth_tutorial_themes_title": "25+ Neuro-Teman 🎨",
    "auth_tutorial_themes_desc": "Välj teman designade för ADHD-fokus, autismkomfort, ångestlindring, färgblinds tillgänglighet och humörstöd.",
    "auth_tutorial_fonts_title": "Dyslexi-Vänliga Typsnitt 📖",
    "auth_tutorial_fonts_desc": "12+ tillgänglighetstypsnitt inklusive OpenDyslexic, Lexend och Atkinson Hyperlegible.",
    "auth_tutorial_community_title": "Gemenskap & Stolthet 🌈",
    "auth_tutorial_community_desc": "Anslut med människor som förstår. Neurodivergenta, LGBTQ+ och allierade—alla hjärnor, alla identiteter, alla vackra.",
    "auth_tutorial_safety_title": "Byggd för Säkerhet 🛡️",
    "auth_tutorial_safety_desc": "Innehållsfiltrering, föräldrakontroller, skärmtidsbegränsningar och sovläge skyddar ditt välbefinnande.",
    "auth_tutorial_ready_title": "Redo att Börja! ✨",
    "auth_tutorial_ready_desc": "Logga in eller skapa ditt konto för att gå med tusentals neurodivergenta och LGBTQ+ individer.",
    "auth_tutorial_skip": "Hoppa över",
    "auth_tutorial_continue": "Fortsätt",
    "auth_tutorial_lets_go": "Nu kör vi! ✨",
    "auth_tutorial_tap_here": "Tryck här",

    # Splash
    "splash_tagline_default": "Ett utrymme designat för dig",
    "splash_tagline_hyperfocus": "Distraktionsfri zon",
    "splash_tagline_overload": "Låg stimulering-läge",
    "splash_tagline_calm": "Ditt lugna utrymme",
    "splash_tagline_adhd_energized": "Surfa på vågen",
    "splash_tagline_adhd_low_dopamine": "Varsam motivation",
    "splash_tagline_adhd_task_mode": "Minimala distraktioner",
    "splash_tagline_autism_routine": "Förutsägbart & säkert",
    "splash_tagline_autism_sensory_seek": "Rika upplevelser väntar",
    "splash_tagline_autism_low_stim": "Minimal stimulering",
    "splash_tagline_anxiety_soothe": "Lugnar ditt sinne",
    "splash_tagline_anxiety_grounding": "Rotad i nuet",
    "splash_tagline_dyslexia_friendly": "Designad för klarhet",
    "splash_tagline_mood_tired": "Vila är okej",
    "splash_tagline_mood_anxious": "Andas med oss",
    "splash_tagline_mood_happy": "Låt oss fira dig",
    "splash_tagline_mood_overwhelmed": "Tar det varsamt",
    "splash_tagline_mood_creative": "Låt idéerna flöda",
    "splash_tagline_rainbow_brain": "Omfamna din regnbågshjärna",

    # DM
    "dm_title": "Meddelanden",
    "dm_search_placeholder": "Sök meddelanden",
    "dm_empty_title": "Inga konversationer ännu",
    "dm_empty_subtitle": "När någon skickar meddelande till dig ser du det här.",
    "dm_no_conversations": "Inga konversationer",
    "dm_new_message": "Nytt meddelande",
    "dm_send": "Skicka",
    "dm_back": "Tillbaka",
    "dm_report": "Rapportera",
    "dm_copy": "Kopiera",
    "dm_block_user": "Blockera användare",
    "dm_unblock_user": "Avblockera användare",
    "dm_mute_user": "Tysta",
    "dm_unmute_user": "Slå på ljud",
    "dm_copied": "Kopierat",
    "dm_sending": "Skickar…",
    "dm_failed": "Misslyckades",
    "dm_tap_to_retry": "Tryck för att försöka igen",

    # Status
    "status_online": "Online",
    "status_offline": "Offline",
    "status_blocked": "Blockerad",
    "status_muted": "Tystad",
    "status_away": "Borta",

    # Settings
    "settings_profile_group": "Profil",
    "settings_badges_title": "Märken",
    "settings_badges_subtitle": "Visa dina intjänade prestationer och mål.",
    "settings_visual_comfort_group": "Visuell Komfort & Teman",
    "settings_text_size_title": "Textstorlek",
    "settings_dark_mode_title": "Mörkt Läge",
    "settings_dark_mode_enabled": "Aktiverat",
    "settings_dark_mode_disabled": "Inaktiverat",
    "settings_high_contrast_title": "Högkontrastläge",
    "settings_neuro_centric_theme_title": "Neuro-Centriskt Tema",
    "settings_go_premium_title": "Bli Premium",
    "settings_go_premium_subtitle": "Ta bort annonser och stöd utvecklarna.",
    "settings_account_group": "Konto",
    "settings_logout": "Logga ut",
    "settings_youre_awesome": "Du är fantastisk! 🙌",
    "settings_not_authenticated": "Användaren är inte autentiserad.",
    "settings_verified_human_title": "Verifierad Människa",
    "settings_2fa_title": "Tvåfaktorsautentisering",
    "settings_2fa_subtitle": "Kräv en kod vid inloggning.",
}

# French (fr) - 440 strings needed, ~69% done
TRANSLATIONS['values-fr'] = {
    "auth_app_tagline": "Un espace sûr pour chaque esprit ✨",
    "auth_email_placeholder": "votre@email.com",
    "auth_confirm_password_label": "Confirmer le mot de passe",
    "auth_password_requirements": "🔒 Le mot de passe doit contenir 12+ caractères avec majuscules, minuscules, chiffres et symboles.",
    "auth_passwords_not_match": "Les mots de passe ne correspondent pas",
    "auth_password_weak": "Le mot de passe ne répond pas aux exigences",
    "auth_skip": "Ignorer pour le moment",
    "auth_skip_dev": "Ignorer pour le moment (dev)",
    "auth_data_secure": "Vos données sont cryptées et sécurisées",
    "auth_2fa_title": "Authentification à deux facteurs",
    "auth_2fa_description": "Entrez le code à 6 chiffres envoyé à votre appareil",
    "auth_2fa_code_label": "Code à 6 chiffres",
    "auth_2fa_verify": "Vérifier",
    "auth_hide_password": "Masquer le mot de passe",
    "auth_show_password": "Afficher le mot de passe",
    "auth_tutorial_welcome_title": "Bienvenue Chez Vous 💜",
    "auth_tutorial_welcome_desc": "NeuroComet est un espace sûr et bienveillant pour les esprits neurodivergents et LGBTQ+. Vous appartenez ici tel que vous êtes.",
    "auth_tutorial_comfort_title": "Votre Confort Compte ∞",
    "auth_tutorial_comfort_desc_anim": "Appuyez sur le symbole de l'infini pour réduire le mouvement visuel. Conçu pour une navigation sensorielle.",
    "auth_tutorial_comfort_desc_static": "Parfait ! Vous avez activé le mode calme. Appuyez à nouveau quand vous voulez plus ou moins de mouvement.",
    "auth_tutorial_themes_title": "25+ Thèmes Neuro 🎨",
    "auth_tutorial_themes_desc": "Choisissez des thèmes conçus pour la concentration TDAH, le confort autistique, le soulagement de l'anxiété et plus encore.",
    "auth_tutorial_fonts_title": "Polices Dyslexie-Friendly 📖",
    "auth_tutorial_fonts_desc": "12+ polices d'accessibilité incluant OpenDyslexic, Lexend et Atkinson Hyperlegible.",
    "auth_tutorial_community_title": "Communauté & Fierté 🌈",
    "auth_tutorial_community_desc": "Connectez-vous avec des gens qui comprennent. Neurodivergents, LGBTQ+ et alliés—tous les cerveaux, toutes les identités.",
    "auth_tutorial_safety_title": "Conçu pour la Sécurité 🛡️",
    "auth_tutorial_safety_desc": "Filtrage de contenu, contrôles parentaux, limites de temps d'écran et mode nuit protègent votre bien-être.",
    "auth_tutorial_ready_title": "Prêt à Commencer ! ✨",
    "auth_tutorial_ready_desc": "Connectez-vous ou créez votre compte pour rejoindre des milliers de personnes neurodivergentes et LGBTQ+.",
    "auth_tutorial_skip": "Ignorer",
    "auth_tutorial_continue": "Continuer",
    "auth_tutorial_lets_go": "Allons-y ! ✨",
    "auth_tutorial_tap_here": "Appuyez ici",

    # Splash
    "splash_tagline_default": "Un espace conçu pour vous",
    "splash_tagline_hyperfocus": "Zone sans distraction",
    "splash_tagline_overload": "Mode faible stimulation",
    "splash_tagline_calm": "Votre espace tranquille",
    "splash_tagline_adhd_energized": "Surfez sur la vague",
    "splash_tagline_adhd_low_dopamine": "Motivation douce",
    "splash_tagline_adhd_task_mode": "Distractions minimales",
    "splash_tagline_autism_routine": "Prévisible & sûr",
    "splash_tagline_autism_sensory_seek": "Des expériences riches vous attendent",
    "splash_tagline_autism_low_stim": "Stimulation minimale",
    "splash_tagline_anxiety_soothe": "Apaisant votre esprit",
    "splash_tagline_anxiety_grounding": "Ancré dans le présent",
    "splash_tagline_dyslexia_friendly": "Conçu pour la clarté",
    "splash_tagline_mood_tired": "Se reposer c'est bien",
    "splash_tagline_mood_anxious": "Respirez avec nous",
    "splash_tagline_mood_happy": "Célébrons-vous",
    "splash_tagline_mood_overwhelmed": "Allons-y doucement",
    "splash_tagline_mood_creative": "Laissez les idées couler",
    "splash_tagline_rainbow_brain": "Embrassez votre cerveau arc-en-ciel",

    # DM
    "dm_title": "Messages",
    "dm_search_placeholder": "Rechercher des messages",
    "dm_empty_title": "Pas encore de conversations",
    "dm_empty_subtitle": "Quand quelqu'un vous envoie un message, vous le verrez ici.",
    "dm_no_conversations": "Pas de conversations",
    "dm_new_message": "Nouveau message",
    "dm_send": "Envoyer",
    "dm_back": "Retour",
    "dm_report": "Signaler",
    "dm_copy": "Copier",
    "dm_block_user": "Bloquer l'utilisateur",
    "dm_unblock_user": "Débloquer l'utilisateur",
    "dm_mute_user": "Couper le son",
    "dm_unmute_user": "Rétablir le son",
    "dm_copied": "Copié",
    "dm_sending": "Envoi…",
    "dm_failed": "Échec",
    "dm_tap_to_retry": "Appuyez pour réessayer",

    # Status
    "status_online": "En ligne",
    "status_offline": "Hors ligne",
    "status_blocked": "Bloqué",
    "status_muted": "Mis en sourdine",
    "status_away": "Absent",
}

# Add more languages...

def apply_translations(lang_dir):
    """Apply pre-built translations to a language."""
    if lang_dir not in TRANSLATIONS:
        print(f"No translations available for {lang_dir}")
        return

    update_translations(lang_dir, TRANSLATIONS[lang_dir])


def main():
    """Apply translations to all configured languages."""
    for lang_dir in TRANSLATIONS:
        print(f"\nApplying translations to {lang_dir}...")
        apply_translations(lang_dir)

    print("\n✅ Translations applied successfully!")
    print("Run 'python translate_strings.py' to see updated status.")


if __name__ == '__main__':
    main()

