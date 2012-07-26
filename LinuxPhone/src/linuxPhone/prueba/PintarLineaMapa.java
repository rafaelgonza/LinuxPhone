package linuxPhone.prueba;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class PintarLineaMapa extends Overlay {

	private Paint p;
	private GeoPoint pInicio;
	private GeoPoint pFinal;
	private Projection proyeccion;

	public PintarLineaMapa(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void parametrosOverlay(GeoPoint p1, GeoPoint p2) {

		pInicio = p1;
		pFinal = p2;
	}

	@Override
	protected void draw(Canvas canvas, MapView mapview, boolean shadow) {
		// TODO Auto-generated method stub

		if (shadow == false) {

			p = new Paint();
			p.setDither(true);
			p.setColor(Color.BLUE);
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setStrokeJoin(Paint.Join.ROUND);
			p.setStrokeCap(Paint.Cap.ROUND);
			p.setStrokeWidth(4);
			
			Point p1 = new Point();
			Point p2 = new Point();
			Path path = new Path();

			proyeccion = mapview.getProjection();
			proyeccion.toPixels(pInicio, p1);
			proyeccion.toPixels(pFinal, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			canvas.drawPath(path, p);
		}
	}

}
