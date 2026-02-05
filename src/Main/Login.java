
package Main;

import javax.swing.JFrame;


public class Login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());

    
    public Login() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/home.jpg"))); // NOI18N
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
        jPanel4.add(registerbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 380, 410, -1));

        forgetbtn.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        forgetbtn.setText("Forget Password");
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
        home_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        home_btn.setForeground(new java.awt.Color(255, 255, 255));
        home_btn.setText("HOME");
        home_btn.setBorder(null);
        home_btn.setContentAreaFilled(false);
        home_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_btnActionPerformed(evt);
            }
        });
        jPanel3.add(home_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

        reg_btn.setBackground(new java.awt.Color(0, 0, 0));
        reg_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        reg_btn.setForeground(new java.awt.Color(255, 255, 255));
        reg_btn.setText("REGISTER");
        reg_btn.setBorder(null);
        reg_btn.setContentAreaFilled(false);
        reg_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_btnActionPerformed(evt);
            }
        });
        jPanel3.add(reg_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 30, -1, -1));

        login_btn.setBackground(new java.awt.Color(0, 0, 0));
        login_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        login_btn.setForeground(new java.awt.Color(255, 255, 255));
        login_btn.setText("LOGIN");
        login_btn.setBorder(null);
        login_btn.setContentAreaFilled(false);
        login_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_btnActionPerformed(evt);
            }
        });
        jPanel3.add(login_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 30, -1, -1));

        aboutus_btn.setBackground(new java.awt.Color(0, 0, 0));
        aboutus_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        aboutus_btn.setForeground(new java.awt.Color(255, 255, 255));
        aboutus_btn.setText("ABOUT US");
        aboutus_btn.setBorder(null);
        aboutus_btn.setContentAreaFilled(false);
        aboutus_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutus_btnActionPerformed(evt);
            }
        });
        jPanel3.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 30, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jTextField2.setBackground(new java.awt.Color(0, 0, 0));
        jTextField2.setFont(new java.awt.Font("Script MT Bold", 1, 48)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jTextField2.setText("Efind");
        jTextField2.setBorder(null);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanel3.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loginbtnActionPerformed

    private void home_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_home_btnActionPerformed
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_home_btnActionPerformed

    private void reg_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_btnActionPerformed
        Register register = new Register();
        register.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reg_btnActionPerformed

    private void login_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login_btnActionPerformed
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_login_btnActionPerformed

    private void aboutus_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutus_btnActionPerformed
        AboutUs aboutus = new AboutUs();
        aboutus.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_aboutus_btnActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void usernamefeildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamefeildActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamefeildActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton forgetbtn;
    private javax.swing.JButton home_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton login_btn;
    private javax.swing.JButton loginbtn;
    private javax.swing.JLabel pword;
    private javax.swing.JPasswordField pwordfeild;
    private javax.swing.JButton reg_btn;
    private javax.swing.JButton registerbtn;
    private javax.swing.JLabel username;
    private javax.swing.JTextField usernamefeild;
    // End of variables declaration//GEN-END:variables
}
