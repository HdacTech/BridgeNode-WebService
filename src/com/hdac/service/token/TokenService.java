/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.service.token;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Token Service interface 
 * 
 * @version 0.8
 * 
 * @see		java.util.List
 * @see     java.util.Map
 * @see     org.springframework.stereotype.Service
 */
@Service
public interface TokenService
{

    /**
    * make seed value when start bridge node first
    */	
	public void checkRootSeed();

    /**
	 * connect websocket both main chain and side chain
	 */
	public void checkWebSocket();
	
	/**
	 * get token list data from database
	 * 
	 * @param paramMap	(Map(string,object)) token info
	 * @return			(List(Map(string,object))) the formatted list(string, object) map data
	 */
	public List<Map<String, Object>> getTokenList(Map<String, Object> paramMap);
	/**
	 * get detailed token data from database
	 * 
	 * @param paramMap	(Map(string,object)) token info
	 * @return			(Map(string,object)) formatted string-object map data
	 */
	public Map<String, Object> getTokenInfo(Map<String, Object> paramMap);
	/**
	 * 1. Issue token
	 * 2. Register contract data
	 * 3. insert tokeninfo into database
	 * 
	 * @param paramMap	(Map(string,object)) token info
	 * @return			(Map(string,object)) formatted string-object map data
	 */
	public Map<String, Object> insertToken(Map<String, Object> paramMap);
	/**
	 * verify address for anchoring
	 * called by verifyTx(RpcServiceImpl)
	 * 
	 * @param paramMap	(Map(string,object)) address data for anchoring
	 * @return			(Map(string,object)) formatted string-object map data
	 */	
	public Map<String, Object> getVerifyAddress(Map<String, Object> paramMap);  //-->for verify

	/**
	 * insert tokeninfo into database
	 * 
	 * @param data		(string) tokeninfo data
	 * @return			(boolean) 
	 */	
	public boolean insertTokenInfo(String data);
	
	public String getMainPageData();  //--> for main
	
	public String getContractHash(MultipartFile file);  //-->for contract upload
	
	public Map<String, Object> updateLib(Map<String, Object> paramMap);
	
	public String getRecordPageData();  
}