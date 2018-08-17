/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatclient;

import java.awt.GridLayout;
import javachatclient.Struct.DataControl;
import javachatclient.Struct.ProtocolCS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author ubuntu
 */

public class ClientThread {
    /* 
==========================================================================
                                VARIABLE
========================================================================== 
*/
    // send msg to server
    public static final int OK = 1;
    public static final int ERROR = -1;
    public final int ADDOK = 2;
    public final int DELOK = 3;
    public final int PRVOK = 4;
    public final int ROOMOK = 5;
    /* add to sign up */
    public final int SIGNOUTOK = 506;
    public final int SIGNINFAIL = 100;
    
    public final int SIGNINOK = 101;
    /* add to sign up */
    
    public final int SIGNUPOK = 105;
    public final int SIGNUPFAIL = 104;
    public final int REPASSOK = 35;
    public final int REPASSFAIL = 34;
    /*109 dang xuat*/
    public final int OUTOK = 109;
    /*----------------------------------*/
/*---------------CHAT FEATURES COMMAND CODE-------------------*/
    /* add to show list user & */
    
    public final int LISTUSEROK = 102;
    /*list room */
    
    
    public final int REVCAP=43; // you are captian
    public final int NOTROOM=44; // specific room have been deleted
    
    /**/
 

    public final int LISTROOMOK = 42; // 103

    /*list room */

    public final int ROOMCREATE = 110;
    public final int ONLINEUSEROK = 66301; // In char is 997
    public final int ONLINEUSERADDED = 888; // Added online user
    public final int OUTDONE = 887; // Added online user
    /* RECEIVED FRIEND REQUIRE */
    public final int ADDFRIENDFAIL = 30;
    public final int ADDFRIENDOK = 31; 
    
/*---------------ROOM FEATURES COMMAND CODE-------------------*/
    
    /*REICEVE FROM SERVER CREATE ROOM*/
    public final int ADDROOMFAIL = 106;
    public final int ADDROOMOK = 107;
    /*INVITE*/
    public final int INVITEROOMFAIL = 80;// 200, 80, 65531;
    public final int INVITEROOMOK = 81;//201, 81, 65532;
    public final int BEINVITEDROOM = 82;//202 , 82, 65533;
    /*KICK FROM OWNER*/
    public final int KICKROOMFAIL = 84;//204;
    public final int KICKROOMOK = 85;//205;
    public final int BEKICKROOM = 86;//206;
    public final int HAVEKICK=99;
    /*LET ME BEING ROOM*/
    public final int LETROOMFAIL =88;// 208;
    public final int LETROOMOK = 87;//207; // 
    /*CHANGE PASS ROOM FROM OWNER*/
    public final int PASSROOMFAIL =90;// 210
    public final int PASSROOMOK = 89;//209;
    /*DELETE ROOM FROM OWNER*/
    public final int DELETEROOMFAIL = 300;// 210
    public final int DELETEROOMOK = 108;//209;
    /*NOTIFY SOMEONE IN OUT ROOM */
    public final int ONEINROOM = 91;// 210
    public final int ONEOUTROOM = 92;//209;
    /*REQUEST TO SERVER WHO IN THIS ROOM*/
    public final int REFRESHROOM=15;
    /*REQUEST TO SERVER REPASS THIS ROOM*/
    public final int REPASSROOMOK = 89;
    public final int REPASSROOMFAIL = 90;
/*----------------------------------------------------------------------------*/
    /* add to show list user & list room */
    
    public final int REQUIREADD = 500;
    
    /*----------------------------------*/
    public final int RADDOK = 8;
    public final int RDELOK = 9;
    
    public final int RECVPRV = 11;
    public final int RECVROOM = 12;
    
    /*
    Private Variables
    */
    private char[] recvData = new char[1024];
    public static Thread readMessage,reconnect;
    private char[] desUser = new char[30];
    private char[] curUser = new char[30];
    private char[] Message = new char[896];
    
    private int precode;
    public int savedcmd;
    
    public int len;
    public int signoutSignal, signupSignal;
    public int timeToRecon=0;
    
    /*For Notify*/
    private JFrame frame;
    private JPanel pane;
    /***/
    
/*==========================================================================
                                Function
==========================================================================*/ 
    public ClientThread() {
        GlobalStatic.connectionStatus = ConnectToServer();
        InitThread();
    }
    
