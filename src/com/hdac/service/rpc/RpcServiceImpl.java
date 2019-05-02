/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.service.rpc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hdac.common.BeanUtil;
import com.hdac.common.HdacUtil;
import com.hdac.common.JsonUtil;
import com.hdac.common.ServerConfig;
import com.hdac.common.StringUtil;
import com.hdac.dao.rpc.RpcDao;
import com.hdac.dao.rpc.RpcDaoImpl;
import com.hdac.service.token.TokenService;
import com.hdac.service.token.TokenServiceImpl;

/**
 * Remote Procedure Call Service Implementation
 * 
 * @version 0.8
 * 
 * @see     java.util.Map
 * @see     java.text.DecimalFormat
 * @see     java.util.ArrayList
 * @see     java.util.HashMap
 * @see     java.util.List
 * @see     java.util.Map
 * @see     org.json.JSONArray
 * @see     org.json.JSONException
 * @see     org.json.JSONObject
 * @see     org.slf4j.Logger
 * @see     org.slf4j.LoggerFactory
 * @see     org.spongycastle.crypto.digests.SHA256Digest
 * @see     org.springframework.stereotype.Service
 * @see     org.springframework.web.multipart.MultipartFile
 */
@Service
public class RpcServiceImpl implements RpcService
{
	private static Logger logger = LoggerFactory.getLogger(RpcServiceImpl.class);

