create database export_center;

drop table if exists export_job;
create table export_job
(
    id          bigint       not null auto_increment comment '主键',
    name        varchar(255) not null comment '任务名称',
    job_handler varchar(255) not null comment '任务处理器',
    job_params  varchar(255) not null comment '任务参数',
    page_size   int          not null default '1000' comment '每页大小',
    status      int          not null default '0' comment '0:未执行,1:执行中,2:执行成功,3:执行失败',
    url         varchar(255) comment '导出文件的url',
    message     varchar(255) comment '执行结果信息',
    primary key (id)
)  ENGINE=InnoDB COMMENT='导出任务表';