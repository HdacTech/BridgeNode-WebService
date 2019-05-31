/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.dao.common;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

/**
 * Common Data Access Object Interface
 * 
 * @version 0.8
 * 
 * @see		org.apache.ibatis.session.SqlSession
 * @see     org.springframework.stereotype.Repository
 */
@Repository
public interface CommonDao
{
	
	/**
	 * set ibatis sqlSession
	 * 
	 * @param sqlSession ibatis sqlSession
	 */
	public void setSqlSession(SqlSession sqlSession);
	
//	public long getSeqMember();

	/**
	 * get sequencial number(unique database no)
	 * @return (long) get next database no(UID)
	 */
	public long getSeqTokenInfo();

	/**
	 * get allowed access ip list
	 * @return (List) get access ip list
	 */
	public List<String> getAccessIp();
}