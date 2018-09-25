package com.rookies.grzeda.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Messaging.Message;
import Messaging.ChatMessage;
import com.rookies.grzeda.Client.Receiver;

public class Main {

	public static void main(String[] args) {
		Socket clientSocket = null;
		try {
			clientSocket = new Socket("mbulatek.nazwa.pl", 8008);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Receiver receiver = new Receiver();
		
		receiver.socket = clientSocket;
		receiver.eventListener = new DataReadyEventListener() {
			
			public void event(Message message) {
				ChatMessage msg = (ChatMessage) message;
				System.out.println("Other: " + msg.text);
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
			ChatMessage msg = new ChatMessage();
			
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
