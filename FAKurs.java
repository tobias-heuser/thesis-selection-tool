import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Ein <code>FAKurs</code>-Objekt repräsentiert einen Kurs, in dem {@link FASchueler}
 * eine Facharbeit anfertigen können.</p>
 * 
 * Ein <code>FAKurs</code> ist durch ein Fach, eine Kursart, eine Kursnummer und
 * einen Lehrer sowie eine Liste von {@link FASchueler}n charakterisiert. Die
 * dynamisch änderbare Liste der Schüler steht für die dem Kurs zugeordneten
 * Schüler, die in diesem Kurs ihre Facharbeit anfertigen.</p>
 * 
 * Es können nicht direkt Instanzen der Klasse <code>FAKurs</code> erzeugt
 * werden. Vielmehr verwaltet diese Klasse einen Instanzen-Cache, aus dem
 * Instanzen angefragt werden müssen. Angefragte, bereits existierende Instanzen
 * werden wieder verwendet. Existiert eine angefragte Instanz noch nicht, so
 * wird sie neu erzeugt und dem Instanzen-Cache hinzugefügt. So werden Duplikate
 * vermieden und jedes <code>FAKurs</code>-Objekt ist eindeutig.</p>
 * 
 * @author Christian Wolf
 * @author Tobias Heuser
 */

public class FAKurs implements Serializable, Comparable<FAKurs> {

	// ------------- static -------------

	// Instanzen-Cache
	
	private static final long serialVersionUID = 1L;

	private static HashMap<String, FAKurs> sInstanzenCache = new HashMap<String, FAKurs>();
	
	private static ArrayList<WahlomatListener> wListeners = new ArrayList<>();

	/**
	 * Gibt die FAKurs Instanz für die angegebenen Attribute zurück.
	 * */
	public static FAKurs instanzFuer(String pFach, String pKursart, int pKursnummer, String pLehrer) throws IllegalArgumentException {
		//Exceptions bei leeren Argumenten:
		if (pFach == null || pFach.isEmpty()) throw new IllegalArgumentException("Argument pFach ist null oder leer");
		else if (pKursart == null || pKursart.isEmpty()) throw new IllegalArgumentException("Argument pKursart ist null oder leer");
		else if (pLehrer == null || pLehrer.isEmpty()) throw new IllegalArgumentException("Argument pLehrer ist null oder leer");
		
		String key = schluesselFuer(pFach, pKursart, pKursnummer, pLehrer);

		if (existiertInstanzFuer(key)) return sInstanzenCache.get(key);
		else {
			FAKurs kurs = new FAKurs(pFach, pKursart, pKursnummer, pLehrer);
			sInstanzenCache.put(key, kurs);
			//Listener benachrichtigen
			bearbeitet();
			return kurs;
		}
	}

	/**
	 * Gibt die FAKurs Instanz für die angegebenen Attribute zurück.
	 * */
	public static FAKurs instanzFuer(String pFach, String pLehrer) throws IllegalArgumentException {
		return instanzFuer(pFach, "[GK|LK]", -1, pLehrer);
	}

	/**
	 * Gibt den Schlüssel für die FAKurs Instanz mit den angegebenen Attribute zurück.
	 * */
	private static String schluesselFuer(String pFach, String pKursart, int pKursnummer, String pLehrer) {
		return "Fach=" + pFach + ", Kursart=" + pKursart + ", Kursnr.=" + pKursnummer + ", Lehrer=" + pLehrer;
	}

	public static boolean istInstanzenCacheLeer() {
		return sInstanzenCache.isEmpty();
	}
	
