package linuxPhone.prueba;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class CreacionRutaKML {

	private String nombre;
	private List<GeoPoint> camino;

	public CreacionRutaKML(String nombre, List<GeoPoint> camino)
			throws IllegalArgumentException, IllegalStateException, IOException {
		super();
		this.nombre = nombre;
		this.camino = camino;

		construccionKML();
	}

	private void construccionKML() throws IllegalArgumentException,
			IllegalStateException, IOException {
		// TODO Auto-generated method stub

		// Creamos el serializer
		XmlSerializer ser = Xml.newSerializer();

		// Creamos fichero en sd

		File ruta_sd = Environment.getExternalStorageDirectory();

		File f = new File(ruta_sd.getAbsolutePath(), "" + nombre + ".kml");

		OutputStreamWriter fout = new OutputStreamWriter(
				new FileOutputStream(f));

		// Asignamos el resultado del serializer al fichero
		ser.setOutput(fout);

		// Construimos el XML
		ser.startTag("", "kml xmlns=\"http://www.opengis.net/kml/2.2\"");

		for (int i = 0; i < camino.size(); i++) {
			ser.startTag("", "Placemark");
			ser.startTag("", "name");
			ser.text("Punto " + i);
			ser.endTag("", "name");
			ser.startTag("", "Point");
			ser.startTag("", "coordinates");
			ser.text("" + camino.get(i).getLatitudeE6() + ","
					+ camino.get(i).getLongitudeE6());
			ser.endTag("", "coordinates");
			ser.endTag("", "Point");
			ser.endTag("", "Placemark");
		}

		ser.endTag("", "kml xmlns=\"http://www.opengis.net/kml/2.2\"");
		ser.endDocument();

		fout.close();
	}

}
