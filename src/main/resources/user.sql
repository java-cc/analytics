DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`           INT         NOT NULL AUTO_INCREMENT,
  `user_id`      VARCHAR(45) NOT NULL,
  `install_date` DATETIME,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC)
);
