package com.example.audiorecorderdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button startRecording;
	private Button stopRecording;
	private Button startPlayback;
	private Button stopPlayback;

	private RecorderAndPlaybackInterface audioRecorderAndPlaybackInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startRecording = (Button) findViewById(R.id.startRecording);
		stopRecording = (Button) findViewById(R.id.stopRecording);
		startPlayback = (Button) findViewById(R.id.startPlayback);
		stopPlayback = (Button) findViewById(R.id.stopPlayback);

		startRecording.setOnClickListener(this);
		stopRecording.setOnClickListener(this);
		startPlayback.setOnClickListener(this);
		stopPlayback.setOnClickListener(this);

		audioRecorderAndPlaybackInterface = new RecorderAndPlayerbackMediaRecorderImpl(
				getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.startRecording:
			audioRecorderAndPlaybackInterface.startRecording();

			break;

		case R.id.stopRecording:
			audioRecorderAndPlaybackInterface.stopRecording();

			break;

		case R.id.startPlayback:
			audioRecorderAndPlaybackInterface.startPlayback();

			break;

		case R.id.stopPlayback:
			audioRecorderAndPlaybackInterface.stopPlayback();

			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		audioRecorderAndPlaybackInterface.release();
	}

}
