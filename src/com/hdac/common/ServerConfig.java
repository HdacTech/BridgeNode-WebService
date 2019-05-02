/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.common;

import org.springframework.web.socket.client.WebSocketConnectionManager;

/**
 * This class will be moved to the contractlib
 * Deprecated
 * 
 * @version 0.8
 * 
 * @see     org.springframework.web.socket.client.WebSocketConnectionManager
 */
public class ServerConfig
{
	private String rpcIp;
	private String rpcPort;
	private String rpcUser;
	private String rpcPassword;
	private String chainName;
	private String wsHost;
	private String wsIp;
	private String wsPort;
	private WebSocketConnectionManager webSocketManager;

	public String getRpcIp()
	{
		return rpcIp;
	}
	public void setRpcIp(String rpcIp)
	{
		this.rpcIp = rpcIp;
	}
	public String getRpcPort()
	{
		return rpcPort;
	}
	public void setRpcPort(String rpcPort)
	{
		this.rpcPort = rpcPort;
	}
	public String getRpcUser()
	{
		return rpcUser;
	}
	public void setRpcUser(String rpcUser)
	{
		this.rpcUser = rpcUser;
	}
	public String getRpcPassword()
	{
		return rpcPassword;
	}
	public void setRpcPassword(String rpcPassword)
	{
		this.rpcPassword = rpcPassword;
	}
	public String getChainName()
	{
		return chainName;
	}
	public void setChainName(String chainName)
	{
		this.chainName = chainName;
	}
	public String getWsHost()
	{
		return wsHost;
	}
	public void setWsHost(String wsHost)
	{
		this.wsHost = wsHost;
	}
	public String getWsIp()
	{
		return wsIp;
	}
	public void setWsIp(String wsIp)
	{
		this.wsIp = wsIp;
	}
	public String getWsPort()
	{
		return wsPort;
	}
	public void setWsPort(String wsPort)
	{
		this.wsPort = wsPort;
	}
	public WebSocketConnectionManager getWebSocketManager()
	{
		return webSocketManager;
	}
	public void setWebSocketManager(WebSocketConnectionManager webSocketManager)
	{
		this.webSocketManager = webSocketManager;
	}
}