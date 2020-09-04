import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.ScrollPane;
import javax.swing.JTextField;
import java.awt.Choice;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class GUIReceiver implements ActionListener {

	private JFrame frmReceiver;
	private JTextField ipReceiver;
	private JTextField portNumberSender;
	private JTextField portNumberReceiver;
	private JTextField fileReceiver;
	private Choice choiceMode; 
	private JTextArea txtAreaConsole;
	private TextArea txtAreaPackets;

	static boolean reliable = true; 
	static DatagramSocket socket = null; 
	static InetAddress address; 
	static boolean ack[]; 
	static boolean transmitting = true; 
	static Thread receive;  
	static String filename;
	static int byteOver;
	static int PNSender;
	static int PNReceiver;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIReceiver window = new GUIReceiver();
					window.frmReceiver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIReceiver () {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmReceiver = new JFrame();
		frmReceiver.getContentPane().setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		frmReceiver.getContentPane().setBackground(Color.DARK_GRAY);
		frmReceiver.setTitle("Receiver");
		frmReceiver.setBounds(100, 100, 737, 467);
		frmReceiver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReceiver.getContentPane().setLayout(null);
		
		JLabel lblIPR = new JLabel("Enter IP Address of Receiver:");
		lblIPR.setForeground(Color.WHITE);
		lblIPR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblIPR.setBounds(12, 13, 200, 22);
		frmReceiver.getContentPane().add(lblIPR);
		
		JLabel lblPNS = new JLabel("Enter UDP Port Number for Sender:");
		lblPNS.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblPNS.setForeground(Color.WHITE);
		lblPNS.setBounds(12, 56, 245, 22);
		frmReceiver.getContentPane().add(lblPNS);
		
		JLabel lblPNR = new JLabel("Enter UDP Port Number for Receiver:");
		lblPNR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblPNR.setForeground(Color.WHITE);
		lblPNR.setBounds(12, 96, 245, 29);
		frmReceiver.getContentPane().add(lblPNR);
		
		JLabel lblNewLabel_3 = new JLabel("Enter Name of File to Write Received Data to:");
		lblNewLabel_3.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblNewLabel_3.setForeground(Color.WHITE);
		lblNewLabel_3.setBounds(12, 142, 309, 22);
		frmReceiver.getContentPane().add(lblNewLabel_3);
		
		txtAreaPackets = new TextArea();
		txtAreaPackets.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		txtAreaPackets.setForeground(Color.LIGHT_GRAY);
		txtAreaPackets.setEditable(false);
		txtAreaPackets.setBounds(370, 230, 339, 180);
		
		JLabel lblPackets = new JLabel("------------------ In-Order Packets ------------------");
		lblPackets.setForeground(Color.WHITE);
		lblPackets.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblPackets.setBounds(370, 208, 339, 16);
		frmReceiver.getContentPane().add(lblPackets);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setBounds(370, 232, 339, 178);
		scrollPane.add(txtAreaPackets);
		frmReceiver.getContentPane().add(scrollPane);
		
		ipReceiver = new JTextField("localhost");
		ipReceiver.setBounds(325, 11, 384, 30);
		frmReceiver.getContentPane().add(ipReceiver);
		ipReceiver.setColumns(10);
		
		portNumberSender = new JTextField("4433");
		portNumberSender.setBounds(325, 54, 384, 30);
		frmReceiver.getContentPane().add(portNumberSender);
		portNumberSender.setColumns(10);
		
		portNumberReceiver = new JTextField("3344");
		portNumberReceiver.setBounds(325, 97, 384, 30);
		frmReceiver.getContentPane().add(portNumberReceiver);
		portNumberReceiver.setColumns(10);
		
		fileReceiver = new JTextField("Testing.txt");
		fileReceiver.setBounds(325, 140, 384, 30);
		frmReceiver.getContentPane().add(fileReceiver);
		fileReceiver.setColumns(10);
		
		choiceMode = new Choice();
		choiceMode.setBounds(12, 198, 156, 29);
		choiceMode.add("Reliable");
		choiceMode.add("Unreliable");
		frmReceiver.getContentPane().add(choiceMode);
		
		JButton btnConnect = new JButton("CONNECT");
		btnConnect.setEnabled(true);
		btnConnect.addActionListener(this);
		btnConnect.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		btnConnect.setBounds(184, 199, 162, 27);
		frmReceiver.getContentPane().add(btnConnect);
		
		JLabel lblConsole = new JLabel("---------------------- Console -----------------------");
		lblConsole.setForeground(Color.WHITE);
		lblConsole.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblConsole.setBounds(12, 256, 334, 16);
		frmReceiver.getContentPane().add(lblConsole);
		
		txtAreaConsole = new JTextArea();
		txtAreaConsole.setBounds(12, 280, 334, 130);
		txtAreaConsole.setEnabled(false);

		ScrollPane scrollPane_1 = new ScrollPane();
		scrollPane_1.setBounds(12, 280, 334, 130);
		scrollPane_1.add(txtAreaConsole);
		frmReceiver.getContentPane().add(scrollPane_1);
	}

	public void makeConnection(){
		try {
			PNSender = Integer.parseInt(portNumberSender.getText());
			PNReceiver = Integer.parseInt(portNumberReceiver.getText());
			socket = new DatagramSocket(PNReceiver);
			address = InetAddress.getByName(ipReceiver.getText());
			socket.connect(address, Integer.parseInt(portNumberSender.getText()));
			filename = fileReceiver.getText(); 
			System.out.println(filename); 
		}
		catch(Exception e){
			System.out.println("There is an error on connection"); 
		}
	}

	public void disconnection(){
		try{
			socket.close();
		}
		catch(Exception e){
			System.out.println("This doesn't connect"); 
		}
	}

	public String Handshake(){
		try{
			System.out.println("sfdsdfsdf");
			//---------------Receive the SYN packet----------------
			byte[] banner = new byte[128]; 
			DatagramPacket packet = new DatagramPacket(banner, banner.length);
			socket.receive(packet);
			String synchronize = new String (packet.getData());
			System.out.println(synchronize + "fsfsdf");
			//--------------Send the SYNACK packet------------------ 
			banner = new byte[4];
//			ByteBuffer buffer = ByteBuffer.wrap(banner); 
//			buffer.putInt(-1); 
			InetAddress ia = InetAddress.getLocalHost();
			String ack = new String("0");
			banner = ack.getBytes();
			DatagramPacket complete = new DatagramPacket(banner, banner.length, ia, PNSender);
			socket.send(complete); 

			return synchronize; 
		}
		catch (Exception e){
			txtAreaConsole.append(e + "/n");  
		}
		return ""; 
	}

	public void ReceiverThread(){
		receive = new Thread(){
			public void run(){
				try{
					while (true) {
						int count = 1;
						transmitting = true;
						
						//-------------Handshake String----------------
						String packInfo[] = Handshake().split(" ");
						
						int sizeOfPacket = Integer.parseInt(packInfo[0]);
						int numOfPacket = Integer.parseInt(packInfo[1]);
						int leftOverByte = Integer.parseInt(packInfo[2]);
						
						//-----------------Create buffers/ File array for packets------------
						byte[][] file = new byte[numOfPacket][sizeOfPacket];
						ack = new boolean[numOfPacket];
						byte[] buffers = new byte[sizeOfPacket];
						
						//-----------------Create Initial Packet-----------------------------
						DatagramPacket packet = new DatagramPacket(buffers, sizeOfPacket);
						
						//-----------------Loop while receivin-------------------------------
						while(transmitting){
							count++;
							socket.receive(packet);
							String str = new String(packet.getData());
							
							byte[] seqNumByte = Arrays.copyOfRange(buffers, 0, 4);
							byte[] filePrintBuf;
							int sequenceNum = ByteBuffer.wrap(seqNumByte).getInt();
							if (sequenceNum == (numOfPacket)){
								filePrintBuf = Arrays.copyOfRange(buffers, 4, leftOverByte);
							}
							else{
								filePrintBuf = Arrays.copyOfRange(buffers, 4, buffers.length);
							}
							if(sequenceNum == -1){
								transmitting = false;
								FileOutputStream fileoutput = new FileOutputStream(filename); 
								for(int j = 0; j<numOfPacket; j++){
									fileoutput.write(file[j]);
								}
								fileoutput.close();
	
							}
							else if(count%10 !=0 || reliable){
								file[sequenceNum] = filePrintBuf;
								ack[sequenceNum] = true;
								
								DatagramPacket packetSend = new DatagramPacket(seqNumByte, 4); 
								socket.send(packetSend); 
							}
	
						}

					}
				}
				catch (Exception e){
					txtAreaConsole.append(e + "/n"); 
				}
			}
		};
				
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String reliableCheck = choiceMode.getItem(choiceMode.getSelectedIndex());
		String action = e.getActionCommand();
		if(action.equals("CONNECT")){
			if(reliableCheck.equals("Unreliable")){
				makeConnection();
				ReceiverThread();
				receive.start(); 
				System.out.println("Unreliable"); 
			}
			else{
				makeConnection();
				ReceiverThread();
				receive.start();
//				System.out.println("Reliable"); 
				
				
			}
		}
		else if(action.equals("Disconnect")){
			disconnection(); 
		}
	}
}