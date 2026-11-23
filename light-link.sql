/*
SQLyog Community v13.2.1 (64 bit)
MySQL - 8.0.36 : Database - personal_meetting
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`light_link` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `light_link`;

/*Table structure for table `apply_info` */

DROP TABLE IF EXISTS `apply_info`;

CREATE TABLE `apply_info` (
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(256) DEFAULT NULL,
  `extra` varchar(500) DEFAULT NULL,
  `info` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `apply_info` */

/*Table structure for table `invite_link` */

DROP TABLE IF EXISTS `invite_link`;

CREATE TABLE `invite_link` (
  `link_key` varchar(20) NOT NULL,
  `link_server` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`link_key`),
  KEY `idx_unique_server` (`link_server`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


/*Table structure for table `server` */

DROP TABLE IF EXISTS `server`;

CREATE TABLE `server` (
  `server_id` varchar(20) NOT NULL,
  `server_name` varbinary(20) NOT NULL,
  `server_master` varchar(9) NOT NULL,
  `server_info` varchar(200) DEFAULT NULL,
  `max_number` int NOT NULL,
  `server_image` varchar(256) DEFAULT NULL,
  `extra` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`server_id`),
  UNIQUE KEY `idx_unique_name` (`server_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


/*Table structure for table `server_member` */

DROP TABLE IF EXISTS `server_member`;

CREATE TABLE `server_member` (
  `server_id` varchar(20) NOT NULL,
  `server_user` varchar(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `uid` varchar(9) NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(256) DEFAULT NULL,
  `extra` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
