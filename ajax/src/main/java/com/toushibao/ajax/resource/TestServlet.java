package com.toushibao.ajax.resource;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet { 
	private static final long serialVersionUID = 1L;


	public void service(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException { 
		resp.setContentType("text/html;charset=UTF-8"); // 这条语句指明了向客户端发送的内容格式和采用的字符编码
		resp.setStatus(200);
		PrintWriter out = resp.getWriter();
		out.println("pong"); // 利用PrintWriter对象的方法将数据发送给客户端
		out.close();
	} 
}

