import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{
  private JTextField userText;
  private JTextArea chatWindow;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private String message = "";
  private String serverIP;
  private Socket connection;

  public Client(String host)
  {
    super("Client mofo!");
    serverIP = host;
    userText = new JTextField();
    userText.setEditable(false);
    userText.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent event){
          sendData(event.getActionCommand());
          userText.setText("");
        }
      }
    );
    add(userText, BorderLayout.NORTH);
    chatWindow = new JTextArea();
    add(new JScrollPane(chatWindow), BorderLayout.CENTER);
    setSize(300, 150);
    setVisible(true);
  }

  public void startRunning()
  {
    try
    {
      connectToServer();
      setUpStreams();
      whileChatting();
    }

    catch(EOFException eofexception)
    {
      showMessage("\n Client terminated connenction");
    }

    catch(IOException ioexception)
    {
      ioexception.printStackTrace();
    }

    finally
    {
      closeCrap();
    }
  }

  private void connectToServer() throws IOException
  {
    showMessage("Attempting connection... \n");
    connection = new Socket(InetAddress.getByName(serverIP), 1476);
    showMessage("Connected to:" + connection.getInetAddress().getHostName());
  }

  private void setUpStreams() throws IOException
  {
    output = new ObjectOutputStream(connection.getOutputStream());
    output.flush();
    input = new ObjectInputStream(connection.getInputStream());
    showMessage("\n Dude your streams are now good to go! \n");
  }

  private void whileChatting() throws IOException
  {
    ableToType(true);
    do {
      try
      {
        message = (String) input.readObject();
        showMessage("\n" + message);
      }

      catch(ClassNotFoundException classnotfoundexception)
      {
        showMessage("\n I don't know that object type");
      }
    } while (!message.equals("SERVER - END"));
  }

  private void closeCrap()
  {
    showMessage("\n Closing convo down");
    ableToType(false);

    try
    {
      output.close();
			input.close();
			connection.close();
    }

    catch(IOException ioexception)
    {
      ioexception.printStackTrace();
    }
  }

  private void sendData(String message)
  {
    try
    {
      output.writeObject("CLIENT - " + message);
      output.flush();
      showMessage("\nCLIENT - " + message);
    }

    catch (IOException ioexception)
    {
      chatWindow.append("\n Something messed up!");
    }
  }

  private void showMessage(final String m)
  {
    SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(m);
				}
			}
		);
  }

  private void ableToType(final boolean tof)
  {
    SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
  }
}
