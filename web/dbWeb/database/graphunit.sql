-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 07, 2015 at 03:02 AM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `graphunit`
--

-- --------------------------------------------------------

--
-- Table structure for table `amazon_turk`
--

CREATE TABLE IF NOT EXISTS `amazon_turk` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aws_access_key` varchar(70) DEFAULT NULL,
  `aws_secret_key` varchar(70) DEFAULT NULL,
  `hit_title` varchar(45) DEFAULT NULL,
  `no_assigments` int(11) DEFAULT NULL,
  `reward` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `AWS_secret_key_UNIQUE` (`aws_secret_key`),
  UNIQUE KEY `AWS_access_key_UNIQUE` (`aws_access_key`),
  UNIQUE KEY `hit_title_UNIQUE` (`hit_title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `dataset`
--

CREATE TABLE IF NOT EXISTS `dataset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `path` varchar(200) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`,`user_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_dataset_user1_idx` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=97 ;

--
-- Dumping data for table `dataset`
--

INSERT INTO `dataset` (`id`, `name`, `path`, `user_id`, `description`) VALUES
(76, 'Test Dataset 1', NULL, 2, 'asdasd'),
(77, 'Test Dataset 2', NULL, 2, 'asd'),
(78, '123', NULL, 2, '123'),
(79, 'Edit a dataset', NULL, 2, 'Edit'),
(92, '92 Test', NULL, 2, 'asdas'),
(93, '93 Test', NULL, 2, 'asd'),
(94, '92 Test', NULL, 2, 'asdas'),
(96, '92 Test', NULL, 2, 'asdas');

-- --------------------------------------------------------

--
-- Table structure for table `study`
--

CREATE TABLE IF NOT EXISTS `study` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `path` varchar(200) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`,`user_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_study_user1_idx` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `study`
--

INSERT INTO `study` (`id`, `name`, `user_id`, `path`, `description`) VALUES
(1, 'Study sample ', 2, 'Study sample ', NULL),
(2, 'Study sample ', 2, 'Study/sample ', NULL),
(3, 'Study sample ', 2, 'Study/sample ', NULL),
(4, 'Study sample ', 2, 'Study/sample ', NULL),
(5, 'Study sample ', 2, 'Study/sample ', NULL),
(6, 'Study sample ', 2, 'Study/sample ', NULL),
(7, 'Study sample ', 2, 'Study/sample ', NULL),
(9, 'Study sample ', 2, 'Study/sample ', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `task`
--

CREATE TABLE IF NOT EXISTS `task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `path` varchar(45) DEFAULT NULL,
  `description` text,
  `dataset_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`dataset_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_task_dataset1_idx` (`dataset_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `task`
--

INSERT INTO `task` (`id`, `name`, `path`, `description`, `dataset_id`) VALUES
(1, 'asd', NULL, NULL, 96);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password` varchar(45) NOT NULL,
  `amazon_turk_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `fk_user_amazon_turk1_idx` (`amazon_turk_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=114 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `firstName`, `lastName`, `email`, `password`, `amazon_turk_id`) VALUES
(2, 'Dayan', 'Yamin', 'yamindayan@gmail.com', 'dayan', NULL),
(3, 'Dayan', 'Yamin', 'yami2ndayan@gmail.com', 'dayan', NULL),
(4, 'd', 'y', 'a@a.c', 'dayan', NULL),
(10, 'da', 'ya', 'aa@a.ac', 'adayan', NULL),
(12, 'test', 'test', 'test@test.test', '123', NULL),
(13, 'Dayan', 'Yamin', 'root@ddasdasdas.asdas', 'asdas', NULL),
(15, 'asd', 'asd', 'rootasdas@asdas.asdas', 'asdasd', NULL),
(18, 'TALISA', 'asdas', 'rootasdasdas@akldmalsk.cioasnd', 'asd', NULL),
(47, 'test@test.test', 'test@test.test', 'test@test.test2', 'test@test.test', NULL),
(48, 'test@test.test', 'test@test.test', 'test@test.2', 'test@test.test', NULL),
(49, 'test@test.test', 'test@test.test', 'root@asd.asd', 'asddsas', NULL),
(51, 'talisa', 'salvador', 'talisa.s@gmail.com', 'asdas', NULL),
(52, '213', '123', 'root', '123', NULL),
(54, 'asd', 'asd', 'roots', '123', NULL),
(55, 'testcons', 'testcons', 'tes@asdas.as', 'asdasdasdas', NULL),
(58, 'testcons', 'testcons', 'tes@asdas.as2', 'asdasdasdas', NULL),
(59, 'asd', 'asd', 'root@sadasdasd.asd', 'asdasd', NULL),
(60, 'Dayan', 'Yamin', 'yaminda0yan@gmail.com', 'dayan', NULL),
(61, 'Dayan', 'Yamin', 'yamindasdayan@gmail.com', 'b7329686b839c2327a6f2a364e903aa5', NULL),
(62, 'asdasd', 'SAd', 'yamindssayan@gmail.com', 'dayan', NULL),
(64, 'Dayan', 'Yamin', 'yadmindayan@gmail.com', 'dayan', NULL),
(66, '', '', 'yamindayan@gmail.coma', 'dayan', NULL),
(67, '', '', 'yamind2ayan@gmail.com2', 'dayan23', NULL),
(74, '', '', 'yamindayan@gmail.comd', 'dayand', NULL),
(76, '', '', 'yamindayan@gmail.co', 'dayan', NULL),
(77, '', '', 'yaminsdayan@gmail.com', 'dayan', NULL),
(78, '', '', 'yaminsdadayan@gmail.com', 'dayan', NULL),
(79, '', '', 'ydayan@gmail.com', 'dayan', NULL),
(111, '', '', 'tes2t@gmail.com', 'test1', NULL),
(113, '', '', 'test1@gmail.com', 'dayan', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `viewer`
--

CREATE TABLE IF NOT EXISTS `viewer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `path` varchar(200) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`,`user_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_viewer_user1_idx` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=65 ;

--
-- Dumping data for table `viewer`
--

INSERT INTO `viewer` (`id`, `name`, `path`, `user_id`, `description`) VALUES
(26, 'Test Viewer 1', NULL, 2, '123'),
(28, 'Test Viewer 28', NULL, 2, 'asd'),
(30, 'Test Viewer 30', NULL, 2, 'asd'),
(35, 'Test Viewer 35', NULL, 2, '123'),
(36, 'Test Viewer23', NULL, 2, 'Viewer Description23'),
(39, 'Test Viewer', NULL, 2, 'asd'),
(47, 'Test 45', NULL, 2, 'asda'),
(64, 'Test Viewer 64', NULL, 2, 'Description2');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `dataset`
--
ALTER TABLE `dataset`
  ADD CONSTRAINT `fk_dataset_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `study`
--
ALTER TABLE `study`
  ADD CONSTRAINT `fk_study_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `task`
--
ALTER TABLE `task`
  ADD CONSTRAINT `fk_task_dataset1` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `fk_user_amazon_turk1` FOREIGN KEY (`amazon_turk_id`) REFERENCES `amazon_turk` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `viewer`
--
ALTER TABLE `viewer`
  ADD CONSTRAINT `fk_viewer_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
