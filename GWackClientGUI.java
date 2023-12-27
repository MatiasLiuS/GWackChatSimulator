import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class GWackClientGUI extends Thread  {
/****************************************************
                GLOBAL VARIABLES 
****************************************************/
//Socket / connection things
private Socket socket;                  //Socket int 
private PrintWriter out;                //print writer (what I send to the server)
private BufferedReader in;              //buffered writer (what the server sends to me)
private boolean exit;                   //exit the thread

//JFrame
private JFrame frame;                   //frame 

//JLabels
private JLabel logoLabel;             //label to place the logo in

//JTextAreas 
private JTextArea messageTextInput;    //Where message is written
private JTextArea messageTextsArea;    //Where messages are displayed
private JTextArea activeMembersArea;   //Where activemembers are shown

//JTextFields
private JTextField usernameInput;      //Where Username is written
private JTextField portArea;           //Where portArea is written
private JTextField ipArea;             //Where ipArea is written

//JButtons
private JButton sendMessageB;           //SendMessage Button
private JButton connect;

//Other
private  int messageScreenCounter;                                       //How many messages are on screen
private  ArrayList<String> ActiveMembers = new ArrayList<String>() ;     //String Array of active members
private  String username;                                                //Set Username
private  String text;                                                    //Set Text
private  ColorUIResource lb= new ColorUIResource(138, 199, 219); //Light blue color
private  ColorUIResource lg= new ColorUIResource(241, 229, 172); //Light gold color
private int portInt;                                                     //make the port into an interger
private String ipString;                                                 //make ip into a string 

 //Set the window size
 private static final int HEIGHT = 700;
 private static final int WIDTH = 1200;
 
/****************************************************************************************************************
****************************************************************************************************************/

 public GWackClientGUI(){

/****************************************************
                Panels and Frames Formats
****************************************************/

  logoLabel = new JLabel();                                                 //set label for logo                                                 
  logoLabel.setIcon(new ImageIcon("ack.png"));                    //grab logo image
  Dimension size = logoLabel.getPreferredSize();                            //get dimension of logo
  logoLabel.setBounds(00, 00, size.width, size.height);                //set logo bounds

  //Frame and title (Window)    
  frame = new JFrame("GWack -- GW Slack Simulator (disconnected)");  //give title to frame
  frame.setSize(WIDTH,HEIGHT);                                             //set frame size

  //Panel where all connection related objects will be
  JPanel connectionPanel = new JPanel();                                   //set up connectionpanel
  connectionPanel.setLayout(new GridLayout(0,1));               //set up dimensions            
  connectionPanel.setBackground(lb);                                       //make it light blue

  //Panel where active members reside
  JPanel membersPanel = new JPanel();                                      //set up active members panel
  membersPanel.setLayout(new BorderLayout());                              //set up its layout
  membersPanel.setBackground(lb);                                          //make it light blue

  //Set panel where messages sent are shown
  JPanel messageBoardPanel = new JPanel();                                 //set up message board panel
  messageBoardPanel.setLayout(new BorderLayout());                         //set it in middle
  messageBoardPanel.setBackground(lb);                                     //set it light blue

  //Whole Compose message  Area
  JPanel bottomPanel = new JPanel();                                        //set up whole bottom area             

  //composeMessagePanel 
  JPanel composePanel = new JPanel();                                       //set panel to compose        
  bottomPanel.add(composePanel,BorderLayout.NORTH);                         //add compose panel to bottom panel
  composePanel.add(logoLabel,BorderLayout.SOUTH);                           //add logo next to compose
  bottomPanel.setBackground(lb);                                            //set it light blue
  composePanel.setBackground(lb);                                           //set it light blue


/****************************************************************************************************************
****************************************************************************************************************/

/****************************************************
*****************************************************
            TOP INITIAL CONNECTION AREA 
*****************************************************
****************************************************/


/****************************************************
                Input Username 
****************************************************/

  //Name tag
  JLabel usernameLabel = new JLabel("Name:");               //set name label next to user input
  connectionPanel.add(usernameLabel);                             //add it to connection panel
  connectionPanel.setLayout(new FlowLayout(FlowLayout.TRAILING)); //make it trail
  //Insert name area
  usernameInput = new JTextField("Matias",15);      //Set the usernameInput 
  usernameInput.setBackground(lg);                                //set the color to gold
  connectionPanel.add(usernameInput);                             //add the input area to to connection panel
  
/****************************************************
                Input Host/IP
****************************************************/

  //IP tag
  JLabel ipLabel = new JLabel("IP Address:");               //set IP Address label next to ip input
  connectionPanel.add(ipLabel);                                   //add it to connection panel
  //Insert IP area
  ipArea = new JTextField("ssh-cs2113.adamaviv.com",10);                   //create area where ip is inputed
  ipArea.setBackground(lg);                                         //set the color to gold
  connectionPanel.add(ipArea);                                    //add it to the connection panel

/****************************************************
                Input Port
****************************************************/

  //Port Tag
  JLabel portLabel = new JLabel("Port:");                    //set Port: label next to port input
  portLabel.setBounds(0,0,0,0);                //set its size
  connectionPanel.add(portLabel);                                 //add it to connection panel
  //Insert Port area
  portArea = new JTextField("8888",10);                 //create area where port is inputed
  portArea.setBackground(lg);                                     //set the color to gold
  connectionPanel.add(portArea);                                  //add it to connection panel

/****************************************************************************************************************
****************************************************************************************************************/


/****************************************************
              Connection Button
****************************************************/

  connect = new JButton("  Connect  "); //create connection button
  connectionPanel.add(connect);                       //add it to the connection panel
  connect.addActionListener(new ActionListener(){     //give action to button
    //Button Action
    public void actionPerformed(ActionEvent e) {      //give action to button 

/****************************************************
        Check if all instances are inputed 
****************************************************/

        //Check if userinput is empty
        if(usernameInput.getText().equals("") || usernameInput.getText().isEmpty()) { 
            JOptionPane.showMessageDialog(frame, //Show error message window
            "Please Input Name.",
            "Error Connecting",
            JOptionPane.ERROR_MESSAGE);
            return;
         } 
        //Check if ipInput is empty
        if(ipArea.getText().equals("") || ipArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, //Show error message window
            "Please Input IP.",   
            "Error Connecting",
            JOptionPane.ERROR_MESSAGE);
            return;
         }
         //Check if portInput is empty
        if(portArea.getText().equals("") || portArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, //Show error message window
            "Please Input Valid Port.",
            "Error Connecting",
            JOptionPane.ERROR_MESSAGE); 
            return;
        }
        
         username = usernameInput.getText(); //get the username and make it a string
         ipString = ipArea.getText();                                          //Set ip text to string
         portInt = Integer.parseInt(portArea.getText());                       //set port text to int

         if(portInt < 8886 || portInt >8888){
          connect.setText("  Connect  ");                                  //Set it to connect
          frame.setTitle("GWack -- GW Slack Simulator (disconnected)");   //Change frame title  
          JOptionPane.showMessageDialog(frame, //Show error message window
          "Please Input Valid Port.",
          "Error Connecting",
          JOptionPane.ERROR_MESSAGE); 
          return;
         }

/****************************************************
             Connecting Performance
****************************************************/
              
            if (connect.getText().equals("  Connect  ")){                   //If button reads connect
                //text changes
                connect.setText("Disconnect");                                  //Set it to disconnect
                frame.setTitle("GWack -- GW Slack Simulator (connected)");     //Change frame title     
                //functionality changes    
                sendMessageB.setEnabled(true);                                       //Allow message to be sent
                usernameInput.setEditable(false);                                   //Set so username cant be changed
                ipArea.setEditable(false);                                          //Set so ip cant be changed
                portArea.setEditable(false);                                        //Set so port cant be changed  
                //color sets
                usernameInput.setBackground(lg);                                      //Set background color to light gold
                ipArea.setBackground(lg);                                             //Set background color to light gold  
                portArea.setBackground(lg);                                           //Set background color to light gold
                ipString = ipArea.getText();                                          //Set ip text to string
                portInt = Integer.parseInt(portArea.getText());                       //set port text to int
                getSocket(ipString, portInt);                                         //Connect to server
             } 

/****************************************************
             Disconecting Performance
****************************************************/

              else if (connect.getText().equals("Disconnect")){              //If button reads disconnect
                Disconnection();
              }
      }
   });

