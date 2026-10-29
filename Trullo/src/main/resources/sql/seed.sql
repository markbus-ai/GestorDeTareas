-- Seed data para Trullo
-- Ejecutar después de enums.sql + schema.sql
-- Usa fechas relativas a CURRENT_DATE para poder probar findOverdue y findDueSoon

-- Limpieza (orden por FKs)
TRUNCATE TABLE task_label, reminders, notes, tasks, labels, projects RESTART IDENTITY CASCADE;

-- Proyectos
INSERT INTO projects (name, description, status, due_date) VALUES
    ('App de tareas', 'Proyecto principal del gestor', 'ACTIVO', CURRENT_DATE + 30),
    ('Renovar suscripciones', 'Pagos y facturación', 'PAUSADO', CURRENT_DATE + 60),
    ('Mudanza', 'Trámites y organizing', 'CANCELADO', NULL);

-- Labels
INSERT INTO labels (name, color) VALUES
    ('trabajo', '#3B82F6'),
    ('personal', '#22C55E'),
    ('urgente', '#EF4444');

-- Tareas (vencidas, por vencer, completadas, sin fecha)
INSERT INTO tasks (title, description, status, priority, due_date, project_id) VALUES
    ('Pagar impuestos',        'Vencimiento trimestral',          'ATRASADA',    'URGENTE', CURRENT_DATE - 5,  2),
    ('Responder emails viejos', NULL,                             'PENDIENTE',   'MEDIA',   CURRENT_DATE - 1,  1),
    ('Terminar informe',       'Informe semanal para el equipo',  'EN_PROGRESO', 'ALTA',    CURRENT_DATE + 1,  1),
    ('Comprar pasajes',        NULL,                              'PENDIENTE',   'ALTA',    CURRENT_DATE + 3,  3),
    ('Renovar dominio',        'Vence en pocos días',             'PENDIENTE',   'MEDIA',   CURRENT_DATE + 7,  2),
    ('Actualizar dependencias','Tarea sin urgencia',              'PENDIENTE',   'BAJA',    CURRENT_DATE + 45, 1),
    ('Diseñar logo',           NULL,                              'COMPLETADA',  'MEDIA',   CURRENT_DATE - 10, 1),
    ('Configurar CI',          'Pipeline listo',                  'COMPLETADA',  'ALTA',    CURRENT_DATE - 3,  1),
    ('Idea sin fecha',         'Todavía no tiene due_date',       'PENDIENTE',   'BAJA',    NULL,               NULL);

-- Asociaciones tarea <-> label
INSERT INTO task_label (task_id, label_id)
SELECT t.id, l.id
FROM tasks t
JOIN labels l ON l.name = CASE
    WHEN t.title IN ('Pagar impuestos', 'Renovar dominio') THEN 'urgente'
    WHEN t.title IN ('Terminar informe', 'Diseñar logo', 'Configurar CI') THEN 'trabajo'
    ELSE 'personal'
END;

-- Reminders (1 pendiente vencido, 1 pendiente futuro, 1 ya enviado)
INSERT INTO reminders (task_id, scheduled_at, channel, target_phone, target_email, sent)
SELECT t.id, (CURRENT_DATE + f.d)::timestamp, f.ch, f.ph, f.em, f.s
FROM tasks t
JOIN (VALUES
    ('Pagar impuestos',   -1, 'EMAIL',     NULL,          'user@ejemplo.com', false),
    ('Terminar informe',   1, 'WHATSAPP',  '+5491100000', NULL,               false),
    ('Configurar CI',     -2, 'AMBOS',     '+5491100000', 'user@ejemplo.com', true)
) AS f(title, d, ch, ph, em, s) ON t.title = f.title;

-- Notas
INSERT INTO notes (title, content) VALUES
    ('Ideas backlog', 'Agregar filtro por etiqueta en el panel de tareas.'),
    ('Config DB', 'Recordar rotar las credenciales del .env antes del deploy.');
