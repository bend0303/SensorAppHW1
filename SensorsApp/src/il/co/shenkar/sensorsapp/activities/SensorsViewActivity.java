package il.co.shenkar.sensorsapp.activities;

import il.co.shenkar.sensorsapp.R;
import il.co.shenkar.sensorsapp.qr.IntentIntegrator;
import il.co.shenkar.sensorsapp.qr.IntentResult;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SensorsViewActivity extends Activity implements SensorEventListener{
	public static final int QR_REQUEST_CODE = 0x0000c0de;
	private Button QRScanButton,QRGenerateButton,LinearAccButton,AzimuthButton,LightButton,pressureButton;
	private TextView LinearAccText,AzimuthText,LightText,pressureText;
	private EditText QRGenerateText;
	private SensorManager sensorManager;
	private Sensor motionSensor,magSensor,lightSensor,pressureSensor;
	
	private IntentIntegrator integrator = new IntentIntegrator(this);
	private float[] mAcc = new float[3];
	private float[] mOrientation = new float[3];
	private float[] Rmat = new float[9];
	private float[] Imat = new float[9];
	private float[] mGeoMags = new float[3];
	private float light,pressure;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensors_view);
		getElementsView();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (motionSensor == null)
		{
			LinearAccText.setText("Motion sensor wasn't found");
			LinearAccButton.setEnabled(false);
		}
		magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (magSensor == null)
		{
			AzimuthText.setText("Magnitude sensor wasn't found");
			AzimuthButton.setEnabled(false);
		}
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (lightSensor == null)
		{
			LightText.setText("Light sensor wasn't found");
			LightButton.setEnabled(false);
		}
		pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		if (pressureSensor == null)
		{
			pressureText.setText("Pressure sensor wasn't found");
			pressureButton.setEnabled(false);
		}
		
		sensorManager.registerListener(this, motionSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
		
		setListeners();

	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, motionSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onStop() {
		super.onResume();
		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	private void setListeners() {
		QRScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				integrator.initiateScan();

			}
		});

		QRGenerateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				integrator.shareText(QRGenerateText.getText().toString());

			}
		});

		LinearAccButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearAccText.setText("X: "+ mAcc[0] +
						"\nY: "+ mAcc[1] +
						"\nZ: " + mAcc[2]);
			}
		});
		
		AzimuthButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SensorManager.getRotationMatrix(Rmat, Imat, mAcc, mGeoMags);
				SensorManager.getOrientation(Rmat, mOrientation);
				AzimuthText.setText("Azimuth: "+ mOrientation[0]);
			}
		});
		
		LightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LightText.setText(Float.toString(light));
			}
		});
		
		pressureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pressureText.setText(Float.toString(pressure));
			}
		});

	}

	private void getElementsView() {
		QRScanButton = (Button) findViewById(R.id.QR_Scan_Sensor_ID);
		QRGenerateButton = (Button) findViewById(R.id.QR_Generate_ID);
		QRGenerateText = (EditText) findViewById(R.id.QR_Text_ID);
		LinearAccButton = (Button) findViewById(R.id.Linear_Acc_Sensor_ID);
		LinearAccText = (TextView) findViewById(R.id.Linear_Acc_Sensor_Text_ID);
		AzimuthButton = (Button) findViewById(R.id.Azimuth_Sensor_ID);
		AzimuthText = (TextView) findViewById(R.id.Azimuth_Sensor_Text_ID);
		LightButton = (Button) findViewById(R.id.Light_Sensor_ID);
		LightText = (TextView) findViewById(R.id.Light_Sensor_Text_ID);
		pressureButton = (Button) findViewById(R.id.Pressure_Sensor_ID);
		pressureText = (TextView) findViewById(R.id.Pressure_Sensor_Text_ID);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null){
			switch (requestCode) {
			case QR_REQUEST_CODE:{
				scanResult.getContents();
				break;
			}

			default:
				break;
			}
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, mAcc, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
            System.arraycopy(event.values, 0, mGeoMags, 0, 3);
            break;
		case Sensor.TYPE_LIGHT:
			light = event.values[0];
            break;         
		case Sensor.TYPE_PRESSURE:
			pressure = event.values[0];
            break;   
            
		default:
			return;
		}
	}
}
