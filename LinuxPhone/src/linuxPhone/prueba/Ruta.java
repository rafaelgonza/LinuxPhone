package linuxPhone.prueba;

import java.util.ArrayList;
import java.util.List;

public class Ruta {

	
    private String nombreRuta;
    private int idRuta;
    private List<Punto> camino;

    public Ruta(int idRuta, String nombreRuta) {
            super();
            this.nombreRuta = nombreRuta;
            this.idRuta = idRuta;
            this.camino = new ArrayList<Punto>();
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
    
    public void anyadirPuntoARuta (Punto punto){
    	camino.add(punto);
    }

}
