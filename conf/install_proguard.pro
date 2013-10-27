#
# This ProGuard configuration file illustrates how to process applications.
# Usage:
#     java -jar proguard.jar @applications.pro
#

# Specify the input jars, output jars, and library jars.

-injars  ../tmp.jar
-outjars ../public_html/install.jar

#-ignorewarnings

-libraryjars <java.home>/lib/rt.jar
-libraryjars ../lib/jnlp.jar
-libraryjars ../lib/servlet.jar
#-libraryjars ../lib/ant.jar

-dontskipnonpubliclibraryclasses 

-dontusemixedcaseclassnames


#-ignorewarnings

#-libraryjars servlet.jar
#-libraryjars jai_core.jar
#...

# Preserve all public applications.

-keepclasseswithmembers public class com.inca.npserver.clientinstall.ClientInstaller {
    public static void main(java.lang.String[]);
}

# Print out a list of what we're preserving.

-printseeds

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.




# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward 
# compatible, please refer to the manual.


#-keepclassmembers class CSteModel {
#}


-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




# Your application may contain more items that need to be preserved; 
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface

#-keep public class com.inca.np.gui.control.**
#-keep public class com.inca.np.gui.ste.**

