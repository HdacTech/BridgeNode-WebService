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
 * Token Data Access Object Interface
 * 
 * @version 0.8
 * 
 * @see     java.util.List
 * @see     java.util.Map
 * @see     org.apache.ibatis.session.SqlSession
 * @see     org.springframework.stereotype.Repository
 */
@Repository
public interface TokenDao
{
	
	/**
	 * set mariadb session(use ibatis)
	 * 
	 * @param sqlSession ibatis sqlSesstion
	 */
	public void setSqlSession(SqlSession sqlSession);

	/**
	 * get bridge node seed
	 * 
	 * @param paramMap no data
	 * @return encoded word-list List(string)
	 */
	public List<String> getSeed(Map<String, Object> paramMap);
	
	/**
	 * put bridge node seed into database
	 * 
	 * @param paramMap encoded seedWords
	 * @return int(save state)
	 */
	public int insertSeedWords(Map<String, Object> paramMap);

	/**
	 * get token list info from database
	 * 
	 * @param paramMap no data
	 * @return return token list info (List(map(string,object)))
	 */
	public List<Map<String, Object>> getTokenList(Map<String, Object> paramMap);

	/**
	 * get selected token info from database
	 * 
	 * @param paramMap value(no)
	 * @return return token info (map(string,object))
	 */
	public Map<String, Object> getTokenInfo(Map<String, Object> paramMap);
	
	/**
	 * put token info into database
	 * called by insertTokenInfo(TokenServiceImpl)
	 * 
	 * @param paramMap values(tokenName,tokenCap,tokenSwapRatio,pointNumber,serverInfo,...etc)
	 * @return int(save state)
	 */
	public int insertTokenInfo(Map<String, Object> paramMap);
	
	/**
	 * verify address for anchoring
	 * called by getVerifyAddress(TokenServiceImpl)
	 * 
	 * @param paramMap	(Map(string,object)) address data for anchoring
	 * @return			(Map(string,object)) formatted string-object map data
	 */	
	public Map<String, Object> getVerifyAddress(Map<String, Object> paramMap);  //-->for verify
}