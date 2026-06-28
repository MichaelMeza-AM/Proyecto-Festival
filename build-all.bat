@echo off
rem Limpieza de contenedores e imágenes previas
FOR /f %%i IN ('docker ps -aq') DO docker rm -f %%i
FOR /f %%i IN ('docker images -aq') DO docker rmi -f %%i

echo Compilando API Gateway...
cd api-gateway
call .\mvnw clean package -DskipTests

echo Compilando Auth Service...
cd ../auth-service
call .\mvnw clean package -DskipTests

echo Compilando Usuario Service...
cd ../usuario-service
call .\mvnw clean package -DskipTests

echo Compilando Artista Service...
cd ../artista-service
call .\mvnw clean package -DskipTests

echo Compilando Escenario Service...
cd ../escenario-service
call .\mvnw clean package -DskipTests

echo Compilando Presentacion Service...
cd ../presentacion-service
call .\mvnw clean package -DskipTests

echo Compilando Itinerario Service...
cd ../itinerario-service
call .\mvnw clean package -DskipTests

echo Compilando Compra Service...
cd ../compra-service
call .\mvnw clean package -DskipTests

echo Compilando Pago Service...
cd ../pago-service
call .\mvnw clean package -DskipTests

cd ..
echo ¡Todos los proyectos han sido compilados con éxito!
pause