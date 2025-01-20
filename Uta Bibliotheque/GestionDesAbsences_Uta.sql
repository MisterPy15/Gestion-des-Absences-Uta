-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : lun. 20 jan. 2025 à 14:01
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
-- Structure de la table `Absence`
--

CREATE TABLE `Absence` (
  `Id` int(11) NOT NULL,
  `DateAbsence` date DEFAULT NULL,
  `Heure` varchar(10) DEFAULT NULL,
  `IdEtudiant` int(11) DEFAULT NULL,
  `IdEnseignant` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `Administrateur`
--

CREATE TABLE `Administrateur` (
  `Id` int(11) NOT NULL,
  `IdUtilisateur` int(11) DEFAULT NULL,
  `Téléphone` int(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `Administrateur`
--

INSERT INTO `Administrateur` (`Id`, `IdUtilisateur`, `Téléphone`) VALUES
(1, 1, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `Cours`
--

CREATE TABLE `Cours` (
  `Id` int(11) NOT NULL,
  `IdModule` int(11) DEFAULT NULL,
  `IdEnseignant` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `Enseignant`
--

CREATE TABLE `Enseignant` (
  `Id` int(11) NOT NULL,
  `Num_Tel` varchar(20) DEFAULT NULL,
  `IdUtilsateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Déchargement des données de la table `Etudiant`
--

INSERT INTO `Etudiant` (`Id`, `NomEtudiant`, `PrenomEtudiant`, `Matricule`, `AdresseEtudiant`, `EmailEtudiant`, `IdFormation`) VALUES
(2, 'kouakou', 'yann', '1212123K', 'Bingerville', 'kyann@gmail.com', NULL),
(3, 'yapi', 'aboa', '14115678Z', 'abobo', 'yapi@gmail.com', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `Formation`
--

CREATE TABLE `Formation` (
  `Id` int(11) NOT NULL,
  `Specialite` varchar(60) DEFAULT NULL,
  `Niveau` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `Module`
--

CREATE TABLE `Module` (
  `Id` int(11) NOT NULL,
  `NomModule` varchar(60) DEFAULT NULL,
  `Coefficient_Module` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `Utilisateur`
--

CREATE TABLE `Utilisateur` (
  `Id` int(11) NOT NULL,
  `Nom` varchar(60) DEFAULT NULL,
  `Prenom` varchar(60) DEFAULT NULL,
  `MotDePasse` varchar(64) DEFAULT NULL,
  `NumTel` int(20) DEFAULT NULL,
  `Adresse` varchar(120) DEFAULT NULL,
  `Email` varchar(60) DEFAULT NULL,
  `Role` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `Utilisateur`
--

INSERT INTO `Utilisateur` (`Id`, `Nom`, `Prenom`, `MotDePasse`, `NumTel`, `Adresse`, `Email`, `Role`) VALUES
(1, 'Mr', 'Py', 'py1234', NULL, 'Yopougon', 'py@gmail.com', 'Admin'),
(3, 'agoh', 'chris', 'f97366bc05d3fdb27fbf72a024dd8f62973059264e3c1fb2f4e7512d96e649ff', 778748602, 'Adjamé', 'chris@gmail.com', 'Enseignant'),
(5, 'kouakou', 'Yann', 'de34ddf5af5bcbda0219a7280880a0b7c6ae7b12885160996fe3effaa67733a3', 89792682, 'Poy', 'kyann@gmail.com', 'Enseignant');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `Absence`
--
ALTER TABLE `Absence`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdEtudiant` (`IdEtudiant`),
  ADD KEY `IdEnseignant` (`IdEnseignant`);

--
-- Index pour la table `Administrateur`
--
ALTER TABLE `Administrateur`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdUtilisateur` (`IdUtilisateur`);

--
-- Index pour la table `Cours`
--
ALTER TABLE `Cours`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdModule` (`IdModule`),
  ADD KEY `IdEnseignant` (`IdEnseignant`);

--
-- Index pour la table `Enseignant`
--
ALTER TABLE `Enseignant`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdUtilsateur` (`IdUtilsateur`);

--
-- Index pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `IdFormation` (`IdFormation`);

--
-- Index pour la table `Formation`
--
ALTER TABLE `Formation`
  ADD PRIMARY KEY (`Id`);

--
-- Index pour la table `Module`
--
ALTER TABLE `Module`
  ADD PRIMARY KEY (`Id`);

--
-- Index pour la table `Utilisateur`
--
ALTER TABLE `Utilisateur`
  ADD PRIMARY KEY (`Id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `Absence`
--
ALTER TABLE `Absence`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `Administrateur`
--
ALTER TABLE `Administrateur`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `Cours`
--
ALTER TABLE `Cours`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `Enseignant`
--
ALTER TABLE `Enseignant`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `Formation`
--
ALTER TABLE `Formation`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `Module`
--
ALTER TABLE `Module`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `Utilisateur`
--
ALTER TABLE `Utilisateur`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `Absence`
--
ALTER TABLE `Absence`
  ADD CONSTRAINT `absence_ibfk_1` FOREIGN KEY (`IdEtudiant`) REFERENCES `Etudiant` (`Id`),
  ADD CONSTRAINT `absence_ibfk_2` FOREIGN KEY (`IdEnseignant`) REFERENCES `Enseignant` (`Id`);

--
-- Contraintes pour la table `Administrateur`
--
ALTER TABLE `Administrateur`
  ADD CONSTRAINT `administrateur_ibfk_1` FOREIGN KEY (`IdUtilisateur`) REFERENCES `Utilisateur` (`Id`);

--
-- Contraintes pour la table `Cours`
--
ALTER TABLE `Cours`
  ADD CONSTRAINT `cours_ibfk_1` FOREIGN KEY (`IdModule`) REFERENCES `Module` (`Id`),
  ADD CONSTRAINT `cours_ibfk_2` FOREIGN KEY (`IdEnseignant`) REFERENCES `Enseignant` (`Id`);

--
-- Contraintes pour la table `Enseignant`
--
ALTER TABLE `Enseignant`
  ADD CONSTRAINT `enseignant_ibfk_1` FOREIGN KEY (`IdUtilsateur`) REFERENCES `Utilisateur` (`Id`);

--
-- Contraintes pour la table `Etudiant`
--
ALTER TABLE `Etudiant`
  ADD CONSTRAINT `etudiant_ibfk_2` FOREIGN KEY (`IdFormation`) REFERENCES `Formation` (`Id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