    public void InitThreadRecon(){
        System.err.println("javachatclient.ClientThread.InitThreadRecon()");
        /*****************************************
         * Reconnect Thread
         */
        reconnect = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (GlobalStatic.connectionStatus == true){
                            GlobalStatic.signinPannel.HideReconBtn();
                            GlobalStatic.clientPannel.disableReconBtn();
                            InitThread();
                            GlobalStatic.blockToSend.command = 125;
                            GlobalStatic.blockToSend.IDRoom = 0;
                            GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
                            GlobalStatic.blockToSend.desUsername = "";
                            GlobalStatic.blockToSend.ownPassword = "";
                            GlobalStatic.blockToSend.roomPassword = "";
                            GlobalStatic.blockToSend.message = "";
                            GlobalStatic.blockToSend.mIDUser = GlobalStatic.myIDUser;
                            Send(GlobalStatic.blockToSend.GetText());
                            System.out.println(".run()");
                            reconnect.stop();
                            
                            break;
                        }   
                        if (timeToRecon < 3){
                            timeToRecon ++;
                            ConnectToServer();
                            Thread.sleep(300);
                        }
                        
                        if (timeToRecon >= 3){
                            GlobalStatic.signinPannel.showReconBtn();
                            GlobalStatic.clientPannel.showReconBtn();
                            /*#recon*/
                            {
                                pane = new JPanel();
                                pane.setLayout(new GridLayout(0, 2, 2, 2));
                                pane.add(new JLabel("YES to Reconnect again or NO to QUIT "));
                                int option = JOptionPane.showConfirmDialog(frame, pane, "Can not connect to Server. ", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                                if (option == JOptionPane.YES_OPTION) {
//                                    GlobalStatic.clientThread.AddNewRoom(nameROOM,passROOM);
                                        GlobalStatic.signinPannel.ClickRecon();
                                }
                                else if (option==JOptionPane.NO_OPTION){
                                    System.exit(0);
                                }
                            }
                            System.out.println("SHOW BTN");
                            reconnect.stop();
                            break;
                        }
//                        Recon();
//                        if (GlobalStatic.connectionStatus == false){
//                            ConnectToServer();
//                        }
//                        else if (GlobalStatic.connectionStatus == true){
//                            GlobalStatic.os = new BufferedWriter(new OutputStreamWriter(GlobalStatic.socket.getOutputStream()));
//                            GlobalStatic.is = new BufferedReader(new InputStreamReader(GlobalStatic.socket.getInputStream()));
//                            reconnect.stop();
//                        }
//                        System.err.println("in");
//
//                        if (GetCommandCode() != 66302){
//                            //readMessage.wait(1000);
//                            System.err.println("in send 999");
//                            GlobalStatic.blockToSend.command = 999;
//                            GlobalStatic.blockToSend.IDRoom = 0;
//                            GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
//                            GlobalStatic.blockToSend.desUsername = "";
//                            GlobalStatic.blockToSend.ownPassword = "";
//                            GlobalStatic.blockToSend.roomPassword = "";
//                            GlobalStatic.blockToSend.message = "";
//                            GlobalStatic.blockToSend.mIDUser = GlobalStatic.myIDUser;
//                            Send(GlobalStatic.blockToSend.GetText());
//                            System.err.println("send 999 ok");
//                            System.err.print(GlobalStatic.blockToSend.mIDUser);
//                            System.err.println(GlobalStatic.blockToSend.command);
//                            System.out.println("start of command");
//                            System.out.println((GlobalStatic.blockToSend.command >> 0) & 0xFF);
//                            System.out.println((GlobalStatic.blockToSend.command >> 8) & 0xFF);
//                            System.out.println((GlobalStatic.blockToSend.command >> 16) & 0xFF);
//                            System.out.println((GlobalStatic.blockToSend.command >> 24) & 0xFF);
//                        }
//                        else if (GetCommandCode() == 66302){
//                            System.err.println("gotcha");
//                            reconnect.stop();
//                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        reconnect.start();
        System.err.println("javachatclient.ClientThread.InitThreadRecon()");
    }
    
    private void InitThread(){
        
        timeToRecon = 0;
        
        
        
        /*****************************************
         * Receive Thread
         */
        readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        len = GlobalStatic.is.read(recvData);

                        if (len > 0){
                            System.out.println(len);
                            System.out.println(recvData);
                        }
                        int f = recvData[0] + (recvData[1] << 8);
//                        f = f >> 24;
                        System.err.println("push cmd here" + f);
                        
                        GetCommandCode();
//                        GlobalStatic.is.reset();
                        if (len == 1024){
                        

//                            System.out.println(len);
//                            System.out.println(recvData);

                            

                            

 
                        }
                        else if (len < 0){
                            System.err.println("Server error");
                            GlobalStatic.connectionStatus = false;
//                            GlobalStatic.socket.close();
//                            GlobalStatic.os.close();
//                            GlobalStatic.is.close();
                              InitThreadRecon();
                              readMessage.stop();
                              break;
                              
                        }
                        
//                        if (GlobalStatic.connectionStatus == false){
//                            if (timeToRecon < 3){
//                                ConnectToServer();
//                                timeToRecon++;
//                                System.out.println("Time to recon " + timeToRecon);
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException ex) { }
//                            }
//                            if (GlobalStatic.connectionStatus == false){
//                                GlobalStatic.signinPannel.showReconBtn();
//                                System.out.println("SHOW BTN");
//                            }
//                            if (timeToRecon >= 3)
//                                readMessage.stop();
//                            
//                            if (GlobalStatic.connectionStatus == true){
//                                readMessage.start();
//                            }
//                        }
                    } catch (Exception e) {
                        
                        System.out.println(e.getMessage());
                        System.out.println("ERROR READ MESS: " + e);
                        if (!e.equals("java.lang.NullPointerException")){
//                            timeToRecon = 0;
                            InitThreadRecon();
                            readMessage.stop();
                        }
//                        reconnect.start();
//                        if (GlobalStatic.connectionStatus == false){
//                            if (timeToRecon < 3){
//                                ConnectToServer();
//                                timeToRecon++;
//                                System.out.println("Time to recon " + timeToRecon);
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException ex) { }
//                            }
//                            if (GlobalStatic.connectionStatus == false){
//                                GlobalStatic.signinPannel.showReconBtn();
//                                System.out.println("SHOW BTN");
//                            }
//                            if (timeToRecon >= 3)
//                                readMessage.stop();
//                            if (GlobalStatic.connectionStatus == true){
//                                readMessage.start();
//                            }
//                        }
                    }
                }
            }
        });
        readMessage.start();
        System.err.println("javachatclient.ClientThread.InitThread()");
    }
    
    
    public void StartReadThread(){
        InitThread();
    }
    /*
 
    *Function : ConnectToServer()
    *Description: Connect to a server with defined IP and Port.
    *Argument: Nope
    *Return: boolean
    Note:  

    */
    private boolean ConnectToServer() 
    {
        try
        {
            GlobalStatic.socket = new Socket(GlobalStatic.SERVERIP, GlobalStatic.SERVERPORT);
            GlobalStatic.os = new BufferedWriter(new OutputStreamWriter(GlobalStatic.socket.getOutputStream()));
            GlobalStatic.is = new BufferedReader(new InputStreamReader(GlobalStatic.socket.getInputStream()));
            GlobalStatic.connectionStatus = true;
            System.out.println("Connect successful");
            timeToRecon = 0;
//            sP.cG.disableReconBtn();
//            sP.statusConnect=1;
        }
        catch (IOException e)
        {
            GlobalStatic.connectionStatus = false;
            System.err.println("Error on Connect To Server: " + e.getMessage());
//            try {
//                Recon();
//            } catch (InterruptedException ex) {
//            }
//            sP.statusConnect=0;
//            sP.mG.showReconBtn();
            return false;
            
        }    
        return true;
    }
    
    /*
    Function name: Send(char[] str)
    Description: Send block to server
    Argument: int 
    Return: int error code
    Note:
    */
    private int Send(char[] str)
    {
        try{
            //String temp = str.toString();
            
            GlobalStatic.os.write(str);
 
            GlobalStatic.os.flush();
        } catch(IOException e){
            System.out.println("ERROR by SEND : "+e);
//            sP.cG.showReconBtn();
        }
        return OK;
    }
    
    /*
    Function name: GetSize()
    Description: Get Size
    Argument: None
    Return: Int
    Note:
    */
    public int GetSize(){
        int Size = (int)((recvData[4]) + (recvData[5] << 8) + (recvData[6] << 16) + (recvData[7] << 24));
        System.err.println("day la size: " + Size);
        return Size;
    }
    
    private int Split()
    {
        curUser[0] = '\0';
        desUser[0] = '\0';
        Message[0] = '\0';
        Arrays.fill(Message, '\0');
        Arrays.fill(curUser, '\0');
        Arrays.fill(desUser, '\0');
        char[] tempData = new char[1024];
        tempData = recvData;
        //Get current User
        for (int i = 0; i < 30; i++){
            if(tempData[8 + i]=='\0')
                break;
            curUser[i] = tempData[8 + i];
        }
        System.err.println( curUser);
        //Get destination User
        for (int i = 0; i < 30; i++){
            if(tempData[38 + i]=='\0')
                break;
            desUser[i] = tempData[38 + i];
        }
        System.err.println( desUser);
        //Get message
        for (int i = 0; i < 896; i++){
            if(tempData[128 + i]=='\0')
                break;
            Message[i] = tempData[128 + i];
        }
        //System.out.println(Message);
        return OK;
    }
    
    public void ClearData()
    {
        Arrays.fill(recvData, '\0');
    }
    
    /*
    Function name: RebuildCmd()
    Description:  Build int cmd from char[] 
    Argument: int
    Return: int
    Note:
    */
    private int RebuildCmd(char[] input){
        int temp = ((input[0]) + (input[1] << 8) + (input[2] << 16) + (input[3] << 24));     
        System.out.flush();
        return temp;
    }
    
    /* 
==========================================================================
                        #1005 MAIN CHECK ALL COMMAND
========================================================================== 
*/    
    /*
    Function name: GetCommandCode()
    Description: get Command code
    Argument: None
    Return: Int
    Note: 
    */
    public int GetCommandCode() throws InterruptedException
    {
        int temp = RebuildCmd(recvData);
        if (precode != temp ){
            System.err.println("Code is here");
            System.out.println(temp);
            System.out.println((int)recvData[0]);
            System.out.println((int)recvData[1]);
            System.out.println((int)recvData[2]);
            System.out.println((int)recvData[3]);
            precode = temp;
        }
/* 
==========================================================================
                        SIGN IN SIGNAL (#101,#100)
========================================================================== 
*/         
        if (temp == SIGNINOK){
            //System.err.println("aa");
            GlobalStatic.signinPannel.SetConnectStatus(1);
            
            ClearData();
            JFrame topFrame = (JFrame) SwingUtilities.getRootPane(GlobalStatic.signinPannel).getParent();
            GlobalStatic.signinPannel.ClearText();
            GlobalStatic.signinPannel.setVisible(false);
            topFrame.add(GlobalStatic.clientPannel);
            GlobalStatic.clientPannel.setVisible(true);
            
            topFrame.pack();
            
            System.err.println("JOIN IN clientPannel");
            GlobalStatic.clientPannel.StatusOnline();
            GlobalStatic.clientPannel.SetLabelName(GlobalStatic.myUserName);
            
            GlobalStatic.mainWindow= (MainWindow) SwingUtilities.getRootPane(GlobalStatic.clientPannel).getParent();
            GlobalStatic.mainWindow.ShowAccountBar();
            // sizeok
            topFrame.setResizable(false);
            topFrame.setLocationRelativeTo(null);
            topFrame.setSize(1000,625);
            /**/ //REQUEST SERVER TO RECEIVED LIST ROOM /**/
            RefreshRoomList();
            return SIGNINOK;
        }
        else if (temp == SIGNINFAIL){
//            mG.SetConnectStatus(0);
            ClearData();
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "Username / Password not correct", "Fail", mcServer);
            return SIGNINFAIL;
        }
