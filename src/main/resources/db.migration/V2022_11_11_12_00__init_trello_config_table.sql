CREATE TABLE IF NOT EXISTS `trello_config`(
    `id` bigint(10) NOT NULL AUTO_INCREMENT,
    `squad` varchar(150) NOT NULL,
    `trello_board_id` varchar(150) NOT NULL,
    `default_list_card_name` varchar(200) NOT NULL DEFAULT 'TODO',
    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `trello_config_check_list`(
    `id` bigint(10) NOT NULL AUTO_INCREMENT,
    `check_list_name` varchar(150) NOT NULL,
    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `trello_config_id` bigint(10) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`trello_config_id`) REFERENCES trello_config(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;