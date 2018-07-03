/*
 
*Function : RoomGUI
*Description: Room Interfaces. Private Chat, Kick or Add User.
*Argument: Nope!
*Return: Nope!
Note: Comment!

 */
package chatclient;

 
import java.awt.event.KeyEvent;
 
import javax.swing.*;
import java.util.*;

/**
 *
 * @author hieupham
 */




public class RoomGUI extends javax.swing.JFrame {

    /**
     * Creates new form RoomGUI
     */
    
     /**
     * Variable
     */
    private DefaultListModel listModel;
    Thread waitThr;
    private String sUser;
    private int IDRoom;
     
    Global g = Global.getInstance();
    private boolean checkUser=true;
    
  
     /**
     * End line
     */
    public RoomGUI(int Name) {
        initComponents();
            
            listUser.setModel(new DefaultListModel());
            listModel = new DefaultListModel();
            lbRoomName.setText(String.valueOf(Name));
            IDRoom = Name;
         
            waitThr = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            // User add to room ok
                            if (g.client.GetCommandCode() == g.client.RADDOK){
                                listModel.addElement(sUser);
                                if (true){
                                   listUser.setModel(listModel);
                                   sUser = "";
                                }
                                g.client.ClearData();
                            }
                            else if (g.client.GetCommandCode() == g.client.RECVROOM){
//                                if (lbRoomName.getText().contains(g.client.GetName())){
//                                    txtContent.append(g.client.GetMessage() + "\n");
//                                }
                                g.client.ClearData();
                            }
                            else if (g.client.GetCommandCode() == g.client.RDELOK){
                                if (true){
                                    listModel.removeElement(sUser);
                                    listUser.setModel(listModel);
                                }
                                g.client.ClearData();
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
        waitThr.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbRoomName = new javax.swing.JLabel();
        lbHostName = new javax.swing.JLabel();
        btnAddUser = new javax.swing.JButton();
        btnKickUser = new javax.swing.JButton();
        txtChat = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lbRoomName.setText("ROOM");

        lbHostName.setText("HOST");

        btnAddUser.setText("AddUser");
        btnAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUserActionPerformed(evt);
            }
        });

        btnKickUser.setText("KickUser");
        btnKickUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKickUserActionPerformed(evt);
            }
        });

        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtChatKeyPressed(evt);
            }
        });

        jLabel1.setText("List");

        jScrollPane2.setViewportView(listUser);

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane1.setViewportView(txtContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbHostName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(64, 64, 64)
                        .addComponent(lbRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(305, 305, 305))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddUser)
                            .addComponent(btnKickUser)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(115, 115, 115)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 689, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(387, Short.MAX_VALUE)
                .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 689, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbHostName)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnKickUser)
                        .addGap(16, 16, 16)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
        // TODO add your handling code here:      
      
        int mc = JOptionPane.INFORMATION_MESSAGE;
	sUser = JOptionPane.showInputDialog (null, "Type User", "Add USer to Private Chat", mc);
        //System.out.println(sUser);
        
        g.client.AddFriendToRoom(lbRoomName.getText(),sUser); //sUser
        
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void txtChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            if (listUser.getModel().getSize() > 0){
                txtContent.append(txtChat.getText() + "\n");
                g.client.SendMsgToRoom(lbRoomName.getText(),g.GetUserName() + ": " +txtChat.getText());
                //System.out.println("SEND ");
                txtChat.setText("");
            }
            else{
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Room empty!!!", "Warning", mcServer);
            }
            txtChat.setText("");
        }
        
    }//GEN-LAST:event_txtChatKeyPressed

    private void btnKickUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKickUserActionPerformed
        // TODO add your handling code here:
        int mc = JOptionPane.INFORMATION_MESSAGE;
	sUser = JOptionPane.showInputDialog (null, "Type User", "Kick User ", mc);
        
        g.client.RemoveFriendFromRoom(sUser, IDRoom, "");
    }//GEN-LAST:event_btnKickUserActionPerformed

    /*public boolean checkAbleUser(String sUser)
{
        String sListUser;
        String sCompareUser= sUser+"\n";
        sListUser= this.listUser.getModel().toString();
        int size = this.listUser.getModel().getSize();
        StringBuilder sSplitUser = new StringBuilder();
        for(int i = 0; i < size; i++) {
            sSplitUser.append("\n").append(listUser.getModel().getElementAt(i));
        }
        System.out.print(sSplitUser);
        String sOneUser=sSplitUser.toString();           
        String[] words=sOneUser.split("\n");
        for(String w:words)
        {  
            
            System.out.println(w);
            if(!sCompareUser.equals(w))
            {
                checkUser=false;
            }
        }
        if(!checkUser)
        {
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "USER: "+ sUser +" not in ROOM", "Warning", mcServer);
        }
        return checkUser;
}*/
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
            java.util.logging.Logger.getLogger(RoomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RoomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RoomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RoomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoomGUI(1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnKickUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbHostName;
    private javax.swing.JLabel lbRoomName;
    private javax.swing.JList<String> listUser;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextArea txtContent;
    // End of variables declaration//GEN-END:variables

}
