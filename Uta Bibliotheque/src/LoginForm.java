import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton connectionButton;
    private JPanel loginPanel;
    private JComboBox<String> CbRole;
    public User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Authentification");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(815, 415));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        connectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());
                String role = (String) CbRole.getSelectedItem();

                user = getAuthentificateUser(email, password, role);

                if (user != null) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Connexion réussie, bon retour " + user.nom + " " + user.prenom,
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new DashoardForm(user); // Passer l'utilisateur authentifié au Dashboard
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email, mot de passe ou rôle incorrect",
                            "Attention",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private User getAuthentificateUser(String email, String password, String role) {
        User user = null;

        final String DB_URL = "jdbc:postgresql://localhost/GestionDesAbsences_Uta";
        final String USERNAME = "postgres";
        final String PASSWORD = "29122003";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "";
            if ("Admin".equals(role)) {
                sql = "SELECT * FROM Utilisateur WHERE Email=? AND MotDePasse=? AND Role='Admin'";
            } else if ("Enseignant".equals(role)) {
                sql = "SELECT * FROM Utilisateur WHERE Email=? AND MotDePasse=? AND Role='Enseignant'";
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.nom = resultSet.getString("Nom");
                        user.prenom = resultSet.getString("Prenom");
                        user.adresse = resultSet.getString("Adresse");
                        user.Mail = resultSet.getString("Email");
                        user.role = resultSet.getString("Role");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return user;
    }

    public static void main(String[] args) {
        new LoginForm(null);
    }
}