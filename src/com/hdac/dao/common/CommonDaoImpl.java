/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.dao.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.hdac.comm.StringUtil;

/**
 * Common Data Access Object Implementation
 * 
 * @version 0.8
 * 
 * @see		java.util.HashMap
 * @see		java.util.Map
 * @see		org.apache.ibatis.session.SqlSession
 * @see     org.springframework.stereotype.Repository
 */
@Repository
public class CommonDaoImpl implements CommonDao
{
	private SqlSession sqlSession;

	@Override
	public void setSqlSession(SqlSession sqlSession)
	{
		this.sqlSession = sqlSession;
	}

//	@Override
//	public long getSeqMember()
//	{
//		Map<String, Object> map = new HashMap<String, Object>();
//		sqlSession.insert("common.insert", map);
//
//		return Long.parseLong(StringUtil.nvl(map.get("seq_val"), "0"));
//	}
//
	@Override
	public long getSeqTokenInfo()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		sqlSession.insert("common.insertTokenInfo", map);

		return Long.parseLong(StringUtil.nvl(map.get("seq_val"), "0"));
	}

	@Override
	public List<String> getAccessIp()
	{
		return sqlSession.selectList("common.selectAccessIp");
	}
}