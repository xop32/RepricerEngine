-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: repricer
-- ------------------------------------------------------
-- Server version	5.5.31-0+wheezy1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `asin_associations`
--

DROP TABLE IF EXISTS `asin_associations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asin_associations` (
  `region` varchar(50) NOT NULL,
  `product_id` varchar(50) NOT NULL,
  `jp_product_id` varchar(50) NOT NULL,
  PRIMARY KEY (`region`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `catalog_missing`
--

DROP TABLE IF EXISTS `catalog_missing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catalog_missing` (
  `sku` varchar(50) NOT NULL DEFAULT '',
  `title` varchar(2000) DEFAULT NULL,
  `artist` varchar(100) DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `asin` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`sku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Catalog table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commands`
--

DROP TABLE IF EXISTS `commands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commands` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `command` varchar(25) DEFAULT NULL,
  `metadata` varchar(250) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2093 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `create_listing_status`
--

DROP TABLE IF EXISTS `create_listing_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `create_listing_status` (
  `region` varchar(50) NOT NULL,
  `stage` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `started_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  PRIMARY KEY (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exchange_rates`
--

DROP TABLE IF EXISTS `exchange_rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchange_rates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_currency` varchar(50) NOT NULL,
  `to_currency` varchar(50) NOT NULL,
  `factor` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feed_submissions`
--

DROP TABLE IF EXISTS `feed_submissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feed_submissions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `feed_file` varchar(100) NOT NULL,
  `amazon_submission_id` varchar(100) NOT NULL,
  `reprice_id` int(15) NOT NULL DEFAULT '0',
  `submitted_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reprice_id` (`reprice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15972 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_feeds`
--

DROP TABLE IF EXISTS `inventory_feeds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory_feeds` (
  `id` int(11) NOT NULL,
  `url` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_items`
--

DROP TABLE IF EXISTS `inventory_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sku` varchar(50) NOT NULL,
  `inventory_id` int(20) NOT NULL,
  `region_product` varchar(50) NOT NULL,
  `product_id` varchar(50) NOT NULL,
  `inventory_region` varchar(50) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` float NOT NULL,
  `item_condition` int(10) NOT NULL,
  `lowest_amazon_price` float DEFAULT NULL,
  `old_quantity` int(11) DEFAULT NULL,
  `old_price` float DEFAULT NULL,
  `obi` bit(1) NOT NULL DEFAULT b'0',
  `is_valid` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inventory_id_region_product` (`inventory_id`,`region_product`),
  KEY `sku` (`sku`),
  KEY `product_id_inventory_region` (`product_id`,`inventory_region`)
) ENGINE=InnoDB AUTO_INCREMENT=57670319 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_items_missing`
--

DROP TABLE IF EXISTS `inventory_items_missing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory_items_missing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sku` varchar(50) NOT NULL,
  `inventory_id` int(20) NOT NULL,
  `region_product` varchar(50) NOT NULL,
  `product_id` varchar(50) NOT NULL,
  `inventory_region` varchar(50) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` float NOT NULL,
  `item_condition` int(10) NOT NULL,
  `lowest_amazon_price` float DEFAULT NULL,
  `old_quantity` int(11) DEFAULT NULL,
  `old_price` float DEFAULT NULL,
  `obi` bit(1) NOT NULL DEFAULT b'0',
  `is_valid` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inventory_id_region_product` (`inventory_id`,`region_product`),
  KEY `sku` (`sku`),
  KEY `product_id_inventory_region` (`product_id`,`inventory_region`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kmd_order_tracking`
--

DROP TABLE IF EXISTS `kmd_order_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kmd_order_tracking` (
  `last_order_tracked_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_order_tracked` int(11) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `latest_inventory`
--

DROP TABLE IF EXISTS `latest_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `latest_inventory` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `region` varchar(10) NOT NULL,
  `inventory_id` int(20) NOT NULL,
  `total_items` int(10) DEFAULT NULL,
  `latest_used_id` int(10) DEFAULT '8000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1 MAX_ROWS=1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listing_configuration`
--

DROP TABLE IF EXISTS `listing_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listing_configuration` (
  `region` varchar(50) NOT NULL,
  `item_note_new` varchar(1000) DEFAULT NULL,
  `item_note_used` varchar(1000) DEFAULT NULL,
  `item_note_obi` varchar(1000) DEFAULT NULL,
  `expedited_shipping` varchar(10) DEFAULT NULL,
  `item_is_marketplace` varchar(10) DEFAULT NULL,
  `will_ship_internationally` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listing_configuration_old`
--

DROP TABLE IF EXISTS `listing_configuration_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listing_configuration_old` (
  `region` varchar(50) NOT NULL,
  `item_note_new` varchar(1000) NOT NULL,
  `item_note_used` varchar(1000) NOT NULL,
  `item_note_obi` varchar(1000) NOT NULL,
  `expedited_shipping` varchar(10) DEFAULT NULL,
  `item_is_marketplace` varchar(10) DEFAULT NULL,
  `will_ship_internationally` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_blacklist`
--

DROP TABLE IF EXISTS `product_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_blacklist` (
  `region` varchar(10) NOT NULL,
  `product_id` varchar(50) NOT NULL,
  `blacklist` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`region`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_details`
--

DROP TABLE IF EXISTS `product_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_details` (
  `product_id` varchar(50) NOT NULL,
  `weight` float DEFAULT NULL,
  `used_quantity` int(11) NOT NULL DEFAULT '-1',
  `used_lowest_price` float NOT NULL DEFAULT '-1',
  `new_quantity` int(11) NOT NULL DEFAULT '-1',
  `new_lowest_price` float NOT NULL DEFAULT '-1',
  `sales_rank` int(11) NOT NULL DEFAULT '-1',
  `last_refreshed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_info`
--

DROP TABLE IF EXISTS `product_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_info` (
  `product_id` varchar(15) NOT NULL,
  `title` varchar(1000) DEFAULT NULL,
  `artist` varchar(1000) DEFAULT NULL,
  `author` varchar(1000) DEFAULT NULL,
  `product_type` varchar(50) DEFAULT NULL,
  `unavailable` bit(1) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_info_backup`
--

DROP TABLE IF EXISTS `product_info_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_info_backup` (
  `product_id` varchar(15) NOT NULL,
  `title` varchar(1000) DEFAULT NULL,
  `artist` varchar(1000) DEFAULT NULL,
  `author` varchar(1000) DEFAULT NULL,
  `product_type` varchar(50) DEFAULT NULL,
  `unavailable` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_stats`
--

DROP TABLE IF EXISTS `product_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_stats` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `product_id` varchar(50) NOT NULL,
  `sales_rank` int(10) NOT NULL DEFAULT '-1',
  `date_added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`,`date_added`)
) ENGINE=InnoDB AUTO_INCREMENT=43614107 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `products_for_review`
--

DROP TABLE IF EXISTS `products_for_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `products_for_review` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `command_id` int(10) NOT NULL,
  `identifier` varchar(50) CHARACTER SET latin1 NOT NULL,
  `asin` varchar(50) CHARACTER SET latin1 NOT NULL,
  `approved` bit(1) DEFAULT NULL,
  `available` bit(1) DEFAULT NULL,
  `title` varchar(1024) DEFAULT NULL,
  `img_url` varchar(2048) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `command_id_identifier_asin` (`command_id`,`identifier`,`asin`),
  KEY `asin` (`asin`),
  KEY `id_command_id` (`id`,`command_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1355 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `python_commands`
--

DROP TABLE IF EXISTS `python_commands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `python_commands` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `command` varchar(50) DEFAULT NULL,
  `metadata` varchar(2048) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `pid` int(10) DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repricer_configuration`
--

DROP TABLE IF EXISTS `repricer_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repricer_configuration` (
  `region` varchar(10) DEFAULT NULL,
  `repricer_status` varchar(15) DEFAULT NULL,
  `formula_id` int(10) NOT NULL,
  `latest_reprice_id` int(10) NOT NULL DEFAULT '-1',
  `repricer_interval` int(10) NOT NULL DEFAULT '-1',
  `next_run` timestamp NULL DEFAULT NULL,
  `marketplace_id` varchar(50) NOT NULL,
  `seller_id` varchar(50) NOT NULL,
  `cache_refresh_interval` int(11) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repricer_formula`
--

DROP TABLE IF EXISTS `repricer_formula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repricer_formula` (
  `formula_id` int(10) NOT NULL AUTO_INCREMENT,
  `quantity_limit` int(10) NOT NULL,
  `new_quantity_limit` int(10) DEFAULT '0',
  `obi_quantity_limit` int(10) DEFAULT NULL,
  `formula` varchar(100) NOT NULL,
  `obi_formula` varchar(100) DEFAULT NULL,
  `default_weight` double NOT NULL DEFAULT '-1',
  `obi_default_weight` double NOT NULL DEFAULT '-1',
  `second_level_repricing` bit(1) NOT NULL,
  `lower_price_marigin` double DEFAULT NULL,
  `lower_limit` double DEFAULT NULL,
  `lower_limit_percent` double DEFAULT NULL,
  `upper_limit` double DEFAULT NULL,
  `upper_limit_percent` double DEFAULT NULL,
  `price_limit_new` int(10) NOT NULL DEFAULT '-1',
  `price_limit_used` int(10) NOT NULL DEFAULT '-1',
  `price_limit_used_obi` int(10) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`formula_id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repricer_reports`
--

DROP TABLE IF EXISTS `repricer_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repricer_reports` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `reprice_id` int(11) NOT NULL DEFAULT '0',
  `price` float NOT NULL,
  `quantity` int(11) NOT NULL,
  `inventory_item_id` int(11) NOT NULL,
  `formula_id` int(11) NOT NULL,
  `audit_trail` varchar(300) NOT NULL,
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_repricer_reports_repricer_status` (`reprice_id`),
  KEY `inventory_item_id_index_reports` (`inventory_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=493039390 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repricer_reports_old`
--

DROP TABLE IF EXISTS `repricer_reports_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repricer_reports_old` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `reprice_id` int(11) NOT NULL DEFAULT '0',
  `price` float NOT NULL,
  `quantity` int(11) NOT NULL,
  `inventory_item_id` int(11) NOT NULL,
  `formula_id` int(11) NOT NULL,
  `audit_trail` varchar(300) NOT NULL,
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_repricer_reports_repricer_status` (`reprice_id`),
  KEY `inventory_item_id_index_reports` (`inventory_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repricer_status`
--

DROP TABLE IF EXISTS `repricer_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repricer_status` (
  `reprice_id` int(11) NOT NULL AUTO_INCREMENT,
  `region` varchar(50) NOT NULL,
  `r_status` varchar(50) NOT NULL,
  `total_scheduled` int(11) NOT NULL DEFAULT '0',
  `total_completed` int(11) NOT NULL DEFAULT '0',
  `total_repriced` int(11) NOT NULL DEFAULT '0',
  `reprice_rate` float NOT NULL DEFAULT '0',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  `elapsed` int(11) NOT NULL DEFAULT '0',
  `quantity_reset_to_zero` int(11) NOT NULL DEFAULT '0',
  `price_up` int(11) NOT NULL DEFAULT '0',
  `price_down` int(11) NOT NULL DEFAULT '0',
  `no_price_change` int(11) NOT NULL DEFAULT '0',
  `lowest_price` int(11) NOT NULL DEFAULT '0',
  `last_repriced_id` int(11) NOT NULL DEFAULT '0',
  `last_repriced` varchar(50) DEFAULT NULL,
  `obi_quantity_reset_to_zero` int(11) NOT NULL DEFAULT '0',
  `obi_price_up` int(11) NOT NULL DEFAULT '0',
  `obi_price_down` int(11) NOT NULL DEFAULT '0',
  `obi_no_price_change` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`reprice_id`),
  KEY `region` (`region`)
) ENGINE=InnoDB AUTO_INCREMENT=1444 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translations`
--

DROP TABLE IF EXISTS `translations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translations` (
  `asin` varchar(50) CHARACTER SET utf8 NOT NULL,
  `title_en` mediumtext CHARACTER SET utf8,
  `artist_en` mediumtext CHARACTER SET utf8,
  `title_jp` mediumtext CHARACTER SET utf8,
  `artist_jp` mediumtext CHARACTER SET utf8,
  PRIMARY KEY (`asin`),
  FULLTEXT KEY `title_jp` (`title_jp`),
  FULLTEXT KEY `artist_jp` (`artist_jp`),
  FULLTEXT KEY `artist_en` (`artist_en`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_inventory_status`
--

DROP TABLE IF EXISTS `upload_inventory_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_inventory_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `region` varchar(10) NOT NULL,
  `file_path` varchar(200) NOT NULL,
  `is_valid` bit(1) NOT NULL DEFAULT b'1',
  `current_status` varchar(30) DEFAULT NULL,
  `reference_id` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-05-21 17:58:59
