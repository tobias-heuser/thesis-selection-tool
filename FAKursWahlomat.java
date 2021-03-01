import java.util.*;

/**
 * Ein <code>FAKursWahlomat</code>-Objekt ist für die Verteilung von
 * {@link FASchueler}n auf die {@link FAKurs}e gemäß ihrer Wahlen zuständig.
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 */

public class FAKursWahlomat {

	/**
	 * Führt <code>pMaxAnzahlIterationen</code>-viele Verteilungsdurchgänge aus, stellt
	 * die Verteilung mit der besten Güte wieder her und gibt diese zurück.
	 * 
	 * @param pMaxAnzahlIterationen Anzahl der Verteilungsdurchgänge
	 * @return Die Güte der besten Verteilung
	 * */
	public static double verteile(int pMaxAnzahlIterationen) {

		// Beste Güte
		double besteGuete = -1.;

		// (Schüler -> Fixierte Wahl)
		HashMap<FASchueler, Integer> besteVerteilung = new HashMap<FASchueler, Integer>();

		// Wenn weniger als ein Verteilungsdurchgang angegebn wurde, wird einer ausgeführt
		if (pMaxAnzahlIterationen <= 0) pMaxAnzahlIterationen = 1;

		FAGUI.log.info("Führe insgesamt " + pMaxAnzahlIterationen
				+ (pMaxAnzahlIterationen > 1
						? " Verteilungsdurchgänge durch..." : " Verteilungsdurchgang durch..."));

		// Verteilungsdurchgänge
		for (int i = 1; i <= pMaxAnzahlIterationen; i++) {

			FAGUI.log.info("Starte Verteilungsdurchgang " + i + "...");

			// Verteilungdurchgang ausführen und Güte zwischenspeichern
			double guete = verteile();
			
			// Wenn die Güte des letzten Verteilungsdurchgangs besser ist,
			// als die bisher beste
			if (guete > besteGuete) {

				// Löschen der bisher besten Verteilung
				besteVerteilung.clear();

				// Die Güte des letzten Verteilungsdurchgangs ist die neu beste Güte
				besteGuete = guete;

				// Speichern der neuen besten Verteilung
				Iterator<FASchueler> iter = FASchueler.getAlleInstanzen().iterator();
				
				while (iter.hasNext()) {
					FASchueler schueler = iter.next();
					System.out.println(schueler.toString());
					besteVerteilung.put(schueler, (schueler.schreibtFacharbeit())
							? schueler.getWahlZettel().getFixierteWahl() : null);
				} // Ende while
				
			} // Ende if

			FAGUI.log.info("Verteilungsdurchgang " + i+ " abgeschlossen.");

		} // Ende for

		// Wiederherstellen der besten Verteilung

		FAGUI.log.info("Beste Güte=\'" + besteGuete + "\'.");
		FAGUI.log.info("Stelle Verteilung mit bester Güte=\'"
				+ besteGuete + "\' wieder her.");

		// Reset von Kurszuordnungen
		Iterator<FAKurs> iteratorAlleKurse = FAKurs.getAlleInstanzen().iterator();
		while (iteratorAlleKurse.hasNext()) {
			iteratorAlleKurse.next().getSchuelerListe().removeAlleSchueler();
		}

		// Wiederherstellen der Kurszuordnungen & fixierten Wahlen!
		Iterator<FASchueler> iteratorAlleSchueler = FASchueler.getAlleInstanzen().iterator();

		while (iteratorAlleSchueler.hasNext()) {

			FASchueler schueler = iteratorAlleSchueler.next();

			Integer fixierteWahl = besteVerteilung.get(schueler);

			if (fixierteWahl != null) {
				if (fixierteWahl == -1)
					schueler.getWahlZettel().resetFixierteWahl();
				else {
					schueler.getWahlZettel().setFixierteWahl(fixierteWahl);
					FAKurs kurs = schueler.getWahlZettel().getKurs(fixierteWahl);
					kurs.getSchuelerListe().addSchueler(schueler);
				} // Ende if/else
				
			} // Ende if

		} // Ende while

		return besteGuete;

	} // Ende Methode verteile(int pMaxAnzahlIterationen)

