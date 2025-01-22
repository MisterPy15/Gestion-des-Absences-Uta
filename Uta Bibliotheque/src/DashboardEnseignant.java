import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AbsenceForm extends JFrame {
    private JComboBox<String> cbFiliereNiveau;
    private JComboBox<String> cbCours;
    private JTextField tfMatricule;
    private JTextField tfIdEnseignant;
    private JComboBox<String> cbDateAbsence; // Modifié en JComboBox pour la date
    private JComboBox<String> cbHeures;
    private JComboBox<String> cbSemestre;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnVider;
    private JTable tableEtudiants;
    private Connection con;
    private PreparedStatement pst;
    private JLabel lblDateHeure;

    public AbsenceForm() {
        setTitle("Tableau de Bord Enseignant");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Panneau principal
        JPanel AbsencePanel = new JPanel(new BorderLayout());
        setContentPane(AbsencePanel);

        // Panel pour les listes déroulantes
        JPanel panelSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbFiliereNiveau = new JComboBox<>(new String[] {
            "IGL-L1", "IGL-L2", "IGL-L3", "RIT-L1", "RIT-L2", "RIT-L3",
            "FBA-L1", "FBA-L2", "FBA-L3", "SEG-L1", "SEG-L2", "SEG-L3"
        });
        cbCours = new JComboBox<>(new String[]{"Algorithmique", "Base de données", "Programmation Java"});
        cbFiliereNiveau.setPreferredSize(new Dimension(200, 30));
        cbCours.setPreferredSize(new Dimension(200, 30));

        panelSelection.add(new JLabel("Filière_Niveau:"));
        panelSelection.add(cbFiliereNiveau);
        panelSelection.add(new JLabel("Module:"));
        panelSelection.add(cbCours);

        JLabel lblEnseignant = new JLabel("Enseignant(e) : Kouakou Yann / Id : 3");
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(lblEnseignant);
        panelSelection.add(rightPanel);

        AbsencePanel.add(panelSelection, BorderLayout.NORTH);

        // Formulaire
        tfMatricule = new JTextField();
        tfIdEnseignant = new JTextField(); // Champ modifiable pour l'Id Enseignant
        cbDateAbsence = new JComboBox<>(generateDatesForComboBox()); // Utilisation du JComboBox pour la date
        cbHeures = new JComboBox<>(new String[]{"08:00-12:00", "13:00-17:00"});
        cbSemestre = new JComboBox<>(new String[]{"Semestre 1", "Semestre 2"});

        tfMatricule.setPreferredSize(new Dimension(200, 30));
        tfIdEnseignant.setPreferredSize(new Dimension(200, 30));
        cbDateAbsence.setPreferredSize(new Dimension(200, 30)); // Remplacement de JTextField par JComboBox
        cbHeures.setPreferredSize(new Dimension(200, 30));
        cbSemestre.setPreferredSize(new Dimension(200, 30));

        btnAjouter = createButton("Ajouter", Color.BLUE, Color.WHITE);
        btnModifier = createButton("Modifier", Color.GRAY, Color.WHITE);
        btnSupprimer = createButton("Supprimer", new Color(139, 69, 19), Color.WHITE);
        btnVider = createButton("Vider", Color.GREEN, Color.WHITE);

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Matricule Étudiant:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfMatricule, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Id Enseignant:"), gbc);
        gbc.gridx = 1;
        panelForm.add(tfIdEnseignant, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        panelForm.add(cbDateAbsence, gbc); // Ajout du JComboBox pour la date

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Heures:"), gbc);
        gbc.gridx = 1;
        panelForm.add(cbHeures, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelForm.add(new JLabel("Semestre:"), gbc);
        gbc.gridx = 1;
        panelForm.add(cbSemestre, gbc);

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

        tableEtudiants = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableEtudiants);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelForm, scrollPane);
        splitPane.setDividerLocation(350);

        AbsencePanel.add(splitPane, BorderLayout.CENTER);
        AbsencePanel.add(panelBottom, BorderLayout.SOUTH);

        connect();

        cbFiliereNiveau.addActionListener(e -> filiereSelectionnee());

        Timer timer = new Timer(1000, e -> mettreAJourHeure());
        timer.start();

        setVisible(true);

        btnAjouter.addActionListener(e -> ajouterAbsence());
        btnModifier.addActionListener(e -> modifierAbsence());
        btnSupprimer.addActionListener(e -> supprimerAbsence());
        btnVider.addActionListener(e -> viderChamps());
    }

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

    private String[] generateDatesForComboBox() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String currentDateStr = dateFormat.format(currentDate);
        return new String[]{currentDateStr}; // Affiche la date actuelle
    }

    public void connect() {
        String url = "jdbc:postgresql://localhost:5432/GestionDesAbsences_Uta";
        String user = "postgres";
        String password = "29122003";

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie à la base de données");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filiereSelectionnee() {
        String filiereNiveau = (String) cbFiliereNiveau.getSelectedItem();

        try {
            String query = "SELECT * FROM Etudiant WHERE Filiere_Niveau = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, filiereNiveau);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Matricule", "Nom", "Prénom", "Filière_Niveau", "EmailEtudiant", "IdFormation"});

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("Matricule"),
                        rs.getString("NomEtudiant"),
                        rs.getString("PrenomEtudiant"),
                        rs.getString("Filiere_Niveau"),
                        rs.getString("EmailEtudiant"),
                        rs.getString("IdFormation")
                });
            }

            tableEtudiants.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ajouterAbsence() {
        if (tfMatricule.getText().isEmpty() || cbDateAbsence.getSelectedItem() == null ||
            cbHeures.getSelectedItem() == null || cbSemestre.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez renseigner tous les champs.", "Champs vides", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO Absences (Matricule, Id_Enseignant, DateAbsence, Heures, Semestre) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, tfMatricule.getText());
            pst.setString(2, tfIdEnseignant.getText());
            pst.setString(3, cbDateAbsence.getSelectedItem().toString());
            pst.setString(4, cbHeures.getSelectedItem().toString());
            pst.setString(5, cbSemestre.getSelectedItem().toString());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Absence ajoutée avec succès.");
            viderChamps();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierAbsence() {
        // Ajoutez ici le code pour modifier une absence
    }

    private void supprimerAbsence() {
        // Ajoutez ici le code pour supprimer une absence
    }

    private void viderChamps() {
        tfMatricule.setText("");
        cbDateAbsence.setSelectedIndex(0); // Réinitialise la sélection de la date
        tfIdEnseignant.setText("");  // Remise à zéro du champ Id Enseignant
        cbHeures.setSelectedIndex(-1);
        cbSemestre.setSelectedIndex(-1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AbsenceForm::new);
    }
}
