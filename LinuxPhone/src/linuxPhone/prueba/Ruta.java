package linuxPhone.prueba;

import java.util.LinkedList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

public class Ruta {

	
    private String nombreRuta;
    private int idRuta;
    public List<GeoPoint> getCamino() {
		return camino;
	}

	public void setCamino(List<GeoPoint> camino) {
		this.camino = camino;
	}

	private List<GeoPoint> camino;

    public Ruta(int idRuta, String nombreRuta) {
            super();
            this.nombreRuta = nombreRuta;
            this.idRuta = idRuta;
            this.camino = new LinkedList<GeoPoint>();
    }

    public int getIdRuta() {
            return idRuta;
    }

    public void setIdRuta(int idRuta) {
            this.idRuta = idRuta;
    }

    public String getNombreRuta() {
            return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
            this.nombreRuta = nombreRuta;
    }
    
    public void anyadirPuntoARuta (GeoPoint punto){
    	this.camino.add(punto);
    }

}
