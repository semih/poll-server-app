-- Query: SELECT * FROM polling_app.users
INSERT INTO `users` (`id`,`name`,`username`,`email`,`password`,`created_at`,`updated_at`) VALUES (1,'Admin','adminuser','adminuser@gmail.com','123457','2020-09-27 15:08:03','2020-09-27 15:08:03');
INSERT INTO `users` (`id`,`name`,`username`,`email`,`password`,`created_at`,`updated_at`) VALUES (2,'Semih','semihkirdinli','semihkirdinli@gmail.com','123456','2020-09-26 15:29:29','2020-09-26 15:29:29');

-- Query: SELECT * FROM polling_app.roles
INSERT INTO `roles` (`id`,`name`) VALUES (1,'ROLE_ADMIN');
INSERT INTO `roles` (`id`,`name`) VALUES (2,'ROLE_USER');

-- Query: SELECT * FROM polling_app.user_roles
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (1,1);
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (2,2);

-- Query: SELECT * FROM polling_app.choices
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (1,'sinek',1);
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (2,'örümcek',1);
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (3,'karınca',1);
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (4,'kartal',2);
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (5,'albatros',2);
INSERT INTO `choices` (`id`,`text`,`poll_id`) VALUES (6,'akbaba',2);

-- Query: SELECT * FROM polling_app.polls
INSERT INTO `polls` (`id`,`question`,`expiration_date_time`,`created_at`,`updated_at`,`created_by`,`updated_by`) VALUES (1,'Dünyadaki insan sayısının 1 milyon katı kadar bir popülasyona sahip olan hayvan hangisidir?','2025-09-12 00:00:00','2020-09-28 00:00:00','2020-09-27 00:00:00',NULL,NULL);
INSERT INTO `polls` (`id`,`question`,`expiration_date_time`,`created_at`,`updated_at`,`created_by`,`updated_by`) VALUES (2,'Kanatlarını çırpmadan bir gün boyunca havada kalabilen kuş türü sence hangisidir?','2025-09-12 00:00:00','2020-09-28 00:00:00','2020-09-28 00:00:00',NULL,NULL);