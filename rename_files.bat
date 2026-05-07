@echo off
cd /d "c:\Users\VINAYAK CHINNARATHOD\Documents\Bank System Website\backend\src\main\java\com\bank\securebank\controller"
echo Renaming duplicate AdminController files...
ren "AdminController-FIXED.java" "AdminController-FIXED.java.bak"
ren "AdminController-SIMPLE.java" "AdminController-SIMPLE.java.bak"
echo Done!
dir *.java
pause
