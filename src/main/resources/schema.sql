-- -------------------------------------------------------
-- Table: estados_tarea (task status catalog)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS estados_tarea (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- -------------------------------------------------------
-- Table: usuarios (application users)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- -------------------------------------------------------
-- Table: tareas (tasks)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS tareas (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    description VARCHAR(1000),
    status_id   BIGINT        NOT NULL,
    user_id     BIGINT        NOT NULL,
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP     NOT NULL,
    CONSTRAINT fk_tareas_status FOREIGN KEY (status_id) REFERENCES estados_tarea(id),
    CONSTRAINT fk_tareas_user   FOREIGN KEY (user_id)   REFERENCES usuarios(id)
);