--liquibase formatted sql

--changeset jenni:1
CREATE TABLE zona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255)
);

--changeset jenni:2
CREATE TABLE escenario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    puerta_acceso VARCHAR(255) NOT NULL,
    aforo_maximo INT NOT NULL,
    precio INT NOT NULL,
    zona_id BIGINT NOT NULL,
    CONSTRAINT fk_zona FOREIGN KEY (zona_id) REFERENCES zona(id)
);

--changeset michael:3
INSERT INTO zona (nombre, descripcion) VALUES
('Explanada Central', 'Ubicación principal en el centro del parque'),
('Parque Norte', 'Sector rodeado de árboles en la zona norte'),
('Sector Oriente', 'Área pavimentada cerca del acceso principal');

--changeset michael:4
INSERT INTO escenario (nombre, puerta_acceso, aforo_maximo, precio, zona_id) VALUES
('Main Stage - Entrada General', 'Puerta Central Amplia', 40000, 65000, 1),
('Main Stage - VIP Lounge', 'Acceso Exclusivo Norte', 3000, 150000, 1),
('Alternative Stage - Entrada General', 'Puerta Bosque', 20000, 45000, 2),
('Alternative Stage - VIP Deck', 'Pasarela VIP', 1500, 100000, 2),
('Electronic Tent - Pista General', 'Entrada Carpa', 8000, 30000, 3);