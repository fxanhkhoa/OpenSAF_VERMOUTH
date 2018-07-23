/*
 
*Function : ClientGUI
*Description: Client Interfaces. Chat General or Select User to Private Chat
*Argument: Nope!
*Return: Nope!
Note: First Interface 

 */
package chatclient;
import java.awt.ComponentOrientation;
import java.awt.Container;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultListModel;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author hieupham
 */

public class ClientGUI extends javax.swing.JFrame {

    /**
     * Creates new form ClientGUI
     */
    Global g;
    Thread waitThr;
    //Thread MessThr;
    private int sName;
    private String sNewUser;
    private DefaultListModel listModel,listModelUserPrv;
    RoomGUI roomNew;
    DataControl dataControl;
    private SavedPreference sP;
    
    public ClientGUI() {
        initComponents();
        sP = SavedPreference.getInstance();
        g = Global.getInstance();
        dataControl = new DataControl();
        listRoom.setModel(new DefaultListModel());
        listModel = new DefaultListModel();
        listUserPrv.setModel(new DefaultListModel());
        listModelUserPrv = new DefaultListModel();
        lbUser.setText(sP.GetUserName());
        //User cannot change
        txtContent.setEditable(false);
        txtContent.setCaretPosition(0);
        ReloadOnline();
        waitThr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (g.client.GetStatus() == false){
//                            g.Release();
//                            g.CreateNew();
//                            Thread.sleep(2000);
                        }
                        else if (g.client.GetCommandCode() == g.client.SIGNOUTOK){
                            MainGUI mainGui = new MainGUI();
                            mainGui.setVisible(true);
                            ClientGUI.this.dispose();        
                        }
                        else if (g.client.GetCommandCode() == g.client.REPASSOK){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null,"Your password was changed", "Successful", mcServer);        
                        }
                        else if (g.client.GetCommandCode() == g.client.ADDOK){
                            int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            JOptionPane.showMessageDialog (null, "Got", "Warning", mcServer);
                            g.client.ClearData();
                            //waitThr.stop();
                        }
                        else if (g.client.GetCommandCode() == g.client.PRVOK){
                            //int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            //JOptionPane.showMessageDialog (null, "PRV", "Warning", mcServer);
                            g.client.ClearData();
                            //waitThr.stop();
                        }
                        else if (g.client.GetCommandCode() == g.client.RECVPRV){
                            g.client.StopRecon();
                            //int mcServer = JOptionPane.INFORMATION_MESSAGE;
                            //JOptionPane.showMessageDialog (null,"PRV " + g.client.GetName() + g.client.GetMessage(), "Warning", mcServer);
//                            if (listUserPrv.getSelectedValue().contains(g.client.GetName())){
//                                txtContent.append(g.client.GetName() + ": " + g.client.GetMessage() + "\n");
//                            }
                            // Destination User is me
                            if (String.valueOf(g.client.GetDesName()).contains(sP.GetUserName())){
                                txtContent.append(String.valueOf(g.client.GetName()) + ": " + String.valueOf(g.client.GetMessage()) + "\n");
                                
                                // Add to local databases
                                //System.out.println(g.client.GetName());
                                dataControl.curUsr = String.valueOf(g.client.GetName()).trim();
                                dataControl.desUsr = String.valueOf(g.client.GetDesName()).trim();
                                dataControl.numberOfElement = dataControl.CountXMLElement(String.valueOf(g.client.GetName()).trim());
                                //System.out.println(String.valueOf(g.client.GetName()));
                                dataControl.Message = String.valueOf(g.client.GetMessage()).trim();
                                dataControl.AppendXMLFile(String.valueOf(g.client.GetName()).trim());
                                //g.client.ClearData();
                            }
                            g.client.ClearData();
                        }
                        else if (g.client.GetCommandCode() == g.client.ADDROOMOK){
                            roomNew = new RoomGUI(sName);           
                            roomNew.setVisible(true);
                            listModel.addElement(sName);
                            if(true){
           
                                listRoom.setModel(listModel);
                                
                            }
                            System.err.println("ADD SUCCESS");
                            g.client.ClearData();
                        }
                        /* add list user & list room */
                        else if (g.client.GetCommandCode() == g.client.LISTUSEROK){
                            
                            
                            ReloadOnline();
                            System.err.println("ADD SUCCESS");
                            g.client.ClearData();
                        }
                        /* list user begin  */
                        else if (g.client.GetCommandCode() == g.client.OUTDONE){
                            
                           ReloadOnline();
                           g.client.ClearData();
                        }
                        /* list user  */
                        else if (g.client.GetCommandCode() == g.client.ONLINEUSERADDED){
                           System.err.println("nhay vao day");
                           ReloadOnline();
                           
                           g.client.ClearData();
                        }
                         /* ROOM RECV  */
                        else if (g.client.GetCommandCode() == g.client.LISTROOMOK){
                            
                            char[] IDUser= g.client.GetName();
                            listModel.addElement(IDUser);
                            if(true){
           
                                listRoom.setModel(listModel);
                                
                            }
                            g.client.ClearData();
                        }
                        
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        if (!waitThr.isAlive())
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

