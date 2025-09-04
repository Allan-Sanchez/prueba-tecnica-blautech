@echo off
echo ========================================
echo  INICIANDO MICROSERVICIOS CON MAVEN
echo ========================================

echo.
echo Verificando si Maven esta instalado...
mvn -version
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Maven no esta instalado o no esta en el PATH
    echo.
    echo SOLUCION:
    echo 1. Descarga Maven desde: https://maven.apache.org/download.cgi
    echo 2. Extrae el archivo en C:\Program Files\
    echo 3. Agrega C:\Program Files\apache-maven-X.X.X\bin al PATH
    echo 4. Reinicia la terminal y ejecuta este script de nuevo
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ Maven encontrado. Compilando microservicios...
echo ========================================

echo.
echo [Compilando] Parent POM...
cd microservices
mvn clean install -N
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando parent POM
    pause
    exit /b 1
)

echo.
echo [Compilando] Discovery Server...
cd discovery-server
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando Discovery Server
    pause
    exit /b 1
)
cd ..

echo.
echo [Compilando] Auth Service...
cd auth-service
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando Auth Service
    pause
    exit /b 1
)
cd ..

echo.
echo [Compilando] Product Service...
cd product-service
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando Product Service
    pause
    exit /b 1
)
cd ..

echo.
echo [Compilando] API Gateway...
cd api-gateway
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando API Gateway
    pause
    exit /b 1
)
cd ..

echo.
echo ✅ Compilacion exitosa. Iniciando servicios...
echo ========================================

cd ..

echo.
echo [1/4] Iniciando Discovery Server (Eureka) - Puerto 8761...
start cmd /k "cd /d microservices\discovery-server && title Discovery-Server-8761 && mvn spring-boot:run"

echo Esperando 30 segundos...
timeout /t 30 /nobreak

echo.
echo [2/4] Iniciando Auth Service - Puerto 8081...
start cmd /k "cd /d microservices\auth-service && title Auth-Service-8081 && mvn spring-boot:run"

echo Esperando 20 segundos...
timeout /t 20 /nobreak

echo.
echo [3/4] Iniciando Product Service - Puerto 8082...
start cmd /k "cd /d microservices\product-service && title Product-Service-8082 && mvn spring-boot:run"

echo Esperando 20 segundos...
timeout /t 20 /nobreak

echo.
echo [4/4] Iniciando API Gateway - Puerto 8080...
start cmd /k "cd /d microservices\api-gateway && title API-Gateway-8080 && mvn spring-boot:run"

echo.
echo ========================================
echo  ✅ TODOS LOS MICROSERVICIOS INICIADOS
echo ========================================
echo.
echo URLs de verificacion:
echo  - Eureka Dashboard: http://localhost:8761
echo  - API Gateway:      http://localhost:8080
echo  - Frontend:         http://localhost:3000
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause