/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.net.*;
import java.io.*;
import chatclient.Global;
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
    private static String SERVERIP = "localhost";
    private static int SERVERPORT = 1234;
    public final int ADDOK = 2;
    public final int DELOK = 3;
    public final int PRVOK = 4;
    public final int ROOMOK = 5;
    public final int SIGNOUTOK = 6;
    public final int SIGNINOK = 7;
    public final int RADDOK = 8;
    public final int RDELOK = 9;
    public final int ADDROOMOK = 10;
    public final int RECVPRV = 11;
    public final int RECVROOM = 12;
    
    /*
    Private Variables
    */
    private Socket socket;
    private BufferedWriter os;
    private BufferedReader is;
    private String recvData;
    private boolean status;
    private int isDataReceived;
    private static Thread readMessage;
    private String Name;
    private String Message;
 
    /*
    Constructor Function
    */
    public Client(){
        status = ConnectToServer();
        readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        //recvData = "";
                        recvData = is.readLine();
                        if (recvData != null){
                            recvData.trim();
                            recvData.replace("null", "");
                            isDataReceived = 1;
                            //Send(recvData);
                            //if (recvData.contains("CLSIGNOK")) status = false;
                        }
                        if (recvData == null){
                            System.err.println("Server error");
                            readMessage.stop();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        System.err.println("aa");
    }
    
    /*
 
    *Function : MainGUI
    *Description: Main Interfaces. Login to Chat Server or Sign Up Account.
    *Argument: Nope!
    *Return: Nope!
    Note: First Interface 

    */
    private boolean ConnectToServer()
    {
        try
        {
            this.socket = new Socket(SERVERIP,SERVERPORT);
            os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            status=true;
        }
        catch (IOException e)
        {
            status=false;
            System.err.println("Can't connect to server" + e.getMessage());
            return false;
            
        }
        try
        {
            os.write("Hello");
            os.newLine();
            os.flush();
            
            
        }
        catch (IOException e)
        {
            System.err.println("Can't send hello server" + e.getMessage());
        }
        return true;
    }
    public boolean GetStatus()
    {
        return status;
    }
    public void ClearData()
    {
        recvData = "";
    }
    public int SignIn(String usr, String pass)
    {
        return Send("SVSIGNIN*" + usr + "*" + pass);
    }
    public void SignUp()
    {
        
    }
    public int SignOut()
    {
        return Send("SVSIGNOUT");
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
    Return: Acceptable
    Note:
    */
    private int Send(String str)
    {
        try{
            os.write(str);
            os.newLine();
            os.flush();
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
    public int SendPrivateMessage(String usr, String message)
    {
        return Send("SVPRV*" + usr + "*" + message);
    }
    
    /*
    Function name: Receive_data()
    Description: get current data in client
    Argument: String 
    Return: String
    Note:
    */
    public String ReceiveData()
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
    public int IsDataReceived()
    {   
        int temp = isDataReceived;
        isDataReceived = 0;
        return temp;
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
        return Send("SVADD*" + usr);
    }
    
    /*
    Function name: RemoveFriendFromRoom(String usr)
    Description: Send Remove friend command to server
    Argument: String username
    Return: Int
    Note:
    */
    public int RemoveFriendFromRoom(String usr)
    {
        return Send("SVRDEL*" + usr);
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
        return Send("SVDEL*"+username);
    }
 
    public void NewGroupDM()
    {
        
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
        if (recvData.contains("CLADDOK"))
        {
            return ADDOK;
        }
        if (recvData.contains("CLPRVOK"))
        {
            return PRVOK;
        }
        if (recvData.contains("CLSIGNOUTOK"))
        {
            return SIGNOUTOK;
        }
        if (recvData.contains("CLSIGNINOK"))
        {
            return SIGNINOK;
        }
        if (recvData.contains("CLRDELOK"))
        {
            return RDELOK;
	}
        if (recvData.contains("DELOK"))
        {
            return DELOK;
        }
        if (recvData.contains("ROOMOK"))
        {
            return ROOMOK;
        }
        if (recvData.contains("RADDOK"))
        {
            return RADDOK;
        }
        if (recvData.contains("ADDROOMOK"))
        {
            return ADDROOMOK;
        }
        if (recvData.contains("CLRECVPRV"))
        {
            Split();
            return RECVPRV;
        }
        if (recvData.contains("CLRECVROOM"))
        {
            Split();
            return RECVROOM;
        }
        return OK;
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
        String temp = recvData;
        //Name = temp.substring(temp.indexOf("*"), temp.indexOf("*",temp.indexOf("*") + 1));
        //Message = temp.substring(temp.indexOf("*",temp.indexOf("*") + 1), temp.length() - 1);
        Name = temp.substring(temp.indexOf("*") + 1, temp.lastIndexOf("*"));
        Message = temp.substring(temp.lastIndexOf("*") + 1, temp.length());
        if ((Name != null) && (Message != null))
        {
            //recvData = "";
            return OK;
        }
        return ERROR;
    }
    
    public String GetName()
    {
        return Name;
    }
    
    public String GetMessage()
    {
        return Message;
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
        return Send("SVROOM*"+room+"*"+msg);    
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
        return Send("SVRADD*"+room+"*"+usr);    
    }
      /*
    Function name: AddNewRoom()
    Description: Add new room
    Argument: String roomname
    Return: Int
    Note:
    */
    public int AddNewRoom(String room)
    {
        return Send("SVRADDROOM*"+room);    
    }
}
