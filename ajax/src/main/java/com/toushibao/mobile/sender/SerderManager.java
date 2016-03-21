package com.toushibao.mobile.sender;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.lifecycle.Managed;

public class SerderManager implements Managed {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ExecutorService executor;

	private final BlockingQueue<Map> esDataQueue;

	private final Map<String, String> suroClusterConfig;

	public SerderManager(ExecutorService executor, BlockingQueue<Map> esDataQueue,
			Map<String, String> suroClusterConfig) {
		this.executor = executor;

		this.esDataQueue = esDataQueue;
		this.suroClusterConfig = suroClusterConfig;
	}

	public void start() throws Exception {
		try {

			int threadNum = Integer
					.valueOf(suroClusterConfig.get("threadNum") == null ? "2" : suroClusterConfig.get("threadNum"));
			for (int i = 0; i < threadNum; i++) {
				executor.execute(new DataSender(i + 1, esDataQueue, suroClusterConfig));
			}

		} catch (Exception ex) {
			logger.error("Unexpected Error occurred", ex);
			throw new RuntimeException(ex);
		}
	}

	public void stop() throws Exception {

	}
}
