rem keytool -genkey -keystore keyngpcs 
rem keytool -selfcert -keystore keyngpcs
rem keytool -list -keystore keyngpcs

jarsigner -keystore keyngpcs ../public_html/install.jar mykey<passwd