/* 
==========================================================================
                        SIGN OUT SIGNAL (LOCAL)
========================================================================== 
*/                
        else if (signoutSignal == 1){
            ClearData();
            System.err.println("Sign out");
            signoutSignal = 0;
            /* Clear all data of current user */
            // Clear list friend
            GlobalStatic.friendList.clear();
            //Clear Online User
            GlobalStatic.onlineUser.clear();
            // Clear all Chat Room
            GlobalStatic.roomList.clear();
            //Clear all private pannel list
            GlobalStatic.pPList.clear();
            // Clear all Chat Room GUI
            GlobalStatic.clientPannel.RemoveAllTab();
            JFrame topFrame = (JFrame) SwingUtilities.getRootPane(GlobalStatic.signinPannel).getParent();
            GlobalStatic.clientPannel.setVisible(false);
            topFrame.add(GlobalStatic.signinPannel);
            GlobalStatic.signinPannel.setVisible(true);
            
            topFrame.pack();
            /*#disable account menu bar*/
            GlobalStatic.mainWindow= (MainWindow) SwingUtilities.getRootPane(GlobalStatic.clientPannel).getParent();
            GlobalStatic.mainWindow.HideAccountBar();
            Thread.sleep(500);
            return SIGNOUTOK;
        }
/*------------------------------END SIGN OUT SIGNAL------------------------------------*/                                
/* 
==========================================================================
                        SIGN UP SIGNAL (#104,#105)
========================================================================== 
*/                   
            if (temp == SIGNUPOK)
            {
                ClearData();    
                int mcServer = JOptionPane.INFORMATION_MESSAGE;
                JOptionPane.showMessageDialog (null, "Success", "Success", mcServer);
                JFrame topFrame = (JFrame) SwingUtilities.getRootPane(GlobalStatic.signupPannel).getParent();
                GlobalStatic.signupPannel.setVisible(false);
                topFrame.add(GlobalStatic.signinPannel);
                GlobalStatic.signinPannel.setVisible(true);
                topFrame.pack();
            
                return SIGNUPOK;
            }
            else if (temp == SIGNUPFAIL){
                ClearData();
                GlobalStatic.signupPannel.ShowSignUp();
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Fail", "Fail", mcServer);
                return SIGNUPFAIL;
            }        
/*------------------------------END SIGN UP SIGNAL------------------------------------*/ 

/* 
==========================================================================
                        CHANGE PASS SIGNAL (#34,#35, request #11)
========================================================================== 
*/                   
            if (temp == REPASSOK)
            {
                ClearData(); 
                GlobalStatic.myPass=GlobalStatic.myTempPass;
                int mcServer = JOptionPane.INFORMATION_MESSAGE;
                JOptionPane.showMessageDialog (null, "Success", "Success", mcServer);
                return REPASSOK;
            }
            else if (temp == REPASSFAIL){
                ClearData();
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Fail", "Fail", mcServer);
                return REPASSFAIL;
            }        
/*------------------------------END SIGN UP SIGNAL------------------------------------*/ 
/* 
==========================================================================
                        CHAT SIGNAL (#Local)
========================================================================== 
*/

