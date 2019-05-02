/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.service.token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
//import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.hdac.common.BeanUtil;
import com.hdac.common.HdacUtil;
import com.hdac.common.JsonUtil;
import com.hdac.common.SqlMapConfig;
import com.hdac.common.SqlMapConfig2;
//import com.hdac.common.StringUtil;
import com.hdac.contract.HdacTokenIssue;
import com.hdac.dao.rpc.RpcDao;
import com.hdac.dao.rpc.RpcDaoImpl;
//import com.hdac.dao.common.CommonDao;
//import com.hdac.dao.common.CommonDaoImpl;
import com.hdac.dao.token.TokenDao;
import com.hdac.dao.token.TokenDaoImpl;
//import com.hdac.service.rpc.RpcService;
//import com.hdac.service.rpc.RpcServiceImpl;
//import com.hdacSdk.hdacWallet.HdacCoreAddrParams;
import com.hdacSdk.hdacWallet.HdacWallet;
//import com.hdacSdk.hdacWallet.HdacWalletManager;

/**
 * Token Service implementation
 * 
 * @version 0.8
 * 
 * @see     java.util.HashMap
 * @see		java.util.List
 * @see     java.util.Map
 * @see     org.apache.ibatis.session.SqlSession
 * @see     org.json.JSONArray
 * @see     org.json.JSONException
 * @see     org.json.JSONObject
 * @see     org.slf4j.Logger
 * @see     org.slf4j.LoggerFactory 
 */
@Service
public class TokenServiceImpl implements TokenService
{
	private static Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

	@Override
	public void checkRootSeed()
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
			tokenDao.setSqlSession(sqlSession);

			Map<String, Object> paramMap = new HashMap<String, Object>();

			List<String> seedWords = tokenDao.getSeed(paramMap);
			if ((seedWords == null) || (seedWords.size() <= 0))
			{
				List<String> seed = HdacUtil.getSeedWord(null);
				if (seed != null)
				{
					List<String> encSeed = HdacUtil.encodeSeed(seed, HdacUtil.getKey());
					paramMap.put("seedWords", encSeed);

					int ret = tokenDao.insertSeedWords(paramMap);
					if (ret > 0)
					{
						sqlSession.commit();
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			sqlSession.close();
		}
	}
	
	/* 
	 * connect websocket both main chain and side chain
	 * 
	 * (non-Javadoc)
	 * @see com.hdac.service.token.TokenService#checkToken()
	 */
	@Override
	public void checkWebSocket()
	{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//if (getTokenList(paramMap).size() > 0)
		{
			//start web socket;
			if (HdacUtil._PUBLIC_.getWebSocketManager() == null)
				HdacUtil._PUBLIC_.setWebSocketManager(HdacUtil.getWebSocket(HdacUtil._PUBLIC_));
			if (HdacUtil._PRIVATE_.getWebSocketManager() == null)
				HdacUtil._PRIVATE_.setWebSocketManager(HdacUtil.getWebSocket(HdacUtil._PRIVATE_));
		}
	}

	/* (non-Javadoc)
	 * @see com.hdac.service.token.TokenService#getTokenList(java.util.Map)
	 */
	@Override
	public List<Map<String, Object>> getTokenList(Map<String, Object> paramMap)
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
			tokenDao.setSqlSession(sqlSession);
			return tokenDao.getTokenList(paramMap);
		}
		finally
		{
			sqlSession.close();
		}
	}

	@Override
	public Map<String, Object> getTokenInfo(Map<String, Object> paramMap)
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
			tokenDao.setSqlSession(sqlSession);
			return tokenDao.getTokenInfo(paramMap);
		}
		finally
		{
			sqlSession.close();
		}
	}

	@Override
	public Map<String, Object> insertToken(Map<String, Object> paramMap)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);

		//hyyang change make contract
		HdacTokenIssue main = new HdacTokenIssue();
		main.init(SqlMapConfig2.getSqlSession());
		
		HdacWallet wallet = HdacUtil.getTokenHdacWallet();
		String contractAddress = wallet.getHdacAddress(false, 0);
		String recordAddress = wallet.getHdacAddress(false, 1);
		String anchoringAddress = wallet.getHdacAddress(true, 0);
		
		String result = main.issueToken(paramMap, contractAddress);
	    logger.debug(" issueToken result : " + result);
		
		if (result == null)
			return resultMap;
		else {
			paramMap.put("contractAddress",		contractAddress);
			paramMap.put("tokenTxid",			result);
			paramMap.put("recordAddress",		recordAddress);
			paramMap.put("anchoringAddress",	anchoringAddress);
		}
		
