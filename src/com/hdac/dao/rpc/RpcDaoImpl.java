/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.dao.rpc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdac.common.Constants;
import com.hdac.common.HdacUtil;
import com.hdac.common.ServerConfig;
import com.hdac.common.StringUtil;
import com.hdac.comparator.JsonComparator;

/**
 * RPC(Remote Procedure Call) Data Access Object Implementation
 * 
 * @version 0.8
 * 
 * @see		java.util.List
 * @see     java.util.Map
 * @see     org.json.JSONArray
 * @see     org.json.JSONObject
 */
public class RpcDaoImpl implements RpcDao
{
	private static Logger logger = LoggerFactory.getLogger(RpcDaoImpl.class);

	@Override
	public double getMultiBalance(Map<String, Object> paramMap)
	{
		double balance = 0;

		try
		{
			JSONArray array = getMultiBalances(paramMap);
			int length = array.length();
			for (int i = 0; i < length; i++)
			{
				if (array.getJSONObject(i).has("name") 
						&& array.getJSONObject(i).getString("name").equals(paramMap.get("asset").toString()))
					balance += array.getJSONObject(i).getDouble("qty");
			}
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return balance;
	}

	@Override
	public JSONArray getMultiBalances(Map<String, Object> paramMap)
	{
		try
		{
//			String[] addresses = StringUtil.nvl(paramMap.get("address")).split(",");
			String[] addresses = getFilterListAddresses(StringUtil.nvl(paramMap.get("address")).split(","));
			
			if (addresses == null)
				return new JSONArray();

			Object[] params = new Object[3];
			params[0] = addresses;
			params[1] = StringUtil.nvl(paramMap.get("asset"));
			params[2] = 0;

			ServerConfig config = HdacUtil._PRIVATE_;

			JSONObject objBalance = HdacUtil.getDataObject("getmultibalances", params, config);
			
			String filter = (addresses.length>1 ? Constants.strTOTAL : addresses[0]);
			
			if (objBalance.has(filter))
				return objBalance.getJSONArray(filter);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return new JSONArray();
	}

	public String[] getFilterListAddresses(String[] addrs)
	{
		String[] addresses = null;
		try
		{
			String result = HdacUtil.getDataFromRPC("listaddresses", new Object[0], HdacUtil._PRIVATE_);
			logger.debug("result : " + result);
			logger.debug("result len : " + result.length());

			if (result != null && result.length() > 35) 
			{
				List<String> addrList = new ArrayList<String>();

				for(String address : addrs)
				{
					if (result.contains(address))
						addrList.add(address);
				}

				int size = addrList.size();
				logger.debug("size : " + size);
				
				if (size > 0)
				{
					addresses = new String[size];
					for (int i=0; i < size; i++)
					{
						logger.debug("i : "+ i);
						addresses[i] = addrList.get(i);
					}
					
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return addresses;
	}
	
	@Override
	public List<String> getAssetTxList(Map<String, Object> paramMap) 
	{
		List<String> totalTxList = new ArrayList<String>();		//listaddresstransactions "address" ( count skip verbose )

		try
		{
			String address	= StringUtil.nvl(paramMap.get("address"));
			int count		= Integer.parseInt(StringUtil.nvl(paramMap.get("count"), "50"));
			int skip		= Integer.parseInt(StringUtil.nvl(paramMap.get("skip"), "0"));

			Object[] params = new Object[1];
			params[0] = address;

			JSONObject options = new JSONObject();
			options.put("count",	count);
			options.put("skip",		skip);
			options.put("verbose",	false);

			JSONArray blockTxList = HdacUtil.getDataArray("listaddresstransactions", params, options, HdacUtil._PRIVATE_);

			// add tx list to total tx list
			for (int i = blockTxList.length() - 1; i >= 0; i--)
			{
				JSONObject obj = blockTxList.getJSONObject(i);
				totalTxList.add(obj.getString("txid"));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return totalTxList;
	}

	@Override
	public List<JSONObject> getAssetUtxos(Map<String, Object> paramMap)
	{
		List<JSONObject> listMap = new ArrayList<JSONObject>();

		try
		{
			Object[] params = new Object[3];
			params[0] = 0;
			params[1] = 999999999;
			params[2] = StringUtil.nvl(paramMap.get("addresses")).split(",");

			JSONArray objUtxoBlockArray = HdacUtil.getDataArray("listunspent", params, HdacUtil._PRIVATE_);
			logger.debug("objUtxoBlockArray " + objUtxoBlockArray);

			String asset = StringUtil.nvl(paramMap.get("asset"));
			int length = objUtxoBlockArray.length();
			for (int i = 0; i < length; i++)
			{
				JSONArray assetArray = objUtxoBlockArray.getJSONObject(i).getJSONArray("assets");

				if (assetArray.length() <= 0)
					continue;

				if (asset.equals(assetArray.getJSONObject(0).getString("name")))
				{
					JSONObject obj = objUtxoBlockArray.getJSONObject(i);

					JSONObject map = new JSONObject();
					map.put("unspent_hash",		obj.get("txid"));
					map.put("address",			obj.get("address"));
					map.put("scriptPubKey",		obj.get("scriptPubKey"));
					map.put("amount",			obj.getDouble("amount"));
					map.put("vout",				obj.get("vout"));
					map.put("confirmations",	obj.get("confirmations"));
					map.put("satoshis",			(long)(obj.getDouble("amount") * Math.pow(10, 8)));
					map.put("txid",				obj.get("txid"));
					map.put("assets",			obj.getJSONArray("assets"));

					listMap.add(map);
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return listMap;
	}

	@Override
	public List<JSONObject> getAssetTxs(Map<String, Object> paramMap)
	{
		List<JSONObject> list = new ArrayList<JSONObject>();

		try
		{
			List<String> txids = new ArrayList<String>();

			ServerConfig config = HdacUtil._PRIVATE_;
			long blockCount = getBlockCount(config);

			String[] addrs	= StringUtil.nvl(paramMap.get("addresses")).split(",");
			int count		= Integer.parseInt(StringUtil.nvl(paramMap.get("count"), "50"));
			int skip		= Integer.parseInt(StringUtil.nvl(paramMap.get("skip"), "0"));

			//make txs list of one address of param because of RPC getrawtransaction param is not multi
			for (String addr : addrs)
			{
				paramMap.put("address", addr);

				List<String> txlist = getAssetTxList(paramMap);

				int size = txlist.size();
				for (int i = 0; i < size; i++)
				{
					String txid = txlist.get(i);
					if (txids.contains(txid))
						continue;

					txids.add(txid);
					list.add(getTxInfo(txid, blockCount, config));
				}
			}

			//total txs list arrange by time field
			Collections.sort(list, new JsonComparator());

			// make total tx list size to size of parameter
			list = getList(list, skip, count);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<JSONObject> getUtxosNew(Map<String, Object> paramMap, ServerConfig config)
	{
		List<JSONObject> list = new ArrayList<JSONObject>();

		try
		{
			long blockCount = getBlockCount(config);

			String[] addresses = StringUtil.nvl(paramMap.get("addresses")).split(",");

			JSONObject params = new JSONObject();
			params.put("addresses", new JSONArray(Arrays.asList(addresses)));

			JSONArray objMempoolArray = HdacUtil.getDataArray("getaddressmempool", params, config);

			List<JSONObject> vinList = new ArrayList<JSONObject>();
			separateMempoolList(vinList, list, objMempoolArray);

			JSONArray objUtxoBlockArray = HdacUtil.getDataArray("getaddressutxos", params, config);
			int blockLength = objUtxoBlockArray.length();
			for (int i = 0; i < blockLength; i++)
			{
				JSONObject obj = objUtxoBlockArray.getJSONObject(i);

				if (TxContains(list, obj) == false)
					list.add(obj);
			}

			for (int i = list.size() - 1; i >= 0; i--)
			{
				JSONObject obj = list.get(i);

				if (TxContainsPrev(vinList, obj))
				{
					list.remove(i);
					continue;
				}

				if (obj.has("script"))	// rpc
				{
					String txid = obj.getString("txid");
					long satoshis = obj.getLong("satoshis");

					JSONObject newObj = new JSONObject();
					newObj.put("unspent_hash",		txid);
					newObj.put("address",			obj.get("address"));
					newObj.put("scriptPubKey",		obj.get("script"));
					newObj.put("amount",			satoshis * Math.pow(10, -8));
					newObj.put("vout",				obj.get("outputIndex"));
					newObj.put("confirmations",		blockCount - obj.getLong("height") + 1);
					newObj.put("satoshis",			satoshis);
					newObj.put("txid",				txid);

					list.set(i, newObj);
				}
				else	// mempool
				{
					String txid = obj.getString("txid");
					int index = obj.getInt("index");
					long satoshis = obj.getLong("satoshis");
					String scriptPubKey = getScriptPubKey(txid, index, config);

					JSONObject newObj = new JSONObject();
					newObj.put("unspent_hash",		txid);
					newObj.put("address",			obj.get("address"));
					newObj.put("scriptPubKey",		scriptPubKey);
					newObj.put("amount",			satoshis * Math.pow(10, -8));
					newObj.put("vout",				index);
					newObj.put("confirmations",		0);
					newObj.put("satoshis",			satoshis);
					newObj.put("txid",				txid);

					list.set(i, newObj);
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public JSONObject getTxsNew(Map<String, Object> paramMap, ServerConfig config)
	{
		JSONObject obj = new JSONObject();

		try
		{
			List<JSONObject> list = new ArrayList<JSONObject>();

			long blockCount = getBlockCount(config);

			String[] addrs	= StringUtil.nvl(paramMap.get("addresses")).split(",");
			int start		= Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "-1"));
			int end			= Integer.parseInt(StringUtil.nvl(paramMap.get("end"), "-1"));
			int from		= Integer.parseInt(StringUtil.nvl(paramMap.get("from"), "0"));
			int count		= Integer.parseInt(StringUtil.nvl(paramMap.get("count"), "50"));

			JSONObject params = new JSONObject();
			params.put("addresses", new JSONArray(Arrays.asList(addrs)));

			JSONArray mempoolArray = HdacUtil.getDataArray("getaddressmempool", params, config);
			int mempoolLength = mempoolArray.length();

//			params.put("from", Math.max(from - mempoolLength, 0));
//			params.put("count", from + count);

			if (start > -1)
				params.put("start", start);
			if (end > -1)
				params.put("end", end);

			JSONArray blockArray = HdacUtil.getDataArray("getaddresstxids", params, config);
			int blockLength = blockArray.length();
			logger.debug("array : " + blockArray);

			List<String> listTx = new ArrayList<String>();
			//for (int i = 0; i < blockLength; i++)
			for (int i = blockLength; i > 0; i--)
			{
				listTx.add(blockArray.getString(i - 1));
			}

			for (int i = mempoolLength; i > 0; i--)
			{
				String txid = mempoolArray.getJSONObject(i - 1).getString("txid");

				if (listTx.contains(txid))
					continue;

				listTx.add(0, txid);
//				list.add(getTxInfo(txid, blockCount, config));
			}
/*
			for (int i = 0; i < blockLength; i++)
			{
				list.add(getTxInfo(listTx.get(i), blockCount, config));
			}

			//total txs list arrange by time field
			Collections.sort(list, new JsonComparator());
*/
			int totalSize = listTx.size();

			// make total tx list size to size of parameter
			listTx = getList(listTx, from, count);

			int listSize = listTx.size();
			for (int i = 0; i < listSize; i++)
			{
				list.add(getTxInfo(listTx.get(i), blockCount, config));
			}

			obj.put("totalItems", totalSize);
			obj.put("from", from);
			obj.put("to", count);
			obj.put("items", new JSONArray(list));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public JSONObject getBlock(String blockHash, ServerConfig config)
	{
		JSONObject obj = new JSONObject();
		
		try
		{	//get block height from block hash
			Object[] params = new Object[2];
			params[0] = blockHash;
			params[1] = 4;

			obj = HdacUtil.getDataObject("getblock", params, config);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return obj;
	}
	
	private long getBlockCount(ServerConfig config)
	{
		try
		{
			String strBlockCount = HdacUtil.getDataFromRPC("getblockcount", new String[0], config);
			JSONObject objBlockCount = new JSONObject(strBlockCount);
			long blockHeight = objBlockCount.getLong("result");

			return blockHeight;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	private void setValueInValue(JSONObject txInfo, JSONArray vinArray, double valueOut, ServerConfig config) throws JSONException
	{	// make some new field valuein, fees, value and so on
		int vinLength = vinArray.length();
		if (vinLength <= 0)
			return;

		boolean coinbase = vinArray.getJSONObject(0).has("coinbase");
		if (coinbase)
		{
			txInfo.put("isCoinBase", coinbase);
			return;
		}

		double valueIn = 0.;
		for (int i = 0; i < vinLength; i++)
		{
			JSONObject obj = vinArray.getJSONObject(i);

			int vout = obj.getInt("vout");

			Map<String, Object> result = getVinValueAddr(obj.getString("txid"), vout, config);
			double value = (double)result.get("value");

			valueIn += value;

			obj.put("value",	value);
			obj.put("valueSat",	value * Math.pow(10, 8));
			obj.put("addr",		result.get("addr"));
			obj.put("assets",	result.get("assets"));
			obj.remove("scriptSig");
		}

		double fees = ((long)(valueIn * Math.pow(10, 8)) - (long)(valueOut * Math.pow(10, 8))) * Math.pow(10, -8);

		txInfo.put("valueIn", valueIn);	
		txInfo.put("fees", fees);
	}

	private Map<String, Object> getVinValueAddr(String txid, int vout, ServerConfig config) throws JSONException
	{
		Map<String, Object> result = new HashMap<String, Object>();

		JSONObject txInfo = getRawTransaction(txid, config);
		JSONArray voutArray = txInfo.getJSONArray("vout");
		int n = 0;
		for (int i = 0; i < voutArray.length(); i++)
		{
			JSONObject obj = voutArray.getJSONObject(i);

			n = obj.getInt("n");
			if (vout == n)
			{
				double sum = obj.getDouble("value");
				JSONArray addr = obj.getJSONObject("scriptPubKey").getJSONArray("addresses");

				JSONArray ret = new JSONArray();
				JSONArray assets = obj.getJSONArray("assets");
				int length = assets.length();
				for (int j = 0; j < length; j++)
				{
					JSONObject target = new JSONObject();
					JSONObject source = assets.getJSONObject(j);

					if (source.has("name"))
						target.put("name", source.get("name"));

					if (source.has("assetref"))
						target.put("assetref", source.get("assetref"));

					if (source.has("qty"))
						target.put("qty", source.get("qty"));

					ret.put(target);
				}

				result.put("value", sum);
				result.put("addr", addr);
				result.put("assets", ret);

				break;
			}
		}
		return result;
	}

	private double getValueOutValue(JSONArray voutArray) throws JSONException
	{
		double valueOut = 0.;

		int voutLength = voutArray.length();
		for (int i = 0; i < voutLength; i++)
		{
			JSONObject obj = voutArray.getJSONObject(i);

			valueOut += obj.getDouble("value");
			//obj.remove("assets");
			obj.remove("permissions");
			obj.remove("items");
		}

		return valueOut;
	}

	private String getScriptPubKey(String txid, int index, ServerConfig config) throws JSONException
	{
		Object[] params = new Object[2];
		params[0] = txid;
		params[1] = index;

		JSONObject options = new JSONObject();
		options.put("unconfirmed", true);

		JSONObject voutObj = HdacUtil.getDataObject("gettxout", params, options, config);
		if (voutObj.has("scriptPubKey"))
		{
			JSONObject scriptObj = voutObj.getJSONObject("scriptPubKey");
			if (scriptObj.has("hex"))
				return scriptObj.getString("hex");
		}
		return "";
	}

	private void separateMempoolList(List<JSONObject> vinList, List<JSONObject> voutList, JSONArray objMempoolArray) throws JSONException
	{
		int length = objMempoolArray.length();
		for (int i = 0; i < length; i++)
		{
			JSONObject obj = objMempoolArray.getJSONObject(i);
			long satoshis = obj.getLong("satoshis");
			if (satoshis > 0)
			{
				voutList.add(obj);
			}
			else if (satoshis < 0)
			{
				vinList.add(obj);
			}
		}
	}

	private boolean TxContains(List<JSONObject> list, JSONObject obj) throws JSONException
	{
		String txid = obj.getString("txid");

		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			JSONObject source = list.get(i);

			if (txid.equals(source.getString("txid")))
			{
				int sIndex = getIndex(source);
				int tIndex = getIndex(obj);

				if (sIndex == tIndex)
					return true;
			}
		}
		return false;
	}
	private boolean TxContainsPrev(List<JSONObject> list, JSONObject obj) throws JSONException
	{
		String txid = obj.getString("txid");

		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			JSONObject source = list.get(i);

			if (txid.equals(source.getString("prevtxid")))
			{
				int sIndex = getIndexPrev(source);
				int tIndex = getIndex(obj);

				if (sIndex == tIndex)
					return true;
			}
		}
		return false;
	}

	private int getIndex(JSONObject obj) throws JSONException
	{
		int index = -1;
		
		if (obj.has("vout"))
		{
			index = obj.getInt("vout");
		}
		else if (obj.has("index"))
		{
			index = obj.getInt("index");
		}
		else if (obj.has("outputIndex"))
		{
			index = obj.getInt("outputIndex");
		}
		return index;
	}
	private int getIndexPrev(JSONObject obj) throws JSONException
	{
		int index = -1;
		
		if (obj.has("prevout"))
		{
			index = obj.getInt("prevout");
		}
		return index;
	}

	private JSONObject getTxInfo(String txid, long blockCount, ServerConfig config) throws JSONException
	{
		JSONObject txInfo = getRawTransaction(txid, config);

		// make time field of mempool tx object to current time  
		if (txInfo.has("time") == false)
			txInfo.put("time", (long)(System.currentTimeMillis() / 1000));

		// make block height field 
		long blockHeight = 0;
		if (txInfo.has("confirmations"))
		{	// blockheight = blockcount - confirmation + 1
			long confirmations = txInfo.getLong("confirmations");
			blockHeight = blockCount - confirmations + 1;
		}
		else
		{
			txInfo.put("confirmations", 0);
			txInfo.put("blockhash", "");
			blockHeight = -1;
		}

		JSONArray vinArray = txInfo.getJSONArray("vin");
		JSONArray voutArray = txInfo.getJSONArray("vout");

		double valueOut = getValueOutValue(voutArray);
		txInfo.put("valueOut", valueOut);

		// valuein
		setValueInValue(txInfo, vinArray, valueOut, config);

		txInfo.put("size", txInfo.getString("hex").length());
		txInfo.put("blockheight", blockHeight);
		txInfo.remove("hex");

		return txInfo;
	}

	@Override
	public double getAddressBalance(String address, ServerConfig config)
	{
		double balance = 0;

		try
		{
			JSONObject params = new JSONObject();
			params.put("addresses", address.split(","));

			JSONObject objBalance = HdacUtil.getDataObject("getaddressbalance", params, config);
			BigDecimal satoshis = objBalance.getBigDecimal("balance");

			JSONArray mempoolList = HdacUtil.getDataArray("getaddressmempool", params, config);
			int mempoolLength = mempoolList.length();
			for (int i = 0; i < mempoolLength; i++)
			{
				JSONObject obj = mempoolList.getJSONObject(i);
				satoshis = satoshis.add(obj.getBigDecimal("satoshis"));
			}
			
			balance = satoshis.divide(BigDecimal.TEN.pow(8)).doubleValue();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return balance;
	}

	@Override
	public List<String> getAddressList(Map<String, Object> paramMap, ServerConfig config)
	{
		List<String> list = new ArrayList<String>();

		try
		{
			String address	= StringUtil.nvl(paramMap.get("address"));
			int start		= Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "-1"));
			int end			= Integer.parseInt(StringUtil.nvl(paramMap.get("end"), "-1"));
			int from		= Integer.parseInt(StringUtil.nvl(paramMap.get("from"), "0"));
			int count		= Integer.parseInt(StringUtil.nvl(paramMap.get("count"), "50"));

			JSONObject params = new JSONObject();
			params.put("addresses", address.split(","));

			JSONArray mempoolArray = HdacUtil.getDataArray("getaddressmempool", params, config);
			int mempoolLength = mempoolArray.length();

			params.put("from", Math.max(from - mempoolLength, 0));
			params.put("count", from + count);

			if (start > -1)
				params.put("start", start);
			if (end > -1)
				params.put("end", end);

			JSONArray blockArray = HdacUtil.getDataArray("getaddresstxids", params, config);
			int blockLength = blockArray.length();
			for (int i = 0; i < blockLength; i++)
			{
				list.add(blockArray.getString(i));
			}

			for (int i = 0; i < mempoolLength; i++)
			{
				String txid = mempoolArray.getJSONObject(i).getString("txid");

				if (list.contains(txid))
					continue;

				list.add(0, txid);
			}

			// make total tx list size to size of parameter
			list = getList(list, from, count);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	private JSONObject getRawTransaction(String txid, ServerConfig config)
	{
		JSONObject tx = null;
		
		try
		{
			Object[] params = new Object[2];
			params[0] = txid;
			params[1] = 1;

			String result = HdacUtil.getDataFromRPC("getrawtransaction", params, config);
	
			JSONObject rpcObject = new JSONObject(result);
			tx = rpcObject.getJSONObject("result");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return tx;
	}

	@Override
	public String assetIssue(Map<String, Object> paramMap)
	{
		String result = null;
				
		try
		{
			Object[] params = new Object[5];
			params[0] = StringUtil.nvl(paramMap.get("address"));
			params[1] = StringUtil.nvl(paramMap.get("tokenName"));
			params[2] = Double.valueOf(StringUtil.nvl(paramMap.get("amount")));
			params[3] = Float.parseFloat(StringUtil.nvl(paramMap.get("unit")));
			if(paramMap.containsKey("native")) params[4] = Double.valueOf(StringUtil.nvl(paramMap.get("native")));
			else params[4] = null;
			
			if(params[1].toString().indexOf('{') != -1) {
				JSONObject typeName = new JSONObject(StringUtil.nvl(paramMap.get("tokenName")));
				if(typeName.has("name"))  params[1] = typeName;
			}

			result = HdacUtil.getDataFromRPC("issue", params, HdacUtil._PRIVATE_);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
				
		return result;
	}

	private <T> List<T> getList(List<T> list, int from, int count)
	{
		int size = list.size();
		if (size > from)
			list = list.subList(from, Math.min(size, from + count));
		else
			list.clear();

		return list;
	}
}