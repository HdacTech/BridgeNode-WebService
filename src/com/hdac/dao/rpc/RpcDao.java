/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.dao.rpc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * RPC(Remote Procedure Call) Data Access Object Interface
 * 
 * @version 0.8
 * 
 * @see		java.util.List
 * @see     java.util.Map
 * @see     org.json.JSONArray
 * @see     org.json.JSONObject
 */
public interface RpcDao
{
	/**
	 * get multi addresses token balance value (side chain only)
	 * called by getAssetAddressBalance(RpcService)
	 * 
	 * @param paramMap	values(addresses,asset)
	 * @return          get total balance(BigInteger) 
	 */
	public BigDecimal getMultiBalance(Map<String, Object> paramMap);

	/**
	 * get multi addresses token total valus(side chain only)
	 * called by getAssetAddress(RpcService)
	 * called by getMultiBalance(RpcDao)
	 * 
	 * 
	 * @param paramMap	values(addresses,asset)
	 * @return          get token values(jsonarray) 
	 */
	public JSONArray getMultiBalances(Map<String, Object> paramMap);
	
	/**
	 * get single address infomation(balance, transactions) (side chain only)
	 * called by getAssetAddress(RpcService)
	 *
	 * @param paramMap	values(address,count,skip,asset)
	 * @return          (List(string)) the formatted list string
	 */
	public List<String> getAssetTxList(Map<String, Object> paramMap);

	/**
	 * get multi addresses utxo informations (side chain only)
	 * called by getAssetUtxos(RpcService)
	 * 
	 * @param paramMap	values(addresses,asset)
	 * @return          (list(jsonobject)) the formatted list(jsonobject)
	 */	
	public List<JSONObject> getAssetUtxos(Map<String, Object> paramMap);
	
	/**
	 * get multi addresses transactions informations (side chain only)
	 * called by getAssetTxs(RpcService)
	 * 
	 * @param paramMap	values(addresses,count,skip)
	 * @return          (list(jsonobject)) the formatted list(jsonobject)
	 */
	public List<JSONObject> getAssetTxs(Map<String, Object> paramMap);
	
	/**
	 * get multi addresses utxos informations
	 * called by getUtxos(RpcService)
	 * 
	 * @param paramMap	values(addresses)
	 * @param config	value(main chain or side chain)
	 * @return          (list(jsonobject)) the formatted list(jsonobject)
	 */
	public List<JSONObject> getUtxosNew(Map<String, Object> paramMap, Map<String, Object> config);
	
	/**
	 * get multi addresses transactions informations
	 * called by getTxs(RpcService)
	 * 
	 * @param paramMap	values(addresses,start,end,from,count)
	 * @param config	value(main chain or side chain)
	 * @return          (jsonobject) the formatted jsonobject
	 */
	public JSONObject getTxsNew(Map<String, Object> paramMap, Map<String, Object> config);
	
	/**
	 * get signle address balance value
	 * called by getAddress(RpcService)
	 * 
	 * @param address	(string) the base58check encoded address
	 * @param config	value(main chain or side chain)
	 * @return          (BigDecimal)address balance 
	 */	
	public BigDecimal getAddressBalance(String address, Map<String, Object> config);
	
	/**
	 * get signle address infomation(balance, transactions, etc...)
	 * called by getAddress(RpcService)
	 * 
	 * @param paramMap	values(address,start,end,from,count)
	 * @param config	value(main chain or side chain)
	 * @return          (List(string)) the formatted list string
	 */
	public List<String> getAddressList(Map<String, Object> paramMap, Map<String, Object> config);
	
	//public JSONObject getRawTransaction(String txid, ServerConfig config);

	/**
	 * issuing token
	 * called by issueToken(RpcServiceImpl)
	 * 
	 * @param paramMap	values(address,tokenName,amount,unit,native,... etc)
	 * @return			(string) the formatted jsonobject string
	 */
	public String assetIssue(Map<String, Object> paramMap);
	
	/**
	 * deprecated function
	 * 
	 * @param blockHash	(string)blockhash 
	 * @param config	value(main chain or side chain)
	 * @return          (jsonobject) the formatted jsonobject
	 */
	public JSONObject getBlock(String blockHash, Map<String, Object> config);
}