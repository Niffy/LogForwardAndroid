package com.niffy.logforward.android;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.logforward.android.task.TaskDeleteAndroid;
import com.niffy.logforward.android.task.TaskReadAndroid;
import com.niffy.logforwarder.lib.ISelector;
import com.niffy.logforwarder.lib.logmanagement.LogManagerServer;
import com.niffy.logforwarder.lib.logmanagement.task.CallbackInfo;
import com.niffy.logforwarder.lib.logmanagement.task.CallbackTask;
import com.niffy.logforwarder.lib.messages.MessageDeleteRequest;
import com.niffy.logforwarder.lib.messages.MessageFlag;
import com.niffy.logforwarder.lib.messages.MessageSendRequest;
import com.niffy.logforwarder.lib.messages.MessageSendSizeResponse;

public class LogManagerAndroid extends LogManagerServer {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(LogManagerAndroid.class);

	// ===========================================================
	// Fields
	// ===========================================================
	protected IService mServce;
	// ===========================================================
	// Constructors
	// ===========================================================

	public LogManagerAndroid(final ISelector pSelector, final int pVersion, final IService pService) {
		super(pSelector, pVersion);
		this.mServce = pService;
	}

	public LogManagerAndroid(final int pVersion, final IService pService) {
		super(pVersion);
		this.mServce = pService;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void createReadTask(final InetSocketAddress pAddress, final MessageSendRequest pMessage) {
		final int pSeq = this.mSequence.getAndIncrement();
		Runnable runnable = new TaskReadAndroid(pMessage.getLogFileNameAndPath());
		CallbackInfo info = new CallbackInfo(pAddress, pSeq, pMessage.getSequence(), pMessage.getMessageFlag(),
				runnable);
		this.mRunnables.put(pSeq, info);
		this.mCrosRef.put(pMessage.getSequence(), pSeq);
		CallbackTask task = new CallbackTask(runnable, this, pSeq);
		this.mService.execute(task);
	}

	@Override
	protected void createDeleteTask(final InetSocketAddress pAddress, final MessageDeleteRequest pMessage) {
		final int pSeq = this.mSequence.getAndIncrement();
		Runnable runnable = new TaskDeleteAndroid(pMessage.getLogFileNameAndPath());
		CallbackInfo info = new CallbackInfo(pAddress, pSeq, pMessage.getSequence(), pMessage.getMessageFlag(),
				runnable);
		this.mRunnables.put(pSeq, info);
		this.mCrosRef.put(pMessage.getSequence(), pSeq);
		CallbackTask task = new CallbackTask(runnable, this, pSeq);
		this.mService.execute(task);
	}

	@Override
	protected void createReadResponse(final CallbackInfo pCallbackInfo) {
		MessageSendSizeResponse pMessage = (MessageSendSizeResponse) this.getMessage(MessageFlag.SEND_SIZE_RESPONSE
				.getNumber());
		pMessage.setSequence(pCallbackInfo.getClientSeq());
		TaskReadAndroid runnable = (TaskReadAndroid) pCallbackInfo.getRunnable();
		pMessage.setFileSize(runnable.getFileSize());
		this.sendMessage(pCallbackInfo.getAddress(), pMessage);
	}

	@Override
	public void shutdownService(final InetSocketAddress pAddress){
		super.shutdownService(pAddress);
		this.mServce.shutdown();
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
