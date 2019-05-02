/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.interceptor;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AccessInterceptor extends HandlerInterceptorAdapter
{
	private static Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);
	public static  ArrayList<String> ipList = new ArrayList<String>();
	
	static
	{
		try
		{
			setIpList("config/access-ip.properties");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		String uri = request.getRequestURI();
			
		if(uri.contains("form")) 
		{
			if(ipList.size() != 0) 
			{
				if(!ipList.contains(request.getServerName()))
				{
					PrintWriter writer = response.getWriter();
					writer.println("<script>alert('Access fail. It can only use for administrator.'); history.go(-1);</script>");
					writer.flush();
					return false;
				}
			}
		}
			
		return true;
    }
	
	public static void setIpList(String resource)
	{
		Properties properties = new Properties();

		try
		{
			Reader reader = Resources.getResourceAsReader(resource);
			properties.load(reader);
						
			for(int i = 0; i < properties.size(); i++) 
			{
				String allowIp = properties.getProperty(String.valueOf(i+1));
				if(allowIp.equals("127.0.0.1")) allowIp = "localhost";
				ipList.add(allowIp);
			}
			
			logger.debug("IP properties read success. IP list size = " + properties.size());

			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}