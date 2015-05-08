# JavaShellScript is a tool to run small Java programs as shell scripts. #

## How jss works ##
With jss (short for JavaShellScript) you can run java programs, without compiling them, directly as if they were shell scripts. jss will compile the Java-file and save the class file in a cache. When you call the Program the next time, jss will run the cached class file, if the source file isn't changed.

You can use jss with every Java program. The only thing you need to do is to add a [Shebang line](http://en.wikipedia.org/wiki/Shebang_(Unix)) to your source code. The file don't need to have the .java suffix.

## A "Hello World" sample ##
First create a file sample.java with the following content
```
#!/usr/bin/env jss
public class sample {
   public static void main(String[] args) {
       System.out.println("Hello World!");
   }
}
```

Make the file executable:
```
chmod +x sample.java
```

Run the program:
```
./sample.java
```