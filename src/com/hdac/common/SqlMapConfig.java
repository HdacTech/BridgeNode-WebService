/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.common;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlMapConfig
{
	private static Logger logger = LoggerFactory.getLogger(SqlMapConfig.class);
	private static SqlSessionFactory sqlSession;

	static
	{
		String resource = "config/mybatis-config.xml";

		try
		{
			Reader reader = Resources.getResourceAsReader(resource);
			sqlSession = new SqlSessionFactoryBuilder().build(reader);
			reader.close();
		}
		catch (Exception e)
		{
			logger.debug("SqlMapConfig exception : " + e);
		}
	}

	public static SqlSession getSqlSession()
	{
		return getSqlSession(true);
	}
	public static SqlSession getSqlSession(boolean autoCommit)
	{
		return sqlSession.openSession(autoCommit);
	}
}