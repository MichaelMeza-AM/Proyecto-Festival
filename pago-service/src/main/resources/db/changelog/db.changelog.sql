--liquibase formatted sql
--changeset michael:1
CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_compra BIGINT NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    monto_subtotal INT NOT NULL,
    porcentaje_descuento INT NOT NULL,
    monto_descuento INT NOT NULL,
    monto_neto INT NOT NULL,
    iva INT NOT NULL,
    monto_total INT NOT NULL,
    medio_pago VARCHAR(50) NOT NULL,
    fecha_pago DATETIME NOT NULL
);