package com.example.audiorecorderdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorderAndPlayerbackImpl implements
		AudioRecorderAndPlaybackInterface {
	private static final String TAG = AudioRecorderAndPlayerbackImpl.class
			.getSimpleName();

	protected static final int RECORDER_BPP = 16;
	protected static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	protected static final String AUDIO_RECORDER_FOLDER = "EFAudioRecorder";
	protected static final String AUDIO_RECORDER_TEMP_FILE = "record";
	protected static final int RECORDER_SAMPLERATE = 44100;
	protected static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	protected static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	protected static final int MAX_DURATION_MSEC = 60 * 1000;

	// private AudioRecord recorder = null;
	private int bufferSize = 0;
	private Thread recordingThread = null;
	private boolean isRecording = false;

	private Context context;
	private AudioRecord audioRecord;

	protected File audioFile;

	private MediaPlayer mediaPlayer;

	public AudioRecorderAndPlayerbackImpl(Context context) {
		this.context = context;
		audioFile = createAudioTmpFiles();
	}

	@Override
	public boolean startRecording() {
		log("startRecording");
		// audioRecord = new AudioRecord(audioSource, sampleRateInHz,
		// channelConfig, audioFormat, bufferSizeInBytes);
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		bufferSize = bufferSize * 2;

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, bufferSize);
		int i = audioRecord.getState();

		if (i == AudioRecord.STATE_INITIALIZED) {
			audioRecord.startRecording();
			isRecording = true;

			recordingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// write audio file.
					saveAudioRecorderFile();

				}
			}, "audio recorder");
			recordingThread.start();

		} else if (i == AudioRecord.STATE_UNINITIALIZED) {
			return false;
		}

		return true;
	}

	private void saveAudioRecorderFile() {
		log("saveAudioRecorderFile");
		byte[] data = new byte[bufferSize];
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(audioFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (null == outputStream) {
			return;
		}

		int read = 0;

		while (isRecording) {
			read = audioRecord.read(data, 0, bufferSize);
			if (AudioRecord.ERROR_INVALID_OPERATION == read
					|| AudioRecord.ERROR_BAD_VALUE == read) {
				// occur errors.
				break;
			} else {
				try {
					outputStream.write(data, 0, read);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			outputStream.close();
			outputStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createAudioTmpFiles() {
		String path = getAudioTmpFilesPath();

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		}

		return file;
	}

	protected String getAudioTmpFilesPath() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File dir = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.getAbsolutePath() + File.separator
				+ AUDIO_RECORDER_TEMP_FILE;
	}

	@Override
	public boolean stopRecording() {
		log("stopRecording");
		if (null != audioRecord) {
			isRecording = false;

			int i = audioRecord.getState();
			if (i == 1) {
				audioRecord.stop();
			}
			audioRecord.release();

			audioRecord = null;
			recordingThread = null;
		}

		recordingComplete(audioFile.getPath());

		return true;
	}

	@Override
	public boolean startPlayback() {
		log("startPlayback");
		mediaPlayer = createMediaPlayer();

		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				log("onError,what=" + what + ",extra=" + extra);
				return false;
			}
		});

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayer.start();
			}
		});

		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				playbackComplete();
			}
		});

		try {
			String path = "file://" + getAudioTmpFilesPath();
			log("startPlayback,path=" + path);
			mediaPlayer.setDataSource(path);
			// mediaPlayer
			// .setDataSource("file:///storage/emulated/0/AudioRecorder/record_temp.raw");
			// mediaPlayer
			// .setDataSource("file:///sdcard/EFAudioRecorder/record_temp.raw");
			// mediaPlayer.setDataSource("file:///sdcard/mp3/1.mp3");

			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private MediaPlayer createMediaPlayer() {
		log("createMediaPlayer");
		MediaPlayer player = null;
		if (null != mediaPlayer) {

			releaseMediaPlayer();

			player = new MediaPlayer();
			player.setScreenOnWhilePlaying(false);

		} else {
			player = new MediaPlayer();

			// player = setMediaPlayerListener(player);
			player.setScreenOnWhilePlaying(false);
		}

		return player;
	}

	private void releaseMediaPlayer() {
		log("releaseMediaPlayer");
		if (null != mediaPlayer) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public boolean stopPlayback() {
		log("stopPlayback");
		if (null != mediaPlayer) {
			mediaPlayer.pause();
		}

		return true;
	}

	@Override
	public void recordingComplete(String filePath) {
		log("recordingComplete");
	}

	@Override
	public void playbackComplete() {
		log("playbackComplete");
	}

	private void log(String msg) {
		Log.i(TAG, msg);
	}

	@Override
	public void release() {
		if (null != audioRecord) {
			isRecording = false;

			int i = audioRecord.getState();
			if (i == 1) {
				audioRecord.stop();
			}
			audioRecord.release();

			audioRecord = null;
			recordingThread = null;
		}

		releaseMediaPlayer();
	}

}