//		//regist contractlib hash
//		bSuccess = sendTransaction(paramMap);		// make tx(registed contractlib hash) to main chain
//		if (bSuccess == false)
//			return resultMap;

		result = main.registTokenInfo(wallet, paramMap, contractAddress);		// make tx to main chain
	    logger.debug(" registTokenInfo result : " + result);
		
		if (result == null)
			return resultMap;
		else {
			paramMap.put("contractTxid", result);
		}
		
		
		boolean bSuccess = main.insertTokenInfo(paramMap);
	    logger.debug(" insertTokenInfo bSuccess : " + bSuccess);
		
		if (bSuccess)
			resultMap.put("success", bSuccess);
		
		
//		boolean bSuccess = issueToken(paramMap);	// issue token
//		if (bSuccess == false)
//			return resultMap;
//		
////		//regist contractlib hash
////		bSuccess = sendTransaction(paramMap);		// make tx(registed contractlib hash) to main chain
////		if (bSuccess == false)
////			return resultMap;
//
//		bSuccess = sendTransaction(paramMap);		// make tx to main chain
//		if (bSuccess == false)
//			return resultMap;
//
//		insertTokenInfo(paramMap, resultMap);		// save db

		return resultMap;
	}
	
	private boolean insertTokenInfo(Map<String, Object> paramMap, Map<String, Object> resultMap)
	{
		boolean flag = false;

		HdacTokenIssue main = new HdacTokenIssue();
		main.init(SqlMapConfig2.getSqlSession());
		
		flag = main.insertTokenInfo(paramMap);
//	    logger.debug(" insertTokenInfo bSuccess : " + flag);
		
		if (flag)
			resultMap.put("success", flag);
		
		return flag;
	}	

