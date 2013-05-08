package com.example.audiorecorderdemo;

public interface RecorderAndPlaybackInterface {
	boolean startRecording();

	boolean stopRecording();

	boolean startPlayback();

	boolean pausePlayback();

	boolean resumePlayback();

	boolean stopPlayback();

	boolean isPlaying();

	void recordingComplete(String filePath);

	void playbackComplete();

	void release();
}
