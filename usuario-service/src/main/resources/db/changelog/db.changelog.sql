--liquibase formatted sql

--changeset michael:1
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    rut VARCHAR(20) UNIQUE,
    fecha_nacimiento DATE
);

--changeset michael:2
-- Insertamos un admin (ID 1) y un fan (ID 2) asignando el ID manualmente
INSERT INTO usuario (id, nombre, email, rut, fecha_nacimiento) VALUES
(1, 'Productor Admin', 'admin@festival.com', NULL, '1980-05-15'),
(2, 'Fan Asistente', 'fan@gmail.com', '19876543-2', '2002-10-20');