//	private boolean issueToken(Map<String, Object> paramMap)
//	{
//		boolean bSuccess = false;
//
//		SqlSession sqlSession = SqlMapConfig.getSqlSession();
//
//		try
//		{
//			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
//			tokenDao.setSqlSession(sqlSession);
//
//			Map<String, Object> tokenparamMap = new HashMap<String, Object>();
//
//			List<String> seedWords = tokenDao.getSeed(tokenparamMap);
//			List<String> seed = HdacUtil.decodeSeed(seedWords, HdacUtil.getKey());
//
//			HdacWallet wallet = HdacUtil.getHdacWallet(seed, null);
//			String contractAddress = wallet.getHdacAddress(false, 0);
//
//			String rAddress = wallet.getHdacAddress(false, 1);
//			String aAddress = wallet.getHdacAddress(true, 0);
//
//		    logger.debug("*** contractAddress : " + contractAddress);
//		    logger.debug("*** rAddress : " + rAddress);
//		    logger.debug("*** aAddress : " + aAddress);
//
//		    double pointNumber = Double.parseDouble(StringUtil.nvl(paramMap.get("pointNumber"), "0"));
////		    int pointNumber = 8;
//
//		    tokenparamMap.clear();
//			tokenparamMap.put("address",	contractAddress);
//			tokenparamMap.put("tokenName",	paramMap.get("tokenName"));
//			tokenparamMap.put("amount",		paramMap.get("tokenCap"));
//			tokenparamMap.put("unit",		Math.pow(10, pointNumber * -1));
//
//			RpcServiceImpl service = (RpcServiceImpl)BeanUtil.getBean(RpcServiceImpl.class);
//			Map<String, Object> resultTokenMap = service.assetIssue(tokenparamMap); 
//
//			bSuccess = (boolean)resultTokenMap.get("success");
//			if (bSuccess)
//			{
//				String tokenTxid = resultTokenMap.get("txid").toString();
//
//				paramMap.put("contractAddress",		contractAddress);
//				paramMap.put("tokenTxid",			tokenTxid);
//				paramMap.put("recordAddress",		rAddress);
//				paramMap.put("anchoringAddress",	aAddress);
//
//				paramMap.put("wallet",				wallet);
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			sqlSession.close();
//		}
//		return bSuccess;
//	}
//
//	private boolean sendTransaction(Map<String, Object> paramMap)
//	{
//		boolean bSuccess = false;
//
//		Map<String, Object> contractparamMap = new HashMap<String, Object>();
//		contractparamMap.put("addresses", paramMap.get("contractAddress"));
//
//		RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
//		List<JSONObject> rpcResult = rpcdao.getUtxosNew(contractparamMap, HdacUtil._PUBLIC_);
//		HdacWallet wallet = (HdacWallet)paramMap.get("wallet");
//
//		JSONArray jsonArray = new JSONArray(rpcResult);
//
//		logger.debug("*** jsonArray : " + jsonArray);
//		String raw_tx = HdacUtil.getRawTransaction(wallet, jsonArray, paramMap);
//		logger.debug("####" + raw_tx);
//
//		// send raw transaction
//		contractparamMap.clear();
//		contractparamMap.put("rawtx", raw_tx);
//
//		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
//		Map<String, Object> resultMap = service.sendTx(contractparamMap, HdacUtil._PUBLIC_);
//
//		bSuccess = (boolean)resultMap.get("success");
//		if (bSuccess)
//		{
//			String contractTxid = resultMap.get("txid").toString();
//			paramMap.put("contractTxid", contractTxid);
//
//			logger.debug("#### result" + contractTxid);
//		}
//		return bSuccess;
//	}
//
//	private boolean insertTokenInfo(Map<String, Object> paramMap, Map<String, Object> resultMap)
//	{
//		boolean flag = false;
//		SqlSession sqlSession = SqlMapConfig.getSqlSession();
//
//		try
//		{
//			CommonDao commonDao = (CommonDao)BeanUtil.getBean(CommonDaoImpl.class);
//			commonDao.setSqlSession(sqlSession);
//
//			long tokenNo = commonDao.getSeqTokenInfo();
//			if (tokenNo > 0)
//			{
//				TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
//				tokenDao.setSqlSession(sqlSession);
//
//				paramMap.put("tokenNo", tokenNo);
//
//				int ret = tokenDao.insertTokenInfo(paramMap);
//				if (ret > 0)
//				{
//					sqlSession.commit();
//					resultMap.put("success", "true");
//					flag = true;
//				}
//				else
//				{
//					throw new Exception("");
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			resultMap.put("message", e.getMessage());
//		}
//		finally
//		{
//			sqlSession.close();
//		}
//
//		return flag;
//	}
	
	//-->for verify
	@Override
	public Map<String, Object> getVerifyAddress(Map<String, Object> paramMap)
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
			tokenDao.setSqlSession(sqlSession);
			return tokenDao.getVerifyAddress(paramMap);
		}
		finally
		{
			sqlSession.close();
		}
	}
	//-->for verify

