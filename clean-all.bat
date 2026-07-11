@echo off
cd api-gateway
call .\mvnw clean

cd ../auth-service
call .\mvnw clean

cd ../usuario-service
call .\mvnw clean

cd ../artista-service
call .\mvnw clean

cd ../escenario-service
call .\mvnw clean

cd ../presentacion-service
call .\mvnw clean

cd ../itinerario-service
call .\mvnw clean

cd ../compra-service
call .\mvnw clean

cd ../pago-service
call .\mvnw clean

cd ../promocion-service
call .\mvnw clean

cd ../ticket-service
call .\mvnw clean

cd ..
echo Limpieza completa finalizada.
pause