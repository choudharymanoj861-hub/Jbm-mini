JBM MINI - STOCK MANAGER (Android project)

Is sample app mein:
- Door stock add karna
- Total pieces dekhna
- Stock item tap karke edit karna
- Stock item long-press karke delete karna
- Data phone ke local SQLite database mein save hota hai; internet nahi chahiye.

IMPORTANT:
Is chat ke current build environment mein Android SDK/Gradle installed nahi hai, isliye yahan APK compile nahi ho saka. Yeh Android Studio/Gradle project source hai, APK file nahi.

APK banane ka sabse seedha remote tareeka:
1. Is ZIP ko GitHub par ek naye repository mein upload karein.
2. Repository ka naam JBMMini rakhein (naam kuch bhi ho sakta hai).
3. GitHub mein Actions tab kholein.
4. 'Build JBM Mini APK' workflow select karke 'Run workflow' dabayein.
5. Build complete hone par run ke Artifacts section se 'JBM-Mini-debug-apk' download karein.
6. ZIP ke andar app-debug.apk milega. Android phone par install karein.

Android Studio mein bhi project folder kholkar Build > Build Bundle(s) / APK(s) > Build APK(s) se build kiya ja sakta hai.
