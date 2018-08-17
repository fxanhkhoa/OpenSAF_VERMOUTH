/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIPannel;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javachatclient.ButtonTabComponent;
import javachatclient.GlobalStatic;
import javachatclient.Struct.DataControl;
import javachatclient.Struct.MessageStruct;
import javachatclient.Struct.OnlineUserRenderer;
import javachatclient.Struct.RoomStruct;
import javachatclient.Struct.UserStruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
/**
 *
 * @author ubuntu
 */
public class ClientPannel extends javax.swing.JPanel {

    /**
     * Creates new form ClientPannel
     */
    
    
    
    /**/
    
    private JFrame frame;
    private JPanel pane;
    private JTextField nameROOMField,nameUserField;
    private JPasswordField passROOMField;
    private String nameROOM="";
    private String passROOM="";
    private DefaultListModel listModel,listModelUserPrv;
    DataControl dataControl;
    
    /**/
    public ClientPannel() {
        initComponents();
        
        /**/
        UIManager.put("swing.boldMetal", Boolean.FALSE);
       /**/
        btnReconnect.setEnabled(false);
        
        dataControl = new DataControl();
        listRoom.setModel(new DefaultListModel());
        listModel = new DefaultListModel();
        listUserPrv.setModel(new DefaultListModel());
        listModelUserPrv = new DefaultListModel();
        
        
        /**/
        String Path = System.getProperty("user.dir");
        ImageIcon imgThisImg = new ImageIcon(Path + "/images/addR.png");
        Image img = imgThisImg.getImage();
        Image resizedImg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        imgThisImg = new ImageIcon(resizedImg);
        btnCreateRoom.setIcon(imgThisImg);
        /* Iconf for Refresh */
        ImageIcon imgRefresh = new ImageIcon(Path + "/images/refresh.png");
        Image imgR = imgRefresh.getImage();
        Image resizedImgR = imgR.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        imgRefresh = new ImageIcon(resizedImgR);
        btnRefresh.setIcon(imgRefresh);
        /* Iconf for reCon */
        ImageIcon imgreCon = new ImageIcon(Path + "/images/reCon.png");
        Image imgRC = imgreCon.getImage();
        Image resizedImgRC = imgRC.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        imgreCon = new ImageIcon(resizedImgRC);
        btnReconnect.setIcon(imgreCon);
        /* Iconf for AddF */
        ImageIcon imgreAddF = new ImageIcon(Path + "/images/addfr.png");
        Image imgAF = imgreAddF.getImage();
        Image resizedImgAF = imgAF.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        imgreAddF = new ImageIcon(resizedImgAF);
        btnAddUser.setIcon(imgreAddF);
        /**/
        /*Hint*/
        btnAddUser.setToolTipText("Click button if you want to Add Friend ");
        btnCreateRoom.setToolTipText("Click button if you want to Create a Room ");
        btnReconnect.setToolTipText("Click button if auto reconnect fail ");
        btnRefresh.setToolTipText("Click button if refresh ");
        
        /**/
        lbStatus.setOpaque(true);
        lbStatus.setBackground(Color.GRAY);

        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCreateRoom = new javax.swing.JButton();
        btnReconnect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listRoom = new javax.swing.JList<>();
        lbStatus = new javax.swing.JLabel();
        btnAddUser = new javax.swing.JButton();
        scrollListFriend = new javax.swing.JScrollPane();
        listUserPrv = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        lbUser = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        lbWelcome = new javax.swing.JLabel();
        TabMain = new javax.swing.JTabbedPane();

        setMaximumSize(new java.awt.Dimension(866, 470));
        setMinimumSize(new java.awt.Dimension(866, 470));

        btnCreateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRoomActionPerformed(evt);
            }
        });

        btnReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReconnectActionPerformed(evt);
            }
        });

        listRoom.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listRoomMouseClicked(evt);
            }
        });
        listRoom.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listRoomValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listRoom);

        lbStatus.setText("OFFLINE");

        btnAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUserActionPerformed(evt);
            }
        });

        listUserPrv.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listUserPrv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUserPrvMouseClicked(evt);
            }
        });
        listUserPrv.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listUserPrvValueChanged(evt);
            }
        });
        scrollListFriend.setViewportView(listUserPrv);

        jLabel1.setText("Room Avaiable");

        lbUser.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N

        jLabel2.setText("My  Friends");

        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lbWelcome.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lbWelcome.setText("WELCOME");

        TabMain.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrollListFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(btnAddUser, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(btnReconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)
                        .addComponent(lbWelcome)
                        .addGap(43, 43, 43)
                        .addComponent(lbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addComponent(lbStatus)
                        .addGap(45, 45, 45))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(TabMain))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnReconnect, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbStatus, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(451, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(btnCreateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollListFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddUser))
                    .addComponent(TabMain))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void PerformRefreshClick(){
        btnRefresh.doClick();
    }
    
    public void PerformReconClick(){
        btnReconnect.doClick();
    }
    public void RemoveAllTab(){
        TabMain.removeAll();
    }
    //fusing on ClientThread
    public void StatusOnline(){
        lbStatus.setOpaque(true);
        lbStatus.setText("ONLINE");
        lbStatus.setBackground(Color.GREEN);
    }
    
    public void SetLabelName(String usr){
        lbUser.setText(usr);
    }
    public void ClearTab(){
        TabMain.removeAll();
    }
    public void disableReconBtn(){
        btnReconnect.setEnabled(false);
    }
    public void showReconBtn(){
        btnReconnect.setEnabled(true);
    }
    
    public void Reload(int size){
        ReloadOnline(size);
    }
    
    public void ReloadRoom(){
        ReloadRoomList();
    }
    
    private void ReloadRoomList(){
        listModel.removeAllElements();
        int size = GlobalStatic.numberOfRoom;
        System.out.println("Size of Room :" + GlobalStatic.roomList.size());
        for (RoomStruct rS : GlobalStatic.roomList){
            listModel.addElement(rS.roomName);
        }
        
        if (true){
            listRoom.setModel(listModel);
        }
    }
    
    private void ReloadOnline(int size){
        listModelUserPrv.removeAllElements();
        System.out.println("Cap :" + size);
        for (UserStruct uS: GlobalStatic.friendList){
            try {
                System.out.println("i day:" + uS.userName);
                if (!uS.userName.equals(GlobalStatic.myUserName)){
                //System.err.println(sP.onlineUser.elementAt(k).userName);
                listModelUserPrv.addElement(uS);
                }
            } catch (Exception e) {
                System.err.println("ERROR + " + e);
            }
             
        }
            if(true)
            {
                listUserPrv.setModel(listModelUserPrv);
            }
                            
        //Now render Jlist
        listUserPrv.setCellRenderer(new OnlineUserRenderer());
    }
    
    public void AddTextToBox(char[] curUser,char[] desUser,char[] Message){

        String des = String.valueOf(desUser).trim();
        String mess = String.valueOf(Message).trim();
        String cur = String.valueOf(curUser).trim();

        if (des.equals(GlobalStatic.myUserName)){

//            txtContent.append(cur + ": " + mess +  "\n");

            // Add to local databases
            //System.out.println(g.client.GetName());
            dataControl.curUsr = des;
            dataControl.desUsr = GlobalStatic.myUserName;
            dataControl.numberOfElement = dataControl.CountXMLElement(des);
            //System.out.println(String.valueOf(g.client.GetName()));
            dataControl.Message = mess;
            dataControl.AppendXMLFile(des);
            //g.client.ClearData();
        }
    }
    public void AddTabRoom(String nameRoom,int roomID){
        RoomPannel rP = GlobalStatic.GetRoomPanel(roomID);
        TabMain.addTab(nameRoom, rP);

        rP.indexOfTab = TabMain.indexOfComponent(rP);
        initTabComponent(rP.indexOfTab );
        rP.status = 1;
        
    }
    
    private void btnCreateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRoomActionPerformed
        // TODO add your handling code here:
        /*GET MULTI INPUT */
        pane = new JPanel();
        pane.setLayout(new GridLayout(0, 2, 2, 2));

        nameROOMField = new JTextField(29);
        passROOMField = new JPasswordField(29);

        pane.add(new JLabel("What is your room's name?"));
        pane.add(nameROOMField);

        pane.add(new JLabel("Set Password"));
        pane.add(passROOMField);

        int option = JOptionPane.showConfirmDialog(frame, pane, "Please fill all the fields", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {

            String nameROOM = nameROOMField.getText();
            String passROOM = passROOMField.getText();
            GlobalStatic.clientThread.AddNewRoom(nameROOM,passROOM);
        }

    }//GEN-LAST:event_btnCreateRoomActionPerformed

    private void btnReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReconnectActionPerformed
        try {
            System.out.println("Reconnect is clicked");
            btnReconnect.setEnabled(false);
            Thread.sleep(1000);
            GlobalStatic.clientThread.timeToRecon = 0;
            GlobalStatic.clientThread.InitThreadRecon();
            //GlobalStatic.clientThread.StartReadThread();
        } catch (InterruptedException ex) {   System.out.println("catch");     }
    }//GEN-LAST:event_btnReconnectActionPerformed

    private void listRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listRoomMouseClicked
        // TODO add your handling code here:
        
        int statusNEWROOM=1005;

        if (evt.getClickCount() % 2== 0){
            if(listRoom.getModel().getSize()== 0 ){
                System.err.println("Empty List");
            }
            else{
            String nameRoom =String.valueOf(listRoom.getSelectedValue());
            int roomID = GlobalStatic.GetIDROOM(nameRoom);
            System.out.println("ROOM ID WHEN CLICKED LIST:" +roomID);
            if (GlobalStatic.CheckOwnerRoomGUI(roomID) == 0){
                pane = new JPanel();
                pane.setLayout(new GridLayout(0, 2, 2, 2));
                passROOMField = new JPasswordField(29);
                pane.add(new JLabel("Enter Room's Password"));
                pane.add(passROOMField);
                int option = JOptionPane.showConfirmDialog(frame, pane, "Please fill all the fields", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {

                    String sPassROOM = passROOMField.getText();
                    GlobalStatic.clientThread.LetMeBeInRoom(roomID, sPassROOM);
                }
                else{
                    System.err.println("Cancel action LET ME BEING ROOM");
                }

            }
            else{
                //sP.InitRoomGUI(nameRoom,roomID); //Da init luc tao room r nen ko init lai
                statusNEWROOM = GlobalStatic.GetstatusROOM(roomID);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }
                if(statusNEWROOM==0){

                    try {
                        // Yeu cau server gui LIST USER TRONG ROOM CREATE VE
                        GlobalStatic.clientThread.RefreshRoom(roomID);// CMD 15
                    } catch (Exception e) {
                    }
                     
                    //Add Tab Room
//                    int exist = GlobalStatic.UpdateRoomList(roomID, nameRoom.toCharArray(), "".toCharArray());
                    RoomPannel rP = GlobalStatic.GetRoomPanel(roomID);
                    TabMain.addTab(nameRoom, rP);
                    
                    rP.indexOfTab = TabMain.indexOfComponent(rP);
                    initTabComponent(rP.indexOfTab );
                    rP.status = 1;

                }
                System.out.println("CHECK ROOM AVAILABLE : "+ statusNEWROOM);
            }
            //System.err.println(String.valueOf(listRoom.getSelectedValue()));
            }
        }
    }//GEN-LAST:event_listRoomMouseClicked
