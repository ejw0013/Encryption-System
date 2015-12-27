Requirements:
1. JDK and JRE 8+
2. Unlimited JCE policy installed in the JDK and JRE Security Library.
3. Java included in Windows environmental variables or use Linux/Unix system.
4. An offline system when using the file encryption system.

How to use on Windows:
1. Place the FileEncryptionSystem folder within ROOT directory of your desired USB.
2. Place the batch file somewhere on your system.
3. Run the batch file.
4. Use the system as directed by the batch file.
5. Unplug the USB when complete.

How to use on Linux/Unix: (Sadly I haven't translated the batch file to shell code)
1. Place the FileEncryptionSystem folder within a directory of your desired USB.
2. Change to that directory in Terminal or Bash or whatever shell you like.
3. Compile the java files within the folder - javac *.java
4. To encrypt a file system:               java Interface.java -enc
5. To access a file:                       java Interface.java -acc
6. To update a file system:                java Interface.java -upd (note you should always update a file system after accessing a file.)
7. Unplug the USB drive when done. 

Note:
* DO NOT try to encrypt large files (>1.5Mb).
* DO NOT rename the root directory of a file encryption system.
* DO NOT rename a file when it is being accessed.
