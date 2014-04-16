package com.example.micromediaplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition; 
	int counter; 
	volatile boolean stopWorker;
	BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
	private Set<BluetoothDevice> pairedDevices;
	BluetoothDevice mmDevice;
	AlertDialog alertDialogStores;

//	String address = "000666662fb6";
//	BluetoothDevice msp430 = bluetooth.getRemoteDevice(address);

	ToggleButton BT;
	RadioButton song1, song2, song3;
	Button stop, pairedDevicesButton;
	MediaPlayer player;
	RadioGroup radioGroup1;
	TextView BPM, /* BPMvalue, */songTitle, paired;

	EditText bpmInput;

	Integer closest = -1;
	ArrayList<Integer> bpmArray = new ArrayList<Integer>(Arrays.asList(130,
			125, 119, 30, 60, 90));
	ArrayList<String> songTitleArray = new ArrayList<String>(
			Arrays.asList("Bang Bang - will.i.am", "All We Got - Fergie",
					"Lose Yourself to Dance - Daft Punk ", "jt", "mirrors",
					"that girl"));
	ArrayList<Integer> mp3Array = new ArrayList<Integer>(Arrays.asList(
			R.raw.allwegot, R.raw.bangbang, R.raw.givelife, R.raw.jt,
			R.raw.mirrors, R.raw.thatgirl));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// volume adjusts with phone volume buttons
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

//		lv = (ListView)findViewById(R.id.listView1);
		// player controls
		stop = (Button) findViewById(R.id.stop);
		pairedDevicesButton = (Button) findViewById(R.id.paired_devices);
		player = MediaPlayer.create(this, R.raw.jt);

		// text views
		BPM = (TextView) findViewById(R.id.BPM);
		// BPMvalue = (TextView) findViewById(R.id.BPMvalue);
		songTitle = (TextView) findViewById(R.id.songTitle);
		bpmInput = (EditText) findViewById(R.id.editText1);

		addKeyListener();
		addListenerOnButton();
		setUpBluetoooth();

		if (getIntent().getBooleanExtra("Exit me", false)) {
			finish();
			return; // add this to prevent from doing unnecessary stuffs
		}
	}
	
	public void addListenerOnButton(){
		BT = (ToggleButton) findViewById(R.id.BT);
		BT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (BT.isChecked()){
					turnOnBt();
				}
				else{
					turnOffBt();
				}
			}
		});
	}

	public void setUpBluetoooth() {
		if (bluetooth != null) {
			// Toast.makeText(this, "Continue with bluetooth setup",
			// Toast.LENGTH_LONG).show();
			System.out.println("Continue with bluetooth setup");
		}
//		if (bluetooth.isEnabled()) {
//			Toast.makeText(this, "BT on.", Toast.LENGTH_LONG).show();
//		} else {
//			Intent turnon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(turnon, 0);
//			Toast.makeText(this, "Disabled -->  BT Turned on",
//					Toast.LENGTH_LONG).show();
//		}

		String status;
		if (bluetooth.isEnabled()) {
			BT.setChecked(true);
			String mydeviceaddress = bluetooth.getAddress();
			bluetooth.setName("bpmApp");
			String mydevicename = bluetooth.getName();
			status = mydevicename + ":" + mydeviceaddress + ":"
					+ bluetooth.getState();
		} else {
			status = "Bluetooth is not Enabled.";
			BT.setChecked(false);
		}
		Toast.makeText(this, status, Toast.LENGTH_LONG).show();
	}
	
	public void turnOnBt(){
		if (bluetooth.isEnabled()) {
//			Toast.makeText(this, "BT on.", Toast.LENGTH_LONG).show();
		} 
		else {
			Intent turnon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnon, 0);
//			Toast.makeText(this, "Disabled -->  BT Turned on", Toast.LENGTH_LONG).show();
		}
	}
	
	public void turnOffBt(){
		bluetooth.disable();
//		Toast.makeText(this, "BT off." , Toast.LENGTH_LONG).show();
	}

	public void listPairedDevices(View view) {
		pairedDevices = bluetooth.getBondedDevices();
		ArrayList<String> list = new ArrayList<String>();

		if (pairedDevices.size() > 0){
			for (BluetoothDevice bt : pairedDevices) {
				if (bt.getName().equals("RNBT-2FB6")){
					list.add(bt.getName());
				}
			}
//			Toast.makeText(getApplicationContext(), list.get(0), Toast.LENGTH_LONG).show();
			final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Adapter alert");
			
			ListView listViewItems = new ListView(this);
			listViewItems.setAdapter(adapter);
			
			
			
			
			listViewItems.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					
					 Context context = view.getContext();
						//Toast.makeText(context, "Attempt connection to msp" , Toast.LENGTH_LONG).show();
						connectToMsp(view);
				}
			});
			
			
			alertDialogStores = new AlertDialog.Builder(MainActivity.this)
				.setView(listViewItems)
				.setTitle("Bonded Devices")
				.show();
//			lv.setAdapter(adapter);
		
			
			 
		}
		else{
			Toast.makeText(getApplicationContext(), "No Paired Devices :(", Toast.LENGTH_SHORT).show();	
		}
		
