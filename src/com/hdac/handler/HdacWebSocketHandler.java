/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//import com.hdac.common.BeanUtil;
import com.hdac.common.Constants;
import com.hdac.common.StringUtil;

/**
 * HdacWebSocketHandler extends TextWebSocketHandler
 * 
 * @version 0.8
 * 
 * @see     java.util.HashMap
 * @see     java.util.Iterator
 * @see     java.util.Map
 * @see     org.json.JSONObject
 * @see     org.slf4j.Logger
 * @see     org.slf4j.LoggerFactory
 * @see     org.springframework.web.socket.CloseStatus
 * @see     org.springframework.web.socket.TextMessage
 * @see     org.springframework.web.socket.WebSocketSession
 * @see     org.springframework.web.socket.handler.TextWebSocketHandler
 */
public class HdacWebSocketHandler extends TextWebSocketHandler
{
	private static Logger logger = LoggerFactory.getLogger(HdacWebSocketHandler.class);
	private static Map<String, Object> constantsMap = null;
	private static Map<String, Object> tokenInfo = null;

	static
	{
		setMap();
	}

	private String wsHost = "";
	private JSONObject configObj = null;
//	private HdacService service = null;

	public HdacWebSocketHandler(JSONObject obj, String wsHost)
	{
		super();
//		service = (HdacService)BeanUtil.getBean(HdacServiceImpl.class);
		this.wsHost = wsHost;
		this.configObj = obj;

//		if (tokenInfo == null)
//			tokenInfo = service.wsContract();
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception
	{
		super.afterConnectionEstablished(session);
		logger.debug("connect!!" + session);
	}

	/**
	 * get block or transaction info in real time through websocket
	 * 
	 * @param session		websocket sesstion connected core(main/side)
	 * @param message		data from(block or transaction)
	 * @throws Exception	exception
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
	{
		super.handleTextMessage(session, message);
		handle(session, message);
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
	{
		super.afterConnectionClosed(session, status);
		logger.debug("close!!");
	}

	private void handle(WebSocketSession session, TextMessage message) throws Exception
	{
		try
		{
			String data = message.getPayload();
			if (data == null)
				return;

			JSONObject resultObj = new JSONObject(data);
			int method = getMapData(resultObj);

			switch (method)
			{
				case Constants.nINFO:
					logger.debug("Constants.nINFO");
					session.sendMessage(new TextMessage(configObj.toString()));
					break;

				case Constants.nAUTH:
					logger.debug("Constants.nAUTH");
//					session.sendMessage(new TextMessage(Constants.cmdBLOCK2));  //do not use now
//					session.sendMessage(new TextMessage(Constants.cmdTX2));     //do not use now
					break;

				case Constants.nBLOCK:
					logger.debug("Constants.nBLOCK : " + wsHost);
//					if (tokenInfo == null)
//						tokenInfo = service.wsContract();
////					logger.debug("Constants.tokenInfo : " + tokenInfo);
//					if (tokenInfo != null)
//						service.wsBlock(tokenInfo, wsHost, resultObj);
					break;

				case Constants.nTX:
					logger.debug("Constants.nTX : " + data);
					break;

				default:
					logger.debug("default : " + data);
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * make map(websocket command,switch index)
	 * 
	 */
	private static void setMap()
	{
		if (constantsMap == null)
		{
			constantsMap = new HashMap<String, Object>();
			constantsMap.put(Constants.strINFO,		Constants.nINFO);
			constantsMap.put(Constants.strAUTH,		Constants.nAUTH);
			constantsMap.put(Constants.strBLOCK,	Constants.nBLOCK);
			constantsMap.put(Constants.strTX,		Constants.nTX);
		}
	}
	
	private String SwitchID(JSONObject obj)
	{
		String result = "";
		
		Iterator<?> keys = obj.keys();

		if (keys.hasNext())
			result = StringUtil.nvl(keys.next());
		
		return result;
	}
	/**
	 * change websocket command into switch index
	 * 
	 * 
	 * @param obj	message.getPayload() 
	 * @return		websocket switch index
	 */
	private int getMapData(JSONObject obj)
	{
		int result = -1;
		Iterator<?> keys = obj.keys();

		while (keys.hasNext())
		{
			String data = StringUtil.nvl(keys.next());
//			logger.debug("keys.next() : "  +data);
			
			String key = StringUtil.toSmallLetter(data, 0);
			
			if (constantsMap.containsKey(key))
			{
				result = Integer.parseInt(StringUtil.nvl(constantsMap.get(key), "-1"));
				break;
			}
		};
		return result;
	}
}