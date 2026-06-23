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
(1, 1, '2026-03-20 21:30:00', 90), -- Artista 1 en Escenario 1
(2, 2, '2026-03-20 19:00:00', 60), -- Artista 2 en Escenario 2
(3, 1, '2026-03-21 22:00:00', 120), -- Artista 3 en Escenario 1
(4, 1, '2026-03-20 18:00:00', 90),  -- Soda Stereo
(5, 2, '2026-03-20 23:00:00', 120), -- Bad Bunny
(6, 1, '2026-03-21 17:00:00', 90),  -- Taylor Swift
(7, 2, '2026-03-21 21:00:00', 120), -- Metallica
(8, 1, '2026-03-22 16:00:00', 60),  -- Gorillaz
(9, 2, '2026-03-22 19:30:00', 90),  -- Karol G
(10, 3, '2026-03-22 22:00:00', 120),-- Calvin Harris
(1, 1, '2026-03-21 15:00:00', 60),  -- Los Prisioneros (Segundo show)
(2, 2, '2026-03-20 16:00:00', 45),  -- Dua Lipa (Show apertura)
(3, 3, '2026-03-22 23:30:00', 90);  -- Daft Punk (Cierre)