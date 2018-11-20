-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 127.0.0.1    Database: Disexam
-- ------------------------------------------------------
-- Server version	5.7.21

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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `city` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `zipcode` varchar(45) COLLATE utf8_danish_ci NOT NULL,
  `street_address` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'Henrik Thorn','København','2200','Amagerbrogade'),(2,'ITK','København','2400','Lygten'),(3,'Henrik Thorn','København','2200','Amagerbrogade'),(4,'ITK','København','2400','Lygten'),(5,'Henrik Thorn','København','2200','Amagerbrogade'),(6,'ITK','København','2400','Lygten'),(7,'Henrik Thorn','København','2200','Amagerbrogade'),(8,'ITK','København','2400','Lygten'),(9,'Henrik Thorn','København','2200','Amagerbrogade'),(10,'ITK','København','2400','Lygten'),(11,'Henrik Thorn','København','2200','Amagerbrogade'),(12,'ITK','København','2400','Lygten');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `line_item`
--

DROP TABLE IF EXISTS `line_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `line_item` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(10) unsigned NOT NULL,
  `order_id` int(10) unsigned NOT NULL,
  `price` float NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `line_item`
--

LOCK TABLES `line_item` WRITE;
/*!40000 ALTER TABLE `line_item` DISABLE KEYS */;
INSERT INTO `line_item` VALUES (1,1,1,20,3),(2,2,1,25,1),(3,0,2,20,3),(4,0,2,25,1),(5,1,3,20,3),(6,2,3,25,1),(7,1,4,20,3),(8,2,4,25,1),(9,1,5,20,3),(10,2,5,25,1),(11,1,6,20,3),(12,2,6,25,1);
/*!40000 ALTER TABLE `line_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `billing_address_id` int(10) unsigned NOT NULL,
  `shipping_address_id` int(10) unsigned NOT NULL,
  `order_total` float NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL,
  `updated_at` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,15,1,2,80,1,1),(2,20,3,4,45,1539277496,1539277496),(3,16,5,6,45,1539277742,1539277742),(4,18,7,8,45,1539277791,1539277791),(5,19,9,10,45,1539277902,1539277902),(6,21,11,12,45,1539335661,1539335661);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `sku` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `price` float NOT NULL DEFAULT '0',
  `description` varchar(255) COLLATE utf8_danish_ci DEFAULT NULL,
  `stock` int(11) unsigned NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `sku_UNIQUE` (`sku`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Cola','coke',20,'Coca Cola',100,1),(2,'Fanta','fanta',25,'Fanta',50,2),(3,'null','tasteofturkey',30,'Turkey',500,1541419970);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `last_name` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_danish_ci NOT NULL,
  `created_at` int(11) unsigned NOT NULL,
  `username` varchar(45) COLLATE utf8_danish_ci DEFAULT NULL,
  `token` varchar(100) COLLATE utf8_danish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin1','admin','3dd93646c8053f04aa09c24f475dbc0b1d955f437c09a4be3a3dc3a7aee3f6c6','admin@admin.dk',1541509841,'admin','871f9abf5f9eeb8adac6730a57cf6395a04f5ccbe6e82b6249009a4d6ac8ce1d'),(12,'malthe','jørgensen','fd1bf3d8d45e71dbfeadb1bccadf39ddcc6a95a68b4c9969ab4735c1abfa7026','malthe@cbs.dk',1539335661,'test12',NULL),(14,'null','null','902fbdd2b1df0c4f70b4a5d23525e932','hans@cbs.dk',1540216753,'test14',NULL),(15,'null','null','902fbdd2b1df0c4f70b4a5d23525e932','hans@cbs.dk',1540216799,'test15',NULL),(16,'Hans','Poulsen','902fbdd2b1df0c4f70b4a5d23525e932','hans@cbs.dk',1540216868,'test16',NULL),(19,'Malthe','Jørgensen','92c8e057e3009bd9173b4efe68896446','hans@cbs.dk',1540291672,'test19',NULL),(20,'Malthe','Jørgensen','92c8e057e3009bd9173b4efe68896446','hans@cbs.dk',1540291691,'test20',NULL),(21,'Malthe','Jørgensen','902fbdd2b1df0c4f70b4a5d23525e932','mwj@cbs.dk',1540315297,'test21',NULL),(32,'Malthe','Jørgensen','f3e23d36d5ba886dbbc498d5be10e0ed','mwj@cbs.dk',1540720424,'malthewj',NULL),(34,'Hans','Hansen','hans123','hans@hansen.dk',2,'HansH',NULL),(36,'Pivert','Hansen','hans123','hans@hansen.dk',2,'Pivert12',NULL),(37,'Malthe','Jørgensen','bbb4b6e9556ac8c2a441ea5f143b2d25402ae84acc1c308182672684328112e5','mwj@cbs.dk',1541159347,'malthewejnold',NULL),(40,'Malthe','Jørgensen','3c625e0f0c047164eaa259985cc7be65b3258b31b969f83ccb7790d7b5a3501d','mwj@cbs.dk',1541160417,'malthew',NULL),(43,'Malthe','Jørgensen','cd35af78bf10993dad659ed180fc9cf120d2c1e9747961ca1d6217006a7eaf0a','mwj@cbs.dk',1541430590,'malthewejnold123',NULL),(44,'Malthe','Jørgensen','9dd02f288fa5e9191caee9787a465956987ad2bf5c1cf271cb7b9a8460983c9c','mwj@cbs.dk',1541431395,'malthe',NULL),(45,'Malthe','Jørgensen','2d6627aba69877831a664e41eaed7905bf3d27928ecdfa69abd55db30613c073','mwj@cbs.dk',1541431526,'malt',NULL),(46,'Malthe','Jørgensen','8c80d8042eed9689c0d452fc3da830d646d44e3dc750474522b4da4c4618dcb8','mwj@cbs.dk',1541431861,'test30',NULL),(50,'lars','carsten','29ff7e5fe04af2daccd67bdb219ed3bdb06533fc3d199eedd7af31bd25d0c2fb','henrik',1541603803,'malth','b8c6caa93e693b21263b64f089519dd5'),(51,'Malthe','Wejnold','dcd00cb7cd1c1686572bdfd6b3ff5b1526f7f5d01b829d951494ded8e26d7889','malthe@malthe.dk',1541603867,'malthee',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-20 14:46:01
