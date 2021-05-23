/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.SearchV2Result;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import view.ReceiverView;
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
	private boolean role; // true : Sender , false : Receiver
	// Sender
	private SenderView sender;
	private JButton btnRegenerate, btnFileChooser, btnDeleteFile, btnConfirm, btnSend, btnInstruction, btnCheck,btnReset;
	private JTable tblFile;
	private JTextField txtCode;
	private int code = -1;
	private JFileChooser chooser;
	private List<File> files;
	private String path;
	//Receiver
	private ReceiverView receiver;
	private JButton btnDownload, btnGetList;
	private List<String> fileNames;

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
		btnReset = sender.getjButton8();
		btnReset.addActionListener(this);
		btnReset.setActionCommand("btnReset");

	}

	private void initReceiver() {
		receiver = new ReceiverView();
		btnConfirm = receiver.getjButton1();
		btnConfirm.addActionListener(this);
		btnConfirm.setActionCommand("btnConfirmReceiver");
		btnGetList = receiver.getjButton2();
		btnGetList.addActionListener(this);
		btnGetList.setActionCommand("btnGetList");
		btnDeleteFile = receiver.getjButton3();
		btnDeleteFile.addActionListener(this);
		btnDeleteFile.setActionCommand("btnDeleteFile");
		btnInstruction = receiver.getjButton4();
		btnInstruction.addActionListener(this);
		btnInstruction.setActionCommand("btnInstructionReceiver");
		btnDownload = receiver.getjButton5();
		btnDownload.addActionListener(this);
		btnDownload.setActionCommand("btnDownload");
		txtCode = receiver.getjTextField1();
		tblFile = receiver.getjTable1();
		btnReset = receiver.getjButton6();
		btnReset.addActionListener(this);
		btnReset.setActionCommand("btnReset");
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
			role = true;
			sender.setVisible(true);
			roleView.dispose();

		} else if (command.equals("btnReceiver")) {
			initReceiver();
			role = false;
			receiver.setVisible(true);
			roleView.dispose();

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
				JOptionPane.showMessageDialog(sender, "List is Empty ! ", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			model.removeRow(index);
			if (role) {
				files.remove(index);
			} else {
				fileNames.remove(index);
			}

		} else if (command.equals("btnInstructionSender")) {
			String instruction
					= sender.getInstruction();
			JOptionPane.showMessageDialog(sender, instruction, "Instruction", JOptionPane.INFORMATION_MESSAGE);

		} else if (command.equals("btnCheckSender")) {
			if(code==-1) {
				JOptionPane.showMessageDialog(sender, "Regenerate First !", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}
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
				File tem = new File("src/main/java/model/SENDER_OK.txt");
				tem.createNewFile();
				InputStream in = new FileInputStream(tem);
				FileMetadata metadata = client.files().uploadBuilder(path + "/" + tem.getName()).uploadAndFinish(in);
				JOptionPane.showMessageDialog(sender, "Channel created successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
				btnRegenerate.setEnabled(false);
				btnCheck.setEnabled(false);
				btnConfirm.setEnabled(true);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (DbxException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			}

		} else if (command.equals("btnConfirmSender")) {
//			Check if Receiver has connected
			btnConfirm.setEnabled(false);
			try {
				SearchV2Result r = client.files().searchV2Builder("RECEIVER_OK.txt").start();
				if (r.getMatches().size() > 0) {
					JOptionPane.showMessageDialog(sender, "Receiver has Connected !", "Info", JOptionPane.INFORMATION_MESSAGE);
					btnConfirm.setEnabled(false);
					btnSend.setEnabled(true);
					return;
				}
			} catch (DbxException ex) {
					
			}
			btnConfirm.setEnabled(true);
			JOptionPane.showMessageDialog(sender, "Receiver has not Connected !", "Info", JOptionPane.INFORMATION_MESSAGE);

		} else if (command.equals("btnSend")) {
			btnFileChooser.setEnabled(false);
			btnDeleteFile.setEnabled(false);
			InputStream in;
			for (File f : files) {
				try {
					in = new FileInputStream(f);
					FileMetadata metadata = client.files().uploadBuilder(path + "/" + f.getName()).uploadAndFinish(in);
					in.close();
				} catch (FileNotFoundException ex) {
					Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
				} catch (DbxException ex) {
					Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			JOptionPane.showMessageDialog(sender, "Files has been uploaded ! Get list and Download on Receiver.", "Info", JOptionPane.INFORMATION_MESSAGE);

		} else if (command.equals("btnConfirmReceiver")) {
			try {
				code = Integer.parseInt(txtCode.getText());
				path = "/" + code;
				client.files().listFolder(path);
				btnConfirm.setEnabled(false);
				txtCode.setEditable(false);
				File tem = new File("src/main/java/model/RECEIVER_OK.txt");
				tem.createNewFile();
				InputStream in = new FileInputStream(tem);
				FileMetadata metadata = client.files().uploadBuilder(path + "/" + tem.getName()).uploadAndFinish(in);
				JOptionPane.showMessageDialog(receiver, "Connected to Sender !", "Info", JOptionPane.INFORMATION_MESSAGE);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(receiver, "Code is not Vaild !", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			} catch (DbxException ex) {
				JOptionPane.showMessageDialog(receiver, "Sender has not Checked !", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			}

		} else if (command.equals("btnInstructionReceiver")) {
			String instruction
					= receiver.getInstruction();
			JOptionPane.showMessageDialog(receiver, instruction, "Instruction", JOptionPane.INFORMATION_MESSAGE);

		} else if (command.equals("btnGetList")) {
			try {
				fileNames = new ArrayList<String>();
				ListFolderResult list = client.files().listFolderBuilder(path).start();
				for (Metadata d : list.getEntries()) {
					if (d.getName().equals("SENDER_OK.txt") || d.getName().equals("RECEIVER_OK.txt")) {
						continue;
					}
					fileNames.add(d.getName());
				}
				DefaultTableModel model = (DefaultTableModel) tblFile.getModel();
				model.setRowCount(0);
				for (int i = 0; i < fileNames.size(); i++) {
					model.addRow(new Object[]{
						i + 1, fileNames.get(i)
					});
				}
			} catch (DbxException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			}

		} else if (command.equals("btnDownload")) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = fileChooser.showOpenDialog(receiver);
			File downloadPath;
			if (option == JFileChooser.APPROVE_OPTION) {
				downloadPath = fileChooser.getSelectedFile();
			} else {
				JOptionPane.showMessageDialog(receiver, "Canceled Download Process !", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			try {
				ListFolderResult list = client.files().listFolderBuilder(path).start();
				for (Metadata d : list.getEntries()) {
					if (fileNames.contains(d.getName())) {
						DbxDownloader<FileMetadata> downloader = client.files().download(d.getPathLower());
						FileOutputStream out = new FileOutputStream(downloadPath + "/" + d.getName());
						downloader.download(out);
						out.close();
					}
				}
			} catch (DbxException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
			}
			JOptionPane.showMessageDialog(receiver, "Files Downloaded Successfully !", "Info", JOptionPane.INFORMATION_MESSAGE);
			
		} else if (command.equals("btnReset")) {
			if(role) {
				if(code != -1) {
					try {
						client.files().deleteV2("/" + code);
					} catch (DbxException ex) {
						
					}
				}
				txtCode.setText("");
				btnRegenerate.setEnabled(true);
				btnCheck.setEnabled(true);
				btnConfirm.setEnabled(false);
				btnSend.setEnabled(false);
				btnFileChooser.setEnabled(true);
				btnDeleteFile.setEnabled(true);
				files = new ArrayList<File>();
			}
			else {
				txtCode.setText("");
				btnConfirm.setEnabled(true);
				fileNames.clear();
			}
			DefaultTableModel model = (DefaultTableModel) tblFile.getModel();
			model.setRowCount(0);
		}
	}
}
