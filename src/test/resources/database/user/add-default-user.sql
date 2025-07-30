INSERT INTO roles (id, name)
VALUES (1, 'USER');

INSERT INTO users (id, email, password, first_name, last_name, shipping_address)
VALUES (1, 'test@gmail.com', '$2a$10$0VxP2gWJ5cE1XeJmeQoI7ON7rXBBs7oD0s/vR9ZgIFZ1GbZ/P5K7e', 'Test', 'Test', 'Test');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);
