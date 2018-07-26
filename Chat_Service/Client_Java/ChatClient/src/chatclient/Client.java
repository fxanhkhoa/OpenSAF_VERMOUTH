/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.net.*;
import java.io.*;
import chatclient.Global;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 *
 * @author shawry
 */

public class Client {
    /*
    Constant status of function return
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
    public final int REPASSOK = 133;
    /*109 dang xuat*/
    public final int OUTOK = 109;
    /*----------------------------------*/
/*---------------CHAT FEATURES COMMAND CODE-------------------*/
    /* add to show list user & */
    
    public final int LISTUSEROK = 102;
    /*list room */
    public final int LISTROOMOK = 103;
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
    public final int INVITEROOMFAIL =80;// 200;
    public final int INVITEROOMOK = 81;//201;
    public final int BEINVITEDROOM = 82;//202;
    /*KICK FROM OWNER*/
    public final int KICKROOMFAIL = 84;//204;
    public final int KICKROOMOK = 85;//205;
    public final int BEKICKROOM = 86;//206;
    /*LET ME BEING ROOM*/
    public final int LETROOMFAIL =88;// 208;
    public final int LETROOMOK = 87;//207; // 
    /*CHANGE PASS ROOM FROM OWNER*/
    public final int PASSROOMFAIL =90;// 210
    public final int PASSROOMOK = 89;//209;
    
/*----------------------------------------------------------------------------*/
    /* add to show list user & list room */
    
    public final int REQUIREADD = 500;
    //public final int LISTROOMOK = 103;
    
    /*----------------------------------*/
    public final int RADDOK = 8;
    public final int RDELOK = 9;
    
    public final int RECVPRV = 11;
    public final int RECVROOM = 12;
    
    /*
    Private Variables
    */
    private char[] recvData = new char[1024];
    private boolean status = false;
    private int isDataReceived = 0;
    public static Thread readMessage,reconnect;
    private char[] desUser = new char[30];
    private char[] curUser = new char[30];
    private char[] Message = new char[896];
    public String userNameInGlobal;
    private ProtocolCS blockToSend;
    private int len;
    
    private int signoutSignal=0,signupSignal=0;
    private boolean ok = false;
    
    private int precode;
    public int savedcmd;
    
    private String passRoom;
    
    private MainGUI mG;
    private ClientGUI cG;
    private RoomGUI rG;
            
    private Global g;
    public SavedPreference sP;
    
    private static Client instance;
    /*
    Constructor Function
    */
    public Client(){
        g = Global.getInstance();
        sP = SavedPreference.getInstance();
    }
    
    public static synchronized Client getInstance(){
        if (instance == null){
            instance = new Client();
        }
        return instance;
    }

    
    protected void InitVar(){
        blockToSend = new ProtocolCS();
    }
    
    protected void InitSocket(){
        status = ConnectToServer();
    }
    
    protected void Init(){
        
        
        reconnect = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                        if (status == false){
                            ConnectToServer();
                        }
                        else if (status == true){
                            g.os = new BufferedWriter(new OutputStreamWriter(g.socket.getOutputStream()));
                            g.is = new BufferedReader(new InputStreamReader(g.socket.getInputStream()));
                            reconnect.stop();
                        }
                        System.err.println("in");
//                        if (GetCommandCode() != SIGNINOK){
//                            SignIn(sP.GetUserName(), sP.GetPassword());
//                        }
//                        else if (GetCommandCode() == SIGNINOK){
//                            System.err.println("Sign again ok");
//                            reconnect.stop();
//                        }
                        if (GetCommandCode() != 66302){
                            //readMessage.wait(1000);
                            System.err.println("in send 999");
                            blockToSend.command = 999;
                            blockToSend.IDRoom = 0;
                            blockToSend.ownUsername = sP.GetUserName();
                            blockToSend.desUsername = "";
                            blockToSend.ownPassword = "";
                            blockToSend.roomPassword = "";
                            blockToSend.message = "";
                            blockToSend.mIDUser = sP.GetIDUser();
                            Send(blockToSend.GetText());
                            System.err.println("send 999 ok");
                            System.err.print(blockToSend.mIDUser);
                            System.err.println(blockToSend.command);
                            System.out.println("start of command");
                            System.out.println((blockToSend.command >> 0) & 0xFF);
                            System.out.println((blockToSend.command >> 8) & 0xFF);
                            System.out.println((blockToSend.command >> 16) & 0xFF);
                            System.out.println((blockToSend.command >> 24) & 0xFF);
                        }
                        else if (GetCommandCode() == 66302){
                            System.err.println("gotcha");
                            reconnect.stop();
                        }
//                        if (IsDataReceived() == 1){
//                            System.err.println("thread stop");
//                            reconnect.stop();
//                        }

                    } catch (Exception e) {
                    }
                }
            }
        });
        readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
