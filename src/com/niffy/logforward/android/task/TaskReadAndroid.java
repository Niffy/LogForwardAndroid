package com.niffy.logforward.android.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;

import com.niffy.logforwarder.lib.logmanagement.task.TaskRead;

public class TaskReadAndroid extends TaskRead {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(TaskReadAndroid.class);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TaskReadAndroid(final String pFile) {
		super(pFile);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void run() {
		super.run();
		log.info("Reading: {}", this.mFile);
		File dir = Environment.getExternalStorageDirectory();
		File logFile = new File(dir, this.mFile);
		this.mFileSize = (int) logFile.length();
		this.mData = new byte[this.mFileSize];
		try {
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(logFile));
			buf.read(this.mData, 0, this.mFileSize);
			buf.close();
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException: {}", logFile.getAbsolutePath(), e);
		} catch (IOException e) {
			log.error("IOException: {}", logFile.getAbsolutePath(), e);
		}
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
