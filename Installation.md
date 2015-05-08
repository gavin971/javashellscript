# Prerequisite #
  * you must have a Java 1.6 JDK on your System
  * to check that run `java -version` and `javac -version`
  * The jsslib is compiled with Sun Java 1.6, if you use a other VM (maybe GNU Java) you will maybe have problems.

# Installation #
  * Download the tar-archive for your system
  * unpack it where you want. That can be in your home directory if you don't have root permissions.
```
tar -xvf jss-0.1-r44-macosx-intel-x64.tar.gz
```
  * create a symbolic link to the jss executable in a directory inside your PATH-variable
```
ln -s ${install_dir}/jss/jss /usr/bin/jss
```
  * or add the jss directory to your PATH variable (if you don't have root permissions).

# Configuration #
Normally you don't need to do any configurations. But if you have more then one JDK on your System, then you can chose the one you would like to use by editing the file `${install_dir}/jss/jss.conf`. This file contains the commands to call `java` and the Java-Compiler `javac`.

The default content of `${install_dir}/jss/jss.conf`
```
javac=javac
java=java
```

The content of `${install_dir}/jss/jss.conf` if your JDK is in `/usr/lib/jvm/java-6-sun/`
```
javac=/usr/lib/jvm/java-6-sun/bin/javac
java=/usr/lib/jvm/java-6-sun/bin/java
```