/****************************************************************************************************************
****************************************************************************************************************/

/****************************************************
                ACTIVE USERS AREA
****************************************************/

  JLabel memberLabel = new JLabel("Members Online");            //Set members online tag
  membersPanel.add(memberLabel,BorderLayout.NORTH);                  //Add label to membersPanel
  activeMembersArea = new JTextArea(null,10,10); //set the active user text area
  activeMembersArea.setBackground(lg);                               //set it to light gold
  membersPanel.add(activeMembersArea);                               //add active members to membersPanel
  activeMembersArea.setEditable(false);                           //Set it so it cant be edited

/****************************************************************************************************************
****************************************************************************************************************/

/****************************************************
               CHAT SCREEN AREA
****************************************************/

  JLabel messageLabel = new JLabel("Messages");                //Set message board label
  messageBoardPanel.add(messageLabel,BorderLayout.NORTH);           //add it to board panel
  JLabel gap = new JLabel(" ");                                //lil gap
  messageBoardPanel.add(gap,BorderLayout.WEST);                     //add it to board panel
  messageTextsArea = new JTextArea(null,10,15); //set up the text adea
  messageTextsArea.setBackground(lg);                               //set it to light gold
  messageBoardPanel.add(messageTextsArea, BorderLayout.EAST);       //add it to boardPanel
  messageTextsArea.setEditable(false);                           //make it uneditable

