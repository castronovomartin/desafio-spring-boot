-- -------------------------------------------------------
-- Task status catalog (fixed values, do not modify)
-- -------------------------------------------------------
INSERT INTO estados_tarea (name) VALUES
                                     ('PENDING'),
                                     ('IN_PROGRESS'),
                                     ('COMPLETED');

-- -------------------------------------------------------
-- Preloaded users (password: 'password' for all)
-- -------------------------------------------------------
INSERT INTO usuarios (username, password, full_name) VALUES
                                                         ('admin', '$2a$10$82SqzhDAhWKsm9Q0KpxezeCPDt1vZVF647RkiO29hfA2r8oyC5GSS', 'Administrator'),
                                                         ('user1', '$2a$10$82SqzhDAhWKsm9Q0KpxezeCPDt1vZVF647RkiO29hfA2r8oyC5GSS', 'User One'),
                                                         ('user2', '$2a$10$82SqzhDAhWKsm9Q0KpxezeCPDt1vZVF647RkiO29hfA2r8oyC5GSS', 'User Two');

-- -------------------------------------------------------
-- Sample tasks (requires estados_tarea and usuarios above)
-- -------------------------------------------------------
INSERT INTO tareas (title, description, status_id, user_id, created_at, updated_at) VALUES
                                                                                        ('Setup project infrastructure',  'Initialize repository, configure CI/CD pipeline',     1, 1, NOW(), NOW()),
                                                                                        ('Design database schema',        'Define all entities and relationships for the system', 2, 1, NOW(), NOW()),
                                                                                        ('Implement authentication',      'JWT login endpoint and security filter chain',         3, 2, NOW(), NOW());