//                        //recvData = "";
                        if (status == true){
                            
                        }
                        len = g.is.read(recvData);
 
                        //System.out.println(len);
                        if (GetCommandCode() == LISTUSEROK ){
                            System.out.println("list vao " + len);
                            //Split();
//                            curUser[0] = recvData[12];
//                            curUser[1] = recvData[13];
//                            curUser[2] = recvData[14];
                        }

                        if (len == 1024){
                            GetCommandCode();
//                            recvData.trim();
                            //recvData.replace("null", "");
                            isDataReceived = 1;
//                            //Send(recvData);
////                            //if (recvData.contains("CLSIGNOK")) status = false;
                            System.out.println(len);
                            System.out.println(recvData);
//                            System.out.println(Integer.toBinaryString(recvData[0]));
//                            System.out.println((byte)recvData[1]);
//                            System.out.println((char)recvData[2]);
//                            System.out.println((char)recvData[3]);
//                            System.out.println((char)65533);
//                            char k = 230
                            int f = recvData[0] + (recvData[1] << 8);
                            System.err.println("push cmd here" + f);
//                            char k = 229, k1 = 3;
//                            int t = (int)k + (k1 << 8);
//                            System.err.println(t);
                            g.is.reset();
 
//                            if (GetCommandCode() == 102){
//                                System.err.println("102 OK");
//                                reconnect.stop();
//                            }
//                            else if (GetCommandCode() == 103){
//                                System.err.println("103 OK");
//                            }
 
                        }
                        else if (len < 0){
                            System.err.println("Server error");
                            status = false;
                            g.socket.close();
                            g.os.close();
                            g.is.close();
                            isDataReceived = 0;
                           //readMessage.stop();
                           //ConnectToServer();
//                           int mc = JOptionPane.WARNING_MESSAGE;
//                           JOptionPane.showMessageDialog (null, "Server got something wrong, Reconnect?", "Warning", mc);
                            //Thread.sleep(5000);
//                           if (!reconnect.isAlive())
//                           {
//                               reconnect.start();
//                               System.out.println("Reconnect successfully");
//                           }
                            sP.cG.showReconBtn();
                            sP.mG.showReconBtn();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        readMessage.start();
        System.err.println("Started Thread");
    }
    
    /*
 
    *Function : StopRecon()
    *Description: Stop ReConnect to a server  .
    *Argument: Nope
    *Return: None.
    Note:  

    */
    
   public void StopRecon(){
       if(reconnect.isAlive())
       reconnect.interrupt();
       
   }
   
   public void Recon(){
       boolean check=ConnectToServer();
       SignIn(sP.GetUserName(), sP.GetPassword());
       if(!check)
            sP.mG.showReconBtn();
   }
   public void ReconLog()
   {
        boolean check=ConnectToServer();
       if(!check)
            sP.mG.showReconBtn();   
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
            this.g.socket = new Socket(sP.GetServerIP(),sP.GetServerPORT());
            g.os = new BufferedWriter(new OutputStreamWriter(g.socket.getOutputStream()));
            g.is = new BufferedReader(new InputStreamReader(g.socket.getInputStream()));
            status=true;
            System.out.println("Connect successful");
            sP.cG.disableReconBtn();
            sP.statusConnect=1;
        }
        catch (IOException e)
        {
            status=false;
            System.err.println("Error: " + e.getMessage());
            sP.statusConnect=0;
            sP.mG.showReconBtn();
            return false;
            
        }    
        return true;
    }
    /*
 
    *Function : GetStatus()
    *Description: Check status.
    *Argument: Nope
    *Return: true if connect successfully to server /false if fail.
    Note:  

    */
    public boolean GetStatus()
    {
        return status;
    }
    /*
 
    *Function : ClearData()
    *Description: Clear data recv.
    *Argument: Nope
    *Return: None
    Note:  

    */
    public void ClearData()
    {
        recvData[0] = '\0';
//        recvData[1] = 0;
//        recvData[2] = 0;
//        recvData[3] = 0;
        //recvData = "";
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
        blockToSend.command = ProtocolCS.commandCode.Signin.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = usr;
        blockToSend.desUsername = "";
        blockToSend.ownPassword = pass;
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        blockToSend.mIDUser = 1;
        System.out.print((blockToSend.command >> 0) & 0xFF);
                            System.out.print((blockToSend.command >> 8) & 0xFF);
                            System.out.print((blockToSend.command >> 16) & 0xFF);
                            System.out.println((blockToSend.command >> 24) & 0xFF);
        return Send(blockToSend.GetText());
    }
    /*
 
    *Function : SignUp(String usr, String pass)
    *Description: Send block data to server
    *Argument:  String usr, String pass 
    *Return: 1  <---- successful
    Note:  

    */
    public int SignUp(String usr, String pass)
    {
        blockToSend.command = ProtocolCS.commandCode.Signup.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = usr;
        blockToSend.desUsername = "";
        blockToSend.ownPassword = pass;
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        signupSignal = 1;
        return Send(blockToSend.GetText());
    }
    /*
 
    *Function : SignOut()
    *Description: Send block data to server
    *Argument:  int
    *Return: 1  <---- successful
    Note:  

    */
    public int SignOut()
    {
        blockToSend.command = ProtocolCS.commandCode.SignOut.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = sP.GetPassword();
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        
        signoutSignal = 1;
        System.err.println("sign out signal : "+signoutSignal );
        
        return Send(blockToSend.GetText());
    }
    /*
    Function name: Repass
    Description: Repass - 11 , send block to server
    Argument: Int
    Return: Send block to server
    Note:
    */
    public int RePass(String passU)
    {
        blockToSend.command = ProtocolCS.commandCode.RePass.ordinal();//11
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = passU;
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        return Send(blockToSend.GetText());
    }
    public int ReFresh()
    {
        blockToSend.command = ProtocolCS.commandCode.ReFresh.ordinal();//21
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        return Send(blockToSend.GetText());
    }
    
    public void GetOnlineList()
    {
        
    }
    public void GetAllList()
    {
        
    }
    public void GetPendingList()
    {
        
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
            
            g.os.write(str);
 
            g.os.flush();
            //System.out.flush();
            //System.out.print(blockToSend.command + blockToSend.IDRoom );
            //System.out.print(str);
            //System.out.println("zzzzz");
        } catch(IOException e){
            System.out.println(e);
        }
        return OK;
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
        blockToSend.command = ProtocolCS.commandCode.PrivateChat.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = desusr;
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = message;
        return Send(blockToSend.GetText());
    }
    
    /*
    Function name: Receive_data()
    Description: get current data in client
    Argument: char[] 
    Return: char[] data receive
    Note:
    */
    public char[] ReceiveData()
    {
        return recvData;
    }
    /*
    Function name: IsDataReceived()
    Description: Return 1 if data received else 0
    Argument: None
    Return: int
    Note:
    */
    public int IsDataReceived(){   
        if (isDataReceived > 0)
            return OK;
        return ERROR;
    }
    
    public void SetDataReceived(){
        
    }
    /*
    Function name: StartReceive()
    Description: Start Receive thread
    Argument: NOne 
    Return: int
    Note:
    */
    public int StartReceive()
    {
        try {
            readMessage.start();
        } catch (Exception e) {
            return ERROR;
        }
        return OK;
    }
    /*
    Function name: StopReceive()
    Description: Stop Receive thread
    Argument: None 
    Return: int
    Note:
    */
    public int StopReceive()
    {
        try {
            readMessage.stop();
        } catch (Exception e) {
        }
        return OK;
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
        blockToSend.command = ProtocolCS.commandCode.RequestFriend.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = usr; // friend's name.
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        return Send(blockToSend.GetText());
    }
    
    /*
    Function name: RemoveFriendFromRoom(String usr)
    Description: Send Remove friend command to server
    Argument: String username
    Return: Int
    Note:
    */
    public int RemoveFriendFromRoom(String desusr, int roomID, String roomPass)
    {
        blockToSend.command = ProtocolCS.commandCode.KickFromRoom.ordinal();
        blockToSend.IDRoom = roomID;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = desusr;
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        return Send(blockToSend.GetText());
    }
    /*
    Function name: RemoveFriend()
    Description: Remove a friend from my list
    Argument: String username
    Return: Int
    Note:
    */
    public int RemoveFriend(String username)
    {
        return 1;
    }
 
    public void NewGroupDM()
    {
        
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
//        System.out.print((int)input[0]);
//        System.out.print((int)input[1]);
//        System.out.print((int)input[2]);
//        System.out.println((int)input[3]);
        return temp;
    }
    /*
    Function name: GetIDRoom
    Description: get ID ROOM
    Argument: Nope
    Return: Int
    Note:
    */
      public int GetIDRoom(){
        int IDRoom = (int)((recvData[4]) | (recvData[5] << 8) | (recvData[6] << 16) | (recvData[7] << 24));
        System.out.println(IDRoom);
        return (int)((recvData[4]) | (recvData[5] << 8) | (recvData[6] << 16) | (recvData[7] << 24));
    }
    
    
    /*
    Function name: GetCommandCode()
    Description: get Command code
    Argument: None
    Return: Int
    Note: #1005 MAIN CHECK ALL COMMAND
    */
    public int GetCommandCode() throws InterruptedException
    {
//        if ((len != 0) && (len != 1020))
//            System.err.println(len);
        int temp = RebuildCmd(recvData);
//        System.out.println("saved cmd " + savedcmd);
        if (precode != temp ){
            System.err.println("BEGGGGGGGGGGGGGGGG");
            System.out.println(temp);
            System.out.println((int)recvData[0]);
            System.out.println((int)recvData[1]);
            System.out.println((int)recvData[2]);
            System.out.println((int)recvData[3]);
            precode = temp;
        }
/*------------------------------SIGN IN SIGNAL------------------------------------*/                        
        if (temp == SIGNINOK){
            //System.err.println("aa");
            sP.mG.SetConnectStatus(1);
            
            ClearData();
            
            sP.HideMainGUI();
            sP.OpenClientGUI();
            sP.cG.StatusOnline();
            sP.cG.SetLabelName(sP.GetUserName());
            return SIGNINOK;
        }
        else if (temp == SIGNINFAIL){
            mG.SetConnectStatus(0);
            ClearData();
            return SIGNINFAIL;
        }
/*------------------------------SIGN OUT SIGNAL------------------------------------*/                        
        else if (signoutSignal == 1){
            ClearData();
            sP.HideClientGUI();
            sP.OpenMainGUI();
            Thread.sleep(2000);
            ReconLog();
            sP.HideClientGUI();
            System.err.println("Sign out");
            signoutSignal = 0;
            return SIGNOUTOK;
        }
/*------------------------------END SIGN OUT SIGNAL------------------------------------*/                                

/*------------------------------SIGN UP SIGNAL------------------------------------*/                        
            if (temp == SIGNUPOK)
            {
                ClearData();    
                int mcServer = JOptionPane.INFORMATION_MESSAGE;
                JOptionPane.showMessageDialog (null, "Success", "Success", mcServer);
                return SIGNUPOK;
            }
            else if (temp == SIGNUPFAIL){
                ClearData();
                int mcServer = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog (null, "Fail", "Fail", mcServer);
                return SIGNUPFAIL;
            }        
/*------------------------------END SIGN UP SIGNAL------------------------------------*/                                
            
/*------------------------------PrivateChat SIGNAL------------------------------------*/   
        else if (temp == ProtocolCS.commandCode.PrivateChat.ordinal()){
            Split();
            sP.cG.AddTextToBox(curUser, desUser, Message);
            
            
            DataControl dataControl = new DataControl();
            
            try {
                if (dataControl.CheckExistXMLFile(String.valueOf(curUser).trim()) == 0){
                dataControl.curUsr = sP.GetUserName();
                dataControl.desUsr = String.valueOf(String.valueOf(curUser).trim());
                dataControl.numberOfElement = 0;
                dataControl.Message = "1st created";
                dataControl.CreateXMLFile(String.valueOf(String.valueOf(curUser).trim()));
                }
            } catch (Exception e) {
            }
            
            if (String.valueOf(desUser).contains(sP.GetUserName())){
                //txtContent.append(String.valueOf(g.client.GetName()) + ": " + String.valueOf(g.client.GetMessage()) + "\n");

                // Add to local databases
                System.out.println(String.valueOf(curUser).trim());
                System.out.println(String.valueOf(desUser).trim());
                dataControl.curUsr = String.valueOf(curUser).trim();
                dataControl.desUsr = String.valueOf(desUser).trim();
                dataControl.numberOfElement = dataControl.CountXMLElement(String.valueOf(curUser).trim());
                //System.out.println(String.valueOf(g.client.GetName()));
                dataControl.Message = String.valueOf(Message).trim();
                dataControl.AppendXMLFile(String.valueOf(curUser).trim());
                //g.client.ClearData();
            }
            ClearData();
            return RECVPRV;
        }
/*------------------------------RoomChat SIGNAL------------------------------------*/  
        else if (temp == ProtocolCS.commandCode.RoomChat.ordinal()){
            Split();
            int id = GetSize();
            sP.AddMessInRoomGUI(id, new String(curUser).trim().replaceAll(" ", ""), new String(Message).trim().replaceAll(" ", ""));
            ClearData();
            return ProtocolCS.commandCode.RoomChat.ordinal();
        }
/*------------------------------END PrivateChat SIGNAL------------------------------------*/           

/*------------------------------update if 1 user online SIGNAL------------------------------------*/           
        else if (temp == ONLINEUSEROK){
            Split();
                System.out.println("da vao ONLINE USER");
            int result;
            result = sP.UpdateOnlineUser(curUser,1);
            if (result == 1)
                sP.size = sP.size + 1;
            //System.err.println("added roi");
            ClearData();
            sP.RealizeOnlineList();
            sP.cG.Reload(sP.sizeFriend);
            return ONLINEUSERADDED;
        }
/*------------------------------END update if 1 user online SIGNAL------------------------------------*/ 
        
        /* reconnect successfully*/
        else if (temp == 998){
            return (int)998;
        }
/*------------------------------LIST ONLINE BEGIN FRIST------------------------------------*/              
        
        else if (temp == LISTUSEROK){
            System.err.println("RECEIVED LIST USER DATA OK");
            sP.size = GetSize();
                System.out.println("Size khi LIST OK" + sP.size);
            for (int i = 0; i < sP.size; i++){
                char[] usrOnline = new char[30];
                for (int j = 4; j < 36; j++){
                    if (recvData[i * 36 + j + 8] == '\0')
                        break;
                    usrOnline[j-4] = recvData[i * 36 + j + 8];
                }
                sP.UpdateOnlineUser(usrOnline,1);
            }
            sP.cG.PerformRefreshClick();
            return 0;
        }
/*------------------------------END LIST ONLINE BEGIN FRIST------------------------------------*/
        
/*------------------------------BEGIN REFRESH------------------------------------*/              
        
        else if (temp == 22){// refresh btn
            System.err.println("Received list on Server by cmd 22");
            //max 29 user
            sP.sizeFriend=GetSize();
                System.err.println("Size friend :"+ sP.size);
            for (int i = 0; i < sP.sizeFriend; i++){
                char[] usrOnline = new char[30];
                Arrays.fill(usrOnline, '\0');
                for (int j = 4; j < 36; j++){
                    if (recvData[i * 36 + j + 8] == '\0')
                        break;
                    usrOnline[j-4] = recvData[i * 36 + j + 8];
                }
                System.out.println(String.valueOf(usrOnline));
                sP.UpdateFriendList(usrOnline,0);
                //System.out.println("da add" + String.valueOf(usrOnline).trim() + " 0");
            }
                try {
                    sP.RealizeOnlineList();
                } catch (Exception e) {
                }
            
            sP.cG.Reload(sP.sizeFriend);
            ClearData();
            return 0;
        }
/*------------------------------END REFRESH------------------------------------*/         
        
        
/*------------------------------CREATE NEW ROOM BY OWNER------------------------------------*/                        
        else if (temp == ADDROOMOK){
            System.err.println("CREATE ROOM SUCCESS");
            int id = GetSize();
            sP.UpdateRoomList(id, sP.nameRoomTemp.toCharArray(), sP.passRoomTemp.toCharArray());
            sP.cG.ReloadRoom();
            ClearData();
            return ADDROOMOK;
        }

/*------------------------------ROOM GUI FEATURES------------------------------------*/   
        /*-----------ADD BY OWNER--------*/
        else if (temp == INVITEROOMOK){
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "INVITED SUCCESSFULLY ", "OWNER COMMAND", mcServer);
            System.err.println("INVITE SUCCESS");
            
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
            int IDRoom = GetSize();
            char[] owner = new char[30];
            Arrays.fill(owner, '\0');
            for (int i = 0; i < 30; i++){
                owner[i] = recvData[8 + i];
            }
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOU HAVE BEEN INVITED FROM YOUR FRIEND INTO ROOM ", "HELLO GUYS", mcServer);
            ClearData();
            String nameRoom=sP.GetNameROOM(IDRoom);
            sP.InitRoomGUI(nameRoom,IDRoom);
            sP.OpenRoomGUI(IDRoom);
            sP.AddToListInRoomGUI(IDRoom, new String(owner).trim().replaceAll(" ", ""));
            sP.AddToListInRoomGUI(IDRoom, sP.GetUserName());
            return 0;
        }
        /*-----------KICK BY OWNER--------*/
        else if (temp == KICKROOMOK){
                 
                try {
                    //rG.ReloadListFriendOnRoom(0);   
                } catch (Exception e) {
                }
            ClearData();
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, " KICK SUCCESS ", "OWNER COMMAND", mcServer);
            System.err.println("KICK SUCCESS");
            return 0;
        }
        
        else if (temp == KICKROOMFAIL){
            int mcServer = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog (null, "KICK FAIL", "FAIL", mcServer);
            ClearData();
            System.err.println("INVITE FAIL");
            return 0;
        }
        /*-----------YOU HAVE BEEN KICKED BY OWNER--------*/
        else if (temp == BEKICKROOM){
            //rG.ReloadListFriendOnRoom(0);
            int id = GetSize();
            sP.TerminateRoomGUI(id);
            ClearData();
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "YOU HAVE BEEN KICKED FROM YOUR FRIEND INTO ROOM ", "HEY GUYS", mcServer);
            System.err.println("YOU HAVE BEEN KICKED BY OWNER ");
            return 0;
        }
        /*LOST DATA DUE TO NO FUNCTION OR CMD TO NOTIFY OTHER ,THIS KICKED'S MAN IN ROOM */
        
        /**/
        
/*------------------------------END ROOM GUI FEATURE------------------------------------*/                                
        
/*------------------------------add new friend appect------------------------------------*/                        
        else if (temp == ADDFRIENDOK){
            Split();// desUser
            char[] usrOnline = new char[30];
            usrOnline=desUser;
            sP.AddOnlineUser(usrOnline,0);
            System.err.println("ADD FRIEND SUCCESS");
            int mcServer = JOptionPane.INFORMATION_MESSAGE;
            JOptionPane.showMessageDialog (null, "ADD FRIEND SUCCESS", "Success", mcServer);
            sP.cG.PerformRefreshClick();
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
            sP.UpdateRoomList(id, sP.nameRoomTemp.toCharArray(), sP.passRoomTemp.toCharArray());
            sP.cG.ReloadRoom();
            return ROOMCREATE;
        }
/*------------------------------------------------------------------*/     
/*--------------------------------LIST ROOM BEGIN----------------------------------*/                                
        else if (temp == LISTROOMOK){
 
            
            System.err.println("RECEIVED DATA ROOM");

            sP.sizeRoom=GetSize();
            ClearData();
            
            // Get data of Room
            for (int i = 0; i < sP.sizeRoom; i++){
                int id;
                char[] nameRoom = new char[30];
                char[] pass = new char[30];
                Arrays.fill(nameRoom, '\0');
                Arrays.fill(pass, '\0');
                
                id = (int) ((recvData[8 + 197 * i]) 
                        + (recvData[9 + 197 * i] << 8) 
                        + (recvData[10+ 197 * i] << 16) 
                        + (recvData[11+ 197 * i] << 24));
                
                for (int j = 0; j < 30; j++){
                    if (recvData[j + 16 + i * 197] == '\0')
                        break;
                    nameRoom[j] = recvData[j + 16 + i * 197];
                }
                
                for (int j = 0; j < 30; j++){
                    if (recvData[j + 46 + i * 197] == '\0')
                        break;
                    pass[j] = recvData[j + 46 + i * 197];
                }
                
                sP.UpdateRoomList(id, nameRoom, pass);
            }

            
            sP.cG.ReloadRoom();
            
            /* reload list friend after collect full data */
            sP.cG.PerformRefreshClick();
            
            /* reload list room  */

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
            sP.UpdateOnlineUser(usrOffline,0);
            sP.size = sP.size-1;
            ClearData();
                try {
                    sP.RealizeOnlineList();
                } catch (Exception e) {
                }
            sP.cG.Reload(sP.sizeFriend);
            return OUTDONE;
        }
