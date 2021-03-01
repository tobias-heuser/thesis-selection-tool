import java.util.HashMap;

/**
 * Ein <code>FAObject</code> ist ein serializierbares Objekt, in dem alle für eine
 * Verteilung relevanten Daten gespeichert sind.
 * 
 * @author Tobias Heuser
 * */
public class FAObject implements java.io.Serializable {
	private static final long serialVersionUID = 2L;

	// Cache mit allen Schülern
	private HashMap<String, FASchueler> schuelerCache;
	
	// Cache mit allen Kursen
	private HashMap<String, FAKurs> kurseCache;
	
	// Güte der Verteilung
	private double guete;

	// Getter & Setter
	
	public HashMap<String, FASchueler> getSchuelerCache() {
		return schuelerCache;
	}

	public void setSchuelerCache(HashMap<String, FASchueler> schuelerCache) {
		this.schuelerCache = schuelerCache;
	}

	public HashMap<String, FAKurs> getKurseCache() {
		return kurseCache;
	}

	public void setKurseCache(HashMap<String, FAKurs> kurseCache) {
		this.kurseCache = kurseCache;
	}

	public double getGuete() {
		return guete;
	}

	public void setGuete(double guete) {
		this.guete = guete;
	}
	
	// Ende Getter & Setter
}