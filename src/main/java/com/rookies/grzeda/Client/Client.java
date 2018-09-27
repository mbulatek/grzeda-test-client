package com.rookies.grzeda.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.rookies.grzeda.Client.ReceiverEventListener.Event;

import Messaging.Message;
import Messaging.MessageChat;

public class Client {
	Socket clientSocket = null;
	int senderId;
	int currentTable;

	public void run() {
		try {
			clientSocket = new Socket("localhost", 8008);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Receiver receiver = new Receiver();
		
		receiver.socket = clientSocket;
		receiver.eventListener = new ReceiverEventListener() {
			
			@Override
			public void eventInitiailized(Event event) {
				senderId = event.message.senderId;
			}
			
			@Override
			public void eventChat(Event event) {
				MessageChat msg = (MessageChat) event.message;
				
				if (msg.senderId != senderId) {
					System.out.println("Other: " + msg.text);
				}
			}
		};
		
		new Thread(receiver).start();
		
		ObjectOutputStream outToServer = null;
		try {
			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scanner input = new Scanner(System.in);
		System.out.println("Enter table No: ");
		int tableNo = input.nextInt();
		input.nextLine();
		
		Message msgInit = new Message();
		msgInit.tableNo = tableNo;
		msgInit.type = Message.Type.initial;
		
		try {
			outToServer.writeObject(msgInit);
		} catch (IOException e) {
			e.printStackTrace();
		}            


		while (true) {
			MessageChat msg = new MessageChat();
			
			msg.tableNo = tableNo;
			
			System.out.println("You: ");
			msg.text = input.nextLine();
			
			msg.type = Message.Type.chat;
			
			try {
				outToServer.writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}            
		}
	}
}
