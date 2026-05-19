@echo off
REM Cleanup script for Windows - Remove AI-generated looking files before pushing to GitHub

echo Cleaning up repository for GitHub...
echo.

REM Remove Kiro AI files
echo Removing .kiro directory...
if exist .kiro rmdir /s /q .kiro

REM Remove internal documentation that looks AI-generated
echo Removing internal documentation files...
if exist GITHUB_PUSH_CHECKLIST.md del /f GITHUB_PUSH_CHECKLIST.md
if exist PROJECT_SUMMARY.md del /f PROJECT_SUMMARY.md
if exist CONTRIBUTING.md del /f CONTRIBUTING.md
if exist QUICK_START.md del /f QUICK_START.md
if exist WHAT_TO_PUSH.md del /f WHAT_TO_PUSH.md
if exist cleanup-for-github.bat del /f cleanup-for-github.bat
if exist cleanup-for-github.sh del /f cleanup-for-github.sh

REM Remove extra deployment guides
echo Removing extra deployment guides...
if exist AWS_DEPLOYMENT_GUIDE.md del /f AWS_DEPLOYMENT_GUIDE.md

REM Remove .env files
echo Checking for .env files...
for /r %%i in (.env) do del /f "%%i"
for /r %%i in (*.env) do del /f "%%i"

REM Remove DS_Store files
echo Removing .DS_Store files...
for /r %%i in (.DS_Store) do del /f "%%i"
for /r %%i in (DS_Store) do del /f "%%i"

REM Remove log files
echo Removing log files...
for /r %%i in (*.log) do del /f "%%i"

echo.
echo Cleanup complete!
echo.
echo Your repository now contains only:
echo    - Source code (backend and frontend)
echo    - README.md
echo    - DEPLOYMENT.md
echo    - RENDER_DEPLOYMENT.md
echo    - Docker files
echo    - .gitignore
echo.
echo Ready to push to GitHub!
echo.
echo Next steps:
echo 1. git init
echo 2. git add .
echo 3. git commit -m "Initial commit: Pipeline execution system"
echo 4. git remote add origin your-github-url
echo 5. git push -u origin main
echo.
pause
