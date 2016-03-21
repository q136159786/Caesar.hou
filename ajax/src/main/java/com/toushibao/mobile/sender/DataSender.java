package com.toushibao.mobile.sender;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudwise.ajax.container.ConstantContainer;
import com.cloudwise.hostagent.utils.SuroSenderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.suro.client.SuroClient;

public class DataSender  implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private final BlockingQueue<Map> esDataQueue;  
	private final int workerId; 
	private volatile boolean stop = false;
	
	private final SuroClient sClient;
	
	public DataSender(int workerId, BlockingQueue<Map> esDataQueue, Map<String, String> suroClusterConfig) { 
		this.workerId = workerId;
		this.esDataQueue = esDataQueue;
		this.sClient = SuroSenderUtils.creatSuroClientr(suroClusterConfig);
	}
	
	private String logMsg(String msg) {
		return "[ESDataSender-" + workerId + "] " + msg;
	}
	
	public void run() {
		logger.info(logMsg("ESDataSender is running..."));
		if(esDataQueue != null){ 
			while (!Thread.currentThread().isInterrupted() && !stop) {
				try {
					Map data = esDataQueue.take();
					byte[] byteData = objectMapper.writeValueAsBytes(data); 
				    logger.info("Send ajax topic to suro");
					SuroSenderUtils.sendToSuro(sClient, ConstantContainer.AJAX_TOPIC_NAME, byteData); 
				} catch (Exception e) {
					logger.error("Unexpected Error Occurred", e);
				}
			}
		}
	}
}
