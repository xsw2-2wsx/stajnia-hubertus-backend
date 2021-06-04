CREATE DATABASE hubertus
    CHARACTER SET = 'utf8'
    COLLATE = 'utf8_polish_ci';

USE hubertus;

CREATE TABLE users (
    users_id int NOT NULL primary key AUTO_INCREMENT,
    users_name varchar(20) NOT NULL,
    users_phone varchar(15) NULL,
    users_email varchar(30) NULL
);

CREATE TABLE roles (
    roles_id int NOT NULL primary key AUTO_INCREMENT,
    roles_name varchar(20) NOT NULL,
    roles_description varchar(100) NULL
);

CREATE TABLE authorities (
    authorities_name varchar(30) NOT NULL,
    roles_id int NOT NULL,
    KEY (roles_id),
    CONSTRAINT authorities_roles_id_to_roles FOREIGN KEY (roles_id) REFERENCES roles(roles_id) ON UPDATE CASCADE ON DELETE CASCADE
)
