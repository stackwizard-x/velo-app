VELO - APK build (no Android Studio needed)

1. Create a free account at github.com, make a NEW repository (private is fine).
2. Upload everything in this folder to it (drag & drop in the browser:
   "Add file" > "Upload files"). Make sure the hidden .github folder goes too.
3. Open the repo > Actions tab > "Build Velo APK" > wait ~5 minutes for the green tick.
4. Click the finished run > Artifacts > download "velo-apk", unzip it.
5. Send app-debug.apk to your phone, tap it, allow "install unknown apps". Done.

To change the app later: edit www/index.html, upload it again, and a new APK builds.