/*------------------------------CLIENT CHAT SIGNAL------------------------------------*/  
        else if (temp == ProtocolCS.commandCode.PrivateChat.ordinal()){
            Split();
//            GlobalStatic.clientPannel.AddTextToBox(curUser, desUser, Message);
            GlobalStatic.AddTextToBoxPrivatePanel(curUser, curUser, Message);
            GlobalStatic.SetHaveMessPrivatePanel(String.valueOf(curUser).trim(), 1);
            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
            DataControl dataControl = new DataControl();
            try {
                if (dataControl.CheckExistXMLFile(String.valueOf(curUser).trim()) == 0){
                dataControl.curUsr = GlobalStatic.myUserName;
                dataControl.desUsr = String.valueOf(String.valueOf(curUser).trim());
                dataControl.numberOfElement = 0;
                dataControl.Message = "1st created";
                dataControl.CreateXMLFile(String.valueOf(String.valueOf(curUser).trim()));
                }
            } catch (Exception e) {
            }
            
            if (String.valueOf(desUser).trim().equals(GlobalStatic.myUserName)){

                // Add to local databases
                System.out.println(String.valueOf(curUser).trim());
                System.out.println(String.valueOf(desUser).trim());
                dataControl.curUsr = String.valueOf(curUser).trim();
                dataControl.desUsr = String.valueOf(desUser).trim();
                dataControl.numberOfElement = dataControl.CountXMLElement(String.valueOf(curUser).trim());
                dataControl.Message = String.valueOf(Message).trim();
                dataControl.AppendXMLFile(String.valueOf(curUser).trim());
            }
            ClearData();
            return RECVPRV;
        }
/*------------------------------RoomChat SIGNAL------------------------------------*/  
        else if (temp == ProtocolCS.commandCode.RoomChat.ordinal()){
            Split();
            int id = GetSize();
            GlobalStatic.AddMessInRoomGUI(id, new String(curUser).trim().replaceAll(" ", ""), new String(Message).trim());
            ClearData();
            return ProtocolCS.commandCode.RoomChat.ordinal();
        }
/*------------------------------END CHAT SIGNAL------------------------------------*/           
/* 
==========================================================================
                        ONLINE SIGNAL (#66301, )
========================================================================== 
*/
/*------------------------------update if 1 user online SIGNAL------------------------------------*/           
        else if (temp == ONLINEUSEROK){
            Split();
                System.out.println("da vao ONLINE USER");
            int result;
            result = GlobalStatic.UpdateOnlineUser(curUser,1);
            if (result == 1)
                GlobalStatic.size = GlobalStatic.size +1 ;
            ClearData();
            GlobalStatic.RealizeOnlineList();
            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
            /*temp method to revert*/
            GlobalStatic.clientPannel.PerformRefreshClick();
            return ONLINEUSERADDED;
        }
/*------------------------------END update if 1 user online SIGNAL------------------------------------*/ 
        
        /* reconnect successfully*/
        else if (temp == 998){
            return (int)998;
        }
/*------------------------------LIST ONLINE BEGIN FRIST #102------------------------------------*/              
        
        else if (temp == LISTUSEROK){
            System.err.println("RECEIVED LIST USER DATA OK");
            GlobalStatic.size = GetSize();
                System.out.println("Size khi LIST OK" + GlobalStatic.size);
            for (int i = 0; i < GlobalStatic.size; i++){
                char[] usrOnline = new char[30];
                for (int j = 4; j < 36; j++){
                    if (recvData[i * 36 + j + 8] == '\0')
                        break;
                    usrOnline[j-4] = recvData[i * 36 + j + 8];
                }
                GlobalStatic.UpdateOnlineUser(usrOnline,1);
            }
//            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
            ClearData();
            /*temp method to revert*/
//            GlobalStatic.clientPannel.PerformRefreshClick();
            return 0;
        }
/*------------------------------END LIST ONLINE BEGIN FRIST------------------------------------*/
/* 
==========================================================================
                       REFRESH LIST ROOMGUI (#15)
========================================================================== 
*/
        else if (temp == REFRESHROOM){// refresh cmd send and received
            System.err.println("Received list on Server by cmd 14");
            //max 29 user
//            for (int k = 50; k < 100; k++ ){
//                System.err.print(k);
//                System.err.println(recvData[k]);
//            }
            GlobalStatic.sizeFriendROOMGUI = GetSize();
            System.err.println("Size friend on ROOMGUI :"+ GlobalStatic.sizeFriendROOMGUI);
            int IDROOM = (int)((recvData[8]) + (recvData[9] << 8) + (recvData[10] << 16) + (recvData[11] << 24));
                //System.out.println("IDROOM GET REFRESH ROOM " + IDROOM);
            GlobalStatic.ClearListInRoomGUI(GlobalStatic.idRefreshROOM);
            for (int i = 0; i < GlobalStatic.sizeFriendROOMGUI; i++){
                char[] usrOnlineROOMGUI = new char[30];
                Arrays.fill(usrOnlineROOMGUI, '\0');
                for (int j = 0; j < 30; j++){
                    //System.out.println(4 + 12 + j + i * 34);
                    //System.out.println(recvData[4 + 12 + j + i * 34]);
                    if (recvData[4 + 12 + j + i * 36] == '\0')
                        break;
                    usrOnlineROOMGUI[j] = recvData[4 + 12 + j + i * 36];
                }
                System.out.println(String.valueOf(usrOnlineROOMGUI).trim());
                String usrROOMGUI = new String(usrOnlineROOMGUI).trim().replaceAll(" ", "");
                GlobalStatic.AddToListInRoomGUI(GlobalStatic.idRefreshROOM, usrROOMGUI);
//                System.out.println("da add" + String.valueOf(usrOnline).trim() + " 0");
            }
            ClearData();
            return 0;
        }        
        
/*------------------------------ #22 BEGIN REFRESH LIST FRIEND------------------------------------*/              
        
        else if (temp == 22){// refresh btn
            System.err.println("Received list on Server by cmd 22");
            //max 29 user
//            for (int i = 0 ; i < 1024; i++){
//                System.out.print(i);
//                System.out.println(String.valueOf(recvData[i]));
//            }
            GlobalStatic.sizeFriend = GetSize();
                System.err.println("Size friend :"+ GlobalStatic.sizeFriend);
            for (int i = 0; i < GlobalStatic.sizeFriend; i++){
                char[] usrOnline = new char[30];
                Arrays.fill(usrOnline, '\0');// su that co 36 character
                for (int j = 4; j < 36; j++){
                    if (recvData[i * 36 + j + 8] == '\0')
                        break;
                    usrOnline[j-4] = recvData[i * 36 + j + 8];
                }
                System.out.println(String.valueOf(usrOnline));
                GlobalStatic.UpdateFriendList(usrOnline,0);
                //System.out.println("da add" + String.valueOf(usrOnline).trim() + " 0");
            }
            GlobalStatic.RealizeOnlineList();
            
            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
            ClearData();
            return 0;
        }
