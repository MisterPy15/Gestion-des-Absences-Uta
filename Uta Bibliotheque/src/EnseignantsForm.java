import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class EnseignantsForm extends JFrame {
    private JPanel EnseignantPanel;
    private JTextField tfNom;
    private JTextField tfRecherche;
    private JButton btnRcherche;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JTable table1;
    private JTextField tfAdresse;
    private JTextField tfIdEmail;
    private JTextField tfPrenom;
    private JPasswordField pfMotdePasse;
    private JTextField tfNumTel;
    private Connection con;
    private PreparedStatement pst;

    public EnseignantsForm() {
        setTitle("Gestion des Enseignants");
        setContentPane(EnseignantPanel);
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

        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEnseignant();
            }
        });

        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnseignant();
            }
        });

        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEnseignant();
            }
        });

        btnRcherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchEnseignant();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table1.getSelectedRow();
                tfNom.setText(table1.getModel().getValueAt(row, 1).toString());
                tfPrenom.setText(table1.getModel().getValueAt(row, 2).toString());
                pfMotdePasse.setText(table1.getModel().getValueAt(row, 3).toString());
                tfNumTel.setText(table1.getModel().getValueAt(row, 4).toString());
                tfAdresse.setText(table1.getModel().getValueAt(row, 5).toString());
                tfIdEmail.setText(table1.getModel().getValueAt(row, 6).toString());
            }
        });
    }


    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Succès");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    private void table_load() {
        try {
            String query = "SELECT e.Id, u.Nom, u.Prenom, u.MotDePasse, u.NumTel, u.Adresse, u.Email FROM Enseignant e JOIN Utilisateur u ON e.IdUtilsateur = u.Id";
            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ViderChamps() {
        tfNom.setText("");
        tfAdresse.setText("");
        tfIdEmail.setText("");
        tfRecherche.setText("");
        tfPrenom.setText("");
        pfMotdePasse.setText("");
        tfNumTel.setText("");
    }

    private void searchEnseignant() {
        try {
            String recherche = tfRecherche.getText();
            String query = "SELECT e.Id, u.Nom, u.Prenom, u.MotDePasse, u.NumTel, u.Adresse," +
                            " u.Email FROM Enseignant e JOIN Utilisateur u ON e.IdUtilsateur = u.Id WHERE u.Nom LIKE ? OR u.Prenom LIKE ?";
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            pst.setString(2, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void createEnseignant() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String adresse = tfAdresse.getText();
        String email = tfIdEmail.getText();
        String motDePasse = new String(pfMotdePasse.getPassword());
        String numTel = tfNumTel.getText();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(motDePasse);

        try {
            String queryUtilisateur = "INSERT INTO Utilisateur (Nom, Prenom, MotDePasse, NumTel, Adresse, Email, Role) VALUES (?, ?, ?, ?, ?, ?, 'Enseignant')";
            pst = con.prepareStatement(queryUtilisateur, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, hashedPassword);
            pst.setString(4, numTel);
            pst.setString(5, adresse);
            pst.setString(6, email);
            pst.executeUpdate();

            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int utilisateurId = generatedKeys.getInt(1);

                String queryEnseignant = "INSERT INTO Enseignant (Num_Tel, IdUtilsateur) VALUES (?, ?)";
                pst = con.prepareStatement(queryEnseignant);
                pst.setString(1, numTel);
                pst.setInt(2, utilisateurId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Enseignant Créé", "Succès", JOptionPane.INFORMATION_MESSAGE);
                table_load();
                ViderChamps();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateEnseignant() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un enseignant dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String adresse = tfAdresse.getText();
        String email = tfIdEmail.getText();
        String motDePasse = new String(pfMotdePasse.getPassword());
        String numTel = tfNumTel.getText();
        int idEnseignant = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || numTel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp remplissez tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(motDePasse);

        try {
            String queryUtilisateur = "UPDATE Utilisateur SET Nom = ?, Prenom = ?, MotDePasse = ?, NumTel = ?, Adresse = ?, Email = ? WHERE Id = (SELECT IdUtilsateur FROM Enseignant WHERE Id = ?)";
            pst = con.prepareStatement(queryUtilisateur);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, hashedPassword);
            pst.setString(4, numTel);
            pst.setString(5, adresse);
            pst.setString(6, email);
            pst.setInt(7, idEnseignant);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Enseignant modifié", "Succès", JOptionPane.INFORMATION_MESSAGE);
            table_load();
            ViderChamps();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEnseignant() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un enseignant dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idEnseignant = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        try {
            String queryEnseignant = "DELETE FROM Enseignant WHERE Id = ?";
            pst = con.prepareStatement(queryEnseignant);
            pst.setInt(1, idEnseignant);
            pst.executeUpdate();

            String queryUtilisateur = "DELETE FROM Utilisateur WHERE Id = (SELECT IdUtilsateur FROM Enseignant WHERE Id = ?)";
            //String queryUtilisateurEns = "DELETE FROM Utilisateur WHERE Id = ?";

            pst = con.prepareStatement(queryUtilisateur);
            pst.setInt(1, idEnseignant);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Enseignant supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
            table_load();
            ViderChamps();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EnseignantsForm();
    }
}