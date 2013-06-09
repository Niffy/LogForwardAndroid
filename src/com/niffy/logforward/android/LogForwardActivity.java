package com.niffy.logforward.android;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.niffy.logforward.android.R;

public class LogForwardActivity extends Activity {
	private final Logger log = LoggerFactory.getLogger(LogForwardActivity.class);
	protected TextView mStatus;
	protected Button mStart;
	protected Button mStop;
	protected Button mSettings;
	protected Button mCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConfigureLog.configure("LogForwardActivity.log", 1, 10000, 1024 * 1024);
		setContentView(R.layout.activity_log_forward);
		this.mStatus = (TextView) findViewById(R.id.status_text);
		this.mStart = (Button) findViewById(R.id.btn_start);
		this.mStop = (Button) findViewById(R.id.btn_stop);
		this.mSettings = (Button) findViewById(R.id.btn_settings);
		this.mCheck = (Button) findViewById(R.id.btn_check);

		boolean running = this.isServiceRunning(LogForwardService.class.getName());
		if (running) {
			log.info("Service is running on launch");
			this.mStatus.setText(getResources().getString(R.string.activity_status_running));
		} else {
			log.info("Service is not running on launch");
			this.mStatus.setText(getResources().getString(R.string.activity_status_not_running));
		}

		this.mStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();
			}
		});

		this.mStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
		});

		this.mSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settings();
			}
		});

		this.mSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settings();
			}
		});

		this.mCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				check();
			}
		});

		PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.settings_preference, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_forward, menu);
		return true;
	}

	protected void start() {
		boolean running = this.isServiceRunning(LogForwardService.class.getName());
		if (running) {
			log.info("Service already running");
			this.mStatus.setText(getResources().getString(R.string.activity_status_running));
		} else {
			log.info("Launching service");
			Intent intent = new Intent(this, LogForwardService.class);
			ComponentName name = startService(intent);
			if (name != null) {
				log.info("Service is now running: {}", name);
				this.mStatus.setText(getResources().getString(R.string.activity_status_running));
			} else {
				this.mStatus.setText(getResources().getString(R.string.activity_status_unknown));
			}
		}
	}

	protected void stop() {
		boolean running = this.isServiceRunning(LogForwardService.class.getName());
		if (running) {
			log.info("Service is running, will stop");
			Intent intent = new Intent(this, LogForwardService.class);
			boolean stopped = stopService(intent);
			if (stopped) {
				log.info("Service was stopped");
				this.mStatus.setText(getResources().getString(R.string.activity_status_not_running));
			} else {
				log.info("Service was not stopped");
				this.mStatus.setText(getResources().getString(R.string.activity_status_unknown));
			}
		} else {
			log.info("Service is not running");
		}
	}

	protected void settings() {
		log.info("Settings called:");
		startActivity(new Intent(this, Settings.class));
	}

	protected void check() {
		boolean running = this.isServiceRunning(LogForwardService.class.getName());
		if (running) {
			log.info("Service check. Is running");
			this.mStatus.setText(getResources().getString(R.string.activity_status_running));
		} else {
			log.info("Service check. Is not running");
			this.mStatus.setText(getResources().getString(R.string.activity_status_not_running));
		}
	}

	public boolean isServiceRunning(String serviceClassName) {
		log.debug("Service name to check is running:{}", serviceClassName);
		final ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(
				Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo runningServiceInfo : services) {
			log.info("Service name: {}", runningServiceInfo.service.getClassName());
			if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
				return true;
			}
		}
		return false;
	}

}