/****************************************************
               SEND MESSAGE AREA
****************************************************/

  JLabel composeLabel = new JLabel("Compose");   //Set compose tag
  composePanel.add(composeLabel,BorderLayout.NORTH);   //add it to compose panel
    
/****************************************************
                Write Message
****************************************************/

  messageTextInput = new JTextArea("",4,20); //set TextInput area
  messageTextInput.setBackground(lg);                                               //set it to light gold
  composePanel.add(messageTextInput,BorderLayout.SOUTH);                            //add it to composePanel

/****************************************************
               SEND Message with Enter Key
****************************************************/
messageTextInput.addKeyListener(new KeyAdapter() {                       //Read for key
    @Override 
    public void keyPressed(KeyEvent e) {                                 //Upon Key Pressed
      if(connect.getText().equals("Disconnect")){              //Only works if its connected to a server
        if(e.getKeyCode() == KeyEvent.VK_ENTER){                         //if Enter key is pressed
          text = messageTextInput.getText();                             //get the text input and set it to string
          SendMessage(text);
          messageTextInput.selectAll();                                   //select text in input area                            
          messageTextInput.replaceSelection("");                 //set it all to null     
         }
      }                    
    }
    public void keyReleased(KeyEvent e) {                                 //upon key released
      if(connect.getText().equals("Disconnect")){               //Only works if its connected to a server
        if(e.getKeyCode() == KeyEvent.VK_ENTER){                          //if enter key is pressed
          messageTextInput.selectAll();                                   //select text in input area                            
          messageTextInput.replaceSelection("");                 //set it all to nul
         }
      }
    }
 });
/****************************************************
                Send Message Button
****************************************************/

  sendMessageB = new JButton("Send");                              //set up send buttom  
  sendMessageB.setEnabled(false);                                     //make it unclickable
  sendMessageB.addActionListener(new ActionListener(){                   //Add an action to button                                                                                                        
   public void actionPerformed(ActionEvent e) {                          //Add an action button                                                           
      text = messageTextInput.getText();                                 //get the text input and set it to string                                                                                               
      SendMessage(text);                                                 //send the message
     }
});

/****************************************************************************************************************
****************************************************************************************************************/

/****************************************************
                 ADD SCROLLBAR
****************************************************/

  JScrollPane scrollBar = new JScrollPane(messageTextsArea,          //Set Scrollbar
  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,                             //Show vertical scollbar always
  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);                           //Never show vertical scrollbar
  scrollBar.getVerticalScrollBar().setUI(new BasicScrollBarUI() {    //set the color for scrollbar
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new ColorUIResource(212, 175, 55); //make it gold
    }
});

/****************************************************
     ADD THE PANELS TO CONSTRUCT THE WINDOW
****************************************************/

  //Set Bottom Panel
  bottomPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));  //set layout for bottomPanel
  bottomPanel.add(sendMessageB,BorderLayout.SOUTH);             //add sendmessage to bottomPanel

  //Add panels to frame
  frame.add(connectionPanel,BorderLayout.NORTH);                //add connectionPanel to frame
  frame.add(membersPanel,BorderLayout.WEST);                    //add membersPanel to frame
  frame.add(messageBoardPanel,BorderLayout.CENTER);             //add messageBoardPanel to frame   
  frame.add(bottomPanel,BorderLayout.SOUTH);                    //add bottomPanel to frame            

  //Set Scrollbar
  scrollBar.setBackground(lg);                                  //set the scrollbar background to light gold
  frame.add(scrollBar);                                         //add scrollbar to frame

  //Put it all together
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);         //Make it so it exits on close
  frame.pack();                                                 //pack it all 
  frame.setVisible(true);                                    //make it visible
 }

