import com.toedter.calendar.JDateChooser;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbsenceForm extends JFrame {
    private JButton btnSelection;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnVider;
    private JPanel DateParutionPanel;
    private JPanel LivrePanel;
    private JTable table1;
    private JTextField tfDateAbsence;
    private JTextField tfeure;
    private JTextField tfCode;
    private JButton btnAjouter;
    private JButton btnRecherche;
    private JLabel lbPhoto;
    private JTextField tfSemestre;
    private JTextField tfRecherche;
    private JTextField tfMatricule;
    private JTextField tfIdEnseignant;
    private JComboBox comboBox1;
    private JDateChooser dateOfParution;
    private Connection con;
    private PreparedStatement pst;
    private String photoPath;

    public AbsenceForm() {
        setTitle("Gestion Des Livres");
        setContentPane(LivrePanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        connect();
        table_load();

        // Initialisation de JDateChooser
        dateOfParution = new JDateChooser();

        // Ajout de JDateChooser au panneau DateParutionPanel
        DateParutionPanel.setLayout(new BorderLayout());
        DateParutionPanel.add(dateOfParution, BorderLayout.CENTER);

        btnSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPhoto();
            }
        });
        btnModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyLivre();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLivre();
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
                createLivre();
            }
        });
        btnRecherche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchLivre();
            }
        });
    }

    private void ViderChamps() {
        tfDateAbsence.setText("");
        tfeure.setText("");
        tfSemestre.setText("");
        tfCode.setText("");
        dateOfParution.setDate(null);

        tfRecherche.setText("");
        lbPhoto.setIcon(null);
        lbPhoto.setPreferredSize(new Dimension(100, 100)); // Ajuste la taille selon tes besoins
        lbPhoto.revalidate();
        lbPhoto.repaint();
        photoPath = null;

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

    private void createLivre() {
        String titre = tfDateAbsence.getText();
        String auteur = tfeure.getText();
        Date dateParution = dateOfParution.getDate();
        String nbrPage = tfSemestre.getText();
        String code = tfCode.getText();

        if (titre.isEmpty() || auteur.isEmpty() || dateParution == null || nbrPage.isEmpty() || photoPath == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs et Sélectionnez une Photo", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateParution = dateFormat.format(dateParution);

        try {
            String query = "INSERT INTO livre (Titre, Auteur, Date_Parution, Nbr_Page, Code, Photo) VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, titre);
            pst.setString(2, auteur);
            pst.setString(3, formattedDateParution);
            pst.setString(4, nbrPage);
            pst.setString(5, code);
            pst.setString(6, photoPath); // Enregistrement du chemin de la photo
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Inscription Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Réinitialiser les champs
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM livre");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            photoPath = selectedFile.getAbsolutePath();
            ImageIcon imageIcon = new ImageIcon(photoPath);
            Image image = imageIcon.getImage();

            // Vérifiez que lbPhoto a des dimensions valides avant de redimensionner l'image
            int width = lbPhoto.getWidth();
            int height = lbPhoto.getHeight();
            if (width > 0 && height > 0) {
                Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lbPhoto.setIcon(new ImageIcon(resizedImage));
            } else {
                // Définissez une taille préférée si les dimensions sont invalides
                lbPhoto.setPreferredSize(new Dimension(100, 100));
                lbPhoto.revalidate();
                lbPhoto.repaint();
            }
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }



    private void modifyLivre() {
        String idLivre = tfRecherche.getText(); // Récupérer l'identifiant du livre
        String titre = tfDateAbsence.getText();
        String auteur = tfeure.getText();
        Date dateparution = dateOfParution.getDate();
        String nbrpage = tfSemestre.getText();
        String code = tfCode.getText();

        if (titre.isEmpty() || auteur.isEmpty() || dateparution == null || nbrpage.isEmpty() || code.isEmpty() || photoPath == null) {
            JOptionPane.showMessageDialog(AbsenceForm.this, "Svp Remplissez Tous les Champs et Sélectionnez une Photo", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateParution = dateFormat.format(dateparution);
        try {
            String query = "UPDATE livre SET Titre=?, Auteur=?, Date_Parution=?, Nbr_Page=?, Code=?, Photo=? WHERE Id_Livre=?";
            pst = con.prepareStatement(query);
            pst.setString(1, titre);
            pst.setString(2, auteur);
            pst.setString(3, formattedDateParution);
            pst.setString(4, nbrpage);
            pst.setString(5, code);
            pst.setString(6, photoPath);
            pst.setString(7, idLivre);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(AbsenceForm.this, "Modification Réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
                table_load();
                ViderChamps();
            } else {
                JOptionPane.showMessageDialog(AbsenceForm.this, "Aucun livre trouvé avec cet identifiant", "Attention", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void deleteLivre() {
        String Id_Livre = tfRecherche.getText();

        if (Id_Livre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer l'ID du livre à supprimer", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM livre WHERE Id_Livre = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, Id_Livre);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Livre Supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
                ViderChamps();
                table_load();
            } else {
                JOptionPane.showMessageDialog(this, "ID Livre invalide", "Attention", JOptionPane.ERROR_MESSAGE);
            }
            ViderChamps();
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchLivre() {
        try {
            String IdLivre = tfRecherche.getText();
            pst = con.prepareStatement("SELECT Titre, Auteur, Date_Parution, Nbr_Page, Code, Photo FROM livre WHERE Id_Livre=?");
            pst.setString(1, IdLivre);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String titre = rs.getString(1);
                String auteur = rs.getString(2);
                Date dateParution = rs.getDate(3);
                String nbrPage = rs.getString(4);
                String code = rs.getString(5);
                String photo = rs.getString(6);

                tfDateAbsence.setText(titre);
                tfeure.setText(auteur);
                dateOfParution.setDate(dateParution);
                tfSemestre.setText(nbrPage);
                tfCode.setText(code);

                if (photo != null && !photo.isEmpty()) {
                    photoPath = photo; // Mise à jour de photoPath
                    ImageIcon imageIcon = new ImageIcon(photo);
                    Image image = imageIcon.getImage();
                    int width = lbPhoto.getWidth();
                    int height = lbPhoto.getHeight();

                    if (width > 0 && height > 0) {
                        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        lbPhoto.setIcon(new ImageIcon(resizedImage));
                    } else {
                        lbPhoto.setIcon(imageIcon);
                    }
                } else {
                    lbPhoto.setIcon(null);
                    lbPhoto.setPreferredSize(new Dimension(100, 100));
                    lbPhoto.revalidate();
                    lbPhoto.repaint();
                }
            } else {
                ViderChamps();
                lbPhoto.setPreferredSize(new Dimension(100, 100));
                lbPhoto.revalidate();
                lbPhoto.repaint();
                JOptionPane.showMessageDialog(null, "Aucun livre trouvé pour la recherche spécifiée", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            table_load();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la recherche de livres", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AbsenceForm();
    }


}
