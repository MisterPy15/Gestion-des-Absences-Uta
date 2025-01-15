# Liste des Vues

## vue_absences_etudiant

### Utilisation:
- Pour obtenir les absences d'un étudiant spécifique:
- `SELECT * FROM vue_absences_etudiant WHERE id_etudiant = ?`
* `Remplacer `?` par l'identifiant de l'étudiant`
-  Pour afficher toutes les absences de tous les étudiants :
* `SELECT * FROM vue_absences_etudiant`


## vue_absences_enseignant
### Utilisation:

- Pour obtenir les absences des cours gérés par un enseignant spécifique 
(par exemple, avec id_enseignant = 3) :

* `SELECT * FROM vue_absences_enseignant WHERE id_enseignant = 3`

- Pour afficher toutes les absences gérées par tous les enseignants :
* `SELECT * FROM vue_absences_enseignant`


## Liste des absences par classe
### Utilisation:
Pour afficher toutes les absences des étudiants dans une classe spécifique 
(par exemple, avec id_classe = 2) :

* `SELECT * FROM vue_absences_classe WHERE id_classe = 2`

- Pour afficher toutes les absences par classe sans filtre :

* `SELECT * FROM vue_absences_classe`
