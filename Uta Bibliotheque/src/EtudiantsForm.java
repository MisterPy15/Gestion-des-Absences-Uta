import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JComboBox cbSpecialitéNiveau;
    private JButton btnVider;

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
    private void loadEtudiants() {
        String query = "SELECT * FROM Etudiant";
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Matricule", "NomEtudiant", "PrenomEtudiant", "EmailEtudiant", "AdresseEtudiant", "SpecialiteNiveau"}, 0);

            while (rs.next()) {
                String matricule = rs.getString("Matricule");
                String nom = rs.getString("NomEtudiant");
                String prenom = rs.getString("PrenomEtudiant");
                String email = rs.getString("EmailEtudiant");
                String adresse = rs.getString("AdresseEtudiant");
                String specialiteNiveau = rs.getString("SpecialiteNiveau");
                model.addRow(new Object[]{matricule, nom, prenom, email, adresse, specialiteNiveau});
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
        cbSpecialitéNiveau.setSelectedIndex(-1);
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

        String query = "INSERT INTO Etudiant (NomEtudiant, PrenomEtudiant, EmailEtudiant, AdresseEtudiant, Matricule, SpecialiteNiveau) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, email);
            pst.setString(4, adresse);
            pst.setString(5, matricule);
            pst.setString(6, specialiteNiveau);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Inscription Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
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

        String query = "UPDATE Etudiant SET NomEtudiant = ?, PrenomEtudiant = ?, EmailEtudiant = ?, AdresseEtudiant = ?, SpecialiteNiveau = ? WHERE Matricule = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
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

        String query = "DELETE FROM Etudiant WHERE Matricule = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
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

        String query = "SELECT * FROM Etudiant WHERE Matricule = ? OR EmailEtudiant = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
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