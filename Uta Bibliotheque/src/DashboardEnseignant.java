import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DashboardEnseignant extends JFrame {
    private JComboBox<String> cbFiliereNiveau;
    private JComboBox<String> cbCours;
    private JTextField tfMatricule;
    private JTextField tfDateAbsence;
    private JTextField tfMotif;
    private JTextField tfDuree;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnVider;
    private JTable tableEtudiants;
    private Connection con;
    private PreparedStatement pst;
    private JLabel lblDateHeure;
    private String dbType = "mysql"; // Change to "postgresql" for PostgreSQL

    public DashboardEnseignant() {
        setTitle("Tableau de Bord Enseignant");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Panneau principal
        JPanel AbsencePanel = new JPanel(new BorderLayout());
        setContentPane(AbsencePanel);

        // Panel pour les listes déroulantes
        JPanel panelSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbFiliereNiveau = new JComboBox<>(new String[]{"IGL-L1", "IGL-L2", "IGL-L3","RIT-L1","RIT-L2","RIT-L3","FBA-L1","FBA-L2","FBA-L3","SEG-L1","SEG-L2","SEG-L3"});
        cbCours = new JComboBox<>(new String[]{"Algorithmique", "Base de données", "Programmation Java"});
        cbFiliereNiveau.setPreferredSize(new Dimension(200, 30));
        cbCours.setPreferredSize(new Dimension(200, 30));

        panelSelection.add(new JLabel("Filière_Niveau:"));
        panelSelection.add(cbFiliereNiveau);
        panelSelection.add(new JLabel("Cours:"));
        panelSelection.add(cbCours);

        // Ajouter le texte "Enseignant(e) : Kouakou Yann" à droite
        JLabel lblEnseignant = new JLabel("Enseignant(e) : Kouakou Yann / Id : 3");

        // Utilisation de FlowLayout.RIGHT pour aligner le texte à droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(lblEnseignant);

        panelSelection.add(rightPanel);

        AbsencePanel.add(panelSelection, BorderLayout.NORTH);

        // Formulaire
        tfMatricule = new JTextField();
        tfDateAbsence = new JTextField();
        tfMotif = new JTextField();
        tfDuree = new JTextField();

        tfMatricule.setPreferredSize(new Dimension(200, 30));
        tfDateAbsence.setPreferredSize(new Dimension(200, 30));
        tfMotif.setPreferredSize(new Dimension(200, 30));
        tfDuree.setPreferredSize(new Dimension(200, 30));

        // Création des boutons avec des couleurs spécifiques
        btnAjouter = createButton("Ajouter", Color.BLUE, Color.WHITE);
        btnModifier = createButton("Modifier", Color.GRAY, Color.WHITE);
        btnSupprimer = createButton("Supprimer", new Color(139, 69, 19), Color.WHITE); // Marron
        btnVider = createButton("Vider", Color.GREEN, Color.WHITE);

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Matricule Etudiant:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfMatricule, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Date Absence:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfDateAbsence, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Motif:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfMotif, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Durée:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfDuree, gbc);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelButtons.add(btnAjouter);
        panelButtons.add(btnModifier);
        panelButtons.add(btnSupprimer);
        panelButtons.add(btnVider);

        lblDateHeure = new JLabel();
        lblDateHeure.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(panelButtons, BorderLayout.WEST);
        panelBottom.add(lblDateHeure, BorderLayout.EAST);

        // Table des étudiants à afficher à droite
        tableEtudiants = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableEtudiants);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelForm, scrollPane);
        splitPane.setDividerLocation(350);

        AbsencePanel.add(splitPane, BorderLayout.CENTER);
        AbsencePanel.add(panelBottom, BorderLayout.SOUTH);

        connect();

        // Initialiser la méthode pour afficher les étudiants au choix d'une filière
        cbFiliereNiveau.addActionListener(e -> filiereSelectionnee());

        Timer timer = new Timer(1000, e -> mettreAJourHeure());
        timer.start();

        setVisible(true);

        // Gestion des boutons
        btnAjouter.addActionListener(e -> ajouterAbsence());
        btnModifier.addActionListener(e -> modifierAbsence());
        btnSupprimer.addActionListener(e -> supprimerAbsence());
        btnVider.addActionListener(e -> viderChamps());
    }

    // Méthode pour créer un bouton avec des couleurs spécifiques
    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    private void mettreAJourHeure() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblDateHeure.setText(dateFormat.format(new Date()));
    }

    public void connect() {
        try {
            Class.forName("com.postgresql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost/GestionDesAbsences_Uta", "postgres", "29122003");
            System.out.println("Succès");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    // Affichage des étudiants selon la filière et niveau sélectionnés
    private void filiereSelectionnee() {
        String filiereNiveau = (String) cbFiliereNiveau.getSelectedItem();

        try {
            // Requête pour obtenir les étudiants de la filière et du niveau sélectionnés
            String query = "SELECT * FROM Etudiant WHERE Filiere_Niveau = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, filiereNiveau);
            ResultSet rs = pst.executeQuery();

            // Création du modèle de table pour afficher les résultats
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Matricule", "Nom", "Prénom", "Filière_Niveau", "EmailEtudiant", "IdFormation"});

            // Remplir la table avec les résultats de la requête
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Matricule"),
                        rs.getString("NomEtudiant"),
                        rs.getString("PrenomEtudiant"),
                        rs.getString("Filiere_Niveau"),
                        rs.getString("EmailEtudiant"),
                        rs.getString("IdFormation")
                });
            }

            // Affichage des résultats dans la JTable
            tableEtudiants.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ajouterAbsence() {
        if (tfMatricule.getText().isEmpty() || tfDateAbsence.getText().isEmpty() ||
                tfMotif.getText().isEmpty() || tfDuree.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez renseigner tous les champs.", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO Absences (Matricule, DateAbsence, Motif, Duree) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, tfMatricule.getText());
            pst.setString(2, tfDateAbsence.getText());
            pst.setString(3, tfMotif.getText());
            pst.setString(4, tfDuree.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Absence ajoutée avec succès.");
            viderChamps();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'absence.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierAbsence() {
        if (tfMatricule.getText().isEmpty() || tfDateAbsence.getText().isEmpty() ||
                tfMotif.getText().isEmpty() || tfDuree.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez renseigner tous les champs.", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE Absences SET DateAbsence = ?, Motif = ?, Duree = ? WHERE Matricule = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, tfDateAbsence.getText());
            pst.setString(2, tfMotif.getText());
            pst.setString(3, tfDuree.getText());
            pst.setString(4, tfMatricule.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Absence modifiée avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'absence.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerAbsence() {
        if (tfMatricule.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le matricule de l'étudiant.", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "DELETE FROM Absences WHERE Matricule = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, tfMatricule.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Absence supprimée avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'absence.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viderChamps() {
        tfMatricule.setText("");
        tfDateAbsence.setText("");
        tfMotif.setText("");
        tfDuree.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DashboardEnseignant::new);
    }
}