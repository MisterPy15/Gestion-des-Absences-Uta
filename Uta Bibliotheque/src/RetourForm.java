import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class RetourForm extends JFrame {
    private JTextField tfIdAdherent;
    private JButton btnDetails;
    private JButton btnValider;
    private JButton btnMail;
    private JLabel lbNomAdherent;
    private JLabel lbPrenomAdherent;
    private JLabel lbTitreLivre;
    private JLabel lbDateEmprunt;
    private JLabel lbDateRetour;
    private JPanel RetourPanel;
    private JLabel lbEmailAdherent;

    private Connection con;
    private PreparedStatement pst;

    public RetourForm() {
        retourForm();
        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherDetails();
            }
        });
        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validerRetour();
            }
        });
        btnMail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                envoyerMail();
            }
        });
    }

    public void retourForm() {
        setTitle("Gestion Des Emprunts");
        setContentPane(RetourPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        connect();
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

    private void afficherDetails() {
        String idAdherent = tfIdAdherent.getText();
        try {
            String query = "SELECT a.Nom, a.Prénom, a.Email, e.Nom_Livre_Emprunté, e.Date_Emprunt, e.Date_Retour " +
                    "FROM adhérent a JOIN emprunt e ON a.Id_Adhérent = e.Id_Adhérent " +
                    "WHERE a.Id_Adhérent = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, idAdherent);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lbNomAdherent.setText(rs.getString("Nom"));
                lbPrenomAdherent.setText(rs.getString("Prénom"));
                lbEmailAdherent.setText(rs.getString("Email"));
                lbTitreLivre.setText(rs.getString("Nom_Livre_Emprunté"));
                lbDateEmprunt.setText(rs.getString("Date_Emprunt"));
                lbDateRetour.setText(rs.getString("Date_Retour"));
            } else {
                JOptionPane.showMessageDialog(this,
                                                "Aucun emprunt trouvé pour cet adhérent.",
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void validerRetour() {
        String idAdherent = tfIdAdherent.getText();
        try {
            String query = "DELETE FROM emprunt WHERE Id_Adhérent = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, idAdherent);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Retour validé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                lbNomAdherent.setText("");
                lbPrenomAdherent.setText("");
                lbEmailAdherent.setText("");
                lbTitreLivre.setText("");
                lbDateEmprunt.setText("");
                lbDateRetour.setText("");
                tfIdAdherent.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Aucun emprunt trouvé pour cet adhérent.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void envoyerMail() {
        String email = lbEmailAdherent.getText();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'adresse e-mail de l'adhérent est vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sujet = "Rappel de Retour de Livre";
        String message = "Bonjour " + lbPrenomAdherent.getText() + " " + lbNomAdherent.getText() + ",\n\n" +
                "Nous vous rappelons que vous devez retourner le livre emprunté:\n" +
                "Titre: " + lbTitreLivre.getText() + "\n" +
                "Emprunter le : " + lbDateEmprunt.getText() + "\n" +
                "dont le retour est prévue le: " + lbDateRetour.getText() + "\n\n" +"dans exactement 2 jours"+
                "Merci de le retourner dès que possible.\n" +
                "Cordialement,\n" +
                "UTA - Bibliothèque";

        try {
            String encodedSujet = URLEncoder.encode(sujet, StandardCharsets.UTF_8.toString()).replace("+", "%20");
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                    .replace("%0A", "%0D%0A"); // For newline

            String uriStr = "mailto:" + email + "?subject=" + encodedSujet + "&body=" + encodedMessage;
            Desktop.getDesktop().mail(new URI(uriStr));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new RetourForm();
    }
}