/*------------------------------END REFRESH------------------------------------*/         
/* 
==========================================================================
                        CREATE ROOM SIGNAL (#107, )
========================================================================== 
*/        
        
/*------------------------------CREATE NEW ROOM BY OWNER------------------------------------*/                        
        else if (temp == ADDROOMOK){
            System.err.println("CREATE ROOM SUCCESS");
            int idRoom = GetSize();
            Split();
            GlobalStatic.UpdateRoomList(idRoom, GlobalStatic.nameRoomTemp.toCharArray(), GlobalStatic.passRoomTemp.toCharArray());
            GlobalStatic.InitRoomGUI(GlobalStatic.nameRoomTemp, idRoom);
            // Set owner
            if (String.valueOf(curUser).trim().equals(GlobalStatic.myUserName)){
                GlobalStatic.SetOwnerRoomGUI(idRoom);
                System.err.println("CREATE ROOM SUCCESS + Setroom Owner " + GlobalStatic.nameRoomTemp);
            }
            GlobalStatic.SetPassRoomPanel(idRoom, GlobalStatic.passRoomTemp);
            GlobalStatic.clientPannel.ReloadRoom();
            ClearData();
            return ADDROOMOK;
        }
/* 
==========================================================================
                         ROOM GUI FEATURES (#81,...,#99)
========================================================================== 
*/  
        /*-----------ADD BY OWNER--------*/
        else if (temp == INVITEROOMOK){
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "INVITED SUCCESSFULLY IN ROOM ID: " +GlobalStatic.myIDRoom +" USER : " +GlobalStatic.nameDesUser, "OWNER COMMAND", mcServer);
            System.err.println("INVITE SUCCESS IN ROOM ID: "+GlobalStatic.myIDRoom +" USER : " +GlobalStatic.nameDesUser);
            GlobalStatic.AddToListInRoomGUI(GlobalStatic.myIDRoom, GlobalStatic.nameDesUser);
            ClearData();
            return 0;
        }
        else if (temp == INVITEROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "INVITED FRIEND FAIL", "OWNER COMMAND", mcServer);
            ClearData();
            System.err.println("INVITE FAIL");
            return 0;
        }
        /*-----------HAVE BEEN INVITED BY OWNER--------*/
        else if (temp == BEINVITEDROOM){
                
            int IDRoom = (int) ((recvData[8 + 197*0 ]) 
                        + (recvData[9 + 197*0 ] << 8) 
                        + (recvData[10+ 197*0 ] << 16) 
                        + (recvData[11+ 197*0 ] << 24));
            char[] nameRoom = new char[30];
            char[] pass = new char[30];
            char[] ownerOfRoom = new char[30];
            Arrays.fill(nameRoom, '\0');
            Arrays.fill(pass, '\0');
            Arrays.fill(ownerOfRoom, '\0');
            System.out.println("IDROOM: " + IDRoom);
            for (int j = 0; j < 30; j++){
                if (recvData[j + 16] == '\0')
                    break;
                nameRoom[j] = recvData[j + 16];
            }
                
            for (int j = 0; j < 30; j++){
                if (recvData[j + 46] == '\0')
                    break;
                pass[j] = recvData[j + 46 ];
            }
            
            for (int j = 0; j < 30; j++){
                if (recvData[j + 76] == '\0')
                    break;
                ownerOfRoom[j] = recvData[j + 76 ];
            }
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOU HAVE BEEN INVITED FROM YOUR FRIEND INTO ROOM ", "HELLO GUYS", mcServer);
            ClearData();
            GlobalStatic.InitRoomGUI(String.valueOf(nameRoom).trim(), IDRoom);
            //Add Room Pannel
            
            GlobalStatic.AddToListInRoomGUI(IDRoom, new String(ownerOfRoom).trim().replaceAll(" ", ""));
            GlobalStatic.AddToListInRoomGUI(IDRoom, GlobalStatic.myUserName);
            GlobalStatic.clientPannel.ReloadRoom();
            String nameRoomS=String.valueOf(nameRoom).trim();
            GlobalStatic.clientPannel.AddTabRoom(nameRoomS, IDRoom);
            return 0;
        }
        /*-----------KICK BY OWNER--------*/
        else if (temp == KICKROOMOK){
            ClearData();
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, " KICK SUCCESS ", "OWNER COMMAND", mcServer);
            System.err.println("KICK SUCCESS");
            GlobalStatic.RemoveToListInRoomGUI(GlobalStatic.myIDRoom, GlobalStatic.nameDesUser);
            return 0;
        }
        
        else if (temp == KICKROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "KICK FAIL", "FAIL", mcServer);
            ClearData();
            System.err.println("KICK FAIL");
            return 0;
        }
        /*-----------YOU HAVE BEEN KICKED BY OWNER--------*/
        /*Clean */
        else if (temp == BEKICKROOM){
            //rG.ReloadListFriendOnRoom(0);
            int id = GetSize();
            GlobalStatic.TerminateRoomGUI(id);
            ClearData();
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOU HAVE BEEN KICKED FROM YOUR FRIEND INTO ROOM ", "HEY GUYS", mcServer);
            System.err.println("YOU HAVE BEEN KICKED BY OWNER ");
            return 0;
        }
        
        /*LOST DATA DUE TO NO FUNCTION OR CMD TO NOTIFY OTHER ,THIS KICKED'S MAN IN ROOM */
        else if (temp == HAVEKICK){
            char[] kickedUser = new char[30];
                Arrays.fill(kickedUser, '\0'); // fill array
                for (int i = 0; i < 30; i++){
                    kickedUser[i] = recvData[38 + i];
                }
                String kUser=  new String(kickedUser).trim().replaceAll(" ", "");
            if(kUser.equals(GlobalStatic.myUserName)){
//                sP.TerminateRoomGUI(idROOM);
                ClearData();
                int mcServer = JOptionPane.INFORMATION_MESSAGE;
                JOptionPane.showMessageDialog (null, "YOU HAVE BEEN KICKED FROM YOUR FRIEND INTO ROOM ", "HEY GUYS", mcServer);
                System.err.println("YOU HAVE BEEN KICKED BY OWNER ");
            }
            else{
                ClearData();
                System.err.println("ONE MAN GO TO HEAVEN ");
            }
            ClearData();
            System.err.println("ONE MAN GO TO HEAVEN ");
            return HAVEKICK;
        }
        /*-----------LET ME BEING ROOM #87-------*/
        else if (temp == LETROOMOK){
            int IDRoom = (int)((recvData[8]) + (recvData[9] << 8) + (recvData[10] << 16) + (recvData[11] << 24));
                System.out.println("ID LETROOMOK nhan dc "+ IDRoom);
            char[] acceptedUser = new char[30];
            Arrays.fill(acceptedUser, '\0'); // fill array
            for (int i = 0; i < 30; i++){
                acceptedUser[i] = recvData[8 + i];
            }
//            String sAcceptedUser=new String(acceptedUser).trim().replaceAll(" ", "");
//            System.out.println("Accepted usr: " + sAcceptedUser +" Myname :"+ GlobalStatic.myUserName);
            {
               int mcServer = JOptionPane.INFORMATION_MESSAGE;
               JOptionPane.showMessageDialog (null, "YOU HAVE BEEN AT THIS ROOM ", "HEY GUYS", mcServer);
               System.err.println("YOU HAVE BEEN AT THIS ROOM ");
               ClearData();
               String nameRoom = GlobalStatic.GetNameROOM(IDRoom);
                System.out.println("Name room = " + nameRoom);
               GlobalStatic.InitRoomGUI(nameRoom, IDRoom);
               GlobalStatic.AddToListInRoomGUI(IDRoom, GlobalStatic.myUserName);
               
               GlobalStatic.clientPannel.AddTabRoom(nameRoom, IDRoom);
               GlobalStatic.SetPassRoomPanel(IDRoom, GlobalStatic.passRoomTemp);
               
               return LETROOMOK;   
            }
            
        }
        else if (temp == LETROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "NOT ACCESS", "HEY GUYS", mcServer);
            System.err.println("NOT ACCESS ");
            return 0;
        }
        /*-----------CHANGE PASS ROOM FROM OWNER-------*/
        else if (temp == PASSROOMOK){
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOUR ROOM'S PASSWORD HAVE BEEN CHANGE ", "HEY GUYS", mcServer);
            System.err.println("YOUR ROOM'S PASSWORD HAVE BEEN CHANGE ");
            ClearData();
            return 0;
        }
        else if (temp == PASSROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "NOT ACCESS", "HEY GUYS", mcServer);
            System.err.println("NOT ACCESS ");
            return 0;
        }
        /*-----------DELETE ROOM FROM OWNER-------*/
        else if (temp == DELETEROOMOK){
            int IDRoom = GetSize();
            String nameRoom=GlobalStatic.GetNameROOM(IDRoom);
            GlobalStatic.TerminateRoomGUI(IDRoom);
            if(GlobalStatic.nameOwner.equals(GlobalStatic.myUserName) && GlobalStatic.idRoomOwner==IDRoom){               
                String notify = "<html>YOUR ROOM'S  <span style='color:green'>"+nameRoom+"</span> HAVE BEEN DELETED</html>";
                JOptionPane.showMessageDialog(null, notify);
            }
            GlobalStatic.RemoveListRoomOnClientGUI(IDRoom);
            System.err.println("YOUR ROOM'S "+IDRoom+ "HAVE BEEN DELETED");
            GlobalStatic.clientPannel.ReloadRoom();
            System.err.println("YOUR ROOM'S HAVE BEEN DELETED");
            ClearData();
            return 0;
        }
        else if (temp == DELETEROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "NOT ACCESS", "HEY GUYS", mcServer);
            System.err.println("NOT ACCESS ");
            return 0;
        }
        
        /*-----------SOMEONE WITH ROOM-------*/
        else if (temp == ONEINROOM){
            int idRoom = GetSize();
            char[] user = new char[30];
            Arrays.fill(user, '\0'); 
            for (int i = 0; i < 30; i++){
                user[i] = recvData[8 + i];
            }
            System.out.println("Someone In: " + String.valueOf(user).trim());
            GlobalStatic.AddToListInRoomGUI(idRoom, String.valueOf(user).trim());
        }
        
        else if (temp == ONEOUTROOM){
            int idRoom = GetSize();
            char[] user = new char[30];
            Arrays.fill(user, '\0'); // fill array
            for (int i = 0; i < 30; i++){
                user[i] = recvData[8 + i];
            }
            System.out.println("Someone Out: " + String.valueOf(user).trim());
            GlobalStatic.RemoveToListInRoomGUI(idRoom, String.valueOf(user).trim());
        }
        /*/*-----------REPASS WITH ROOM (#89 #90)-------*/
        else if (temp == REPASSROOMOK){
            System.err.println("REPASS ROOM SUCCESS");
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "SUCCESS", "SERVER: ", mcServer);
            ClearData();
            return 0;
        }
        else if (temp == REPASSROOMFAIL){
            System.err.println("REPASS ROOM FAIL");
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "FAIL", "SERVER: ", mcServer);
            ClearData();
            return 0;
        }
