package com.hdac.property;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.hdac.common.SqlMapConfig2;
import com.hdac.service.CommonService;

public class ServerConfig
{
	private Map<String, Object> mainChain = null;
	private Map<String, Object> sideChain = null;

	private ServerConfig()
	{
		SqlSession sqlSession = SqlMapConfig2.getSqlSession();

		try
		{
			CommonService service = CommonService.getInstance();
			this.mainChain = service.getMainChainInfo(sqlSession);
			this.sideChain = service.getSideChainInfo(sqlSession);
		}
		finally
		{
			sqlSession.close();
		}
	}
	public static ServerConfig getInstance()
	{
		return LazyHolder.INSTANCE;
	}	  
	private static class LazyHolder
	{
		private static final ServerConfig INSTANCE = new ServerConfig();  
	}

	public Map<String, Object> getMainChainInfo()
	{
		return this.mainChain;
	}
	public Map<String, Object> getSideChainInfo()
	{
		return this.sideChain;
	}

	public Map<String, Object> getServerType(String path)
	{
		if ("public".equals(path))
			return this.mainChain;
		return this.sideChain;
	}
}