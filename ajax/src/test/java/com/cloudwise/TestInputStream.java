package com.cloudwise;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestInputStream {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket s;
		try {
		s = new Socket("localhost",6577);

		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.write("aslkjlksjdsad".getBytes());
		out.flush();
		out.close();
		} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

}