private void initTabComponent(int i) {
        TabMain.setTabComponentAt(i,
                 new ButtonTabComponent(TabMain));
    }
    private void listRoomValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listRoomValueChanged
        // TODO add your handling code here:
        try {
            //nothing here
        } catch (Exception e) {
        }

    }//GEN-LAST:event_listRoomValueChanged

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
        // TODO add your handling code here:
        pane = new JPanel();
        pane.setLayout(new GridLayout(0, 2, 2, 2));
        nameUserField = new JTextField(29);
        pane.add(new JLabel("Type Friend's ID want to add"));
        pane.add(nameUserField);
        int iNewFriend = JOptionPane.showConfirmDialog(frame, pane, "Please FILL your friend's name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if(iNewFriend==JOptionPane.OK_OPTION){
            String nameFriend = nameUserField.getText();
            System.err.println("Your friend's name : "+nameFriend);
            GlobalStatic.clientThread.AddFriend(nameFriend);
        }
        else{
            System.err.println("Cancel this action ADD FRIEND");
        }
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void listUserPrvValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listUserPrvValueChanged
        // TODO add your handling code here:
       
    }//GEN-LAST:event_listUserPrvValueChanged

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        GlobalStatic.clientThread.ReFresh();
        GlobalStatic.clientThread.RefreshRoomList();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void listUserPrvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUserPrvMouseClicked
        // TODO add your handling code here:
        
        int flag = 1;
        
        if (evt.getClickCount() % 2==0){
            String nameTab =String.valueOf(listUserPrv.getSelectedValue());
            if(listUserPrv.getSelectedValue() == null ){
                System.err.println("Empty Selected");
            }
            else{
            int exist = GlobalStatic.UpdatePrivatePanelList(nameTab);
            PrivatePanel pP = GlobalStatic.GetPrivatePanel(nameTab);
            if (exist == 1){
                TabMain.addTab(nameTab, pP);
                System.out.println(TabMain.indexOfComponent(pP));
                pP.indexOfTab = TabMain.indexOfComponent(pP);
                initTabComponent(pP.indexOfTab);
            }
            exist = 0;
        }
        }
        
    }//GEN-LAST:event_listUserPrvMouseClicked
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabMain;
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnCreateRoom;
    private javax.swing.JButton btnReconnect;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbUser;
    private javax.swing.JLabel lbWelcome;
    private javax.swing.JList<String> listRoom;
    private javax.swing.JList<String> listUserPrv;
    private javax.swing.JScrollPane scrollListFriend;
    // End of variables declaration//GEN-END:variables
}