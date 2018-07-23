/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.util.Vector;

/**
 *
 * @author ubuntu
 */
public class SavedPreference {
     /*
    Constant status of function return
    */
    public static final int OK = 1;
    /**/
    public int max=0;
    public int size;
    
    /*
    private variables
    */
    private static String SERVERIP ="192.168.122.239";
    private static int SERVERPORT = 8888;
    private static SavedPreference instance;
    private static String userName;
    private static String pass;
    public int IDUser;
    public Vector<String> onlineUser = new Vector<String>();
    //public Vector<String> offlineUser = new Vector<String>();
    
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
    
    public int AddOnlineUser(char[] usr){
        String bufferString = new String(usr).trim().replaceAll(" ", "");
        System.out.print("moi add :");
        System.out.println(bufferString);
        //String temp = usr.toString().trim();
        if (!onlineUser.contains(bufferString)){
            onlineUser.add(bufferString);
            return 1;
        }
        return 0;
    }
    /*find the man go heaven*/
    public void AddOfflineUser(char[] usr){
        
        String bufferString = new String(usr).trim().replaceAll(" ", "");
        System.out.print("client sign out :");
        System.out.println(bufferString);
        //String temp = usr.toString().trim();
        //offlineUser.add(bufferString);
        
        onlineUser.remove(bufferString);
    }
    
    /*******************************/
    
    public static synchronized SavedPreference getInstance(){
        if (instance == null){
            instance = new SavedPreference();
        }
        return instance;
    }
}
