--date: 2024-10-24
--author: ElenaKovaleva

create table roles (
    id bigserial,
    name varchar not null,
    primary key (id)
);

create table users (
    id bigserial,
    username varchar not null,
    password varchar not null,
    primary key (id)
);

create table users_roles (
    user_id bigint references users(id) on delete cascade,
    role_id bigint references roles(id) on delete cascade,
    primary key (user_id, role_id)
);