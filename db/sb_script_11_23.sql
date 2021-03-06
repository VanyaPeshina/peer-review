-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.6.4-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for peerreview
CREATE DATABASE IF NOT EXISTS `peerreview` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `peerreview`;

-- Dumping structure for table peerreview.attachments
CREATE TABLE IF NOT EXISTS `attachments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachment` blob NOT NULL,
  `creator_id` int(11) NOT NULL,
  `work_item_fk` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attachments_creator_fk` (`creator_id`),
  KEY `attachments_work_item_fk` (`work_item_fk`),
  CONSTRAINT `attachments_creator_fk` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`),
  CONSTRAINT `attachments_work_item_fk` FOREIGN KEY (`work_item_fk`) REFERENCES `work_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.attachments: ~0 rows (approximately)
/*!40000 ALTER TABLE `attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachments` ENABLE KEYS */;

-- Dumping structure for table peerreview.comments
CREATE TABLE IF NOT EXISTS `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment` varchar(2000) NOT NULL,
  `creator_id` int(11) NOT NULL,
  `work_item_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `comments_user_fk` (`creator_id`),
  KEY `comments_work_item_fk` (`work_item_id`),
  CONSTRAINT `comments_user_fk` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`),
  CONSTRAINT `comments_work_item_fk` FOREIGN KEY (`work_item_id`) REFERENCES `work_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.comments: ~0 rows (approximately)
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

