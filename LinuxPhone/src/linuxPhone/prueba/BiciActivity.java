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
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class BiciActivity extends Activity implements LocationListener {

	// Para el mapa
	private DefaultResourceProxyImpl mResourceProxy;
	private MapView myOpenMapView;
	private MapController myMapController;

	// Para posicion actual
	private SimpleLocationOverlay posicionActualSobreElMapa;
	private LocationManager manejadorPosicion;

	// Linea recorrido
	private PathOverlay lineaOverlay;
	private ArrayList<Punto> puntosLinea;
	private boolean rutaComenzada = false;
	private Ruta ruta;
	private int idPuntoRuta;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitybici);

		puntosLinea = getIntent().getParcelableArrayListExtra("listaPuntos");

		myOpenMapView = (MapView) findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myMapController = myOpenMapView.getController();
		myMapController.setZoom(5);
		myMapController.setCenter(new GeoPoint(-3, -79));

		myOpenMapView.setMultiTouchControls(true);

		// Inicializacion variables
		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		rutaComenzada = false;
		idPuntoRuta = 0;

		// Posicion Actual
		posicionActualSobreElMapa = new SimpleLocationOverlay(this,
				mResourceProxy);
		myOpenMapView.getOverlays().add(posicionActualSobreElMapa);

		// Dibujar linea
		this.lineaOverlay = new PathOverlay(Color.BLUE, mResourceProxy);
//		if (puntosLinea != null) {
//			for (Punto p : puntosLinea) {
//				lineaOverlay.addPoint((int) (p.getLat() * 1e6),
//						(int) (p.getLon() * 1e6));
//			}
//		}

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
			
			if (rutaComenzada) {
				idPuntoRuta++;
				Punto puntoRuta = new Punto(idPuntoRuta,
						location.getLongitude(), location.getLatitude());
				//ruta.anyadirPuntoARuta(puntoRuta);
				//Intento de pintado de linea
				lineaOverlay.addPoint(punto);
				myOpenMapView.getOverlays().add(lineaOverlay);
			}
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
		manejadorPosicion.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1, 1, this);
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		Toast toast = Toast.makeText(getApplicationContext(), "GPS DESACTIVADO", Toast.LENGTH_SHORT);
		toast.show();

	}

	public void onBackPressed() {

		Intent principal = new Intent(this, LinuxPhoneActivity.class);
		startActivity(principal);
		super.onBackPressed();
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menurutabici, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.comienzoRutaBici:
			comienzoGuardadoPuntos();
			return true;
		case R.id.pararRutaBici:
			pararGuardadoPuntos();
			Intent principal = new Intent(getApplicationContext(),
					LinuxPhoneActivity.class);
			startActivity(principal);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void pararGuardadoPuntos() {
		// TODO Auto-generated method stub
		rutaComenzada = false;
		//guardar ruta en fichero
		
	}

	private void comienzoGuardadoPuntos() {
		// TODO Auto-generated method stub

		ruta = new Ruta(1, "prueba");
		rutaComenzada = true;

	}

}
