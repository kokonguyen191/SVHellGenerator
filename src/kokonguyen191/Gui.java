package kokonguyen191;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;


public class Gui {

	private JFrame frmSvgenerator;
	private JTextField inputRate;
	private JTextField inputBPM;
	private File selectedFile = null;
	private File selectedFolder = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmSvgenerator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setFrmSvgenerator(new JFrame());
		getFrmSvgenerator().setTitle("SVGenerator");
		getFrmSvgenerator().setBounds(100, 100, 450, 300);
		getFrmSvgenerator().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getFrmSvgenerator().getContentPane().setLayout(null);
		
		//"select file" button
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBackground(UIManager.getColor("Button.background"));
		btnSelectFile.setFont(new Font("Arial", Font.ITALIC, 12));
		btnSelectFile.setBounds(37, 40, 118, 23);
		getFrmSvgenerator().getContentPane().add(btnSelectFile);
		
		//file label
		JLabel lblFile = new JLabel("File: ...");
		lblFile.setBounds(37, 74, 223, 14);
		getFrmSvgenerator().getContentPane().add(lblFile);
		
		//"select folder" button
		JButton btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.setFont(new Font("Arial", Font.ITALIC, 12));
		btnSelectFolder.setBounds(37, 99, 164, 23);
		getFrmSvgenerator().getContentPane().add(btnSelectFolder);
		
		//folder label
		JLabel lblFolder = new JLabel("Folder: ...");
		lblFolder.setBounds(37, 134, 223, 14);
		getFrmSvgenerator().getContentPane().add(lblFolder);
		
		//"generate" button
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setEnabled(false);
		btnGenerate.setFont(new Font("Arial", Font.BOLD, 12));
		btnGenerate.setBounds(317, 227, 107, 23);
		getFrmSvgenerator().getContentPane().add(btnGenerate);
		
		//"reset" button
		JButton btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Arial", Font.PLAIN, 12));
		btnReset.setBounds(10, 227, 77, 23);
		getFrmSvgenerator().getContentPane().add(btnReset);
		
		//rate label
		JLabel lblRate = new JLabel("RATE");
		lblRate.setFont(new Font("Arial", Font.PLAIN, 13));
		lblRate.setBounds(317, 62, 41, 14);
		getFrmSvgenerator().getContentPane().add(lblRate);
		
		//rate text field
		inputRate = new JTextField();
		inputRate.setFont(new Font("Arial", Font.PLAIN, 13));
		inputRate.setBounds(358, 59, 39, 20);
		getFrmSvgenerator().getContentPane().add(inputRate);
		inputRate.setColumns(10);
		
		PlainDocument rateDoc = new PlainDocument();
		rateDoc.setDocumentFilter(new DocumentFilter() {
		    @Override
		    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    } 
		    @Override
		    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		});
		inputRate.setDocument(rateDoc);
		
		//bpm label
		JLabel lblBpm = new JLabel("BPM");
		lblBpm.setFont(new Font("Arial", Font.PLAIN, 13));
		lblBpm.setBounds(317, 102, 41, 17);
		getFrmSvgenerator().getContentPane().add(lblBpm);
		
		//bpm text field
		inputBPM = new JTextField("Auto");
		inputBPM.setFont(new Font("Arial", Font.PLAIN, 13));
		inputBPM.setColumns(10);
		inputBPM.setBounds(358, 100, 39, 20);
		getFrmSvgenerator().getContentPane().add(inputBPM);
		
		/*
		PlainDocument bpmDoc = new PlainDocument();
		bpmDoc.setDocumentFilter(new DocumentFilter() {
		    @Override
		    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    } 
		    @Override
		    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		});
		inputBPM.setDocument(bpmDoc);
		*/
		
		//Status label
		JLabel lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
		lblStatus.setBounds(97, 214, 210, 23);
		frmSvgenerator.getContentPane().add(lblStatus);
		
		//"select file" button event listeners
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Osu map file", "osu");
				fileChooser.setFileFilter(filter);
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
					lblFile.setText(selectedFile.getName());
					btnSelectFolder.setEnabled(false);
					btnGenerate.setEnabled(true);
				}	
			}
		});
		
		//"select folder" button event listeners
		btnSelectFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					selectedFolder = fileChooser.getSelectedFile();
					lblFolder.setText(selectedFolder.getName());
					btnSelectFile.setEnabled(false);
					btnGenerate.setEnabled(true);
				}
			}
		});
		
		//"generate" button event listeners
		btnGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!inputRate.getText().equals("")) {
					int rate = Integer.parseInt(inputRate.getText());
					
					if(inputBPM.getText().equals("")) {
						
						//need to check if rate and bpm is number only
						
						if(selectedFile != null) {
							SVGenerate.runFile(selectedFile, rate);
						}
						else if(selectedFolder != null) {
							SVGenerate.runFolder(selectedFolder, rate);
						}
						lblStatus.setText("Complete! Auto-detected BPM.");
					}
					else {
						
						double BPM = Double.parseDouble(inputBPM.getText());
						if(selectedFile != null) {
							SVGenerateWithUninheritedPoints.runFile(selectedFile, rate, BPM);
						}
						else if(selectedFolder != null) {
							SVGenerateWithUninheritedPoints.runFolder(selectedFolder, rate, BPM);
						}
						lblStatus.setText("Complete!");
					}
				}
				else {
					lblStatus.setText("Rate undertermined.");
				}
			}
		});
		
		//"reset" button event listeners
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnSelectFile.setEnabled(true);
				btnSelectFolder.setEnabled(true);
				btnGenerate.setEnabled(false);
				lblFile.setText("File: ...");
				lblFolder.setText("Folder: ...");
				inputRate.setText(null);
				inputBPM.setText(null);
				selectedFile = null;
				lblStatus.setText("");
			}
		});
		
		////bpm text field event listeners
		inputBPM.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(inputBPM.getText().equals("Auto")) {
					inputBPM.setText("");
				}
			}
		});
		inputBPM.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if(inputBPM.getText().equals("")) {
					inputBPM.setText("Auto");
				}
			}
		});
		
	}

	public JFrame getFrmSvgenerator() {
		return frmSvgenerator;
	}

	public void setFrmSvgenerator(JFrame frmSvgenerator) {
		this.frmSvgenerator = frmSvgenerator;
	}
}
