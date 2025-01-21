-- PostgreSQL SQL Dump
-- Version du serveur PostgreSQL : Adaptée pour PostgreSQL
-- Généré le : lun. 20 jan. 2025 à 14:01

-- Création de la base de données et connexion
CREATE DATABASE "GestionDesAbsences_Uta";
\c "GestionDesAbsences_Uta";

-- Création des tables

-- Table Absence
CREATE TABLE "Absence" (
  "Id" SERIAL PRIMARY KEY,
  "DateAbsence" DATE,
  "Heure" VARCHAR(10),
  "IdEtudiant" INTEGER,
  "IdEnseignant" INTEGER,
  CONSTRAINT fk_etudiant FOREIGN KEY ("IdEtudiant") REFERENCES "Etudiant" ("Id"),
  CONSTRAINT fk_enseignant FOREIGN KEY ("IdEnseignant") REFERENCES "Enseignant" ("Id")
);

-- Table Administrateur
CREATE TABLE "Administrateur" (
  "Id" SERIAL PRIMARY KEY,
  "IdUtilisateur" INTEGER,
  "Téléphone" VARCHAR(20),
  CONSTRAINT fk_utilisateur FOREIGN KEY ("IdUtilisateur") REFERENCES "Utilisateur" ("Id")
);

-- Table Cours
CREATE TABLE "Cours" (
  "Id" SERIAL PRIMARY KEY,
  "IdModule" INTEGER,
  "IdEnseignant" INTEGER,
  CONSTRAINT fk_module FOREIGN KEY ("IdModule") REFERENCES "Module" ("Id"),
  CONSTRAINT fk_enseignant_cours FOREIGN KEY ("IdEnseignant") REFERENCES "Enseignant" ("Id")
);

-- Table Enseignant
CREATE TABLE "Enseignant" (
  "Id" SERIAL PRIMARY KEY,
  "Num_Tel" VARCHAR(20),
  "IdUtilsateur" INTEGER,
  CONSTRAINT fk_utilisateur_enseignant FOREIGN KEY ("IdUtilsateur") REFERENCES "Utilisateur" ("Id")
);

-- Table Etudiant
CREATE TABLE "Etudiant" (
  "Id" SERIAL PRIMARY KEY,
  "NomEtudiant" VARCHAR(50),
  "PrenomEtudiant" VARCHAR(60),
  "Matricule" VARCHAR(20),
  "AdresseEtudiant" TEXT,
  "EmailEtudiant" VARCHAR(60),
  "IdFormation" INTEGER,
  CONSTRAINT fk_formation FOREIGN KEY ("IdFormation") REFERENCES "Formation" ("Id")
);

-- Table Formation
CREATE TABLE "Formation" (
  "Id" SERIAL PRIMARY KEY,
  "Specialite" VARCHAR(60),
  "Niveau" VARCHAR(30)
);

-- Table Module
CREATE TABLE "Module" (
  "Id" SERIAL PRIMARY KEY,
  "NomModule" VARCHAR(60),
  "Coefficient_Module" INTEGER
);

-- Table Utilisateur
CREATE TABLE "Utilisateur" (
  "Id" SERIAL PRIMARY KEY,
  "Nom" VARCHAR(60),
  "Prenom" VARCHAR(60),
  "MotDePasse" VARCHAR(64),
  "NumTel" VARCHAR(20),
  "Adresse" VARCHAR(120),
  "Email" VARCHAR(60),
  "Role" VARCHAR(60)
);

-- Insertion des données

-- Table Administrateur
INSERT INTO "Administrateur" ("IdUtilisateur", "Téléphone") VALUES
(1, NULL);

-- Table Etudiant
INSERT INTO "Etudiant" ("NomEtudiant", "PrenomEtudiant", "Matricule", "AdresseEtudiant", "EmailEtudiant", "IdFormation") VALUES
('kouakou', 'yann', '1212123K', 'Bingerville', 'kyann@gmail.com', NULL),
('yapi', 'aboa', '14115678Z', 'abobo', 'yapi@gmail.com', NULL);

-- Table Utilisateur
INSERT INTO "Utilisateur" ("Nom", "Prenom", "MotDePasse", "NumTel", "Adresse", "Email", "Role") VALUES
('Mr', 'Py', 'py1234', NULL, 'Yopougon', 'py@gmail.com', 'Admin'),
('agoh', 'chris', 'f97366bc05d3fdb27fbf72a024dd8f62973059264e3c1fb2f4e7512d96e649ff', '778748602', 'Adjamé', 'chris@gmail.com', 'Enseignant'),
('kouakou', 'Yann', 'de34ddf5af5bcbda0219a7280880a0b7c6ae7b12885160996fe3effaa67733a3', '89792682', 'Poy', 'kyann@gmail.com', 'Enseignant');
