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
    public static final int OK = 0;
    
    /*
    private variables
    */
    private static String SERVERIP = "localhost";
    private static int SERVERPORT = 9000;
    private static Global instance;
    
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
    
    public static synchronized Global getInstance(){
        if (instance == null){
            instance = new Global();
        }
        return instance;
    }
}