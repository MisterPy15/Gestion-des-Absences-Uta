import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AbsenceForm extends JFrame {
    private JButton btnSupprimer;
    private JButton btnVider;
    private JPanel AbsencePanel;
    private JTable table1;
    private JButton btnRecherche;
    private JTextField tfRecherche;
    private JTextField tfMatricule;
    private JTextField tfDateAbsence;
    private JTextField tfIdEnseignant;
    private JComboBox<String> cbSemestre;
    private JComboBox<String> cbHeure;

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
        btnRecherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAbsence();
            }
        });
    }

    private void ViderChamps() {
        tfMatricule.setText("");
        tfDateAbsence.setText("");
        tfIdEnseignant.setText("");
        cbSemestre.setSelectedIndex(-1);
        cbHeure.setSelectedIndex(-1);
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
        String matricule = tfRecherche.getText();

        if (matricule.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un matricule pour la recherche", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT * FROM Absence WHERE Matricule = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, matricule);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfMatricule.setText(rs.getString("Matricule"));
                tfDateAbsence.setText(rs.getString("DateAbsence"));
                tfIdEnseignant.setText(rs.getString("IdEnseignant"));
                cbSemestre.setSelectedItem(rs.getString("Semestre"));
                cbHeure.setSelectedItem(rs.getString("HeureAbsence"));
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(this, "Aucune absence trouvée pour le matricule spécifié", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des absences", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AbsenceForm();
    }
}