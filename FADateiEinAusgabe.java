import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Liest CSV-Dateien ein und gibt Verteilungen als solche aus
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 * */
public class FADateiEinAusgabe {

	/**
	 * Schreibt Kurs-Schüler-Zuordnung in <code>pAusgabe</code>.
	 * */
	private static void schreibeKursSchuelerZuordnung(BufferedWriter pAusgabe) throws IOException {

		pAusgabe.write("#<Lehrer>;<Fach>;<AnzahlSchueler>;<SchuelerName1>;<SchuelerVorname1>[;<SchuelerName2>;<SchuelerVorname2>...]\r\n");

		ArrayList<FAKurs> kurse = new ArrayList<>(FAKurs.getAlleInstanzen());

		if (kurse == null || kurse.isEmpty()) {
			System.err.println("Es existieren keine Kurs-Instanzen! Breche Ausgabe der Kurse ab.");
			return;
		}
		
		// Sortiere alle Kurse
		Collections.sort(kurse);

		// Zeilenweise Ausgabe der Kurse
		for (FAKurs kurs : kurse) {
			String zeile = kurs.getLehrer() + ";" + kurs.getFach() + ";" + kurs.getSchuelerListe().getAnzahlSchueler();
			
			for (FASchueler schueler : kurs.getSchuelerListe().getSchueler()) {
				zeile += ";" + schueler.getName() + ";" + schueler.getVorname();
			}
			
			pAusgabe.write(zeile + "\r\n");

		}
	}

	/**
	 * Schreibt Kurzreport in <code>pAusgabe</code>
	 * */
	private static void schreibeKurzReport(BufferedWriter pAusgabe, double pGueteVerteilung) throws IOException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd 'um' HH:mm:ss ");
		Date currentTime = new Date();

