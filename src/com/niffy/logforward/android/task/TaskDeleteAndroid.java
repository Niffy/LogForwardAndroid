package com.niffy.logforward.android.task;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;

import com.niffy.logforwarder.lib.logmanagement.task.TaskDelete;

public class TaskDeleteAndroid extends TaskDelete {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(TaskDeleteAndroid.class);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TaskDeleteAndroid(final String pFile) {
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
		this.mTaskSuccessful = logFile.delete();
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
