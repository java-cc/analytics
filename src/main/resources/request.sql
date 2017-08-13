DROP TABLE IF EXISTS `request`;
CREATE TABLE `request` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT,
  `user_id`      VARCHAR(45) NOT NULL,
  `request_date` DATETIME    NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `user_id_idx` (`user_id` ASC),
  CONSTRAINT `user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
