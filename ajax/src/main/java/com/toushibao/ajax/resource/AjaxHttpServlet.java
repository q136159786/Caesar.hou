package com.toushibao.ajax.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.cloudwise.hostagent.utils.DES;
import com.cloudwise.hostagent.utils.SuroSenderUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.suro.client.SuroClient;

public class AjaxHttpServlet extends HttpServlet {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final BlockingQueue<Map> esDataQueue;
	private final SuroClient sClient;
    private final DES des=new DES();

	public AjaxHttpServlet(BlockingQueue<Map> esDataQueue, Map<String, String> suroClusterConfig) {
		// TODO Auto-generated constructor stub
		this.esDataQueue = esDataQueue;
		this.sClient = SuroSenderUtils.creatSuroClientr(suroClusterConfig);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajData = null;
		try {
			String from = null;
			boolean isGzip = false;
			String contentEncode = request.getHeader("Content-Encoding");
			if (contentEncode != null && contentEncode.trim().equals("gzip")) {
				isGzip = true;
			}

			ajData = request.getParameter("rumData");
			String requestIp = request.getRemoteAddr();
			if (ajData == null) {
				String ajDataBuffer=null;
				InputStream is = null;
					is = request.getInputStream();
				byte[] res = readBytes(is);
				if (res!=null) {
					ajDataBuffer = new String(res);
				}
				if(ajDataBuffer.contains("rumData=")&&ajDataBuffer.startsWith("rumData=")){
					ajData=ajDataBuffer.substring(ajDataBuffer.indexOf("rumData=")+"rumData=".length());
				}

				if (ajData == null || ajData.trim().length() == 0) {
					logger.info("RequestIp-->" + requestIp + " , request missing rumData......");
					response.setContentType("text/html;charset=UTF-8"); // 这条语句指明了向客户端发送的内容格式和采用的字符编码
					response.setStatus(200);
					PrintWriter out = response.getWriter();
					out.println("Missing rumData"); // 利用PrintWriter对象的方法将数据发送给客户端
					out.close();
					return;
				}
				from = "body_raw";
			} else {
				from = "url_parameter";
			}
			logger.info("requestIp: " + requestIp + ", isGzip: " + isGzip + ",from: "
					+ from + ", ajax_rum data: " + ajData);
			if (ajData != null) {
				HashMap<String, Object> ajaxRumData = (HashMap<String, Object>) objectMapper.readValue(ajData,HashMap.class);
				parseRumData(ajaxRumData, requestIp);
			}
				logger.info("send ok");
				response.setContentType("text/html;charset=UTF-8"); // 这条语句指明了向客户端发送的内容格式和采用的字符编码
				response.setStatus(200);
				PrintWriter out = response.getWriter();
				out.println("send ok!"); // 利用PrintWriter对象的方法将数据发送给客户端
				out.close();
			
		} catch (Exception ex) {
			// TODO: handle exception
			logger.error("send error!");
			response.setStatus(200);
			response.setContentType("text/html;charset=UTF-8"); // 这条语句指明了向客户端发送的内容格式和采用的字符编码
			PrintWriter out = response.getWriter();
			out.println("send error!"); // 利用PrintWriter对象的方法将数据发送给客户端
			out.close();
		}
	}

