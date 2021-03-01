/**
 * Eine Interface um Änderungen an {@link FASchueler}n oder {@link FAKurs}en von
 * der Quelle zu einem Verarbeiter zu kommunizieren.
 * */
public interface WahlomatListener {

	public void schuelerBearbeitet();
	
	public void kursBearbeitet();
}
