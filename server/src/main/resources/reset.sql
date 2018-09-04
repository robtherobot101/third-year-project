-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Jul 25, 2018 at 12:21 PM
-- Server version: 5.6.40
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+12:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

--
-- Table structure for table `ADMIN`
--

DROP TABLE IF EXISTS `ADMIN`;
CREATE TABLE IF NOT EXISTS `ADMIN` (
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `name` text,
  `work_address` text,
  `region` text,
  `staff_id` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `CLINICIAN`
--

DROP TABLE IF EXISTS `CLINICIAN`;
CREATE TABLE IF NOT EXISTS `CLINICIAN` (
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `name` text,
  `work_address` text,
  `region` text,
  `staff_id` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `COUNTRIES`
--

DROP TABLE IF EXISTS `COUNTRIES`;
CREATE TABLE IF NOT EXISTS `COUNTRIES` (
  `country` text NOT NULL,
  `valid` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Table structure for table `NZREGIONS`
--

DROP TABLE IF EXISTS `NZREGIONS`;
CREATE TABLE IF NOT EXISTS `NZREGIONS` (
  `name` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `DISEASE`
--

DROP TABLE IF EXISTS `DISEASE`;
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

DROP TABLE IF EXISTS `DONATION_LIST_ITEM`;
CREATE TABLE IF NOT EXISTS `DONATION_LIST_ITEM` (
  `name` text NOT NULL,
  `id` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `timeOfDeath` bigint(20) DEFAULT NULL,
  `expired` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `HISTORY_ITEM`
--

DROP TABLE IF EXISTS `HISTORY_ITEM`;
CREATE TABLE IF NOT EXISTS `HISTORY_ITEM` (
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `action` text NOT NULL,
  `description` text NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `HOSPITAL`
--
DROP TABLE IF EXISTS `HOSPITAL`;
CREATE TABLE IF NOT EXISTS `HOSPITAL` (
  `hospital_id` bigint(20) NOT NULL,
  `name` text NOT NULL,
  `address` text NOT NULL,
  `region` text NOT NULL,
  `city` text NOT NULL,
  `country` text NOT NULL,
  `latitude` float(10,6) NOT NULL,
  `longitude` float(10,6) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `WAITING_LIST_ITEM`
--

DROP TABLE IF EXISTS `PUSH_DEVICE`;
CREATE TABLE IF NOT EXISTS `PUSH_DEVICE` (
  `device_id` varchar(64) NOT NULL,
  `user_id` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `MEDICATION`
--

DROP TABLE IF EXISTS `MEDICATION`;
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

DROP TABLE IF EXISTS `PROCEDURES`;
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
-- Table structure for table `TOKEN`
--

DROP TABLE IF EXISTS `TOKEN`;
CREATE TABLE IF NOT EXISTS `TOKEN` (
  `id` int(11) NOT NULL,
  `token` text NOT NULL,
  `access_level` int(11) NOT NULL,
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `WAITING_LIST_ITEM`
--

DROP TABLE IF EXISTS `WAITING_LIST_ITEM`;
CREATE TABLE IF NOT EXISTS `WAITING_LIST_ITEM` (
  `organ_type` text NOT NULL,
  `organ_registered_date` date NOT NULL,
  `organ_deregistered_date` date DEFAULT NULL,
  `id` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `deregistered_code` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
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
  `date_of_death` timestamp NULL DEFAULT NULL,
  `cityOfDeath` text,
  `regionOfDeath` text,
  `countryOfDeath` text,
  `country` text,
  `profile_image_type` varchar(4) DEFAULT NULL
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
-- Indexes for table `HOSPITAL`
--
ALTER TABLE `HOSPITAL`
  ADD PRIMARY KEY (`hospital_id`);

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
-- Indexes for table `WAITING_LIST_ITEM`
--
ALTER TABLE `PUSH_DEVICE`
  ADD PRIMARY KEY (`device_id`),
  ADD KEY `User_id_foreign_key6` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ADMIN`
--
ALTER TABLE `ADMIN`
  MODIFY `staff_id` bigint(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `CLINICIAN`
--
ALTER TABLE `CLINICIAN`
  MODIFY `staff_id` bigint(11) NOT NULL AUTO_INCREMENT;
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
-- AUTO_INCREMENT for table `HOSPITAL`
--
ALTER TABLE `HOSPITAL`
  MODIFY `hospital_id` bigint(20) NOT NULL AUTO_INCREMENT;
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
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT;
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
  ADD CONSTRAINT `User_id_foreign_key2` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `HISTORY_ITEM`
--
ALTER TABLE `HISTORY_ITEM`
  ADD CONSTRAINT `HISTORY_ITEM_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `MEDICATION`
--
ALTER TABLE `MEDICATION`
  ADD CONSTRAINT `User_id_foreign_key3` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `PROCEDURES`
--
ALTER TABLE `PROCEDURES`
  ADD CONSTRAINT `User_id_foreign_key4` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `WAITING_LIST_ITEM`
--
ALTER TABLE `WAITING_LIST_ITEM`
  ADD CONSTRAINT `User_id_foreign_key5` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;


--
-- Constraints for table `PUSH_DEVICE`
--
ALTER TABLE `PUSH_DEVICE`
  ADD CONSTRAINT `User_id_foreign_key6` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE CASCADE;

--
-- Create default clinician and admin
--

INSERT INTO ADMIN(username, password, name, work_address, region, staff_id) VALUES('admin', 'default', 'default', 'default', 'default', 'default');

INSERT INTO TOKEN(id, token, access_level) VALUES(0, 'masterToken', 2);

INSERT INTO CLINICIAN(username, password, name, work_address, region, staff_id) VALUES('default', 'default', 'default', 'default', 'default', 'default');


--
-- Populate country and region tables
--

INSERT INTO `COUNTRIES`(`country`) VALUES ('Afghanistan'), ('Albania'), ('Algeria'), ('Andorra'), ('Angola'), ('Antigua & Deps'), ('Argentina'), ('Armenia'), ('Australia'), ('Austria'), ('Azerbaijan'), ('Bahamas'), ('Bahrain'), ('Bangladesh'), ('Barbados'), ('Belarus'), ('Belgium'), ('Belize'), ('Benin'), ('Bhutan'), ('Bolivia'), ('Bosnia Herzegovina'), ('Botswana'), ('Brazil'), ('Brunei'), ('Bulgaria'), ('Burkina'), ('Burundi'), ('Cambodia'), ('Cameroon'), ('Canada'), ('Cape Verde'), ('Central African Rep'), ('Chad'), ('Chile'), ('China'), ('Colombia'), ('Comoros'), ('Congo'), ('Congo {Democratic Rep}'), ('Costa Rica'), ('Croatia'), ('Cuba'), ('Cyprus'), ('Czech Republic'), ('Denmark'), ('Djibouti'), ('Dominica'), ('Dominican Republic'), ('East Timor'), ('Ecuador'), ('Egypt'), ('El Salvador'), ('Equatorial Guinea'), ('Eritrea'), ('Estonia'), ('Ethiopia'), ('Fiji'), ('Finland'), ('France'), ('Gabon'), ('Gambia'), ('Georgia'), ('Germany'), ('Ghana'), ('Greece'), ('Grenada'), ('Guatemala'), ('Guinea'), ('Guinea-Bissau'), ('Guyana'), ('Haiti'), ('Honduras'), ('Hungary'), ('Iceland'), ('India'), ('Indonesia'), ('Iran'), ('Iraq'), ('Ireland {Republic}'), ('Israel'), ('Italy'), ('Ivory Coast'), ('Jamaica'), ('Japan'), ('Jordan'), ('Kazakhstan'), ('Kenya'), ('Kiribati'), ('Korea North'), ('Korea South'), ('Kosovo'), ('Kuwait'), ('Kyrgyzstan'), ('Laos'), ('Latvia'), ('Lebanon'), ('Lesotho'), ('Liberia'), ('Libya'), ('Liechtenstein'), ('Lithuania'), ('Luxembourg'), ('Macedonia'), ('Madagascar'), ('Malawi'), ('Malaysia'), ('Maldives'), ('Mali'), ('Malta'), ('Marshall Islands'), ('Mauritania'), ('Mauritius'), ('Mexico'), ('Micronesia'), ('Moldova'), ('Monaco'), ('Mongolia'), ('Montenegro'), ('Morocco'), ('Mozambique'), ('Myanmar'), ('Namibia'), ('Nauru'), ('Nepal'), ('Netherlands'), ('New Zealand'), ('Nicaragua'), ('Niger'), ('Nigeria'), ('Norway'), ('Oman'), ('Pakistan'), ('Palau'), ('Panama'), ('Papua New Guinea'), ('Paraguay'), ('Peru'), ('Philippines'), ('Poland'), ('Portugal'), ('Qatar'), ('Romania'), ('Russia'), ('Rwanda'), ('St Kitts & Nevis'), ('St Lucia'), ('Saint Vincent & the Grenadines'), ('Samoa'), ('San Marino'), ('Sao Tome & Principe'), ('Saudi Arabia'), ('Senegal'), ('Serbia'), ('Seychelles'), ('Sierra Leone'), ('Singapore'), ('Slovakia'), ('Slovenia'), ('Solomon Islands'), ('Somalia'), ('South Africa'), ('South Sudan'), ('Spain'), ('Sri Lanka'), ('Sudan'), ('Suriname'), ('Swaziland'), ('Sweden'), ('Switzerland'), ('Syria'), ('Taiwan'), ('Tajikistan'), ('Tanzania'), ('Thailand'), ('Togo'), ('Tonga'), ('Trinidad & Tobago'), ('Tunisia'), ('Turkey'), ('Turkmenistan'), ('Tuvalu'), ('Uganda'), ('Ukraine'), ('United Arab Emirates'), ('United Kingdom'), ('United States'), ('Uruguay'), ('Uzbekistan'), ('Vanuatu'), ('Vatican City'), ('Venezuela'), ('Vietnam'), ('Yemen'), ('Zambia'), ('Zimbabwe');

UPDATE `COUNTRIES` SET `valid`=1 WHERE country='New Zealand';

INSERT INTO `NZREGIONS`(`NAME`) VALUES ('Northland'),('Auckland'),('Waikato'),('Bay of Plenty'),('Gisborne'),('Hawke''s Bay'),('Taranaki'),('Manawatu-Wanganui'),('Wellington'),('Tasman'),('Nelson'),('Marlborough'),('West Coast'),('Canterbury'),('Otago'),('Southland');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
