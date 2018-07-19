-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Jul 19, 2018 at 01:24 PM
-- Server version: 5.6.40
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `seng302-2018-team300-test`
--

-- --------------------------------------------------------

DROP TABLE IF EXISTS `ADMIN`;
DROP TABLE IF EXISTS `CLINICIAN`;
DROP TABLE IF EXISTS `DISEASE`;
DROP TABLE IF EXISTS `DONATIION_LIST_ITEM`;
DROP TABLE IF EXISTS `HISTORY_ITEM`;
DROP TABLE IF EXISTS `MEDICATION`;
DROP TABLE IF EXISTS `PROCEDURE`;
DROP TABLE IF EXISTS `USER`;
DROP TABLE IF EXISTS `WAITING_LIST_ITEM`;

--
-- Table structure for table `ADMIN`
--

CREATE TABLE IF NOT EXISTS `ADMIN` (
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `name` text,
  `work_address` text,
  `region` text,
  `staff_id` bigint(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ADMIN`
--

INSERT INTO `ADMIN` (`username`, `password`, `name`, `work_address`, `region`, `staff_id`) VALUES
('admin', 'default', 'default', 'default', 'default', 1);

-- --------------------------------------------------------

--
-- Table structure for table `CLINICIAN`
--

CREATE TABLE IF NOT EXISTS `CLINICIAN` (
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `name` text,
  `work_address` text,
  `region` text,
  `staff_id` bigint(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `CLINICIAN`
--

INSERT INTO `CLINICIAN` (`username`, `password`, `name`, `work_address`, `region`, `staff_id`) VALUES
('default', 'default', 'default', 'default', 'default', 1);

-- --------------------------------------------------------

--
-- Table structure for table `DISEASE`
--

CREATE TABLE IF NOT EXISTS `DISEASE` (
  `name` text NOT NULL,
  `diagnosis_date` date NOT NULL,
  `is_cured` tinyint(1) NOT NULL,
  `is_chronic` tinyint(1) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `DONATION_LIST_ITEM`
--

CREATE TABLE IF NOT EXISTS `DONATION_LIST_ITEM` (
  `name` text NOT NULL,
  `id` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `HISTORY_ITEM`
--

CREATE TABLE IF NOT EXISTS `HISTORY_ITEM` (
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `action` text NOT NULL,
  `description` text NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `MEDICATION`
--

CREATE TABLE IF NOT EXISTS `MEDICATION` (
  `name` text NOT NULL,
  `active_ingredients` text NOT NULL,
  `history` text NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `PROCEDURES`
--

CREATE TABLE IF NOT EXISTS `PROCEDURES` (
  `summary` text NOT NULL,
  `description` text NOT NULL,
  `date` date NOT NULL,
  `organs_affected` text,
  `user_id` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `USER`
--

CREATE TABLE IF NOT EXISTS `USER` (
  `first_name` text NOT NULL,
  `middle_names` text,
  `last_name` text,
  `preferred_name` text,
  `preferred_middle_names` text,
  `preferred_last_name` text,
  `creation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `gender` text,
  `gender_identity` text,
  `height` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `blood_type` text,
  `id` bigint(11) NOT NULL,
  `current_address` text,
  `region` text,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` text NOT NULL,
  `blood_pressure` text,
  `smoker_status` text,
  `alcohol_consumption` text,
  `date_of_birth` date NOT NULL,
  `date_of_death` date DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `USER`
--

INSERT INTO `USER` (`first_name`, `middle_names`, `last_name`, `preferred_name`, `preferred_middle_names`, `preferred_last_name`, `creation_time`, `last_modified`, `gender`, `gender_identity`, `height`, `weight`, `blood_type`, `id`, `current_address`, `region`, `username`, `email`, `password`, `blood_pressure`, `smoker_status`, `alcohol_consumption`, `date_of_birth`, `date_of_death`) VALUES
('Andy', 'Robert', 'French', 'Andy', 'Robert', 'French', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 'andy', 'andy@andy.com', 'andrew', NULL, NULL, NULL, '2018-07-19', NULL),
('Buzz', 'Buzzy', 'Knight', 'Buzz', 'Buzzy', 'Knight', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, 'buzz', 'buzz@buzz.com', 'drowssap', NULL, NULL, NULL, '2018-07-19', NULL),
('James', 'Mozza', 'Morritt', 'James', 'Mozza', 'Morritt', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 3, NULL, NULL, 'mozza', 'mozza@mozza.com', 'mozza', NULL, NULL, NULL, '2018-07-19', NULL),
('Jono', 'Zilla', 'Hills', 'Jono', 'Zilla', 'Hills', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 4, NULL, NULL, 'jonozilla', 'zilla@zilla.com', 'zilla', NULL, NULL, NULL, '2018-07-19', NULL),
('James', 'Mackas', 'Mackay', 'James', 'Mackas', 'Mackay', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 5, NULL, NULL, 'mackas', 'mackas@mackas.com', 'mackas', NULL, NULL, NULL, '2018-07-19', NULL),
('Nicky', 'The Dark Horse', 'Zohrab-Henricks', 'Nicky', 'The Dark Horse', 'Zohrab-Henricks', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 6, NULL, NULL, 'nicky', 'nicky@nicky.com', 'nicky', NULL, NULL, NULL, '2018-07-19', NULL),
('Kyran', 'Playing Fortnite', 'Stagg', 'Kyran', 'Playing Fortnite', 'Stagg', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 7, NULL, NULL, 'kyran', 'kyran@kyran.com', 'fortnite', NULL, NULL, NULL, '2018-07-19', NULL),
('Andrew', 'Daveo', 'Davidson', 'Andrew', 'Daveo', 'Davidson', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, 8, NULL, NULL, 'andrew', 'andrew@andrew.com', 'andrew', NULL, NULL, NULL, '2018-07-19', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `WAITING_LIST_ITEM`
--

CREATE TABLE IF NOT EXISTS `WAITING_LIST_ITEM` (
  `organ_type` text NOT NULL,
  `organ_registered_date` date NOT NULL,
  `organ_deregistered_date` date DEFAULT NULL,
  `id` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `deregistered_code` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ADMIN`
--
ALTER TABLE `ADMIN`
  ADD PRIMARY KEY (`staff_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `CLINICIAN`
--
ALTER TABLE `CLINICIAN`
  ADD PRIMARY KEY (`staff_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `DISEASE`
--
ALTER TABLE `DISEASE`
  ADD PRIMARY KEY (`id`),
  ADD KEY `User_id_foreign_key1` (`user_id`);

--
-- Indexes for table `DONATION_LIST_ITEM`
--
ALTER TABLE `DONATION_LIST_ITEM`
  ADD PRIMARY KEY (`id`),
  ADD KEY `User_id_foreign_key2` (`user_id`);

--
-- Indexes for table `HISTORY_ITEM`
--
ALTER TABLE `HISTORY_ITEM`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `MEDICATION`
--
ALTER TABLE `MEDICATION`
  ADD PRIMARY KEY (`id`),
  ADD KEY `User_id_foreign_key3` (`user_id`);

--
-- Indexes for table `PROCEDURES`
--
ALTER TABLE `PROCEDURES`
  ADD PRIMARY KEY (`id`),
  ADD KEY `User_id_foreign_key4` (`user_id`);

--
-- Indexes for table `USER`
--
ALTER TABLE `USER`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `WAITING_LIST_ITEM`
--
ALTER TABLE `WAITING_LIST_ITEM`
  ADD PRIMARY KEY (`id`),
  ADD KEY `User_id_foreign_key5` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ADMIN`
--
ALTER TABLE `ADMIN`
  MODIFY `staff_id` bigint(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `CLINICIAN`
--
ALTER TABLE `CLINICIAN`
  MODIFY `staff_id` bigint(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `DISEASE`
--
ALTER TABLE `DISEASE`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `DONATION_LIST_ITEM`
--
ALTER TABLE `DONATION_LIST_ITEM`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `HISTORY_ITEM`
--
ALTER TABLE `HISTORY_ITEM`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `MEDICATION`
--
ALTER TABLE `MEDICATION`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `PROCEDURES`
--
ALTER TABLE `PROCEDURES`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `USER`
--
ALTER TABLE `USER`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `WAITING_LIST_ITEM`
--
ALTER TABLE `WAITING_LIST_ITEM`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `DISEASE`
--
ALTER TABLE `DISEASE`
  ADD CONSTRAINT `User_id_foreign_key1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `DONATION_LIST_ITEM`
--
ALTER TABLE `DONATION_LIST_ITEM`
  ADD CONSTRAINT `User_id_foreign_key2` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`);

--
-- Constraints for table `HISTORY_ITEM`
--
ALTER TABLE `HISTORY_ITEM`
  ADD CONSTRAINT `HISTORY_ITEM_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`);

--
-- Constraints for table `MEDICATION`
--
ALTER TABLE `MEDICATION`
  ADD CONSTRAINT `User_id_foreign_key3` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`);

--
-- Constraints for table `PROCEDURES`
--
ALTER TABLE `PROCEDURES`
  ADD CONSTRAINT `User_id_foreign_key4` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`);

--
-- Constraints for table `WAITING_LIST_ITEM`
--
ALTER TABLE `WAITING_LIST_ITEM`
  ADD CONSTRAINT `User_id_foreign_key5` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
