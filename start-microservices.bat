@echo off
echo ========================================
echo  INICIANDO MICROSERVICIOS - ORDEN CORRECTO
echo ========================================

echo.
echo [1/4] Iniciando Discovery Server (Eureka) - Puerto 8761...
echo ========================================
start cmd /k "cd /d microservices\discovery-server && title Discovery-Server-8761 && mvn spring-boot:run"

echo Esperando 30 segundos para que Discovery Server se inicie completamente...
timeout /t 30 /nobreak

echo.
echo [2/4] Iniciando Auth Service - Puerto 8081...
echo ========================================
start cmd /k "cd /d microservices\auth-service && title Auth-Service-8081 && mvn spring-boot:run"

echo Esperando 20 segundos para que Auth Service se registre...
timeout /t 20 /nobreak

echo.
echo [3/4] Iniciando Product Service - Puerto 8082...
echo ========================================
start cmd /k "cd /d microservices\product-service && title Product-Service-8082 && mvn spring-boot:run"

echo Esperando 20 segundos para que Product Service se registre...
timeout /t 20 /nobreak

echo.
echo [4/4] Iniciando API Gateway - Puerto 8080...
echo ========================================
start cmd /k "cd /d microservices\api-gateway && title API-Gateway-8080 && mvn spring-boot:run"

echo.
echo ========================================
echo  TODOS LOS MICROSERVICIOS INICIADOS
echo ========================================
echo.
echo URLs de verificacion:
echo  - Eureka Dashboard: http://localhost:8761
echo  - API Gateway:      http://localhost:8080
echo  - Auth Service:     http://localhost:8081
echo  - Product Service:  http://localhost:8082
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause