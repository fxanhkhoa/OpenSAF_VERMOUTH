/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.net.*;
import java.io.*;
import chatclient.Global;
import java.util.concurrent.TimeUnit;
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



    //private static String SERVERIP = "10.0.3.105";
    //private static int SERVERPORT = 8888;

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
    public final int REPASSOK = 110;
    /*109 dang xuat*/
    public final int OUTOK = 109;
    /*----------------------------------*/
    /* add to show list user & list room */
    
    public final int LISTUSEROK = 102;
    public final int LISTROOMOK = 103;
    public final int ONLINEUSEROK = 66301; // In char is 997
    public final int ONLINEUSERADDED = 888; // Added online user
    public final int OUTDONE = 887; // Added online user
    
    /*----------------------------------*/
    /* add to show list user & list room */
    
    public final int REQUIREADD = 500;
    //public final int LISTROOMOK = 103;
    
    /*----------------------------------*/
    public final int RADDOK = 8;
    public final int RDELOK = 9;
    public final int ADDROOMOK = 107;
    public final int RECVPRV = 11;
    public final int RECVROOM = 12;
    
    /*
    Private Variables
    */
    private Socket socket;
    private BufferedWriter os;
    private BufferedReader is;
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
    private boolean ok = false;
    public SavedPreference sP;
    /*
    Constructor Function
    */
    public Client(){
        blockToSend = new ProtocolCS();
        sP = SavedPreference.getInstance();
        status = ConnectToServer();
        
        reconnect = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (status == false){
                            ConnectToServer();
                        }
                        else if (status == true){
                            os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        }
                        System.err.println("in");
                        if (GetCommandCode() != SIGNINOK){
                            SignIn(sP.GetUserName(), sP.GetPassword());
                        }
                        else if (GetCommandCode() == SIGNINOK){
                            System.err.println("Sign again ok");
                            reconnect.stop();
                        }
                        if (GetCommandCode() != 998){
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
                        Thread.sleep(1000);
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
                        len = is.read(recvData);
 
                        //System.out.println(len);
                        if (GetCommandCode() == LISTUSEROK ){
                            System.out.println("list vao " + len);
                            //Split();
//                            curUser[0] = recvData[12];
//                            curUser[1] = recvData[13];
//                            curUser[2] = recvData[14];
                        }

                        if (len == 1024){
//                            recvData.trim();
                            //recvData.replace("null", "");
                            isDataReceived = 1;
//                            //Send(recvData);
////                            //if (recvData.contains("CLSIGNOK")) status = false;
//                            //System.out.println(len);
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
                            is.reset();
 
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
                            socket.close();
                            os.close();
                            is.close();
                            isDataReceived = 0;
                           //readMessage.stop();
                           //ConnectToServer();
                           if (!reconnect.isAlive())
                           {
                               reconnect.start();
                               System.out.println("Reconnect successfully");
                           }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        readMessage.start();
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
       ConnectToServer();
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
            this.socket = new Socket(sP.GetServerIP(),sP.GetServerPORT());
            os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            status=true;
            System.out.println("Connect successful");
        }
        catch (IOException e)
        {
            status=false;
            System.err.println("Error: " + e.getMessage());
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
        blockToSend.command = ProtocolCS.commandCode.RePass.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = passU;
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
            
            os.write(str);
 
            os.flush();
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
        
        return 1;
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
        blockToSend.command = ProtocolCS.commandCode.InviteToRoom.ordinal();
        blockToSend.IDRoom = roomID;
        blockToSend.ownUsername = blockToSend.username;
        blockToSend.desUsername = desusr;
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = roomPass;
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
    Note:
    */
    public int GetCommandCode()
    {
//        if ((len != 0) && (len != 1020))
//            System.err.println(len);
        int temp = RebuildCmd(recvData);
        if ((temp != 0) && (temp != 109) && (temp != 65535)&& (temp != 65532) ){
            System.out.println(temp);
            System.out.println((int)recvData[0]);
            System.out.println((int)recvData[1]);
            System.out.println((int)recvData[2]);
            System.out.println((int)recvData[3]);
        }
        //Split();
        if (temp == SIGNINOK){
            return SIGNINOK;
        }
        else if (temp == ADDROOMOK){
            return ADDROOMOK;
        }
        
        else if (temp == ProtocolCS.commandCode.PrivateChat.ordinal()){
            Split();
            return RECVPRV;
        }
        /*update user online*/
        else if (temp == ONLINEUSEROK){
            Split();
            int result;
            result = sP.AddOnlineUser(curUser);
            if (result == 1)
                sP.size = sP.size + 1;
            //System.err.println("added roi");
            ClearData();
            recvData[0] = 120;
            recvData[1] = 3;
            return ONLINEUSERADDED;
        }
        /* reconnect successfully*/
        else if (temp == 998){
            return (int)998;
        }
        /* one man online*/
        else if (temp == LISTUSEROK){
            System.err.println("LIST OK");
            sP.size=GetSize();
            for (int i = 0; i < sP.size; i++){
                char[] usrOnline = new char[30];
                for (int j = 4; j < 36; j++){
                    if (recvData[i * 36 + j + 8] == '\0')
                        break;
                    usrOnline[j-4] = recvData[i * 36 + j + 8];
                }
                sP.AddOnlineUser(usrOnline);
            }
            return LISTUSEROK;
        }
        else if (temp == LISTROOMOK){
            System.err.println("ROOM OK");
            //Split();
            return LISTROOMOK;
        }
        //109
        else if (temp == OUTOK){
            
            /*find him*/
            
                char[] usrOffline = new char[30];
                for (int j = 0; j < 30; j++){
                    if (recvData[j+ 8] == '\0')
                        break;
                    usrOffline[j] = recvData[j+ 8];
                }
                sP.AddOfflineUser(usrOffline);
                sP.size = sP.size-1;
                ClearData();
            //Split();
                recvData[0] = 119;
                recvData[1] = 3;
            return OUTDONE;
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
        int Size = (int)((recvData[4]) | (recvData[5] << 8) | (recvData[6] << 16) | (recvData[7] << 24));
        System.out.println("day la size: " + Size);
        //sP.size = Size;
        return (int)((recvData[4]) | (recvData[5] << 8) | (recvData[6] << 16) | (recvData[7] << 24));
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
    public int SendMsgToRoom(int idRoom,String desusr, String msg)
    {
        blockToSend.command = ProtocolCS.commandCode.RoomChat.ordinal();
        blockToSend.IDRoom = idRoom;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = desusr;
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
    public int AddNewRoom(int roomID)
    {
        blockToSend.command = ProtocolCS.commandCode.CreateRoom.ordinal();
        blockToSend.IDRoom = roomID;
        blockToSend.ownUsername = sP.GetUserName();
        blockToSend.desUsername = "";
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        //System.out.println(blockToSend.ownUsername.length());
        return Send(blockToSend.GetText());
    }
}
