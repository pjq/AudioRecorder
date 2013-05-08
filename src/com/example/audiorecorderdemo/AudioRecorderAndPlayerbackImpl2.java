package com.example.audiorecorderdemo;

import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;

public class AudioRecorderAndPlayerbackImpl2 extends
		AudioRecorderAndPlayerbackImpl {
	private MediaRecorder mediaRecorder;

	public AudioRecorderAndPlayerbackImpl2(Context context) {
		super(context);
	}

	@Override
	public boolean startRecording() {
		if (null != mediaRecorder) {
			stopRecording();
		}

		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setMaxDuration(MAX_DURATION_MSEC);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(getAudioTmpFilesPath());
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean stopRecording() {
		if (null != mediaRecorder) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}

		return true;
	}

	@Override
	public void release() {
		super.release();

		if (null != mediaRecorder) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

}
