import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Erstellt die grafische Benutzeroberfläche.
 * 
 * @author Tobias Heuser
 * */
public class FAGUI extends JFrame{

	/**
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final Logger log = Logger.getLogger(FAGUI.class.getName());
	
	
	private File f = null;
	private static double guete;
	
	private JTextArea statusAnzeige;
	private static JTextArea loggingArea;
	protected static Handler handler;
	
	
	/**
	 * main-Methode
	 * */
	public static void main(String[] args) {
		FAGUI gui = new FAGUI();
		
		gui.create();
	}
	
	
	
	/**
	 * Konstruktor
	 * */
	public FAGUI() {
		super("Facharbeit Kurs Wahlomat");
		
		loggingArea = new JTextArea();
		handler = new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				loggingArea.append(record.getLevel() + ":" + record.getSourceClassName() + ":" + record.getSourceMethodName() + ": "
						+ record.getMessage() + "\n");
				loggingArea.setCaretPosition(loggingArea.getDocument().getLength());
			}
			
			@Override
			public void flush() {}
			
			@Override
			public void close() throws SecurityException {}
		};
		log.addHandler(handler);
		statusAnzeige = new JTextArea();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		
	}
	
	
	/**
	 * Erstellt die Benutzeroberfläche
	 * */
	public void create() {
		
		// Center Panel
		final JTabbedPane centerPanel = new JTabbedPane();
		final JPanel panelSchuelerEdit = createPanelSchuelerEdit();
		centerPanel.add(panelSchuelerEdit, "Wahlen");
		centerPanel.setMnemonicAt(0, KeyEvent.VK_W);
		final JPanel panelKursEdit = createPanelKursEdit();
		centerPanel.add(panelKursEdit, "Gewählte Kurse");
		centerPanel.setMnemonicAt(1, KeyEvent.VK_K);
		final  JPanel panelVerteilteSchueler = createPanelVerteilteSchueler();
		centerPanel.add(panelVerteilteSchueler, "Verteilung (Schüler)");
		centerPanel.setMnemonicAt(2, KeyEvent.VK_V);
		final JPanel panelVerteilteKurse = createPanelVerteilteKurse();
		centerPanel.add(panelVerteilteKurse, "Kurszuordnung");
		centerPanel.setMnemonicAt(3, KeyEvent.VK_Z);
		add(centerPanel, BorderLayout.CENTER);
		// End Center Panel
		
		// South Panel
		final JPanel southPanel = new JPanel(new BorderLayout());
		loggingArea.setRows(5);
		loggingArea.setEditable(false);
		loggingArea.setWrapStyleWord(true);
		southPanel.add(new JScrollPane(loggingArea), BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		// End South Panel
		
		// West Panel
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new FlowLayout());
		
		
		final JButton btnImportCSV = new JButton("Import CSV");
		
		
		westPanel.setPreferredSize(new Dimension(150, 600));
		JButton btnNew = new JButton("Neu");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter(".ser Datei", "ser"));
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = fileChooser.getSelectedFile();
					if (!f.exists()) {
//						f.mkdirs();
						try {
							f.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					centerPanel.removeAll();
					
					FASchueler.setInstanzenCache(new HashMap<String, FASchueler>());
					FAKurs.setInstanzenCache(new HashMap<String, FAKurs>());
							
					centerPanel.add(createPanelSchuelerEdit(), "Wahlen");
					centerPanel.setMnemonicAt(0, KeyEvent.VK_W);
					centerPanel.add(createPanelKursEdit(), "Gewählte Kurse");
					centerPanel.setMnemonicAt(1, KeyEvent.VK_K);
					centerPanel.add(createPanelVerteilteSchueler(), "Verteilung (Schüler)");
					centerPanel.setMnemonicAt(2, KeyEvent.VK_V);
					centerPanel.add(createPanelVerteilteKurse(), "Kurszuordnung");
					centerPanel.setMnemonicAt(3, KeyEvent.VK_Z);
					statusAnzeige.setText("");
					repaint();
				}
			}
		});
		westPanel.add(btnNew);
		JButton btnOpen = new JButton("Öffnen");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter(".ser Datei", "ser"));
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = fileChooser.getSelectedFile();
					if (!f.exists()) {
//						f.mkdirs();
						try {
							f.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					centerPanel.removeAll();
					
					FASchueler.setInstanzenCache(new HashMap<String, FASchueler>());
					FAKurs.setInstanzenCache(new HashMap<String, FAKurs>());
					
					centerPanel.add(createPanelSchuelerEdit(), "Wahlen");
					centerPanel.setMnemonicAt(0, KeyEvent.VK_W);
					centerPanel.add(createPanelKursEdit(), "Gewählte Kurse");
					centerPanel.setMnemonicAt(1, KeyEvent.VK_K);
					centerPanel.add(createPanelVerteilteSchueler(), "Verteilung (Schüler)");
					centerPanel.setMnemonicAt(2, KeyEvent.VK_V);
					centerPanel.add(createPanelVerteilteKurse(), "Kurszuordnung");
					centerPanel.setMnemonicAt(3, KeyEvent.VK_Z);
					btnImportCSV.setEnabled(false);
					statusAnzeige.setText("");
					repaint();
				}
			}
		});
		westPanel.add(btnOpen);
		
		btnImportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf = new JFileChooser();
				jf.setFileFilter(new FileNameExtensionFilter("CSV Eingabedatei", "csv"));
				if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = jf.getSelectedFile();
					if (!file.exists()) {
//						file.mkdirs();
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					FADateiEinAusgabe.lese(file.getAbsolutePath());
					speichern();
					FASchueler.bearbeitet();
					statusAnzeige.setText("");
				}
			}
		});
		westPanel.add(btnImportCSV);
		
		JButton btnExportCSV = new JButton("Export als CSV");
		btnExportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf = new JFileChooser();
				jf.setFileFilter(new FileNameExtensionFilter("CSV Ausgabedatei", "csv"));
				if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = jf.getSelectedFile();
					if (!file.exists()) {
//						file.mkdirs();
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					FADateiEinAusgabe.schreibe(file.getAbsolutePath(), guete);
				}
			}
		});
		westPanel.add(btnExportCSV);
		
		JButton btnAssign = new JButton("Wahlen verteilen");
		btnAssign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final int iterationen = 50; // durch JTextField variabel
				statusAnzeige.setText("Führe " + iterationen + " Verteilungsdurchläufe aus");
				setComponentsEnabled(getContentPane(), false);
				
				SwingWorker<String, Void> myWorker = new SwingWorker<String, Void>() {
					@Override
					protected String doInBackground() throws Exception {
						guete = FAKursWahlomat.verteile(iterationen);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
//								setExistiertVerteilung(true);
//								setVerteilungAktuell(true);
								speichern();
								centerPanel.remove(3);
								centerPanel.remove(2);
								centerPanel.add(createPanelVerteilteSchueler(), "Verteilung (Schüler)");
								centerPanel.add(createPanelVerteilteKurse(), "Kurszuordnung");
								centerPanel.setSelectedIndex(2);
								statusAnzeige.setText("Verteilung mit der Güte " + (new DecimalFormat("#.##")).format(guete) + " abgeschlossen");
								setComponentsEnabled(getContentPane(), true);
							}
						});
						return null;
					}
				};
				myWorker.execute();	
						
						
				
				
			}
		});
		westPanel.add(btnAssign);
		
		statusAnzeige.setEditable(false);
		statusAnzeige.setLineWrap(true);
		statusAnzeige.setWrapStyleWord(true);
		westPanel.add(statusAnzeige);
		
		JButton btnLoggingToClipboard = new JButton("<html>Logging in<br>Zwischenablage</html>");
		btnLoggingToClipboard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(loggingArea.getText()), null);
			}
		});
		westPanel.add(btnLoggingToClipboard);
		
		JButton btnExit = new JButton("Beenden");
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(NORMAL);;
			}
		});
		westPanel.add(btnExit);
		
		Dimension dim = btnAssign.getPreferredSize();
		btnNew.setPreferredSize(dim);
		btnOpen.setPreferredSize(dim);
		btnImportCSV.setPreferredSize(dim);
		btnExportCSV.setPreferredSize(dim);
		statusAnzeige.setPreferredSize(new Dimension(dim.width, dim.height*2));
		btnLoggingToClipboard.setPreferredSize(new Dimension(dim.width, dim.height*2));
		btnExit.setPreferredSize(dim);
		
		add(westPanel, BorderLayout.WEST);
		// End West Panel
				
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Erstellt das Panel, in dem die Wahlen der Schüler bearbeitet werden können.
	 * */
	private JPanel createPanelSchuelerEdit() {
		
		
		final JTextField fieldName = new JTextField(8),
						 fieldVorname = new JTextField(8);
		final JCheckBox box = new JCheckBox("Projektkurs");
		final JTextField[] fields = new JTextField[6];
		final JLabel[] labels = new JLabel[6];
		final JPanel cISInnerSouthPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JLabel labelError;
		final JButton btnAdd = new JButton("Schüler hinzufügen"),
					  btnSchuelerSave = new JButton("Übernehmen"),
					  btnSchuelerCancel = new JButton("Abbrechen"),
					  btnSchuelerDelete = new JButton("Schüler löschen");
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		final DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		
		final String[] columnHeaders = {"Name", "Vorname", "1. Fach", "Lehrer", "2. Fach", "Lehrer", "3. Fach", "Lehrer"};
		
//		Notfall Save/Load Dialog
//		FADateiEinAusgabe.lese("C:\\Users\\Tobias\\Documents\\FAKursWahlomat\\E-MailAnhangTHeuser\\new.csv");
		
//		final File f = new File("C:\\Users\\Tobias\\Documents\\FAKursWahlomat\\E-MailAnhangTHeuser\\test_save.ser");
//		f.mkdirs();
//		try {
//			f.createNewFile();
//		} catch (IOException e1) {}
		FASerializer.load(f);
		// End Notfall Save/Load Dialog
		
		
		String[][] rowData = new String[FASchueler.getAnzahlInstanzen()][columnHeaders.length];
		ArrayList<FASchueler> alleSchueler = new ArrayList<FASchueler>(FASchueler.getAlleInstanzen());
		System.out.println("Anzahl Instanzen: " + FASchueler.getAnzahlInstanzen() + " 'Alle':" + alleSchueler.size());
		Collections.sort(alleSchueler);
		int count = 0;
		for (FASchueler schueler : alleSchueler) {
			rowData[count][0] = FAHelper.macheLesbar(schueler.getName());
			rowData[count][1] = FAHelper.macheLesbar(schueler.getVorname());
			FAWahlZettel wz = schueler.getWahlZettel();
			if (schueler.schreibtFacharbeit()) {
				FAKurs kurs = wz.getKurs(1);
				rowData[count][2] = FAHelper.macheLesbar(kurs.getFach());
				rowData[count][3] = FAHelper.macheLesbar(kurs.getLehrer());
				kurs = wz.getKurs(2);
				rowData[count][4] = FAHelper.macheLesbar(kurs.getFach());
				rowData[count][5] = FAHelper.macheLesbar(kurs.getLehrer());
				kurs = wz.getKurs(3);
				rowData[count][6] = FAHelper.macheLesbar(kurs.getFach());
				rowData[count][7] = FAHelper.macheLesbar(kurs.getLehrer());
			} else {
				rowData[count][2] = "Projektkurs";
			}
			count++;
		}
		
		model.setDataVector(rowData, columnHeaders);
		
		final JTable table = new JTable(model);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					 JTable target = (JTable)e.getSource();
			         int row = target.getSelectedRow();
			         
			         
			         final String tempName = (String) table.getValueAt(row, 0);
			         final String tempVorname = (String) table.getValueAt(row, 1);
			         final String temp1 = (String) table.getValueAt(row, 2);
			         
			         final boolean bool = !temp1.equals("Projektkurs");
			         
			         final FASchueler schuelerOriginal = FASchueler.instanzFuer(tempName, tempVorname, bool);
			         
			         fieldName.setText(schuelerOriginal.getName());
			         fieldName.setEnabled(false);
			         fieldVorname.setText(schuelerOriginal.getVorname());
			         fieldVorname.setEnabled(false);
			         box.setSelected(!bool);
			         box.setEnabled(false);
			         
			         for (int i = 0; i < labels.length; i++) {
							labels[i].setEnabled(bool);
							fields[i].setEnabled(bool);
					 }
			         
			         if (bool) {
			        	 for (int i = 0; i < 3; i++) {
			        		 FAKurs kurs = schuelerOriginal.getWahlZettel().getKurs(i+1);
			        		 fields[i*2].setText(kurs.getFach());
			        		 fields[i*2+1].setText(kurs.getLehrer());
			        	 }
			         } else {
			        	 for (int i = 0; i < labels.length; i++) {
								fields[i].setText("");
						 }
			         }
			         
			         cISInnerSouthPanel.removeAll();
			         
			         for (ActionListener listener : btnSchuelerSave.getActionListeners()) {
			        	 btnSchuelerSave.removeActionListener(listener);
			         }
			         
			         btnSchuelerSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if (bool) {
								FAWahlZettel wz = schuelerOriginal.getWahlZettel();
								System.out.println("Fach: '" + wz.getKurs(1).getFach() + "', neu: '" + fields[0].getText());
								if (!fields[0].getText().equals(wz.getKurs(1).getFach()) || !fields[1].getText().equals(wz.getKurs(1).getLehrer())) {
									wz.removeWahl(1);
									wz.addWahl(1, FAKurs.instanzFuer(fields[0].getText(), fields[1].getText()));
								}
								if (!fields[2].getText().equals(wz.getKurs(2).getFach()) || !fields[3].getText().equals(wz.getKurs(2).getLehrer())) {
									wz.removeWahl(2);
									wz.addWahl(2, FAKurs.instanzFuer(fields[2].getText(), fields[3].getText()));
								}
								if (!fields[4].getText().equals(wz.getKurs(3).getFach()) || !fields[5].getText().equals(wz.getKurs(3).getLehrer())) {
									wz.removeWahl(3);
									wz.addWahl(3, FAKurs.instanzFuer(fields[4].getText(), fields[5].getText()));
								}
							}
							
							speichern();
							FASchueler.bearbeitet();
							statusAnzeige.setText(FASchueler.getAnzahlInstanzen() + " Schüler. Erneute Verteilung notwendig.");
							
							fieldName.setText("");
					        fieldVorname.setText("");
					         
					        box.setSelected(false);
					         
					        for (int i = 0; i < labels.length; i++) {
								labels[i].setEnabled(true);
								fields[i].setText("");
								fields[i].setEnabled(true);
							}
					         
					        fieldName.setEnabled(true);
					        fieldVorname.setEnabled(true);
					        box.setEnabled(true);
					       
					        cISInnerSouthPanel.removeAll();
					        cISInnerSouthPanel.add(btnAdd);
					        cISInnerSouthPanel.repaint();
						}
			         });
			         cISInnerSouthPanel.add(btnSchuelerSave);
			         
			         btnSchuelerCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							 fieldName.setText("");
					         fieldVorname.setText("");
					         
					         box.setSelected(false);
					         
					         for (int i = 0; i < labels.length; i++) {
									labels[i].setEnabled(true);
									fields[i].setText("");
									fields[i].setEnabled(true);
							 }
					         
					         fieldName.setEnabled(true);
						     fieldVorname.setEnabled(true);
						     box.setEnabled(true);
					         
					         cISInnerSouthPanel.removeAll();
					         cISInnerSouthPanel.add(btnAdd);
					         cISInnerSouthPanel.repaint();
						}
					});
			         cISInnerSouthPanel.add(btnSchuelerCancel);
			         
			         //  Action Listener nur einmal hinzufügen und Löschoperation entfernen!!
			         for (ActionListener listener : btnSchuelerDelete.getActionListeners()) {
			        	 btnSchuelerDelete.removeActionListener(listener);
			         }
			         btnSchuelerDelete.addActionListener(new ActionListener() {
			        	 public void actionPerformed(ActionEvent arg0) {
			        		 if (JOptionPane.showConfirmDialog(null, "'" + schuelerOriginal.getName() + ", " + schuelerOriginal.getVorname() + "' wirklich löschen?") == JOptionPane.YES_OPTION) {
			        			 FASchueler.entferneInstanzFuer(schuelerOriginal);
			        			 fieldName.setText("");
			        			 fieldVorname.setText("");
						        
			        			 box.setSelected(false);
						         
			        			 for (int i = 0; i < labels.length; i++) {
			        				 labels[i].setEnabled(true);
			        				 fields[i].setText("");
			        				 fields[i].setEnabled(true);
			        			 }
						        
			        			 fieldName.setEnabled(true);
			        			 fieldVorname.setEnabled(true);
			        			 box.setEnabled(true);
					         
						         cISInnerSouthPanel.removeAll();
						         cISInnerSouthPanel.add(btnAdd);
						         cISInnerSouthPanel.repaint();
							}
							speichern();
							statusAnzeige.setText(FASchueler.getAnzahlInstanzen() + " Schüler. Erneute Verteilung notwendig.");
						}
			         });
			         cISInnerSouthPanel.add(btnSchuelerDelete);
			         
			         repaint();
				}
			}
		});
		
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		// Center Inner South Panel
		JPanel centerInnerSouthPanel = new JPanel(new BorderLayout());
		
		// - Center Inner South Inner Center Panel
		JPanel cISInnerCenterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelName = new JLabel("Name:");
		cISInnerCenterPanel.add(labelName);
		cISInnerCenterPanel.add(fieldName);
		JLabel labelVorname = new JLabel("Vorname:");
		cISInnerCenterPanel.add(labelVorname);
		cISInnerCenterPanel.add(fieldVorname);
		
		cISInnerCenterPanel.add(FAHelper.sep());
		
		cISInnerCenterPanel.add(box);
		
		cISInnerCenterPanel.add(FAHelper.sep());
		
		for (int i = 0; i < 3; i++) {
			int j = i*2;
			labels[j] = new JLabel((i+1) + ". Fach");
			fields[j] = new JTextField(2);
			labels[j+1] = new JLabel((i+1) + ". Lehrer");
			fields[j+1] = new JTextField(2);
		}
		
		for (int i = 0; i< 6; i++) {
			cISInnerCenterPanel.add(labels[i]);
			cISInnerCenterPanel.add(fields[i]);
		}

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean bool = !box.isSelected();
				for (int i = 0; i < labels.length; i++) {
					labels[i].setEnabled(bool);
					fields[i].setEnabled(bool);
				}
			}
		});
		
		centerInnerSouthPanel.add(cISInnerCenterPanel, BorderLayout.CENTER);
		// - End Center Inner South Inner Center Panel

		// - Center Inner South Inner South Panel
		labelError = new JLabel();
		cISInnerSouthPanel.add(labelError);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fieldName.getText().isEmpty()) {
					labelError.setText("'Name' ist leer");
					System.out.println("Name ist leer");
					return;
				} else if (fieldVorname.getText().isEmpty()) {
					labelError.setText("'Vorname' ist leer");
					System.out.println("Vorname ist leer");
					return;
				} else if (box.isSelected()) {
					FASchueler.instanzFuer(fieldName.getText(), fieldVorname.getText(), null);
					speichern();
					statusAnzeige.setText(FASchueler.getAnzahlInstanzen() + " Schüler. Erneute Verteilung notwendig.");
				} else {
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].getText().isEmpty()) {
							System.out.println("Feld " + i + " ist leer");
							labelError.setText("'" + labels[i].getText() + "' ist leer");
							return;
						}
					}
					String[] wahlen = new String[fields.length];
					for (int i = 0; i < fields.length; i++) {
						wahlen[i] = fields[i].getText();
					}
					FASchueler.instanzFuer(fieldName.getText(), fieldVorname.getText(), wahlen);
