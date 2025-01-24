import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardEnseignant {
    private JComboBox<String> cbModule;
    private JComboBox<String> cbSpecialiteNiveau;
    private JTextField tfMatriculeEtudiant;
    private JButton btnAjouterAbsence;
    private JLabel lblEnseignant;
    private JLabel LblidEnseignant;
    private JTextField tfIdEnseignant;
    private JPanel PanelDate;
    private JComboBox<String> cbHheure;
    private JComboBox<String> cbSemestre;
    private JLabel lblHeure;
    private JTable table1;
    private JPanel dashboardPanel;
    private JTextField tfDate;

    private Connection con;
    private PreparedStatement pst;

    public DashboardEnseignant(User user) {
        lblEnseignant.setText(user.nom + " " + user.prenom);
        LblidEnseignant.setText(String.valueOf(user.id));

        connect();
        loadSpecialiteNiveau();
        loadModules();
        loadAbsences();
        setDate();
        startCountdown();

        cbSpecialiteNiveau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadModules();
            }
        });

        btnAjouterAbsence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterAbsence();
            }
        });
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSpecialiteNiveau() {
        try {
            pst = con.prepareStatement("SELECT DISTINCT SpecialiteNiveau FROM Etudiant");
            ResultSet rs = pst.executeQuery();
            cbSpecialiteNiveau.removeAllItems();
            while (rs.next()) {
                cbSpecialiteNiveau.addItem(rs.getString("SpecialiteNiveau"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadModules() {
        String specialiteNiveau = (String) cbSpecialiteNiveau.getSelectedItem();
        if (specialiteNiveau == null) return;

        try {
            pst = con.prepareStatement("SELECT NomModule FROM Module WHERE SpecialiteNiveau = ?");
            pst.setString(1, specialiteNiveau);
            ResultSet rs = pst.executeQuery();
            cbModule.removeAllItems();
            while (rs.next()) {
                cbModule.addItem(rs.getString("NomModule"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ajouterAbsence() {
        String matricule = tfMatriculeEtudiant.getText();
        String idEnseignant = tfIdEnseignant.getText();
        String dateAbsence = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String heureAbsence = (String) cbHheure.getSelectedItem();
        String semestre = (String) cbSemestre.getSelectedItem();

        if (matricule.isEmpty() || idEnseignant.isEmpty() || heureAbsence == null || semestre == null) {
            JOptionPane.showMessageDialog(dashboardPanel, "Veuillez remplir tous les champs", "Attention", JOptionPane.WARNING_MESSAGE);
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

            JOptionPane.showMessageDialog(dashboardPanel, "Absence ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            loadAbsences();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAbsences() {
        try {
            pst = con.prepareStatement("SELECT * FROM Absence");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setDate() {
        if (PanelDate != null) {
            PanelDate.removeAll(); // Clear any existing components
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            JLabel dateLabel = new JLabel(currentDate);
            PanelDate.add(dateLabel);
            PanelDate.revalidate(); // Refresh the panel to show the new label
            PanelDate.repaint();
        } else {
            System.err.println("PanelDate is not initialized.");
        }
    }

    private void startCountdown() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                lblHeure.setText(currentTime);
            }
        }, 0, 1000);
    }


//    public static void main(String[] args) {
////        User user = new User(); // Replace with the logged-in user
////        user.nom = "agoh";
////        user.prenom = "chris";
////        user.id = 9;
//
//        JFrame frame = new JFrame("DashboardEnseignant");
//        frame.setContentPane(new DashboardEnseignant(user).dashboardPanel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DashboardEnseignant(null); // For testing purposes, no user is passed
            }
        });
    }













}