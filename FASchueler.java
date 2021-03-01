import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Ein <code>FASchueler</code>-Objekt repräsentiert einen Schüler, für den ein
 * {@link FAKurs}, also ein Kurs, in dem der Schüler eine Facharbeit anfertigt,
 * bestimmt werden muss.</p>
 * 
 * Ein <code>FASchueler</code> hat eine ID, einen Namen und Vornamen sowie ein
 * {@link FAWahlZettel}-Objekt, der seine Wahlwünsche nach Prioritäten
 * verwaltet.</p>
 * 
 * Es können nicht direkt Instanzen der Klasse <code>FASchueler</code> erzeugt
 * werden. Vielmehr verwaltet diese Klasse einen Instanzen-Cache, aus dem
 * Instanzen angefragt werden müssen. Angefragte, bereits existierende Instanzen
 * werden wieder verwendet. Existiert eine angefragte Instanz noch nicht, so
 * wird sie neu erzeugt und dem Instanzen-Cache hinzugefügt. So werden Duplikate
 * vermieden und jedes <code>FASchueler</code>-Objekt ist eindeutig.</p>
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 * @version 0.1
 * @since 10/10/2013
 */

public class FASchueler implements Serializable, Comparable<FASchueler> {

	// static

	private static final long serialVersionUID = 1L;

	private static HashMap<String, FASchueler> sInstanzenCache = new HashMap<String, FASchueler>();
	
	private static ArrayList<WahlomatListener> wListeners = new ArrayList<>();
	
	/**
	 * Gibt den {@link FASchueler} mit dem Namen <code>pSchuelerName</code>, 
	 * dem Vornamen <code>pSchuelerVorname</code> und den Wahlen <code>pWahlen</code>
	 * zurück. (Diese Methode ist nötig, damit die Listener erst benachtichtigt
	 * werden, wenn die Schüler Instanzen bereits erzeugt sind)
	 * 
	 * @param pSchuelerName Name des Schülers
	 * @param pSchuelerVorname Vorname des Schülers
	 * @param pWahlen Wahlen des Schülers
	 * @return der gesuchte Schüler
	 * */
	public static FASchueler instanzFuer(String pSchuelerName, String pSchuelerVorname, String[] pWahlen) {
		FASchueler schueler;
		if (pWahlen != null && pWahlen.length > 0) {
			schueler = instanzFuer(-1, pSchuelerName, pSchuelerVorname, true);
			
			FAWahlZettel wz = schueler.getWahlZettel();
			for (int i = 0; i < (pWahlen.length/2); i++) {
				wz.addWahl((i+1), FAKurs.instanzFuer(pWahlen[i*2], pWahlen[(i*2)+1]));
			}
		} else {
			schueler = instanzFuer(-1, pSchuelerName, pSchuelerVorname, false);
		}
		
		bearbeitet();
		
		return schueler;
	}

	/**
	 * Gibt den Schüler mit den angegeben Attributen zurück
	 * 
	 * @param pSchuelerID ID des Schülers
	 * @param pSchuelerName Name des Schülers
	 * @param pSchuelerVorname Vorname des Schülers
	 * @param pSchreibtFacharbeit Ob der Schüler eine Facharbeit schreibt
	 * @return der gesuchte Schüler
	 * */
	public static FASchueler instanzFuer(int pSchuelerID, String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) throws IllegalArgumentException {

		if (pSchuelerName == null || pSchuelerName.isEmpty() || pSchuelerVorname == null || pSchuelerVorname.isEmpty()) {
			throw new IllegalArgumentException("Entweder Argument \'pSchuelerName\' ist null/ leer "
					+ "oder Argument \'pSchuelerVorname\' ist null/ leer. "
					+ "Es ist nicht sinnvoll, eine Schueler-Instanz mit diesen Attributen zu erzeugen!");
		}

		String key = schluesselFuer(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit);
		
		if (existiertInstanzFuer(key)) return sInstanzenCache.get(key);
		else {
			FASchueler schueler = new FASchueler(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit);
			sInstanzenCache.put(key, schueler);
			return schueler;
		}
	}
	
