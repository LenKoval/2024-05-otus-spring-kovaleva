insert into authors(full_name)
values ('Cormac McCarthy'), ('Ernest Hemingway'), ('Richard Feynman');

insert into genres(name)
values ('Southern gothic'), ('Western'), ('Novel'),
       ('Military fiction'), ('Autobiography'), ('Popular science fiction');

insert into books(title, author_id)
values ('Blood Meridian, Or the Evening Redness in the West', 1), ('For Whom the Bell Tolls', 2),
       ('Surely You are Joking, Mr. Feynman!', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (1, 3),   (2, 3),
       (2, 4),   (3, 5),
       (3, 6);

insert into comments(text, book_id)
values ('A true masterpiece', 1), ('This book is a must read', 2), ('This book is a real page turner', 3);