/*--------------------------------CMD 2000---------------------------------*/                                
        else if (temp == 2000){
            System.exit(0);
            return 0;
        }
            
//        if (recvData.contains("CLADDOK"))
//        {
//            return ADDOK;
//        }
//        if (recvData.contains("CLPRVOK"))
//        {
//            return PRVOK;
//        }
//        if (recvData.contains("CLSIGNOUTOK"))
//        {
//            return SIGNOUTOK;
//        }
//        if (recvData.contains("CLSIGNINOK"))
//        {
//            return SIGNINOK;
//        }
//        if (recvData.contains("CLRDELOK"))
//        {
//            return RDELOK;
//	}
//        if (recvData.contains("CLDELOK"))
//        {
//            return DELOK;
//        }
//        if (recvData.contains("CLROOMOK"))
//        {
//            return ROOMOK;
//        }
//        if (recvData.contains("CLRADDOK"))
//        {
//            return RADDOK;
//        }
//        if (recvData.contains("CLADDROOMOK"))
//        {
//            return ADDROOMOK;
//        }
//        if (recvData.contains("CLRECVPRV"))
//        {
//            Split();
//            return RECVPRV;
//        }
//        if (recvData.contains("CLRECVROOM"))
//        {
//            Split();
//            return RECVROOM;
//        }
        return temp;
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
        //sP.size = Size;
        return Size;
    }
    /*
    Function name: GetIDUser()
    Description: Get ID User
    Argument: None
    Return: Int
    Note:
    */
    public int GetIDUser(){
        int IDUser = (int)((recvData[1020]) | (recvData[1021] << 8) | (recvData[1022] << 16) | (recvData[1023] << 24));
        System.out.println(IDUser);
        sP.IDUser = IDUser;
        return (int)((recvData[1020]) | (recvData[1021] << 8) | (recvData[1022] << 16) | (recvData[1023] << 24));
    }
    
    /*
    Function name: SplitName()
    Description: Get Name from Receive message
    Argument: None
    Return: Int
    Note:
    */
    private int Split()
    {
//        String temp = recvData;
//        //Name = temp.substring(temp.indexOf("*"), temp.indexOf("*",temp.indexOf("*") + 1));
//        //Message = temp.substring(temp.indexOf("*",temp.indexOf("*") + 1), temp.length() - 1);
//        Name = temp.substring(temp.indexOf("*") + 1, temp.lastIndexOf("*"));
//        Message = temp.substring(temp.lastIndexOf("*") + 1, temp.length());
//        if ((Name != null) && (Message != null))
//        {
//            //recvData = "";
//            return OK;
//        }
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
    /*
    Function name: GetName()
    Description: Get Name
    Argument: None
    Return: char[]
    Note:
    */
    public char[] GetName()
    {
        
        return curUser;
    }
    /*
    Function name: GetMessage()
    Description: Get Message
    Argument: None
    Return: char[]
    Note:
    */
    public char[] GetMessage()
    {
        return Message;
    }
    /*
    Function name: GetDesName()
    Description: Get Destination Name
    Argument: None
    Return: char[]
    Note:
    */
    public char[] GetDesName(){
        return desUser;
    }
    /*
    Function name: SendMsgToRoom()
    Description: Send message to a specific room
    Argument: String roomname, string message
    Return: Int
    Note:
    */
    public int SendMsgToRoom(int idRoom, String msg)
    {
        blockToSend.command = ProtocolCS.commandCode.RoomChat.ordinal();
        blockToSend.IDRoom = idRoom;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = msg;
        return Send(blockToSend.GetText()); 
    }
     /*
    Function name: AddFriendToRoom()
    Description: Add new user to a specific room
    Argument: String roomname, string user
    Return: Int
    Note:
    */
    public int AddFriendToRoom(int roomID, String desusr)
    {
        
        blockToSend.command = ProtocolCS.commandCode.InviteToRoom.ordinal();
        blockToSend.IDRoom = roomID;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = desusr;
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        
        return Send(blockToSend.GetText());
        
        
    }
      /*
    Function name: AddNewRoom()
    Description: Add new room
    Argument: String roomname
    Return: Int
    Note:
    */
    public int AddNewRoom(String nameROOM, String passROOM)
    {
        blockToSend.command = ProtocolCS.commandCode.CreateRoom.ordinal();
        blockToSend.IDRoom = 0;// not appect on SERVER
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = nameROOM;// ROOM NAME 
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = passROOM;
        blockToSend.message = "";
        //System.out.println(blockToSend.ownUsername.length());
        sP.passRoomTemp = passROOM;
        sP.nameRoomTemp = nameROOM;
        return Send(blockToSend.GetText());
    }
}
