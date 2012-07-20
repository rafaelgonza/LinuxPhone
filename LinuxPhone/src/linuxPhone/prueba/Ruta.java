package linuxPhone.prueba;

public class Ruta {

	
    private String nombreRuta;
    private int idRuta;
    private int localImage;

    public Ruta(int idRuta, String nombreRuta, int localImage) {
            super();
            this.nombreRuta = nombreRuta;
            this.idRuta = idRuta;
            this.localImage = localImage;
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

    public int getLocalImage() {
            return localImage;
    }

    public void setLocalImage(int i) {
            this.localImage = i;
    }
}
