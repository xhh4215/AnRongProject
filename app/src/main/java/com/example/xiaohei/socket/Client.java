package com.example.xiaohei.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

	private Socket socket;
	private InputStreamReader mInputStreamReader;
	private BufferedReader mBufferReader;
	private OutputStreamWriter mOutputStreamReader;
	private BufferedWriter mBufferWriter;
	
	public Client(Socket socket) {
		setSocket(socket);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	private void setSocket(Socket socket) {
		this.socket = socket;
		try {
			mInputStreamReader = new InputStreamReader(socket.getInputStream());
			mBufferReader = new BufferedReader(mInputStreamReader);
			mOutputStreamReader = new OutputStreamWriter(socket.getOutputStream());
			mBufferWriter = new BufferedWriter(mOutputStreamReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Object receive() throws ClassNotFoundException, IOException {
		String data =  mBufferReader.readLine();
		return data;
	}
	
	public void send(String data) throws IOException {
		mBufferWriter.write(data);
		mBufferWriter.flush();
	}
}
