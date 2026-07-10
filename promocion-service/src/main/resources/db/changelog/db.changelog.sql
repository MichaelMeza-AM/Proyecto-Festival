--liquibase formatted sql

--changeset jenni:1
CREATE TABLE promociones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    porcentaje_descuento INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    activo BOOLEAN NOT NULL
);

--changeset jenni:2
INSERT INTO promociones (codigo, porcentaje_descuento, fecha_inicio, fecha_fin, activo) VALUES
('FESTIVAL2026', 20, '2026-01-01 00:00:00', '2026-12-31 23:59:59', true),
('VERANO26', 15, '2026-01-01 00:00:00', '2026-03-21 23:59:59', true),
('EXPIRADO50', 50, '2025-01-01 00:00:00', '2025-12-31 23:59:59', true),
('PAUSADO10', 10, '2026-01-01 00:00:00', '2026-12-31 23:59:59', false);