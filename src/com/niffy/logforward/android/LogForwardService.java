package com.niffy.logforward.android;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.niffy.logforward.android.R;
import com.niffy.logforwarder.lib.ServerSelector;

public class LogForwardService extends Service implements IService {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(LogForwardService.class);

	// ===========================================================
	// Fields
	// ===========================================================
	public static ServerSelector THREAD;
	public static LogManagerAndroid LOGMANAGER;
	public static int VERSION = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	public LogForwardService() {

	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onCreate() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String pLogName = prefs.getString(getResources().getString(R.string.settings_key_log_name),
				"LogForwardService.log");
		String maxBackupSize = prefs.getString(getResources().getString(R.string.settings_key_log_backup_qty), "10");
		String level = prefs.getString(getResources().getString(R.string.settings_key_log_level), "10000");
		String fileSize = prefs.getString(getResources().getString(R.string.settings_key_log_file_size), "10485760");
		String buffer = prefs.getString(getResources().getString(R.string.settings_key_network_buffer), "12582912");
		String port = prefs.getString(getResources().getString(R.string.settings_key_network_port), "1008");
		int pMaxBackupSize = Integer.valueOf(maxBackupSize);
		int pLevel = Integer.valueOf(level);
		int pFileSize = Integer.valueOf(fileSize);
		int pBufferCapacity = Integer.valueOf(buffer);
		int pPort = Integer.valueOf(port);
		InetSocketAddress pAddress = new InetSocketAddress(Utils.getIPAddress(true), pPort);
		
		ConfigureLog.configure(pLogName, pMaxBackupSize, pLevel, pFileSize);
		LOGMANAGER = new LogManagerAndroid(VERSION, this);
		if (THREAD != null) {
			log.info("Selector thread is already running, no need to create a new one");
		} else {
			log.info("Selector thread is not running, will create a new one");
			try {
				THREAD = new ServerSelector("LogForwardServiceSelector", pAddress, pBufferCapacity, LOGMANAGER);
				THREAD.start();
				LOGMANAGER.setSelector(THREAD);
			} catch (IOException e) {
				log.error("Could not create Server Selector", e);
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		log.info("OnStartCommand: Start id: {}", startId);
		/*
		 * Don't need to do anything!
		 */
		// If we get killed, after returning from here, restart
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		log.info("Destroying service");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void shutdown() {
		stopSelf();
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
