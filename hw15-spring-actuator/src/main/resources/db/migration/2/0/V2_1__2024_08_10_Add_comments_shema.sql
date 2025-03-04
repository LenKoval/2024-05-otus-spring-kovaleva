--date: 2024-08-10
--author: ElenaKovaleva

create table comments (
    id bigserial primary key,
    text varchar not null,
    book_id bigint not null references books (id)
);