/*------------------------------END ROOM GUI FEATURE------------------------------------*/                                
        
/*------------------------------add new friend appect------------------------------------*/                        
        else if (temp == ADDFRIENDOK){
            Split();// desUser
            char[] usrOnline = new char[30];
            usrOnline = desUser;
            GlobalStatic.UpdateOnlineUser(usrOnline,0);
            System.err.println("ADD FRIEND SUCCESS");
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "ADD FRIEND SUCCESS", "Success", mcServer);
            GlobalStatic.clientPannel.PerformRefreshClick();
            ClearData();
            return 0;
        }
        else if (temp == ADDFRIENDFAIL){
            ClearData();
            System.err.println("ADD FRIEND FAIL");
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOUR FRIEND'S USER NAME NOT FOUND ON SERVER", "ERROR", mcServer);
            return 0;
        }
/*--------------------------------NOTIFY ONE ROOM HAVE BEING CREATED----------------------------------*/                                
        else if (temp == ROOMCREATE){
            System.err.println("ONE ROOM HAVE BEING CREATED");
            int id = GetSize();
            Split();
            GlobalStatic.UpdateRoomList(id, desUser, "".toCharArray());
            GlobalStatic.InitRoomGUI(String.valueOf(desUser), id);
            // Set owner
            if (String.valueOf(curUser).trim().equals(GlobalStatic.myUserName)){
//                sP.SetOwnerRoomGUI(id);
            }
            else{
                GlobalStatic.AddToListInRoomGUI(id, String.valueOf(curUser).trim());
                System.out.println("ADDED CAPTAIN: " + String.valueOf(curUser).trim());
            }
            GlobalStatic.clientPannel.ReloadRoom();
            return ROOMCREATE;
        }
