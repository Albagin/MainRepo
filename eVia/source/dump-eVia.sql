-- MariaDB dump 10.17  Distrib 10.4.11-MariaDB, for Linux (armv7l)
--
-- Host: localhost    Database: eVia
-- ------------------------------------------------------
-- Server version	10.4.11-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `car`
--

DROP TABLE IF EXISTS `car`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `car` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Registration` varchar(45) DEFAULT NULL,
  `Brand` varchar(45) DEFAULT NULL,
  `Color` varchar(45) DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_car_user1_idx` (`user_ID`),
  CONSTRAINT `fk_car_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car`
--

LOCK TABLES `car` WRITE;
/*!40000 ALTER TABLE `car` DISABLE KEYS */;
/*!40000 ALTER TABLE `car` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `StartPointLat` double DEFAULT NULL,
  `StartPointLng` double DEFAULT NULL,
  `LastPointLat` double DEFAULT NULL,
  `LastPointLng` double DEFAULT NULL,
  `StartTime` datetime DEFAULT NULL,
  `LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `directions`
--

DROP TABLE IF EXISTS `directions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `directions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Time` datetime DEFAULT NULL,
  `Direction` varchar(2) DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_directions_user1_idx` (`user_ID`),
  CONSTRAINT `fk_directions_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `directions`
--

LOCK TABLES `directions` WRITE;
/*!40000 ALTER TABLE `directions` DISABLE KEYS */;
/*!40000 ALTER TABLE `directions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driverPosition`
--

DROP TABLE IF EXISTS `driverPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `driverPosition` (
  `ID` int(11) NOT NULL,
  `PointLat` double DEFAULT NULL,
  `PointLng` double DEFAULT NULL,
  `Time` datetime DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_driverPosition_user1_idx` (`user_ID`),
  CONSTRAINT `fk_driverPosition_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driverPosition`
--

LOCK TABLES `driverPosition` WRITE;
/*!40000 ALTER TABLE `driverPosition` DISABLE KEYS */;
INSERT INTO `driverPosition` VALUES (1,53.010104,18.594673,'2020-01-02 18:19:52',0,1);
/*!40000 ALTER TABLE `driverPosition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `path`
--

DROP TABLE IF EXISTS `path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `path` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `user_ID_kierowca` int(11) NOT NULL,
  `user_ID_pasazer` int(11) NOT NULL,
  `course_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_path_user1_idx` (`user_ID_kierowca`),
  KEY `fk_path_user2_idx` (`user_ID_pasazer`),
  KEY `fk_path_course1_idx` (`course_ID`),
  CONSTRAINT `fk_path_course1` FOREIGN KEY (`course_ID`) REFERENCES `course` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_path_user1` FOREIGN KEY (`user_ID_kierowca`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_path_user2` FOREIGN KEY (`user_ID_pasazer`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `path`
--

LOCK TABLES `path` WRITE;
/*!40000 ALTER TABLE `path` DISABLE KEYS */;
/*!40000 ALTER TABLE `path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `questions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Role` varchar(10) DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  `requests_ID` int(11) NOT NULL,
  `Answer` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_questions_user1_idx` (`user_ID`),
  KEY `fk_questions_requests1_idx` (`requests_ID`),
  CONSTRAINT `fk_questions_requests1` FOREIGN KEY (`requests_ID`) REFERENCES `requests` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_questions_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `requests` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `StartPointLat` double DEFAULT NULL,
  `StartPointLng` double DEFAULT NULL,
  `Time` datetime DEFAULT NULL,
  `DestPointLat` double DEFAULT NULL,
  `DestPointLng` double DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  `Status` varchar(10) DEFAULT NULL,
  `Direction` double DEFAULT 0,
  PRIMARY KEY (`ID`),
  KEY `fk_requests_user1_idx` (`user_ID`),
  CONSTRAINT `fk_requests_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

LOCK TABLES `requests` WRITE;
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scores`
--

DROP TABLE IF EXISTS `scores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scores` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Score` varchar(45) DEFAULT NULL,
  `Role` tinyint(4) DEFAULT NULL,
  `user_ID` int(11) NOT NULL,
  `course_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_scores_user1_idx` (`user_ID`),
  KEY `fk_scores_course1_idx` (`course_ID`),
  CONSTRAINT `fk_scores_course1` FOREIGN KEY (`course_ID`) REFERENCES `course` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_scores_user1` FOREIGN KEY (`user_ID`) REFERENCES `user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scores`
--

LOCK TABLES `scores` WRITE;
/*!40000 ALTER TABLE `scores` DISABLE KEYS */;
/*!40000 ALTER TABLE `scores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `Surname` varchar(45) DEFAULT NULL,
  `Email` varchar(45) DEFAULT NULL,
  `Login` varchar(45) DEFAULT NULL,
  `LastUserType` varchar(1) DEFAULT NULL,
  `Admin` tinyint(1) DEFAULT NULL,
  `Ranking` int(11) DEFAULT NULL,
  `Podwozki_kierowca` int(11) DEFAULT NULL,
  `Podwozki_pasazer` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Jan','Kowalski','jk@example.com','jk','D',0,4,4,3);
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

-- Dump completed on 2020-01-06 11:23:59
