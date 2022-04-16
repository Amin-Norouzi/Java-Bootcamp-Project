-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 16, 2022 at 02:20 AM
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
-- Database: `loan_db`
--
CREATE
DATABASE IF NOT EXISTS `loan_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `loan_db`;

-- --------------------------------------------------------

--
-- Table structure for table `loan`
--

DROP TABLE IF EXISTS `loan`;
CREATE TABLE IF NOT EXISTS `loan`
(
    `id`                     bigint NOT NULL AUTO_INCREMENT,
    `amount`                 decimal(10, 0) DEFAULT NULL,
    `installment`            decimal(10, 0) DEFAULT NULL,
    `total_installments`     int            DEFAULT NULL,
    `remaining_installments` int            DEFAULT NULL,
    `rate`                   varchar(255)   DEFAULT NULL,
    `type`                   varchar(255)   DEFAULT NULL,
    `status`                 varchar(255)   DEFAULT NULL,
    `created_at`             date           DEFAULT NULL,
    `account_id`             bigint         DEFAULT NULL,
    `remaining_count`        int            DEFAULT NULL,
    `total_count`            int            DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `loan`
--

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
