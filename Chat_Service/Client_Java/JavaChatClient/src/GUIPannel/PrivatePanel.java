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
    
    public PrivatePanel(String tabName) {
        initComponents();
        txtContent.setEditable(false);
        this.nameTab = tabName;
        
        this.txtContent.setText("");
        
        //Get History
        int flag = 1;
        DataControl dataControl = new DataControl();
        try {
                
                System.err.println(String.valueOf(nameTab));
                Map<String, MessageStruct> dataMap = new HashMap<String, MessageStruct>();
                dataMap = dataControl.GetList(String.valueOf(nameTab));

                for (String key: dataMap.keySet()){
                    MessageStruct ms = new MessageStruct();
                    ms = dataMap.get(key);

                    if (ms.desUsr.contains(GlobalStatic.myUserName)){
                        txtContent.append(ms.curUsr + ": " + ms.Message + "\n");
                    }
                    else {
                        txtContent.append(ms.Message + "\n");
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
    
    public void AddToTextBox_String(String text){
        txtContent.append(text);
        txtContent.setCaretPosition(txtContent.getDocument().getLength());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtChat = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();

        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtChatKeyPressed(evt);
            }
        });

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane3.setViewportView(txtContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                    .addComponent(txtChat, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
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
        }
    }//GEN-LAST:event_txtChatKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextArea txtContent;
    // End of variables declaration//GEN-END:variables
}
