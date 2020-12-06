insert into usr (id, username, password, active)
values (1, 'admin', '$2y$08$an659l9Sq.jXiaVOi43xSup16m9tgqGp1lzyIVFUPjs7Srv6DTz4y', true);

insert into user_role (user_id, roles)
values (1, 'USER'),
       (1, 'ADMIN');