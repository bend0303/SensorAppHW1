package il.co.shenkar.sensorsapp.activities;

import il.co.shenkar.sensorsapp.R;
import il.co.shenkar.sensorsapp.qr.IntentIntegrator;
import il.co.shenkar.sensorsapp.qr.IntentResult;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SensorsViewActivity extends Activity {
	private Button QRScanButton;
	private TextView QRScanText;
	//rivate SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	private IntentIntegrator integrator = new IntentIntegrator(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensors_view);
		getElementsView();
		setListeners();

	}

	private void setListeners() {
		QRScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				integrator.initiateScan();

			}
		});

	}

	private void getElementsView() {
		QRScanButton = (Button) findViewById(R.id.QR_Scan_Sensor_ID);
		QRScanText = (TextView) findViewById(R.id.QR_Scan_Sensor_TEXT_ID);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			QRScanText.setText(scanResult.getContents());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sensors_view, menu);
		/*
		 * - QR Scan activate BarCode scanner and show the result - QR Generate
		 * prompt the user for text and display the appropriate QR code using
		 * BarCode Scanner - Linear acceleration Show raw linear acceleration
		 * readings - Azimuth Show current Azimuth - Light Show current Light
		 * reading - Pressure Show current Pressure reading. (Tell user if the
		 * sensor is not present)
		 */
		return true;
	}

}
