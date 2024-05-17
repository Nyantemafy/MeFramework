@echo off
setlocal

rem Le nom de l'app web compresser en jar
set jar_name=framework

rem Le chemin du poste de travail
set work_dir=.

rem Le dossier de librairies d'independances
set bin=%work_dir%\bin

rem Le dossier de librairies d'independances
set lib=%work_dir%\lib

rem Le dossier des fichiers sources java
set src=%work_dir%\src

@REM :: Compiler les fichiers java en utilisant les jar
@REM for %%f in (src\mg\itu\prom16\*.java) do (
@REM     javac -d . "%%f" -cp "%lib%\*";"%src%"
@REM )
 
rem Aller dans le répertoire de destination des fichiers compilés
cd "%bin%"

rem Compresser dans un fichier jar
jar -cvf "../%lib%/%jar_name%.jar" *

echo Fichier .jar créé : %jar_name%.jar

endlocal