//					System.out.println("I'm alive 1");
//					FAWahlZettel wz = schueler.getWahlZettel();
//					System.out.println("I'm alive 2");
//					System.out.println("Füge Kurse hinzu");
//					for (int i = 0; i < (labels.length/2); i++) {
//						FAKurs kurs = FAKurs.instanzFuer(fields[i*2].getText(), fields[i*2 + 1].getText());
//						System.out.println("--" + kurs.toString());
//						System.out.println(i);
//						wz.addWahl(i+1, kurs);
//					}
					speichern();
					statusAnzeige.setText(FASchueler.getAnzahlInstanzen() + " Schüler. Erneute Verteilung notwendig.");				
				}
				
				fieldName.setText("");
				fieldVorname.setText("");
				box.setSelected(false);
				
				for (JTextField field : fields) {
					field.setText("");
					field.setEnabled(true);
				}
				
				for (JLabel label : labels) {
					label.setEnabled(true);
				}
			}
		});
		cISInnerSouthPanel.add(btnAdd);
		centerInnerSouthPanel.add(cISInnerSouthPanel, BorderLayout.SOUTH);
		// - End Center Inner South Inner South Panel
		
		panel.add(centerInnerSouthPanel, BorderLayout.SOUTH);
		// End Center Inner South Panel
		
		WahlomatListener wListener = new WahlomatListener() {
			
			public void schuelerBearbeitet() {
				String[][] updatedRowData = new String[FASchueler.getAnzahlInstanzen()][columnHeaders.length];
				ArrayList<FASchueler> alleSchueler = new ArrayList<>(FASchueler.getAlleInstanzen());
				Collections.sort(alleSchueler);
				int i = 0;
				for (FASchueler schueler : alleSchueler) {
					updatedRowData[i][0] = FAHelper.macheLesbar(schueler.getName());
					updatedRowData[i][1] = FAHelper.macheLesbar(schueler.getVorname());
					if (schueler.schreibtFacharbeit()) {
						FAWahlZettel wz = schueler.getWahlZettel();
						FAKurs kurs = wz.getKurs(1);
						updatedRowData[i][2] = FAHelper.macheLesbar(kurs.getFach());
						updatedRowData[i][3] = FAHelper.macheLesbar(kurs.getLehrer());
						kurs = wz.getKurs(2);
						updatedRowData[i][4] = FAHelper.macheLesbar(kurs.getFach());
						updatedRowData[i][5] = FAHelper.macheLesbar(kurs.getLehrer());
						kurs = wz.getKurs(3);
						updatedRowData[i][6] = FAHelper.macheLesbar(kurs.getFach());
						updatedRowData[i][7] = FAHelper.macheLesbar(kurs.getLehrer());
					} else {
						updatedRowData[i][2] = "Projektkurs";
					}
					i++;
					((DefaultTableModel) table.getModel()).setDataVector(updatedRowData, columnHeaders);
				}
			}

			@Override
			public void kursBearbeitet() {}
		};
		FASchueler.addWahlomatListener(wListener);
		
		return panel;
	}

	
	/**
	 * Erstellt das Panel, in dem die gewählten Kurse dargestellt sind.
	 * */
	private JPanel createPanelKursEdit() {
		
		final String[] columnNames = {"Lehrer", "Fach", "Maximale Schülerzahl"};
		
		ArrayList<FAKurs> kurse = new ArrayList<>(FAKurs.getAlleInstanzen());
		Collections.sort(kurse);
		
		String[][] rowData = new String[kurse.size()][3];
		for (int i = 0; i < rowData.length; i++) {
			FAKurs kurs = kurse.get(i);
			rowData[i][0] = kurs.getLehrer();
			rowData[i][1] = kurs.getFach();
			rowData[i][2] = (new Integer(kurs.getSchuelerListe().getMaxAnzSchueler())).toString();
		}
		
		JPanel panel = new JPanel(new BorderLayout());
		
		final DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		
		final JTable table = new JTable(model);
		
		
		((DefaultTableModel) table.getModel()).setDataVector(rowData, columnNames);
		
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		final JPanel innerSouthPanel = new JPanel(new BorderLayout());
		JPanel innerSouthWestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel innerSouthEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		final JLabel kursLabel = new JLabel("Kurs");
		innerSouthWestPanel.add(kursLabel);
		JLabel maxAnzLabel = new JLabel("    Maximale Schülerzahl:");
		innerSouthWestPanel.add(maxAnzLabel);
		final JTextField maxAnzField = new JTextField(5);
		innerSouthWestPanel.add(maxAnzField);
		innerSouthPanel.add(innerSouthWestPanel, BorderLayout.WEST);

		JButton btnSave = new JButton("Übernehmen");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = table.getSelectedRow();
				
				String fach = (String) table.getValueAt(row, 1);
				String lehrer = (String) table.getValueAt(row, 0);
				
				FAKurs kurs = FAKurs.instanzFuer(fach, lehrer);
				FAKurs.FASchuelerListe liste = kurs.getSchuelerListe();
				
				try {
					int neueMaxAnz = Integer.parseInt(maxAnzField.getText());
					if (neueMaxAnz != liste.getMaxAnzSchueler()) {
						liste.setMaxAnzSchueler(neueMaxAnz);
					} 
					kursLabel.setText("Kurs");
					maxAnzField.setText("");
					setComponentsEnabled(innerSouthPanel, false);
				} catch (NumberFormatException e) {
					FAGUI.log.warning("Für die Maximale Schülerzahl wurde keine Ganzzahl eingegeben.");
				}
			}
		});
		innerSouthEastPanel.add(btnSave);
		JButton btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				kursLabel.setText("Kurs");
				maxAnzField.setText("");
				setComponentsEnabled(innerSouthPanel, false);
			}
		});
		innerSouthEastPanel.add(btnCancel);
		innerSouthPanel.add(innerSouthEastPanel);
		
		panel.add(innerSouthPanel, BorderLayout.SOUTH);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					
					String fach = (String) table.getValueAt(row, 1);
					String lehrer = (String) table.getValueAt(row, 0);
					
					setComponentsEnabled(innerSouthPanel, true);
					
					kursLabel.setText(fach + " (" + lehrer + ")");
					
				}
			}
		});
		
		setComponentsEnabled(innerSouthPanel, false);
		
		FAKurs.addWahlomatListener(new WahlomatListener() {
			public void schuelerBearbeitet() {}
			
			public void kursBearbeitet() {
				ArrayList<FAKurs> kurse = new ArrayList<>(FAKurs.getAlleInstanzen());
				Collections.sort(kurse);
				
				String[][] data = new String[kurse.size()][3];
				for (int i = 0; i < data.length; i++) {
					FAKurs kurs = kurse.get(i);
					data[i][0] = kurs.getLehrer();
					data[i][1] = kurs.getFach();
					data[i][2] = (new Integer(kurs.getSchuelerListe().getMaxAnzSchueler())).toString();
				}
				((DefaultTableModel) table.getModel()).setDataVector(data, columnNames);
			}
		});
		
		return panel;
	}
	
	
	/**
	 * Erstellt das Panel, in dem die Verteilung nach Schülern angezeigt wird.
	 * */
	private JPanel createPanelVerteilteSchueler() {
		JPanel panel = new JPanel(new BorderLayout());
		
		
		JLabel labelGuete = new JLabel("Güte: " + (new DecimalFormat("#.##")).format(guete));
		panel.add(labelGuete, BorderLayout.NORTH);
		
		
		String[] columnNames = {"Name", "Vorname", "Fach", "Lehrer", "Priorität"};
		
		ArrayList<FASchueler> verteilteSchueler = new ArrayList<>();
		for (FASchueler schueler : FASchueler.getAlleInstanzen()) {
			if (!schueler.schreibtFacharbeit()) {
				verteilteSchueler.add(schueler);
			} else if (schueler.getWahlZettel().existiertFixierteWahl()) {}
		}
		
		ArrayList<String[]> verteilt = new ArrayList<>();
		ArrayList<FASchueler> alle = new ArrayList<>(FASchueler.getAlleInstanzen());
		Collections.sort(alle);
		for (FASchueler schueler : alle) {
			if (!schueler.schreibtFacharbeit()) {
				String[] str = {schueler.getName(), schueler.getVorname(), "Projektkurs", "", ""};
				verteilt.add(str);
			} else if (schueler.getWahlZettel().existiertFixierteWahl()) {
				FAWahlZettel wz = schueler.getWahlZettel();
				FAKurs kurs = wz.getFixiertenKurs();
				String[] str = {schueler.getName(), schueler.getVorname(), kurs.getFach(), kurs.getLehrer(), (new Integer(wz.getFixierteWahl())).toString()};
				verteilt.add(str);
			}
		}
		int anzahl = verteilt.size();
		String[][] rowData = new String[anzahl][5];
		for (int i = 0; i < anzahl; i++) {
			rowData[i] = verteilt.get(i);
		}
		
		
		JTable table = new JTable(rowData, columnNames);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		return panel;
	}
	
	
	/**
	 * Erstellt das Panel, in dem die Verteilung nach Kursen umgesetzt wird.
	 * */
	private JPanel createPanelVerteilteKurse() {
		JPanel panel = new JPanel(new BorderLayout());

		FAKurs.FASchuelerListe.updateGlobalMaxAnz();
		
		int columns = 2+FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler();
		
		String[] columnNames = new String[columns];//{"Lehrer", "Fach", "Schueler 1", "Schueler 2", "Schueler 3", "Schueler 4", "Schueler 5"};
		columnNames[0] = "Lehrer";
		columnNames[1] = "Fach";
		for (int i = 2; i < columns; i++) {
			columnNames[i] = ("Schüler " + (i-1));
		}
		
		ArrayList<FAKurs> kurse = new ArrayList<>();
		for (FAKurs kurs : FAKurs.getAlleInstanzen()) {
			if (!kurs.getSchuelerListe().istLeer()) kurse.add(kurs);
		}
		Collections.sort(kurse);
		String[][] rowData = new String[kurse.size()][columns];
		for (int i = 0; i < rowData.length; i++) {
			FAKurs kurs = kurse.get(i);
			rowData[i][0] = kurs.getLehrer();
			rowData[i][1] = kurs.getFach();
			int j = 2;
			for (FASchueler schueler : kurs.getSchuelerListe().getSchueler()) {
				rowData[i][j++] = schueler.getName() + ", " + schueler.getVorname();
			}
		}
		
		JTable table = new JTable(rowData, columnNames);
		
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * Speichert die Wahlen und Verteilung.
	 * */
	public void speichern() {
		if (f != null) {
			FASerializer.save(f);
		} else {
			JFileChooser jf = new JFileChooser();
			jf.setFileFilter(new FileNameExtensionFilter(".ser Datei", "ser"));
			if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				f = jf.getSelectedFile();
				if (!f.exists()) {
//					f.mkdirs();
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FASerializer.save(f);
			}
		}
	}

	public static double getGuete() {
		return guete;
	}

	public static void setGuete(double pGuete) {
		guete = pGuete;
	}
	
	/**
	 * Enabled / Disabled einen {@link Container} und alle seine Kind-{@link Component}s.
	 * */
	private void setComponentsEnabled(Container pContainer, boolean pEnabled) {
		for (Component c : pContainer.getComponents()) {
			c.setEnabled(pEnabled);
			if (c instanceof Container) setComponentsEnabled((Container) c, pEnabled);
		}
	}
 	
	
}
