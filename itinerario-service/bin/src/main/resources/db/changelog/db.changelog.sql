--liquibase formatted sql

--changeset jenni:1
CREATE TABLE itinerario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    presentacion_id BIGINT NOT NULL,
    fecha_agregado DATETIME NOT NULL
);

--changeset jenni:2
-- El usuario 2 (Fan Asistente) quiere ir a ver la presentación 1 y la 3
INSERT INTO itinerario (usuario_id, presentacion_id, fecha_agregado) VALUES
(2, 1, '2026-03-01 10:00:00'),
(2, 3, '2026-03-01 10:05:00');