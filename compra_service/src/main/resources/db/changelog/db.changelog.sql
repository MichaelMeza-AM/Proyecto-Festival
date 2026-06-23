--liquibase formatted sql

--changeset michael:1
CREATE TABLE compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    escenario_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_asistencia DATETIME NOT NULL,
    fecha_registro DATETIME NOT NULL
);

--changeset michael:2
-- Inserción de prueba controlada: Usuario 2 reserva 2 pases para el Escenario 1
INSERT INTO compras (usuario_id, escenario_id, cantidad, fecha_asistencia, fecha_registro) VALUES
(2, 1, 2, '2026-11-20 18:00:00', '2026-06-13 23:45:00');