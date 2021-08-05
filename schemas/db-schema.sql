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
    is_locked tinyint(1) NOT NULL DEFAULT false,
    profile_pic_path varchar(100) NULL,
    UNIQUE KEY (users_name)
);

CREATE TABLE roles (
    roles_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE activities(
    activities_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    activities_name varchar(30) NOT NULL,
    activities_description varchar(100) NULL,
    activities_points decimal NOT NULL,
    UNIQUE KEY (activities_name)
);

CREATE TABLE activity_constraints(
    activities_id int NOT NULL,
    activity_constraints_time_start time NOT NULL,
    activity_constraints_time_end time NOT NULL,
    KEY (activities_id),
    CONSTRAINT activity_constraints_activities_id_to_activities FOREIGN KEY (activities_id) REFERENCES activities(activities_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE configuration(
    configuration_id int NOT NULL PRIMARY KEY auto_increment,
    configuration_key varchar(150) NOT NULL,
    configuration_value varchar(100) NOT NULL,
    configuration_group varchar(100) NOT NULL,
    KEY (configuration_key),
    KEY (configuration_group)
);

CREATE TABLE bookings(
    bookings_id int NOT NULL PRIMARY KEY auto_increment,
    creation_time timestamp NOT NULL default CURRENT_TIMESTAMP,
    start_time datetime NOT NULL,
    end_time datetime NOT NULL,
    activities_id int NOT NULL,
    subject varchar(15) NOT NULL,
    users_id int NOT NULL,
    KEY (start_time),
    KEY (end_time),
    KEY (activities_id),
    KEY (users_id),
    CONSTRAINT activities_id_to_activities FOREIGN KEY (activities_id) REFERENCES activities(activities_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT users_id_to_users FOREIGN KEY (users_id) REFERENCES users(users_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);