	@Override
	public String getAddress(Map<String, Object> paramMap, ServerConfig config)
	{
		JSONObject resultObj = new JSONObject();

		try
		{
			RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);

			String address = StringUtil.nvl(paramMap.get("address"));

			double balance = rpcDao.getAddressBalance(address, config);
			List<String> txList = rpcDao.getAddressList(paramMap, config);

			resultObj.put("addrStr",		address);
			resultObj.put("balance",		balance);
			resultObj.put("balanceSat",		(long)(balance * Math.pow(10, 8)));
			resultObj.put("txApperances",	txList.size());
			resultObj.put("transactions",	txList);

			logger.debug("ADDR/{ADDR} = " + resultObj.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return resultObj.toString();
	}

	@Override
	public String getUtxos(Map<String, Object> paramMap, ServerConfig config)
	{
		RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		String resultStr = "";

		try
		{
			List<JSONObject> rpcResult = rpcdao.getUtxosNew(paramMap, config);
			resultStr = rpcResult.toString();
			logger.debug("ADDRS/{ADDRS}/UTXO = " + resultStr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultStr;
	}

	@Override
	public String getTxs(Map<String, Object> paramMap, ServerConfig config)
	{
		RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		String resultStr = "";

		try
		{
			JSONObject rpcResult = rpcdao.getTxsNew(paramMap, config);
			resultStr = rpcResult.toString();
			logger.debug(resultStr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultStr;
	}

	@Override
	public Map<String, Object> sendTx(Map<String, Object> paramMap, ServerConfig config)
	{
		Map<String, Object> map = new HashMap<String, Object>();

		try
		{
			Object[] params = new Object[1];
			params[0] = paramMap.get("rawtx");

			String strSendResult = HdacUtil.getDataFromRPC("sendrawtransaction", params, config);
			JSONObject objSendResult = new JSONObject(strSendResult);

			String resultTX = "";
			boolean success = false;
			if (objSendResult.get("error").equals(null))
			{
				resultTX = objSendResult.getString("result");
				success = true;
			}
			else
			{
				resultTX = objSendResult.getJSONObject("error").toString();
			}

			map.put("txid", resultTX);
			map.put("success", success);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public String getAssetAddressBalance(Map<String, Object> paramMap)
	{
		double balance = 0;
		try
		{
			RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
			balance = rpcdao.getMultiBalance(paramMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		DecimalFormat format = new DecimalFormat(".########");
		return format.format(Math.round(balance * Math.pow(10, 8)) / Math.pow(10, 8));
	}

	@Override
	public String getAssetAddress(Map<String, Object> paramMap)
	{
		JSONObject resultObj = new JSONObject();

		try
		{
			RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);

			JSONArray array = rpcDao.getMultiBalances(paramMap);
			setAssets(resultObj, array);

			//address info from rpc
			List<String> txList = rpcDao.getAssetTxList(paramMap);
			long totalCount = txList.size();

			double balance = resultObj.getDouble("balance");
			long balanceSat = (long)(balance * Math.pow(10, 8));

			resultObj.put("addrStr",		paramMap.get("address"));
			resultObj.put("transactions",	txList);
			resultObj.put("txApperances",	totalCount);
			resultObj.put("balance",		balance);
			resultObj.put("balanceSat",		balanceSat);
		}
		catch (Exception e)
		{
			logger.debug("result : " + resultObj);
			e.printStackTrace();
		}

		logger.debug("ADDR/{ADDR} = " + resultObj.toString());
		return resultObj.toString();
	}

	private void setAssets(JSONObject resultObj, JSONArray array) throws Exception
	{
		JSONArray assets = new JSONArray();

		int length = array.length();
		for (int i = 0; i < length; i++)
		{
			JSONObject obj = array.getJSONObject(i);

			if (obj.has("name"))
			{
				assets.put(obj);
			}
			else if (obj.has("assetref") && ("".equals(obj.getString("assetref"))))
			{
				double balance = obj.getDouble("qty");
				resultObj.put("balance", balance);
			}
		}

		if (assets.length() > 0)
		{
			resultObj.put("assets", assets);
		}
	}

	@Override
	public String getAssetUtxos(Map<String, Object> paramMap)
	{
		RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		String resultStr = "";

		try
		{
			List<JSONObject> rpcResult = rpcDao.getAssetUtxos(paramMap);
			resultStr = new JSONArray(rpcResult).toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultStr;
	}

	@Override
	public String getAssetTxs(Map<String, Object> paramMap)
	{
		RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		String resultStr = "";

		try
		{
			List<JSONObject> rpcResult = rpcdao.getAssetTxs(paramMap);
			resultStr = rpcResult.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultStr;
	}

	private String getContractHash(MultipartFile file) 
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

	public String verifyTx(Map<String, Object> paramMap) throws JSONException
	{
		JSONObject resultObj = new JSONObject();
		String rpcResult = "";
		
		String checkResult = "";
		String blockHash = "";
		String height = "";
				
		Object[] params = new Object[2];

		//get block hash from txid
		params[0] = paramMap.get("txid");
		params[1] = 1;

		rpcResult = HdacUtil.getDataFromRPC("getrawtransaction", params, HdacUtil._PRIVATE_);
		checkResult = new JSONObject(rpcResult).get("result").toString();
		if(checkResult.equals("null")) {
			String message = new JSONObject(rpcResult).getJSONObject("error").getString("message"); 
			resultObj.put("matchingResult", "Invalid");
			resultObj.put("message", message);
			return resultObj.toString();
		}else {
			blockHash = new JSONObject(rpcResult).getJSONObject("result").getString("blockhash");
		}
				
		//get block height from block hash
		params[0] = blockHash;
				
		rpcResult = HdacUtil.getDataFromRPC("getblock", params, HdacUtil._PRIVATE_);
		checkResult = new JSONObject(rpcResult).get("result").toString();
		if(checkResult.equals("null")) {
			String message = new JSONObject(rpcResult).getJSONObject("error").getString("message"); 
			resultObj.put("matchingResult", "Invalid");
			resultObj.put("message", message);
			return resultObj.toString();
		}else {
			height = new JSONObject(rpcResult).getJSONObject("result").get("height").toString();
		}
		
		//get tx list of anchoring to address
		Map<String, Object> tempParam = new HashMap<String, Object>();  
		tempParam.put("blockCnt", height);
		TokenService tService = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		Map<String, Object> addressMap = tService.getVerifyAddress(tempParam);
		
		if(addressMap == null) {
			resultObj.put("matchingResult", "Invalid");
			resultObj.put("message", "Block " + height + " is not anchored");
			return resultObj.toString();	
		}
		String toAddress = addressMap.get("to_address").toString();
		String addressIndex = addressMap.get("address_index").toString();
		String anchorChangeSize = addressMap.get("change_size").toString();
				
		//anchoring size
		int size = Integer.parseInt(addressMap.get("anchor_size").toString());
				
		//Setting the range included block height for calculating the Merkle root 
		int startIndex = (Integer.parseInt(height) / size);
		int startBlockCnt = size * startIndex;  
		int endBlockCnt = startBlockCnt + size - 1;
		
		//making merkle tree list
		List<String> merkleList = new ArrayList<String>();
		for(int i = startBlockCnt; i < endBlockCnt + 1; i++) {
			params[0] = String.valueOf(i);
			rpcResult = HdacUtil.getDataFromRPC("getblock", params, HdacUtil._PRIVATE_);
			String merkle = new JSONObject(rpcResult).getJSONObject("result").getString("merkleroot");
			merkleList.add(merkle);
		}
		
		//calculate the hash of merkle trees
		List<String> finalMerkle = HdacUtil.makeHash(merkleList);
		String hash = finalMerkle.get(0).toString();
				
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("address", toAddress);
		map.put("skip", "0");
		map.put("limit", "99999");
		map.put("count", anchorChangeSize);
		
		RpcDao rpcDao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		List<String> txList = rpcDao.getAddressList(map, HdacUtil._PUBLIC_);
		logger.debug("VERIFY : TX LIST        : " + txList.size());
		
		//get data of transaction
		startIndex = startIndex - ((Integer.parseInt(addressIndex) - 1) * Integer.parseInt(anchorChangeSize));
		logger.debug("VERIFY : MATCHING INDEX : " + startIndex);
		String matchingTxid = txList.get(startIndex);
		
		params[0] = matchingTxid;
		rpcResult = HdacUtil.getDataFromRPC("getrawtransaction", params, HdacUtil._PUBLIC_);
		String txDataTemp = new JSONObject(rpcResult).getJSONObject("result").get("data").toString();
		String txData = txDataTemp.substring(2, txDataTemp.length()-2);
		
		//compare merkle root with data
		boolean matching = false;
		String byteMerkleTree = "";
		try {
			byte[] merkleTreeTemp = hash.getBytes("UTF-8");
			byte[] reverse = HdacUtil.reverse(merkleTreeTemp);
			StringBuilder sb = new StringBuilder();
	 		for (byte bb : reverse)
			{
	 			sb.insert(0, String.format("%02X", bb & 0xFF)); 
			}
	 		byteMerkleTree = sb.toString();
	 		matching = byteMerkleTree.equals(txData.substring(0, 128));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String txDataConv = "";
		for (int i = 0; i < 128; i += 2) {
			String c = txData.substring(i, i+2);
			txDataConv += (char)(Integer.parseInt(c, 16));
	    }
		
		//convert token Qty info and conrtract hash
		String tokenQty = "";
		String libHash = "";
		String temp = "";
		for (int i = 130; i < txData.length(); i += 2) {
			String c = txData.substring(i, i+2);
			temp += (char)(Integer.parseInt(c, 16));
	    }
		
		int divide = temp.indexOf('|');
		if(divide > 0) 
		{
			tokenQty = temp.substring(0, divide);
			libHash = temp.substring(divide+1, temp.length());	
		}
		
		//get hash of upload contract lib file
		String fileName = "";
		String uploadFileHash = "";
		if(paramMap.get("file") != null)
		{
			MultipartFile contractFile = (MultipartFile) paramMap.get("file");
			fileName = contractFile.getOriginalFilename();
			uploadFileHash = getContractHash(contractFile);
		}
										
		//make return data
		resultObj.put("sideBlockHeight", height);
		resultObj.put("mainData", txDataConv);
		resultObj.put("sideHashData", hash);
		resultObj.put("publicAddress", toAddress);
		resultObj.put("matchingPublicTx", matchingTxid);
		resultObj.put("tokenQty", tokenQty);
		resultObj.put("libHash", libHash);
		if(matching) resultObj.put("matchingResult", "Valid");
		else resultObj.put("matchingResult", "Invalid");
				
		logger.debug("VERIFY : TO ADDRESS        : " + addressMap.toString());
		logger.debug("VERIFY : SIDE BLOCK RANGE  : " + startBlockCnt + " - " + endBlockCnt);
		logger.debug("VERIFY : PUBLIC TX DATA    : " + txData);
		logger.debug("VERIFY : SIDE HASHING DATA : " + byteMerkleTree);
		logger.debug("VERIFY : VERIFY TX RESULT  : " + matching);
		
		logger.debug("VERIFY : TOKEN QTY         : " + tokenQty);
		
		logger.debug("VERIFY : PUBLIC FILE HASH  : " + libHash);
		logger.debug("VERIFY : UPLOAD FILE NAME  : " + fileName);
		logger.debug("VERIFY : UPLOAD FILE HASH  : " + uploadFileHash);
		
		return resultObj.toString();
	}

	public Map<String, Object> assetIssue(Map<String, Object> paramMap) throws JSONException
	{
		Map<String, Object> map = new HashMap<String, Object>();

		RpcDaoImpl rpcDao = (RpcDaoImpl)BeanUtil.getBean(RpcDaoImpl.class);
		String result = "";

		result = rpcDao.assetIssue(paramMap);
		JSONObject objSendResult = new JSONObject(result);
		logger.debug("strSendResult : " + result);
		logger.debug("asset objSendResult : " + objSendResult);
		
		
		String resultTX = "";
		boolean success = false;

		if (objSendResult.get("error").equals(null))
		{
			resultTX = objSendResult.getString("result");
			success = true;
		}
		else
		{
			resultTX = objSendResult.getJSONObject("error").toString();
		}

		map.put("txid", resultTX);
		map.put("success", success);

		logger.debug("ASSET/ISSUE : " + JsonUtil.toJsonString(map).toString());
		return map;
	}

	@Override
	public String getListAssets(Map<String, Object> paramMap)
	{
		String asset = StringUtil.nvl(paramMap.get("asset"));

		Object[] params = new Object[1];
		if ("".equals(asset) == false)
			params[0] = asset;

		return HdacUtil.getDataFromRPC("listassets", params, HdacUtil._PRIVATE_);
	}
	
	public String getInfo(ServerConfig config)
	{
		String result = HdacUtil.getDataFromRPC("getinfo", new Object[0] , config);
		return result;
	}
}