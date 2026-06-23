--liquibase formatted sql

--changeset michael:1
CREATE TABLE artistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    biografia VARCHAR(600),
    genero_musical VARCHAR(255) NOT NULL
);

--changeset michael:2
INSERT INTO artistas (nombre, biografia, genero_musical) VALUES
('Los Prisioneros', 'Banda icónica de rock chileno.', 'Rock'),
('Dua Lipa', 'Estrella del pop internacional.', 'Pop'),
('Daft Punk', 'Dúo legendario de música electrónica.', 'Electrónica'),
('Soda Stereo', 'Banda de rock argentina liderada por Gustavo Cerati.', 'Rock'),
('Bad Bunny', 'Líder global del género urbano y reggaetón.', 'Urbano'),
('Taylor Swift', 'Cantautora estadounidense icono del pop contemporáneo.', 'Pop'),
('Metallica', 'Una de las bandas más influyentes del heavy metal.', 'Metal'),
('Gorillaz', 'Proyecto musical virtual liderado por Damon Albarn.', 'Indie'),
('Karol G', 'Referente colombiana de la música urbana y reggaetón.', 'Urbano'),
('Calvin Harris', 'Dj y productor escocés referente de la electrónica.', 'Electrónica');