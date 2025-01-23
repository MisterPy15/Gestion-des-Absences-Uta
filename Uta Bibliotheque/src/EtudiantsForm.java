import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EtudiantsForm extends JFrame {
    private JTextField tfNom;
    private JTextField tfPrenom;
    private JTextField tfMatricule;
    private JTextField tfAdresse;
    private JTextField tfEmail;
    private JTable table1;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton rechercherButton;
    private JTextField tfRecherche;
    private JPanel AdherentPanel;
<<<<<<< HEAD
    private JComboBox cbSpecialitéNiveau;
    private JButton btnVider;
=======
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179

    private Connection con;

    public EtudiantsForm() {
        setTitle("Gestion Des Etudiants");
        setContentPane(AdherentPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setResizable(true);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        loadEtudiants();

        btnCreer.addActionListener(e -> {
            createEtudiant();
            loadEtudiants();
        });

        btnModifier.addActionListener(e -> {
            updateEtudiant();
            loadEtudiants();
        });

        btnSupprimer.addActionListener(e -> {
            deleteEtudiant();
            loadEtudiants();
        });

        rechercherButton.addActionListener(e -> searchEtudiant());

        btnVider.addActionListener(e -> resetFields());
    }

    private void connect() {
        try {
            // Charger le pilote PostgreSQL
            Class.forName("org.postgresql.Driver");
            // Se connecter à la base de données
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GestionDesAbsences_Uta", "postgres", "29122003");
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Pilote JDBC non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void loadEtudiants() {
<<<<<<< HEAD
        String query = "SELECT * FROM Etudiant";
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Matricule", "NomEtudiant", "PrenomEtudiant", "EmailEtudiant", "AdresseEtudiant", "SpecialiteNiveau"}, 0);

=======
        try {
            String query = "SELECT * FROM Etudiant";
            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"Matricule", "NomEtudiant", "PrenomEtudiant", "EmailEtudiant", "AdresseEtudiant"}, 0);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            while (rs.next()) {
                String matricule = rs.getString("Matricule");
                String nom = rs.getString("NomEtudiant");
                String prenom = rs.getString("PrenomEtudiant");
                String email = rs.getString("EmailEtudiant");
                String adresse = rs.getString("AdresseEtudiant");
<<<<<<< HEAD
                String specialiteNiveau = rs.getString("SpecialiteNiveau");
                model.addRow(new Object[]{matricule, nom, prenom, email, adresse, specialiteNiveau});
=======
                model.addRow(new Object[]{matricule, nom, prenom, email, adresse});
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            }

            table1.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void resetFields() {
        tfNom.setText("");
        tfPrenom.setText("");
        tfEmail.setText("");
        tfAdresse.setText("");
        tfMatricule.setText("");
        tfRecherche.setText("");
<<<<<<< HEAD
        cbSpecialitéNiveau.setSelectedIndex(-1);
=======
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
    }

    private void createEtudiant() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String email = tfEmail.getText();
        String adresse = tfAdresse.getText();
        String matricule = tfMatricule.getText();
        String specialiteNiveau = cbSpecialitéNiveau.getSelectedItem() != null ? cbSpecialitéNiveau.getSelectedItem().toString() : "";

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || adresse.isEmpty() || matricule.isEmpty() || specialiteNiveau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

<<<<<<< HEAD
        String query = "INSERT INTO Etudiant (NomEtudiant, PrenomEtudiant, EmailEtudiant, AdresseEtudiant, Matricule, SpecialiteNiveau) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
=======
        try {
            String query = "INSERT INTO Etudiant (NomEtudiant, PrenomEtudiant, EmailEtudiant, AdresseEtudiant, Matricule) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, email);
            pst.setString(4, adresse);
            pst.setString(5, matricule);
            pst.setString(6, specialiteNiveau);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Inscription Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
<<<<<<< HEAD
            resetFields();
=======
            resetFields();  //pour vider les champs après l'inscription

>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateEtudiant() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String email = tfEmail.getText();
        String adresse = tfAdresse.getText();
        String matricule = tfMatricule.getText();
        String specialiteNiveau = cbSpecialitéNiveau.getSelectedItem() != null ? cbSpecialitéNiveau.getSelectedItem().toString() : "";

        if (matricule.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || adresse.isEmpty() || specialiteNiveau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

<<<<<<< HEAD
        String query = "UPDATE Etudiant SET NomEtudiant = ?, PrenomEtudiant = ?, EmailEtudiant = ?, AdresseEtudiant = ?, SpecialiteNiveau = ? WHERE Matricule = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
=======
        try {
            String query = "UPDATE Etudiant SET NomEtudiant = ?, PrenomEtudiant = ?, EmailEtudiant = ?, AdresseEtudiant = ? WHERE Matricule = ?";
            pst = con.prepareStatement(query);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, email);
            pst.setString(4, adresse);
            pst.setString(5, specialiteNiveau);
            pst.setString(6, matricule);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Modification Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEtudiant() {
        String matricule = tfMatricule.getText();

        if (matricule.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Entrez le Matricule", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

<<<<<<< HEAD
        String query = "DELETE FROM Etudiant WHERE Matricule = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
=======
        try {
            String query = "DELETE FROM Etudiant WHERE Matricule = ?";
            pst = con.prepareStatement(query);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst.setString(1, matricule);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Suppression Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchEtudiant() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Entrez un Matricule ou Email", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

<<<<<<< HEAD
        String query = "SELECT * FROM Etudiant WHERE Matricule = ? OR EmailEtudiant = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
=======
        try {
            String query = "SELECT * FROM Etudiant WHERE Matricule = ? OR NomEtudiant = ?";
            pst = con.prepareStatement(query);
>>>>>>> 98974231dd4166c977521f8b0e3cef9c69f32179
            pst.setString(1, recherche);
            pst.setString(2, recherche);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfNom.setText(rs.getString("NomEtudiant"));
                tfPrenom.setText(rs.getString("PrenomEtudiant"));
                tfEmail.setText(rs.getString("EmailEtudiant"));
                tfAdresse.setText(rs.getString("AdresseEtudiant"));
                tfMatricule.setText(rs.getString("Matricule"));
                cbSpecialitéNiveau.setSelectedItem(rs.getString("SpecialiteNiveau"));
            } else {
                JOptionPane.showMessageDialog(this, "Etudiant non trouvé", "Attention", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EtudiantsForm::new);
    }

    private void createUIComponents() {
        // Initialisation de cbSpecialitéNiveau
        cbSpecialitéNiveau = new JComboBox(new String[]{"Informatique L1", "Informatique L2", "Mathématiques L1", "Mathématiques L2"});
        cbSpecialitéNiveau.setSelectedIndex(-1);

        // Initialisation du bouton btnVider
        btnVider = new JButton("Vider");
    }
}
