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

public class ReservationForm extends JFrame {
    private JPanel ReservationPanel;
    private JTextField tfTitreLivre;
    private JTextField tfRecherche;
    private JButton btnRcherche;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnViderChamps;
    private JTable table1;
    private JTextField tfIdLivre;
    private JTextField tfIdAdhrent;
    private JPanel DateReservePanel;
    private JPanel DateRappelDispoPanel;
    private JLabel lbNbrReserve;
    private Connection con;
    private PreparedStatement pst;
    private JDateChooser dateReserve;
    private JDateChooser dateRappeldeDispo;

    public ReservationForm() {
        setTitle("Gestion des Réservations");
        setContentPane(ReservationPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        // Initialisation de JDateChooser
        dateReserve = new JDateChooser();
        DateReservePanel.setLayout(new BorderLayout());
        DateReservePanel.add(dateReserve, BorderLayout.CENTER);

        dateRappeldeDispo = new JDateChooser();
        DateRappelDispoPanel.setLayout(new BorderLayout());
        DateRappelDispoPanel.add(dateRappeldeDispo, BorderLayout.CENTER);

        DateReservePanel.revalidate();
        DateReservePanel.repaint();
        DateRappelDispoPanel.revalidate();
        DateRappelDispoPanel.repaint();

        btnViderChamps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViderChamps();
            }
        });

        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteReservation();
            }
        });

        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateReservation();
            }
        });

        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createReservation();
            }
        });

        btnRcherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchReservation();
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
            pst = con.prepareStatement("SELECT * FROM réservation");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ViderChamps() {
        tfTitreLivre.setText("");
        tfIdLivre.setText("");
        tfIdAdhrent.setText("");
        lbNbrReserve.setText("Nombre de Réservation : 0");
        dateReserve.setDate(null);
        dateRappeldeDispo.setDate(null);
        tfRecherche.setText("");
    }

    private void searchReservation() {
        try {
            String idReserve = tfRecherche.getText();
            pst = con.prepareStatement("SELECT Nom_Livre, Date_réservation, Rappel_de_disponibilité, Id_Livre, Id_Adhérent FROM réservation WHERE Id_Réservation=?");
            pst.setString(1, idReserve);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String Titre = rs.getString(1);
                Date DateReserve = rs.getDate(2);
                Date DateRapelledeDispo = rs.getDate(3);
                String idlivre = rs.getString(4);
                String idadherent = rs.getString(5);

                tfTitreLivre.setText(Titre);
                dateReserve.setDate(DateReserve);
                dateRappeldeDispo.setDate(DateRapelledeDispo);
                tfIdLivre.setText(idlivre);
                tfIdAdhrent.setText(idadherent);

                // Obtenir le nom de l'adhérent
                String adherentName = getAdherentNameById(idadherent);
                lbNbrReserve.setText("Adhérent: " + adherentName);

                // Compter le nombre de réservations pour l'adhérent
                int nbrReserve = countReservationsForAdherent(idadherent);
                lbNbrReserve.setText(lbNbrReserve.getText() + " | Réservations: " + nbrReserve);
            } else {
                ViderChamps();
                JOptionPane.showMessageDialog(null, "Id réservation invalide", "Attention", JOptionPane.ERROR_MESSAGE);
            }
                table_load();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int countReservationsForAdherent(String idAdherent) throws SQLException {
        pst = con.prepareStatement("SELECT COUNT(*) FROM réservation WHERE Id_Adhérent=?");
        pst.setString(1, idAdherent);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    private boolean livreExists(String titreLivre) throws SQLException {
        pst = con.prepareStatement("SELECT * FROM livre WHERE Titre=?");
        pst.setString(1, titreLivre);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    private String getAdherentNameById(String idAdherent) throws SQLException {
        pst = con.prepareStatement("SELECT Nom FROM adhérent WHERE Id_Adhérent=?");
        pst.setString(1, idAdherent);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getString("Nom");
        } else {
            return "Adhérent Inconnu";
        }
    }


    private boolean adherentExists(String idAdherent) throws SQLException {
        pst = con.prepareStatement("SELECT * FROM adhérent WHERE Id_Adhérent=?");
        pst.setString(1, idAdherent);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    private int countNonReturnedBooks(String idAdherent) throws SQLException {
        pst = con.prepareStatement("SELECT COUNT(*) FROM emprunt WHERE Id_Adhérent=? AND Date_Retour IS NULL");
        pst.setString(1, idAdherent);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    private void createReservation() {
        String TitreLivre = tfTitreLivre.getText();
        String IdAdherent = tfIdAdhrent.getText();
        Date dateReserveValue = dateReserve.getDate();

        if (TitreLivre.isEmpty() || IdAdherent.isEmpty() || dateReserveValue == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
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

            if (countNonReturnedBooks(IdAdherent) >= 2) {
                JOptionPane.showMessageDialog(this, "L'adhérent " + IdAdherent + " a déjà emprunté 2 livres non retournés", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateReserve = dateFormat.format(dateReserveValue);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateReserveValue);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            Date dateRappelValue = cal.getTime();

            String formattedDateRappel = dateFormat.format(dateRappelValue);

            String query = "INSERT INTO réservation (Nom_Livre, Date_réservation, Rappel_de_disponibilité, Nbre_Réservation, Id_Livre, Id_Adhérent) VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, TitreLivre);
            pst.setString(2, formattedDateReserve);
            pst.setString(3, formattedDateRappel);
            pst.setInt(4, 1);  // Initialisation à 1 pour une nouvelle réservation
            pst.setString(5, tfIdLivre.getText());
            pst.setString(6, IdAdherent);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Réservation Créée", "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Update the number of reservations for the adherent
            int nbrReserve = countReservationsForAdherent(IdAdherent);
            String adherentName = getAdherentNameById(IdAdherent);
            lbNbrReserve.setText("Adhérent: " + adherentName + " | Réservations: " + nbrReserve);


            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateReservation() {
        String TitreLivre = tfTitreLivre.getText();
        String IdAdherent = tfIdAdhrent.getText();
        Date dateReserveValue = dateReserve.getDate();
        String idReserve = tfRecherche.getText();

        if (TitreLivre.isEmpty() || IdAdherent.isEmpty() || dateReserveValue == null || idReserve.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateReserve = dateFormat.format(dateReserveValue);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateReserveValue);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            Date dateRappelValue = cal.getTime();

            String formattedDateRappel = dateFormat.format(dateRappelValue);

            String query = "UPDATE réservation SET Nom_Livre=?, Date_réservation=?, Rappel_de_disponibilité=?, Id_Livre=?, Id_Adhérent=? WHERE Id_Réservation=?";
            pst = con.prepareStatement(query);
            pst.setString(1, TitreLivre);
            pst.setString(2, formattedDateReserve);
            pst.setString(3, formattedDateRappel);
            pst.setString(4, tfIdLivre.getText());
            pst.setString(5, IdAdherent);
            pst.setString(6, idReserve);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Réservation Modifiée", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteReservation() {
        String idReserve = tfRecherche.getText();

        if (idReserve.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Svp Recherchez et Sélectionnez une Réservation à Supprimer", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM réservation WHERE Id_Réservation=?";
            pst = con.prepareStatement(query);
            pst.setString(1, idReserve);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Réservation Supprimée", "Succès", JOptionPane.INFORMATION_MESSAGE);
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateDateRappelLivreRetourner(String idLivre) {
        try {
            String query = "UPDATE réservation SET Rappel_de_disponibilité = CURDATE() WHERE Id_Livre = ? AND Rappel_de_disponibilité IS NULL";
            pst = con.prepareStatement(query);
            pst.setString(1, idLivre);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new ReservationForm();
    }
}
