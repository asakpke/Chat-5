//import java.net.*;
//import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class TCPServer {
	public static void main (String args[]) {
		try {
			int serverPort = 7896; // the server port
      int clientCount = 1;
			ServerSocket listenSocket = new ServerSocket( serverPort );
      System.out.println( "Server started" );
			while( true ) {
				Socket clientSocket = listenSocket.accept();
				System.out.println( "clientSocket = " + clientSocket );
				Connection c = new Connection( clientSocket, clientCount++ );
			}
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
	}
}