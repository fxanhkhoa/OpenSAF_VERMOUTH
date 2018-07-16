/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author shawry
 */
public class Global {
    
    /*
    Constant status of function return
    */
    public static final int OK = 1;
    
    /*
    private variables
    */
    //private static String SERVERIP = "localhost";
    //private static int SERVERPORT = 9000;
    private static Global instance;
    private static String userName;
    private static String pass;
    private static int IDUser;
    /*
    public variables
    */
    public static Client client;
    
    /*
    private function
    */
    
    
    /*
    public funtion
    */
    public Global(){
        client = new Client();
    }
    
    //public int SetServerIP(String IP){
     //   SERVERIP = IP;
    //    return OK;
    //}
    
    //public String GetServerIP(){
    //    return SERVERIP;
    //}
    
    //public int SetServerPORT(int Port){
     //   SERVERPORT = Port;
     //   return OK;
    //}
    
    //public int GetServerPORT(){
    //    return SERVERPORT;
    //}
    //
    public void SetUserName(String usr){
        userName = usr;
    }
    
    public String GetUserName(){
        return userName;
    }
    
    public void SetPass(String password){
        pass = password;
    }
    
    public String GetPassword(){
        return pass;
    }
    
    public int GetIDUser(){
        return IDUser;
    }
    
    public void SetIDUser(int value){
        IDUser = value;
    }
    
    public static synchronized Global getInstance(){
        if (instance == null){
            instance = new Global();
        }
        return instance;
    }
    
    public void Release(){
        client = null;
    }
    
    public void CreateNew(){
        client = new Client();
    }
}