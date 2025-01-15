import com.toedter.calendar.JDateChooser;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class EmpruntForm extends JFrame {
    private JTextField tfTitreLivre;
    private JTextField tfIdAdherent;
    private JButton btnRecherche;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JTable table1;
    private JPanel DateEmpruntPanel;
    private JPanel DateRetourPanel;
    private JPanel DateRappelPanel;
    private JPanel EmpruntPanel;
    private JLabel lbNbrEmpruntAherent;
    private JTextField tfRecherche;
    private Connection con;
    private PreparedStatement pst;
    private JDateChooser dateEmprunt;
    private JDateChooser dateRetour;
    private JDateChooser dateRappel;

    public EmpruntForm() {
        setTitle("Gestion Des Emprunts");
        setContentPane(EmpruntPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        // Initialisation de JDateChooser
        dateEmprunt = new JDateChooser();
        dateRetour = new JDateChooser();
        dateRappel = new JDateChooser();

        // Ajout de JDateChooser aux panneaux respectifs
        DateEmpruntPanel.setLayout(new BorderLayout());
        DateEmpruntPanel.add(dateEmprunt, BorderLayout.CENTER);

        DateRetourPanel.setLayout(new BorderLayout());
        DateRetourPanel.add(dateRetour, BorderLayout.CENTER);

        DateRappelPanel.setLayout(new BorderLayout());
        DateRappelPanel.add(dateRappel, BorderLayout.CENTER);

        // Actualisation de l'affichage pour s'assurer que les composants sont visibles
        DateEmpruntPanel.revalidate();
        DateEmpruntPanel.repaint();
        DateRetourPanel.revalidate();
        DateRetourPanel.repaint();
        DateRappelPanel.revalidate();
        DateRappelPanel.repaint();

        btnViderChamps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();

            }
        });
        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEmprunt();
            }
        });
        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyEmprunt();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmprunt();
            }
        });
        btnRecherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherEmprunts();
            }
        });

        dateEmprunt.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                updateDates();
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
            pst = con.prepareStatement("SELECT * FROM emprunt");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ViderChamps() {
        tfTitreLivre.setText("");
        tfIdAdherent.setText("");
        lbNbrEmpruntAherent.setText("Nombre d'Emprunt Adhérent : 0");
        dateEmprunt.setDate(null);
        dateRetour.setDate(null);
        dateRappel.setDate(null);
        tfRecherche.setText("");

        table_load();
    }

    private boolean livreExists(String TitreLivre) throws SQLException {
        String query = "SELECT COUNT(*) FROM stock WHERE Titre = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, TitreLivre);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }


    private boolean adherentExists(String IdAdherent) throws SQLException {
        String query = "SELECT COUNT(*) FROM adhérent WHERE Id_Adhérent = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, IdAdherent);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }


    private int getNbrEmprunts(String IdAdherent) throws SQLException {
        String query = "SELECT COUNT(*) FROM emprunt WHERE Id_Adhérent = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, IdAdherent);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private void updateNbrEmpruntsLabel(String IdAdherent) {
        try {
            int nbrEmprunts = getNbrEmprunts(IdAdherent);
            lbNbrEmpruntAherent.setText("Nombre d'Emprunt Adhérent : " + nbrEmprunts);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isAdherentLimitReached(String IdAdherent) throws SQLException {
        int nbrEmprunts = getNbrEmprunts(IdAdherent);
        return nbrEmprunts >= 2;
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

    private void diminuerStockLivre(String titreLivre) throws SQLException {
        String query = "UPDATE stock SET Nbr_Exemplaire_Livre = Nbr_Exemplaire_Livre - 1 WHERE Titre = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, titreLivre);
        pst.executeUpdate();
    }

    private void augmenterStockLivre(String titreLivre) throws SQLException {
        String query = "UPDATE stock SET Nbr_Exemplaire_Livre = Nbr_Exemplaire_Livre + 1 WHERE Titre = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, titreLivre);
        pst.executeUpdate();
    }





    private void createEmprunt() {
        String titreLivre = tfTitreLivre.getText();
        String idAdherent = tfIdAdherent.getText();
        Date dateEmpruntValue = dateEmprunt.getDate();

        if (titreLivre.isEmpty() || idAdherent.isEmpty() || dateEmpruntValue == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!livreExists(titreLivre)) {
                JOptionPane.showMessageDialog(this, "Le livre avec le titre " + titreLivre + " n'existe pas", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!adherentExists(idAdherent)) {
                JOptionPane.showMessageDialog(this, "L'adhérent " + idAdherent + " n'existe pas", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isAdherentLimitReached(idAdherent)) {
                JOptionPane.showMessageDialog(this, "L'adhérent a déjà emprunté le nombre maximum de livres (2).", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isLivreDisponible(titreLivre)) {

                JOptionPane.showMessageDialog(this, "Le livre avec le titre " + titreLivre + " n'est pas disponible", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateEmprunt = dateFormat.format(dateEmpruntValue);

            // Calcul automatique des dates de retour et de rappel
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateEmpruntValue);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            Date dateRetourValue = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            Date dateRappelValue = cal.getTime();

            String formattedDateRetour = dateFormat.format(dateRetourValue);
            String formattedDateRappel = dateFormat.format(dateRappelValue);

            String query = "INSERT INTO emprunt (Nom_Livre_Emprunté, Date_Emprunt, Date_Retour, Rappel, Nbre_Emprunt, Id_Adhérent) VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, titreLivre);
            pst.setString(2, formattedDateEmprunt);
            pst.setString(3, formattedDateRetour);
            pst.setString(4, formattedDateRappel);
            pst.setInt(5, 1);  // Initialisation à 1 pour un nouvel emprunt
            pst.setString(6, idAdherent);
            pst.executeUpdate();

            // Mise à jour du stock
            diminuerStockLivre(titreLivre);

            JOptionPane.showMessageDialog(this, "Emprunt Créé", "Succès", JOptionPane.INFORMATION_MESSAGE);
            updateNbrEmpruntsLabel(idAdherent);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void modifyEmprunt() {
        String TitreLivre = tfTitreLivre.getText();
        String IdAdherent = tfIdAdherent.getText();
        Date dateEmpruntValue = dateEmprunt.getDate();

        if (TitreLivre.isEmpty() || IdAdherent.isEmpty() || dateEmpruntValue == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!livreExists(TitreLivre)) {
                JOptionPane.showMessageDialog(this, "Le livre avec le titre " + TitreLivre + " n'existe pas", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!adherentExists(IdAdherent)) {
                JOptionPane.showMessageDialog(this, "L'adhérent " + IdAdherent + " n'existe pas", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateEmprunt = dateFormat.format(dateEmpruntValue);

            // Calcul automatique des dates de retour et de rappel
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateEmpruntValue);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            Date dateRetourValue = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            Date dateRappelValue = cal.getTime();

            String formattedDateRetour = dateFormat.format(dateRetourValue);
            String formattedDateRappel = dateFormat.format(dateRappelValue);

            String query = "UPDATE emprunt SET Date_Emprunt=?, Date_Retour=?, Rappel=? WHERE Nom_Livre_Emprunté=? AND Id_Adhérent=?";
            pst = con.prepareStatement(query);
            pst.setString(1, formattedDateEmprunt);
            pst.setString(2, formattedDateRetour);
            pst.setString(3, formattedDateRappel);
            pst.setString(4, TitreLivre);
            pst.setString(5, IdAdherent);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                updateNbrEmpruntsLabel(IdAdherent);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun emprunt trouvé avec ce titre de livre et cet identifiant d'adhérent", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEmprunt() {
        String TitreLivre = tfTitreLivre.getText();
        String IdAdherent = tfIdAdherent.getText();

        if (TitreLivre.isEmpty() || IdAdherent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM emprunt WHERE Nom_Livre_Emprunté=? AND Id_Adhérent=?";
            pst = con.prepareStatement(query);
            pst.setString(1, TitreLivre);
            pst.setString(2, IdAdherent);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Suppression réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                updateNbrEmpruntsLabel(IdAdherent);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun emprunt trouvé avec ce titre de livre et cet identifiant d'adhérent", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            augmenterStockLivre(TitreLivre);
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void rechercherEmprunts() {
        String recherche = tfRecherche.getText();

        if (recherche.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un texte pour la recherche", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT Nom_Livre_Emprunté, Date_Emprunt, Date_Retour, Rappel, Nbre_Emprunt, Id_Adhérent FROM emprunt WHERE Nom_Livre_Emprunté LIKE ? OR Id_Adhérent LIKE ?";
            pst = con.prepareStatement(query);
            pst.setString(1, "%" + recherche + "%");
            pst.setString(2, "%" + recherche + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String nomLivreEmprunte = rs.getString(1);
                Date dateOfEmprunt = rs.getDate(2);
                Date dateOfRetour = rs.getDate(3);
                Date dateOfRappel = rs.getDate(4);
                int nbreEmprunt = rs.getInt(5);
                String idAdherent = rs.getString(6);

                tfTitreLivre.setText(nomLivreEmprunte);
                dateEmprunt.setDate(dateOfEmprunt);
                dateRetour.setDate(dateOfRetour);
                dateRappel.setDate(dateOfRappel);
                lbNbrEmpruntAherent.setText("Nombre d'Emprunt Adhérent : " + nbreEmprunt);
                tfIdAdherent.setText(idAdherent);
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(this, "Aucun emprunt trouvé pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

            table1.setModel(DbUtils.resultSetToTableModel(rs));
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des emprunts", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDates() {
        Date dateEmpruntValue = dateEmprunt.getDate();
        if (dateEmpruntValue != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateEmpruntValue);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            Date dateRetourValue = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            Date dateRappelValue = cal.getTime();

            dateRetour.setDate(dateRetourValue);
            dateRappel.setDate(dateRappelValue);
        }
    }

    public static void main(String[] args) {
        new EmpruntForm();
    }
}