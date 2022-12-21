CREATE TABLE IF NOT EXISTS `service_now_sync_file`(
    `id` bigint(10) NOT NULL AUTO_INCREMENT,
    `ticket` varchar(100) NOT NULL,
    `name` TEXT NOT NULL,
    `url_link` VARCHAR(500) NOT NULL,
    `service_now_sync_data_id` bigint(10) NOT NULL,
    `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`service_now_sync_data_id`) REFERENCES service_now_sync_data(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_ticket` on `service_now_sync_data_file`(`ticket`);
