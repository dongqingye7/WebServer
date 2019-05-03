//Name: Dongqing Ye
//Student ID: 1001403301
//Reference:https://medium.com/@ssaurel/create-a-simple-http-web-server-in-java-3fc12b29d5fd

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


//To parse the HTTP Request for a requested resource
//and compose the HTTP response message
public class ClientHandler implements Runnable { 
    
    final Socket socket; 

    // Constructor 
    //create ClientHandler with socket connection
    public ClientHandler(Socket socket){ 
        this.socket = socket; 
    } 
  
    @Override
    public void run(){ 
        BufferedReader inFromClient=null;
        PrintWriter out=null;
        BufferedOutputStream outToClient=null;
        String fileRequested=null;
        OutputStream output;
        try {
            //Input stream that reads character from client
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Output stream that send header information to client
            out = new PrintWriter(socket.getOutputStream());
            //Output stream that send binary data to client
            outToClient = new BufferedOutputStream(socket.getOutputStream());
            //output stream 
            output = socket.getOutputStream();
            // get first line of HTTP request message
            String input = inFromClient.readLine();
            // parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);
            // get the HTTP method of the client
            String method = parse.nextToken().toUpperCase(); 
            // get file requested
            fileRequested = parse.nextToken().toLowerCase();
            //check if the header method is GET
            if(method.equals("GET")){
                //check if the requst file is file.html
                    if(fileRequested.equals("/file.html")){
                        //send HTTP response message 301 file moved permanently
                        fileMove(out,output,fileRequested);
                    }else{
                        File root = new File(".");
                        File file = new File(root, fileRequested);
                        int fileLength = (int) file.length();
                        String content = "text/html";
                        if(file.isFile()){
                            //read file data in bytes 
                            byte[] fileData = readFileData(file, fileLength);
                            //send HTTP header
                            out.println("HTTP/1.0 200 OK");
                            out.println("Server: HTTP Server/0.1");
                            out.println("Date: " + new Date());
                            out.println("Content-type: " + content);
                            out.println("Content-length: " + fileLength);
                            out.println(); 
                            out.flush(); // flush character output stream buffer
			    //send requested file to client
                            outToClient.write(fileData, 0, fileLength);
                            outToClient.flush();
                        }else{
                            //requsted file is not found
                            //send 404 HTTP response message
                            fileNotFound(out, output, fileRequested);
                        }      
                    }
            }else{
                System.out.println("501 Not Implemented : " + method + " method.");
            }
                
        } catch (FileNotFoundException fnfe) {
            try {
		fileNotFound(out, outToClient, fileRequested);
            } catch (IOException ioe) {
		System.err.println("Error with file not found exception : " + ioe.getMessage());
            }
			
	} catch (IOException ioe) {
		System.err.println("Server error : " + ioe);
	} finally {
	    try {
		socket.close(); //close socket connection
	    } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            } 
            System.out.println("Connection closed.\n");		
        }
    } 
    //read file data in bytes 
    private byte[] readFileData(File file, int fileLength) throws IOException {
	FileInputStream fileIn = null;
	byte[] fileData = new byte[fileLength];
		
	try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
	} finally {
            if (fileIn != null) 
                fileIn.close();
	}
		
	return fileData;
    }
    //requested file is not found
private void fileNotFound(PrintWriter out, OutputStream output, String fileRequested) throws IOException {
        File root = new File(".");
        File file = new File(root, "404.html");
	int fileLength = (int) file.length();
        String content = "text/html";
	byte[] fileData = readFileData(file, fileLength);
        //send 404 HTTP response message 
	out.println("HTTP/1.0 404 File Not Found");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
	out.println("Date: " + new Date());
        out.println("Content-type: " + content);
	out.println("Content-length: " + fileLength);
        out.println();
	out.flush(); // flush character output stream buffer
	//send client 404 html file	
        output.write(fileData, 0, fileLength);
	output.flush();
        System.out.println("File " + fileRequested + " not found");
		
}
//requested file is moved permanently
private void fileMove(PrintWriter out, OutputStream output, String fileRequested) throws IOException {
        File root = new File(".");
        File file = new File(root, "301.html");
	int fileLength = (int) file.length();
        String content = "text/html";
	byte[] fileData = readFileData(file, fileLength);
        //send 301 HTTP response message
	out.println("HTTP/1.0 301 Moved Permanently\r");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
	out.println("Date: " + new Date());
        out.println("Content-type: " + content);
	out.println("Content-length: " + fileLength);
        out.println(); 
	out.flush(); // flush character output stream buffer
        //send client 301 html page
        output.write(fileData, 0, fileLength);
	output.flush();
        System.out.println("File "+fileRequested+" Moved Permanently\n"+"Location: http://localhost:6789/file.html\n");		
}
	
}





