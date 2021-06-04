CREATE DATABASE hubertus
    CHARACTER SET = 'utf8'
    COLLATE = 'utf8_polish_ci';

USE hubertus;

CREATE TABLE users (
    users_id int NOT NULL primary key AUTO_INCREMENT,
    users_name varchar(20) NOT NULL,
    users_password varchar(100) NOT NULL,
    users_phone varchar(15) NULL,
    users_email varchar(30) NULL,
    is_locked tinyint(1) DEFAULT false,
    KEY (users_name)
);

CREATE TABLE roles (
    roles_id int NOT NULL primary key AUTO_INCREMENT,
    roles_name varchar(20) NOT NULL,
    roles_description varchar(100) NULL
);

CREATE TABLE users_roles (
    users_id int NOT NULL,
    roles_id int NOT NULL,
    KEY (users_id),
    KEY (roles_id),
    CONSTRAINT users_roles_users_id_to_users FOREIGN KEY (users_id) REFERENCES users(users_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT users_roles_roles_id_to_roles FOREIGN KEY (roles_id) REFERENCES roles(roles_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE authorities (
    authorities_name varchar(30) NOT NULL,
    roles_id int NOT NULL,
    KEY (roles_id),
    CONSTRAINT authorities_roles_id_to_roles FOREIGN KEY (roles_id) REFERENCES roles(roles_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);
