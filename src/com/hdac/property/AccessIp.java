package com.hdac.property;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.hdac.common.BeanUtil;
import com.hdac.common.SqlMapConfig;
import com.hdac.dao.common.CommonDao;
import com.hdac.dao.common.CommonDaoImpl;

public class AccessIp
{
	private List<String> list = null;

	private AccessIp()
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			CommonDao commonDao = (CommonDao)BeanUtil.getBean(CommonDaoImpl.class);
			commonDao.setSqlSession(sqlSession);

			list = commonDao.getAccessIp();
		}
		finally
		{
			sqlSession.close();
		}
	}
	public static AccessIp getInstance()
	{
		return LazyHolder.INSTANCE;
	}	  
	private static class LazyHolder
	{
		private static final AccessIp INSTANCE = new AccessIp();  
	}

	public boolean checkAccessIp(String ip)
	{
		if ((list == null) || list.isEmpty())
			return true;

		for (String s : list)
		{
			if (ip.equals(s))
				return true;
		}
		return false;
	}
}