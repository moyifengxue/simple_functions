DROP TABLE IF EXISTS `hello_word`;
CREATE TABLE `hello_word`(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(255) DEFAULT NULL COMMENT '名称',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into hello_word(name) values ('hello');
