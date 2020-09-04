//GUI imports
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.TextArea;
import java.awt.ScrollPane;
//File Transfer imports
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.nio.ByteBuffer;




public class GUISender {
	//private GUI elements
	private JFrame frmSender;
	private JTextField IPR;
	private JTextField PNR;
	private JTextField PNS;
	private JTextField MDS;
	private JTextField txtTransmissionTime;
	private JTextField FileName;
	private JLabel lblEnterNameOf;
	private JLabel lblTimeout;
	private JTextField Timeout;
	private TextArea txtAreaConsole;
	private ScrollPane scrollPane;
	private JLabel lblNewLabel_1;
	
	//private variables
	static InetAddress IPReceiver;
	static int PNReceiver;
	static int PNSender;
	static DatagramSocket dSocket;
	static boolean connected = false;
	static boolean send = true;
	static File fileName;
	static FileInputStream fInput;
	static Thread transfer;
	static long time = 0;
	static int TOI;
	static int mds;
	static int bytes;
	static int numSeq;
	static int seqNumber;
	static int packet;
	static int synACK;
	byte[][] buffer;
	int[] ACKArray;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUISender window = new GUISender();
					window.frmSender.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUISender() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSender = new JFrame();
		frmSender.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmSender.getContentPane().setBackground(Color.DARK_GRAY);
		frmSender.setBackground(Color.DARK_GRAY);
		frmSender.setTitle("Sender");
		frmSender.setBounds(100, 100, 724, 467);
		frmSender.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSender.getContentPane().setLayout(null);
		
		JLabel lblIPR = new JLabel("Enter IP Address of Receiver:");
		lblIPR.setForeground(Color.WHITE);
		lblIPR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblIPR.setBounds(12, 17, 197, 28);
		frmSender.getContentPane().add(lblIPR);
		
		JLabel lblPNR = new JLabel("Enter UDP Port Number for Receiver:");
		lblPNR.setForeground(Color.WHITE);
		lblPNR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblPNR.setBounds(12, 60, 254, 28);
		frmSender.getContentPane().add(lblPNR);
		
		JLabel lblPNS = new JLabel("Enter UDP Port Number for Sender:");
		lblPNS.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblPNS.setForeground(Color.WHITE);
		lblPNS.setBounds(12, 103, 254, 28);
		frmSender.getContentPane().add(lblPNS);
		//---------IP Address Receiver-------------------------------
		IPR = new JTextField("localhost"); 
		IPR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		IPR.setBounds(288, 16, 408, 30);
		frmSender.getContentPane().add(IPR);
		IPR.setColumns(10);
		//---------Port Number Receiver-------------------------------
		PNR = new JTextField("3344");
		PNR.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		PNR.setBounds(288, 59, 408, 30);
		frmSender.getContentPane().add(PNR);
		PNR.setColumns(10);
		//---------Port Number Sender-------------------------------
		PNS = new JTextField("4433");
		PNS.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		PNS.setBounds(288, 102, 408, 30);
		frmSender.getContentPane().add(PNS);
		PNS.setColumns(10);
		
		JLabel lblDataSize = new JLabel("Enter Max Size of UDP Datagram:");
		lblDataSize.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblDataSize.setForeground(Color.WHITE);
		lblDataSize.setBounds(12, 145, 254, 31);
		frmSender.getContentPane().add(lblDataSize);
		//---------Max Datagram Size-------------------------------
		MDS = new JTextField();
		MDS.setText("700");
		MDS.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		MDS.setBounds(288, 145, 408, 30);
		frmSender.getContentPane().add(MDS);
		MDS.setColumns(10);
		
		//---------Transfer Button-------------------------------
		JButton btnTransfer = new JButton("TRANSFER");
		btnTransfer.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		btnTransfer.setBounds(336, 315, 360, 31);
		btnTransfer.addActionListener(new TransferListener());
		frmSender.getContentPane().add(btnTransfer);
		
