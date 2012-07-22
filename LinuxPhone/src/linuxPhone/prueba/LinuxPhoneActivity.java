package linuxPhone.prueba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LinuxPhoneActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button botonBici = (Button) findViewById(R.id.buttonRutaBici);
		final Button botonPie = (Button) findViewById(R.id.buttonRutaAPie);

		botonBici.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						BiciActivity.class);
				startActivity(intent);

			}
		});

		botonPie.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
				PieActivity.class);
				startActivity(intent);

			}
		});
	}

	public void onBackPressed() {

		finish();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
	    
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menuprincipal, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case R.id.salir:
		        finish();
		        return true;
		    case R.id.configuracion:
		        Intent configuracion = new Intent(getApplicationContext(), MenuConfiguracion.class);
		        startActivity(configuracion);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
		} }
}