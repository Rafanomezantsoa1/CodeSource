@echo off
:: Définir les variables de répertoire
set SRC_DIR=D:\S5\MrNaina\sprint1\Test
set LIB_DIR=%SRC_DIR%\lib
set WEB_DIR=%SRC_DIR%\webapp
set CLASSES_DIR=%WEB_DIR%\WEB-INF\classes
set BUILD_DIR=%SRC_DIR%\build
set WAR_FILE=%SRC_DIR%\sprint1.war
set TOMCAT_WEBAPPS_DIR=C:\apache-tomcat-10.1.34\webapps

:: Créer le répertoire BUILD si nécessaire
if not exist "%BUILD_DIR%" (
    mkdir "%BUILD_DIR%"
)

:: Etape 1 : Compiler le fichier Java
echo Compiling FrontServlet.java...
javac -cp "%LIB_DIR%\servlet-api.jar;%LIB_DIR%\Framework.jar" -d "%CLASSES_DIR%" "%SRC_DIR%\src\main\java\Test\TestServlet.java"


if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

:: Etape 2 : Copier les classes dans le répertoire de build
echo Copying classes to build directory...
xcopy /E /I "%WEB_DIR%" "%BUILD_DIR%"
if %ERRORLEVEL% neq 0 (
    echo Copying classes failed!
    exit /b %ERRORLEVEL%
)

:: Etape 3 : Créer le fichier .war
echo Creating the .war file...
cd "%BUILD_DIR%"
jar -cvf "%WAR_FILE%" *
if %ERRORLEVEL% neq 0 (
    echo Failed to create the .war file!
    exit /b %ERRORLEVEL%
)

:: Etape 4 : Déployer le fichier .war dans Tomcat
echo Deploying the .war file to Tomcat...
copy "%WAR_FILE%" "%TOMCAT_WEBAPPS_DIR%"
if %ERRORLEVEL% neq 0 (
    echo Deployment failed!
    exit /b %ERRORLEVEL%
)

:: Fin
echo Deployment successful!
pause