		//---------Transmission Time TextField-------------------------------
		txtTransmissionTime = new JTextField();
		txtTransmissionTime.setBackground(Color.LIGHT_GRAY);
		txtTransmissionTime.setForeground(Color.GREEN);
		txtTransmissionTime.setEditable(false);
		txtTransmissionTime.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		txtTransmissionTime.setBounds(410, 377, 286, 30);
		frmSender.getContentPane().add(txtTransmissionTime);
		txtTransmissionTime.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("---------- Total Transmission Time ----------");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblNewLabel.setBounds(410, 359, 286, 16);
		frmSender.getContentPane().add(lblNewLabel);
		
		//---------File Name-------------------------------
		FileName = new JTextField();
		FileName.setText("footFettish.jpg");
		FileName.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		FileName.setBounds(288, 188, 408, 30);
		frmSender.getContentPane().add(FileName);
		FileName.setColumns(10);
		
		lblEnterNameOf = new JLabel("Enter Name of File to be Transferred:");
		lblEnterNameOf.setForeground(Color.WHITE);
		lblEnterNameOf.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblEnterNameOf.setBounds(12, 189, 254, 29);
		frmSender.getContentPane().add(lblEnterNameOf);
		
		lblTimeout = new JLabel("Enter Integer for Timeout (in microseconds):");
		lblTimeout.setFont(new Font("Yu Gothic Light", Font.PLAIN, 16));
		lblTimeout.setForeground(Color.WHITE);
		lblTimeout.setBounds(12, 231, 320, 28);
		frmSender.getContentPane().add(lblTimeout);
		
		//---------Timeout Interval-------------------------------
		Timeout = new JTextField();
		Timeout.setText("600");
		Timeout.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		Timeout.setBounds(336, 231, 360, 30);
		frmSender.getContentPane().add(Timeout);
		Timeout.setColumns(10);
		
		txtAreaConsole = new TextArea();
		txtAreaConsole.setBackground(Color.LIGHT_GRAY);
		txtAreaConsole.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		txtAreaConsole.setEditable(false);
		txtAreaConsole.setBounds(10, 288, 299, 122);
		
		scrollPane = new ScrollPane();
		scrollPane.setBounds(12, 288, 297, 114);
		scrollPane.add(txtAreaConsole);
		frmSender.getContentPane().add(scrollPane);
		
		lblNewLabel_1 = new JLabel("------------------- Console -------------------");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(12, 266, 299, 16);
		frmSender.getContentPane().add(lblNewLabel_1);
		
