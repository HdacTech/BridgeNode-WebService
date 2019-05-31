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
import com.hdac.comm.HdacUtil;
import com.hdac.comm.StringUtil;
import com.hdac.comparator.JsonComparator;
import com.hdac.property.ServerConfig;
import com.hdac.service.RpcService;

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
	public BigDecimal getMultiBalance(Map<String, Object> paramMap)
	{
		BigDecimal balance = BigDecimal.ZERO;

		try
		{
			JSONArray array = getMultiBalances(paramMap);
			int length = array.length();
			for (int i = 0; i < length; i++)
			{
				JSONObject obj = array.getJSONObject(i);
				if (obj.has("name") && obj.getString("name").equals(paramMap.get("asset")))
				{
					balance = balance.add(obj.getBigDecimal("qty"));
				}
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
			String[] addresses = getFilterListAddresses(StringUtil.nvl(paramMap.get("address")).split(","));
			
			if (addresses == null)
				return new JSONArray();

			Object[] params = new Object[3];
			params[0] = addresses;
			params[1] = StringUtil.nvl(paramMap.get("asset"));
			params[2] = 0;

			ServerConfig config = ServerConfig.getInstance();
			JSONObject objBalance = HdacUtil.getDataJSONObject("getmultibalances", params, config.getSideChainInfo());
			
			String filter = (addresses.length > 1 ? Constants.strTOTAL : addresses[0]);
			
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
			ServerConfig config = ServerConfig.getInstance();
			String result = HdacUtil.getDataFromRPC("listaddresses", new Object[0], config.getSideChainInfo());
			logger.debug("result : " + result);
			logger.debug("result len : " + result.length());

			if ((result != null) && (result.length() > 35)) 
			{
				List<String> addrList = new ArrayList<String>();

				for (String address : addrs)
				{
					if (result.contains(address))
						addrList.add(address);
				}

				int size = addrList.size();
				logger.debug("size : " + size);
				if (size > 0)
					addresses = addrList.toArray(new String[size]);
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

			ServerConfig config = ServerConfig.getInstance();
			JSONArray blockTxList = HdacUtil.getDataJSONArray("listaddresstransactions", params, options, config.getSideChainInfo());

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
		ServerConfig config = ServerConfig.getInstance();
		RpcService service = RpcService.getInstance();
		return service.getAssetUtxos(paramMap, config.getSideChainInfo());
	}

	@Override
	public List<JSONObject> getAssetTxs(Map<String, Object> paramMap)
	{
		List<JSONObject> list = new ArrayList<JSONObject>();

		try
		{
			List<String> txids = new ArrayList<String>();

			ServerConfig config = ServerConfig.getInstance();
			long blockCount = getBlockCount(config.getSideChainInfo());

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
					list.add(getTxInfo(txid, blockCount, config.getSideChainInfo()));
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
	public List<JSONObject> getUtxosNew(Map<String, Object> paramMap, Map<String, Object> config)
	{
		RpcService service = RpcService.getInstance();
		return service.getUtxos(paramMap, config);
	}

	@Override
	public JSONObject getTxsNew(Map<String, Object> paramMap, Map<String, Object> config)
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

			JSONArray mempoolArray = HdacUtil.getDataJSONArray("getaddressmempool", params, config);
			int mempoolLength = mempoolArray.length();

//			params.put("from", Math.max(from - mempoolLength, 0));
//			params.put("count", from + count);

			if (start > -1)
				params.put("start", start);
			if (end > -1)
				params.put("end", end);

			JSONArray blockArray = HdacUtil.getDataJSONArray("getaddresstxids", params, config);
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
	public JSONObject getBlock(String blockHash, Map<String, Object> config)
	{
		RpcService service = RpcService.getInstance();
		return service.getblock(Long.parseLong(blockHash), config);
	}
	
	private long getBlockCount(Map<String, Object> config)
	{
		RpcService service = RpcService.getInstance();
		return service.getBlockCount(config);
	}

	private void setValueInValue(JSONObject txInfo, JSONArray vinArray, BigDecimal valueOut, Map<String, Object> config) throws JSONException
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

		BigDecimal valueIn = BigDecimal.ZERO;
		for (int i = 0; i < vinLength; i++)
		{
			JSONObject obj = vinArray.getJSONObject(i);

			int vout = obj.getInt("vout");

			Map<String, Object> result = getVinValueAddr(obj.getString("txid"), vout, config);
			BigDecimal value = new BigDecimal(StringUtil.nvl(result.get("value"), "0"));

			valueIn = valueIn.add(value);

			obj.put("value",	value);
			obj.put("valueSat",	value.multiply(BigDecimal.TEN.pow(8)));
			obj.put("addr",		result.get("addr"));
			obj.put("assets",	result.get("assets"));
			obj.remove("scriptSig");
		}

		BigDecimal fees = valueIn.subtract(valueOut);

		txInfo.put("valueIn", valueIn);	
		txInfo.put("fees", fees);
	}

	private Map<String, Object> getVinValueAddr(String txid, int vout, Map<String, Object> config) throws JSONException
	{
		Map<String, Object> result = new HashMap<String, Object>();

		RpcService service = RpcService.getInstance();
		JSONObject txInfo = service.getRawTransaction(txid, config);
		JSONArray voutArray = txInfo.getJSONArray("vout");
		int n = 0;
		for (int i = 0; i < voutArray.length(); i++)
		{
			JSONObject obj = voutArray.getJSONObject(i);

			n = obj.getInt("n");
			if (vout == n)
			{
				BigDecimal sum = obj.getBigDecimal("value");
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

	private BigDecimal getValueOutValue(JSONArray voutArray) throws JSONException
	{
		BigDecimal valueOut = BigDecimal.ZERO;

		int voutLength = voutArray.length();
		for (int i = 0; i < voutLength; i++)
		{
			JSONObject obj = voutArray.getJSONObject(i);

			valueOut = valueOut.add(obj.getBigDecimal("value"));
			//obj.remove("assets");
			obj.remove("permissions");
			obj.remove("items");
		}

		return valueOut;
	}

	private JSONObject getTxInfo(String txid, long blockCount, Map<String, Object> config) throws JSONException
	{
		RpcService service = RpcService.getInstance();
		JSONObject txInfo = service.getRawTransaction(txid, config);

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

		BigDecimal valueOut = getValueOutValue(voutArray);
		txInfo.put("valueOut", valueOut);

		// valuein
		setValueInValue(txInfo, vinArray, valueOut, config);

		txInfo.put("size", txInfo.getString("hex").length());
		txInfo.put("blockheight", blockHeight);
		txInfo.remove("hex");

		return txInfo;
	}

	@Override
	public BigDecimal getAddressBalance(String address, Map<String, Object> config)
	{
		BigDecimal balance = BigDecimal.ZERO;

		try
		{
			Object[] params = new Object[1];
			
			JSONObject paramObj = new JSONObject();
			paramObj.put("addresses", address.split(","));
			params[0] = paramObj;

			JSONObject objBalance = HdacUtil.getDataJSONObject("getaddressbalance", params, config);
			BigDecimal satoshis = objBalance.getBigDecimal("balance");

			JSONArray mempoolList = HdacUtil.getDataJSONArray("getaddressmempool", params, config);
			int mempoolLength = mempoolList.length();
			for (int i = 0; i < mempoolLength; i++)
			{
				JSONObject obj = mempoolList.getJSONObject(i);
				satoshis = satoshis.add(obj.getBigDecimal("satoshis"));
			}
			
			balance = satoshis.divide(BigDecimal.TEN.pow(8));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return balance;
	}

	@Override
	public List<String> getAddressList(Map<String, Object> paramMap, Map<String, Object> config)
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

			JSONArray mempoolArray = HdacUtil.getDataJSONArray("getaddressmempool", params, config);
			int mempoolLength = mempoolArray.length();

			params.put("from", Math.max(from - mempoolLength, 0));
			params.put("count", from + count);

			if (start > -1)
				params.put("start", start);
			if (end > -1)
				params.put("end", end);

			JSONArray blockArray = HdacUtil.getDataJSONArray("getaddresstxids", params, config);
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

			ServerConfig config = ServerConfig.getInstance();
			result = HdacUtil.getDataFromRPC("issue", params, config.getSideChainInfo());
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