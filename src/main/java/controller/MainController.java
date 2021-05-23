/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;
import view.LoginView;
import view.RoleView;
import view.SenderView;

/**
 *
 * @author Admin
 */
public class MainController implements ActionListener {

	//Login
	private LoginView lgView;
	private JButton btnLogin;
	private JTextArea txtToken;
	private DbxClientV2 client;
	//Role
	private RoleView roleView;
	private JButton btnSender, btnReceiver;
	// Sender
	private SenderView sender;
	private JButton btnRegenerate, btnFileChooser, btnDeleteFile, btnConfirm, btnSend, btnInstruction, btnCheck;
	private JTable tblFile;
	private JTextField txtCode;
	private int code;
	private JFileChooser chooser;
	private List<File> files;
	private String path;

	public MainController() {

	}

	public void start() {
		initLogin();
		initRole();
	}

	private void initLogin() {
		lgView = new LoginView();
		btnLogin = lgView.getjButton1();
		txtToken = lgView.getjTextArea1();
		client = null;
		btnLogin.addActionListener(this);
		btnLogin.setActionCommand("btnLogin");
	}

	private void initRole() {
		roleView = new RoleView();
		btnSender = roleView.getjButton1();
		btnReceiver = roleView.getjButton3();

		btnSender.addActionListener(this);
		btnSender.setActionCommand("btnSender");

		btnReceiver.addActionListener(this);
		btnReceiver.setActionCommand("btnReceiver");
	}

	private void initSender() {
		sender = new SenderView();
		btnRegenerate = sender.getjButton1();
		btnRegenerate.addActionListener(this);
		btnRegenerate.setActionCommand("btnRegenerate");
		btnFileChooser = sender.getjButton2();
		btnFileChooser.addActionListener(this);
		btnFileChooser.setActionCommand("btnFileChooser");
		btnDeleteFile = sender.getjButton3();
		btnDeleteFile.addActionListener(this);
		btnDeleteFile.setActionCommand("btnDeleteFile");
		btnConfirm = sender.getjButton4();
		btnConfirm.addActionListener(this);
		btnConfirm.setActionCommand("btnConfirmSender");
		btnConfirm.setEnabled(false);
		btnSend = sender.getjButton5();
		btnSend.addActionListener(this);
		btnSend.setActionCommand("btnSend");
		btnSend.setEnabled(false);
		btnInstruction = sender.getjButton6();
		btnInstruction.addActionListener(this);
		btnInstruction.setActionCommand("btnInstructionSender");
		btnCheck = sender.getjButton7();
		btnCheck.addActionListener(this);
		btnCheck.setActionCommand("btnCheckSender");
		tblFile = sender.getjTable1();
		txtCode = sender.getjTextField1();
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		files = new ArrayList<File>();
		sender.setFocusable(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("btnLogin")) {
			String token = txtToken.getText();
			DbxRequestConfig config = DbxRequestConfig.newBuilder("sthidk").build();
			client = new DbxClientV2(config, token);
			try {
				client.files().listFolder("");
				lgView.dispose();
				roleView.setVisible(true);
			} catch (DbxException ex) {
				JOptionPane.showMessageDialog(lgView, "Token is invalid ! ", "Error", JOptionPane.WARNING_MESSAGE);
			}
		} else if (command.equals("btnSender")) {
			initSender();
			sender.setVisible(true);
			roleView.dispose();
		} else if (command.equals("btnReceiver")) {

		} else if (command.equals("btnRegenerate")) {
			Random r = new Random();
			code = Math.abs(r.nextInt()) % 1000;
			txtCode.setText(code + "");
		} else if (command.equals("btnFileChooser")) {
			chooser.showOpenDialog(sender);
			List<File> temp = Arrays.asList(chooser.getSelectedFiles());
			for (File f : temp) {
				if (!files.contains(f)) {
					files.add(f);
				}
			}
			DefaultTableModel model = (DefaultTableModel) tblFile.getModel();
			model.setRowCount(0);
			int i = 0;
			for (File f : files) {
				model.addRow(sender.fileToObject(++i, f));
			}
		} else if (command.equals("btnDeleteFile")) {
			DefaultTableModel model = (DefaultTableModel) tblFile.getModel();
			int index = tblFile.getSelectedRow();
			if (index == -1) {
				JOptionPane.showMessageDialog(lgView, "List is Empty ! ", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			model.removeRow(index);
			files.remove(index);

		} else if (command.equals("btnInstructionSender")) {
			String instruction
					= "1. Click Regenerate. Send code to Receiver. Let Receiver connected.\n"
					+ "2. Choose Files : Choose Files to Send.\n"
					+ "3. Check : Create Communication Channel.\n"
					+ "4. Confirm: Check if Receiver has Connected and Check Files.\n"
					+ "5. Send: Send file. Be Patient ! A Dialog will appear when it is finished !";
			JOptionPane.showMessageDialog(lgView, instruction, "Instruction", JOptionPane.INFORMATION_MESSAGE);
		} else if (command.equals("btnCheckSender")) {
			path = "/" + code;
			try {
				//Create Folder
				client.files().createFolderV2(path);
			} catch (DbxException ex) {
				JOptionPane.showMessageDialog(sender, "Code has been already used. Try Regenerate !", "Error", JOptionPane.WARNING_MESSAGE);
				ex.printStackTrace();
				return;
			}
			try {
				// Upload Sender Confirmation
				File tem = new File("SENDER_OK.txt");
				tem.createNewFile();
				InputStream in = new FileInputStream(tem);
				FileMetadata metadata = client.files().uploadBuilder(path+"/"+tem.getName()).uploadAndFinish(in);
				JOptionPane.showMessageDialog(sender, "Channel created successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
				btnRegenerate.setEnabled(false);
				btnCheck.setEnabled(false);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (DbxException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			}

		} else if (command.equals("btnConfirmSender")) {
			//Check if Receiver has connected
//			long timeLimit = 60000;
//			long timeStart = System.currentTimeMillis();
//			File temp = new File("RECEIVER_OK.txt");
//			while ((System.currentTimeMillis() - timeStart) < timeLimit) {
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException ex) {
//					// TODO Auto-generated catch block
//					ex.printStackTrace();
//				}
//				try {
//					if (Arrays.asList(client.files().listFolder("")).contains(temp)) {
//						JOptionPane.showMessageDialog(sender, "Receiver has Connected !", "Info", JOptionPane.INFORMATION_MESSAGE);
//						return;
//					}
//				} catch (DbxException ex) {
//					Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//				}
//			}
//			JOptionPane.showMessageDialog(sender, "Receiver has not Connected !", "Info", JOptionPane.INFORMATION_MESSAGE);
		} else if (command.equals("btnSend")) {

		}
	}
}
