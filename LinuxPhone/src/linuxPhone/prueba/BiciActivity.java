package linuxPhone.prueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
			if (contador == 0) {// Mirar que hacemos con esto
				if (ruta.getCamino().size() > 1) {

					lineaOverlay.addPoint(punto);
					myOpenMapView.getOverlays().add(lineaOverlay);

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
			LinearLayout contenedorGuardar = (LinearLayout) findViewById(R.id.contenedorGuardarRuta);
			org.osmdroid.views.MapView mapa2 = (org.osmdroid.views.MapView) findViewById(R.id.openmapviewBici);
			final Button btnStart = (Button) findViewById(R.id.buttonComenzarBici);

			mapa2.setVisibility(View.GONE);
			contenedorGuardar.setVisibility(View.VISIBLE);

			memoTiempo = SystemClock.elapsedRealtime();
			tiempo.stop();
			estado = "pausado";
			btnStart.setText("Continuar");

			final Button btnguardar = (Button) findViewById(R.id.buttonGuardarRuta);

			btnguardar.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					boolean sdDisponible = false, sdAccesoEscritura = false;
					String estado = Environment.getExternalStorageState();

					if (estado.equals(Environment.MEDIA_MOUNTED)) {
						sdDisponible = true;
						sdAccesoEscritura = true;
					} else if (estado
							.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
						sdDisponible = true;
						sdAccesoEscritura = false;
						Toast toast = Toast.makeText(getApplicationContext(),
								"ERROR: SD SOLO LECTURA", Toast.LENGTH_SHORT);
						toast.show();
					} else {
						sdDisponible = false;
						sdAccesoEscritura = false;
						Toast toast = Toast.makeText(getApplicationContext(),
								"ERROR: SD NO DISPONIBLE", Toast.LENGTH_SHORT);
						toast.show();
					}
					if (sdDisponible && sdAccesoEscritura) {

						// Abrimos la base de datos en modo
						// escritura
						RutaBiciSQLite usdbh = new RutaBiciSQLite(
								getApplicationContext(), "RutasBici", null, 1);
						SQLiteDatabase db = usdbh.getWritableDatabase();

						EditText nombreRuta = (EditText) findViewById(R.id.editTextNombreRutaAGuardar);
						List<GeoPoint> rutaCamino = ruta.getCamino();
						String nombreR = nombreRuta.getText().toString();

						// Si hemos abierto correctamente la base de datos
						if (db != null) {

							// Insertamos los datos en la tabla Usuarios
							db.execSQL("INSERT INTO RutaBici (nombre, velocidad, velocidadMaxima, distancia) VALUES (\""
									+ nombreR + "\", 1, 2, 10)");

							// Cerramos la base de datos
							db.close();
							// Creamos el fichero
							try {
								CreacionRutaKML fichero = new CreacionRutaKML(
										nombreRuta.getText().toString(),
										rutaCamino);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						LinearLayout contenedorGuardar = (LinearLayout) findViewById(R.id.contenedorGuardarRuta);
						org.osmdroid.views.MapView mapa2 = (org.osmdroid.views.MapView) findViewById(R.id.openmapviewBici);
						nombreRuta.setText("");
						Toast toast = Toast.makeText(getApplicationContext(),
								"Ruta: " + nombreR + " GUARDADA",
								Toast.LENGTH_SHORT);
						toast.show();
						mapa2.setVisibility(View.VISIBLE);
						contenedorGuardar.setVisibility(View.GONE);

					}

				}
			});

			return true;
		case R.id.cargarRuta:

			ListView ListaRutas = (ListView) findViewById(R.id.listViewListaRutas);
			LinearLayout contenedorDatos3 = (LinearLayout) findViewById(R.id.contenedorCargarRuta);
			org.osmdroid.views.MapView mapa3 = (org.osmdroid.views.MapView) findViewById(R.id.openmapviewBici);

			RutaBiciSQLite usdbh = new RutaBiciSQLite(getApplicationContext(),
					"RutasBici", null, 1);
			SQLiteDatabase db = usdbh.getReadableDatabase();

			// Si hemos abierto correctamente la base de datos
			if (db != null) {

				Cursor c = db.rawQuery(" SELECT nombre FROM RutaBici", null);
				final String[] datos = new String[c.getCount()];
				int i = 0;

				// Nos aseguramos de que existe al menos un registro
				if (c.moveToFirst()) {
					// Recorremos el cursor hasta los registros
					do {
						String nombRuta = c.getString(0);
						datos[i] = nombRuta;
						i++;
					} while (c.moveToNext());
				}
				c.close();
				db.close();
				ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, datos);

				ListaRutas.setAdapter(adaptador);

				ListaRutas.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						String seleccion;
						seleccion = datos[arg2];
						Toast toast2 = Toast.makeText(getApplicationContext(),
								"" + seleccion + "",
								Toast.LENGTH_SHORT);
						toast2.show();

						boolean sdDisponible = false;
						String estado = Environment.getExternalStorageState();

						if (estado.equals(Environment.MEDIA_MOUNTED)) {
							sdDisponible = true;
						} else {
							sdDisponible = false;
							Toast toast = Toast.makeText(
									getApplicationContext(),
									"ERROR: SD NO DISPONIBLE",
									Toast.LENGTH_SHORT);
							toast.show();
						}
						if (sdDisponible) {

							try {
								File ruta_sd = Environment
										.getExternalStorageDirectory();

								File f = new File(ruta_sd.getAbsolutePath(),
										seleccion);

								FileInputStream fich = new FileInputStream(f);

								DocumentBuilderFactory factory = DocumentBuilderFactory
										.newInstance();

								DocumentBuilder builder = factory
										.newDocumentBuilder();
								Document dom = builder.parse(fich);
								Element root = (Element) dom.getDocumentElement();
								NodeList items = root.getElementsByTagName("Point");
								
								List<GeoPoint> rutaCargada = new LinkedList<GeoPoint>();
								for (int i =0; i<items.getLength(); i++){
									
									Node nodo = items.item(i);
									Node nodoLatitud = nodo.getFirstChild();
									Node nodoLongitud = nodo.getLastChild();
									Double latitud = Double.valueOf(nodoLatitud.getNodeValue());
									Double longitud = Double.valueOf(nodoLongitud.getNodeValue());
									
									GeoPoint punto = new GeoPoint(latitud, longitud);
									rutaCargada.add(punto);
									
								}
								System.out.print(rutaCargada.toString());

							} catch (Exception ex) {
								Log.e("Ficheros",
										"Error al leer fichero desde tarjeta SD");
							}
						}
					}

				});

			}

			mapa3.setVisibility(View.GONE);
			contenedorDatos3.setVisibility(View.VISIBLE);

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