//		
	}
	
	public void beginListenForData(View view){
		
//		final Context context = view.getContext();
		
		final Handler handler = new Handler();
		final byte delimeter = 88;
		
		stopWorker = false; 
		readBufferPosition = 0; 
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					
					try {
						int bytesAvailable = mmInputStream.available();
					
					if(bytesAvailable > 0)
					{
						byte[] packetBytes = new byte[bytesAvailable];
						mmInputStream.read(packetBytes);
						for(int i=0; i <bytesAvailable; i++)
						{
//							Toast.makeText(context, "reading bytes" , Toast.LENGTH_LONG).show();
							byte b = packetBytes[i];
							if (b == delimeter)
							{
//								Toast.makeText(context, "all bytes read" , Toast.LENGTH_LONG).show();
								byte[] encodedBytes = new byte[readBufferPosition];
							     System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
							     final String data = new String(encodedBytes, "US-ASCII");
							     readBufferPosition = 0;
							     
							     handler.post(new Runnable() {
									
									@Override
									public void run() {
//										Toast.makeText(context, "printing bytes" , Toast.LENGTH_LONG).show();
										bpmInput.setText(data);
//										songTitle.setText(data);
										chooseBpmFromDesiredBpm();
										if (closest > 0) {
											Integer closestIndex = bpmArray.indexOf(closest);
											chooseSong(closestIndex);
										}
									}
								});
							}
							else{
								readBuffer[readBufferPosition++] = b;
							}
						}
					}
					} catch (IOException e) {
//						Toast.makeText(context, "stopping work" , Toast.LENGTH_LONG).show();
						stopWorker = true;
					}
				}
			}
		});
		
		workerThread.start();
		
	}

	public void connectToMsp(View view) {
		pairedDevices = bluetooth.getBondedDevices();
		 if(pairedDevices.size() > 0)
	        {
	            for(BluetoothDevice device : pairedDevices)
	            {
	                if(device.getName().equals("RNBT-2FB6")) //Note, you will need to change this to match the name of your device
	                {
	                	//Toast.makeText(this, "Found RNBT-2FB6" , Toast.LENGTH_LONG).show();
	                    mmDevice = device;
	                    break;
	                }
	            }
	        }
		 
		  UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
	      BluetoothSocket mmSocket;
	      
			try {
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
				mmSocket.connect();
				Toast.makeText(this, "Connected RNBT-2FB6!" , Toast.LENGTH_LONG).show();
		        mmOutputStream = mmSocket.getOutputStream();
		        mmInputStream = mmSocket.getInputStream();
		        beginListenForData(view);
		        songTitle.setText("Listening...");
		        
			} catch (IOException e) {
				Toast.makeText(this, "ERR could not connect" , Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
	        
		
	}

	public void onClick(View view) {
		
		switch(view.getId()){
		case R.id.stop:
			if (player.isPlaying()) {
				player.stop();
				// radioGroup1.clearCheck();
			}
			setUpBluetoooth();
			break;
		case R.id.paired_devices:
			//listPairedDevices(view);
			connectToMsp(view);
			break;
		}
		
		
	}

	public void addOnCheckedChangeListener() {
		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup1, int checkedId) {
				if (song1.isChecked()) {
					chooseSong(0);
				} else if (song2.isChecked()) {
					chooseSong(1);
				} else if (song3.isChecked()) {
					chooseSong(2);
				}
			}
		});
	}

	public void chooseSong(Integer index) {
		player.stop();
		player = MediaPlayer.create(MainActivity.this, mp3Array.get(index));
		player.start();
		BPM.setText("BPM: " + bpmArray.get(index) + "");
		songTitle.setText(songTitleArray.get(index));
	}

	public void addKeyListener() {

		// bpmInput = (EditText) findViewById(R.id.editText1);
		bpmInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					// display a floating message
					// Toast.makeText(MainActivity.this, bpmInput.getText(),
					// Toast.LENGTH_LONG).show();
					// BPMvalue.setText(bpmInput.getText());
					// radioGroup1.clearCheck();
					chooseBpmFromDesiredBpm();

					if (closest > 0) {
						Integer closestIndex = bpmArray.indexOf(closest);
						chooseSong(closestIndex);
						// switch (closestIndex){
						// case 0:
						// song1.setChecked(true);
						// break;
						// case 1:
						// song2.setChecked(true);
						// break;
						// case 2:
						// song3.setChecked(true);
						// break;
						// }
					}

					return true;

				}

				return false;
			}
		});

	}

	public Integer chooseBpmFromDesiredBpm() {
		int despiredBpm = Integer.parseInt(bpmInput.getText().toString());
		int offset = Integer.MAX_VALUE;
		// search for closest matching bpm in bpmArray
		for (Integer bpm : bpmArray) {
			int tempOffset = Math.abs(despiredBpm - bpm);
			if (tempOffset <= offset) {
				offset = tempOffset;
				closest = bpm;
			}
		}

		return closest;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.exit:
			exitApp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void exitApp() {

		// set the exit boolean
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("Exit me", true);
		startActivity(intent);

		// release all the media players
		player.release();

		// wrap-up the app
		// if (mBluetoothService != null) mBluetoothService.stop();
		finish();
	}

}
