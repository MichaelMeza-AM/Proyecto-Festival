--liquibase formatted sql

--changeset michael:1
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

--changeset michael:2
INSERT INTO users (email, password, role) VALUES ('admin@festival.com', 'f865b53623b121fd34ee5426c792e5c33af8c227', 'ROLE_ADMIN');
INSERT INTO users (email, password, role) VALUES ('fan@gmail.com', '5b8269b9ba9746dadc7d6e9700b4205969dff280', 'ROLE_USER');