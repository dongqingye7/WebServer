//Name: Dongqing Ye
//Student ID: 1001403301
//Reference:https://medium.com/@ssaurel/create-a-simple-http-web-server-in-java-3fc12b29d5fd

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args)throws Exception {
        //Open Server socket
        ServerSocket welcomeSocket=new ServerSocket(6789);
        System.out.println("Listening");
        //Request handler loop
        while(true){
            try{
                //Wait for request
                Socket connectionSocket=welcomeSocket.accept(); 
                System.out.println("Connected");
                // Each Client Connection is managed in a dedicated Thread
                ClientHandler connect=new ClientHandler(connectionSocket);
                //Creates a new thread to process each HTTP request
                Thread t = new Thread(connect); 
                // Invoking the start() method 
                t.start(); 
            }catch(IOException e){
                System.err.println("Server Connection error : " + e.getMessage());
                
            }
           
            
        }
        
    }
    
}
