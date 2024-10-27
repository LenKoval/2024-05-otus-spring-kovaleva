insert into roles(name)
values('ROLE_USER'), ('ROLE_ADMIN');

insert into users(username, password)
values('user', '$2a$10$EbxTMIw9GmSzG7K/9gTYq.PylaOhWMFH2zm1Z4oYEVd0.OwLsWGYW'),
      ('admin', '$2a$10$9eS7j6nK9ze/q9RdAafjue3u1guInN5xQ6yMP6/LvsC.K4gyATDji');

insert into users_roles(user_id, role_id)
values(1, 1), (2, 1), (2,2);