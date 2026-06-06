# neuro_comet

NeuroComet Flutter client.

## Supabase configuration

This app reads Supabase config from compile-time Dart defines. It no longer ships with a hardcoded fallback URL or anon key.

Required defines:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`

### Run locally

```powershell
flutter pub get
flutter run --dart-define=SUPABASE_URL=https://YOUR-PROJECT-REF.supabase.co --dart-define=SUPABASE_ANON_KEY=eyJ...
```

### Build locally

```powershell
flutter build apk --dart-define=SUPABASE_URL=https://YOUR-PROJECT-REF.supabase.co --dart-define=SUPABASE_ANON_KEY=eyJ...
```

## Notes

- The root `local.properties` file is used by the native Android app in `../app/`.
- The Flutter app in this folder does not read the root `local.properties` Supabase values automatically.
- If Supabase is not configured, the app now skips Supabase initialization and auth screens will show a configuration message instead of silently using stale credentials.
