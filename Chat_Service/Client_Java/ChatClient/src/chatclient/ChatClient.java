/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 *
 * @author ubuntu
 */
public class ChatClient {
    
      JFrame myFrame = null;  

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//         Client cl = new Client();
            (new ChatClient()).test();    
//        if( cl.ConnectToServer() == true)
//            System.out.println("Connect successes");
        //while(true);
        
        
        
    }
    private void test() {    
        myFrame = new JFrame("JEditorPane Test");    
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        myFrame.setSize(400, 200);
        
        JEditorPane myPane = new JEditorPane();    
        myPane.setContentType("text/html");    
        myPane.setText("<span style=\"background-color: #1ddced; color: #fff; display: inline-block; padding: 3px 10px; font-weight: bold; border-radius: 5px;\">Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s </span> ");   
        
        myFrame.setContentPane(myPane);    
        myFrame.setVisible(true);    
    }    
    
}
