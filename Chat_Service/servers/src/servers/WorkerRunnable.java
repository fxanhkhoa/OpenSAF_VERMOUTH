package servers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class WorkerRunnable implements Runnable
{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) 
    {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() 
    {
        try 
        {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //InputStream input  = clientSocket.getInputStream();
            //OutputStream output = clientSocket.getOutputStream();
            //long time = System.currentTimeMillis();
            //output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
            //    this.serverText + " - " +
            //    time +
            //    ""));
            //output.write(input.readLine());
            System.out.println(input.readLine());
            output.close();
            input.close();
            
        } 
        catch (IOException e) 
        {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}