CREATE TABLE IF NOT EXISTS `service_now_sync_data`(
    `id` bigint(10) NOT NULL AUTO_INCREMENT,
    `ticket` varchar(100) NOT NULL,
    `short_description` TEXT NOT NULL,
    `description` TEXT NOT NULL,
    `service_now_link` VARCHAR(500) NOT NULL,
    `contact` VARCHAR(100) NOT NULL,
    `ticket_open_date` timestamp NOT NULL,
    `squad` varchar(150) NOT NULL,
    `trello_card_id` varchar(200) NOT NULL,
    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_ticket` on `service_now_sync_data`(`ticket`);
CREATE INDEX `idx_squad` on `service_now_sync_data`(`squad`);
CREATE INDEX `idx_ticket_open_date` on `service_now_sync_data`(`ticket_open_date`);
CREATE INDEX `idx_trello_card_id` on `service_now_sync_data`(`trello_card_id`);
CREATE INDEX `idx_created_time` on `service_now_sync_data`(`created_time`);
