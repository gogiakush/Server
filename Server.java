import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame
{
	private JTextField userText; //type stuff in
	private JTextArea chatWindow; //shows all messages
	private ObjectOutputStream output; //you send messages in this
	private ObjectInputStream input; //they use this to send messages to you
	private ServerSocket server;
	private Socket connection;
	
	public Server()
	{
		super("Kush's Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	public void startRunning()
	{
		try
		{
			server = new ServerSocket(1476, 100);
			while(true)
			{
				try
				{
					waitForConnection();
					setUpStreams();
					whileChatting();
				}
				catch (EOFException eofException)
				{
					showMessage("\nServer ended the connection");
				}
				
				finally
				{
					closeCrap();
				}
			}
		}
		
		catch(IOException ioexception)
		{
			ioexception.printStackTrace();
		}   
	}
	
	private void waitForConnection() throws IOException
	{
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}
}
