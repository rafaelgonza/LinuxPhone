package linuxPhone.prueba;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PieActivity extends Activity {

	// Para cronometro
	private Chronometer tiempo;
	private String estado = "inactivo";
	private Long memoTiempo;

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
	private Ruta ruta;
	private int idPuntoRuta;

	private Long instanteActual, instanteAnterior = (long) 0, instanteInicial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_pie);

		tiempo = (Chronometer) findViewById(R.id.cronometro);

		puntosLinea = getIntent().getParcelableArrayListExtra("listaPuntos");

		myOpenMapView = (MapView) findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myMapController = myOpenMapView.getController();
		myMapController.setZoom(5);
		myMapController.setCenter(new GeoPoint(-3, -79));

		myOpenMapView.setMultiTouchControls(true);

		// Inicializacion variables
		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

		idPuntoRuta = 0;

		// Posicion Actual
		posicionActualSobreElMapa = new SimpleLocationOverlay(this,
				mResourceProxy);
		myOpenMapView.getOverlays().add(posicionActualSobreElMapa);

		// Dibujar linea
		this.lineaOverlay = new PathOverlay(Color.BLUE, mResourceProxy);
		myOpenMapView.getOverlays().add(lineaOverlay);

		activarGPS();

		final Button btnStart = (Button) findViewById(R.id.buttonComenzarPie);

		btnStart.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (estado == "inactivo") {
					instanteInicial = SystemClock.elapsedRealtime();
					tiempo.setBase(instanteInicial);
					tiempo.start();
					estado = "activo";
					btnStart.setText("Pausar");
					return;
				}
				if (estado == "activo") {
					memoTiempo = SystemClock.elapsedRealtime();
					tiempo.stop();
					estado = "pausado";
					btnStart.setText("Continuar");
					return;
				} else {
					tiempo.setBase(tiempo.getBase()
							+ SystemClock.elapsedRealtime() - memoTiempo);
					tiempo.start();
					estado = "activo";
					btnStart.setText("Pausar");
				}

			}
		});

		final Button btnStop = (Button) findViewById(R.id.buttonPararPie);

		btnStop.setOnClickListener(new OnClickListener() {
			@SuppressLint("ParserError")
			public void onClick(View arg0) {
				tiempo.stop();
				btnStart.setText("Iniciar");
				estado = "inactivo";

			}
		});

	}

	private void activarGPS() {

		LocationListener locListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(getApplicationContext(),
						"GPS DESACTIVADO", Toast.LENGTH_SHORT);
				toast.show();

			}

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				if (location != null) {
					tratarPosicion(location);

				}

			}
		};
		manejadorPosicion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		manejadorPosicion.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1, 1, locListener);
	}

	protected void tratarPosicion(Location location) {
		// TODO Auto-generated method stub

		double distancia, distanciaTotal, velocidad, velocidadMedia;

		GeoPoint punto = new GeoPoint(location);
		posicionActualSobreElMapa.setLocation(punto);
		myMapController.animateTo(punto);
		myMapController.setCenter(punto);

		instanteActual = SystemClock.elapsedRealtime();

		if (estado != "inactivo") {

			lineaOverlay.addPoint(punto);
			myOpenMapView.getOverlays().add(lineaOverlay);

			distancia = calculaDistancia(punto,
					ruta.getCamino().get(ruta.getCamino().size() - 1));
			distanciaTotal = calculaDistancia(punto, ruta.getCamino().get(0));

			velocidad = calculaVelocidad(distancia, instanteActual
					- instanteAnterior);
			velocidadMedia = calculaVelocidad(distanciaTotal, instanteActual
					- instanteInicial);
			ruta.anyadirPuntoARuta(punto);

			TextView distanciaText = (TextView) findViewById(R.id.textViewDistancia);
			TextView velocidadText = (TextView) findViewById(R.id.textViewVelocidad);
			TextView velocidadMediaText = (TextView) findViewById(R.id.textViewVelocidadMedia);

			distanciaText.setText("Distancia: " + distanciaTotal);
			velocidadText.setText("Velocidad: " + velocidad);
			velocidadMediaText.setText("Velocidad media: " + velocidadMedia);
			//asdfasdfasd

		}

	}

	private double calculaVelocidad(double distancia, long tiempo) {
		// TODO Auto-generated method stub

		return distancia / (double) tiempo;
	}

	private double calculaDistancia(GeoPoint punto, GeoPoint geoPoint) {
		// TODO Auto-generated method stub

		double distancia = punto.distanceTo(geoPoint) * 1609.344;

		return distancia;

	}

	public void onBackPressed() {

		Intent principal = new Intent(this, LinuxPhoneActivity.class);
		startActivity(principal);
		super.onBackPressed();
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_pie, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.salir:
			finish();
			return true;
		case R.id.guardar:
			// Intent configuracion = new Intent(getApplicationContext(),
			// MenuConfiguracion.class);
			// startActivity(configuracion);
			return true;
		case R.id.menuCambiarVista:

			FrameLayout contenedorMapa = (FrameLayout) findViewById(R.id.contenedorMapa);
			LinearLayout contenedorDatos = (LinearLayout) findViewById(R.id.contenedorDatos);
			org.osmdroid.views.MapView mapa = (org.osmdroid.views.MapView) findViewById(R.id.openmapview);
			TextView distancia = (TextView) findViewById(R.id.textViewDistancia);
			TextView velocidad = (TextView) findViewById(R.id.textViewVelocidad);
			TextView velocidadMedia = (TextView) findViewById(R.id.textViewVelocidadMedia);
			
			
			if (mapa.getVisibility() == View.VISIBLE) {
				mapa.setVisibility(View.GONE);
				contenedorMapa.setVisibility(View.GONE);
				contenedorDatos.setVisibility(View.VISIBLE);
				distancia.setVisibility(View.VISIBLE);
				velocidad.setVisibility(View.VISIBLE);
				velocidadMedia.setVisibility(View.VISIBLE);
			} else {
				
				contenedorDatos.setVisibility(View.INVISIBLE);
				distancia.setVisibility(View.INVISIBLE);
				velocidad.setVisibility(View.INVISIBLE);
				velocidadMedia.setVisibility(View.INVISIBLE);
				mapa.setVisibility(View.VISIBLE);
				contenedorMapa.setVisibility(View.VISIBLE);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