	/**
	 * Führt einen Verteilungsdurchgang aus.
	 * 
	 * @return Güte des Verteilungsdurchlaufs
	 * */
	private static double verteile() {

		// alle Schüler, die eine FA schreiben
		final Collection<FASchueler> alleSchueler = FASchueler.getAlleSchreiberlinge();

		// alle Kurse
		final Collection<FAKurs> alleKurse = FAKurs.getAlleInstanzen();

		// Liste der bereits verteilten Schüler (initial leer)
		ArrayList<FASchueler> listeVerteilteSchueler = new ArrayList<FASchueler>();

		// Liste der nicht verteilten Schüler (initial gleich einer Kopie von alleSchueler)
		ArrayList<FASchueler> listeNichtVerteilteSchueler = new ArrayList<FASchueler>();

		// Anfertigung einer Kopie der Referenzen aller Schüler und Reset ihrer bisherigen
		// fixierten Wahl
		Iterator<FASchueler> iter = alleSchueler.iterator();
		while (iter.hasNext()) {
			FASchueler schueler = iter.next();
			if (schueler.schreibtFacharbeit()) {
				listeNichtVerteilteSchueler.add(schueler);
				schueler.getWahlZettel().resetFixierteWahl();
			}
		}

		// Reset von Kurszuordnungen
		Iterator<FAKurs> iterKurs = alleKurse.iterator();
		while (iterKurs.hasNext()) {
			iterKurs.next().getSchuelerListe().removeAlleSchueler();
		}

		// Zufällige Verteilung der Schüler erzeugen
		Collections.shuffle(listeNichtVerteilteSchueler);

		// Notwendige Bedingung für erfolgreiche Verteilung abprüfen
		if (alleSchueler.size() >= FAKurs.FASchuelerListe.getAnzPlaetzeAllerKurse()) {
			FAGUI.log.warning("Kann keine optimale Verteilung von Schülern auf die Kurse"
					+ " erzeugen. Anzahl aller Schüler ("  + alleSchueler.size()
					+ ") ist grösser als Facharbeitsplätze in den Kursen vorhanden sind ("
					+ FAKurs.FASchuelerListe.getAnzPlaetzeAllerKurse() + ").");
			FAGUI.log.info("Führe Verteilung trotzdem durch.");
		}

		int aktPrio = 1;

		while (aktPrio <= FAWahlZettel.MAX_PRIO
				&& listeNichtVerteilteSchueler.size() != listeVerteilteSchueler.size()) {

			for (FASchueler schueler : listeNichtVerteilteSchueler) {

				// nur nicht verteilte Schüler auswählen
				if (listeVerteilteSchueler.contains(schueler)) continue;

				// Kurs mit aktueller Priorität auswählen
				FAKurs kurs = schueler.getWahlZettel().getKurs(aktPrio);

				// Fehler wenn Kurs nicht existiert und Beenden des jetzigen
				// Schleifendurchlaufs -> mit nächstem Schüler weiter
				if (kurs == null) {
					FAGUI.log.severe("Schüler \'" + schueler.getName() + ", "
							+ schueler.getVorname()
							+ "\' hat keine Kurswahl mit Priorität \'" + aktPrio
							+ "\'. Kann keine Verteilung mit dieser Priorität vornehmen.");
					continue;
				} 

				// Wenn im Kurs noch Platz für einen weiteren Schüler ist
				if (kurs.getSchuelerListe().hatNochPlatz()) {
					// Schüler zum Kurs hinzufügen
					kurs.getSchuelerListe().addSchueler(schueler); 
					// zur Liste verteilter Schüler hinzufügen
					listeVerteilteSchueler.add(schueler); 
					// Fixierte Wahl setzen
					schueler.getWahlZettel().setFixierteWahl(aktPrio); 

					FAGUI.log.info("Schüler \'" + schueler.getName()
							+ ", " + schueler.getVorname()
							+ "\' wurde auf Kurs [" + kurs.toString()
							+ "] verteilt. Priorität " + aktPrio
							+ " wurde erfüllt.");
				} // Ende if

			} // Ende for
			
			// Mit nächster Priorität fortfahren
			aktPrio++;

		} // Ende while

		// Nicht-verteilte Schüler bestimmen
		listeNichtVerteilteSchueler = new ArrayList<FASchueler>();

		iter = alleSchueler.iterator();
		while (iter.hasNext()) {

			FASchueler schueler = iter.next();

			if (schueler.schreibtFacharbeit() && !listeVerteilteSchueler.contains(schueler)) {
				if (einmischen(schueler)) { // Nicht verteilten Schüler einmischen
					                        // -> Einmischen erfolgreich
					FAGUI.log.info("Schüler \'" + schueler.getName() + ", "
							+ schueler.getVorname() + " wurde eingemischt");
				} else { // -> Einmischen nicht erfolgreich
					listeNichtVerteilteSchueler.add(schueler);
					FAGUI.log.warning("Schüler \'" + schueler.getName() + ", "
							+ schueler.getVorname()
							+ "\' konnte letztlich nicht verteilt werden.");
				}
			}
		}

		// Final nicht verteilte Schüler ermitteln
		if (listeNichtVerteilteSchueler.size() > 0) {
			FAGUI.log.warning("Insgesamt konnten " + listeNichtVerteilteSchueler.size()
					+ " von " + alleSchueler.size() + " Schülern nicht verteilt werden.");
		} else FAGUI.log.info("Alle Schüler konnten verteilt werden :-)");

		// Güte der Verteilung berechnen und zurückgeben
		return gueteBerechnen();

	}
	
