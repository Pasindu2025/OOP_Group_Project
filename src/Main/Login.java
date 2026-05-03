
package Main;

import javax.swing.JFrame;
import forgetPassword.forgetpassword;

public class Login extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());


    public Login() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        pword = new javax.swing.JLabel();
        pwordfeild = new javax.swing.JPasswordField();
        loginbtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        registerbtn = new javax.swing.JButton();
        forgetbtn = new javax.swing.JButton();
        usernamefeild = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        home_btn = new javax.swing.JButton();
        reg_btn = new javax.swing.JButton();
        login_btn = new javax.swing.JButton();
        aboutus_btn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo1.jpg"))); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 690, 640));

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(0, 51, 51));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("LOGIN");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 130, -1));

        username.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setText("Username");
        jPanel4.add(username, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, -1, -1));

        pword.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        pword.setForeground(new java.awt.Color(255, 255, 255));
        pword.setText("Password");
        jPanel4.add(pword, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 210, -1, -1));

        pwordfeild.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jPanel4.add(pwordfeild, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, 220, -1));

        loginbtn.setBackground(new java.awt.Color(255, 153, 0));
        loginbtn.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        loginbtn.setText("Login");
        loginbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtnActionPerformed(evt);
            }
        });
        jPanel4.add(loginbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 300, 410, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("If You Are Not Registered Please Register Before Login");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, -1, -1));

        registerbtn.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        registerbtn.setText("Register");
        registerbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerbtnActionPerformed(evt);
            }
        });
        jPanel4.add(registerbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 380, 410, -1));

        forgetbtn.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        forgetbtn.setText("Forget Password");
        forgetbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgetbtnActionPerformed(evt);
            }
        });
        jPanel4.add(forgetbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 460, 410, -1));

        usernamefeild.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        usernamefeild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernamefeildActionPerformed(evt);
            }
        });
        jPanel4.add(usernamefeild, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, 220, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 70, 570, 540));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1570, 800));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        home_btn.setBackground(new java.awt.Color(0, 0, 0));
        home_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        home_btn.setForeground(new java.awt.Color(255, 255, 255));
        home_btn.setText("HOME");
        home_btn.setBorder(null);
        home_btn.setContentAreaFilled(false);
        home_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                home_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                home_btnMouseExited(evt);
            }
        });
        home_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_btnActionPerformed(evt);
            }
        });
        jPanel3.add(home_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

        reg_btn.setBackground(new java.awt.Color(0, 0, 0));
        reg_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        reg_btn.setForeground(new java.awt.Color(255, 255, 255));
        reg_btn.setText("REGISTER");
        reg_btn.setBorder(null);
        reg_btn.setContentAreaFilled(false);
        reg_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reg_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reg_btnMouseExited(evt);
            }
        });
        reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_btnActionPerformed(evt);
            }
        });
        jPanel3.add(reg_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 30, -1, -1));

        login_btn.setBackground(new java.awt.Color(0, 0, 0));
        login_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        login_btn.setForeground(new java.awt.Color(255, 255, 255));
        login_btn.setText("LOGIN");
        login_btn.setBorder(null);
        login_btn.setContentAreaFilled(false);
        login_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                login_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                login_btnMouseExited(evt);
            }
        });
        login_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_btnActionPerformed(evt);
            }
        });
        jPanel3.add(login_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 30, -1, -1));

        aboutus_btn.setBackground(new java.awt.Color(0, 0, 0));
        aboutus_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        aboutus_btn.setForeground(new java.awt.Color(255, 255, 255));
        aboutus_btn.setText("ABOUT US");
        aboutus_btn.setBorder(null);
        aboutus_btn.setContentAreaFilled(false);
        aboutus_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aboutus_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aboutus_btnMouseExited(evt);
            }
        });
        aboutus_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutus_btnActionPerformed(evt);
            }
        });
        jPanel3.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 30, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5.setFont(new java.awt.Font("Script MT Bold", 1, 45)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Efind");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, -1));

        pack();
    }// </editor-fold>

    private void loginbtnActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // 1. TextFields wala thiyana data tika gannawa
            String uName = usernamefeild.getText();
            String pWord = new String(pwordfeild.getPassword());

            // Simple validation - Check filds are empty
            if (uName.isEmpty() || pWord.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please enter both Username and Password!");
                return;
            }

            // 2.To prepare JSON format to send backend
            String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", uName, pWord);

            // 3. use HttpClient  and send request to the backend
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8081/api/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // 4. login after check the response
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenApply(java.net.http.HttpResponse::body)
                    .thenAccept(response -> {
                        if (response.equals("Success")) {
                            java.awt.EventQueue.invokeLater(() -> {

                                User.Main userMain = new User.Main();
                                userMain.setVisible(true);

                                // close login frame
                                this.dispose();
                            });
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
                        }
                    })
                    .exceptionally(ex -> {
                        java.awt.EventQueue.invokeLater(() ->
                                javax.swing.JOptionPane.showMessageDialog(this, "Backend connection failed! Check if Spring Boot is running."));
                        return null;
                    });

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void home_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }

    private void reg_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Register register = new Register();
        register.setVisible(true);
        this.dispose();
    }

    private void login_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }

    private void aboutus_btnActionPerformed(java.awt.event.ActionEvent evt) {
        AboutUs aboutus = new AboutUs();
        aboutus.setVisible(true);
        this.dispose();
    }

    private void usernamefeildActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void home_btnMouseEntered(java.awt.event.MouseEvent evt) {
        home_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void home_btnMouseExited(java.awt.event.MouseEvent evt) {
        home_btn.setForeground(java.awt.Color.WHITE);
    }

    private void reg_btnMouseEntered(java.awt.event.MouseEvent evt) {
        reg_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void reg_btnMouseExited(java.awt.event.MouseEvent evt) {
        reg_btn.setForeground(java.awt.Color.WHITE);
    }

    private void login_btnMouseEntered(java.awt.event.MouseEvent evt) {
        login_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void login_btnMouseExited(java.awt.event.MouseEvent evt) {
        login_btn.setForeground(java.awt.Color.WHITE);
    }

    private void aboutus_btnMouseEntered(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void aboutus_btnMouseExited(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(java.awt.Color.WHITE);
    }

    private void registerbtnActionPerformed(java.awt.event.ActionEvent evt) {
        Register reg = new Register();
        reg.setVisible(true);
        this.dispose();
    }

    private void forgetbtnActionPerformed(java.awt.event.ActionEvent evt) {
        forgetpassword fp = new forgetpassword();
        fp.setVisible(true);
        this.dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton forgetbtn;
    private javax.swing.JButton home_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JButton login_btn;
    private javax.swing.JButton loginbtn;
    private javax.swing.JLabel pword;
    private javax.swing.JPasswordField pwordfeild;
    private javax.swing.JButton reg_btn;
    private javax.swing.JButton registerbtn;
    private javax.swing.JLabel username;
    private javax.swing.JTextField usernamefeild;
    // End of variables declaration                   
}