/*-----------------------------------------------------------------------------*/  
/* 
==========================================================================
                        BEING CAPTIAN  (#43)
========================================================================== 
*/                   
        else if (temp == REVCAP){
            System.out.println("RECAP");
            int idRoom = GetSize();
                
            GlobalStatic.SetOwnerRoomGUI(idRoom);
            GlobalStatic.clientPannel.ReloadRoom();
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            String nameRoom= GlobalStatic.GetNameROOM(idRoom);
            JOptionPane.showMessageDialog (null, "YOU ARE OWNER ON ROOM : "+nameRoom , "SERVER ADMIN", mcServer);
            ClearData();
            return REVCAP;
        }
                   
/*------------------------------END SET CAPTIAN  (#43) SIGNAL------------------------------------*/   
            
/*--------------------------------LIST ROOM BEGIN #42 replace for #103----------------------------------*/                                
        else if (temp == LISTROOMOK){
 
            
            System.err.println("RECEIVED DATA ROOM");
//                System.err.println(recvData[0]);
            GlobalStatic.sizeRoom = GetSize();
                System.out.println("Size in List Room OK " + GlobalStatic.sizeRoom);
//            GlobalStatic.sizeRoom = GlobalStatic.sizeRoom >> 24;
            
//            for (int k = 508; k < 1024; k++){
//                    System.err.print(k);
//                    System.out.println((int)recvData[k]);
//                }
            // Get data of Room
            for (int i = 0; i < GlobalStatic.sizeRoom; i++){
                int id;
                char[] nameRoom = new char[30];
                char[] pass = new char[30];
                Arrays.fill(nameRoom, '\0');
                Arrays.fill(pass, '\0');
                
                id = (int) ((recvData[8 + 500 * i]) 
                        + (recvData[9 + 500 * i] << 8) 
                        + (recvData[10+ 500 * i] << 16) 
                        + (recvData[11+ 500 * i] << 24));
                System.out.println("ID = " + id);
                for (int j = 0; j < 30; j++){
                    if (recvData[j + 16 + i * 500] == '\0')
                        break;
                    nameRoom[j] = recvData[j + 16 + i * 500];
                }
                for (int j = 0; j < 30; j++){
                    if (recvData[j + 46 + i * 500] == '\0')
                        break;
                    pass[j] = recvData[j + 46 + i * 500];
                }
                System.out.println(id);
                System.out.println(String.valueOf(nameRoom));
                System.out.println(String.valueOf(pass));
                GlobalStatic.UpdateRoomList(id, nameRoom, pass);
            }

            
            GlobalStatic.clientPannel.ReloadRoom();
            
            /* reload list room  */
            ClearData();
            return 0;
            
        }
/*--------------------------------END LIST ROOM----------------------------------*/                                        
        //109 somebody out
        else if (temp == OUTOK){
            
            /*find him*/
            
            char[] usrOffline = new char[30];
            for (int j = 0; j < 30; j++){
                if (recvData[j+ 8] == '\0')
                    break;
                usrOffline[j] = recvData[j+ 8];
            }
            GlobalStatic.UpdateOnlineUser(usrOffline,0);
            GlobalStatic.size = GlobalStatic.size-1;
            ClearData();
                try {
                    GlobalStatic.RealizeOnlineList();
                } catch (Exception e) {
                }
            GlobalStatic.clientPannel.Reload(GlobalStatic.sizeFriend);
            
            return OUTDONE;
        }
