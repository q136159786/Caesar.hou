package com.cloudwise.ajax.communicate;


import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toushibao.ajax.resource.AjaxHttpServlet;
import com.toushibao.ajax.resource.TestServlet;
import com.toushibao.mobile.sender.SerderManager;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class AjaxCommMain extends Application<AjaxCommConfiguration>{
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
    private ExecutorService executor;
	public void initialize(Bootstrap<AjaxCommConfiguration> bootstrap) {
	}
	public void run(AjaxCommConfiguration configuration,Environment environment)throws ServletException,Exception {
		logger.info("Ajax Communicate Application service starting...."); 
		Map<String, String> suroClusterConfig = configuration.getSuroClusterConfig(); 
		BlockingQueue<Map> esDataQueue = new ArrayBlockingQueue<Map>(Integer.parseInt(suroClusterConfig.get("queueSize")));
		executor = environment.lifecycle().executorService("Sender-%d").minThreads(20).maxThreads(20).build();
		SerderManager senderManager =new SerderManager(executor, esDataQueue, suroClusterConfig);
		environment.lifecycle().manage(senderManager);
		AjaxHttpServlet ajaxhttpservlet = new AjaxHttpServlet(esDataQueue,suroClusterConfig);
		TestServlet testservlet = new TestServlet();
		environment.servlets().addServlet("ajaxhttpservlet", ajaxhttpservlet).addMapping("/ajaxApi");
		environment.servlets().addServlet("testservlet", testservlet).addMapping("/ping");
		environment.jersey().disable();
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	new AjaxCommMain().run(new String[]{"server","conf/toushibao.yml"});
	}

}
