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
    public static final int OK = 1;
    public static final int ERROR = -1;
    private static String SERVERIP = "192.168.122.239";
    private static int SERVERPORT = 8888;
    public final int ADDOK = 2;
    public final int DELOK = 3;
    public final int PRVOK = 4;
    public final int ROOMOK = 5;
    public final int SIGNOUTOK = 6;
    public final int SIGNINOK = 101;
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
    private SavedPreference sP;
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
                        else if (GetCommandCode() == 998){
                            System.err.println("gotcha");
                            reconnect.stop();
                        }
                        if (IsDataReceived() == 1){
                            System.err.println("thread stop");
                            reconnect.stop();
                        }
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
                        if (len == 1024){
//                            recvData.trim();
                            //recvData.replace("null", "");
                            isDataReceived = 1;
                            //Send(recvData);
//                            //if (recvData.contains("CLSIGNOK")) status = false;
                            System.out.println(len);
                            System.out.println(recvData);
                            is.reset();
                            if (GetCommandCode() == 998){
                                System.err.println("998 OK");
                                reconnect.stop();
                            }
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
                            reconnect.start();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        readMessage.start();
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
            System.err.println("Can't connect to server" + e.getMessage());
            return false;
            
        }    
        return true;
    }
    public boolean GetStatus()
    {
        return status;
    }
    public void ClearData()
    {
        recvData[0] = recvData[1] = recvData[2] = recvData[3] = 0;
        //recvData = "";
    }
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
    public int SignOut()
    {
        blockToSend.command = ProtocolCS.commandCode.SignOut.ordinal();
        blockToSend.IDRoom = 0;
        blockToSend.ownUsername = blockToSend.username;
        blockToSend.desUsername = "";
        blockToSend.ownPassword = blockToSend.password;
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
    Function name: Send(string str)
    Description: Send string to server
    Argument: String 
    Return: int error code
    Note:
    */
    private int Send(char[] str)
    {
        try{
            //String temp = str.toString();
            
            os.write(str);
            //os.newLine();
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
    Argument: String 
    Return: String
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
    
    private int RebuildCmd(char[] input){
        int temp = (int)((input[0]) | (input[1] << 8) | (input[2] << 16) | (input[3] << 24));
          
          System.out.flush();
//        System.out.print((int)input[0]);
//        System.out.print((int)input[1]);
//        System.out.print((int)input[2]);
//        System.out.println((int)input[3]);
        return ((input[0]) | (input[1] << 8) | (input[2] << 16) | (input[3] << 24));
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
        int temp = RebuildCmd(recvData);
        if (temp != 0){
            System.out.println(temp);
            System.out.println((int)recvData[1020]);
            System.out.println((int)recvData[1021]);
            System.out.println((int)recvData[1022]);
            System.out.println((int)recvData[1023]);
        }
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
        else if (temp == 998){
            return (int)998;
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
        char[] tempData = new char[1024];
        tempData = recvData;
        //Get current User
        for (int i = 0; i < 30; i++){
            curUser[i] = tempData[8 + i];
        }
        //System.out.println(curUser);
        //Get destination User
        for (int i = 0; i < 30; i++){
            desUser[i] = tempData[38 + i];
        }
        //System.out.println(desUser);
        //Get message
        for (int i = 0; i < 896; i++){
            Message[i] = tempData[128 + i];
        }
        //System.out.println(Message);
        return OK;
    }
    
    public char[] GetName()
    {
        return curUser;
    }
    
    public char[] GetMessage()
    {
        return Message;
    }
    
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
    public int SendMsgToRoom(String room, String msg)
    {
        return 1;    
    }
     /*
    Function name: AddFriendToRoom()
    Description: Add new user to a specific room
    Argument: String roomname, string user
    Return: Int
    Note:
    */
    public int AddFriendToRoom(String room, String usr)
    {
        return 1;    
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
        blockToSend.ownUsername = blockToSend.username;
        blockToSend.desUsername = "";
        blockToSend.ownPassword = "";
        blockToSend.roomPassword = "";
        blockToSend.message = "";
        //System.out.println(blockToSend.ownUsername.length());
        return Send(blockToSend.GetText());
    }
}
