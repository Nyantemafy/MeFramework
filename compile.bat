@echo off

REM créer un dossier qui contient tout 

REM Répertoire contenant vos fichiers .java
set "JAVA_DIR=.\src\mg\itu\prom16"

REM Répertoire contenant vos fichiers .class
set "CLASSES_DIR=java"


REM Nom de votre fichier JAR
set "JAR_FILE=Meframework.jar"

REM Compilez tous les fichiers .java
javac -d  %CLASSES_DIR% %JAVA_DIR%\*.java

REM Création du fichier JAR
jar cvf %JAR_FILE% -C %CLASSES_DIR% .

REM supprimer le dossier contenant les .classes 
rmdir /s /q  %CLASSES_DIR%

REM couper le fichier .jar vers lib 
xcopy /s /q /y "Meframework.jar" "C:\Users\Ny Antema\Documents\Antema\s4\s4\Mr_Naina\MeFramework\sprint4\Test\lib"