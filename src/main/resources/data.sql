CREATE TABLE IF NOT EXISTS task_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES task_category(id)
);

INSERT INTO task_category (name) VALUES ('Pc de escritorio');
INSERT INTO task_category (name) VALUES ('Notebook');
INSERT INTO task_category (name) VALUES ('Celulares');
INSERT INTO task_category (name) VALUES ('Atencion al cliente');

SET @pc_id = (SELECT id FROM task_category WHERE name = 'Pc de escritorio');
SET @notebook_id = (SELECT id FROM task_category WHERE name = 'Notebook');
SET @celular_id = (SELECT id FROM task_category WHERE name = 'Celulares');
SET @atencion_id = (SELECT id FROM task_category WHERE name = 'Atencion al cliente');

INSERT INTO task (description, category_id) VALUES ('Formateo e instalacion de SO', @pc_id);
INSERT INTO task (description, category_id) VALUES ('Limpieza Fisica', @pc_id);
INSERT INTO task (description, category_id) VALUES ('Desinfeccion de virus', @pc_id);
INSERT INTO task (description, category_id) VALUES ('Diagnosticar', @pc_id);
INSERT INTO task (description, category_id) VALUES ('Cambio de partes', @pc_id);

INSERT INTO task (description, category_id) VALUES ('Formateo e instalacion de SO', @notebook_id);
INSERT INTO task (description, category_id) VALUES ('Limpieza Fisica', @notebook_id);
INSERT INTO task (description, category_id) VALUES ('Desinfeccion de virus', @notebook_id);
INSERT INTO task (description, category_id) VALUES ('Diagnosticar', @notebook_id);
INSERT INTO task (description, category_id) VALUES ('Cambio de partes', @notebook_id);

INSERT INTO task (description, category_id) VALUES ('Flasheo', @celular_id);
INSERT INTO task (description, category_id) VALUES ('Cambio de bateria', @celular_id);
INSERT INTO task (description, category_id) VALUES ('Cambio de pantalla', @celular_id);
INSERT INTO task (description, category_id) VALUES ('Cambio de vidrio templado', @celular_id);

INSERT INTO task (description, category_id) VALUES ('Recibir nuevos clientes', @atencion_id);
INSERT INTO task (description, category_id) VALUES ('Presupuestar equipo', @atencion_id);
INSERT INTO task (description, category_id) VALUES ('Venta de equipos', @atencion_id);
INSERT INTO task (description, category_id) VALUES ('Compra a proveedores', @atencion_id);
