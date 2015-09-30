import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import java.
import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class TCPClient extends JFrame implements Runnable, ActionListener {
//class TCPClient extends JFrame implements Runnable, ActionListener {
  Thread t;
  DataInputStream in;
  DataOutputStream out;
  JTextField txtData;
  Socket socket = null;
  static String userName = "userName";
  int userID = 0;
  String users[] = new String[ 0 ]; //User firstUser;
  SendTo sendTos[] = new SendTo[ 0 ];
  //List lst;
  JList lst;
  //lst
  JTextArea txtChat;
  //JLabel lblChat;

  TCPClient() {
    super( "Client site for user = " + userName );

    JPanel rightPanel = new JPanel( new BorderLayout() );
    lst = new JList();
    final TCPClient tempTCPClientThis = this;
    lst.addMouseListener( new MouseAdapter() {
      public void mouseClicked( MouseEvent me ) {
        int lstInd = lst.getSelectedIndex();
        if( sendTos[ lstInd ] == null ) {
          //sendTos[ lstInd ] = new SendTo( lstInd, users[ lstInd ], userName, out );
          //sendTos[ lstInd ] = new SendTo( lstInd, users[ lstInd ], userName, out, tempTCPClientThis);
          sendTos[ lstInd ] = new SendTo( lstInd, tempTCPClientThis);
          sendTos[ lstInd ].setSize( 400, 400 );
          sendTos[ lstInd ].show();
        }
        //lst.setSelectedIndex( -1 );
      }
    });
    //lst.add
    //lst = new List();

    rightPanel.add( new JLabel( "Online Users List" )
      , BorderLayout.PAGE_START );
    rightPanel.add( lst, BorderLayout.CENTER );
    getContentPane().add( rightPanel, BorderLayout.EAST );

    JScrollPane scrollPaneTxt;   // NEW
    txtChat = new JTextArea( "CHATTING ROOM FOR GENERAL CHAT" );
    txtChat.setEditable( false );
    txtChat.setBackground( Color.lightGray );
    scrollPaneTxt = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
           , JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    //scrollPane.setBounds(10, 10, 270, 200);
    scrollPaneTxt.add( txtChat );
    scrollPaneTxt.setViewportView( txtChat );

    //lblChat = new JLabel( userName + " connected" );

    //getContentPane().add( txtChat, BorderLayout.CENTER );
    getContentPane().add( scrollPaneTxt, BorderLayout.CENTER );

    //getContentPane().add( lblChat, BorderLayout.CENTER );
    //lblChat.setText( lblChat.getText() + "\n\rSo he is online" );
    //txtChat.setText( txtChat.getText() + "\nSo he is online" );

    JPanel bottomPanel= new JPanel( new FlowLayout() );//new GridLayout(1, 1));
    txtData = new JTextField( 20 );
    bottomPanel.add( txtData );

    JButton btnSend = new JButton("Send");
    btnSend.setActionCommand("btnSend");
    btnSend.addActionListener( this );
    bottomPanel.add( btnSend );

    JButton btnExit = new JButton("Exit");
    btnExit.setActionCommand("btnExit");
    btnExit.addActionListener( this );
    bottomPanel.add( btnExit );
    getContentPane().add( bottomPanel, BorderLayout.PAGE_END );
  }

	public static void main (String args[]) {
		// arguments supply message and hostname
		userName = JOptionPane.showInputDialog( "Enter your name" );
		TCPClient client = new TCPClient();
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.setSize( 400, 400 );
		client.show();
		client.connect( args[0] );
		//client.pack();
  }

  public void connect( String strServer ) {
    int v;
		try {
		  int serverPort = 7896;
			String msg;
			socket = new Socket( strServer, serverPort );
			in = new DataInputStream( socket.getInputStream());
			//inFromUser   = new BufferedReader( new InputStreamReader(System.in) );
			out =new DataOutputStream( socket.getOutputStream());
      String data;
      //write name
      //System.out.println( "\nEnter your name" );
      //userName = inFromUser.readLine();
      out.writeUTF(userName);      	// UTF is a string encoding see Sn. 4.4
      userID = Integer.parseInt( in.readUTF() );
      data = in.readUTF();
      int usersCount = Integer.parseInt( data );
      users = new String[ usersCount ];
      sendTos = new SendTo[ usersCount ];
      if( usersCount == 0 ) {
        System.out.println( "No other user online" );
      }
      else {
        int userID;
        //String userName;
        System.out.println( "Number of online users = " + usersCount );
        for( v=0; v < usersCount; v++ ) {
          // user id
          data = in.readUTF();
          userID = Integer.parseInt( data );
          System.out.print( "\t" + data) ;
          // user name
          data = in.readUTF();
			    System.out.println( "\t" + data) ;
			    if( userID >= users.length ) {
            String tempUsers[] = users;
            SendTo tempSendTos[] = sendTos;
  	        users = new String[ userID + 1 ];
  	        sendTos = new SendTo[ userID + 1 ];
  	        for( v = 0; v < tempUsers.length; v++ ) {
  	          users[v] = tempUsers[v];
  	          sendTos[v] = tempSendTos[v];
            }
          }
          users[ userID ] = data;
          sendTos[ userID ] = null;
        }
      }

      lst.setListData( users );

      displayUserList();

      t = new Thread( this );
      t.start();
		}
    catch( UnknownHostException e ){System.out.println("Socket:"+e.getMessage());}
    catch( EOFException e ){System.out.println("EOF:"+e.getMessage());}
    catch (IOException e){System.out.println("readline:"+e.getMessage());}
    //finally {if( s!=null ) try {socket.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
  }// public void connect( String strServer ) {

  public void run() {
    int v;
    int userID;
    String data;
    String msg;
    try {
      while( true ) {
        data = in.readUTF();
        StringTokenizer tokens = new StringTokenizer( data );
        String token;
        token = tokens.nextToken();
        //if( token.equals( CMD[cmdNewUser] ) ) {
        if( token.equals( "NewUser" ) ) { // CMD = NewUser UserID UserName
          System.out.println( "\nNEW USER COMES") ;
          token = tokens.nextToken();
          System.out.print( "\t" + token ) ; // id
          //s
          userID = Integer.parseInt( token );
          token = tokens.nextToken();
			    System.out.println( "\t" + token ) ;// name
          if( userID >= users.length ) {
            String tempUsers[] = users;
            SendTo tempSendTos[] = sendTos;
  	        users = new String[ userID + 1 ];
  	        sendTos = new SendTo[ userID + 1 ];
  	        for( v = 0; v < tempUsers.length; v++ ) {
  	          users[ v ] = tempUsers[ v ];
  	          sendTos[ v ] = tempSendTos[ v ];
            }
          }
          users[ userID ] = token;
          sendTos[userID] = null;
          msg = "\n" + users[ userID ] + " is now online";
          txtChat.setText( txtChat.getText() + msg );
          lst.setListData( users );
          displayUserList();
          //e
        }
        else if( token.equals( "DelUser" ) ) { // CMD = DelUser UserID
          System.out.println( "\nUSER DELETED") ;
          token = tokens.nextToken();
          userID = Integer.parseInt( token );
          msg = "\n" + users[ userID ] + " is now offline";
          txtChat.setText( txtChat.getText() + msg );
          users[ userID ] = null;
          sendTos[ userID ] = null;
          System.out.print( "\t" + token ) ; // id
          System.out.println();
          lst.setListData( users );
          displayUserList();
        } // CMD = SendAll FromUserID Data
        else if( token.equals( "SendAll" ) ) {
          System.out.println( "\nUSER MESSAGE") ;
          token = tokens.nextToken();
          userID = Integer.parseInt( token );
          System.out.println( "\tFrom User Name = " + users[ userID ] ) ;
          System.out.println( "\tUserID = " + token ) ;
          token = tokens.nextToken();
          while( tokens.hasMoreTokens() )
            token = token.toString() + " " + tokens.nextToken();
          System.out.println( "\tMessage = " + token ) ;
          msg = "\n[" + users[ userID ] + "] " + token;
          txtChat.setText( txtChat.getText() + msg );
        }
        else if( token.equals( "SendFrom" ) ) {
          token = tokens.nextToken();
          userID = Integer.parseInt( token );
          token = tokens.nextToken();
          while( tokens.hasMoreTokens() )
            token = token.toString() + " " + tokens.nextToken();
          msg = "\n[" + users[ userID ] + "] " + token;
          if( sendTos[ userID ] == null ) {
            //sendTos[ userID ] = new SendTo( userID, users[ userID ], userName, out );
            //sendTos[ userID ] = new SendTo( userID, users[ userID ], userName, out, this );
            sendTos[ userID ] = new SendTo( userID, this );
            sendTos[ userID ].setSize( 400, 400 );
            sendTos[ userID ].show();
          }
          //txtChat.setText( txtChat.getText() + msg );
          sendTos[ userID ].msgFrom( msg );
        }
      }
    }
    catch( UnknownHostException e ){System.out.println("Socket:"+e.getMessage());}
    catch( EOFException e ){System.out.println("EOF:"+e.getMessage());}
    catch (IOException e){System.out.println("run readline:"+e.getMessage());}
  }

  public void actionPerformed(ActionEvent ae) {
    if( ae.getActionCommand() == "btnSend" ) {
      String strData = txtData.getText();
      txtData.setText( "" );
      try {
        if( strData.equals( "bye" ) ) {
          out.writeUTF( strData );
          socket.close();
          dispose();
          System.exit( 0 );
        }

        //int selUserInd = lst.getSelectedIndex();
        //if( selUserInd == -1 )
        out.writeUTF( "SendAll" + " " + strData );
        //else
        //out.writeUTF( "SendTo" + " " + selUserInd + " " + strData );
      }
      catch( UnknownHostException e ){System.out.println("Socket:"+e.getMessage());}
      catch( EOFException e ){System.out.println("EOF:"+e.getMessage());}
      catch (IOException e){System.out.println(" btnreadline:"+e.getMessage());}
    }
    else if( ae.getActionCommand() == "btnExit" ) {
      try {
        out.writeUTF( "bye" );
        socket.close();
        dispose();
        System.exit( 0 );
      }
      catch( UnknownHostException e ){System.out.println("Socket:"+e.getMessage());}
      catch( EOFException e ){System.out.println("EOF:"+e.getMessage());}
      catch (IOException e){System.out.println(" btnreadline:"+e.getMessage());}
    }
  }
  void displayUserList() {
    System.out.println( "\nstart - Users List in users[]") ;
    for( int v = 0; v < users.length; v++ )
      System.out.println( "\t" + v + "\t" + users[v]) ;
    System.out.println( "\nend   - Users List in users[]") ;
  }// void displayUserList() {
}