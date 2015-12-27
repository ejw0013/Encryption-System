# File Encryption System

* Authors: Erich Wu, Ramaswamy Muthukumar
* Version: 12/27/2015

This is a file encryption system developed in an information security class. The goal of this system is to develop a system of storing keys and randomly generated salts for multiple file trees. An offline system will be able to securely encrypt and store the keys in a flash drive or similar storage device. The keys and salts are generated uniquely for each file system and some of the keys are stored by adding entropy upon each file access.

The current system allows access to only a *single* file at a time of a tree. After access the system should be "updated" which will generate a set of new keys that encrypts the file key.

### Requirements:
1. JDK and JRE 8+
2. [Unlimited JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) policy installed in the JDK and JRE Security Library.
3. Java included in Windows environmental variables or use Linux/Unix system.
4. An offline system when using the file encryption system (Unplug or disable your network adapter before accessing the drive with the keys!).

### How to use on Windows:

1. Place the FileEncryptionSystem folder within ROOT directory of your desired USB.
2. Place the batch file somewhere on your system.
3. Run the batch file.
4. Use the system as directed by the batch file.
5. Unplug the USB when complete.

### How to use on Linux/Unix: (Sadly I haven't translated the batch file to shell code)

1. Place the FileEncryptionSystem folder within a directory of your desired USB.
2. Change to that directory in Terminal or Bash or whatever shell you like.
3. Compile the java files within the folder - javac *.java
4. To encrypt a file system:               java Interface.java -enc
5. To access a file:                       java Interface.java -acc
6. To update a file system:                java Interface.java -upd (note you should always update a file system after accessing a file.)
7. Unplug the USB drive when done.

### *Note*:
* DO NOT try to encrypt large files (>1.5Mb). May work, may not. Depends a lot on the system you're running on. Java AES-256 decryption is a bit slow. [Read More](http://stackoverflow.com/questions/26920906/how-come-putting-the-gcm-authentication-tag-at-the-end-of-a-cipher-stream-requir?rq=1)
* DO NOT rename the root directory of a file encryption system.
* DO NOT rename a file when it is being accessed.
