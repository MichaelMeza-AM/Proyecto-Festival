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
('Bad Bunny', 'Líder global del género urbano y reggaetón.', 'Urbano'),
('Metallica', 'Una de las bandas más influyentes del heavy metal.', 'Metal');