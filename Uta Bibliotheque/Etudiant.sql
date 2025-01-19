-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : dim. 19 jan. 2025 à 13:50
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `GestionDesAbsences_Uta`
--

-- --------------------------------------------------------

--
-- Structure de la table `Etudiant`
--

CREATE TABLE `Etudiant` (
  `Id` int(11) NOT NULL,
  `NomEtudiant` varchar(50) DEFAULT NULL,
  `PrenomEtudiant` varchar(60) DEFAULT NULL,
  `Matricule` varchar(20) DEFAULT NULL,
  `AdresseEtudiant` text DEFAULT NULL,
  `EmailEtudiant` varchar(60) DEFAULT NULL,
  `IdFormation` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdFormation` (`IdFormation`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  ADD CONSTRAINT `etudiant_ibfk_2` FOREIGN KEY (`IdFormation`) REFERENCES `Formation` (`Id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