        jLabel1 = new javax.swing.JLabel();
        txtChat = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreateRoom = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listRoom = new javax.swing.JList<>();
        addUser = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        listUserPrv = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();
        lbUser = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        lbWelcome = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuAccount = new javax.swing.JMenu();
        jMenuItemSignOut = new javax.swing.JMenuItem();
        jMenuItemChangePass = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 624));
        setResizable(false);
        setSize(new java.awt.Dimension(1000, 200));

        jLabel1.setText("Room Avaiable");

        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtChatKeyPressed(evt);
            }
        });

        jLabel2.setText("User Avaiable");

        btnCreateRoom.setText("CreateRoom");
        btnCreateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRoomActionPerformed(evt);
            }
        });

        listRoom.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listRoomMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listRoom);

        addUser.setText("AddUser");
        addUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserActionPerformed(evt);
            }
        });

        listUserPrv.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listUserPrv.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listUserPrvValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listUserPrv);

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane3.setViewportView(txtContent);

        lbUser.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lbWelcome.setFont(new java.awt.Font("Ubuntu", 0, 16)); // NOI18N
        lbWelcome.setText("WELCOME");

        jMenuFile.setText("File");

        jExit.setText("Quit");
        jExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jExit);

        jMenuBar1.add(jMenuFile);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenuAccount.setText("Account");

        jMenuItemSignOut.setText("Sign Out");
        jMenuItemSignOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSignOutActionPerformed(evt);
            }
        });
        jMenuAccount.add(jMenuItemSignOut);

        jMenuItemChangePass.setText("Change Password");
        jMenuItemChangePass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangePassActionPerformed(evt);
            }
        });
        jMenuAccount.add(jMenuItemChangePass);

        jMenuBar1.add(jMenuAccount);

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
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCreateRoom))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbWelcome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(addUser))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefresh)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lbWelcome))
                    .addComponent(lbUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(btnCreateRoom)
                                .addGap(77, 77, 77)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(btnRefresh))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUser))
                .addGap(38, 38, 38))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void ReloadOnline(){
        listModelUserPrv.removeAllElements();
                            System.out.println("Cap :" + sP.size);
                            for (int i = 0; i < sP.size; i++)
                                if (!sP.onlineUser.elementAt(i).contains(sP.GetUserName()))
                                  listModelUserPrv.addElement(sP.onlineUser.elementAt(i));
                            if(true)
                            {
                                listUserPrv.setModel(listModelUserPrv);
                            }
    
    }
    private void txtChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            if (!listUserPrv.isSelectionEmpty()){
                txtContent.append(txtChat.getText() + "\n");
                g.client.SendPrivateMessage(listUserPrv.getSelectedValue(), txtChat.getText());
                //System.out.println("SEND ");
                //g.client.SendPrivateMessage(listUserPrv.getSelectedValue(), txtChat.getText());
                
                // Add to local databases
                dataControl.curUsr = sP.GetUserName();
                dataControl.desUsr = listUserPrv.getSelectedValue();
                dataControl.numberOfElement = dataControl.CountXMLElement(listUserPrv.getSelectedValue());
                dataControl.Message = txtChat.getText().trim();
                dataControl.AppendXMLFile(listUserPrv.getSelectedValue());
                txtChat.setText("");
            }
            else{
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Select 1 user man!!!!", "Warning", mcServer);
            }
        }
    }//GEN-LAST:event_txtChatKeyPressed

    private void btnCreateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRoomActionPerformed
        // TODO add your handling code here:
        int mc = JOptionPane.INFORMATION_MESSAGE;
	sName = Integer.parseInt(JOptionPane.showInputDialog (null, "Type Room's Name", "Create Room to Chat Private (0-2000000)", mc));
        if ((sName < 2000000) || (sName > 0)){
            g.client.AddNewRoom(sName);
        }
        else{
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null,"Error", "Warning", mcServer);
        }
    }//GEN-LAST:event_btnCreateRoomActionPerformed

    private void addUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserActionPerformed
        // TODO add your handling code here:
        int mc = JOptionPane.INFORMATION_MESSAGE;
	sNewUser = JOptionPane.showInputDialog (null, "Type User", "Create Room to Chat Private", mc);
        g.client.AddFriend(sNewUser);
        g.client.sP.AddOnlineUser(sNewUser.toCharArray());
        g.client.sP.size++;
        listModelUserPrv.addElement(sNewUser);
        if(true)
        {
           
           listUserPrv.setModel(listModelUserPrv);
        }

        try {
            if (dataControl.CheckExistXMLFile(sNewUser) == 0){
            dataControl.curUsr = sP.GetUserName();
            dataControl.desUsr = sNewUser;
            dataControl.numberOfElement = 0;
            dataControl.Message = "1st created";
            dataControl.CreateXMLFile(sNewUser);
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_addUserActionPerformed

    private void listRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listRoomMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2){
            //Double-click detected
            int index = listRoom.locationToIndex(evt.getPoint());
            RoomGUI roomNew = new RoomGUI(Integer.parseInt(listRoom.getSelectedValue()));           
            roomNew.setVisible(true);
        }
    }//GEN-LAST:event_listRoomMouseClicked

    private void listUserPrvValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listUserPrvValueChanged
        // TODO add your handling code here:
        txtContent.setText("");
        
        boolean adjust = evt.getValueIsAdjusting();
        if (!adjust){
            try {
                JList changedList = (JList)evt.getSource();
                if (listUserPrv == changedList){
                    System.out.println("gogogo");
                    //txtContent.setText("");
                    System.err.println(listUserPrv.getSelectedValue());
                    Map<String, MessageStruct> dataMap = new HashMap<String, MessageStruct>();
                    dataMap = dataControl.GetList(listUserPrv.getSelectedValue());
                    //for (int i = 0; i < dataMap.size(); i++){
                    for (String key: dataMap.keySet()){
                        MessageStruct ms = new MessageStruct();
                        ms = dataMap.get(key);
                        //System.out.println(sP.GetUserName());
                        //System.out.println(ms.desUsr + "z");
                        if (ms.desUsr.contains(sP.GetUserName())){
                            txtContent.append(ms.curUsr + ": " + ms.Message + "\n");
                        }
                        else {
                            txtContent.append(ms.Message + "\n");
                        }
                    }
                }
            } catch (Exception e) {
            }
            
            }
        
    }//GEN-LAST:event_listUserPrvValueChanged

    private void jMenuItemSignOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSignOutActionPerformed
        // TODO add your handling code here:
        g.client.SignOut();
        
        MainGUI mainGui = new MainGUI();
        mainGui.setVisible(true);
        ClientGUI.this.dispose();
        
        
    }//GEN-LAST:event_jMenuItemSignOutActionPerformed

    private void jMenuItemChangePassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangePassActionPerformed
        // TODO add your handling code here:
        int mcUser = JOptionPane.INFORMATION_MESSAGE;
        String sUser = JOptionPane.showInputDialog (null, "Type Password to change", "Chang password ", mcUser);
        g.client.RePass(sUser);
    }//GEN-LAST:event_jMenuItemChangePassActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        listModelUserPrv.removeAllElements();
        System.out.println("Cap :" + sP.size);
        for (int i = 0; i < sP.size; i++)
            if (!sP.onlineUser.elementAt(i).contains(sP.GetUserName()))
              listModelUserPrv.addElement(sP.onlineUser.elementAt(i));
         
        
        /*CHECK man go to heaven*/
//        for (int J = 0; J < 30; J++)
//        if (!sP.offlineUser.elementAt(J).contains(sP.GetUserName()))
//              listModelUserPrv.removeElement(sP.offlineUser.elementAt(J));
        if(true)
        {
           
           // can find the man
            listUserPrv.setModel(listModelUserPrv);
        }
        
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void jExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jExitActionPerformed

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
//                int k = 98;
//                char cm = (char) k;
//                System.out.println(cm);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUser;
    private javax.swing.JButton btnCreateRoom;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JMenuItem jExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenuAccount;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemChangePass;
    private javax.swing.JMenuItem jMenuItemSignOut;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbUser;
    private javax.swing.JLabel lbWelcome;
    private javax.swing.JList<String> listRoom;
    private javax.swing.JList<String> listUserPrv;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextArea txtContent;
    // End of variables declaration//GEN-END:variables
}
