import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * (De-)serializiert {@link FAObject}e und speichert und lädt dadurch Wahlen und
 * eine Verteilung in beziehungsweise aus einer Datei.
 * 
 * @author Tobias Heuser
 * */
public class FASerializer {
	
	/**
	 * Lädt die Wahlen und die Verteilung aus der Datei <code>pFile</code>.
	 * */
	public static void load(File pFile) {
		if (pFile != null) {
			FAObject object = null;
			try {
				FileInputStream fileIn = new FileInputStream(pFile);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				object = (FAObject) in.readObject();
				in.close();
				fileIn.close();
				FASchueler.setInstanzenCache(object.getSchuelerCache());
				FAKurs.setInstanzenCache(object.getKurseCache());
				FAGUI.setGuete(object.getGuete());
			} catch (EOFException e) {
				System.out.println("Eingabedatei ist leer");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Speichert in die Datei <code>pFile</code>.
	 * */
	public static void save(File pFile) {
		if (pFile != null) {
			FAObject object = new FAObject();
			object.setSchuelerCache(FASchueler.getInstanzenCache());
			object.setKurseCache(FAKurs.getInstanzenCache());
			object.setGuete(FAGUI.getGuete());
			try {
				FileOutputStream fileOut = new FileOutputStream(pFile);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(object);
				out.close();
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