		pAusgabe.write("#Zeit und Datum: " + formatter.format(currentTime) + "\r\n");
		pAusgabe.write("#Insgesamt wurden " + FAKurs.getAnzahlInstanzen()
				+ " verschiedene Kurse von " + FASchueler.getAnzahlInstanzen()
				+ " SuS gewÃ¤hlt.\r\n");
		pAusgabe.write("#Notwendige Bedingung, dass die Anzahl der SuS ("
				+ FASchueler.getAnzahlInstanzen()
				+ ") kleiner oder gleich der Anzahl gewÃ¤hlter Kurse ("
				+ FAKurs.getAnzahlInstanzen()
				+ ") in "
				+ FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler()
				+ "-facher Anzahl ist ("
				+ FAKurs.getAnzahlInstanzen()
				+ "x"
				+ FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler()
				+ "="
				+ FAKurs.getAnzahlInstanzen()
				* FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler()
				+ "), ist "
				+ (FAKurs.getAnzahlInstanzen()
						* FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler() >= FASchueler
							.getAnzahlInstanzen() ? "erfÃ¼llt.\r\n"
						: "NICHT erfÃ¼llt.\r\n"));
		pAusgabe.write("#Es wurde eine Verteilung von SuS auf gewÃ¤hlte Kurse wie folgt mit der GÃ¼te "
				+ pGueteVerteilung + " erzeugt.\r\n");
		pAusgabe.write("\r\n");
		pAusgabe.write("#\r\n");
		pAusgabe.write((new SimpleDateFormat("yyyy.MM.dd")).format(currentTime) + ";" + (new SimpleDateFormat("HH:mm:ss")).format(currentTime)
				+ ";" + FAKurs.getAnzahlInstanzen() + ";" + FASchueler.getAnzahlInstanzen()
				+ ";" + FAKurs.FASchuelerListe.getGlobalMaxAnzSchueler() + ";" + pGueteVerteilung + "\r\n");
		pAusgabe.write("\r\n");

	}

	/**
	 * Schreibt Schüler-Kurs-Zuordnung in <code>pAusgabe</code>.
	 * */
	private static void schreibeSchuelerKursZuordnung(BufferedWriter pAusgabe) throws IOException {
		ArrayList<FASchueler> alleSchueler = new ArrayList<>(FASchueler.getAlleInstanzen());
		if (alleSchueler.isEmpty()) {
			System.err.println("Keine Schüler. Breche ab");
			return;
		}
		ArrayList<FASchueler> proSchueler = new ArrayList<>();
		ArrayList<FASchueler> nfSchueler = new ArrayList<>(); //Schüler mit nicht fixierter Wahl
		Collections.sort(alleSchueler);
		for (FASchueler schueler : alleSchueler) {
			if (schueler.schreibtFacharbeit()) {
				FAKurs fixierterKurs = schueler.getWahlZettel().getFixiertenKurs();
				if (fixierterKurs != null) {
					pAusgabe.write(schueler.getName() + ";" + schueler.getVorname()
						+ ";" + fixierterKurs.getFach() + ";" + fixierterKurs.getLehrer()
						+ ";" + schueler.getWahlZettel().getFixierteWahl() + "\r\n");
				} else {
					nfSchueler.add(schueler);
				}
			} else {
				proSchueler.add(schueler);
			}
		}

		pAusgabe.write("\r\n");
		pAusgabe.write("#Nicht verteilte Schueler (" + nfSchueler.size() + ") mit ihren Wahlen\r\n");
		
		if (!nfSchueler.isEmpty()) {
			for (FASchueler schueler : nfSchueler) {
				FAKurs fixierterKurs = schueler.getWahlZettel().getFixiertenKurs();
				String zeile = null;

				if (fixierterKurs == null) {
					zeile = schueler.getName() + ";" + schueler.getVorname()
							+ ";";

					FAWahlZettel wz = schueler.getWahlZettel();
					Set<Integer> prios = wz.getPrioritaeten();

					if (prios == null) {
						zeile += ";" + ";" + wz.getFixierteWahl() + "\r\n";
					} else {
						Iterator<Integer> iter = prios.iterator();
						while (iter.hasNext()) {
							Integer prio = iter.next();
							FAKurs kurs = wz.getKurs(prio);
							zeile += kurs.getFach() + ";" + kurs.getLehrer() + ";";
						}
						zeile += "\r\n";
					}
					pAusgabe.write(zeile);
				}

			}
		}
		
		if (!proSchueler.isEmpty()) {
			pAusgabe.write("\r\n");
			pAusgabe.write("#Schueler mit Projektkurs\r\n");
			for (FASchueler schueler : proSchueler) {
				pAusgabe.write(schueler.getName() + ";" + schueler.getVorname());
			}
		}
	}

	/**
	 * Schreibt die Ausgabe in die Datei <code>pAusgabeDatei</code>.
	 * */
	public static void schreibe(String pAusgabeDatei, double pGueteVerteilung) {

		// Erzeuge einen kurssortierten Ausgabenstrom

		BufferedWriter ausgabe = null;
		FileWriter datei = null;

		try {
			datei = new FileWriter(pAusgabeDatei);
			ausgabe = new BufferedWriter(datei);

			FAGUI.log.info("Starte Schreiben in Datei \'" + pAusgabeDatei + "\'...");

			ausgabe.write("#Kurzreport:\r\n");
			schreibeKurzReport(ausgabe, pGueteVerteilung);

			ausgabe.write("#Ausgabe der Zuordnung im Folgenden sortiert nach Kursen:\r\n");
			schreibeKursSchuelerZuordnung(ausgabe);
			ausgabe.write("\r\n");

			ausgabe.write("#Ausgabe der Zuordnung im Folgenden sortiert nach SchÃ¼lern:\r\n");
			schreibeSchuelerKursZuordnung(ausgabe);

			FAGUI.log.info("Schreiben der Datei \'" + pAusgabeDatei + "\' abgeschlossen.");

		} catch (IOException e) {
			FAGUI.log.severe("Ein-/ Ausgabefehler beim Verarbeiten der Datei \'"
							+ pAusgabeDatei + "\'!");
			System.err.print("[Systemfehlermeldung lautet:");
			e.printStackTrace();
			System.err.println("]");
		} finally {
			if (ausgabe != null) {
				try {
					ausgabe.close();
				} catch (IOException e) {
					System.err
							.println("FEHLER: Ein-/ Ausgabefehler beim Verarbeiten der Datei \'"
									+ pAusgabeDatei + "\'!");
					System.err.print("[Systemfehlermeldung lautet:");
					e.printStackTrace();
					System.err.println("]");
				}
			}
		}

	}

	/**
	 * Liest die Datei <code>pEingabeDatei</code> ein.
	 * */
	public static void lese(String pEingabeDatei) {

		BufferedReader eingabe = null;
		FileReader datei = null;
		final String trennzeichen = ";";

		try {
			datei = new FileReader(pEingabeDatei);
			eingabe = new BufferedReader(datei);

			String zeile = null;
			int zeilenNummer = 0;

			final int nameIdx = 0, vornameIdx = 1, fachIdx = 2, lehrerIdx = 3;
			final String projektkurs = "P";

			FAGUI.log.info("Starte Auslesen der Datei \'" + pEingabeDatei + "\'...");
			
			ArrayList<String> speichernutzung = new ArrayList<>();
			Runtime.getRuntime().gc();
			long before = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
			long total = 0;
			speichernutzung.add("Schülerzahl: " + FASchueler.getAnzahlInstanzen() + " Genutzter Speicher: " + 
					before + " M/S: " );

			while (eingabe.ready() && (zeile = FAHelper.macheLesbar(eingabe.readLine())) != null) {

				++zeilenNummer;

				FAGUI.log.info("Zeile " + zeilenNummer + " wurde eingelesen und wird nun verarbeitet.");

				if (zeile.isEmpty()) { 
					FAGUI.log.info("Zeile " + zeilenNummer + " ist leer. Überspringe Zeile.");
					continue;
				} else if (zeile.startsWith("#")) {
					FAGUI.log.info(" Zeile " + zeilenNummer + " ist eine Kommentarzeile. Überspringe Zeile.");
					continue;
				}

				StringTokenizer tokenizer = new StringTokenizer(FAHelper.macheLesbar(zeile), trennzeichen);
				int count = tokenizer.countTokens();
				String[] tokens = new String[count];
				for (int i = 0; tokenizer.hasMoreTokens(); i++) {
					tokens[i] = tokenizer.nextToken();
				}

				FAGUI.log.info("Verarbeite Tokens der Zeile " + zeilenNummer + ".");
				
				if (count % 2 == 0) {
					FASchueler schueler = FASchueler.instanzFuer(tokens[nameIdx], tokens[vornameIdx], true);
					FAWahlZettel wz = schueler.getWahlZettel();
					for (int i = 0; i < count-2; i=i+2) {
						wz.addWahl(i+1-(i/2), FAKurs.instanzFuer(tokens[i+fachIdx], tokens[i+lehrerIdx]));
					}
				} else if (tokens[count-1].equals(projektkurs)) {
					FASchueler.instanzFuer(tokens[nameIdx], tokens[vornameIdx], false);
				}
				
				Runtime.getRuntime().gc();
				long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - before);
				total += used;
				speichernutzung.add("Schülerzahl: " + FASchueler.getAnzahlInstanzen() + " Genutzter Speicher: " + 
						used + " M/S: " + (used / FASchueler.getAnzahlInstanzen()));

			} // while

			for (String x : speichernutzung) {
				System.out.println(x);
			}
			System.out.println("Durchschnittlich " + ((total / FASchueler.getAnzahlInstanzen()) / FASchueler.getAnzahlInstanzen()) + "Bytes pro Schüler");
			
			FAGUI.log.info("Auslesen der Datei \'" + pEingabeDatei + "\' abgeschlossen.");
			FAGUI.log.info(zeilenNummer + " Zeilen wurden verarbeitet.");
			FAGUI.log.info(FAKurs.getAnzahlInstanzen() + " Kurs-Instanzen wurden eingelesen und erzeugt:");
			FAGUI.log.info(FAKurs.instanzenToString());
			FAGUI.log.info(FASchueler.getAnzahlInstanzen() + " Schüler-Instanzen wurden eingelesen und erzeugt:");
			FAGUI.log.info(FASchueler.instanzenToString());

		} catch (FileNotFoundException e) {
			System.err.println("FEHLER: Datei \'" + pEingabeDatei + "\' wurde nicht gefunden!");
			System.err.print("[Systemfehlermeldung lautet:");
			e.printStackTrace();
			System.err.println("]");
		} catch (IOException e) {
			System.err.println("FEHLER: Ein-/ Ausgabefehler beim Verarbeiten der Datei \'" + pEingabeDatei + "\'!");
			System.err.print("[Systemfehlermeldung lautet:");
			e.printStackTrace();
			System.err.println("]");

		} finally {
			if (eingabe != null) {
				try {
					eingabe.close();
				} catch (IOException e) {
					System.err.println("FEHLER: Ein-/ Ausgabefehler beim Schliessen der Datei \'" + pEingabeDatei + "\'!");
					System.err.print("[Systemfehlermeldung lautet:");
					e.printStackTrace();
					System.err.println("]");
				}
			}
		}

	}

	/**
	 * Liest eine Ausgabedatei ein
	 * */
	public static String leseAusgabe(String pPfad) {
		BufferedReader eingabe = null;
		FileReader datei = null;
		final String trennzeichen = ";";
		String kurzreport = "";
		
		try {
			datei = new FileReader(pPfad);
			eingabe = new  BufferedReader(datei);
			
			String zeile = null;
			int zeilenNummer = 0;
			
			//TODO final int p1DatumIdx = 0, p1ZeitIdx = 1, p1KurseIdx = 2, p1SchuelerIdx = 3, p1MaxSchuelerIdx = 4, p1GueteIdx = 5;
			final int p1MaxSchuelerIdx = 4;
			final int p3NameIdx = 0, p3VornameIdx = 1, p3FachIdx = 2, p3LehrerIdx = 3, p3PrioIdx = 4;
			final int p4NameIdx = 0, p4VornameIdx = 1, p4Fach1Idx = 2, p4Lehrer1Idx = 3, p4Fach2Idx = 4, p4Lehrer2Idx = 5, p4Fach3Idx = 6, p4Lehrer3Idx = 7;
			
			int phase = 0;
			
			while (eingabe.ready() && (zeile = eingabe.readLine()) != null) {
				
				FAGUI.log.info("Beginne Auslesen von Zeile " + ++zeilenNummer + " in Phase " + phase + ".");
				
				if (zeile.isEmpty()) {
					System.out.println("INFO: Zeile " + zeilenNummer + " ist leer. Führe Auslesen in Phase " + ++phase + " fort.");
					continue;
				}
				
				if (phase == 0 && zeile.startsWith("#")) {
					kurzreport += zeile;
					continue;
				}
				
				if (zeile.startsWith("#")) {
					FAGUI.log.info("Zeile " + zeilenNummer + " ist ein Kommentar und wird übersprungen.");
					continue;
				} else if (phase == 1) {
					StringTokenizer tokenizer = new StringTokenizer(zeile);
					if (tokenizer.countTokens() < 3) {
						continue;
					}
					
					String[] tokens = new String[tokenizer.countTokens()];
					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						tokens[i] = tokenizer.nextToken();
					}
					
					try {
						FAKurs.FASchuelerListe.setDefaultMaxAnzSchueler(Integer.parseInt(tokens[p1MaxSchuelerIdx]));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					
				} else if (phase == 2) {
					StringTokenizer tokenizer = new StringTokenizer(zeile);
					if (tokenizer.countTokens() < 3) {
						continue;
					}
					
					String[] tokens = new String[tokenizer.countTokens()];
					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						tokens[i] = tokenizer.nextToken();
					}
					
					
					
				} else if (phase == 3) {
					StringTokenizer tokenizer = new StringTokenizer(zeile, trennzeichen);
					if (tokenizer.countTokens() != 5) {
						FAGUI.log.warning("Sysntaxfehler in Zeile " + zeilenNummer + ".");
						continue;
					}
					
					String[] tokens = new String[5];
					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						tokens[i] = tokenizer.nextToken();
					}
					
					int prio = -1;
					
					try {
						prio = Integer.parseInt(tokens[p3PrioIdx]);
					} catch (NumberFormatException e) {
						FAGUI.log.severe("<Priorität> in Zeile " + zeilenNummer + " muss eine Ganzzahl sein.");
					}
					
					FASchueler schueler = FASchueler.instanzFuer(tokens[p3NameIdx], tokens[p3VornameIdx]);
					FAKurs kurs = FAKurs.instanzFuer(tokens[p3FachIdx], tokens[p3LehrerIdx]);
					schueler.getWahlZettel().addWahl(prio, kurs);
					schueler.getWahlZettel().setFixierteWahl(prio);
					kurs.getSchuelerListe().addSchueler(schueler);
					
				} else if (phase == 4) {
					StringTokenizer tokenizer = new StringTokenizer(zeile, trennzeichen);
					if (tokenizer.countTokens() != 8) {
						FAGUI.log.warning("Sysntaxfehler in Zeile " + zeilenNummer + ".");
						continue;
					}
					
					String[] tokens = new String[tokenizer.countTokens()];
					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						tokens[i] = tokenizer.nextToken();
					}
					
					FASchueler schueler = FASchueler.instanzFuer(tokens[p4NameIdx], tokens[p4VornameIdx]);
					schueler.getWahlZettel().addWahl(1, FAKurs.instanzFuer(tokens[p4Fach1Idx], tokens[p4Lehrer1Idx]));
					schueler.getWahlZettel().addWahl(2, FAKurs.instanzFuer(tokens[p4Fach2Idx], tokens[p4Lehrer2Idx]));
					schueler.getWahlZettel().addWahl(3, FAKurs.instanzFuer(tokens[p4Fach3Idx], tokens[p4Lehrer3Idx]));
				} else if (phase == 5) {
					StringTokenizer tokenizer = new StringTokenizer(zeile, trennzeichen);
					if (tokenizer.countTokens() != 2) {
						FAGUI.log.warning("Sysntaxfehler in Zeile " + zeilenNummer + ".");
						continue;
					}
					
					String[] tokens = new String[tokenizer.countTokens()];
					for (int i = 0; tokenizer.hasMoreTokens(); i++) {
						tokens[i] = tokenizer.nextToken();
					}
					
					FASchueler.instanzFuer(tokens[0], tokens[1], false);
				}
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("FEHLER: Die Datei " + pPfad + "wurde nicht gefunden.");
			System.err.print("[Systemfehlermeldung lautet: ");
			e.printStackTrace();
			System.err.println(" ]");
		} catch (IOException e) {
			System.err.println("FEHLER: Ein-/Ausgabefehler beim Verarbeiten der Datei \'" + pPfad + "\'.");
			System.err.print("[Systemfehlermeldung lautet: ");
			e.printStackTrace();
			System.err.println(" ]");
		} finally {
			if (eingabe != null) {
				try {
					eingabe.close();
				} catch (IOException e) {
					System.err.println("FEHLER: Ein-/Ausgabefehler beim Schliessen der Datei \'" + pPfad + "\'.");
					System.err.print("[Systemfehlermeldung lautet: ");
					e.printStackTrace();
					System.err.println(" ]");
				}
			}
		}
		
		return kurzreport;
	}
	
} // Ende Klasse
