create database truello;

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status project_status NOT NULL DEFAULT 'ACTIVO', -- ACTIVO | PAUSADO | FINALIZADO | CANCELADO
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status task_status NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE | EN_PROGRESO | COMPLETADA | ATRASADA | CANCELADA
    priority task_priority NOT NULL DEFAULT 'MEDIA', -- BAJA | MEDIA | ALTA | URGENTE
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    project_id BIGINT REFERENCES projects(id) ON DELETE SET NULL
);

CREATE TABLE labels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(7)
);

CREATE TABLE task_label (
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    label_id BIGINT NOT NULL REFERENCES labels(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, label_id)
);

CREATE TABLE reminders (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMP NOT NULL
);

CREATE TABLE reminder_targets (
    id BIGSERIAL PRIMARY KEY,
    reminder_id BIGINT NOT NULL REFERENCES reminders(id) ON DELETE CASCADE,
    channel VARCHAR(30) NOT NULL,   -- id del canal, ej. whatsapp | email
    address VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP
);

CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
