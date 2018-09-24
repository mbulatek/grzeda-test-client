package com.rookies.grzeda.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Messaging.Message;

interface DataReadyEventListener {
	public void event(Message message);
}

public class Receiver implements Runnable {
	public DataReadyEventListener eventListener;
	public Socket socket;
	
	ObjectInputStream inFromClient;
	
	public void run() {
		try {
			inFromClient = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (socket.isConnected()) {
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
					eventListener.event(inMsg);
				}
			}
		}
	}
}
