import java.io.Serializable;
import java.util.*;

/**
 * Ein <code>FAWahlZettel</code>-Objekt verwaltet für einen {@link FASchueler}
 * die Menge seiner gewählten {@link FAKurs}e, in denen dieser jeweils mit einer
 * gewissen Priorität eine Facharbeit anfertigen würde. Nur aus dieser Menge von
 * Kursen soll dem {@link FASchueler} ein {@link FAKurs} zwecks
 * Facharbeitsanfertigung zugeordnet werden.</p>
 * 
 * Die dynamisch änderbare Menge von Kurswahlen besteht aus 2-Tupeln. Jedes
 * 2-Tupel wird aus einer ganzzahligen, nicht-negativen Priorität und einem
 * {@link FAKurs} gebildet. Die Priorität gibt die Wertigkeit an, mit der der
 * Schüler in dem Kurs eine Facharbeit anfertigen würde.</p>
 * 
 * Genau eine Kurswahl kann fixiert werden, was bedeuten soll, dass der Schüler
 * in dem fixierten Kurs letztlich seine Facharbeit anfertigt.</p>
 * 
 * Eine fixierte Wahl kann solange nicht aus der Menge der Kurswahlen entfernt
 * werden bis ihre Fixierung aufgehoben wurde.
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 */

public class FAWahlZettel implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int MAX_ANZAHL_WAHLEN = 3;

	public static final int MIN_PRIO = 1;
	public static final int MAX_PRIO = MAX_ANZAHL_WAHLEN;

	/**
	 * Der Schüler, dem diese FAKursWahlen zugeordnet sind.
	 */
	private FASchueler mSchueler;

	/**
	 * Die die (Teil)Wahlen speichernde Map. Jede Wahl wird als Paar aus
	 * Priorität und Kurs gespeichert.
	 */
	private HashMap<Integer, FAKurs> mWahlen = new HashMap<Integer, FAKurs>();

	/**
	 * Die fixierte Kurswahl gespeichert durch ihre Priorität.
	 */
	private int mFixierteWahl = -1;

	/**
	 * Konstruktor
	 * 
	 * @param pSchueler {@link FASchueler} zu dem dieser Wahlzettel gehört
	 * */
	public FAWahlZettel(FASchueler pSchueler) throws IllegalArgumentException {
		if (pSchueler == null) {
			throw new IllegalArgumentException("Argument pSchueler ist null!");
		}

		mSchueler = pSchueler;
	}

	/**
	 * Fügt eine Wahl des {@link FAKurs}es <code>pKurs</code> mit der Priorität
	 * <code>pPrio</code> zum Wahlzettel hinzu.
	 * 
	 * @param pPrioritaet Priorität der Wahl
	 * @param pKurs gewählter Kurs
	 * */
	public void addWahl(int pPrioritaet, FAKurs pKurs)
			throws IllegalArgumentException, NullPointerException, IllegalStateException {

		if (pPrioritaet < MIN_PRIO || pPrioritaet > MAX_PRIO) {
			throw new IllegalArgumentException( "Für Schüler \'" + mSchueler.getName()
					+ ", " + mSchueler.getVorname()
					+ "\' kann keine Kurswahl mit der Priorität " + pPrioritaet
					+ " durchgeführt werden. Kleinste und größte erlaubte Prioritäten sind \'"
					+ MIN_PRIO + "\' bzw. \'" + MAX_PRIO + "\'.");
		}

		if (pKurs == null) {
			throw new NullPointerException("Argument pKurs ist null! Kann keine Kurswahl mit"
					+ "leerem Kurs für Schüler " + mSchueler.getName() + ", "
					+ mSchueler.getVorname() + " hinzufügen.");
		}

		if (mWahlen.containsKey(pPrioritaet)) {
			throw new IllegalArgumentException("FÜr SchÜler \'" + mSchueler.getName() + ", "
					+ mSchueler.getVorname()
					+ "\' existiert bereits eine Kurswahl mit der Priorität "
					+ pPrioritaet + ": "
					+ mSchueler.getWahlZettel().getKurs(pPrioritaet).toString()
					+ ". Kann keine Kurswahl mit gleicher Priorität hinzufügen.");
		}

		mWahlen.put(new Integer(pPrioritaet), pKurs);
	}

	/**
	 * Enfernt die Wahl mit der Priorität <code>pPrioritaet</code>.
	 * 
	 * @param pPrioritaet Priorität der zu entfernenden Wahl
	 * */
	public FAKurs removeWahl(int pPrioritaet) {
		FAKurs kurs = mWahlen.remove(pPrioritaet);
		
		return kurs;
	}

	/**
	 * Überprüft ob eine fixierte Wahl exisitert.
	 * 
	 * @return Existenz einer fixierten Wahl
	 * */
	public boolean existiertFixierteWahl() {
		return mFixierteWahl != -1;
	}
	
	/**
	 * Entfernt diesen Wahlzettel
	 * */
	public void dispose() {
		mSchueler = null;
		mFixierteWahl = 0;
		for (Integer integer : mWahlen.keySet()) {
			FAKurs kurs = mWahlen.get(integer);
			kurs.getSchuelerListe().removeSchueler(mSchueler);
		}
		mWahlen.clear();
		mWahlen = null;
	}

	// Getter & Setter
	
	public int getFixierteWahl() {
		return mFixierteWahl;
	}

	public FASchueler getSchueler() {
		return mSchueler;
	}

	public HashMap<Integer, FAKurs> getWahlen() {
		return mWahlen;
	}

	public FAKurs getKurs(int pPrioritaet) {
		return mWahlen.get(pPrioritaet);
	}

	public Collection<FAKurs> getGewaehlteKurse() {
		return mWahlen.values();
	}

	public Set<Integer> getPrioritaeten() {
		return mWahlen.keySet();
	}

	public void setFixierteWahl(int pPrioritaet) throws IllegalArgumentException {
		if (!mWahlen.containsKey(pPrioritaet)) {
			throw new IllegalArgumentException("Kann PrioritÃ¤t " + pPrioritaet + " nicht fixieren. Die angegebene PrioritÃ¤t existiert nicht.");
		}

		mFixierteWahl = pPrioritaet;
	}

	public FAKurs getFixiertenKurs() {
		return mWahlen.get(mFixierteWahl);
	}

	// Ende Getter & Setter
	
	/**
	 * Setzt die fixierte Wahl zurück
	 * */
	public void resetFixierteWahl() {
		mFixierteWahl = -1;
	}

	@Override
	public String toString() {

		String kursWahlAlsString = "";

		if (mWahlen.isEmpty()) {
			kursWahlAlsString = "[]";
		} else {

			Set<Integer> keys = mWahlen.keySet();
			Iterator<Integer> iter = keys.iterator();

			kursWahlAlsString = "[";

			while (iter.hasNext()) {
				Integer i = iter.next();
				FAKurs kurs = mWahlen.get(i);

				kursWahlAlsString += "[" + "PrioritÃ¤t=\'" + i + " \', " + kurs.toString() + (iter.hasNext() ? "], " : "]");
			}

			kursWahlAlsString += ", FixierteWahl=\'" + (existiertFixierteWahl() ? getFixierteWahl() : "NULL") + "\']";

		}

		return kursWahlAlsString;

	}
}
