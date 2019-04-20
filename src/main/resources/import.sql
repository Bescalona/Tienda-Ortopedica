/*Creamos algunos usuarios con sus roles*/
INSERT INTO `users` (username, password, enabled) VALUES ('bastian','$2a$10$6QiMrpqjwky3R9gxEX7RWey2dYmn6V0.RfA8i/c5Gy5QsP4hONJAu',1);
INSERT INTO `users` (username, password, enabled) VALUES ('admin','$2a$10$RmgAUhQkQ75mPZOvxrkit.Yh/d2gBCKOrvm3JmZcRxAvghJRVuqv2',1);

INSERT INTO `authorities` (user_id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO `authorities` (user_id, authority) VALUES (2, 'ROLE_USER');
INSERT INTO `authorities` (user_id, authority) VALUES (2, 'ROLE_ADMIN');

/*Populate tabla categorias */
INSERT INTO categorias (nombre) VALUES ('Sin categoria asignada');
INSERT INTO categorias (nombre) VALUES ('Brazo y Mano');
INSERT INTO categorias (nombre) VALUES ('Fajas');
INSERT INTO categorias (nombre) VALUES ('Cuellos');
INSERT INTO categorias (nombre) VALUES ('Pierna y Pie');

/* Populate tabla productos */
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(2,'asdasd', 'Muñequera abierta simple', 12000, 6, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(2,'asdasd', 'Muñequera tubular con banda', 16000, 8, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(2,'asdasd', 'Muñequera neopreno bumerán', 19000, 11, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(3,'asdasd', 'Faja abdominal 3 bandas', 20000, 4, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(3,'asdasd', 'Faja abdominal 4 bandas', 25000, 29, '',0);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(3,'asdasd', 'Faja toraxica 2 bandas', 8000, 11, '',2);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(4,'asdasd', 'Cuello blanco infantil', 7000, 19, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(4,'asdasd', 'Cuello Filadelfia', 15000, 19, '',1);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(4,'asdasd', 'Cuello Miami', 18000, 20, '',0);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(5,'asdasd', 'Tobillera simple de neopreno', 5000, 39, '',0);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(5,'asdasd', 'Bota pediátrica', 30000, 4, '',0);
INSERT INTO productos (categoria_id, descripcion, nombre, precio, stock, foto, referencias) VALUES(5,'asdasd', 'Zapato Post Operatorio', 10000, 30, '',0);

/* Creamos algunas facturas */
INSERT INTO facturas (nombre, observacion, create_at) VALUES('Factura hospital de Talca', '', NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(5, 1, 1);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(8, 1, 2);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(3, 1, 6);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(2, 1, 4);

INSERT INTO facturas (nombre, observacion, create_at) VALUES('Factura Cesfam los volcanes', '', NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(5, 2, 6);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(5, 2, 3);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(3, 2, 7);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(5, 2, 8);