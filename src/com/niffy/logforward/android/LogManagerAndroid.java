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
import com.niffy.logforwarder.lib.messages.MessageSendRequest;

public class LogManagerAndroid extends LogManagerServer {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(LogManagerAndroid.class);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public LogManagerAndroid(final ISelector pSelector, final int pVersion) {
		super(pSelector, pVersion);
	}

	public LogManagerAndroid(final int pVersion) {
		super(pVersion);
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
		CallbackTask task = new CallbackTask(runnable, this, pSeq);
		this.mService.execute(task);
	}
	
	/*
	@Override
	protected void createResponse(final CallbackInfo pCallbackInfo) {
		if (pCallbackInfo.getMessageFlag() == MessageFlag.SEND_REQUEST.getNumber()) {
			this.createReadResponse(pCallbackInfo);
		} else if (pCallbackInfo.getMessageFlag() == MessageFlag.DELETE_REQUEST.getNumber()) {
			this.createDeleteResponse(pCallbackInfo);
		} else {
			log.error("Unknown task. Message Flag : {}", pCallbackInfo.getMessageFlag());
		}
	}

	@Override
	protected void createReadResponse(final CallbackInfo pCallbackInfo) {
		MessageSendResponse pMessage = (MessageSendResponse) this.getMessage(MessageFlag.SEND_RESPONSE.getNumber());
		pMessage.setSequence(pCallbackInfo.getClientSeq());
		TaskRead runnable = (TaskRead) pCallbackInfo.getRunnable();
		pMessage.setFileSize(runnable.getFileSize());
		pMessage.setLogFileNameAndPath(runnable.getData());
		this.sendMessage(pCallbackInfo.getAddress(), pMessage);
	}

	@Override
	protected void createDeleteResponse(final CallbackInfo pCallbackInfo) {
		MessageDeleteResponse pMessage = (MessageDeleteResponse) this.getMessage(MessageFlag.DELETE_RESPONSE
				.getNumber());
		pMessage.setSequence(pCallbackInfo.getClientSeq());
		TaskDelete runnable = (TaskDelete) pCallbackInfo.getRunnable();
		pMessage.setDeleted(runnable.getTaskSuccesful());
		this.sendMessage(pCallbackInfo.getAddress(), pMessage);
	}
	*/
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
