#
# This ProGuard configuration file illustrates how to process applications.
# Usage:
#     java -jar proguard.jar @applications.pro
#

# Specify the input jars, output jars, and library jars.

-injars ../in.jar
-outjars ../out.jar

#-ignorewarnings

-libraryjars <java.home>/lib/rt.jar
-libraryjars ../lib/jnlp.jar
-libraryjars ../lib/servlet.jar
-libraryjars ../lib/log4j-1.2.8.jar


-dontskipnonpubliclibraryclasses 

-dontusemixedcaseclassnames


#-ignorewarnings

#-libraryjars servlet.jar
#-libraryjars jai_core.jar
#...

# Preserve all public applications.


# Print out a list of what we're preserving.
#-printseeds


-printmapping ../build/out.map

-renamesourcefileattribute SourceFile
-keepattributes InnerClasses,SourceFile,LineNumberTable,Deprecated,
                Signature,*Annotation*,EnclosingMethod

-keep public class * {
    public protected *;
}

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
