@echo off
cd /d "c:\Users\VINAYAK CHINNARATHOD\Documents\Bank System Website\backend\src\main\java\com\bank\securebank\controller"
echo Deleting duplicate AdminController files...
del /f /q "AdminController-FIXED.java" 2>nul
del /f /q "AdminController-SIMPLE.java" 2>nul
echo Done!
dir *.java
pause
