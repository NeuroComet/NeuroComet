# NeuroComet — Demo Guide

A practical script for showing off NeuroComet. It tells you **what to demo**, **in what order**, and **what to lock** so you only ever land on polished, finished screens.

> **TL;DR** — Turn on **Demo Mode** (Settings → long‑press → Developer Options → Demo Mode), lock the unfinished bits listed below, then walk through the "Demo Flow." Locked areas disappear from the navigation bar and can't be opened.

---

## 1. Before the demo (2‑minute setup)

1. Install the **debug build** on an authorized device.
2. Open the app → **Settings** tab → **long‑press** it to open **Developer Options**.
3. Open the **Demo Mode** section at the top.
4. Tap the master **Demo Mode** toggle **ON**.
5. Tap **Lock all**, then switch back ON only the features in the [Show / Demo](#2-show--demo-the-finished-experience) list below. (Or start from "Show all" and switch off the [Lock / Hide](#3-lock--hide-incomplete-or-experimental) items.)
6. Go back to the app. Locked tabs are now gone and locked screens are unreachable.

**Safety net:** Feed, Settings, and Developer Options can never be locked — you can always get back to turn Demo Mode off.

---

## 2. Show / Demo (the finished experience)

These are the parts worth presenting. Keep them **ON** in Demo Mode.

### Core experience
- **Feed** — the home timeline. Scroll, like, open a post, view a story.
- **Explore** — discovery / topics. Open a **Topic Detail**.
- **Wellness Dashboard** — the neurodivergent widgets and regulation overview.
- **Notifications** — grouped, ND‑friendly notifications.
- **Games Hub** → open a **Game** — quick, self‑contained, always demos well.

### Messaging & social
- **Messages** → open a **Conversation** (typing indicators, read receipts, wallpapers).
- **My Profile** and a **User Profile**.

### Settings & personalization (great "wow" surface)
- **Theme Settings** — light/dark, dynamic color.
- **App Icon Customization**.
- **Accessibility Settings** — reduce motion, screen‑reader mode.
- **Text & Display** — font scale, dyslexia‑friendly font.
- **Animation Settings**.
- **Wellbeing / Break Reminders**.
- **Content Filters**, **Privacy**, **Notification** settings.
- **Parental Controls**.

### Signature feature — Regulation Live Session
- From Developer Options → **Live Session Lab**, start a **Recharge / Focus / Stim** preset.
- Show the **ongoing Android notification** + the **in‑app live banner** (spoons, calming state, sensory break). This is NeuroComet's standout, Live‑Activities‑style surface.

---

## 3. Lock / Hide (incomplete or experimental)

Switch these **OFF** in Demo Mode so you never wander into them.

| Area | Why lock it |
|------|-------------|
| **Video Chat** (Feature Flag) | Experimental, off by default |
| **Story Reactions** (Feature Flag) | Experimental, off by default |
| **Advanced Search** (Feature Flag) | Experimental, off by default |
| **AI Suggestions** (Feature Flag) | Experimental, off by default |
| **New Feed Layout** (Feature Flag) | Experimental grid layout |
| **Practice Call Selection / Practice Call** | Voice practice flow still being polished |
| **Call History** | Depends on the call flow above |
| **Subscription / Premium** | Only show if billing is configured for the demo account |
| **Backup & Restore** | Dev/testing surface, not user‑facing yet |
| **Feedback Hub** | Internal beta tooling |

> Feature Flags live in **Developer Options → Feature Flags**. The screen‑level locks (Practice Call, Backup, etc.) live in **Developer Options → Demo Mode**.

---

## 4. Suggested Demo Flow (5–7 minutes)

1. **Open on Feed** — "This is the home timeline." Like a post, open a story.
2. **Explore** — tap into a topic to show discovery.
3. **Wellness Dashboard** — highlight the ND‑friendly widgets.
4. **Regulation Live Session** — start a Focus session; show the notification + live banner. *(Marquee moment.)*
5. **Messages** — open a conversation; point out typing indicators / read receipts.
6. **Settings → Theme + Accessibility + Text & Display** — show how deeply customizable and accessible it is. *(Strong closer for the neurodivergent‑first story.)*
7. **Games Hub** — quick, fun, optional finisher.

---

## 5. Talking points

- **Neurodivergent‑first:** reduced motion, sensory‑friendly modes, dyslexia‑friendly fonts, screen‑reader support, calming "spoons"‑based regulation sessions.
- **Accessibility is a feature, not an afterthought** — the Settings surface proves it.
- **Modern Android:** built for the latest platform (adaptive layouts for phone/tablet/desktop, Live‑Activities‑style ongoing sessions, handoff between devices).
- **Privacy & safety:** content filters, parental controls, kids mode.

---

## 6. After the demo

- Developer Options → **Demo Mode** → toggle **OFF** (or **Show all**) to restore every feature.
- Demo Mode state persists across restarts, so it's safe to leave configured between demos.

---

### Quick reference — what Demo Mode does
- **Locked tabs** are removed from the bottom bar / navigation rail.
- **Locked screens** can't be opened — any attempt bounces back to the Feed with a "not part of the demo" toast.
- **Feed, Settings, Developer Options** always stay available.
- Configure everything in **Developer Options → Demo Mode**.

