import java.io.Serializable;
import java.util.*;

/**
 * Ein <code>FAWahlZettel</code>-Objekt verwaltet f�r einen {@link FASchueler}
 * die Menge seiner gew�hlten {@link FAKurs}e, in denen dieser jeweils mit einer
 * gewissen Priorit�t eine Facharbeit anfertigen w�rde. Nur aus dieser Menge von
 * Kursen soll dem {@link FASchueler} ein {@link FAKurs} zwecks
 * Facharbeitsanfertigung zugeordnet werden.</p>
 * 
 * Die dynamisch �nderbare Menge von Kurswahlen besteht aus 2-Tupeln. Jedes
 * 2-Tupel wird aus einer ganzzahligen, nicht-negativen Priorit�t und einem
 * {@link FAKurs} gebildet. Die Priorit�t gibt die Wertigkeit an, mit der der
 * Sch�ler in dem Kurs eine Facharbeit anfertigen w�rde.</p>
 * 
 * Genau eine Kurswahl kann fixiert werden, was bedeuten soll, dass der Sch�ler
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
	 * Der Sch�ler, dem diese FAKursWahlen zugeordnet sind.
	 */
	private FASchueler mSchueler;

	/**
	 * Die die (Teil)Wahlen speichernde Map. Jede Wahl wird als Paar aus
	 * Priorit�t und Kurs gespeichert.
	 */
	private HashMap<Integer, FAKurs> mWahlen = new HashMap<Integer, FAKurs>();

	/**
	 * Die fixierte Kurswahl gespeichert durch ihre Priorit�t.
	 */
	private int mFixierteWahl = -1;

	/**
	 * Konstruktor
	 * 
	 * @param pSchueler {@link FASchueler} zu dem dieser Wahlzettel geh�rt
	 * */
	public FAWahlZettel(FASchueler pSchueler) throws IllegalArgumentException {
		if (pSchueler == null) {
			throw new IllegalArgumentException("Argument pSchueler ist null!");
		}

		mSchueler = pSchueler;
	}

	/**
	 * F�gt eine Wahl des {@link FAKurs}es <code>pKurs</code> mit der Priorit�t
	 * <code>pPrio</code> zum Wahlzettel hinzu.
	 * 
	 * @param pPrioritaet Priorit�t der Wahl
	 * @param pKurs gew�hlter Kurs
	 * */
	public void addWahl(int pPrioritaet, FAKurs pKurs)
			throws IllegalArgumentException, NullPointerException, IllegalStateException {

		if (pPrioritaet < MIN_PRIO || pPrioritaet > MAX_PRIO) {
			throw new IllegalArgumentException( "F�r Sch�ler \'" + mSchueler.getName()
					+ ", " + mSchueler.getVorname()
					+ "\' kann keine Kurswahl mit der Priorit�t " + pPrioritaet
					+ " durchgef�hrt werden. Kleinste und gr��te erlaubte Priorit�ten sind \'"
					+ MIN_PRIO + "\' bzw. \'" + MAX_PRIO + "\'.");
		}

		if (pKurs == null) {
			throw new NullPointerException("Argument pKurs ist null! Kann keine Kurswahl mit"
					+ "leerem Kurs f�r Sch�ler " + mSchueler.getName() + ", "
					+ mSchueler.getVorname() + " hinzuf�gen.");
		}

		if (mWahlen.containsKey(pPrioritaet)) {
			throw new IllegalArgumentException("F�r Sch�ler \'" + mSchueler.getName() + ", "
					+ mSchueler.getVorname()
					+ "\' existiert bereits eine Kurswahl mit der Priorit�t "
					+ pPrioritaet + ": "
					+ mSchueler.getWahlZettel().getKurs(pPrioritaet).toString()
					+ ". Kann keine Kurswahl mit gleicher Priorit�t hinzuf�gen.");
		}

		mWahlen.put(new Integer(pPrioritaet), pKurs);
	}

	/**
	 * Enfernt die Wahl mit der Priorit�t <code>pPrioritaet</code>.
	 * 
	 * @param pPrioritaet Priorit�t der zu entfernenden Wahl
	 * */
	public FAKurs removeWahl(int pPrioritaet) {
		FAKurs kurs = mWahlen.remove(pPrioritaet);
		
		return kurs;
	}

	/**
	 * �berpr�ft ob eine fixierte Wahl exisitert.
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
			throw new IllegalArgumentException("Kann Priorität " + pPrioritaet + " nicht fixieren. Die angegebene Priorität existiert nicht.");
		}

		mFixierteWahl = pPrioritaet;
	}

	public FAKurs getFixiertenKurs() {
		return mWahlen.get(mFixierteWahl);
	}

	// Ende Getter & Setter
	
	/**
	 * Setzt die fixierte Wahl zur�ck
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

				kursWahlAlsString += "[" + "Priorität=\'" + i + " \', " + kurs.toString() + (iter.hasNext() ? "], " : "]");
			}

			kursWahlAlsString += ", FixierteWahl=\'" + (existiertFixierteWahl() ? getFixierteWahl() : "NULL") + "\']";

		}

		return kursWahlAlsString;

	}
}
