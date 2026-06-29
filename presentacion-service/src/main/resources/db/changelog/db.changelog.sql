--liquibase formatted sql

--changeset jenni:1
CREATE TABLE presentacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artista_id BIGINT NOT NULL,
    escenario_id BIGINT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    duracion_minutos INT NOT NULL
);

--changeset jenni:2
INSERT INTO presentacion (artista_id, escenario_id, fecha_hora, duracion_minutos) VALUES
(4, 1, '2026-11-20 17:00:00', 90),  
(4, 2, '2026-11-20 17:00:00', 90),  
(2, 4, '2026-11-20 20:00:00', 60),  
(1, 1, '2026-11-21 18:00:00', 90), 
(3, 5, '2026-11-21 22:00:00', 120); 