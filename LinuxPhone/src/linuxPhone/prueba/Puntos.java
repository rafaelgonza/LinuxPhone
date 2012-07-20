package linuxPhone.prueba;

import android.os.Parcel;
import android.os.Parcelable;

public class Puntos implements Parcelable{
	
    private int idPunto;
    private double lon;
    private double lat;

    public Puntos(Parcel in) {
            this.idPunto = in.readInt();
            this.lon = in.readDouble();
            this.lat = in.readDouble();
    }

    public Puntos(int idPunto, double lon, double lat) {
            super();
            this.idPunto = idPunto;
            this.lon = lon;
            this.lat = lat;
    }

    public int getIdPunto() {
            return idPunto;
    }

    public void setIdPunto(int idPunto) {
            this.idPunto = idPunto;
    }

    public double getLon() {
            return lon;
    }

    public void setLon(double lon) {
            this.lon = lon;
    }

    public double getLat() {
            return lat;
    }

    public void setLat(double lat) {
            this.lat = lat;
    }

    public static final Parcelable.Creator<Puntos> CREATOR = new Parcelable.Creator<Puntos>() {
            public Puntos createFromParcel(Parcel in) {
                    return new Puntos(in);
            }

            public Puntos[] newArray(int size) {
                    return new Puntos[size];
            }
    };

    public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(idPunto);
            dest.writeDouble(lon);
            dest.writeDouble(lat);
    }

}