/*--------------------------------CMD 2000 CATCH ERR??---------------------------------*/                                
        else if (temp == 2000){
            //System.exit(0);
            return 0;
        }
        else if (temp == 124){
                System.out.println("javachatclient.ClientThread.GetCommandCode() 124 OK");
        }
        return temp;
    }
    /*
 
    *Function : Recon 
    *Description: Reconnect server
    *Argument:  void
    *Return:  
    Note:  

    */
    public void Recon() throws InterruptedException{
        while(timeToRecon<3){
             if(GlobalStatic.connectionStatus){
                  GlobalStatic.ClearAllData();
                  System.err.println("CLEARING ALL DATA");
                  SignIn(GlobalStatic.myUserName, GlobalStatic.myPass);
                  GlobalStatic.clientPannel.PerformRefreshClick();
                  System.err.println("SUCCESSED REFRESH AND RELOG");
                  break;
             }
             else{
                 ConnectToServer();
                 timeToRecon++;
                 Thread.sleep(2000);
             }
        }
        System.err.println("OVERLOAD "+ timeToRecon);
        GlobalStatic.clientPannel.showReconBtn();  
        GlobalStatic.signinPannel.showReconBtn();
        int mcServer = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog (null, "Please click Reconect  ", "Can not auto reconnect", mcServer);
        reconnect.stop();
   }
    /*
    *Function : SignIn(String usr, String pass)
    *Description: Send block data to server
    *Argument:  String usr, String pass
    *Return: 1<-successful
    Note:  
    */
    public int SignIn(String usr, String pass)
    {
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.Signin.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = usr;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = pass;
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "";
        GlobalStatic.blockToSend.mIDUser = 1;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: LetMeBeInRoom()
    Description: LetMeBeInRoom
    Argument: int,String
    Return: Int
    Note:
    */
    public int LetMeBeInRoom(int roomID, String passRoom)
    {
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RequestToRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = roomID;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = passRoom;
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO JOIN ROOM WITH ID :"+roomID;
        GlobalStatic.myIDRoom=roomID;
        GlobalStatic.passRoomTemp = passRoom;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: AddFriend()
    Description: Send add friend command to server
    Argument: String username
    Return: Int
    Note:
    */
    public int AddFriend(String usr)
    {
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RequestFriend.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = usr; // friend's name.
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO ADD THIS NAME "+usr+ " to FRIEND LIST";
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: SendPrivateMessage(String usr)
    
    Description: Send message to a user
    Argument: String user, String message
    Return: int error code
    Note:
    */
    public int SendPrivateMessage(String desusr, String message)
    {
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.PrivateChat.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = desusr;
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = message;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    *Function : ReFresh(String usr, String pass)
    *Description: ReFresh
    *Argument:  int
    *Return: s
    Note:  
    */
    public int ReFresh(){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.ReFresh.ordinal();//21
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+" want to received a list user online";
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    *Function : SignUp(String usr, String pass)
    *Description: Send block data to server
    *Argument:  String usr, String pass 
    *Return: 1  <---- successful
    Note:  
    */
    public int SignUp(String usr, String pass){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.Signup.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = usr;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = pass;
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "";
        signupSignal = 1;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: AddFriendToRoom()
    Description: Add new user to a specific room
    Argument: String roomname, string user
    Return: Int
    Note:
    */
    public int AddFriendToRoom(int roomID, String desusr){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.InviteToRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = roomID;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = desusr;
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+" want to add friend: "+desusr+ " to ROOM WITH ID: "+roomID;
        GlobalStatic.myIDRoom = roomID;
        GlobalStatic.nameDesUser = desusr;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: RemoveFriendFromRoom(String usr)
    Description: Send Remove friend command to server
    Argument: String username
    Return: Int
    Note:
    */
    public int RemoveFriendFromRoom(String desusr, int roomID, String roomPass){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.KickFromRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = roomID;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = desusr;
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+" want to remove friend: "+desusr+ " on ROOM";
        GlobalStatic.myIDRoom = roomID;
        GlobalStatic.nameDesUser = desusr;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: SendMsgToRoom()
    Description: Send message to a specific room
    Argument: String roomname, string message
    Return: Int
    Note:
    */
    public int SendMsgToRoom(int idRoom, String msg){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RoomChat.ordinal();
        GlobalStatic.blockToSend.IDRoom = idRoom;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = msg;
        return Send(GlobalStatic.blockToSend.GetText()); 
    }
    /*
    Function name: TerminateRoom()
    Description: TerminateRoom
    Argument: int
    Return: Int
    Note:
    */
    public int TerminateRoom(int roomID){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.DeleteRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = roomID;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User: " + GlobalStatic.myUserName + " WANT TO TERMINATE ROOM "+ roomID;
        GlobalStatic.nameOwner = GlobalStatic.myUserName;
        GlobalStatic.idRoomOwner = roomID;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: ReFreshROOMGUI()
    Description: ReFresh LIST ROOMGUI
    Argument: int
    Return: Int
    Note:
    */
    public int ReFreshROOMGUI(int idROOM){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RefreshList.ordinal();//15
        GlobalStatic.blockToSend.IDRoom = idROOM;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "Request to received a list user in this room with ID " +idROOM;
        GlobalStatic.idRefreshROOM = idROOM;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: AddNewRoom(String nameROOM, String passROOM)
    Description: CREATE New Room  
    Argument: int
    Return: Int
    Note:
    */
    public int AddNewRoom(String nameROOM, String passROOM){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.CreateRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;// not appect on SERVER
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = nameROOM;// ROOM NAME 
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = passROOM;
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO CREATE ROOM WITH ROOMNAME "+nameROOM+ " and PASS "+ passROOM;
        GlobalStatic.passRoomTemp = passROOM;
        GlobalStatic.nameRoomTemp = nameROOM;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    *Function : SignOut()
    *Description: Send block data to server
    *Argument:  int
    *Return: 1  <---- successful
    Note:  
    */
    public int SignOut(){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.SignOut.ordinal();
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = GlobalStatic.myPass;
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "DANG XUAT USER : "+GlobalStatic.myUserName;
        signoutSignal = 1;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: Repass
    Description: Repass - 11 , send block to server
    Argument: Int
    Return: Send block to server
    Note:
    */
    public int AddToBlackList(String desur){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.AddBlack.ordinal();//16
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = desur;
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO ADD: "+desur+ " TO BLACK LIST";
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: Repass
    Description: Repass - 11 , send block to server
    Argument: Int
    Return: Send block to server
    Note:
    */
    public int RemoveFromBlackList(String desur){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RemoveBlack.ordinal();//17
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = desur;
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO REMOVE: "+desur+ " FROM BLACK LIST";
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: Repass
    Description: Repass - 11 , send block to server
    Argument: Int
    Return: Send block to server
    Note:
    */
    public int RePassUser(String passU){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RePass.ordinal();//11
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = passU;
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "USER: "+GlobalStatic.myUserName+ " WANT TO CHANGE PASS";
        GlobalStatic.myTempPass=passU;
        
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: RefreshRoom(int roomID)
    Description: Refresh list user in Room on ROOM PAN
    Argument: Int
    Return: int
    Note:
    */
    public int RefreshRoom(int roomID){
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RefreshList.ordinal();
        GlobalStatic.blockToSend.IDRoom = roomID;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "CLIENT WANT TO REICEVE ALL MAN IN ROOM ID : "+roomID;
        GlobalStatic.idRefreshROOM=roomID;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: RefreshRoom(int roomID)
    Description: Refresh list Room on Client PAN
    Argument: Int
    Return: int
    Note:
    */
    public int RefreshRoomList(){
        System.out.println("Sent REFRESH ROOM LIST");
        GlobalStatic.blockToSend.command = LISTROOMOK;
        GlobalStatic.blockToSend.IDRoom = 0;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+" want to refresh room  to received a list room";
        GlobalStatic.idRefreshROOM=0;
        GlobalStatic.roomList.clear();
//        GlobalStatic.sizeRoom = 0;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    
    /*
    Function name: IOutRoom(int id)
    Description: Notify server user have left on specific room.
    Argument: Int
    Return: int
    Note:
    */
    public int IOutRoom(int id){
        System.out.println("Sent IOutRoom");
        GlobalStatic.blockToSend.command = 14;
        GlobalStatic.blockToSend.IDRoom = id;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = "";
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+ " HAVE LEFT ROOM WITH ID USER: "+id;
        GlobalStatic.idRefreshROOM=0;
        return Send(GlobalStatic.blockToSend.GetText());
    }
    /*
    Function name: IOutRoom(int id)
    Description: Notify server user have left on specific room.
    Argument: Int
    Return: int
    Note:
    */
    public int RePassRoom(int idRoom, String passRoom){
        System.out.println("RePassRoom");
        GlobalStatic.blockToSend.command = ProtocolCS.commandCode.RePassRoom.ordinal();
        GlobalStatic.blockToSend.IDRoom = idRoom;
        GlobalStatic.blockToSend.ownUsername = GlobalStatic.myUserName;
        GlobalStatic.blockToSend.desUsername = "";
        GlobalStatic.blockToSend.ownPassword = "";
        GlobalStatic.blockToSend.roomPassword = passRoom;
        GlobalStatic.blockToSend.message = "User : "+GlobalStatic.myUserName+ " WANT TO REPASS: "+ passRoom+" ROOM  WITH ID ROOM: "+idRoom;
        //GlobalStatic.idRefreshROOM=0;
        return Send(GlobalStatic.blockToSend.GetText());
    }
}