/****************************************************************************************************************
*****************************************************************************************************************
****************************************************************************************************************/

/****************************************************
     SEND MESSAGE CONSTRUCTOR
****************************************************/

public void SendMessage(String msg) {
  username = usernameInput.getText();                                 //get username                                                                                              
  if(messageScreenCounter < 100){                                     //Once it hits 100, remove top message                                
    out.println(text);                                                //print message on server
    out.flush();                                                      //flush it                    
    messageTextInput.selectAll();                                     //select text in input area                            
    messageTextInput.replaceSelection("");                   //set it all to null                                                 
    messageScreenCounter++;                                           //messagecounter ++                    
  }else{                                                                 
      String s = messageTextsArea.getText();                          //Get string text of message Text area                                        
      s = s.substring(s.indexOf("\n")+1);                        //add null factor and one                                          
      out.println(text);                                              //print message on server           
      out.flush();                                                    //flush it                                                                 
      messageTextInput.selectAll();                                   //select text in input area                              
    messageTextInput.replaceSelection("");                   //set it all to null                                                
  }
}

/****************************************************
                Socket Connection
****************************************************/

public void getSocket(String ipString, int portInt){  
  try{
   socket = new Socket(ipString, portInt);                                   //connect server
   out = new PrintWriter(socket.getOutputStream());                          //Print writer
   in = new BufferedReader( new InputStreamReader(socket.getInputStream())); //Print writer
   //Handshake
   out.println("SECRET");                                       
   out.println("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87");
   out.println("NAME");
   out.println(username);
   out.flush();                                                             //Send it to the server
   JOptionPane.showMessageDialog(frame,                                     //Show connection window
   ("Connected Succesfully\n" + "Host: " + ipString + "\nPort: " + portInt),   
   "Information:",
   JOptionPane.INFORMATION_MESSAGE);
   new ThreadDemo();
  }catch(IOException C){ 
    JOptionPane.showMessageDialog(frame,                                     //Show connection window
   ("Cannot Connect:\n" + "Host: " + ipString + "\nPort: " + portInt),   
   "ERROR:",
   JOptionPane.ERROR_MESSAGE);
    Disconnection();
  }
}
public void Disconnection(){  
//text changes                                  
connect.setText("  Connect  ");                                  //Set it to connect
frame.setTitle("GWack -- GW Slack Simulator (disconnected)");   //Change frame title     
//functionality changes                                                 
sendMessageB.setEnabled(false);                                     //Disallow message to be sent
usernameInput.setEditable(true);                                    //Set so username can be changed
ipArea.setEditable(true);                                           //Set so ip can be changed
portArea.setEditable(true);                                         //Set so port can be changed 
//color sets                                                            
usernameInput.setBackground(lg);                                       //Set background color to light gold 
ipArea.setBackground(lg);                                              //Set background color to light gold 
portArea.setBackground(lg);                                            //Set background color to light gold 
//Active Members Printing                                                  
activeMembersArea.setText(null);                                    //clear active members list   
messageTextsArea.setText(null);                                     //clear text area                                                            
ActiveMembers.remove(username);                                        //Remove username to active members list   
try{
  socket.close();                                                      //close socket
  in.close();                                                          //close print writer                                
  out.close();                                                         //close Buffer reader                                        
  exit = true;                                                         //stop thread                                        
}catch(Exception C){  
}
}
/****************************************************
                Buffer Reading Thread
****************************************************/
public class ThreadDemo implements Runnable {
  Thread t1;                                        //create thread
  ThreadDemo() {
     t1 = new Thread(this);                         //new thread
     exit = false;                                  //set exit to false
     t1.start();                                    //start thread
  }
@Override
  public void run() {                                            //thread runner
    while (!exit) {
      try {

            String str = in.readLine();                           //reads the input from textfield
            if(str.equals("START_CLIENT_LIST")){        //if its in the client list 
              str = in.readLine();                                //Read the line
              activeMembersArea.setText("");                   //clear the active members area
                while(!str.equals("END_CLIENT_LIST")){  //if its not at the end of the list
                  activeMembersArea.append(str+"\n");             //insert the current user
                  str = in.readLine();                            //read in the line
                }
            }else{                                                 //once done with the user listt
              messageTextsArea.append(str+"\n");                   //add the text message in the message area
              System.out.println("client data : " + str);          //test on terminal
            }
      } catch (Exception e) {
      }  
    }
    }
}
/****************************************************
     MAIN FUNCTION (OPEN WINDOW)
****************************************************/
 public static void main(String[] args){
 new GWackClientGUI();
 }
}
