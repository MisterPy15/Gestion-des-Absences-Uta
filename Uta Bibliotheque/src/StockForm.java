import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class StockForm extends JFrame{

    private JTextField tfTitreLivre;
    private JTextField tfNbrExemplaire;
    private JTextField tfEmplacement;
    private JTextField tfRecherche;
    private JButton btnRcherche;
    private JTable table1;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JComboBox cbEtatLivre;
    private JPanel StockPanel;
    private Connection con;
    private PreparedStatement pst;


    public StockForm(){
        setTitle("Gestion Du Stock");
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
                rechercherLivreStock();
            }
        });

        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createStock();
            }
        });

        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyStock();
            }
        });

        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLivreStock();
            }
        });

        btnViderChamps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();
            }
        });
    }




    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/GestionDesAbsences_Uta?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Connecter");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM stock");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ViderChamps(){

        tfTitreLivre.setText("");
        tfNbrExemplaire.setText("");
        cbEtatLivre.setSelectedItem("Disponible");
        tfEmplacement.setText("");
        tfRecherche.setText("");
    }

    private void rechercherLivreStock() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer Le Titre du livre ou l'Id", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT Titre, Nbr_Exemplaire_Livre, Etat_Livre, Emplacement FROM stock WHERE Titre LIKE ? OR Id_Stock LIKE ?";
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            pst.setString(2, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String titre = rs.getString(1);
                String nbrExmplaire = rs.getString(2);
                String etatLivre = (String) rs.getString(3);
                String emplacement = rs.getString(4);

                tfTitreLivre.setText(titre);
                tfNbrExemplaire.setText(nbrExmplaire);
                cbEtatLivre.setSelectedItem(etatLivre);
                tfEmplacement.setText(emplacement);
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(this, "Aucun Livre trouvé pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isLivreDisponible(String titreLivre) throws SQLException {
        String query = "SELECT Nbr_Exemplaire_Livre FROM stock WHERE Titre = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, titreLivre);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            int nbrExemplaire = rs.getInt(1);
            return nbrExemplaire > 0;
        }
        return false;
    }


    private void createStock() {
        String titreLivre = tfTitreLivre.getText();
        String nbrExmplaire = tfNbrExemplaire.getText();
        String etatLivre = (String) cbEtatLivre.getSelectedItem();
        String Emplacement = tfEmplacement.getText();


        if (titreLivre.isEmpty() || nbrExmplaire.isEmpty() || etatLivre == null || Emplacement.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO stock (Titre, Nbr_Exemplaire_Livre, Etat_Livre, Emplacement) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, titreLivre);
            pst.setString(2, nbrExmplaire);
            pst.setString(3, etatLivre);
            pst.setString(4, Emplacement);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ajout Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);

           ViderChamps();
           table_load();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private boolean livreExists(String TitreLivre) throws SQLException {
        String query = "SELECT COUNT(*) FROM livre WHERE Titre = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, TitreLivre);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }


    private void modifyStock() {
        String titre = tfTitreLivre.getText();

        if ( titre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!livreExists( titre)) {
                JOptionPane.showMessageDialog(this, "Le livre avec le titre " +  titre + " n'existe pas", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nbrExemplaire = tfNbrExemplaire.getText();
            String etatLIvre = (String) cbEtatLivre.getSelectedItem();
            String emplacement = tfEmplacement.getText();

            String query = "UPDATE stock SET Titre=?, Nbr_Exemplaire_Livre=?, Etat_Livre=?, Emplacement=? WHERE Titre=?";
            pst = con.prepareStatement(query);
            pst.setString(1, titre);
            pst.setString(2, nbrExemplaire);
            pst.setString(3, etatLIvre);
            pst.setString(4, emplacement);
            pst.setString(5, titre);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun livre trouvé avec ce Titre", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void deleteLivreStock() {
        String titre = tfTitreLivre.getText();

        if (titre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM stock WHERE Titre=?";
            pst = con.prepareStatement(query);
            pst.setString(1, titre);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Suppression réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun emprunt trouvé avec ce titre de livre et cet identifiant d'adhérent", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }







    public static void main(String[] args) {
        new StockForm();
    }

}
