--liquibase formatted sql

--changeset jenni:1
CREATE TABLE itinerario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    presentacion_id BIGINT NOT NULL,
    fecha_agregado DATETIME NOT NULL
);

--changeset jenni:2
INSERT INTO itinerario (usuario_id, presentacion_id, fecha_agregado) VALUES
(2, 2, '2026-10-01 10:00:00'), -- Ve a Bad Bunny en VIP
(2, 3, '2026-10-01 10:05:00'), -- Ve a Dua Lipa en VIP
(2, 5, '2026-10-01 10:10:00'); -- Ve a Daft Punk en la carpa general el sábado