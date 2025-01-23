import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminsForm extends JFrame {
    private JTextField tfNom;
    private JTextField tfEmail;
    private JButton btnRecherche;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JTable table1;
    private JPanel AdminPanel;
    private JTextField tfRecherche;
    private JTextField tfPrenom;
    private JTextField tfMotdepasse;
    private JTextField tfNumTel;
    private JTextField tfAdresse;
<<<<<<< HEAD
    private JScrollPane Administrateurs;
=======
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
    private Connection con;
    private PreparedStatement pst;

    public AdminsForm() {
        setTitle("Gestion Des Admins");
        setContentPane(AdminPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        btnViderChamps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();
            }
        });
        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAdmin();
            }
        });
        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyAdmin();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAdmin();
            }
        });
        btnRecherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAdmin();
            }
        });
    }


    public void connect() {
        try {
<<<<<<< HEAD
            // Charger le pilote PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Connexion à la base de données
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GestionDesAbsences_Uta", "postgres", "29122003");
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Pilote JDBC non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !", "Erreur", JOptionPane.ERROR_MESSAGE);
=======
<<<<<<< HEAD
            Class.forName("com.postgresql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GestionDesAbsences_Uta", "postgres", "29122003");
            System.out.println("Connexion réussie");
        } catch (ClassNotFoundException | SQLException ex) {
=======
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Succès");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
>>>>>>> 62ee3580e66de9daf68d5045cd4be9f1180ffef8
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            ex.printStackTrace();
        }
    }

    private void table_load() {
        try {
<<<<<<< HEAD
            String query = "SELECT a.Id, u.Nom, u.Prenom, u.Email, u.NumTel, u.Adresse FROM Administrateur a JOIN Utilisateur u ON a.IdUtilisateur = u.Id";
            pst = con.prepareStatement(query);
=======
            pst = con.prepareStatement("SELECT * FROM Administrateur a JOIN Utilisateur u ON a.IdUtilisateur = u.Id");
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ViderChamps() {
        tfNom.setText("");
        tfPrenom.setText("");
        tfMotdepasse.setText("");
        tfNumTel.setText("");
        tfAdresse.setText("");
        tfEmail.setText("");
        tfRecherche.setText("");
        table_load();
    }

    private void createAdmin() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String motDePasse = tfMotdepasse.getText();
        String numTel = tfNumTel.getText();
        String adresse = tfAdresse.getText();
        String email = tfEmail.getText();

        if (nom.isEmpty() || prenom.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty() || adresse.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
<<<<<<< HEAD
            // Insertion dans la table Utilisateur
            String queryUtilisateur = "INSERT INTO Utilisateur (Nom, Prenom, MotDePasse, NumTel, Adresse, email, role) VALUES (?, ?, ?, ?, ?, ?, 'Admin')";
=======
            String queryUtilisateur = "INSERT INTO Utilisateur (Nom, Prenom, MotDePasse, NumTel, Adresse, Email, Role) VALUES (?, ?, ?, ?, ?, ?, 'Admin')";
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst = con.prepareStatement(queryUtilisateur, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, motDePasse);
            pst.setString(4, numTel);
            pst.setString(5, adresse);
            pst.setString(6, email);
            pst.executeUpdate();

<<<<<<< HEAD
            // Récupération de l'ID de l'utilisateur inséré
=======
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int utilisateurId = generatedKeys.getInt(1);

<<<<<<< HEAD
                // Convertir le numéro de téléphone en long (bigint)
                long numTelLong = Long.parseLong(numTel);

                // Insertion dans la table Administrateur
                String queryAdmin = "INSERT INTO Administrateur (idutilisateur, telephone) VALUES (?, ?)";
                pst = con.prepareStatement(queryAdmin);
                pst.setInt(1, utilisateurId);
                pst.setLong(2, numTelLong); // Insérer le numéro en tant que bigint
=======
                String queryAdmin = "INSERT INTO Administrateur (IdUtilisateur, Téléphone) VALUES (?, ?)";
                pst = con.prepareStatement(queryAdmin);
                pst.setInt(1, utilisateurId);
                pst.setString(2, numTel);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Admin Créé", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
<<<<<<< HEAD
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le numéro de téléphone doit être un nombre valide", "Erreur", JOptionPane.ERROR_MESSAGE);
=======
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
        }
    }

    private void modifyAdmin() {
<<<<<<< HEAD
        String email = tfEmail.getText();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp entrez l'email de l'administrateur à modifier", "Attention", JOptionPane.ERROR_MESSAGE);
=======
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un admin dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            return;
        }

        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String motDePasse = tfMotdepasse.getText();
        String numTel = tfNumTel.getText();
        String adresse = tfAdresse.getText();
<<<<<<< HEAD

        if (nom.isEmpty() || prenom.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty() || adresse.isEmpty()) {
=======
        String email = tfEmail.getText();
        int adminId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        if (nom.isEmpty() || prenom.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty() || adresse.isEmpty() || email.isEmpty()) {
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            JOptionPane.showMessageDialog(this, "Svp remplissez tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
<<<<<<< HEAD
            // Vérifier si l'administrateur existe avec cet email
            String queryUtilisateur = "SELECT u.Id FROM Utilisateur u WHERE u.email = ?";
            pst = con.prepareStatement(queryUtilisateur);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int utilisateurId = rs.getInt("Id");

                // Mise à jour de l'utilisateur
                String updateUtilisateur = "UPDATE Utilisateur SET Nom = ?, Prenom = ?, MotDePasse = ?, NumTel = ?, Adresse = ? WHERE Id = ?";
                pst = con.prepareStatement(updateUtilisateur);
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, motDePasse);
                pst.setString(4, numTel);
                pst.setString(5, adresse);
                pst.setInt(6, utilisateurId);
                pst.executeUpdate();

                // Mise à jour de l'administrateur
                String updateAdmin = "UPDATE Administrateur SET telephone = ? WHERE idutilisateur = ?";
                pst = con.prepareStatement(updateAdmin);
                pst.setString(1, numTel);
                pst.setInt(2, utilisateurId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Admin modifié", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun administrateur trouvé avec cet email", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteAdmin() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un admin dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int adminId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        try {
            String queryAdmin = "DELETE FROM Administrateur WHERE Id = ?";
            pst = con.prepareStatement(queryAdmin);
            pst.setInt(1, adminId);
            pst.executeUpdate();

            String queryUtilisateur = "DELETE FROM Utilisateur WHERE Id = (SELECT idutilisateur FROM Administrateur WHERE Id = ?)";
            pst = con.prepareStatement(queryUtilisateur);
            pst.setInt(1, adminId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
=======
            String queryUtilisateur = "UPDATE Utilisateur SET Nom = ?, Prenom = ?, MotDePasse = ?, NumTel = ?, Adresse = ?, Email = ? WHERE Id = (SELECT IdUtilisateur FROM Administrateur WHERE Id = ?)";
            pst = con.prepareStatement(queryUtilisateur);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, motDePasse);
            pst.setString(4, numTel);
            pst.setString(5, adresse);
            pst.setString(6, email);
            pst.setInt(7, adminId);
            pst.executeUpdate();

            String queryAdmin = "UPDATE Administrateur SET Téléphone = ? WHERE Id = ?";
            pst = con.prepareStatement(queryAdmin);
            pst.setString(1, numTel);
            pst.setInt(2, adminId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin modifié", "Succès", JOptionPane.INFORMATION_MESSAGE);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

<<<<<<< HEAD
=======
    private void deleteAdmin() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un admin dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int adminId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        try {
            String queryAdmin = "DELETE FROM Administrateur WHERE Id = ?";
            pst = con.prepareStatement(queryAdmin);
            pst.setInt(1, adminId);
            pst.executeUpdate();

            String queryUtilisateur = "DELETE FROM Utilisateur WHERE Id = (SELECT IdUtilisateur FROM Administrateur WHERE Id = ?)";
            pst = con.prepareStatement(queryUtilisateur);
            pst.setInt(1, adminId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
    private void searchAdmin() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un texte pour la recherche", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
<<<<<<< HEAD
            String query = "SELECT a.Id, u.Nom, u.Prenom, u.Email, u.NumTel, u.Adresse FROM Administrateur a JOIN Utilisateur u ON a.idutilisateur = u.Id WHERE u.email LIKE ?";
=======
            String query = "SELECT * FROM Administrateur a JOIN Utilisateur u ON a.IdUtilisateur = u.Id WHERE u.Nom LIKE ? OR u.Prenom LIKE ?";
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfNom.setText(rs.getString("Nom"));
                tfPrenom.setText(rs.getString("Prenom"));
<<<<<<< HEAD
                tfNumTel.setText(rs.getString("NumTel"));
                tfAdresse.setText(rs.getString("Adresse"));
                tfEmail.setText(rs.getString("Email"));
                tfMotdepasse.setText(""); // Ne pas remplir le champ mot de passe
=======
                tfMotdepasse.setText(rs.getString("MotDePasse"));
                tfNumTel.setText(rs.getString("NumTel"));
                tfAdresse.setText(rs.getString("Adresse"));
                tfEmail.setText(rs.getString("Email"));
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(this, "Aucun admin trouvé pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des admins", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AdminsForm();
    }
}
