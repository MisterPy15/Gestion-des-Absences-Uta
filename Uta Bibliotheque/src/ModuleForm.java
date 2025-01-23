import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ModuleForm extends JFrame {

    private JTextField tfNomModule;
    private JTextField tfCoefficient;
    private JTextField tfRecherche;
    private JButton btnRcherche;
    private JTable table1;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JPanel StockPanel;
    private JTextField tfFormation;
    private Connection con;
    private PreparedStatement pst;

    public ModuleForm() {
        setTitle("Gestion Des Modules");
        setContentPane(StockPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        btnRcherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchModule();
            }
        });
        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createModule();
            }
        });
        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyModule();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteModule();
            }
        });
        btnViderChamps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table1.getSelectedRow();
                tfNomModule.setText(table1.getModel().getValueAt(row, 1).toString());
                tfCoefficient.setText(table1.getModel().getValueAt(row, 2).toString());
            }
        });
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Connexion réussie");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM Module");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ViderChamps() {
        tfNomModule.setText("");
        tfCoefficient.setText("");
        tfRecherche.setText("");
        table_load();
    }

    private void createModule() {
        String nomModule = tfNomModule.getText();
        String coefficient = tfCoefficient.getText();

        if (nomModule.isEmpty() || coefficient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO Module (NomModule, Coefficient_Module) VALUES (?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, nomModule);
            pst.setString(2, coefficient);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Module Créé", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyModule() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un module dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomModule = tfNomModule.getText();
        String coefficient = tfCoefficient.getText();
        int moduleId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        if (nomModule.isEmpty() || coefficient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp remplissez tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE Module SET NomModule = ?, Coefficient_Module = ? WHERE Id = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, nomModule);
            pst.setString(2, coefficient);
            pst.setInt(3, moduleId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Module modifié", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteModule() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Svp sélectionnez un module dans la table", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int moduleId = Integer.parseInt(table1.getModel().getValueAt(row, 0).toString());

        try {
            String query = "DELETE FROM Module WHERE Id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, moduleId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Module supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchModule() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un texte pour la recherche", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT * FROM Module WHERE NomModule LIKE ?";
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfNomModule.setText(rs.getString("NomModule"));
                tfCoefficient.setText(rs.getString("Coefficient_Module"));
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(this, "Aucun module trouvé pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des modules", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ModuleForm();
    }
}