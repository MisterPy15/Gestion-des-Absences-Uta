import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbsenceForm extends JFrame {
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnVider;
    private JPanel AbsencePanel;
    private JTable table1;
    private JTextField tfDateAbsence;
    private JTextField tfeure;
    private JButton btnAjouter;
    private JButton btnRecherche;
    private JTextField tfRecherche;
    private JTextField tfMatricule;
    private JTextField tfIdEnseignant;
    private JComboBox<String> cbSemestre;
    private Connection con;
    private PreparedStatement pst;

    public AbsenceForm() {
        setTitle("Gestion Des Absences");
        setContentPane(AbsencePanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyAbsence();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAbsence();
            }
        });
        btnVider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();
            }
        });
        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAbsence();
            }
        });
        btnRecherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAbsence();
            }
        });
    }

    private void ViderChamps() {
        tfDateAbsence.setText("");
        tfeure.setText("");
        tfMatricule.setText("");
        tfIdEnseignant.setText("");
        cbSemestre.setSelectedIndex(-1);
        tfRecherche.setText("");
        table_load();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Succès");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAbsence() {
        String matricule = tfMatricule.getText();
        String idEnseignant = tfIdEnseignant.getText();
        String dateAbsence = tfDateAbsence.getText();
        String heureAbsence = tfeure.getText();
        String semestre = (String) cbSemestre.getSelectedItem();

        if (matricule.isEmpty() || idEnseignant.isEmpty() || dateAbsence.isEmpty() || heureAbsence.isEmpty() || semestre == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO Absence (Matricule, IdEnseignant, DateAbsence, HeureAbsence, Semestre) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, matricule);
            pst.setString(2, idEnseignant);
            pst.setString(3, dateAbsence);
            pst.setString(4, heureAbsence);
            pst.setString(5, semestre);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Inscription Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);

            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM Absence");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyAbsence() {
        String idAbsence = tfRecherche.getText();
        String matricule = tfMatricule.getText();
        String idEnseignant = tfIdEnseignant.getText();
        String dateAbsence = tfDateAbsence.getText();
        String heureAbsence = tfeure.getText();
        String semestre = (String) cbSemestre.getSelectedItem();

        if (matricule.isEmpty() || idEnseignant.isEmpty() || dateAbsence.isEmpty() || heureAbsence.isEmpty() || semestre == null) {
            JOptionPane.showMessageDialog(AbsenceForm.this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE Absence SET Matricule=?, IdEnseignant=?, DateAbsence=?, HeureAbsence=?, Semestre=? WHERE Id=?";
            pst = con.prepareStatement(query);
            pst.setString(1, matricule);
            pst.setString(2, idEnseignant);
            pst.setString(3, dateAbsence);
            pst.setString(4, heureAbsence);
            pst.setString(5, semestre);
            pst.setString(6, idAbsence);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(AbsenceForm.this, "Modification Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                table_load();
                ViderChamps();
            } else {
                JOptionPane.showMessageDialog(AbsenceForm.this, "Aucune absence trouvée avec cet identifiant", "Attention", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteAbsence() {
        String idAbsence = tfRecherche.getText();

        if (idAbsence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer l'ID de l'absence à supprimer", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM Absence WHERE Id = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, idAbsence);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Absence Supprimée", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "ID Absence invalide", "Attention", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchAbsence() {
        try {
            String idAbsence = tfRecherche.getText();
            pst = con.prepareStatement("SELECT Matricule, IdEnseignant, DateAbsence, HeureAbsence, Semestre FROM Absence WHERE Id=?");
            pst.setString(1, idAbsence);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String matricule = rs.getString(1);
                String idEnseignant = rs.getString(2);
                String dateAbsence = rs.getString(3);
                String heureAbsence = rs.getString(4);
                String semestre = rs.getString(5);

                tfMatricule.setText(matricule);
                tfIdEnseignant.setText(idEnseignant);
                tfDateAbsence.setText(dateAbsence);
                tfeure.setText(heureAbsence);
                cbSemestre.setSelectedItem(semestre);
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(null, "Aucune absence trouvée pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la recherche des absences", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AbsenceForm();
    }
}