		//---------Connect Button-------------------------------
		JButton btnConnect = new JButton("CONNECT");
		btnConnect.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 16));
		btnConnect.setBounds(336, 274, 360, 31);
		btnConnect.addActionListener(new ConnectListener());
		frmSender.getContentPane().add(btnConnect);
	}
	
	//listener for connect button
	private class ConnectListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//variables
			PNReceiver = getPNR();
			PNSender = getPNS();
			//get IP Address
			try {
				IPReceiver = InetAddress.getLocalHost();
				//IPReceiver = InetAddress.getByName(IPR.getText());
				//code to establish connection
				try {
					dSocket = new DatagramSocket(PNSender);
					dSocket.connect(IPReceiver, PNReceiver);
					connected = true;
					txtAreaConsole.append("Connection Successful, Great Success i Liek\n");
				}catch(Exception ce) {
					txtAreaConsole.append("Error: Could Not Establish Connection\n");
				}
			}catch(Exception error) {
				txtAreaConsole.append("Error: Incorrect Receiver IP Address Value\n");
			}
		}
		//returns Receiver Port Number from text area
		//returns -1 if incorrect input
		public int getPNR() {
			try {
				int pnr = Integer.parseInt(PNR.getText());
				return pnr;
			}catch(Exception error) {
				txtAreaConsole.append("Error: Incorrect Receiver Port Number Value\n");
				return -1;
			}
		}
		//returns Sender Port Number from text area
		//returns -1 if incorrect input
		public int getPNS() {
			try {
				int pns = Integer.parseInt(PNS.getText());
				return pns;
			}catch(Exception error) {
				txtAreaConsole.append("Error: Incorrect Sender Port Number Value\n");
				return -1;
			}
		}
	} //end of ConnectListener
	
	//listener for transfer button
	private class TransferListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//variables
			TOI = getTimeout();
			mds = getMDS();
			fileName = new File(getFile());
			
			//if TOI and mds inputs are correct attempt to open file stream
			if(mds != -1 && TOI != -1 && connected == true) {
				try {
					
					//variables and calculations
					bytes = (int)fileName.length(); //size of file
					numSeq = (int) Math.ceil(bytes/mds); //total number of sequences
					//set socket timeout value
					dSocket.setSoTimeout(TOI);
					//create fileInputStream
					fInput = new FileInputStream(fileName);
					//get start time and start file transfer
					//read in entire file
					buffer = new byte[numSeq][mds];
					ACKArray = new int[numSeq];
					
					for(int i = 0; i < numSeq; i++) {
						//initialize all ACK values to 0
						ACKArray[i] = 0;
						//read contents of file into buffer
						packet = fInput.read(buffer[i]);
					}
					time = System.currentTimeMillis();
					handshake();
//					transferThread();
//					transfer.start();
						
				}catch(Exception fe) {
					txtAreaConsole.append("Error: Could Not Open File Input Stream");
				}
			}else {
				txtAreaConsole.append("Error: Please input correct values before transfer");
			}
			
			
		}
		
		//returns file name from input field
		public String getFile() {
			String fileString = FileName.getText();			
			return fileString;
		}
		
		//returns Timeout Interval from text area
		//returns -1 if incorrect input
		public int getTimeout() {
			try {
				int TOI = Integer.parseInt(Timeout.getText());
				return TOI;
			}catch(Exception error) {
				txtAreaConsole.append("Error: Incorrect Timeout Value\n");
				return -1;
			}
		}
		//returns MDS from text area
		//returns -1 if incorrect input
		public int getMDS() {
			try {
				int mds = Integer.parseInt(MDS.getText());
				return mds;
			}catch(Exception error) {
				txtAreaConsole.append("Error: Incorrect Datagram Size Value\n");
				return -1;
			}
		}
	} //end of TransferListener
	
	public void handshake() {
		try {
			//----handshake----
			//send SYN
			byte[] s = (Integer.toString(mds + 4) + " " + Integer.toString(numSeq) + " " + Integer.toString((int) (((numSeq) * (mds + 4)) - bytes)) + " ").getBytes();
			System.out.println(s.length); //test
			InetAddress ia = InetAddress.getLocalHost();
			DatagramPacket synPacket = new DatagramPacket(s, s.length);
			dSocket.send(synPacket);
			String synchronize = new String (synPacket.getData());
			System.out.println(synchronize);
			System.out.println("Packet sent");
			//receive SYNACK
			byte[] sa = new byte[1024];
			DatagramPacket synAckPacket = new DatagramPacket(sa, sa.length);
			System.out.println("Sender Code stops here");
			dSocket.receive(synAckPacket);
			System.out.println("Receive");
			String ACKString = new String(synAckPacket.getData());
			System.out.println(ACKString);
			try {
				synACK = Integer.parseInt(ACKString.trim());
			} catch (NumberFormatException e) {
				System.out.println("Can't parse");
			}
			System.out.println("after parseInt()");
			System.out.println(synACK);
			System.out.println("AFTER SYNACK");
		}catch(Exception error) {
			txtAreaConsole.append("Error: could not handshake");
		}
	}
	
	//thread to handle packet transferring
	public void transferThread() {

		transfer = new Thread() {
			public void run() {
				//----file transfer----
				//send initial packet
				int i = 0;
				while(send) {	
					try{
						//send pkt
						byte[] pkt = new byte[mds + 4];
						System.arraycopy(buffer[i], 0, pkt, 0, pkt.length);
						DatagramPacket dp = new DatagramPacket(pkt, pkt.length);
						dSocket.send(dp);
						//receive ACK
						byte[] a = new byte[4];
						DatagramPacket ap = new DatagramPacket(a, a.length);
						dSocket.receive(ap);
						String ackCheck = new String(ap.getData());
						int ackNum = Integer.parseInt(ackCheck);
						if(ackNum == 0) { //packet received
							i++;
						}
						if(ackNum == -1) { //EOT
							send = false;
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				time = System.currentTimeMillis() - time;

				//check if ack is 0 or 1 (good or bad packet)
				//then stop
				//if good send next pkt, if bad resend pkt
				//continue till end of file
				//send EOT
			}
		};
	} //end of transferThread
}