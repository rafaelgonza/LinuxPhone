package linuxPhone.prueba;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class BiciActivity extends Activity implements LocationListener{
	
	//Para el mapa
	private DefaultResourceProxyImpl mResourceProxy;
	private MapView myOpenMapView;
	private MapController myMapController;
	
	//Para posicion actual
	private SimpleLocationOverlay posicionActualSobreElMapa;
	private LocationManager manejadorPosicion;
	
	//Linea recorrido
	private PathOverlay lineaOverlay;

	private ArrayList<Puntos> puntosLinea;
	

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitybici);
        
        puntosLinea = getIntent().getParcelableArrayListExtra("listaPuntos");
       
        
        myOpenMapView = (MapView)findViewById(R.id.openmapview);
        myOpenMapView.setBuiltInZoomControls(true);
        myMapController = myOpenMapView.getController();
        myMapController.setZoom(14);
        myMapController.setCenter(new GeoPoint(-3, -79));
        
        myOpenMapView.setMultiTouchControls(true);
        
        
        //Inicializacion variables
        mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        
        //Posicion Actual
        posicionActualSobreElMapa = new SimpleLocationOverlay(this, mResourceProxy);
        myOpenMapView.getOverlays().add(posicionActualSobreElMapa);
        
        //Dibujar linea
        this.lineaOverlay = new PathOverlay(Color.BLUE, mResourceProxy);
        if (puntosLinea != null) {
                for (Puntos p : puntosLinea) {
                        lineaOverlay.addPoint((int) (p.getLat() * 1e6),
                                        (int) (p.getLon() * 1e6));
                }
        }
        
        
        
        myOpenMapView.getOverlays().add(lineaOverlay);
        
        
        
        activarGPS();
        
    }

    

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
            GeoPoint punto = new GeoPoint(location);
            posicionActualSobreElMapa.setLocation(punto);
            myMapController.animateTo(punto);
            myMapController.setCenter(punto);
    }
		
	}



	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	 public void activarGPS() {
         manejadorPosicion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         manejadorPosicion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
 }



	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

    
}
