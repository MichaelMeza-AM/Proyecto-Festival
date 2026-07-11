# Festival Music API - [GOODSTOCK]

## Contexto del Sistema
Este microservicio forma parte de una arquitectura distribuida diseñada para la gestión comercial y operativa de un festival de música. El ecosistema completo permite la administración del catálogo de artistas y escenarios, la programación de presentaciones, la creación de itinerarios y el procesamiento de transacciones financieras para la compra de entradas. Toda la persistencia de datos opera sobre bases de datos MySQL, garantizando la integridad de los registros bajo un modelo de seguridad centralizado con tokens JWT.

## Integrantes
* Michael Meza - Desarrollador Backend
* Jennifer Monchi - Desarrolladora Backend

---

## Arquitectura de Microservicios Implementados

El sistema se compone de los siguientes módulos independientes:

1. api-gateway: Enrutador principal y balanceador de peticiones.
2. auth-service: Emisión y validación de tokens de seguridad JWT.
3. usuario-service: Gestión de cuentas y perfiles de asistentes.
4. artista-service: Catálogo de músicos y bandas.
5. escenario-service: Administración de locaciones y precios base.
6. presentacion-service: Programación de horarios y line-up.
7. itinerario-service: Agenda personal de los asistentes.
8. compra-service: Lógica de reserva de entradas.
9. pago-service: Motor financiero, cálculo de impuestos y procesamiento final.
10. ticket-service: Generación y asignación de códigos únicos para las entradas definitivas.
11. promocion-service: Gestión de campañas, descuentos y validación de cupones.

Nota: Este repositorio contiene específicamente el código fuente de "Api-gateway" y su responsabilidad exclusiva es enrutar las peticiones entrantes, actuar como balanceador y aplicar la primera capa de seguridad mediante el filtro de tokens JWT antes de que la petición toque los servicios internos.

---

## Rutas del API Gateway
Todas las peticiones del cliente deben dirigirse al Gateway (Puerto 9090). Las rutas base son:
* Autenticación: http://localhost:9090/auth/**
* Usuarios: http://localhost:9090/usuarios/**
* Artistas: http://localhost:9090/artistas/**
* Escenarios: http://localhost:9090/escenarios/**
* Presentaciones: http://localhost:9090/presentaciones/**
* Itinerarios: http://localhost:9090/itinerarios/**
* Compras: http://localhost:9090/compras/**
* Pagos: http://localhost:9090/pagos/**
* Tickets: http://localhost:9090/tickets/**
* Promociones: http://localhost:9090/promociones/**

---

## Documentación API (Swagger)
Para visualizar la documentación de los endpoints de este microservicio específico de forma aislada:
* Interfaz de usuario: http://localhost:[PUERTO_DEL_MICROSERVICIO]/doc/swagger-ui.html
* Formato JSON: http://localhost:[PUERTO_DEL_MICROSERVICIO]/v3/api-docs

---

## Despliegue Local (Entorno de Desarrollo)

Requisitos: Java 21, Maven y servidor MySQL en ejecución.

1. Abrir la terminal en la raíz de este microservicio.
2. Limpiar y compilar el proyecto:
   mvnw.cmd clean compile
3. Iniciar la aplicación:
   mvnw.cmd spring-boot:run

---

## Despliegue Remoto (Producción con Docker)

El ecosistema está diseñado para ejecutarse en contenedores mediante Docker Compose.

1. Iniciar Docker Desktop y verificar que el motor esté en ejecución.
2. En la raíz del proyecto general, compilar los ejecutables utilizando el script provisto:
   ./build-all.bat
3. Levantar la infraestructura completa en segundo plano:
   docker-compose up 
4. Para detener la ejecución sin perder datos:
   docker-compose stop

---

## Variables de Entorno

Para la correcta conexión de los servicios, se requieren las siguientes variables (configuradas en el archivo application.properties o docker-compose.yml):

* SECRET: Clave alfanumérica para la firma de tokens JWT.
