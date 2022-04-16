-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 16, 2022 at 02:14 AM
-- Server version: 8.0.26
-- PHP Version: 7.4.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `account_db`
--
CREATE
DATABASE IF NOT EXISTS `account_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `account_db`;

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
CREATE TABLE IF NOT EXISTS `account`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `title`      varchar(255)   DEFAULT NULL,
    `balance`    decimal(10, 0) DEFAULT NULL,
    `status`     varchar(255)   DEFAULT NULL,
    `type`       varchar(255)   DEFAULT NULL,
    `currency`   varchar(255)   DEFAULT NULL,
    `created_at` date           DEFAULT NULL,
    `closed_at`  date           DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `account`
--

-- --------------------------------------------------------

--
-- Table structure for table `customer_ids`
--

DROP TABLE IF EXISTS `customer_ids`;
CREATE TABLE IF NOT EXISTS `customer_ids`
(
    `account_id`  bigint NOT NULL,
    `customer_id` bigint DEFAULT NULL,
    KEY           `FKnlvpskhresia5uguorbh6sij9`(`account_id`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `customer_ids`
--

--
-- Constraints for dumped tables
--

--
-- Constraints for table `customer_ids`
--
ALTER TABLE `customer_ids`
    ADD CONSTRAINT `FKnlvpskhresia5uguorbh6sij9` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
