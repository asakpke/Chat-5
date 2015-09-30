import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
//import java.lang.Thread;

class Connection extends Thread {
  static String users[] = new String[0]; //User firstUser;
  static Socket sockets[] = new Socket[0];
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	int id;
	int userIndex = 0;

/*
	final String CMD[] = { "NewUser", "DelUser", "SendUser", "SendAll" };
	final int cmdNewUser = 0;
	final int cmdDelUser = 1;
	final int cmdSendUser = 2;
	final int cmdSendAll = 3;
*/

	public Connection (Socket aClientSocket, int clientCount) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			id = clientCount;
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}

	public void run() {
  	int v;
  	//start - make a space for new user in array
  	String data;
  	String userName = "userName";
  	boolean needSpace = true;
  	int spaceCount = 0;

  	for( v=0; v < users.length; v++ )
      if( users[v] == null  ) {
        spaceCount++;
        userIndex = v;
        needSpace = false;
        //break;
      }

    if( needSpace ) {
        String tempUsers[] = users;
        Socket tempSockets[] = sockets;

  	    users = new String[ users.length + 1 ];
  	    sockets = new Socket[ sockets.length + 1 ];

  	    for( v = 0; v < tempUsers.length; v++ ) {
  	      users[v] = tempUsers[v];
  	      sockets[v] = tempSockets[v];
        }
  	    userIndex = v;
    }
    else
      spaceCount--;

  	try {
    	userName = in.readUTF();
      out.writeUTF( Integer.toString( userIndex ) );
    	System.out.println( "new login user name = " + userName );
      out.writeUTF( Integer.toString( users.length - spaceCount ) );
    } catch( Exception e ) { System.out.println( "Line # 73, " + e ); }

    users[userIndex] = userName;
    sockets[userIndex] = clientSocket;

    for( v=0; v < users.length; v++ ) {
      try {
        if( users[v] != null  ) {
          out.writeUTF( Integer.toString( v ) );
          out.writeUTF( users[v] );
        }
      } catch( Exception e ) { System.out.println( "Line # 84, " + e ); }
    }

    send2All( "NewUser" + " " + userIndex + " " + userName );

  	//end  - make a space for new user in array

		while( true ) {			     // an echo server
		  try {
        data = in.readUTF();    // read a line of data from the stream
        StringTokenizer tokens = new StringTokenizer( data );
        String token;
        token = tokens.nextToken();
        if( token.equals( "bye" ) || token.equals( "BYE" ) ) {
          send2All( "DelUser" + " " + userIndex );
          users[ userIndex ] = null;
          sockets[ userIndex ] = null;
          System.out.println( "user gone named = " + userName );
          break;
        } // CMD = SendAll FromUserID Data
        else if( token.equals( "SendAll" ) ) {
          token = tokens.nextToken();
          while( tokens.hasMoreTokens() )
            token = token.toString() + " " + tokens.nextToken();

          send2All( "SendAll" + " " + userIndex + " " + token );
        }
        else if( token.equals( "SendTo" ) ) {
          int toUserID = Integer.parseInt( tokens.nextToken() );
          token = tokens.nextToken();
          while( tokens.hasMoreTokens() )
            token = token.toString() + " " + tokens.nextToken();
          sendTo( toUserID, token );
        }
		  }
      catch( EOFException e ) { System.out.println("EOF:"+e.getMessage()); }
      catch( IOException e ) {
        System.out.println("Server readline:"+e.getMessage());
        break;
      }
    }

    try {
		  clientSocket.close();
		}
    catch( Exception e ){ System.out.println( "Line 118, " + e.getMessage()); }
	}// public void run() {

  void send2All( String strCMD) {
  	DataOutputStream out2All;
    for( int v=0; v < sockets.length; v++ ) {
      try {
        if( sockets[ v ] != null ) {
          if( !sockets[v].isClosed() ) {
            out2All = new DataOutputStream( sockets[v].getOutputStream());
            out2All.writeUTF( strCMD );
          }
        }
      }
      catch( Exception eOut2All ) { System.out.println( "Line # 128, " + eOut2All ); }
    }
  }// void send2All() {

  void sendTo( int toUserID, String msg ) {
    DataOutputStream outTo;
    try {
      if( sockets[ toUserID ] != null ) {
        if( !sockets[ toUserID ].isClosed() ) {
          outTo = new DataOutputStream( sockets[ toUserID ].getOutputStream());
          outTo.writeUTF( "SendFrom" + " " + userIndex + " " + msg );
        }
      }
    }
    catch( Exception eOut2All ) { System.out.println( "Line # 128, " + eOut2All ); }
  }
}// class Connection extends Thread {