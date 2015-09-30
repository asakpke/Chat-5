import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.*;
import java.net.*;

public class SendTo extends JFrame implements ActionListener {
  JTextArea txtChat;
  JTextField txtData;
  int toUserID;
  String toUserName;
  String fromUserName;
  DataOutputStream out;
  TCPClient tcpClient;

  //public SendTo( int argToUserID, String argToUserName, String argFromUserName, DataOutputStream argOut ) {
  //public SendTo( int argToUserID, String argToUserName, String argFromUserName, DataOutputStream argOut, TCPClient argTCPClient ) {
  public SendTo( int argToUserID, TCPClient argTCPClient ) {
    //super( argFromUserName + " chatting with " + argToUserName );
    tcpClient = argTCPClient;
    //out = argOut;
    out = tcpClient.out;
    toUserID = argToUserID;
    //fromUserName = argFromUserName;
    fromUserName = tcpClient.userName;
    //toUserName = argToUserName;
    toUserName = tcpClient.users[ toUserID ];
    setTitle( fromUserName + " chatting with " + toUserName );
    JScrollPane scrollPaneTxt;   // NEW
    txtChat = new JTextArea( "CHATTING ROOM FOR GENERAL CHAT" );
    txtChat.setEditable( false );
    txtChat.setBackground( Color.lightGray );
    scrollPaneTxt = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
           , JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    scrollPaneTxt.add( txtChat );
    scrollPaneTxt.setViewportView( txtChat );
    getContentPane().add( scrollPaneTxt, BorderLayout.CENTER );

    JPanel bottomPanel= new JPanel( new FlowLayout() );
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
  }// SendTo()

  public void actionPerformed(ActionEvent ae) {
    if( ae.getActionCommand() == "btnSend" ) {
      String strData = txtData.getText();
      txtData.setText( "" );
      try {
        if( strData.equals( "bye" ) ) {
          hide();
          tcpClient.sendTos[ toUserID ] = null;
          dispose();
        }

        out.writeUTF( "SendTo" + " " + toUserID + " " + strData );
        String msg;
        msg = "\n[" + fromUserName + "] " + strData;
        txtChat.setText( txtChat.getText() + msg );
      }
      catch( UnknownHostException e ){System.out.println("Socket:"+e.getMessage());}
      catch( EOFException e ){System.out.println("EOF:"+e.getMessage());}
      catch (IOException e){System.out.println(" btnreadline:"+e.getMessage());}
    }
    else if( ae.getActionCommand() == "btnExit" ) {
      hide();
      tcpClient.sendTos[ toUserID ] = null;
      dispose();
    }
  }// actionPerformed()
  public void msgFrom( String msg ) {
    txtChat.setText( txtChat.getText() + msg );
  } // msgFrom()
}// class SendTo