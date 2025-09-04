@echo off
echo ========================================
echo  INICIO RAPIDO - DESARROLLO
echo ========================================
echo.
echo Iniciando todos los microservicios en paralelo...
echo (Menos estable pero mas rapido para desarrollo)
echo.

start cmd /k "cd /d microservices\discovery-server && title Discovery-Server-8761 && mvn spring-boot:run"
start cmd /k "cd /d microservices\auth-service && title Auth-Service-8081 && mvn spring-boot:run"
start cmd /k "cd /d microservices\product-service && title Product-Service-8082 && mvn spring-boot:run"
start cmd /k "cd /d microservices\api-gateway && title API-Gateway-8080 && mvn spring-boot:run"

echo.
echo ========================================
echo  MICROSERVICIOS INICIANDOSE...
echo ========================================
echo.
echo Espera 2-3 minutos para que todos se inicien completamente
echo.
echo URLs:
echo  - Eureka: http://localhost:8761
echo  - API:    http://localhost:8080
echo.
pause