/*
 
*Function : ClientGUI
*Description: Client Interfaces. Chat General or Select User to Private Chat
*Argument: Nope!
*Return: Nope!
Note: First Interface 

 */
package chatclient;
import java.awt.Container;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
/**
 *
 * @author hieupham
 */

public class ClientGUI extends javax.swing.JFrame {

    /**
     * Creates new form ClientGUI
     */
    Global g = Global.getInstance();
    Thread waitThr;
    Thread MessThr;
    
    public ClientGUI() {
        initComponents();
        waitThr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (g.client.GetCommandCode() == g.client.ADDOK){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null, "Got", "Warning", mcServer);
                            waitThr.stop();
                        }
                        if (g.client.GetCommandCode() == g.client.PRVOK){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null, "PRV", "Warning", mcServer);
                            waitThr.stop();
                        }
                        if (g.client.GetCommandCode() == g.client.RECVPRV){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null,"PRV " + g.client.GetName() + g.client.GetMessage(), "Warning", mcServer);
                            g.client.ClearData();
                        }
                        if (g.client.GetCommandCode() == g.client.RECVROOM){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null,"ROOM " + g.client.GetName() + g.client.GetMessage(), "Warning", mcServer);
                            g.client.ClearData();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
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
        txtContent = new javax.swing.JTextField();
        txtChat = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lbRoom = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lbRoom1 = new javax.swing.JLabel();
        lbRoom2 = new javax.swing.JLabel();
        lbGENERAL = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Room Avaiable");

        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtChatKeyPressed(evt);
            }
        });

        jLabel2.setText("User Avaiable");

        lbRoom.setBackground(java.awt.Color.green);
        lbRoom.setForeground(java.awt.Color.magenta);
        lbRoom.setText("Room avaible dislay here");
        lbRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel3.setText("Hint: User here");

        lbRoom1.setText("Room N1");
        lbRoom1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbRoom1MouseClicked(evt);
            }
        });

        lbRoom2.setText("Room N2");
        lbRoom2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbRoom2MouseClicked(evt);
            }
        });

        lbGENERAL.setText("GENERAL");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbRoom1)
                                    .addComponent(lbRoom2))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtContent, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbGENERAL)
                        .addGap(319, 319, 319))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lbGENERAL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbRoom1)
                        .addGap(11, 11, 11)
                        .addComponent(lbRoom2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtContent))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbRoom1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbRoom1MouseClicked
        // TODO add your handling code here:
        //g.client.AddFriend("zzz");
        g.client.SendPrivateMessage("fxanhkhoa", "gogogo");
        if (!waitThr.isAlive())
            waitThr.start();
        JPanel pnBox=new JPanel();
        if(evt.getClickCount() % 2 == 0)
        {
            // Open Function Room
            
            RoomGUI room1 = new RoomGUI();
           
           room1.setVisible(true);
           
            
           this.dispose();  
            System.out.println("Double click !!");
            
            //
        }
    }//GEN-LAST:event_lbRoom1MouseClicked

    private void lbRoom2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbRoom2MouseClicked
        // TODO add your handling code here:
        waitThr.start();
        if(evt.getClickCount() % 2 == 0)
        {
            // Open Function Room
            RoomGUI room2 = new RoomGUI();           
            room2.setVisible(true);
            this.dispose();  
            System.out.println("Double click !!");
            
            //
        }
    }//GEN-LAST:event_lbRoom2MouseClicked

    private void txtChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyPressed
        // TODO add your handling code here:
         if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            System.out.println("SEND ");
            txtChat.setText("");
        }
    }//GEN-LAST:event_txtChatKeyPressed

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
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbGENERAL;
    private javax.swing.JLabel lbRoom;
    private javax.swing.JLabel lbRoom1;
    private javax.swing.JLabel lbRoom2;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextField txtContent;
    // End of variables declaration//GEN-END:variables
}
