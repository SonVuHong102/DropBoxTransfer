/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
	private JButton btnRegenerate, btnFileChooser, btnDeleteFile, btnConfirm, btnSend, btnInstruction;
	private JTable tblFile;
	private JTextField txtCode;
	private int code;
	private JFileChooser chooser;
	private List<File> files;

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
		btnSend = sender.getjButton5();
		btnSend.addActionListener(this);
		btnSend.setActionCommand("btnSend");
		btnInstruction = sender.getjButton6();
		btnInstruction.addActionListener(this);
		btnInstruction.setActionCommand("btnInstructionSender");
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
			txtToken.setText("sl.AxVCTSWb7_m6Oqk8AlC1E3gZqWG61llXN9y7wFFQYIDbNOz6KmWWtG8Q2OzbjrcDtsubRX3D3B-VH0hcCCun0Xi0PtXaGRFQOGKH1SL9nqX18k1-bFbmKV_xw5sLIToIKAKp09w67Etw");
			String token = txtToken.getText();
			DbxRequestConfig config = DbxRequestConfig.newBuilder("sthidk").build();
			DbxClientV2 client = new DbxClientV2(config, token);
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
			String instruction = 
					"1. Generate a code to comunicate between Sender and Receiver.\n"
					+ "2. Choose Files to Send.\n"
					+ "3. Confirm. If Success, Send code to Receiver. Cofirm on Receiver side.\n"
					+ "4. Send. Then be Patient ! A Dialog will appear when it is finished !";
			JOptionPane.showMessageDialog(lgView, instruction, "Instruction", JOptionPane.INFORMATION_MESSAGE);
		} else if (command.equals("btnConfirmSender")) {
			
		}
	}
}
