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
            Class.forName("com.postgresql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GestionDesAbsences_Uta", "postgres", "29122003");
            System.out.println("Connexion réussie");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM Administrateur a JOIN Utilisateur u ON a.IdUtilisateur = u.Id");
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
            String queryUtilisateur = "INSERT INTO Utilisateur (Nom, Prenom, MotDePasse, NumTel, Adresse, Email, Role) VALUES (?, ?, ?, ?, ?, ?, 'Admin')";
            pst = con.prepareStatement(queryUtilisateur, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, motDePasse);
            pst.setString(4, numTel);
            pst.setString(5, adresse);
            pst.setString(6, email);
            pst.executeUpdate();

            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int utilisateurId = generatedKeys.getInt(1);

                String queryAdmin = "INSERT INTO Administrateur (IdUtilisateur, Téléphone) VALUES (?, ?)";
                pst = con.prepareStatement(queryAdmin);
                pst.setInt(1, utilisateurId);
                pst.setString(2, numTel);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Admin Créé", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyAdmin() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un admin dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String motDePasse = tfMotdepasse.getText();
        String numTel = tfNumTel.getText();
        String adresse = tfAdresse.getText();
        String email = tfEmail.getText();
        int adminId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        if (nom.isEmpty() || prenom.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty() || adresse.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp remplissez tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
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
            ViderChamps();
            table_load();
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

    private void searchAdmin() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un texte pour la recherche", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT * FROM Administrateur a JOIN Utilisateur u ON a.IdUtilisateur = u.Id WHERE u.Nom LIKE ? OR u.Prenom LIKE ?";
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            pst.setString(2, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfNom.setText(rs.getString("Nom"));
                tfPrenom.setText(rs.getString("Prenom"));
                tfMotdepasse.setText(rs.getString("MotDePasse"));
                tfNumTel.setText(rs.getString("NumTel"));
                tfAdresse.setText(rs.getString("Adresse"));
                tfEmail.setText(rs.getString("Email"));
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