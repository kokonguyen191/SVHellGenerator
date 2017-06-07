package kokonguyen191;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class OsuSVHellGui {

	// Main body
	private JFrame frmSvgenerator = new JFrame();
	private JPanel mainPanel = new JPanel();
	private JLayeredPane lp = frmSvgenerator.getLayeredPane();
	// File and folder selection
	private JButton btnSelectFile, btnSelectFolder;
	private JLabel lblFile, lblFolder;
	private File selectedFile, selectedFolder;
	private String lastDirectory = null;
	// Operation buttons
	private JButton btnReset, btnGenerate;
	// Parameter buttons
	private JLabel lblRate, lblDefaultRate, lblBpm, lblDefaultBpm, lblSnap, lblDefaultSnap;
	private JTextField inputRate, inputBpm, inputSnap;
	// Uninherited timing points toggle
	private JCheckBox uninheritedCheckbox;
	private JLabel lblUninherited;
	// Status label
	private JLabel lblStatus;
	// Log
	private JTextArea textArea;
	private JPanel logPanel = new JPanel();

	/**
	 * Constructor
	 */
	public OsuSVHellGui() {
		frameInitialize();
		fileAndFolderButtons();
		operationsButtons();
		parameterButtons();
		parameterButtonsInputFilter();
		uninheritedToggle();
		statusLabel();
		logOutput();
	}

	/**
	 * Frame getter
	 * 
	 * @return
	 */
	public JFrame getFrmSvgenerator() {
		return frmSvgenerator;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void frameInitialize() {

		// Frame initialization
		frmSvgenerator.setTitle("SVGenerator");
		frmSvgenerator.setBounds(100, 100, 508, 630);
		frmSvgenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSvgenerator.setResizable(false);
		// Main panel initialization
		mainPanel.setLayout(null);
		mainPanel.setSize(500, 300);
		// Log panel initialization
		logPanel.setLayout(new BorderLayout());
		logPanel.setSize(500, 300);
		logPanel.setLocation(0, 300);
		// Align two panel
		lp.add(mainPanel, Integer.valueOf(1));
		lp.add(logPanel, Integer.valueOf(2));

	}

	/**
	 * File and folder selection buttons
	 */
	private void fileAndFolderButtons() {
		// "select file" button
		btnSelectFile = new JButton("Select File");
		btnSelectFile.setBackground(UIManager.getColor("Button.background"));
		btnSelectFile.setFont(new Font("Arial", Font.ITALIC, 12));
		btnSelectFile.setBounds(37, 40, 118, 23);
		mainPanel.add(btnSelectFile);

		// file label
		lblFile = new JLabel("File: ...");
		lblFile.setBounds(37, 74, 223, 14);
		mainPanel.add(lblFile);

		// "select folder" button
		btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.setFont(new Font("Arial", Font.ITALIC, 12));
		btnSelectFolder.setBounds(37, 99, 164, 23);
		mainPanel.add(btnSelectFolder);

		// folder label
		lblFolder = new JLabel("Folder: ...");
		lblFolder.setBounds(37, 134, 223, 14);
		mainPanel.add(lblFolder);

		// "select file" button event listeners
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				JFileChooser fileChooser = new JFileChooser();
				if (lastDirectory == null) {
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				} else {
					fileChooser.setCurrentDirectory(new File(lastDirectory));
				}
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Osu beatmap (.osu)", "osu");
				fileChooser.setFileFilter(filter);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
					lblFile.setText("File: " + selectedFile.getName());
					btnGenerate.setEnabled(true);
					lastDirectory = selectedFile.getParent();
					// Clear Folder selection
					lblFolder.setText(null);
				}
			}
		});

		// "select folder" button event listeners
		btnSelectFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				JFileChooser fileChooser = new JFileChooser();
				if (lastDirectory == null) {
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				} else {
					fileChooser.setCurrentDirectory(new File(lastDirectory));
				}
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFolder = fileChooser.getSelectedFile();
					lblFolder.setText("Folder: " + selectedFolder.getName());
					btnGenerate.setEnabled(true);
					lastDirectory = selectedFolder.getPath();
					// Clear File selection
					lblFile.setText(null);
				}
			}
		});
	}

	/**
	 * Clear button and generation button
	 */
	private void operationsButtons() {
		// "generate" button
		btnGenerate = new JButton("Generate");
		btnGenerate.setEnabled(false);
		btnGenerate.setFont(new Font("Arial", Font.BOLD, 12));
		btnGenerate.setBounds(317, 227, 107, 23);
		mainPanel.add(btnGenerate);

		// "reset" button
		btnReset = new JButton("Clear");
		btnReset.setFont(new Font("Arial", Font.PLAIN, 12));
		btnReset.setBounds(10, 227, 77, 23);
		mainPanel.add(btnReset);

		// "generate" button event listeners
		btnGenerate.addMouseListener(new MouseAdapter() {

			int rate = -100;
			double snap = -100.0;
			double bpm = -100.0;
			boolean autoBpm;

			boolean canPress;

			@Override
			public void mouseEntered(MouseEvent e) {
				// Get rate
				if (inputRate.getText().isEmpty()) {
					rate = 8;
				} else {
					rate = Integer.parseInt(inputRate.getText());
				}

				// Get tempo
				if (inputBpm.getText().isEmpty()) {
					autoBpm = true;
				} else {
					autoBpm = false;
					bpm = Double.parseDouble(inputBpm.getText());
				}

				// Get snap
				if (inputSnap.getText().isEmpty()) {
					snap = -1;
				} else {
					snap = Double.parseDouble(inputSnap.getText());
				}

				if ((selectedFile != null || selectedFolder != null) && rate != -100
						&& (!uninheritedCheckbox.isSelected() || (uninheritedCheckbox.isSelected() && snap != -100.0))
						&& (autoBpm || (!autoBpm && bpm != -100.0))) {
					canPress = true;
				} else {
					canPress = false;
				}

				btnGenerate.setEnabled(canPress);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
				// Clear log
				textArea.setText(null);
				
				StopWatch sw = new StopWatch();
				sw.reset();
				sw.start();
				// File in slot
				if (selectedFile != null) {
					// Auto detect tempo
					if (autoBpm) {
						Generator.readFile(selectedFile, new SVGenerate(), snap, rate);
						lblStatus.setText("SV Hell successfully generated.");
					} else {
						Generator.readFile(selectedFile, new SVGenerate(), snap, rate, bpm);
						lblStatus.setText("Auto detected BPM. SV Hell successfully generated.");
					}
				} else if (selectedFolder != null) { // Folder in slot
					Generator.readFolder(selectedFolder, new SVGenerateWithUninheritedPoints(), snap, rate);
					lblStatus.setText("All SV Hell generated.");
				}
				sw.stop();
				System.out.println("=============================================================");
				System.out.println("=============================================================");
				System.out.println("=============================================================");
				System.out.println("=============================================================");
				System.out.println("=============================================================");
				System.out.println("=============================================================");
				System.out.println("All SV Hells generated in " + sw.getTime() + "s.");
			}
		});

		// "reset" button event listeners
		btnReset.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				btnSelectFile.setEnabled(true);
				btnSelectFolder.setEnabled(true);
				btnGenerate.setEnabled(false);
				lblFile.setText("File: ...");
				lblFolder.setText("Folder: ...");
				inputRate.setText(null);
				inputBpm.setText(null);
				selectedFile = null;
				lblStatus.setText("");
			}
		});
	}

	/**
	 * SV rate, tempo, and snap divisor
	 */
	private void parameterButtons() {
		// rate label
		lblRate = new JLabel("RATE");
		lblRate.setFont(new Font("Arial", Font.PLAIN, 13));
		lblRate.setBounds(267, 60, 40, 20);
		mainPanel.add(lblRate);

		// rate text field
		inputRate = new JTextField();
		inputRate.setFont(new Font("Arial", Font.PLAIN, 13));
		inputRate.setColumns(10);
		inputRate.setBounds(308, 60, 40, 20);
		mainPanel.add(inputRate);

		// Default rate label
		lblDefaultRate = new JLabel("Default: 8");
		lblDefaultRate.setFont(new Font("Arial", Font.PLAIN, 13));
		lblDefaultRate.setBounds(357, 60, 120, 20);
		mainPanel.add(lblDefaultRate);

		// bpm label
		lblBpm = new JLabel("BPM");
		lblBpm.setFont(new Font("Arial", Font.PLAIN, 13));
		lblBpm.setBounds(267, 100, 40, 20);
		mainPanel.add(lblBpm);

		// bpm text field
		inputBpm = new JTextField();
		inputBpm.setFont(new Font("Arial", Font.PLAIN, 13));
		inputBpm.setColumns(10);
		inputBpm.setBounds(308, 100, 40, 20);
		mainPanel.add(inputBpm);

		// Default BPM label
		lblDefaultBpm = new JLabel("Default: Auto-detect");
		lblDefaultBpm.setFont(new Font("Arial", Font.PLAIN, 13));
		lblDefaultBpm.setBounds(357, 100, 120, 20);
		mainPanel.add(lblDefaultBpm);

		// Snap divisor label
		lblSnap = new JLabel("SNAP");
		lblSnap.setFont(new Font("Arial", Font.PLAIN, 13));
		lblSnap.setBounds(267, 140, 40, 20);
		mainPanel.add(lblSnap);

		// Snap divisor text field
		inputSnap = new JTextField();
		inputSnap.setFont(new Font("Arial", Font.PLAIN, 13));
		inputSnap.setColumns(10);
		inputSnap.setBounds(308, 140, 40, 20);
		mainPanel.add(inputSnap);

		// Default snap divisor label
		lblDefaultSnap = new JLabel("Default: All notes");
		lblDefaultSnap.setFont(new Font("Arial", Font.PLAIN, 13));
		lblDefaultSnap.setBounds(357, 140, 120, 20);
		mainPanel.add(lblDefaultSnap);

	}

	/**
	 * Filter what you can input in the parameter boxes
	 */
	private void parameterButtonsInputFilter() {
		// Only allow number input
		DocumentFilter onlyDouble = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
					throws BadLocationException {
				String regExp;
				Document doc = fb.getDocument();
				if (doc.getText(0, doc.getLength()).indexOf(".") == -1) {
					regExp = "[^0-9.]";
				} else {
					regExp = "[^0-9]";
				}
				fb.insertString(off, str.replaceAll(regExp, ""), attr);
			}

			@Override
			public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
					throws BadLocationException {
				String regExp;
				Document doc = fb.getDocument();
				if (doc.getText(0, doc.getLength()).indexOf(".") == -1) {
					regExp = "[^0-9.]";
				} else {
					regExp = "[^0-9]";
				}
				fb.replace(off, len, str.replaceAll(regExp, ""), attr);
			}
		};

		DocumentFilter onlyInt = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
					throws BadLocationException {
				fb.insertString(off, str.replaceAll("[^0-9]", ""), attr);
			}

			@Override
			public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
					throws BadLocationException {
				fb.replace(off, len, str.replaceAll("[^0-9]", ""), attr);
			}
		};

		PlainDocument rateDoc = new PlainDocument();
		PlainDocument bpmDoc = new PlainDocument();
		PlainDocument snapDoc = new PlainDocument();
		rateDoc.setDocumentFilter(onlyInt);
		bpmDoc.setDocumentFilter(onlyDouble);
		snapDoc.setDocumentFilter(onlyDouble);
		inputRate.setDocument(rateDoc);
		inputBpm.setDocument(bpmDoc);
		inputSnap.setDocument(snapDoc);
	}

	/**
	 * Uninherited timing points toggle
	 */
	private void uninheritedToggle() {
		// Uninherited timing points checkbox
		uninheritedCheckbox = new JCheckBox();
		uninheritedCheckbox.setBounds(267, 170, 20, 20);
		mainPanel.add(uninheritedCheckbox);

		// Uninherited timing points label
		lblUninherited = new JLabel("<html>Generate uninherited<br></br>timing points?</html>");
		lblUninherited.setFont(new Font("Arial", Font.PLAIN, 13));
		lblUninherited.setBounds(290, 160, 200, 50);
		mainPanel.add(lblUninherited);

		// Hide snap options
		lblDefaultSnap.setVisible(false);
		inputSnap.setVisible(false);
		lblSnap.setVisible(false);

		// Event listener
		uninheritedCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (uninheritedCheckbox.isSelected()) {
					lblDefaultSnap.setVisible(true);
					inputSnap.setVisible(true);
					lblSnap.setVisible(true);
				} else {
					lblDefaultSnap.setVisible(false);
					inputSnap.setVisible(false);
					lblSnap.setVisible(false);
				}
			}
		});
	}

	/**
	 * Status label
	 */
	private void statusLabel() {
		// Status label
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
		lblStatus.setBounds(97, 214, 210, 23);
		mainPanel.add(lblStatus);
	}

	private void logOutput() {

		textArea = new JTextArea(18, 40);
		TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea);
		logPanel.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		textArea.setBounds(300, 300, 500, 300);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		System.setOut(new PrintStream(taOutputStream));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OsuSVHellGui window = new OsuSVHellGui();
					window.getFrmSvgenerator().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
