@echo off
:usb
echo.
echo Please insert the USB with the File Encryption System Files.
set /P n=Are you ready to proceed?[Y/N]
if /I "%n%" EQU "Y" goto :gotousb
if /I "%n%" EQU "N" goto :usb

:usberror
echo.
echo Error, no USB detected.
echo Please insert the USB with the File Encryption System Files.
set /P n=Are you ready to proceed?[Y/N]
if /I "%n%" EQU "Y" goto :gotousb
if /I "%n%" EQU "N" goto :usb

:gotousb
for /F "tokens=1*" %%a in ('fsutil fsinfo drives') do (
   for %%c in (%%b) do (
      for /F "tokens=3" %%d in ('fsutil fsinfo drivetype %%c') do (
         if %%d equ Removable (
            set drv=%%c
         )
      )
   )
)
cd /d %drv%
echo Changing directory to %drv%
if /I "%drv%" EQU "" goto :usberror
pause
cd FileEncryptionSystem
javac *.java
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
