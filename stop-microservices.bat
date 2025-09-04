@echo off
echo ========================================
echo  DETENIENDO TODOS LOS MICROSERVICIOS
echo ========================================

echo.
echo Buscando y cerrando procesos Java (Spring Boot)...

:: Cerrar procesos por puerto
echo Cerrando proceso en puerto 8761 (Discovery Server)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8761') do taskkill /PID %%a /F 2>nul

echo Cerrando proceso en puerto 8080 (API Gateway)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F 2>nul

echo Cerrando proceso en puerto 8081 (Auth Service)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F 2>nul

echo Cerrando proceso en puerto 8082 (Product Service)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8082') do taskkill /PID %%a /F 2>nul

:: Cerrar ventanas de comandos con títulos específicos
echo Cerrando ventanas de microservicios...
taskkill /FI "WINDOWTITLE eq Discovery-Server-8761*" /F 2>nul
taskkill /FI "WINDOWTITLE eq Auth-Service-8081*" /F 2>nul
taskkill /FI "WINDOWTITLE eq Product-Service-8082*" /F 2>nul
taskkill /FI "WINDOWTITLE eq API-Gateway-8080*" /F 2>nul

echo.
echo ========================================
echo  TODOS LOS MICROSERVICIOS DETENIDOS
echo ========================================
echo.
pause