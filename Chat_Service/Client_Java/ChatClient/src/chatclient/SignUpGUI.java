/*
 
*Function : SIGNUPGUI
*Description:  Sign Up Account.
*Argument: Nope!
*Return: Nope!
Note: Sign Up Interface 

 */
package chatclient;

import javax.swing.JOptionPane;

/**
 *
 * @author hieupham
 */
public class SignUpGUI extends javax.swing.JFrame {

    /**
     * Creates new form SignUpGUI
     */
    public String sUser,sPass,sRepass;
    Global g ;
    Thread waitthr;
    ProtocolCS p ;
    public SignUpGUI() {
        initComponents();
        g = Global.getInstance();
        p = new ProtocolCS();
        waitthr = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true){
                                try{
                                    if (g.client.IsDataReceived() == 1){
                                        btnSignUp.setEnabled(true);
                                    }
                                    if (g.client.GetCommandCode() == g.client.SIGNUPOK){
                                        g.SetUserName(txtUser.getText());
                                        g.SetPass(txtPass.getText());
                                        g.SetIDUser(g.client.GetIDUser());
                                        p.username = txtUser.getText();
                                        p.password = txtPass.getText();
                                        g.client.ClearData();
                                        MainGUI mainGui = new MainGUI();
                                        mainGui.setVisible(true);
                                        SignUpGUI.this.dispose();
                                        System.out.println("Successful create USER " + txtUser.getText());
                                        System.out.println("Join to Client GUI");   
                                        int mc = JOptionPane.WARNING_MESSAGE;
                                        JOptionPane.showMessageDialog (null, "Sign up Successful", "Warning", mc);
                                        waitthr.stop();
                                        
                                    }
                                    else if (g.client.GetCommandCode() == g.client.SIGNUPFAIL){
                                        int mc = JOptionPane.WARNING_MESSAGE;
                                        JOptionPane.showMessageDialog (null, "Sign up Successful", "Warning", mc);
                                    }
                                }catch (Exception e){
                                    
                                }
                            }
                        }
                    });
        if (!waitthr.isAlive())
            waitthr.start();
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSignUp = new javax.swing.JButton();
        txtUser = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        txtPass = new javax.swing.JPasswordField();
        txtRepass = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("SIGN UP");

        jLabel2.setText("Please enter your information below here");

        jLabel3.setText("Username");

        jLabel4.setText("Password");

        btnSignUp.setText("Sign Up ");
        btnSignUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignUpActionPerformed(evt);
            }
        });

        jLabel5.setText("RePassword");

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBack)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btnSignUp)
                                        .addGap(30, 30, 30))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtUser)
                                    .addComponent(txtPass)
                                    .addComponent(txtRepass))))))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRepass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSignUp)
                    .addComponent(btnBack))
                .addGap(90, 90, 90))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSignUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignUpActionPerformed
        // TODO add your handling code here:
        g = Global.getInstance();
        p = new ProtocolCS();
        sUser=txtUser.getText();
        sPass=txtPass.getText();
        sRepass=txtRepass.getText();
        
        // CHECK BLANK
        if(sUser.equals("") )
            {
                int mc = JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog (null, "Blank USER", "Warning", mc);
            }
            else if(sPass.equals("")){
                int mc2 = JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog (null, "Blank PASS", "Warning", mc2);
            }
            else if(!sPass.equals(sRepass)){
                int mc3 = JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog (null, "Repass FALSE", "Warning", mc3);
            }
            else{
                
                
                // GET RESPONE SERVER
                if(g.client.GetStatus() == true)
                {                 
                     
                    
                        g.client.SignUp(sUser, sPass);
                        
                        
                    btnSignUp.setEnabled(false);
                }
                else
                {
                   int mcServer = JOptionPane.WARNING_MESSAGE;
                   JOptionPane.showMessageDialog (null, "Cann't connect to Server", "Warning", mcServer);
                }
                
                
            }
    }//GEN-LAST:event_btnSignUpActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        MainGUI mainGui = new MainGUI();
        mainGui.setVisible(true);
        SignUpGUI.this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SignUpGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignUpGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignUpGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignUpGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignUpGUI().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSignUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JPasswordField txtRepass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
