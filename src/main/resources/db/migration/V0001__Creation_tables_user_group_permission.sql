-- noinspection SqlDialectInspectionForFile

-- noinspection SqlNoDataSourceInspectionForFile

-- -----------------------------------------------------
-- Table user
-- -----------------------------------------------------
CREATE TABLE  user (
  id SERIAL NOT NULL,
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  enable BOOLEAN NOT NULL,
  CONSTRAINT pk_user_id PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table group
-- -----------------------------------------------------
CREATE TABLE group (
  id SERIAL NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  CONSTRAINT pk_group_id PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table permission
-- -----------------------------------------------------
CREATE TABLE permission (
  id SERIAL NOT NULL,
  role TEXT NOT NULL,
  description TEXT ,
  CONSTRAINT pk_group_id PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table user_group
-- -----------------------------------------------------
CREATE TABLE  user_group (
  user_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  CONSTRAINT fk_user_group FOREIGN KEY (user_id) REFERENCES user,
  CONSTRAINT fk_group_user FOREIGN KEY (group_id) REFERENCES group,
  CONSTRAINT pk_user_group_id PRIMARY KEY (user_id, group_id)
);

-- -----------------------------------------------------
-- Table group_permission
-- -----------------------------------------------------
CREATE TABLE group_permission (
  group_id BIGINT  NOT NULL,
  permission_id BIGINT NOT NULL,
  CONSTRAINT fk_group_permission FOREIGN KEY (group_id) REFERENCES group,
  CONSTRAINT fk_permission_group FOREIGN KEY (permission_id) REFERENCES permission,
  CONSTRAINT pk_group_permission FOREIGN KEY (group_id,permission_id)
);
                                                                         -- 1234
INSERT INTO user VALUES(1, 'magaiwer', 'magaiwer@hotmail.com.br', '$2a$10$IVBSSesFznDKYkTiUVjQ9esw1NY0l.JTghlHSP0jrR4B9I8yBGVya', true);
INSERT INTO groups VALUES(1, 'Administrador', 'Usuários que possuem permissão de administrador do sistema');
INSERT INTO permission VALUES(1, 'ROLE_ADMIN', 'Administrador dos sistema',);
INSERT INTO user_group VALUES(1, 1);
INSERT INTO group_permission VALUES(1, 1);