import java.awt.Dimension;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Stellt Hilfsmethoden zur Verf�gung
 * 
 * @author Tobias Heuser
 * */
public class FAHelper {
	
	/**
	 * Erstellt einen vertikalen {@link JSeparator}.
	 * */
 	public static JSeparator sep() {
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setPreferredSize(new Dimension(1, 20));
		return sep;
	}
	
	/**
	 * Bereinigt Strings, die durch Eingabemethoden fehler enthalten
	 * */
	public static String macheLesbar(String str) {
		if (str != null) {
			str = str.replaceAll("\"", "").replaceAll("ö", "�")
					  .replaceAll("ü", "�").replaceAll("ä", "�")
					  .replaceAll("Ä", "�").replaceAll("Ö", "�")
					  .replaceAll("Ü", "�").replaceAll("ß", "�");
		}
		
		return str;
	}
}