-- Dumping structure for table peerreview.statuses
CREATE TABLE IF NOT EXISTS `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `statuses_status_uindex` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.statuses: ~3 rows (approximately)
/*!40000 ALTER TABLE `statuses` DISABLE KEYS */;
INSERT INTO `statuses` (`id`, `status`) VALUES
	(4, 'Change Requested'),
	(1, 'Pending'),
	(2, 'Under Review');
/*!40000 ALTER TABLE `statuses` ENABLE KEYS */;

-- Dumping structure for table peerreview.teams
CREATE TABLE IF NOT EXISTS `teams` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `owner_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `teams_name_uindex` (`name`),
  KEY `teams_owners_fk` (`owner_id`),
  CONSTRAINT `teams_owners_fk` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.teams: ~1 rows (approximately)
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` (`id`, `name`, `owner_id`) VALUES
	(1, 'TestTeam', 1);
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;

-- Dumping structure for table peerreview.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(350) NOT NULL,
  `email` varchar(350) NOT NULL,
  `phone` varchar(10) NOT NULL,
  `photo` blob NOT NULL,
  `team_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_email_uindex` (`email`),
  UNIQUE KEY `users_phone_uindex` (`phone`),
  UNIQUE KEY `users_username_uindex` (`username`),
  KEY `users_role_fk` (`role_id`),
  KEY `users_team_fk` (`team_id`),
  CONSTRAINT `users_role_fk` FOREIGN KEY (`role_id`) REFERENCES `user_roles` (`id`),
  CONSTRAINT `users_team_fk` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.users: ~2 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `photo`, `team_id`, `role_id`) VALUES
	(1, 'vanya.peshina', '12341234', 'vanya.peshina@gmail.com', '0886345243', _binary 0xffd8ffe000104a46494600010200000100010000ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc00011080100010003012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00f10a28a2a46145145005ba28a282c28a28a00f65a28a2b84f4028a28a00ede8a28accd028a28a00e668a28ad4cc28a28a00f0fa28a2bace10a28a28029d14514121451450058a28a29141451450052a28a2acc828a28a00b74514505851451401ecb45145709e8051451401dbd1451599a051451401ccd14515a99851451401e1f45145759c21451450053a28a282428a28a00b14514522828a28a00a3451456c62145145007a5d14515c477851451401ebd45145729d6145145006b514515d260145145005ea28a299214514500788514515230a28a280384a28a2b4310a28a28028d14515a1985145140195451456e73851451401e9745145711de145145007af514515ca75851451401ad4514574980514514017a8a28a64851451401e214514548c28a28a00e128a28ad0c428a28a00a34514568661451450055a28a2b6310a28a2803d6e8a28ae13d00a28a2803728a28a92828a28a00eaa8a28a601451450058a28a290c28a28a00e3a8a28a041451450070b451455121451450070d45145741c81451450045451456c64145145007add14515c27a0145145006e51451525051451401d5514514c028a28a00b14514521851451401c75145140828a28a00e168a28aa2428a28a00e1a8a28ae839028a28a00b145145510145145007a9514515c6778514514009451456c601451450075f4514562740514514009451454941451450072545145741cc1451450056a28a2a0b0a28a2803cb68a28aea38828a28a00b34514521851451401ea545145719de145145002514515b18051451401d7d1451589d014514500251451525051451401c9514515d0730514514015a8a28a82c28a28a00f2da28a2ba8e20a28a2803a5a28a2b3360a28a2803d228a28ae23d30a28a2803bba28a2ac80a28a28032a8a28a448514514010d145140051451401a14514522c28a28a00e2e8a28a0614514500791514515dc794145145006cd1451525851451401e9145145711e9851451401ddd1451564051451401954514522428a28a00868a28a0028a28a00d0a28a29161451450071745145030a28a2803c8a8a28aee3ca0a28a2801d4514504851451401e8d45145719e8851451401dad1451506a1451450057a28a2b52028a28a007d145140828a28a008e8a28ac8d428a28a00e768a28aa320a28a2803c868a28aee3cd0a28a2802f5145148028a28a00f46a28a2b8cf4428a28a00ed68a28a8350a28a2802bd14515a90145145003e8a28a04145145004745145646a1451450073b45145519051451401e4345145771e6851451401d851451505851451401768a28accd428a28a00eba8a28ae73a828a28a00ad451456a6c145145002d1451540145145003a8a28ac4cc28a28a00e2a8a28ae838428a28a00e1e8a28ad8c428a28a00ee28a28a82c28a28a00bb45145666a1451450075d45145739d41451450056a28a2b5360a28a280168a28aa00a28a2801d4514562661451450071545145741c21451450070f451456c62145145007a251451591a851451401994514522828a28a00eb68a28ac0e90a28a2803ada28a282828a28a009e8a28a0028a28a00e428a28a800a28a2803cde8a28ae939428a28a00cfa28a2b7300a28a2803b9a28a2b2340a28a2803328a28a45051451401d6d1451581d21451450075b45145050514514013d145140051451401c8514515001451450079bd14515d2728514514019f451456e60145145007fffd9, 1, NULL),
	(3, 'mihail.paskov', '12341234', 'mihail.paskov@gmail.com', '0888787272', _binary 0xffd8ffe000104a46494600010200000100010000ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc00011080100010003012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00f10a28a2a46145145005ba28a282c28a28a00f65a28a2b84f4028a28a00ede8a28accd028a28a00e668a28ad4cc28a28a00f0fa28a2bace10a28a28029d14514121451450058a28a29141451450052a28a2acc828a28a00b74514505851451401ecb45145709e8051451401dbd1451599a051451401ccd14515a99851451401e1f45145759c21451450053a28a282428a28a00b14514522828a28a00a3451456c62145145007a5d14515c477851451401ebd45145729d6145145006b514515d260145145005ea28a299214514500788514515230a28a280384a28a2b4310a28a28028d14515a1985145140195451456e73851451401e9745145711de145145007af514515ca75851451401ad4514574980514514017a8a28a64851451401e214514548c28a28a00e128a28ad0c428a28a00a34514568661451450055a28a2b6310a28a2803d6e8a28ae13d00a28a2803728a28a92828a28a00eaa8a28a601451450058a28a290c28a28a00e3a8a28a041451450070b451455121451450070d45145741c81451450045451456c64145145007add14515c27a0145145006e51451525051451401d5514514c028a28a00b14514521851451401c75145140828a28a00e168a28aa2428a28a00e1a8a28ae839028a28a00b145145510145145007a9514515c6778514514009451456c601451450075f4514562740514514009451454941451450072545145741cc1451450056a28a2a0b0a28a2803cb68a28aea38828a28a00b34514521851451401ea545145719de145145002514515b18051451401d7d1451589d014514500251451525051451401c9514515d0730514514015a8a28a82c28a28a00f2da28a2ba8e20a28a2803a5a28a2b3360a28a2803d228a28ae23d30a28a2803bba28a2ac80a28a28032a8a28a448514514010d145140051451401a14514522c28a28a00e2e8a28a0614514500791514515dc794145145006cd1451525851451401e9145145711e9851451401ddd1451564051451401954514522428a28a00868a28a0028a28a00d0a28a29161451450071745145030a28a2803c8a8a28aee3ca0a28a2801d4514504851451401e8d45145719e8851451401dad1451506a1451450057a28a2b52028a28a007d145140828a28a008e8a28ac8d428a28a00e768a28aa320a28a2803c868a28aee3cd0a28a2802f5145148028a28a00f46a28a2b8cf4428a28a00ed68a28a8350a28a2802bd14515a90145145003e8a28a04145145004745145646a1451450073b45145519051451401e4345145771e6851451401d851451505851451401768a28accd428a28a00eba8a28ae73a828a28a00ad451456a6c145145002d1451540145145003a8a28ac4cc28a28a00e2a8a28ae838428a28a00e1e8a28ad8c428a28a00ee28a28a82c28a28a00bb45145666a1451450075d45145739d41451450056a28a2b5360a28a280168a28aa00a28a2801d4514562661451450071545145741c21451450070f451456c62145145007a251451591a851451401994514522828a28a00eb68a28ac0e90a28a2803ada28a282828a28a009e8a28a0028a28a00e428a28a800a28a2803cde8a28ae939428a28a00cfa28a2b7300a28a2803b9a28a2b2340a28a2803328a28a45051451401d6d1451581d21451450075b45145050514514013d145140051451401c8514515001451450079bd14515d2728514514019f451456e60145145007fffd9, NULL, NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

-- Dumping structure for table peerreview.user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_roles_role_uindex` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.user_roles: ~1 rows (approximately)
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` (`id`, `role`) VALUES
	(1, 'Admin');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;

-- Dumping structure for table peerreview.work_items
CREATE TABLE IF NOT EXISTS `work_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(80) NOT NULL,
  `description` text NOT NULL,
  `reviewer_id` int(11) DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `creator_id` int(11) NOT NULL,
  `file` blob DEFAULT NULL,
  `team_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `work_items_creator_fk` (`creator_id`),
  KEY `work_items_reviewer_fk` (`reviewer_id`),
  KEY `work_items_status_fk` (`status_id`),
  KEY `work_items_team_fk` (`team_id`),
  CONSTRAINT `work_items_creator_fk` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`),
  CONSTRAINT `work_items_reviewer_fk` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `work_items_status_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`),
  CONSTRAINT `work_items_team_fk` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table peerreview.work_items: ~0 rows (approximately)
/*!40000 ALTER TABLE `work_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `work_items` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