	/**
	 * Berechnet die Güte der Verteilung.
	 * 
	 * @return Güte der Verteilung
	 * */
	private static double gueteBerechnen() {
		// Güte
		double guete = 0;
		
		// anzahlen[0] -> Anzahl nicht verteilte Schüler; anzahlen[x] 
		// -> Anzahl Schüler mit Priorität x erfüllt
		int[] anzahlen = new int[FAWahlZettel.MAX_PRIO + 1];
		
		// Für jeden Schüler überprüfen
		for (FASchueler schueler : FASchueler.getAlleSchreiberlinge()) {
			FAWahlZettel wz = schueler.getWahlZettel();
			if (wz.existiertFixierteWahl()) anzahlen[wz.getFixierteWahl()]++;
			else anzahlen[0]++;
		}
		
		// Anzahlen in Güte umrechnen
		for (int prio = 1; prio <= FAWahlZettel.MAX_PRIO; prio++) {
			guete += anzahlen[prio] * (FAWahlZettel.MAX_PRIO + 1 - prio);
		}
		
		// Nicht verteilte Schüler von Güte abziehen
		guete -= anzahlen[0] * FAWahlZettel.MAX_PRIO;
		
		// Durchschnittliche Güte berechnen -> Gesamtgüte
		guete = guete / (FASchueler.getAlleSchreiberlinge().size() * FAWahlZettel.MAX_PRIO);
		
		FAGUI.log.info("Güte der Verteilung ist: " + guete);
		
		return guete;
	}
	