/////////// for lib
//	public Map<String, Object> IssueToken(Map<String, Object> paramMap)
//	{
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("success", false);
//
//		try
//		{
//			List<String> seeds = (List<String>)paramMap.get("seed");
//
//			HdacWallet wallet = getHdacWallet(seeds, null);
//			String contractAddress = wallet.getHdacAddress(false, 0);
//
//			String rAddress = wallet.getHdacAddress(false, 1);
//			String aAddress = wallet.getHdacAddress(true, 0);
//
//		    logger.debug("*** contractAddress : " + contractAddress);
//		    logger.debug("*** rAddress : " + rAddress);
//		    logger.debug("*** aAddress : " + aAddress);
//
////		    double pointNumber = Double.parseDouble(StringUtil.nvl(paramMap.get("pointNumber"), "0"));
//		    int pointNumber = 8;
//
//		    Map<String, Object> tokenparamMap = new HashMap<String, Object>();
//			tokenparamMap.put("address",	contractAddress);
//			tokenparamMap.put("tokenName",	paramMap.get("tokenName"));
//			tokenparamMap.put("amount",		paramMap.get("tokenCap"));
//			tokenparamMap.put("unit",		Math.pow(10, pointNumber * -1));
//
//			Map<String, Object> resultTokenMap = issueToken2(tokenparamMap); 
//
//			boolean success = (boolean)resultTokenMap.get("success");
//			if (success)
//			{
//				String tokenTxid = resultTokenMap.get("txid").toString();
//
//				paramMap.put("contractAddress",		contractAddress);
//				paramMap.put("tokenTxid",			tokenTxid);
//				paramMap.put("recordAddress",		rAddress);
//				paramMap.put("anchoringAddress",	aAddress);
//
//				paramMap.put("wallet",				wallet);
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return resultMap;
//	}
//
//	private HdacWallet getHdacWallet(List<String> seedWords, String passPhrase)
//	{
//		HdacCoreAddrParams params = new HdacCoreAddrParams(true);
//		return HdacWalletManager.generateNewWallet(seedWords, passPhrase, params);
//	}
//
//	private Map<String, Object> issueToken2(Map<String, Object> paramMap) throws JSONException
//	{
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		
///*
//		String result = null;
//		Object[] params;
//				
//		try
//		{
//			params = new Object[5];
//			params[0] = StringUtil.nvl(paramMap.get("address"));
//			params[1] = StringUtil.nvl(paramMap.get("tokenName"));
//			params[2] = Double.valueOf(StringUtil.nvl(paramMap.get("amount")));
//			params[3] = Float.parseFloat(StringUtil.nvl(paramMap.get("unit")));
//			if(paramMap.containsKey("native")) params[4] = Double.valueOf(StringUtil.nvl(paramMap.get("native")));
//			else params[4] = null;
//			
//			if(params[1].toString().indexOf('{') != -1) {
//				JSONObject typeName = new JSONObject(StringUtil.nvl(paramMap.get("tokenName")));
//				if(typeName.has("name"))  params[1] = typeName;
//			}
//					
//			result = HdacUtil.getDataFromRPC("issue", params, HdacUtil._PRIVATE_);
//		}
//		catch (JSONException e)
//		{
//			logger.error("messege", e);
//		}
//				
//		return result;
// */
//
//		Object[] params = new Object[5];
//		params[0] = StringUtil.nvl(paramMap.get("address"));
//		params[1] = StringUtil.nvl(paramMap.get("tokenName"));
//		params[2] = Double.valueOf(StringUtil.nvl(paramMap.get("amount")));
//		params[3] = Float.parseFloat(StringUtil.nvl(paramMap.get("unit")));
//		if (paramMap.containsKey("native"))
//			params[4] = Double.valueOf(StringUtil.nvl(paramMap.get("native")));
//		else
//			params[4] = null;
//		logger.debug("params[2] : " + params[2]);
//
//		logger.debug("params[3] : " + params[3]);
//		JSONObject result = HdacUtil.getDataObject("issue", params, HdacUtil._PRIVATE_);
//
//		logger.debug("asset result : " + result);
//
//		JSONObject resultTX = null;
//		boolean success = false;
//
//		if (result.get("error").equals(null))
//		{
//			resultTX = result.getJSONObject("result");
//			success = true;
//		}
//		else
//		{
//			resultTX = result.getJSONObject("error");
//		}
//
//		map.put("txid", resultTX);
//		map.put("success", success);
//
//		logger.debug("ASSET/ISSUE : " + map);
//		return map;
//	}

	public boolean insertTokenInfo(String data)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);

		Map<String, Object> paramMap = JsonUtil.fromJsonString(data);
//		paramMap.put("startDate",			"2019-03-13 11:24:15");
//		paramMap.put("endDate",				"2019-04-30 23:59:59");

		return insertTokenInfo(paramMap, resultMap);		
	}
	
	//--> for main
	public String getMainPageData() 
	{
		JSONObject result = new JSONObject();
		
		HdacWallet wallet = HdacUtil.getTokenHdacWallet();
		
		String contractAddress = wallet.getHdacAddress(false, 0);
		String recordAddress = wallet.getHdacAddress(false, 1);
		String anchoringAddress = wallet.getHdacAddress(true, 0);
		
		RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		double contract_balance = rpcDao.getAddressBalance(contractAddress, HdacUtil._PUBLIC_);
		double record_balance = rpcDao.getAddressBalance(recordAddress, HdacUtil._PUBLIC_);
		double anchoring_balance = rpcDao.getAddressBalance(anchoringAddress, HdacUtil._PUBLIC_);
		
		result.put("contractAddress", contractAddress);
		result.put("anchoringAddress", anchoringAddress);
		result.put("recordAddress", recordAddress);
		
		result.put("contractBalance", contract_balance);
		result.put("anchoringBalance", record_balance);
		result.put("recordBalance", anchoring_balance);
						
		return result.toString();
	}
	//<-- for main
}