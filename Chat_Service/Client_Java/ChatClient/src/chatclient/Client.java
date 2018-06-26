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
    private Socket socket;
    private BufferedWriter os;
    private BufferedReader is;
    private boolean status;
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
            Global g = Global.getInstance();
            this.socket = new Socket(g.GetServerIP(),g.GetServerPORT());
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
    public void Send()
    {
        
    }
    public void Receive()
    {
        
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
}
