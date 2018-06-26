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
    private static String SERVERIP = "localhost";
    private static int SERVERPORT = 1234;
    public final int SIGNOK = 2;
    
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
                            Send(recvData);
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
    public void SignIn()
    {
        
    }
    public void SignUp()
    {
        
    }
    public void SignOut()
    {
        
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
    public int Send(String str)
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
    public void AddFriend()
    {
        
    }
    public void RemoveFriend()
    {
        
    }
    public void AddFriendToDM()
    {
        
    }
    public void NewGroupDM()
    {
        
    }
    
    /*
    Function name: GetCommandCode()
    Description: get Command code
    Argument: None
    Return: INt
    Note:
    */
    public int GetCommandCode()
    {
        if (recvData.contains("CLSIGNOK"))
        {
            return SIGNOK;
        }
        
        return OK;
    }
}