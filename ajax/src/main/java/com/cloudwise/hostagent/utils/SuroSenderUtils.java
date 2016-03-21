package com.cloudwise.hostagent.utils;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.suro.ClientConfig;
import com.netflix.suro.client.SuroClient;
import com.netflix.suro.message.Message;

public class SuroSenderUtils { 
	private static final Logger logger = LoggerFactory.getLogger(SuroSenderUtils.class);
	public static void sendToSuro(SuroClient sClient, String topic,  byte[] data){
		sClient.send(new Message(topic, data));
	}
	
	public static SuroClient creatSuroClientr(Map<String,String> suroClusterConfig){
		SuroClient sClient = null;
		if(suroClusterConfig != null){
			try {
				String SURO_CLIENT_TYPE = "clientType";
				String SURO_LB_SERVER = "loadBalancerServer";
				String SURO_LB_TYPE = "loadBalancerType";
				
				Properties clientPros = new Properties();

				clientPros.setProperty(ClientConfig.CLIENT_TYPE, suroClusterConfig.get(SURO_CLIENT_TYPE));
				clientPros.setProperty(ClientConfig.LB_SERVER, suroClusterConfig.get(SURO_LB_SERVER));
				clientPros.setProperty(ClientConfig.LB_TYPE, suroClusterConfig.get(SURO_LB_TYPE));
				System.out.println("properties are : " + clientPros);
				sClient = new SuroClient(clientPros);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
		return sClient;
	}
}
