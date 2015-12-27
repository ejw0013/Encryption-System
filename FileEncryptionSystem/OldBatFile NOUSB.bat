@echo off
cd FileEncryptionSystem
javac *.java
pause
cls

:choice
echo File Encryption Menu
echo.
echo What would you like to do?
echo 1. Set up new Encrypted File System
echo 2. Access an existing Encrypted File System
echo 3. Update file system
echo 4. Quit
set /P c=""
if /I "%c%" EQU "1" goto :new
if /I "%c%" EQU "2" goto :access
if /I "%c%" EQU "3" goto :update
if /I "%c%" EQU "4" goto :quit
goto :choice

:new
java Interface -enc
pause
goto :choice

:access
java Interface -acc
pause
goto :update

:update
set /P n=Do you want to update a file system?[Y/N]
if /I "%n%" EQU "Y" goto :updateY
if /I "%n%" EQU "N" goto :updateN
goto :update

:updateY
java Interface -upd
goto :choice

:updateN
goto :choice

:quit
echo Thanks for using this Encrypted File System project!
pause
echo.
exit
