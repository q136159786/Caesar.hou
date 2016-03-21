package com.cloudwise;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class TestApi {
	public void sendMessage() throws Exception {
	    System.out.println("调用servlet开始=================");
	    ReadFile readFile =new ReadFile();
	    String sendStr=readFile.readFileContent("/Users/houshengliang/Documents/ajax_aaa.txt");

	    BufferedReader reader = null;
	    try {
	        String strMessage = "";
	        StringBuffer buffer = new StringBuffer();
	        // 接报文的地址
	        URL uploadServlet = new URL(
	               "http://10.0.1.62:4577/userEvent");
	        HttpURLConnection servletConnection = (HttpURLConnection) uploadServlet
	               .openConnection();
	        servletConnection.setRequestMethod("GET");
	        servletConnection.setDoOutput(true);
	        servletConnection.setDoInput(true);
	        servletConnection.setAllowUserInteraction(true);
	        OutputStream output = servletConnection.getOutputStream();
	        System.out.println("发送的报文：");
	        System.out.println(sendStr.toString());
	        output.write(sendStr.toString().getBytes());
	        output.flush();
	        output.close();
	        // 获取返回的数据
	        InputStream inputStream = servletConnection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(inputStream));
	        while ((strMessage = reader.readLine()) != null) {
	           buffer.append(strMessage);
	        }
	        System.out.println("接收返回值:" + buffer);
	    } catch (java.net.ConnectException e) {
	        throw new Exception();
	    } finally {
	        if (reader != null) {
	           reader.close();
	        }
	    }
	 }
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestApi testApi =new TestApi();
		testApi.sendMessage();
	}

}
