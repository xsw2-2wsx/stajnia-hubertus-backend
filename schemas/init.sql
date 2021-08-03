insert into roles values
    (1, 'Administrator', 'Wbudowana rola do zarządzania systemem'),
    (2, 'Użytkownik', 'Uprawnienia dla standardowego użytkownika');

insert into authorities values
    ('MANAGE_ACTIVITIES', 1),
    ('USE_BOOKING', 1),
    ('MANAGE_BOOKINGS', 1),
    ('MANAGE_ROLES', 1),
    ('MANAGE_USERS', 1),
    ('RESET_ANY_PASSWORD', 1),
    ('USE_BOOKING', 2);

insert into users values (
    1,
    'Administrator',
    '{bcrypt}$2y$10$usBbY.FI5sSsGGkGFHbEeuI7edYKy9adWHGt4z3g.WP9nnKrAk4yW',
    null,
    null,
    false,
    null
);

insert into users_roles values (1, 1);