	/**
	 * Gibt den Schüler mit den angegeben Attributen zurück
	 * 
	 * @param pSchuelerName Name des Schülers
	 * @param pSchuelerVorname Vorname des Schülers
	 * @param pSchreibtFacharbeit Ob der Schüler eine Facharbeit schreibt
	 * @return der gesuchte Schüler
	 * */
	public static FASchueler instanzFuer(String pName, String pVorname, boolean pSchreibtFacharbeit) {
		return instanzFuer(-1, pName, pVorname, pSchreibtFacharbeit);
	}

	/**
	 * Gibt den Schüler mit den angegeben Attributen zurück
	 * 
	 * @param pSchuelerName Name des Schülers
	 * @param pSchuelerVorname Vorname des Schülers
	 * @return der gesuchte Schüler
	 * */
	public static FASchueler instanzFuer(String pSchuelerName, String pSchuelerVorname) throws IllegalArgumentException {
		return instanzFuer(-1, pSchuelerName, pSchuelerVorname, true);
	}

	/**
	 * Gibt den Schlüssel für den Schüler mit den angegebnen Attributen zurück.
	 * */
	protected static String schluesselFuer(int pID, String pName, String pVorname, boolean pSchreibtFacharbeit) {
		return "SchuelerID=" + pID + ", SchuelerName=" + pName + ", SchuelerVorname=" + pVorname + ", SchreibtFacharbeit=" + pSchreibtFacharbeit;
	}

	public static boolean istInstanzenCacheLeer() {
		return sInstanzenCache.isEmpty();
	}

	/**
	 * Leert den Instanzen Cache
	 * */
	public static void leereInstanzenCache() {
		for (FASchueler schueler : getAlleInstanzen()) {
			entferneInstanzFuer(schueler);
		}
	}
	
	/**
	 * Entfernt die Instanz für den {@link FASchueler} <code>pSchueler</code>.
	 * 
	 * @param pSchueler zu entfernender Schüler
	 * */
	public static void entferneInstanzFuer(FASchueler pSchueler) {
		entferneInstanzFuer(pSchueler.getID(), pSchueler.getName(), pSchueler.getVorname(), pSchueler.schreibtFacharbeit());
	}
	
	/**
	 * Enfernt die Instanz für den Schüler mit den angegebenen Attributen
	 * */
	public static void entferneInstanzFuer(int pSchuelerID, String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) {
		if (existiertInstanzFuer(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit)) {
			FASchueler schueler = instanzFuer(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit);
			sInstanzenCache.remove(schluesselFuer(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit));
			schueler.dispose();
			schueler = null;
			
			bearbeitet();
		}
	}
	
	public static void entferneInstanzFuer (String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) {
		entferneInstanzFuer(-1, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit);
	}
	
	public static boolean existiertInstanzFuer(String key) {
		return sInstanzenCache.containsKey(key);
	}
	
	public static boolean existiertInstanzFuer(int pSchuelerID, String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) {
		return existiertInstanzFuer(schluesselFuer(pSchuelerID, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit));
	}
	
	public static boolean existiertInstanzFuer(String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) {
		return existiertInstanzFuer(-1, pSchuelerName, pSchuelerVorname, pSchreibtFacharbeit);
	}
	
	public static void addWahlomatListener(WahlomatListener listener) {
		if (listener != null) wListeners.add(listener);
	}
	
	public static boolean removeWahlomatListener(WahlomatListener listener) {
		return wListeners.remove(listener);
	}
	
	public static HashMap<String, FASchueler> getInstanzenCache() {
		return sInstanzenCache;
	}
	
	public static void setInstanzenCache(HashMap<String, FASchueler> pInstanzenCache) {
		sInstanzenCache = pInstanzenCache;
	}

	public static int getAnzahlInstanzen() {
		return sInstanzenCache.size();
	}

	public static Collection<FASchueler> getAlleInstanzen() {
		return sInstanzenCache.values();
	}
	
	/**
	 * Gibt eine {@link Collection} aller Schüler zurück, die eine Facharbeit schreiben
	 * */
	public static Collection<FASchueler> getAlleSchreiberlinge() {
		ArrayList<FASchueler> schreiberlinge = new ArrayList<>();
		Iterator<FASchueler> alle = sInstanzenCache.values().iterator();
		while (alle.hasNext()) {
			FASchueler schueler = (FASchueler) alle.next();
			if (schueler.schreibtFacharbeit()) schreiberlinge.add(schueler);
		}
		return schreiberlinge;
	}

