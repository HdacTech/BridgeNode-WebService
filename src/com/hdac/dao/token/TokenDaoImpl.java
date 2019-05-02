/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.dao.token;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
/**
 * Token Data Access Object Implementation
 * 
 * @version 0.8
 * 
 * @see     java.util.List
 * @see     java.util.Map
 * @see     org.apache.ibatis.session.SqlSession
 * @see     org.springframework.stereotype.Repository
 */
@Repository
public class TokenDaoImpl implements TokenDao
{
	private SqlSession sqlSession;

	@Override
	public void setSqlSession(SqlSession sqlSession)
	{
		this.sqlSession = sqlSession;
	}

	@Override
	public List<String> getSeed(Map<String, Object> paramMap)
	{
		return sqlSession.selectList("token.getSeedList", paramMap);
	}

	@Override
	public int insertSeedWords(Map<String, Object> paramMap)
	{
		return sqlSession.insert("token.insertSeedWords", paramMap);
	}

	@Override
	public List<Map<String, Object>> getTokenList(Map<String, Object> paramMap)
	{
		return sqlSession.selectList("token.list");
	}

	@Override
	public Map<String, Object> getTokenInfo(Map<String, Object> paramMap)
	{
		return sqlSession.selectOne("token.select", paramMap);
	}

	@Override
	public int insertTokenInfo(Map<String, Object> paramMap)
	{
		return sqlSession.insert("token.insert", paramMap);
	}
	
	//-->for verify
	@Override
	public Map<String, Object> getVerifyAddress(Map<String, Object> paramMap)
	{
		return sqlSession.selectOne("token.getVerifyAddress", paramMap);
	}
	//-->for verify
}