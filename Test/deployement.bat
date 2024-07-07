@ECHO OFF
:: Declaration des variables

rem Le chemin du poste de travail
set work_dir=.

rem Le chemin de deployement final
set webapps=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps

rem Le dossier de configuration du web xml
set web_xml=%work_dir%\web.xml

rem Le dossier temp dans le dossier de travail
@REM set temp=%work_dir%\temp
set temp=%work_dir%\temp

rem Le dossier de librairies d'independances
set lib=%work_dir%\lib

rem Le dossier des fichiers sources java
set src=%work_dir%\src

rem Le dossier web inf de temp
set web_inf=%temp%\WEB-INF

rem Le dossier views de temp
set view=%temp%\views

rem Le contenue du dossier views 
set test_jsp=%work_dir%\views\*


@rem Tester si le dossier temp existe deja
    if exist "%temp%" (
        :: Supprimer le dossier temp si il existe deja
        rmdir "%temp%" /s
    )
    :: Recree le dossier temp
    mkdir "%temp%"

@rem Creation de la structure de deployement
    :: Creation du dossier views
    mkdir "%view%"
    :: Creation du dossier WebInf
    mkdir "%web_inf%"
    :: Creation de web inf lib
    set  web_inf_lib=%web_inf%\lib
    mkdir "%web_inf_lib%"
    :: Creation de web inf classes
    set  web_inf_cls=%web_inf%\classes
    mkdir "%web_inf_cls%"

@REM Compilation des fichiers source Java
    dir "%src%" /s *.java /b /a:a > src.txt

    for /f "tokens=*" %%a in ('type "src.txt"') do (
        echo [Compilation] "%%a"
        javac -d "%web_inf_cls%" "%%a" -cp "%lib%\*";"%src%"
    )
    del src.txt > NUL

@rem Copie des fichiers avan deployement
    :: Copier le fichier jsp
    xcopy "%test_jsp%" "%temp%\views"
    :: Copier le fichier web xml
    xcopy "%web_xml%" "%web_inf%"
    :: Transfert des librairies
    xcopy "%lib%" "%web_inf%\lib" /s /e /h

