create database parasol_associate;
use parasol_associate;
CREATE TABLE `tb_associate_type` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `appId` bigint(20) NOT NULL COMMENT '应用ID',
  `name` varchar(250) NOT NULL COMMENT '分类名字 (人脉、组织、需求、知识），应用系统自己定义的分类',
  `updateAt` bigint(20) DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `appId_name` (`appId`,`name`) 
) ENGINE=InnoDB AUTO_INCREMENT=1307 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_associate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `userId` bigint(20) NOT NULL COMMENT '用户ID（Ower）',
  `userName` varchar(30) DEFAULT NULL COMMENT '用户名字（OwerName）',
  `appId` bigint(20) NOT NULL COMMENT '应用ID',
  `sourceTypeId` bigint(20) NOT NULL COMMENT '表示知识, 人脉ID,组织ID，等资源类型',
  `sourceId` bigint(20) NOT NULL COMMENT '知识ID, 人脉ID,组织ID，等资源ID',
  `assocDesc` varchar(100) NOT NULL COMMENT '关联描述，比如文章的作者，或者编辑等；关联标签描述',
  `assocTypeId` bigint(20) NOT NULL COMMENT '关联类型 比如管理的类型是知识、人脉、组织等',
  `assocId` bigint(20) NOT NULL COMMENT '关联数据ID',
  `assocTitle` varchar(255) NOT NULL COMMENT '标题',
  `assocMetadata` varchar(1024) DEFAULT NULL COMMENT '关联数据的描述，最好存放应用的Json关键数据',
  `createAt` bigint(20) DEFAULT '0' COMMENT '创建时间',  
  PRIMARY KEY (`id`),
  UNIQUE INDEX `userId_appId_sourceTypeId_sourceId_assocDesc_assocTypeId_assocId` (`userId`, `appId`,`sourceTypeId`,`sourceId`,`assocDesc`,`assocTypeId`,`assocId`) 
) ENGINE=InnoDB AUTO_INCREMENT=1445 DEFAULT CHARSET=utf8 COMMENT='关联信息表'

INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(1,1,'好友', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(2,1,'人脉', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(3,1,'客户', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(4,1,'组织', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(7,1,'需求', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(8,1,'知识', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(9,1,'事务', UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUE(10,1,'会议',UNIX_TIMESTAMP());
INSERT INTO tb_associate_type(id,appId, name,updateAt) VALUES(11,1,'会面',UNIX_TIMESTAMP());