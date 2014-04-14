package com.example.micromediaplayer;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
//import android.widget.Toast;

public class MainActivity extends Activity {

	RadioButton song1, song2, song3;
	Button stop;
	MediaPlayer player;
	RadioGroup radioGroup1;
	TextView BPM, /*BPMvalue,*/ songTitle;
	
	EditText bpmInput;
	
	Integer closest= -1;
	ArrayList<Integer> bpmArray = new ArrayList<Integer>(Arrays.asList(130,125,119, 30, 60, 90));
	ArrayList<String> songTitleArray = new ArrayList<String>(Arrays.asList("Bang Bang - will.i.am",
			"All We Got - Fergie", "Lose Yourself to Dance - Daft Punk ", "jt", "mirrors", "that girl"));
	ArrayList<Integer> mp3Array = new ArrayList<Integer>(Arrays.asList(R.raw.allwegot, R.raw.bangbang, R.raw.givelife,
			R.raw.jt, R.raw.mirrors, R.raw.thatgirl)); 
			
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//volume adjusts with phone volume buttons 
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
        //song controls
//        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
//		song1 = (RadioButton) findViewById(R.id.song1);
//		song2 = (RadioButton) findViewById(R.id.song2);
//		song3 = (RadioButton) findViewById(R.id.song3);
//		radioGroup1.clearCheck();
		
		
		//player controls 
		stop = (Button) findViewById(R.id.stop);
		player = MediaPlayer.create(this, R.raw.jt);
		
		//text views 
		BPM = (TextView) findViewById(R.id.BPM);
		//BPMvalue = (TextView) findViewById(R.id.BPMvalue);
		songTitle = (TextView) findViewById(R.id.songTitle);
		bpmInput = (EditText) findViewById(R.id.editText1);
		
		addKeyListener();
		//addOnCheckedChangeListener();
		
		if (getIntent().getBooleanExtra("Exit me", false)) {
			finish();
			return; // add this to prevent from doing unnecessary stuffs
		}
		
	}
	
//	public void onRadioButtonClicked(View view){
//		
//		boolean checked = ((RadioButton) view).isChecked();
//		
//		switch (view.getId()){
//		
//		case R.id.song1:
//			if (checked){
//				player.stop();
//				player = MediaPlayer.create(this, R.raw.jt);
//				player.start();
//				BPMvalue.setText("100");
//			}
//			
//			break;
//			
//		case R.id.song2:
//			if (checked){
//				player.stop();
//				player = MediaPlayer.create(this, R.raw.mirrors);
//				player.start();
//				BPMvalue.setText("200");
//			}
//			
//			break;
//			
//		case R.id.song3:
//			if (checked){
//				player.stop();
//				player = MediaPlayer.create(this, R.raw.strawberrybubblegum);
//				player.start();
//				BPMvalue.setText("300");
//			}
//			
//			break;
//		}
//	}
	
	public void onClick(View view){
		if (player.isPlaying()){
			player.stop();
			//radioGroup1.clearCheck();

		}
	}
	
	public void addOnCheckedChangeListener(){
		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radioGroup1, int checkedId) {
				if (song1.isChecked()){
//					player.stop();
					//player = MediaPlayer.create(MainActivity.this, R.raw.allwegot);
//					player = MediaPlayer.create(MainActivity.this, mp3Array.get(0));
//					player.start();
//					BPMvalue.setText(bpmArray.get(0) + "");
//					songTitle.setText(songTitleArray.get(0));
					chooseSong(0);
				}
				else if (song2.isChecked()){
					chooseSong(1);
				}
				else if (song3.isChecked()){
					chooseSong(2);
				}
			}
		});
	}
	
	public void chooseSong(Integer index){
		player.stop();
		player = MediaPlayer.create(MainActivity.this, mp3Array.get(index));
		player.start();
		BPM.setText("BPM: " + bpmArray.get(index) + "");
		songTitle.setText(songTitleArray.get(index));
	}
	
	public void addKeyListener(){
		
//		bpmInput = (EditText) findViewById(R.id.editText1);
		bpmInput.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
		 
					// display a floating message
					//Toast.makeText(MainActivity.this, bpmInput.getText(), Toast.LENGTH_LONG).show();
					//BPMvalue.setText(bpmInput.getText());
					//radioGroup1.clearCheck();
					chooseBpmFromDesiredBpm();
					
					if (closest > 0){
						Integer closestIndex = bpmArray.indexOf(closest);
						chooseSong(closestIndex);
//						switch (closestIndex){
//						case 0:
//							song1.setChecked(true);
//							break;
//						case 1:
//							song2.setChecked(true);
//							break;
//						case 2:
//							song3.setChecked(true);
//							break;
//						}	
					}
					
					return true;
		 
				}
				
				
				return false;
			}
		});
		
	}
	
	public Integer chooseBpmFromDesiredBpm(){
		int despiredBpm = Integer.parseInt(bpmInput.getText().toString());
		int offset = Integer.MAX_VALUE; 
		//search for closest matching bpm in bpmArray
		for (Integer bpm : bpmArray){
			int tempOffset = Math.abs(despiredBpm - bpm);
			if (tempOffset <= offset){
				offset=tempOffset;
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
		finish();
	}

}
