/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

/**
 *
 * @author shawry
 */
public class Servers {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        ThreadPooledServer server = new ThreadPooledServer(9000);
        new Thread(server).start();

        try 
        {
            Thread.sleep(60 * 1000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
    
}