	public static boolean removeInstanzFuer(int pKursnummer, String pFach, String pLehrer) {
		String key = schluesselFuer(pFach, "[GK|LK]", pKursnummer, pLehrer);
		if (existiertInstanzFuer(key)) {
			FAKurs kurs = sInstanzenCache.remove(key);
			kurs.dispose();
			bearbeitet();
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean existiertInstanzFuer(String key) {
		return sInstanzenCache.containsKey(key);
	}
	
	public static boolean existiertInstanzFuer(String pFach, String pKursart, int pKursnummer, String pLehrer) {
		return existiertInstanzFuer(schluesselFuer(pFach, pKursart, pKursnummer, pLehrer));
	}
	
	public static boolean existiertInstanzFuer(String pFach, String pLehrer) {
		return existiertInstanzFuer(pFach, "[GK|LK]", -1, pLehrer);
	}
	
	public static HashMap<String, FAKurs> getInstanzenCache() {
		return sInstanzenCache;
	}
	
	public static void setInstanzenCache(HashMap<String, FAKurs> pInstnzenCache) {
		sInstanzenCache = pInstnzenCache;
	}

	public static int getAnzahlInstanzen() {
		return sInstanzenCache.size();
	}

	public static Collection<FAKurs> getAlleInstanzen() {
		return sInstanzenCache.values();
	}
	
	public static void leereInstanzenCache() {
		sInstanzenCache.clear();
	}
	
	public static void addWahlomatListener(WahlomatListener listener) {
		wListeners.add(listener);
	}
	
	public static boolean removeWahlomatListener(WahlomatListener listener) {
		return wListeners.remove(listener);
	}
	
	private static void bearbeitet() {
		for (WahlomatListener listener : wListeners) {
			listener.kursBearbeitet();
		}
	}

	public static String instanzenToString() {

		String instanzenAlsString = "";

		if (!sInstanzenCache.isEmpty()) {

			Collection<FAKurs> c = getAlleInstanzen();
			Iterator<FAKurs> iter = c.iterator();

			while (iter.hasNext()) {
				FAKurs kurs = iter.next();
				instanzenAlsString += kurs.toString() + "\n";
			}

		} else {
			instanzenAlsString = "Es existiert keine Instanz der Klasse FAKurs.";
		}

		return instanzenAlsString;

	}

	// Ende Instanzen-Cache

	// Schülerliste

	/**
	 * Innere Klasse FASchuelerListe, die die Schüler verwaltet, die dem
	 * zugehörigen {@link FAKurs} zugewiesen wurden.
	 * */
	public static class FASchuelerListe implements Serializable{
		private static final long serialVersionUID = 1L;

		private static int mDefaultMaxAnzSchueler = 5;
		private static ArrayList<Integer> mMaxAnzahlen = new ArrayList<>();
		
		private ArrayList<FASchueler> mSchueler = new ArrayList<>();
		
		private int mMaxAnzSchueler = -1;
		
		public boolean addSchueler(FASchueler pSchueler) {
			if (pSchueler != null && getAnzahlSchueler() < getMaxAnzSchueler()) {
				mSchueler.add(pSchueler);
				
				return true;
			} else return false;
		}

		public boolean removeSchueler(FASchueler pSchueler) {
			return mSchueler.remove(pSchueler);
		}

		public void removeAlleSchueler() {
			mSchueler.clear();
		}
		
		public void dispose() {
			removeAlleSchueler();
			mSchueler = null;
		}
		
		/**
		 * Gibt die global größte maximale Schüleranzahl zurück.
		 * */
		public static int getGlobalMaxAnzSchueler() {
			int max = mDefaultMaxAnzSchueler;
			for (Integer i : mMaxAnzahlen) {
				if (i.intValue() > max) max = i.intValue();
			}
			return max;
		}
		
		/**
		 * Gibt die Summe aller Facharbeitsplätze in allen Kursen zurück
		 * */
		public static int getAnzPlaetzeAllerKurse() {
			int sum = mDefaultMaxAnzSchueler * (FAKurs.getAnzahlInstanzen() - mMaxAnzahlen.size());
			for (Integer i : mMaxAnzahlen) {
				sum += i.intValue();
			}
			return sum;
		}
		
		public static int getDefaultMaxAnzSchueler() {
			return mDefaultMaxAnzSchueler;
		}
		
		public static void setDefaultMaxAnzSchueler(int pDefaultMaxAnzSchueler) {
			if (pDefaultMaxAnzSchueler > 0) mDefaultMaxAnzSchueler = pDefaultMaxAnzSchueler;
		}
		
		public int getMaxAnzSchueler() {
			return (mMaxAnzSchueler > 0) ? mMaxAnzSchueler : mDefaultMaxAnzSchueler;
		} 
		
		public void setMaxAnzSchueler(int pMaxAnzSchueler) {
			if (mMaxAnzSchueler > 0) mMaxAnzahlen.remove(new Integer(mMaxAnzSchueler));
			mMaxAnzSchueler = pMaxAnzSchueler;
			mMaxAnzahlen.add(new Integer(pMaxAnzSchueler));
			bearbeitet();
		}
		
		/**
		 * Aktualisiert die Liste der maximalen Schüleranzahlen.
		 * */
		public static void updateGlobalMaxAnz() {
			for (FAKurs  kurs : sInstanzenCache.values()) {
				int maxAnz = kurs.getSchuelerListe().getMaxAnzSchueler();
				if (maxAnz != mDefaultMaxAnzSchueler) {
					mMaxAnzahlen.add(new Integer(maxAnz));
				}
			} 
		}
		
		
		public int getAnzahlSchueler() {
			return mSchueler.size();
		}

		public boolean istLeer() {
			return mSchueler.isEmpty();
		}

		public boolean hatNochPlatz() {
			return getAnzahlSchueler() < getMaxAnzSchueler();
		}

		public boolean istVoll() {
			return getAnzahlSchueler() == getMaxAnzSchueler();
		}

		public ArrayList<FASchueler> getSchueler() {
			return mSchueler;
		}
		
		@Override
		public String toString() {
			
			String schuelerAlsString = "[";
			
			for (FASchueler schueler : mSchueler) {
				schuelerAlsString += "[" + schueler.toString() + "]";
			}
			
			schuelerAlsString += "]";
			
			return schuelerAlsString;
		}

	} // Ende Klasse SchÃ¼lerliste

	// ------------- ende static -------------

	private String mFach;
	private String mKursart;
	private int mKursnummer;
	private String mLehrer;
	private FASchuelerListe mSchuelerListe;
//	private int gewaehltVon = 0;

	private FAKurs(String pFach, String pKursart, int pKursnummer, String pLehrer) {
		setFach(pFach);
		setKursart(pKursart);
		setKursnummer(pKursnummer);
		setLehrer(pLehrer);

		mSchuelerListe = new FASchuelerListe();
	}

	public FASchuelerListe getSchuelerListe() {
		return mSchuelerListe;
	}

	private FAKurs(String pFach, String pLehrer) {
		this(pFach, "", -1, pLehrer);
	}
	
	public void dispose() {
		mFach = null;
		mKursart = null;
		mKursnummer = 0;
		mLehrer = null;
		mSchuelerListe.dispose();
		mSchuelerListe = null;
	}

	public String getFach() {
		return mFach;
	}

	public String getKursart() {
		return mKursart;
	}

	public int getKursnummer() {
		return mKursnummer;
	}

	public String getLehrer() {
		return mLehrer;
	}

	public void setFach(String pFach) {
		if (pFach != null && !pFach.isEmpty())
			mFach = pFach;
	}

	public void setKursart(String pKursart) {
		if (pKursart != null && !pKursart.isEmpty())
			mKursart = pKursart;
	}

	public void setKursnummer(int pKursnummer) {
		mKursnummer = pKursnummer;
	}

	public void setLehrer(String pLehrer) {
		if (pLehrer != null && !pLehrer.isEmpty())
			mLehrer = pLehrer;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FAKurs) {
			FAKurs kursZuVergleichen = (FAKurs) obj;
			return this.mFach.equals(kursZuVergleichen.getFach())
					&& this.mKursart.equals(kursZuVergleichen.getKursart())
					&& this.mKursnummer == kursZuVergleichen.getKursnummer()
					&& this.mLehrer.equals(kursZuVergleichen.getLehrer());

		} else
			return super.equals(obj);
	}

	@Override
	public String toString() {

		String kursAlsString = "";

		kursAlsString += "Fach=\'" + mFach + "\'";
		kursAlsString += ", Lehrer=\'" + mLehrer + "\'";
		kursAlsString += ", Kursart=\'" + mKursart + "\'";
		kursAlsString += ", Kursnr.=\'" + (mKursnummer >0? mKursnummer : "NaN") + "\'";

		return kursAlsString;
	}

	@Override
	public int compareTo(FAKurs pKurs) {
		return (mLehrer + mFach).compareTo(pKurs.getLehrer() + pKurs.getFach());

	}

}
