package linuxPhone.prueba;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BiciActivity extends Activity {

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
	private Ruta ruta;

	private Long instanteActual, instanteAnterior, instanteInicial;

	private String HexColor = "#eda62b";
	private Projection projection;
	private Point p1, p2;

	private double distanciaTotal = 0;
	private int contador = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitybici);

		tiempo = (Chronometer) findViewById(R.id.cronometroBici);
		myOpenMapView = (MapView) findViewById(R.id.openmapviewBici);
		myOpenMapView.setBuiltInZoomControls(true);

		myMapController = myOpenMapView.getController();
		myMapController.setZoom(10);
		myMapController.setCenter(new GeoPoint(0, 0));
		myOpenMapView.setMultiTouchControls(true);

		// Inicializacion variables
		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		ruta = new Ruta(1, "Prueba");
		instanteAnterior = SystemClock.elapsedRealtime();

		// Posicion Actual
		posicionActualSobreElMapa = new SimpleLocationOverlay(this,
				mResourceProxy);
		myOpenMapView.getOverlays().add(posicionActualSobreElMapa);

		// Dibujar linea

		 Paint mPaint = new Paint();
		 mPaint.setDither(true);
		
		 mPaint.setColor(Color.BLUE);
		
		 mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		 mPaint.setStrokeJoin(Paint.Join.ROUND);
		 mPaint.setStrokeCap(Paint.Cap.ROUND);
		 mPaint.setStrokeWidth(6);

		this.lineaOverlay = new PathOverlay(Color.BLUE, mResourceProxy);
		lineaOverlay.setPaint(mPaint);
		myOpenMapView.getOverlays().add(lineaOverlay);

		activarGPS();

		final Button btnStart = (Button) findViewById(R.id.buttonComenzarBici);

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

		final Button btnStop = (Button) findViewById(R.id.buttonPararBici);

		btnStop.setOnClickListener(new OnClickListener() {
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
				switch (status) {
				case 0:
					Toast toast = Toast.makeText(getApplicationContext(),
							"GPS FUERA DE SERVICIO", Toast.LENGTH_SHORT);
					toast.show();

					break;
				case 1:
					Toast toast1 = Toast
							.makeText(getApplicationContext(),
									"GPS TEMPORALMENTE INACCESIBLE",
									Toast.LENGTH_SHORT);
					toast1.show();
					break;
				case 2:
					Toast toast2 = Toast.makeText(getApplicationContext(),
							"GPS ACTIVADO", Toast.LENGTH_SHORT);
					toast2.show();
				default:
					Toast toast3 = Toast.makeText(getApplicationContext(),
							"GPS ERROR INESPERADO", Toast.LENGTH_SHORT);
					toast3.show();
					break;
				}

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(getApplicationContext(),
						"GPS ACTIVADO", Toast.LENGTH_SHORT);
				toast.show();

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
				0, 2, locListener);
	}

	protected void tratarPosicion(Location location) {
		// TODO Auto-generated method stub

		double distancia, velocidad, velocidadMedia;

		GeoPoint punto = new GeoPoint(location);
		posicionActualSobreElMapa.setLocation(punto);
		myMapController.setZoom(19);
		myMapController.animateTo(punto);
		myMapController.setCenter(punto);

		instanteActual = SystemClock.elapsedRealtime();

		if (estado != "inactivo") {
			if (contador == 0) {//Mirar que hacemos con esto
				if (ruta.getCamino().size() > 1) {

					 lineaOverlay.addPoint(punto);
					myOpenMapView.getOverlays().add(lineaOverlay);
//					PintarLineaMapa pintar = new PintarLineaMapa(
//							getApplicationContext());
//					pintar.parametrosOverlay(
//							ruta.getCamino().get(ruta.getCamino().size() - 2),
//							ruta.getCamino().get(ruta.getCamino().size() - 1));
//
//					myOpenMapView.getOverlays().add(pintar);
//					myOpenMapView.postInvalidate();

					distancia = (int) calculaDistancia(
							ruta.getCamino().get(ruta.getCamino().size() - 1),
							punto);

					distanciaTotal = distanciaTotal + distancia;

					velocidad = calculaVelocidad(distancia, instanteActual
							- instanteAnterior);
					velocidadMedia = calculaVelocidad(distanciaTotal,
							instanteActual - instanteInicial);

					instanteAnterior = instanteActual;

					TextView distanciaText = (TextView) findViewById(R.id.textViewDistanciaBici);
					TextView velocidadText = (TextView) findViewById(R.id.textViewVelocidadBici);
					TextView velocidadMediaText = (TextView) findViewById(R.id.textViewVelocidadMediaBici);

					distanciaText.setText("Distancia: " + distanciaTotal);
					velocidadText.setText("Velocidad: " + velocidad);
					velocidadMediaText.setText("Velocidad media: "
							+ velocidadMedia);
					contador = 0;
				}
			} else {
				contador++;
			}
			ruta.anyadirPuntoARuta(punto);
		}
	}

	private double calculaVelocidad(double distancia, long tiempo) {
		// TODO Auto-generated method stub
		double result = (distancia / (double) tiempo);

		return result;
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
		inflater.inflate(R.menu.menu_bici, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.salirBici:
			finish();
			return true;
		case R.id.guardarBici:
			// Intent configuracion = new Intent(getApplicationContext(),
			// MenuConfiguracion.class);
			// startActivity(configuracion);
			return true;
		case R.id.menuCambiarVistaBici:

			LinearLayout contenedorDatos = (LinearLayout) findViewById(R.id.contenedorDatosBici);
			org.osmdroid.views.MapView mapa = (org.osmdroid.views.MapView) findViewById(R.id.openmapviewBici);

			if (mapa.getVisibility() == View.VISIBLE) {
				mapa.setVisibility(View.GONE);
				contenedorDatos.setVisibility(View.VISIBLE);
			} else {
				contenedorDatos.setVisibility(View.INVISIBLE);
				mapa.setVisibility(View.VISIBLE);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