	public static String instanzenToString() {

		String instanzenAlsString = "";

		if (!sInstanzenCache.isEmpty()) {

			Collection<FASchueler> c = getAlleInstanzen();
			Iterator<FASchueler> iter = c.iterator();

			while (iter.hasNext()) {
				FASchueler schueler = iter.next();
				instanzenAlsString += schueler.toString() + "\n";
			}

		} else {
			instanzenAlsString = "Es existiert keine Instanz der Klasse FASchueler.";
		}

		return instanzenAlsString;

	}
	
	/**
	 * Benarichtigt die Listener
	 * */
	public static void bearbeitet() {
		for (WahlomatListener listener : wListeners) {
			listener.schuelerBearbeitet();
		}
	}
	

	// end static

	// -------------------

	// non-static

	private int mID;
	private String mName;
	private String mVorname;
	private FAWahlZettel mWahlZettel;
	private boolean mSchreibtFacharbeit;

	private FASchueler(int pSchuelerID, String pSchuelerName, String pSchuelerVorname, boolean pSchreibtFacharbeit) {
		setID(pSchuelerID);
		setName(pSchuelerName);
		setVorname(pSchuelerVorname);
		setSchreibtFacharbeit(pSchreibtFacharbeit);

		if (pSchreibtFacharbeit) mWahlZettel = new FAWahlZettel(this);
		else mWahlZettel = null;
	}
	
	private FASchueler(String pName, String pVorname, boolean pSchreibtFacharbeit) {
		this(-1, pName, pVorname, pSchreibtFacharbeit);
	}

	private FASchueler(String pName, String pVorname) {
		this(-1, pName, pVorname, true);
	}
	
	// Getter & Setter

	public int getID() {
		return mID;
	}

	public String getName() {
		return mName;
	}

	public String getVorname() {
		return mVorname;
	}

	public FAWahlZettel getWahlZettel() {
		return mWahlZettel;
	}

	public void setID(int pID) {
		mID = pID;
	}

	public void setName(String pName) {
		if (pName != null && !pName.isEmpty())
			mName = pName;
	}

	public void setVorname(String pVorname) {
		if (pVorname != null && !pVorname.isEmpty())
			mVorname = pVorname;
	}
	
	public boolean schreibtFacharbeit() {
		return mSchreibtFacharbeit;
	}

	public void setSchreibtFacharbeit(boolean pSchreibtFacharbeit) {
		this.mSchreibtFacharbeit = pSchreibtFacharbeit;
	}
	
	// Ende Getter & Setter

	public void dispose() {
		mID = 0;                 
		mName = null;            
		mVorname = null;
		if (schreibtFacharbeit()) mWahlZettel.dispose();
		mWahlZettel = null;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof FASchueler) {

			FASchueler schuelerZuVergleichen = (FASchueler) obj;

			return this.mID == schuelerZuVergleichen.getID()
					&& this.mName.equals(schuelerZuVergleichen.getName())
					&& this.mVorname.equals(schuelerZuVergleichen.getVorname())
					&& this.mWahlZettel == schuelerZuVergleichen
							.getWahlZettel();

		} else
			return super.equals(obj);
	}

	@Override
	public String toString() {

		String schuelerAlsString = "";

		schuelerAlsString += "SchuelerName=\'" + mName + "\'";
		schuelerAlsString += ", SchuelerVorname=\'" + mVorname + "\'";
		schuelerAlsString += ", SchuelerID=\'" + (mID > 0? mID : "NaN") + "\'";
		if (schreibtFacharbeit()) schuelerAlsString += ", WahlZettel=" + mWahlZettel.toString();

		return schuelerAlsString;

	}

	@Override
	public int compareTo(FASchueler pSchueler) {
		return (mName + mVorname).toUpperCase().compareTo((pSchueler.getName()
				+ pSchueler.getVorname()).toUpperCase());
	}

} // end class FASchueler

