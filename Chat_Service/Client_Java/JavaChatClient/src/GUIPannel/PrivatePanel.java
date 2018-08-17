/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIPannel;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javachatclient.GlobalStatic;
import javachatclient.Struct.DataControl;
import javachatclient.Struct.MessageStruct;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author hieupham
 */
public class PrivatePanel extends javax.swing.JPanel {

    /**
     * Creates new form PrivatePanel
     */
    
    public String nameTab = "";
    public int indexOfTab;
    /**/
    public StyledDocument doc ;
    /**/
    
    public PrivatePanel(String tabName) {
        initComponents();
        
        doc= textPane.getStyledDocument();
        
        
        txtContent.setEditable(false);
        this.nameTab = tabName;
        
        this.txtContent.setText("");
        
        //Get History
        int flag = 1;
        DataControl dataControl = new DataControl();
        try {
                
                System.err.println(String.valueOf(nameTab));
                Map<Integer, MessageStruct> dataMap = new HashMap<Integer, MessageStruct>();
                dataMap = dataControl.GetList(String.valueOf(nameTab));
                
                for (Integer key: dataMap.keySet()){
                    MessageStruct ms = new MessageStruct();
                    ms = dataMap.get(key);

                    if (ms.desUsr.contains(GlobalStatic.myUserName)){
                        txtContent.append(ms.curUsr + ": " + ms.Message + "\n");
                        RecvSetAlign(ms.curUsr + ": " + ms.Message + "\n");
                    }
                    else {
                        txtContent.append(ms.Message + "\n");
                        SentSetAlign(ms.Message + "\n");
                    }
                }
                
                flag = 1;
            } catch (Exception e) {
                flag = 0;
            }
        
        if (flag == 0){
            try {
                if (dataControl.CheckExistXMLFile(String.valueOf(nameTab)) == 0){
                dataControl.curUsr = GlobalStatic.myUserName;
                dataControl.desUsr = String.valueOf(nameTab);
                dataControl.numberOfElement = 0;
                dataControl.Message = "1st created";
                dataControl.CreateXMLFile(String.valueOf(nameTab));
                }
            } catch (Exception e) {
            }
        }
        
        txtContent.setCaretPosition(txtContent.getDocument().getLength());
    }
    
    public String GetNameTab(){
        return nameTab;
    }
    
    public void AddToTextBox(String curUser, String text){
        txtContent.append(curUser + ": " + text + "\n");
        txtContent.setCaretPosition(txtContent.getDocument().getLength());
    }
    public void InsertIcon(int status){
        
        if(status == 1 ){
            String Path = System.getProperty("user.dir");
            ImageIcon imgThisImg = new ImageIcon(Path + "/images/kakashi.jpeg");
            Image img = imgThisImg.getImage();
            Image resizedImg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_DEFAULT);
            imgThisImg = new ImageIcon(resizedImg);
            textPane.insertIcon(imgThisImg); 
        }
        else{
            String Path = System.getProperty("user.dir");
            ImageIcon imgThisImg = new ImageIcon(Path + "/images/naruto.jpeg");
            Image img = imgThisImg.getImage();
            Image resizedImg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_DEFAULT);
            imgThisImg = new ImageIcon(resizedImg);
            textPane.insertIcon(imgThisImg);
        }
        
    }
  
    public void SentSetAlign(String msg ){
        try {
            SimpleAttributeSet leftA = new SimpleAttributeSet();
            StyleConstants.setAlignment(leftA, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(leftA, Color.RED);
            StyleConstants.setBold(leftA, true);
            doc.setParagraphAttributes(doc.getLength(), 1, leftA, false);
            InsertIcon(1);
            doc.insertString(doc.getLength(), msg+"\n", leftA );
            doc.setParagraphAttributes(doc.getLength(), 1, leftA, false);
            
        } catch (BadLocationException ex) {
            Logger.getLogger(PrivatePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void RecvSetAlign(String msg){
        try {
            SimpleAttributeSet rightA = new SimpleAttributeSet();
            StyleConstants.setAlignment(rightA, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(rightA, Color.BLUE);
            InsertIcon(0);
            doc.setParagraphAttributes(doc.getLength(), 1, rightA, false);
            doc.insertString(doc.getLength(), msg+"\n", rightA );
            doc.setParagraphAttributes(doc.getLength(), 1, rightA, false);
             textPane.setCaretPosition(textPane.getDocument().getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(PrivatePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();
        txtChat = new javax.swing.JTextField();
        jScrolltextPane = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane3.setViewportView(txtContent);

        txtChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtChatMouseClicked(evt);
            }
        });
        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtChatKeyPressed(evt);
            }
        });

        jScrolltextPane.setViewportView(textPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtChat, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(jScrolltextPane))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrolltextPane)
                .addGap(18, 18, 18)
                .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtChatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            // can phai truyen vao DesUSR
            String reDesUsr="";
            if (true){
                DataControl dataControl = new DataControl();
                txtContent.append(txtChat.getText() + "\n");
                
                /**/
                SentSetAlign(txtChat.getText() + "\n");
                /**/
                GlobalStatic.clientThread.SendPrivateMessage(nameTab, txtChat.getText());
                // Add to local databases
                dataControl.curUsr = GlobalStatic.myUserName;
                dataControl.desUsr = reDesUsr;
                dataControl.numberOfElement = dataControl.CountXMLElement(nameTab);
                dataControl.Message = txtChat.getText().trim();
                dataControl.AppendXMLFile(String.valueOf(nameTab));
                txtChat.setText("");
            }
            else{
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Select 1 user man!!!!", "Warning", mcServer);
            }
            txtContent.setCaretPosition(txtContent.getDocument().getLength());
            GlobalStatic.SetHaveMessPrivatePanel(nameTab, 0);
            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
        }
    }//GEN-LAST:event_txtChatKeyPressed

    private void txtChatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtChatMouseClicked
        GlobalStatic.SetHaveMessPrivatePanel(nameTab, 0);
        GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
    }//GEN-LAST:event_txtChatMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrolltextPane;
    private javax.swing.JTextPane textPane;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextArea txtContent;
    // End of variables declaration//GEN-END:variables
}