	private void parseRumData(HashMap<String, Object> ajaxRumData, String requestIp) throws JsonParseException, JsonMappingException, IOException {
		// TODO Auto-generated method stub
		//--------------------------qequset_info  处理 start-------------------------
		String request_info=(String) ajaxRumData.get("request_info");
		String[] requestList=request_info.split("\\_");
		String request = des.decrypt(requestList[0]);
		HashMap<String,Object> requests = (HashMap<String,Object>)objectMapper.readValue(request, HashMap.class);
		if (requests!=null&&requests.size()>0) {
			ajaxRumData.put("account_id", requests.get("account"));
			ajaxRumData.put("target_id",requests.get("target_id"));
			ajaxRumData.put("uuid", requests.get("uuid"));
		}
		if(requestList[1]!=null&&!requestList[1].trim().equals("")){
			ajaxRumData.put("request_id", requestList[1]);
		}
		ajaxRumData.remove("request_info");
		// --------------------------_raw add start-------------------------------------
		if (ajaxRumData.get("url") != null) {
			ajaxRumData.put("url_raw", ajaxRumData.get("url"));
		}
		ajaxRumData.remove("url");
		if (ajaxRumData.get("domain") != null) {
			ajaxRumData.put("domain_raw ", ajaxRumData.get("domain"));
		}
		ajaxRumData.remove("domain");
		if (ajaxRumData.get("uri") != null) {
			ajaxRumData.put("uri_raw", ajaxRumData.get("uri"));
		}
		ajaxRumData.remove("uri");
		// --------------------------_raw add end---------------------------------------

		// --------------------------nest_ add start-------------------------------------
		if (ajaxRumData.get("events") != null) {
			ajaxRumData.put("nest_events", ajaxRumData.get("events"));
			ajaxRumData.remove("events");
		}
		if (ajaxRumData.get("error_list") != null) {
			ajaxRumData.put("nest_errors", ajaxRumData.get("error_list"));
			ajaxRumData.remove("error_list");
		}
		// --------------------------nest_ add end-------------------------------------
		if (ajaxRumData != null && ajaxRumData.size() > 0) {
			//保存数据
			// --------------------------nest_events 处理 start---------------------------
			Object events = ajaxRumData.get("nest_events");
			if (events != null) {
				if (events instanceof List && ((List) events).size() > 0) {
					for (Map event : (List<Map<String, Object>>) events) {
						event.put("req_url_raw", event.get("req_url"));
						event.remove("req_url");
					}
				}
			}
			// -----------------------nest_events处理end----------------------------------
			
			//------------------------nest_errors处理start-----------------------------------
			Object errors = ajaxRumData.get("nest_errors");
			if(errors!=null){
				if (errors instanceof List&&((List) errors).size()>0) {
					for(Map error :(List<Map<String, Object>>)errors){
						error.put("url_raw", error.get("url"));
						error.remove("url");
						error.put("msg_raw", error.get("msg"));
						error.remove("msg");
						error.put("detail_raw", error.get("detail"));
						error.remove("detail");
					}
				}
			}
			/**
			 * 发送数据到suro
			 */
			logger.info("Push one ajax data to queue.");
			byte[] bytedata = objectMapper.writeValueAsBytes(ajaxRumData);
			logger.info(new String(bytedata));
			boolean succ = esDataQueue.offer(ajaxRumData);
			
			if (!succ) {
				logger.info("Push to queue failed, then send to suro directly.");
				byte[] byteData=null;
				try {
					byteData = objectMapper.writeValueAsBytes(ajaxRumData);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
				logger.error("Converting data bytes failed");
				}
				SuroSenderUtils.sendToSuro(sClient, com.cloudwise.ajax.container.ConstantContainer.AJAX_TOPIC_NAME,byteData);
				
				System.out.println(new String(byteData));
			}
			logger.debug("--------- post ajax rum data success. ---------");
		}
	}

	public byte[] readBytes(InputStream in) throws IOException {
		byte[] content = null;
		BufferedInputStream bufin = null;
		ByteArrayOutputStream out = null;

		try {
			bufin = new BufferedInputStream(in);
			int buffSize = 1024;
			out = new ByteArrayOutputStream(buffSize);

			byte[] temp = new byte[buffSize];
			int size = 0;
			while ((size = bufin.read(temp)) != -1) {
				out.write(temp, 0, size);
				out.flush();
			}
			content = out.toByteArray();
		} catch (Exception ex) {
			logger.error(logMsg(ExceptionUtils.getStackTrace(ex)));

		} finally {
			if (out != null) {
				out.close();
			}
			if (bufin != null) {
				bufin.close();
			}
		}
		return content;
	}
	private String logMsg(String msg) {
		return "[AjaxHttpServlet] " + msg;
	}
}
