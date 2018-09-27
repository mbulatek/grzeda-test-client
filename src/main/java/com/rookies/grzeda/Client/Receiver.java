package com.rookies.grzeda.Client;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.rookies.grzeda.Connection.Client;

import Messaging.Message;

interface ReceiverEventListener {
	class Event {
		Receiver source;
		Message message;
	}

	public void eventChat(Event event);
	public void eventInitiailized(Event event);
}

public class Receiver implements Runnable {
	public ReceiverEventListener eventListener;
	public Socket socket;
	
	ObjectInputStream inFromClient;
	
	public void run() {
		try {
			inFromClient = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (socket.isClosed() == false) {
            Message inMsg = null;
			try {
				inMsg = (Message)inFromClient.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (inMsg != null) {
				if (eventListener != null) {
					if (inMsg.type == Message.Type.initial) {
						ReceiverEventListener.Event event = new ReceiverEventListener.Event();
						event.source = this;
						event.message = inMsg;
						eventListener.eventInitiailized(event);
					}
					if (inMsg.type == Message.Type.chat) {
						ReceiverEventListener.Event event = new ReceiverEventListener.Event();
						event.source = this;
						event.message = inMsg;
						eventListener.eventChat(event);
					}
				}
			}
		}
	}
}
