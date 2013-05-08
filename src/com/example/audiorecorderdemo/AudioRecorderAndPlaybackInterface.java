package com.example.audiorecorderdemo;

public interface AudioRecorderAndPlaybackInterface {
	boolean startRecording();

	boolean stopRecording();

	boolean startPlayback();

	boolean stopPlayback();

	void recordingComplete(String filePath);

	void playbackComplete();
	
	void release();
}
