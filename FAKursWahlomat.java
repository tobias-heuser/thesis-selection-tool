import java.util.*;

/**
 * Ein <code>FAKursWahlomat</code>-Objekt ist f�r die Verteilung von
 * {@link FASchueler}n auf die {@link FAKurs}e gem�� ihrer Wahlen zust�ndig.
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 */

public class FAKursWahlomat {

	/**
	 * F�hrt <code>pMaxAnzahlIterationen</code>-viele Verteilungsdurchg�nge aus, stellt
	 * die Verteilung mit der besten G�te wieder her und gibt diese zur�ck.
	 * 
	 * @param pMaxAnzahlIterationen Anzahl der Verteilungsdurchg�nge
	 * @return Die G�te der besten Verteilung
	 * */
	public static double verteile(int pMaxAnzahlIterationen) {

		// Beste G�te
		double besteGuete = -1.;

		// (Sch�ler -> Fixierte Wahl)
		HashMap<FASchueler, Integer> besteVerteilung = new HashMap<FASchueler, Integer>();

		// Wenn weniger als ein Verteilungsdurchgang angegebn wurde, wird einer ausgef�hrt
		if (pMaxAnzahlIterationen <= 0) pMaxAnzahlIterationen = 1;

		FAGUI.log.info("F�hre insgesamt " + pMaxAnzahlIterationen
				+ (pMaxAnzahlIterationen > 1
						? " Verteilungsdurchg�nge durch..." : " Verteilungsdurchgang durch..."));

		// Verteilungsdurchg�nge
		for (int i = 1; i <= pMaxAnzahlIterationen; i++) {

			FAGUI.log.info("Starte Verteilungsdurchgang " + i + "...");

			// Verteilungdurchgang ausf�hren und G�te zwischenspeichern
			double guete = verteile();
			
			// Wenn die G�te des letzten Verteilungsdurchgangs besser ist,
			// als die bisher beste
			if (guete > besteGuete) {

				// L�schen der bisher besten Verteilung
				besteVerteilung.clear();

				// Die G�te des letzten Verteilungsdurchgangs ist die neu beste G�te
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

		FAGUI.log.info("Beste G�te=\'" + besteGuete + "\'.");
		FAGUI.log.info("Stelle Verteilung mit bester G�te=\'"
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
	 * F�hrt einen Verteilungsdurchgang aus.
	 * 
	 * @return G�te des Verteilungsdurchlaufs
	 * */
	private static double verteile() {

		// alle Sch�ler, die eine FA schreiben
		final Collection<FASchueler> alleSchueler = FASchueler.getAlleSchreiberlinge();

		// alle Kurse
		final Collection<FAKurs> alleKurse = FAKurs.getAlleInstanzen();

		// Liste der bereits verteilten Sch�ler (initial leer)
		ArrayList<FASchueler> listeVerteilteSchueler = new ArrayList<FASchueler>();

		// Liste der nicht verteilten Sch�ler (initial gleich einer Kopie von alleSchueler)
		ArrayList<FASchueler> listeNichtVerteilteSchueler = new ArrayList<FASchueler>();

		// Anfertigung einer Kopie der Referenzen aller Sch�ler und Reset ihrer bisherigen
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

		// Zuf�llige Verteilung der Sch�ler erzeugen
		Collections.shuffle(listeNichtVerteilteSchueler);

		// Notwendige Bedingung f�r erfolgreiche Verteilung abpr�fen
		if (alleSchueler.size() >= FAKurs.FASchuelerListe.getAnzPlaetzeAllerKurse()) {
			FAGUI.log.warning("Kann keine optimale Verteilung von Sch�lern auf die Kurse"
					+ " erzeugen. Anzahl aller Sch�ler ("  + alleSchueler.size()
					+ ") ist gr�sser als Facharbeitspl�tze in den Kursen vorhanden sind ("
					+ FAKurs.FASchuelerListe.getAnzPlaetzeAllerKurse() + ").");
			FAGUI.log.info("F�hre Verteilung trotzdem durch.");
		}

		int aktPrio = 1;

		while (aktPrio <= FAWahlZettel.MAX_PRIO
				&& listeNichtVerteilteSchueler.size() != listeVerteilteSchueler.size()) {

			for (FASchueler schueler : listeNichtVerteilteSchueler) {

				// nur nicht verteilte Sch�ler ausw�hlen
				if (listeVerteilteSchueler.contains(schueler)) continue;

				// Kurs mit aktueller Priorit�t ausw�hlen
				FAKurs kurs = schueler.getWahlZettel().getKurs(aktPrio);

				// Fehler wenn Kurs nicht existiert und Beenden des jetzigen
				// Schleifendurchlaufs -> mit n�chstem Sch�ler weiter
				if (kurs == null) {
					FAGUI.log.severe("Sch�ler \'" + schueler.getName() + ", "
							+ schueler.getVorname()
							+ "\' hat keine Kurswahl mit Priorit�t \'" + aktPrio
							+ "\'. Kann keine Verteilung mit dieser Priorit�t vornehmen.");
					continue;
				} 

				// Wenn im Kurs noch Platz f�r einen weiteren Sch�ler ist
				if (kurs.getSchuelerListe().hatNochPlatz()) {
					// Sch�ler zum Kurs hinzuf�gen
					kurs.getSchuelerListe().addSchueler(schueler); 
					// zur Liste verteilter Sch�ler hinzuf�gen
					listeVerteilteSchueler.add(schueler); 
					// Fixierte Wahl setzen
					schueler.getWahlZettel().setFixierteWahl(aktPrio); 

					FAGUI.log.info("Sch�ler \'" + schueler.getName()
							+ ", " + schueler.getVorname()
							+ "\' wurde auf Kurs [" + kurs.toString()
							+ "] verteilt. Priorit�t " + aktPrio
							+ " wurde erf�llt.");
				} // Ende if

			} // Ende for
			
			// Mit n�chster Priorit�t fortfahren
			aktPrio++;

		} // Ende while

		// Nicht-verteilte Sch�ler bestimmen
		listeNichtVerteilteSchueler = new ArrayList<FASchueler>();

		iter = alleSchueler.iterator();
		while (iter.hasNext()) {

			FASchueler schueler = iter.next();

			if (schueler.schreibtFacharbeit() && !listeVerteilteSchueler.contains(schueler)) {
				if (einmischen(schueler)) { // Nicht verteilten Sch�ler einmischen
					                        // -> Einmischen erfolgreich
					FAGUI.log.info("Sch�ler \'" + schueler.getName() + ", "
							+ schueler.getVorname() + " wurde eingemischt");
				} else { // -> Einmischen nicht erfolgreich
					listeNichtVerteilteSchueler.add(schueler);
					FAGUI.log.warning("Sch�ler \'" + schueler.getName() + ", "
							+ schueler.getVorname()
							+ "\' konnte letztlich nicht verteilt werden.");
				}
			}
		}

		// Final nicht verteilte Sch�ler ermitteln
		if (listeNichtVerteilteSchueler.size() > 0) {
			FAGUI.log.warning("Insgesamt konnten " + listeNichtVerteilteSchueler.size()
					+ " von " + alleSchueler.size() + " Sch�lern nicht verteilt werden.");
		} else FAGUI.log.info("Alle Sch�ler konnten verteilt werden :-)");

		// G�te der Verteilung berechnen und zur�ckgeben
		return gueteBerechnen();

	}
	
	/**
	 * Berechnet die G�te der Verteilung.
	 * 
	 * @return G�te der Verteilung
	 * */
	private static double gueteBerechnen() {
		// G�te
		double guete = 0;
		
		// anzahlen[0] -> Anzahl nicht verteilte Sch�ler; anzahlen[x] 
		// -> Anzahl Sch�ler mit Priorit�t x erf�llt
		int[] anzahlen = new int[FAWahlZettel.MAX_PRIO + 1];
		
		// F�r jeden Sch�ler �berpr�fen
		for (FASchueler schueler : FASchueler.getAlleSchreiberlinge()) {
			FAWahlZettel wz = schueler.getWahlZettel();
			if (wz.existiertFixierteWahl()) anzahlen[wz.getFixierteWahl()]++;
			else anzahlen[0]++;
		}
		
		// Anzahlen in G�te umrechnen
		for (int prio = 1; prio <= FAWahlZettel.MAX_PRIO; prio++) {
			guete += anzahlen[prio] * (FAWahlZettel.MAX_PRIO + 1 - prio);
		}
		
		// Nicht verteilte Sch�ler von G�te abziehen
		guete -= anzahlen[0] * FAWahlZettel.MAX_PRIO;
		
		// Durchschnittliche G�te berechnen -> Gesamtg�te
		guete = guete / (FASchueler.getAlleSchreiberlinge().size() * FAWahlZettel.MAX_PRIO);
		
		FAGUI.log.info("G�te der Verteilung ist: " + guete);
		
		return guete;
	}
	
	/**
	 * Mischt den nicht verteilten {@link FASchueler} <code>pSchueler</code> ein.
	 * 
	 * @param pSchueler Einzumischender Sch�ler
	 * @return Erfolg des Einmischens
	 * */
	private static boolean einmischen(FASchueler pSchueler) {
		
		// Iterator �ber alle Instanzen
		Iterator<FASchueler> alle = FASchueler.getAlleInstanzen().iterator();
		
		// Verteilte Sch�ler
		ArrayList<FASchueler> verteilt = new ArrayList<>();
		
		// F�llen von 'verteilt' mit allen verteilten Sch�lern
		while (alle.hasNext()) {
			FASchueler schueler = alle.next();
			if (schueler.schreibtFacharbeit()
					&& schueler.getWahlZettel().existiertFixierteWahl()) {
				verteilt.add(schueler);
			}
		}
			
		FAGUI.log.info("Sch�ler \'" + pSchueler.getName() + ", "
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
	 * der Priorit�t <code>pPrio</code> einzumischen indem der {@link FASchueler} 
	 * <code>pVerteilter</code> umverteilt wird.
	 * 
	 * @param pEinzumischender Einzumischender Sch�ler
	 * @param pVerteilter Bereits verteilter Sch�ler
	 * @param pPrio Priorit�t des Kurses, in den der Sch�ler eingemischt werden soll
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
	 * mit der Priorit�t <code>wechselK</code> ein, indem der {@link FASchueler}
	 * <code>ersatzS</code> auf seinen {@link FAKurs} mit der Priorit�t <code>ersatzK</code>
	 * umverteilt wird.
	 * 
	 * @param einzumischenS Einzumischender Sch�ler
	 * @param ersatzS Sch�ler, der umverteilt wird
	 * @param wechselK Priorit�t des Kurses �ber den getauscht wird
	 * @param ersatzK Priorit�t des Ersatzkurses f�r den Sch�ler der, umverteilt wird
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
