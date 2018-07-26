package chatclient;


import chatclient.Client;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ubuntu
 */
public class ClientCommand extends Client{


    public ClientCommand() {
        getInstance();
        
        sP.InitMainGUI();
        sP.InitClientGUI();
        InitSocket();
        InitVar();
        Init();
        
    }
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ClientCommand cC = new ClientCommand();
                cC.sP.OpenMainGUI();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {}
                cC.sP.HideClientGUI();
            }
        });
    }
}
