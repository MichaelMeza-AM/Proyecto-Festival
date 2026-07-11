-- liquibase formatted sql

-- changeset Michael:1
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    compra_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    escenario_id BIGINT NOT NULL,
    fecha_asistencia DATETIME NOT NULL
);