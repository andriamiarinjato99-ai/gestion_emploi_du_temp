@echo off
cd /d "%~dp0"

set "LOCAL_MVN=%~dp0tools\apache-maven-3.9.9\bin\mvn.cmd"
where mvn >nul 2>&1
if %ERRORLEVEL%==0 (
  mvn clean javafx:run
) else if exist "%LOCAL_MVN%" (
  "%LOCAL_MVN%" clean javafx:run
) else (
  echo Maven introuvable. Verifiez le dossier maven\apache-maven-3.9.9
  pause
)

