/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.service.token;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.bitcoinj.core.ECKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hdac.common.BeanUtil;
import com.hdac.comm.HdacUtil;
import com.hdac.comm.JsonUtil;
import com.hdac.comm.StringUtil;
import com.hdac.common.SqlMapConfig;
import com.hdac.common.SqlMapConfig2;
import com.hdac.contract.HdacTokenIssue;
import com.hdac.dao.rpc.RpcDao;
import com.hdac.dao.rpc.RpcDaoImpl;
import com.hdac.dao.token.TokenDao;
import com.hdac.dao.token.TokenDaoImpl;
import com.hdac.property.ServerConfig;
import com.hdac.service.rpc.RpcService;
import com.hdac.service.rpc.RpcServiceImpl;
import com.hdacSdk.hdacWallet.HdacTransaction;
import com.hdacSdk.hdacWallet.HdacWallet;

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
/*		
		//Map<String, Object> paramMap = new HashMap<String, Object>();
		//if (getTokenList(paramMap).size() > 0)
		{
			//start web socket;
			if (HdacUtil._PUBLIC_.get("webSocketManager") == null)
				HdacUtil._PUBLIC_.put("webSocketManager", HdacUtil.getWebSocket(HdacUtil._PUBLIC_));
			if (HdacUtil._PRIVATE_.get("webSocketManager") == null)
				HdacUtil._PRIVATE_.put("webSocketManager", HdacUtil.getWebSocket(HdacUtil._PRIVATE_));
		}
*/
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
		SqlSession sqlSession = SqlMapConfig2.getSqlSession();

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);

		try
		{
			//hyyang change make contract
			HdacTokenIssue main = new HdacTokenIssue();
			main.init(sqlSession);

			HdacWallet wallet = getTokenHdacWallet();
			String contractAddress = wallet.getHdacAddress(false, 0);
			String binaryAddress = wallet.getHdacAddress(false, 1);
			String anchoringAddress = wallet.getHdacAddress(true, 0);

			String result = main.issueToken(paramMap, contractAddress);
		    logger.debug(" issueToken result : " + result);
		    

		    
			if (result == null)
				return resultMap;
			else
			{
				paramMap.put("contractAddress",		contractAddress);
				paramMap.put("tokenTxid",			result);
				paramMap.put("recordAddress",		binaryAddress);
				paramMap.put("anchoringAddress",	anchoringAddress);
			}
			
			copyContractLib(paramMap);
			
			result = main.registTokenInfo(wallet, paramMap, contractAddress);		// make tx to main chain
			logger.debug(" registTokenInfo result : " + result);
		
			if (result == null)
				return resultMap;
			else
			{
				paramMap.put("contractTxid", result);
			}

			boolean bSuccess = main.insertTokenInfo(paramMap);
			logger.debug(" insertTokenInfo bSuccess : " + bSuccess);

			if (bSuccess)
				resultMap.put("success", bSuccess);
			
		    
		}
		finally
		{
			sqlSession.close();
		}
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


	public boolean insertTokenInfo(String data)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);

		Map<String, Object> paramMap = JsonUtil.fromJsonString(data);

		return insertTokenInfo(paramMap, resultMap);		
	}

	//--> for main
	public String getMainPageData() 
	{
		JSONObject result = new JSONObject();
		
		HdacWallet wallet = getTokenHdacWallet();
		
		String contractAddress = wallet.getHdacAddress(false, 0);
		String recordAddress = wallet.getHdacAddress(false, 1);
		String anchoringAddress = wallet.getHdacAddress(true, 0);

		ServerConfig config = ServerConfig.getInstance();
		RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		BigDecimal contract_balance = rpcDao.getAddressBalance(contractAddress, config.getMainChainInfo());
		BigDecimal record_balance = rpcDao.getAddressBalance(recordAddress, config.getMainChainInfo());
		BigDecimal anchoring_balance = rpcDao.getAddressBalance(anchoringAddress, config.getMainChainInfo());

		result.put("contractAddress", contractAddress);
		result.put("anchoringAddress", anchoringAddress);
		result.put("recordAddress", recordAddress);

		result.put("contractBalance", contract_balance);
		result.put("anchoringBalance", anchoring_balance);
		result.put("recordBalance", record_balance);
		
		RpcServiceImpl service = (RpcServiceImpl)BeanUtil.getBean(RpcServiceImpl.class);
		
		try
		{
			String mainInfo = service.getInfo(config.getMainChainInfo());
			String check = new JSONObject(mainInfo).get("result").toString();
			if(check.equals("null"))
			{
				JSONObject mainErrObj = new JSONObject(mainInfo).getJSONObject("error");
				result.put("mainState", mainErrObj.get("message").toString());
			}
			else
			{
				JSONObject mainObj = new JSONObject(mainInfo).getJSONObject("result");
				result.put("mainName", mainObj.get("chainname").toString());
				result.put("mainState", "Running");
				result.put("mainHeight", mainObj.get("blocks").toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result.put("mainName", "");
			result.put("mainState", "OFF");
			result.put("mainHeight", "");
		}
		
		try
		{
			String sideInfo = service.getInfo(config.getSideChainInfo());
			String check = new JSONObject(sideInfo).get("result").toString();
			if(check.equals("null"))
			{
				JSONObject sideErrObj = new JSONObject(sideInfo).getJSONObject("error");
				result.put("sideState", sideErrObj.get("message").toString());
			}
			else
			{
				JSONObject sideObj = new JSONObject(sideInfo).getJSONObject("result");
				result.put("sideName", sideObj.get("chainname").toString());
				result.put("sideState", "Running");
				result.put("sideHeight", sideObj.get("blocks").toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result.put("sideName", "");
			result.put("sideState", "OFF");
			result.put("sideHeight", "");
		}
		
		return result.toString();
	}
	//<-- for main

	private HdacWallet getTokenHdacWallet()
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();

		try
		{
			TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
			tokenDao.setSqlSession(sqlSession);

			Map<String, Object> tokenparamMap = new HashMap<String, Object>();

			List<String> seedWords = tokenDao.getSeed(tokenparamMap);
			List<String> seed = HdacUtil.decodeSeed(seedWords, HdacUtil.getKey());

			return HdacUtil.getHdacWallet(seed, null);
		}
		finally
		{
			sqlSession.close();
		} 
	}
	
	private void copyContractLib(Map<String, Object> paramMap)
	{
		MultipartFile binaryFile = (MultipartFile) paramMap.get("binary");
		String path = "/opt/shareLib/";
		//String path = "d:/uploadtest/";
		
		logger.debug("copyContractLib file : " + binaryFile.toString());				
		try 
		{
			String contractLibHash = getContractHash(binaryFile);
			
			File file = new File(path + binaryFile.getOriginalFilename());
			binaryFile.transferTo(file);
			logger.debug("copyContractLib fileName : " + binaryFile.getOriginalFilename() + " path : " + path);
						
			paramMap.put("binaryPath", path);
			paramMap.put("binaryHash", contractLibHash);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String getContractHash(MultipartFile file) 
	{
		String result = "";
				
		byte[] data;
		try 
		{
			data = file.getBytes();
			SHA256Digest md = new SHA256Digest();
			md.update(data, 0, data.length);
			
			byte[] hashed = new byte[md.getDigestSize()];
			md.doFinal(hashed, 0);
			
			StringBuilder sb = new StringBuilder(); 
			for (byte b : hashed)
			{
				sb.append(String.format("%02X", b & 0xff)); 
			}
			result = sb.toString();
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}

		return result;
	}
	
	@Override
	public Map<String, Object> updateLib(Map<String, Object> paramMap)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramTx = new HashMap<String, Object>();
		
		//--> get record address
		HdacWallet wallet = getTokenHdacWallet();
		String binaryAddress = wallet.getHdacAddress(false, 1);
		
		//--> get utxo of record address
		paramMap.put("addresses", binaryAddress);
		ServerConfig config = ServerConfig.getInstance();
		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		String utxo = service.getUtxos(paramMap, config.getServerType("public"));
		JSONArray utxoArray = new JSONArray(utxo);
		
		//--> get file hash and create transactions with hash in the data area
		MultipartFile file = (MultipartFile) paramMap.get("binary");
		String hash = getContractHash(file);
		paramTx.put("contractHash", hash);
		String result = makeRawTransactionForRecord(wallet, utxoArray, paramTx, binaryAddress);
				
		//--> send transaction	
		Object[] params = new Object[1];
		params[0] = result;

		String strSendResult = HdacUtil.getDataFromRPC("sendrawtransaction", params, config.getServerType("public"));
		JSONObject resultObj = new JSONObject(strSendResult);
		
		//--> make return data		
		if(resultObj.get("error").toString().equals("null")) 
		{
			resultMap.put("result", "success");
			resultMap.put("recordTxid", resultObj.get("result").toString());
			
			copyContractLib(paramMap);
			resultMap.put("libHash", String.valueOf(paramMap.get("binaryHash")));
		}
		else 
		{
			resultMap.put("result", "Fail");
			resultMap.put("recordTxid", new JSONObject(resultObj.get("error").toString()).get("message").toString());
			resultMap.put("libHash","");
		}
		
		return resultMap;
	}
	
	public String getRecordPageData() 
	{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		HdacWallet wallet = getTokenHdacWallet();
		String binaryAddress = wallet.getHdacAddress(false, 1);
		
		paramMap.put("addresses", binaryAddress);
		paramMap.put("start", -1);
		paramMap.put("end", -1);
		paramMap.put("from", 0);
		paramMap.put("count", 50);
		
		ServerConfig config = ServerConfig.getInstance();
		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		String resultStr = service.getTxs(paramMap, config.getServerType("public"));
		
		JSONObject result = new JSONObject(resultStr);
		JSONArray txArray = result.getJSONArray("items");
		
		JSONArray returnArray = new JSONArray();
		String time = "";
		String date = "";
		String dataHex = "";
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		
		for(int i = 0; i < txArray.length(); i++)
		{
			JSONObject obj = new JSONObject();
			
			time = txArray.getJSONObject(i).get("time").toString() + "000";
			long timeL = Long.parseLong(time);
			date = (String)formatter.format(new Timestamp(timeL));
			
			dataHex = txArray.getJSONObject(i).get("data").toString();
			if(dataHex.length() > 2)
			{
				dataHex = dataHex.substring(2, dataHex.length()-2);
				obj.put("data", StringUtil.hexToString(dataHex));
			}
									
			obj.put("time", date);
			obj.put("txid", txArray.getJSONObject(i).get("txid").toString());
			
			returnArray.put(i, obj);
		}
						
		System.out.println("RESULT2 : " + returnArray.toString());
				
		return returnArray.toString();
	}
		
	private String makeRawTransactionForRecord(HdacWallet wallet, JSONArray data, Map<String, Object> paramMap, String address) 
	{
		HdacTransaction transaction = new HdacTransaction(wallet.getNetworkParams());
		String contractString = "";
		if (paramMap.size() > 0) 
		{
			contractString = JsonUtil.toJsonString(paramMap).toString();
		}
		BigDecimal balance = BigDecimal.ZERO;
		try 
		{
			int len = data.length();
			for (int i = 0; i < len; i++) 
			{
				JSONObject utxo = data.getJSONObject(i);
				balance = balance.add(utxo.getBigDecimal("amount"));

				transaction.addInput(utxo);
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			return null;
		}
		BigInteger lBalance = balance.multiply(BigDecimal.TEN.pow(8)).toBigInteger();
		BigInteger fee = BigInteger.valueOf(2L).multiply(BigInteger.TEN.pow(6))
				.add(BigInteger.valueOf(contractString.length()).multiply(BigInteger.TEN.pow(3)));
		BigInteger remain = lBalance.subtract(fee);
		if (remain.compareTo(BigInteger.ZERO) < 0) 
		{
			return null;
		}
		transaction.addOutput(address, remain.longValue());
		transaction.addOpReturnOutput(JsonUtil.toJsonString(paramMap).toString(), "UTF-8");
		try 
		{
			int len = data.length();
			for (int i = 0; i < len; i++) 
			{
				JSONObject utxo = data.getJSONObject(i);
				ECKey sign = wallet.getHdacSigKey(utxo.getString("address"));
				if (sign != null) 
				{
					transaction.setSignedInput(i, utxo, sign);
				}
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return transaction.getTxBuilder().build().toHex();
	}
	
	
}