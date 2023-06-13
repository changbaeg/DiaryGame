import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Login extends JFrame {
    JPanel panel;
    CardLayout card;
    String id = null;
    Connection con = null;

    final String driver = "com.mysql.jdbc.Driver";
    final String url = "jdbc:mysql://localhost:3306/diary?zeroDateTimeBehavior=convertToNull";
    final String user = "root";
    final String password = "1234";

    public static void main(String[] args) {
        Login lp = new Login();
        lp.setFrame(lp);
    }

    public void setFrame(Login lpro) {
        LoginPanel lp = new LoginPanel(lpro);
        SignUpPanel sp = new SignUpPanel(lpro);

        card = new CardLayout();
        panel = new JPanel(card);
        panel.add(lp.mainPanel, "Login");
        panel.add(sp.mainPanel, "Register");

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public Connection getCon() throws SQLException {
        if (con == null) {
            connect();
            if (con == null) {
                getCon();
            }
        }
        return con;
    }

    private void connect() {
        if (con == null) {
            try {
                Class.forName(driver);
                this.con = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (this.con == null) {
                System.out.println("DB 연결 실패");
            } else {
                System.out.println("DB 연결 성공");
            }
        }
    }

    void closeDB() {
        if (this.con != null) {
            try {
                this.con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}

class LoginPanel extends JPanel implements ActionListener {
    JPanel mainPanel;
    JTextField idText;
    JPasswordField passText;
    Login lp;
    Font font = new Font("로그인", Font.PLAIN, 50);
    String admin = "admin";

    public LoginPanel(Login lp) {
        this.lp = lp;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel centerPanel = new JPanel();
        JLabel loginLabel = new JLabel("로그인 화면");
        loginLabel.setFont(font);
        centerPanel.add(loginLabel);

        JPanel userPanel = new JPanel();

        JPanel gridBagidInfo = new JPanel(new GridBagLayout());
        gridBagidInfo.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints c = new GridBagConstraints();

        JLabel idLabel = new JLabel(" 아이디 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        gridBagidInfo.add(idLabel, c);

        idText = new JTextField(15);
        c.insets = new Insets(0, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 0;
        gridBagidInfo.add(idText, c);

        JLabel passLabel = new JLabel(" 비밀번호 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(20, 0, 0, 0);
        gridBagidInfo.add(passLabel, c);

        passText = new JPasswordField(15);
        c.insets = new Insets(20, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 1;
        gridBagidInfo.add(passText, c);

        JPanel loginPanel = new JPanel();
        JButton loginButton = new JButton("로그인");
        loginPanel.add(loginButton);

        JPanel signupPanel = new JPanel();
        JButton signupButton = new JButton("회원가입");
        loginPanel.add(signupButton);

        mainPanel.add(centerPanel);
        mainPanel.add(userPanel);
        mainPanel.add(gridBagidInfo);
        mainPanel.add(loginPanel);
        mainPanel.add(signupPanel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();

                String id = idText.getText();
                String pass = new String(passText.getPassword());

                try {
                    String sqlQuery = String.format("SELECT password FROM user_info WHERE user_id = '%s' AND password ='%s'", id, pass);

                    Connection c = lp.getCon();
                    Statement stmt = c.createStatement();

                    ResultSet rset = stmt.executeQuery(sqlQuery);
                    rset.next();
                    if (pass.equals(rset.getString(1))) {
                        JOptionPane.showMessageDialog(null, "Login Success", "로그인 성공", 1);
                        Home a = new Home();
                        lp.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Login Failed", "로그인 실패", 1);
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Login Failed", "로그인 실패", 1);
                    System.out.println("SQLException" + ex);
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lp.card.next(lp.panel);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
}

class SignUpPanel {
    JTextField idTf;
    JPasswordField passTf;
    JPasswordField passReTf;
    JPanel mainPanel;
    JPanel subPanel;
    JRadioButton menButton;
    JButton registerButton;
    Font font = new Font("회원가입", Font.BOLD, 40);
    String id = "", pass = "", passRe = "";
    Login lp;

    public SignUpPanel(Login lp) {
        this.lp = lp;
        subPanel = new JPanel();
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel idLabel = new JLabel("아이디 : ");
        JLabel passLabel = new JLabel("비밀번호 : ");
        JLabel passReLabel = new JLabel("비밀번호 재확인 : ");

        idTf = new JTextField(15);
        passTf = new JPasswordField(15);
        passReTf = new JPasswordField(15);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(15, 5, 0, 0);

        c.gridx = 0;
        c.gridy = 0;
        subPanel.add(idLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        subPanel.add(idTf, c); // 아이디

        c.gridx = 0;
        c.gridy = 1;
        subPanel.add(passLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        subPanel.add(passTf, c); // pass

        c.gridx = 2;
        c.gridy = 1;
        subPanel.add(new JLabel("최소 4자"), c); // 보안설정

        c.gridx = 0;
        c.gridy = 2;
        subPanel.add(passReLabel, c);

        c.gridx = 1;
        c.gridy = 2;
        subPanel.add(passReTf, c); // password 재확인

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JLabel signupLabel = new JLabel("회원가입 화면");
        signupLabel.setFont(font);
        signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton = new JButton("회원가입");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(signupLabel);
        mainPanel.add(subPanel);
        mainPanel.add(registerButton);

        registerButton.addActionListener(new ActionListener() { // 회원가입버튼
            public void actionPerformed(ActionEvent e) {
                id = idTf.getText();
                pass = new String(passTf.getPassword());
                passRe = new String(passReTf.getPassword());

                String sql = "insert into user_info(id, password) values (?,?)";

                if (!pass.equals(passRe)) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 서로 맞지 않습니다", "비밀번호 오류", 1);
                } else {
                    try {
                        Connection conn = lp.getCon();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, idTf.getText());
                        pstmt.setString(2, pass);
                        int r = pstmt.executeUpdate();
                        System.out.println("변경된 row " + r);
                        JOptionPane.showMessageDialog(null, "회원 가입 완료!", "회원가입", 1);
                        lp.card.previous(lp.panel); // 다 완료되면 로그인 화면으로
                    } catch (SQLException e1) {
                        System.out.println("SQL error" + e1.getMessage());
                        if (e1.getMessage().contains("PRIMARY")) {
                            JOptionPane.showMessageDialog(null, "아이디 중복!", "아이디 중복 오류", 1);
                        } else {
                            JOptionPane.showMessageDialog(null, "정보를 제대로 입력해주세요!", "오류", 1);
                        }
                    } // try, catch
                }
            }
        });
    }
}