	/**
	 * Mischt den nicht verteilten {@link FASchueler} <code>pSchueler</code> ein.
	 * 
	 * @param pSchueler Einzumischender Schüler
	 * @return Erfolg des Einmischens
	 * */
	private static boolean einmischen(FASchueler pSchueler) {
		
		// Iterator über alle Instanzen
		Iterator<FASchueler> alle = FASchueler.getAlleInstanzen().iterator();
		
		// Verteilte Schüler
		ArrayList<FASchueler> verteilt = new ArrayList<>();
		
		// Füllen von 'verteilt' mit allen verteilten Schülern
		while (alle.hasNext()) {
			FASchueler schueler = alle.next();
			if (schueler.schreibtFacharbeit()
					&& schueler.getWahlZettel().existiertFixierteWahl()) {
				verteilt.add(schueler);
			}
		}
			
		FAGUI.log.info("Schüler \'" + pSchueler.getName() + ", "
				+ pSchueler.getVorname() + " wird eingemischt");
		
		for (FASchueler schueler : verteilt) {
			
			if (schueler.getWahlZettel().getFixierteWahl() != 3) {
				
				for (int prio =FAWahlZettel.MIN_PRIO; prio <= FAWahlZettel.MAX_PRIO; prio++) {
					if (einmischversuch(pSchueler, schueler, prio)) return true;
				}
 				
			}
		}
		
		return false;
	}
	
	/**
	 * Versucht den {@link FASchueler} <code>pEinzumischender</code> in seinen Kurs mit
	 * der Priorität <code>pPrio</code> einzumischen indem der {@link FASchueler} 
	 * <code>pVerteilter</code> umverteilt wird.
	 * 
	 * @param pEinzumischender Einzumischender Schüler
	 * @param pVerteilter Bereits verteilter Schüler
	 * @param pPrio Priorität des Kurses, in den der Schüler eingemischt werden soll
	 * @return Erfolg des Einmischversuchs
	 * */
	private static boolean einmischversuch(FASchueler pEinzumischender,
			FASchueler pVerteilter, int pPrio) {
		
		
		FAWahlZettel eWahlzettel = pEinzumischender.getWahlZettel();
		FAWahlZettel vWahlzettel = pVerteilter.getWahlZettel();
		
		if (vWahlzettel.getFixiertenKurs() == eWahlzettel.getKurs(pPrio)) {
			if (vWahlzettel.getFixierteWahl() == 2
					&& vWahlzettel.getKurs(3).getSchuelerListe().hatNochPlatz()) {
				tausche(pEinzumischender, pVerteilter, pPrio, 3);
				return true;
			} else if (vWahlzettel.getFixierteWahl() == 1) {
				if (vWahlzettel.getKurs(2).getSchuelerListe().hatNochPlatz()) {
					tausche(pEinzumischender, pVerteilter, pPrio, 2);
					return true;
				} else if (vWahlzettel.getKurs(3).getSchuelerListe().hatNochPlatz()) {
					tausche(pEinzumischender, pVerteilter, pPrio, 3);
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Mischt den {@link FASchueler} <code>einzumischenS</code> in seinen {@link FAKurs}
	 * mit der Priorität <code>wechselK</code> ein, indem der {@link FASchueler}
	 * <code>ersatzS</code> auf seinen {@link FAKurs} mit der Priorität <code>ersatzK</code>
	 * umverteilt wird.
	 * 
	 * @param einzumischenS Einzumischender Schüler
	 * @param ersatzS Schüler, der umverteilt wird
	 * @param wechselK Priorität des Kurses über den getauscht wird
	 * @param ersatzK Priorität des Ersatzkurses für den Schüler der, umverteilt wird
	 * */
	private static void tausche(FASchueler einzumischenS, FASchueler ersatzS,
			int wechselK, int ersatzK) {
		FAWahlZettel vWZ = ersatzS.getWahlZettel();
		FAWahlZettel eWZ = einzumischenS.getWahlZettel();
		vWZ.getFixiertenKurs().getSchuelerListe().removeSchueler(ersatzS);
		vWZ.getKurs(ersatzK).getSchuelerListe().addSchueler(ersatzS);
		vWZ.setFixierteWahl(ersatzK);
		eWZ.getKurs(wechselK).getSchuelerListe().addSchueler(einzumischenS);
		eWZ.setFixierteWahl(wechselK);
	}
	
	
}
