/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author ubuntu
 */
public class SavedPreference {
     /*
    Constant status of function return
    */
    public static final int OK = 1;
    
    /*
    private variables
    */
    private static String SERVERIP = "192.168.122.239";
    private static int SERVERPORT = 8888;
    private static SavedPreference instance;
    private static String userName;
    private static String pass;
    public int IDUser;
    
    public SavedPreference(){
        
    }
    
    public int SetServerIP(String IP){
        SERVERIP = IP;
        return OK;
    }
    
    public String GetServerIP(){
        return SERVERIP;
    }
    
    public int SetServerPORT(int Port){
        SERVERPORT = Port;
        return OK;
    }
    
    public int GetServerPORT(){
        return SERVERPORT;
    }
    
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
    
    public static synchronized SavedPreference getInstance(){
        if (instance == null){
            instance = new SavedPreference();
        }
        return instance;
    }
}