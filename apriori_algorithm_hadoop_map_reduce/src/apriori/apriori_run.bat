@echo off
echo "Starting batch script..."
setlocal enabledelayedexpansion

:: Initialize counter
set /a K=1

:: Define parameters
set JAR_PATH=..\AprioriDriver.jar
set CLASS_NAME=apriori.AprioriDriver
set INPUT_PATH=\user\MahaaGURU\input
set OUTPUT_PATH=\user\MahaaGURU\output!K!
:: I have 1 lakh records in the dataset so, I am using 40% support
set SUPPORT_COUNT=20000

cd "C:\Users\MahaaGURU\Downloads\Hadoop\hadoop-3.3.6"

:: Start loop
:loop_apriori_start

:: Execute Hadoop job
echo "iteration !K!"
cmd /C hadoop jar %JAR_PATH% %CLASS_NAME% !K! %SUPPORT_COUNT% %INPUT_PATH% %OUTPUT_PATH%
:: Check if the job was successful
if %ERRORLEVEL% equ 0 (
    set /a K+=1
    set OUTPUT_PATH=\user\MahaaGURU\output!K!
    goto loop_apriori_start
) else (
    echo "Apriori code is executed with exit code %ERRORLEVEL%. Error Level 1 indicates completion of apriori algothm 2 indicated problem with apriori algorithm running.."
    cmd /C hadoop fs -rm -r /user/MahaaGURU/output!K!
    goto loop_apriori_end
)

:: End loop
:loop_apriori_end
pause
exit /b %ERRORLEVEL%
