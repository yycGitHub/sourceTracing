/*
Navicat MySQL Data Transfer

Source Server         : 框架基础数据库
Source Server Version : 50552
Source Host           : 172.28.251.25:3306
Source Database       : ht

Target Server Type    : MYSQL
Target Server Version : 50552
File Encoding         : 65001

Date: 2018-06-15 10:20:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `ACT_EVT_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_EVT_LOG`;
CREATE TABLE `ACT_EVT_LOG` (
`LOG_NR_`  decimal(20,0) NULL DEFAULT NULL ,
`TYPE_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TIME_STAMP_`  datetime NULL DEFAULT NULL ,
`USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DATA_`  longblob NULL ,
`LOCK_OWNER_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LOCK_TIME_`  datetime NULL DEFAULT NULL ,
`IS_PROCESSED_`  decimal(4,0) NULL DEFAULT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_GE_BYTEARRAY`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_BYTEARRAY`;
CREATE TABLE `ACT_GE_BYTEARRAY` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DEPLOYMENT_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`BYTES_`  longblob NULL ,
`GENERATED_`  decimal(1,0) NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_GE_PROPERTY`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_PROPERTY`;
CREATE TABLE `ACT_GE_PROPERTY` (
`NAME_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`VALUE_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
PRIMARY KEY (`NAME_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_ACTINST`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ACTINST`;
CREATE TABLE `ACT_HI_ACTINST` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ACT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CALL_PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACT_NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACT_TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ASSIGNEE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`START_TIME_`  datetime NOT NULL ,
`END_TIME_`  datetime NULL DEFAULT NULL ,
`DURATION_`  decimal(19,0) NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_ATTACHMENT`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ATTACHMENT`;
CREATE TABLE `ACT_HI_ATTACHMENT` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DESCRIPTION_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`URL_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`CONTENT_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_COMMENT`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_COMMENT`;
CREATE TABLE `ACT_HI_COMMENT` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TIME_`  datetime NOT NULL ,
`USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACTION_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MESSAGE_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`FULL_MSG_`  longblob NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_DETAIL`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_DETAIL`;
CREATE TABLE `ACT_HI_DETAIL` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACT_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`VAR_TYPE_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`TIME_`  datetime NOT NULL ,
`BYTEARRAY_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DOUBLE_`  decimal(10,10) NULL DEFAULT NULL ,
`LONG_`  decimal(19,0) NULL DEFAULT NULL ,
`TEXT_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TEXT2_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_IDENTITYLINK`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_IDENTITYLINK`;
CREATE TABLE `ACT_HI_IDENTITYLINK` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`GROUP_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_PROCINST`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_PROCINST`;
CREATE TABLE `ACT_HI_PROCINST` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`BUSINESS_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`START_TIME_`  datetime NOT NULL ,
`END_TIME_`  datetime NULL DEFAULT NULL ,
`DURATION_`  decimal(19,0) NULL DEFAULT NULL ,
`START_USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`START_ACT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`END_ACT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SUPER_PROCESS_INSTANCE_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DELETE_REASON_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_TASKINST`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_TASKINST`;
CREATE TABLE `ACT_HI_TASKINST` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_DEF_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARENT_TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DESCRIPTION_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`OWNER_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ASSIGNEE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`START_TIME_`  datetime NOT NULL ,
`CLAIM_TIME_`  datetime NULL DEFAULT NULL ,
`END_TIME_`  datetime NULL DEFAULT NULL ,
`DURATION_`  decimal(19,0) NULL DEFAULT NULL ,
`DELETE_REASON_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`PRIORITY_`  decimal(65,30) NULL DEFAULT NULL ,
`DUE_DATE_`  datetime NULL DEFAULT NULL ,
`FORM_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CATEGORY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_HI_VARINST`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_VARINST`;
CREATE TABLE `ACT_HI_VARINST` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`VAR_TYPE_`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`BYTEARRAY_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DOUBLE_`  decimal(10,10) NULL DEFAULT NULL ,
`LONG_`  decimal(19,0) NULL DEFAULT NULL ,
`TEXT_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TEXT2_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`CREATE_TIME_`  datetime NULL DEFAULT NULL ,
`LAST_UPDATED_TIME_`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_ID_GROUP`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_GROUP`;
CREATE TABLE `ACT_ID_GROUP` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_ID_INFO`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_INFO`;
CREATE TABLE `ACT_ID_INFO` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`USER_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TYPE_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`VALUE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PASSWORD_`  longblob NULL ,
`PARENT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_ID_MEMBERSHIP`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_MEMBERSHIP`;
CREATE TABLE `ACT_ID_MEMBERSHIP` (
`USER_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`GROUP_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`USER_ID_`, `GROUP_ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_ID_USER`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_USER`;
CREATE TABLE `ACT_ID_USER` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`FIRST_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LAST_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EMAIL_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PWD_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PICTURE_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_PROCDEF_INFO`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_PROCDEF_INFO`;
CREATE TABLE `ACT_PROCDEF_INFO` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`INFO_JSON_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RE_DEPLOYMENT`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_DEPLOYMENT`;
CREATE TABLE `ACT_RE_DEPLOYMENT` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CATEGORY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DEPLOY_TIME_`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RE_LISTENER`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_LISTENER`;
CREATE TABLE `ACT_RE_LISTENER` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '监听器配置信息表主键' ,
`listener_name`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监听器名称' ,
`listener_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监听器类型' ,
`listener_desc`  varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监听器描述' ,
`event_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件类型' ,
`exe_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行类型' ,
`path`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`status`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监听启用状态' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='监听器配置信息表'

;

-- ----------------------------
-- Table structure for `ACT_RE_MODEL`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_MODEL`;
CREATE TABLE `ACT_RE_MODEL` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CATEGORY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_TIME_`  datetime NULL DEFAULT NULL ,
`LAST_UPDATE_TIME_`  datetime NULL DEFAULT NULL ,
`VERSION_`  decimal(65,30) NULL DEFAULT NULL ,
`META_INFO_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`DEPLOYMENT_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EDITOR_SOURCE_VALUE_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EDITOR_SOURCE_EXTRA_VALUE_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RE_PROCDEF`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_PROCDEF`;
CREATE TABLE `ACT_RE_PROCDEF` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`CATEGORY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`VERSION_`  decimal(65,30) NOT NULL ,
`DEPLOYMENT_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`RESOURCE_NAME_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`DGRM_RESOURCE_NAME_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`DESCRIPTION_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`HAS_START_FORM_KEY_`  decimal(1,0) NULL DEFAULT NULL ,
`SUSPENSION_STATE_`  decimal(65,30) NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`HAS_GRAPHICAL_NOTATION_`  decimal(1,0) NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_EVENT_SUBSCR`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EVENT_SUBSCR`;
CREATE TABLE `ACT_RU_EVENT_SUBSCR` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`EVENT_TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`EVENT_NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACTIVITY_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CONFIGURATION_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATED_`  datetime NOT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_EXECUTION`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EXECUTION`;
CREATE TABLE `ACT_RU_EXECUTION` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`BUSINESS_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARENT_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SUPER_EXEC_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`IS_ACTIVE_`  decimal(1,0) NULL DEFAULT NULL ,
`IS_CONCURRENT_`  decimal(1,0) NULL DEFAULT NULL ,
`IS_SCOPE_`  decimal(1,0) NULL DEFAULT NULL ,
`IS_EVENT_SCOPE_`  decimal(1,0) NULL DEFAULT NULL ,
`SUSPENSION_STATE_`  decimal(65,30) NULL DEFAULT NULL ,
`CACHED_ENT_STATE_`  decimal(65,30) NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LOCK_TIME_`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_IDENTITYLINK`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_IDENTITYLINK`;
CREATE TABLE `ACT_RU_IDENTITYLINK` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`GROUP_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`USER_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_JOB`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_JOB`;
CREATE TABLE `ACT_RU_JOB` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`LOCK_EXP_TIME_`  datetime NULL DEFAULT NULL ,
`LOCK_OWNER_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXCLUSIVE_`  decimal(1,0) NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROCESS_INSTANCE_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`RETRIES_`  decimal(65,30) NULL DEFAULT NULL ,
`EXCEPTION_STACK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EXCEPTION_MSG_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`DUEDATE_`  datetime NULL DEFAULT NULL ,
`REPEAT_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`HANDLER_TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`HANDLER_CFG_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_TASK`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TASK`;
CREATE TABLE `ACT_RU_TASK` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_DEF_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARENT_TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DESCRIPTION_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TASK_DEF_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`OWNER_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ASSIGNEE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DELEGATION_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PRIORITY_`  decimal(65,30) NULL DEFAULT NULL ,
`CREATE_TIME_`  datetime NULL DEFAULT NULL ,
`DUE_DATE_`  datetime NULL DEFAULT NULL ,
`CATEGORY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SUSPENSION_STATE_`  decimal(65,30) NULL DEFAULT NULL ,
`TENANT_ID_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`FORM_KEY_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `ACT_RU_VARIABLE`
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_VARIABLE`;
CREATE TABLE `ACT_RU_VARIABLE` (
`ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`REV_`  decimal(65,30) NULL DEFAULT NULL ,
`TYPE_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`NAME_`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`EXECUTION_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PROC_INST_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`BYTEARRAY_ID_`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DOUBLE_`  decimal(10,10) NULL DEFAULT NULL ,
`LONG_`  decimal(19,0) NULL DEFAULT NULL ,
`TEXT_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`TEXT2_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`ID_`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `api_interface`
-- ----------------------------
DROP TABLE IF EXISTS `api_interface`;
CREATE TABLE `api_interface` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口名称' ,
`URL`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口URL' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口状态' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `api_token`
-- ----------------------------
DROP TABLE IF EXISTS `api_token`;
CREATE TABLE `api_token` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`TOKEN`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`FAILURE_TIME`  timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP ,
`APP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `api_user`
-- ----------------------------
DROP TABLE IF EXISTS `api_user`;
CREATE TABLE `api_user` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`NAME`  varchar(126) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SECRET_CODE`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `api_user_interface`
-- ----------------------------
DROP TABLE IF EXISTS `api_user_interface`;
CREATE TABLE `api_user_interface` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`APP_ID`  varchar(126) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`INTERFACE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `cms_article`
-- ----------------------------
DROP TABLE IF EXISTS `cms_article`;
CREATE TABLE `cms_article` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`CATEGORY_ID`  int(11) NOT NULL COMMENT '栏目编号' ,
`TITLE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题' ,
`LINK`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章链接' ,
`COLOR`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题颜色' ,
`IMAGE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章图片' ,
`KEYWORDS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字' ,
`DESCRIPTION`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述、摘要' ,
`WEIGHT`  decimal(11,0) NULL DEFAULT NULL COMMENT '权重，越大越靠前' ,
`WEIGHT_DATE`  datetime NULL DEFAULT NULL COMMENT '权重期限' ,
`HITS`  decimal(11,0) NULL DEFAULT NULL COMMENT '点击数' ,
`POSID`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '推荐位，多选' ,
`CUSTOM_CONTENT_VIEW`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义内容视图' ,
`VIEW_CONFIG`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '视图配置' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
`CONTENT`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '文章内容' ,
`COPYFROM`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章来源' ,
`RELATION`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '相关文章' ,
`ALLOW_COMMENT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否允许评论' ,
`ARTICLE_AUTHOR`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MEETING_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='文章表'
AUTO_INCREMENT=13

;

-- ----------------------------
-- Table structure for `cms_category`
-- ----------------------------
DROP TABLE IF EXISTS `cms_category`;
CREATE TABLE `cms_category` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归属机构' ,
`PARENT_ID`  int(11) NOT NULL COMMENT '父级编号' ,
`PARENT_IDS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`MODULE`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '栏目模块' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '栏目名称' ,
`LONG_IMAGE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`IMAGE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '栏目图片' ,
`HREF`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接' ,
`TARGET`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目标' ,
`DESCRIPTION`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述' ,
`KEYWORDS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字' ,
`SORT`  decimal(11,0) NULL DEFAULT NULL COMMENT '排序（升序）' ,
`IN_MENU`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否在导航中显示' ,
`IN_LIST`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否在分类页中显示列表' ,
`SHOW_MODES`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '展现方式' ,
`ALLOW_COMMENT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否允许评论' ,
`IS_AUDIT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否需要审核' ,
`CUSTOM_LIST_VIEW`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义列表视图' ,
`CUSTOM_CONTENT_VIEW`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义内容视图' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
`VIEW_CONFIG`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '视图配置' ,
`CONTENT`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`MARKS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`IMAGES`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`IS_COMMENT_AUDIT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='栏目表'
AUTO_INCREMENT=30

;

-- ----------------------------
-- Table structure for `cms_category_role_user`
-- ----------------------------
DROP TABLE IF EXISTS `cms_category_role_user`;
CREATE TABLE `cms_category_role_user` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`CATEGORY_ID`  int(11) NOT NULL COMMENT 'cms_category表主键' ,
`ROLE_ID`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sys_role表主键' ,
`USER_ID`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sys_user表主键' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `cms_comment`
-- ----------------------------
DROP TABLE IF EXISTS `cms_comment`;
CREATE TABLE `cms_comment` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`CATEGORY_ID`  int(11) NOT NULL COMMENT '栏目编号' ,
`CONTENT_ID`  int(11) NOT NULL COMMENT '栏目内容的编号' ,
`TITLE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '栏目内容的标题' ,
`CONTENT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论内容' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论姓名' ,
`IP`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论IP' ,
`AUDIT_USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人' ,
`AUDIT_DATE`  datetime NULL DEFAULT NULL COMMENT '审核时间' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='评论表'
AUTO_INCREMENT=23

;

-- ----------------------------
-- Table structure for `cms_link`
-- ----------------------------
DROP TABLE IF EXISTS `cms_link`;
CREATE TABLE `cms_link` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`CATEGORY_ID`  decimal(11,0) NOT NULL COMMENT '栏目编号' ,
`TITLE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '链接名称' ,
`COLOR`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题颜色' ,
`IMAGE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接图片' ,
`HREF`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接地址' ,
`WEIGHT`  decimal(11,0) NULL DEFAULT NULL COMMENT '权重，越大越靠前' ,
`WEIGHT_DATE`  datetime NULL DEFAULT NULL COMMENT '权重期限' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='友情链接'

;

-- ----------------------------
-- Table structure for `gmscjh0101`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0101`;
CREATE TABLE `gmscjh0101` (
`GMSCJH0101_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项目表_ID' ,
`XMMC`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称' ,
`OFFICE_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属单位_ID' ,
`OFFICE_NAME`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属单位名称' ,
`XMLX`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目类型' ,
`XMMS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '项目描述' ,
`XJRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新建日期' ,
`XMZT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目状态' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0101_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0101 项目表'

;

-- ----------------------------
-- Table structure for `gmscjh0103`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0103`;
CREATE TABLE `gmscjh0103` (
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`GMSCJH0101`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目_ID' ,
`XMMC`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称' ,
`GZNR`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '工作内容' ,
`WCXS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成形式' ,
`JHWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计划完成时间' ,
`OFFICE_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '责任单位_ID' ,
`OFFICE_NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '责任单位名称' ,
`SFZRL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否责任令' ,
`XJRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新建日期' ,
`JHZT`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计划状态' ,
`XJRY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新建人员_ID' ,
`XJRYXM`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新建人员姓名' ,
`GDQJHWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '滚动前计划完成时间' ,
`SJZXGDSJ`  datetime NULL DEFAULT NULL COMMENT '数据最新滚动时间' ,
`GDCS`  decimal(5,0) NULL DEFAULT NULL COMMENT '滚动次数' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0103_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0103 生产计划表'

;

-- ----------------------------
-- Table structure for `gmscjh0104`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0104`;
CREATE TABLE `gmscjh0104` (
`GMSCJH0104_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划发布_ID' ,
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`SQFBRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请发布日期' ,
`SQFBSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请发布时间' ,
`FBRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布日期' ,
`FBSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布时间' ,
`FBZT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布状态' ,
`SQFBRY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请发布人员ID' ,
`SQFBRYXM`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请发布人员姓名' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0104_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0104生产计划发布表'

;

-- ----------------------------
-- Table structure for `gmscjh0105`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0105`;
CREATE TABLE `gmscjh0105` (
`GMSCJH0105_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划考核表_ID' ,
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`SQKHRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请考核日期' ,
`SQKHSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请考核时间' ,
`KHJSRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '考核结束日期' ,
`KHJSSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '考核结束时间' ,
`KHZT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '考核状态' ,
`SJWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实际完成时间' ,
`WCQK`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成情况' ,
`WCQKSM`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '完成情况说明' ,
`SQKHRY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请考核人员ID' ,
`SQKHRYXM`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请考核人员姓名' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0105_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0105生产计划考核表'

;

-- ----------------------------
-- Table structure for `gmscjh0106`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0106`;
CREATE TABLE `gmscjh0106` (
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`GMSCJH0101`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目_ID' ,
`XMMC`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称' ,
`GZNR`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '工作内容' ,
`WCXS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成形式' ,
`JHWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计划完成时间' ,
`OFFICE_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '责任单位_ID' ,
`OFFICE_NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '责任单位名称' ,
`SFZRL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否责任令' ,
`XJRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新建时间' ,
`JHZT`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整申请时计划状态' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请调整前工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
`GMSCJH0106_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划历史表_ID' ,
`TZSQRY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整申请人员_ID' ,
`TZSQRYXM`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整申请人员姓名' ,
`TZSQRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整申请日期' ,
PRIMARY KEY (`GMSCJH0103_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0106 生产计划表历史表'

;

-- ----------------------------
-- Table structure for `gmscjh0107`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0107`;
CREATE TABLE `gmscjh0107` (
`GMSCJH0107_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划预警表_ID' ,
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`YJXJRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警新建日期' ,
`YJXJSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警新建时间' ,
`YJJB`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警级别' ,
`YJZT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警状态' ,
`CQYS`  decimal(5,0) NULL DEFAULT NULL COMMENT '超期月数' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0107_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0107生产计划预警表'

;

-- ----------------------------
-- Table structure for `gmscjh0108`
-- ----------------------------
DROP TABLE IF EXISTS `gmscjh0108`;
CREATE TABLE `gmscjh0108` (
`GMSCJH0108_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划滚动记录表_ID' ,
`GMSCJH0103_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '生产计划表_ID' ,
`SJGDSJ`  datetime NULL DEFAULT NULL COMMENT '数据滚动时间' ,
`GDQJHWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '滚动前计划完成时间' ,
`GDHJHWCSJ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '滚动后计划完成时间' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
PRIMARY KEY (`GMSCJH0108_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GMSCJH0108生产计划滚动记录表'

;

-- ----------------------------
-- Table structure for `gxqj0101`
-- ----------------------------
DROP TABLE IF EXISTS `gxqj0101`;
CREATE TABLE `gxqj0101` (
`GXQJ0101_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请假单_ID' ,
`GXXS0101_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生基本信息_ID' ,
`XM`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名' ,
`QJDBT`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假单标题' ,
`QJLB`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假类别' ,
`SQRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假单的创建日期' ,
`SQSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假单的创建时间' ,
`QJKSRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假开始日期' ,
`QJKSSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假开始时间' ,
`QJJSRQ`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假结束日期' ,
`QJJSSJ`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假结束时间' ,
`QJZSC`  decimal(65,30) NULL DEFAULT NULL COMMENT '请假总时长（天）' ,
`SJQJZSJ`  decimal(65,30) NULL DEFAULT NULL COMMENT '实际请假总时长（天）' ,
`ZT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核状态、续假、销假' ,
`SFBL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否为事后补假' ,
`XYID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学院ID' ,
`XYMC`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学院名称' ,
`ZYID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专业ID' ,
`ZYMC`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专业名称' ,
`BJID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '班级ID' ,
`BJMC`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '班级名称' ,
`SLID`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID' ,
`ZHXGSJ`  datetime NULL DEFAULT NULL COMMENT '最后修改时间' ,
`XXID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学校ID' ,
`GXBZ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新标识' ,
`QJSY`  longblob NULL COMMENT '请假事由' ,
`BZ`  longblob NULL COMMENT '备注' ,
`XH`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学号' ,
`JZYJ`  longblob NULL COMMENT '家长意见  由辅导员审批时填入 同时可上传附件  不保存历史审批中的附件' ,
`SFXJ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否有续假' ,
`SFYXJ`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销假状态 1为申请销假  2为销假结束  3为销假被驳回' ,
`XJSFYJS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '续假状态 1为申请续假  2为审批已结束 3为续假驳回' ,
`SZNJ`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所在年级' ,
PRIMARY KEY (`GXQJ0101_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='GXQJ0101 请假单'

;

-- ----------------------------
-- Table structure for `oa_leave`
-- ----------------------------
DROP TABLE IF EXISTS `oa_leave`;
CREATE TABLE `oa_leave` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PROCESS_INSTANCE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程实例编号' ,
`START_TIME`  datetime NULL DEFAULT NULL COMMENT '开始时间' ,
`END_TIME`  datetime NULL DEFAULT NULL COMMENT '结束时间' ,
`LEAVE_TYPE`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假类型' ,
`REASON`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请假理由' ,
`APPLY_TIME`  datetime NULL DEFAULT NULL COMMENT '申请时间' ,
`REALITY_START_TIME`  datetime NULL DEFAULT NULL COMMENT '实际开始时间' ,
`REALITY_END_TIME`  datetime NULL DEFAULT NULL COMMENT '实际结束时间' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NOT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NOT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
`USER`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='请假流程表'

;

-- ----------------------------
-- Table structure for `oa_test_audit`
-- ----------------------------
DROP TABLE IF EXISTS `oa_test_audit`;
CREATE TABLE `oa_test_audit` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PROC_INS_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程实例ID' ,
`USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变动用户' ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归属部门' ,
`POST`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位' ,
`AGE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别' ,
`EDU`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学历' ,
`CONTENT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整原因' ,
`OLDA`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现行标准 薪酬档级' ,
`OLDB`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现行标准 月工资额' ,
`OLDC`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现行标准 年薪总额' ,
`NEWA`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整后标准 薪酬档级' ,
`NEWB`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整后标准 月工资额' ,
`NEWC`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整后标准 年薪总额' ,
`ADD_NUM`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月增资' ,
`EXE_DATE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行时间' ,
`HR_TEXT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人力资源部门意见' ,
`LEAD_TEXT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分管领导意见' ,
`MAIN_LEAD_TEXT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '集团主要领导意见' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NOT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NOT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='审批流程测试表'

;

-- ----------------------------
-- Table structure for `oa_test_expenses`
-- ----------------------------
DROP TABLE IF EXISTS `oa_test_expenses`;
CREATE TABLE `oa_test_expenses` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PROC_INS_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程实例ID' ,
`USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变动用户' ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归属部门' ,
`POST`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位' ,
`AGE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别' ,
`EDU`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学历' ,
`CONTENT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调整原因' ,
`ADD_NUM`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月增资' ,
`HR_TEXT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人力资源部门意见' ,
`LEAD_TEXT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分管领导意见' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NOT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NOT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='审批流程测试表'

;

-- ----------------------------
-- Table structure for `sys_area`
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`PARENT_IDS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`CODE`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域编码' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '区域名称' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域类型' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='区域表'

;

-- ----------------------------
-- Table structure for `sys_dict`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`LABEL`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签名' ,
`VALUE`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据值' ,
`TYPE`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型' ,
`DESCRIPTION`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '描述' ,
`SORT`  decimal(11,0) NOT NULL COMMENT '排序（升序）' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='字典表'

;

-- ----------------------------
-- Table structure for `sys_expand_property_list`
-- ----------------------------
DROP TABLE IF EXISTS `sys_expand_property_list`;
CREATE TABLE `sys_expand_property_list` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '编号' ,
`from_table_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`from_table_name`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`main_standard_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
`syn_time`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`syn_state`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_file_info`
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_info`;
CREATE TABLE `sys_file_info` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`FILE_NAME`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`NEW_FILE_NAME`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`URL`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`FILE_SIZE`  decimal(65,30) NULL DEFAULT NULL ,
`TYPE`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标记' ,
`ABSOLUTE_PATH`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`YWZB_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务主表主键' ,
`YWZB_TYPE`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务主表类别值' ,
`FIELD_MARK`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_group`
-- ----------------------------
DROP TABLE IF EXISTS `sys_group`;
CREATE TABLE `sys_group` (
`GROUP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组主键id' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组名' ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父id' ,
`PARENT_IDS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父id' ,
`GROUP_TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '组类型' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记' ,
PRIMARY KEY (`GROUP_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_group_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_user`;
CREATE TABLE `sys_group_user` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`group_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组编号' ,
`user_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户编号' ,
`master`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '是否是组管理者 0:不是  1:是  默认0' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志类型' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`REMOTE_ADDR`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作IP地址' ,
`USER_AGENT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户代理' ,
`REQUEST_URI`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求URI' ,
`METHOD`  varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作方式' ,
`PARAMS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作提交的数据' ,
`EXCEPTION`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '异常信息' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='日志表'

;

-- ----------------------------
-- Table structure for `sys_login_token`
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_token`;
CREATE TABLE `sys_login_token` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`USER_NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TOKEN`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`FAIL_TIME`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_mdict`
-- ----------------------------
DROP TABLE IF EXISTS `sys_mdict`;
CREATE TABLE `sys_mdict` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`PARENT_IDS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称' ,
`DESCRIPTION`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述' ,
`SORT`  decimal(11,0) NULL DEFAULT NULL COMMENT '排序（升序）' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='多级字典表'

;

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`PARENT_IDS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称' ,
`HREF`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接' ,
`TARGET`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目标' ,
`ICON`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标' ,
`SORT`  decimal(11,0) NOT NULL COMMENT '排序（升序）' ,
`IS_SHOW`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否在菜单中显示' ,
`IS_ACTIVITI`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否同步工作流' ,
`PERMISSION`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
`TYPE`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_office`
-- ----------------------------
DROP TABLE IF EXISTS `sys_office`;
CREATE TABLE `sys_office` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`PARENT_IDS`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`AREA_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '归属区域' ,
`CODE`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域编码' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构名称' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构类型' ,
`GRADE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构等级' ,
`ADDRESS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系地址' ,
`ZIP_CODE`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮政编码' ,
`MASTER`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负责人' ,
`PHONE`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话' ,
`FAX`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '传真' ,
`EMAIL`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='机构表'

;

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归属机构' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称' ,
`DATA_SCOPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据范围' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='角色表'

;

-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
`ROLE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编号' ,
`MENU_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单编号' ,
PRIMARY KEY (`ROLE_ID`, `MENU_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='角色-菜单'

;

-- ----------------------------
-- Table structure for `sys_role_office`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_office`;
CREATE TABLE `sys_role_office` (
`ROLE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编号' ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构编号' ,
PRIMARY KEY (`ROLE_ID`, `OFFICE_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='角色-机构'

;

-- ----------------------------
-- Table structure for `sys_standard`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard`;
CREATE TABLE `sys_standard` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`parent_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`parent_ids`  varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所有父级编号' ,
`name`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称' ,
`sort`  int(11) NOT NULL COMMENT '排序（升序）' ,
`office_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
`type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`syn_time`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_standard_app_item_value`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard_app_item_value`;
CREATE TABLE `sys_standard_app_item_value` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`standard_user_app_list_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`standard_item_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`standard_item_name`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`standard_item_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`item_sort`  int(5) NULL DEFAULT NULL ,
`item_value`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`blob_value`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
`syn_time`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`syn_state`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_standard_app_set`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard_app_set`;
CREATE TABLE `sys_standard_app_set` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`main_standard_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`standard_edition_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`role_type`  varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`standard_name`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_code`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`user_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_remind_time`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_remind_text`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_set_region_type`  varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_set_region_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_set_region_name`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`app_set_region_code`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`office_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
`syn_time`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`set_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`effect_time`  varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`failure_time`  varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`park_used`  tinyint(1) NULL DEFAULT 1 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_standard_edition`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard_edition`;
CREATE TABLE `sys_standard_edition` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`standard_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标准id' ,
`name`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本名称' ,
`create_date`  datetime NULL DEFAULT NULL ,
`sort`  int(11) NULL DEFAULT 0 COMMENT '排序字段' ,
`office_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记' ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_standard_item`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard_item`;
CREATE TABLE `sys_standard_item` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '编号' ,
`target_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父级编号' ,
`target_name`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`name`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名称' ,
`key_name`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`item_type`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`sort`  int(11) NULL DEFAULT NULL COMMENT '排序（升序）' ,
`min_value`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`max_value`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`item_unit`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`warning_app_set_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`warning_app_set_name`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
`syn_time`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`item_limit_type`  char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`is_required`  tinyint(1) NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_standard_item_value`
-- ----------------------------
DROP TABLE IF EXISTS `sys_standard_item_value`;
CREATE TABLE `sys_standard_item_value` (
`id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`standard_item_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '父级编号' ,
`name`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称' ,
`value`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`create_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`create_date`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`update_by`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`update_date`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`remarks`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息' ,
`del_flag`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='菜单表'

;

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
`ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号' ,
`COMPANY_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '归属公司' ,
`OFFICE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '归属部门' ,
`LOGIN_NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名' ,
`PASSWORD`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码' ,
`NO`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工号' ,
`NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名' ,
`EMAIL`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱' ,
`PHONE`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话' ,
`CROP_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CROP_POSTION`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CROP_ADDRESS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`USER_TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型' ,
`LOGIN_IP`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后登陆IP' ,
`LOGIN_DATE`  datetime NULL DEFAULT NULL COMMENT '最后登陆时间' ,
`CREATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者' ,
`CREATE_DATE`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,
`UPDATE_BY`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者' ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL COMMENT '更新时间' ,
`REMARKS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注信息' ,
`DEL_FLAG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标记' ,
`OPEN_ID`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`USER_IMG`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`UNION_ID`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`LOGIN_CODE`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'COOKIE身份验证值' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='用户表'

;

-- ----------------------------
-- Table structure for `sys_user_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_menu`;
CREATE TABLE `sys_user_menu` (
`USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID' ,
`MENU_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单ID' ,
PRIMARY KEY (`USER_ID`, `MENU_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
`USER_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户编号' ,
`ROLE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编号' ,
PRIMARY KEY (`USER_ID`, `ROLE_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='用户-角色'

;

-- ----------------------------
-- Auto increment value for `cms_article`
-- ----------------------------
ALTER TABLE `cms_article` AUTO_INCREMENT=13;

-- ----------------------------
-- Auto increment value for `cms_category`
-- ----------------------------
ALTER TABLE `cms_category` AUTO_INCREMENT=30;

-- ----------------------------
-- Auto increment value for `cms_comment`
-- ----------------------------
ALTER TABLE `cms_comment` AUTO_INCREMENT=23;

-- ----------------------------
-- Indexes structure for table sys_standard_app_item_value
-- ----------------------------
CREATE INDEX `standard_user_app_list_index` ON `sys_standard_app_item_value`(`standard_user_app_list_id`) USING BTREE ;
