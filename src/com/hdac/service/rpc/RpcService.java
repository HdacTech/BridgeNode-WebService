/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.service.rpc;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;


/**
 * Remote Procedure Call Service Interface 
 * 
 * @version 0.8
 * 
 * @see     java.util.Map
 * @see		org.springframework.stereotype.Service
 */
@Service
public interface RpcService
{
	/**
	 * get signle address infomation(balance, transactions, etc...)
	 * called by getAddress(ApiController)
	 * 
	 * @param paramMap	values(address,start,end,from,count)
	 * @param config	value(main chain or side chain)
	 * @return          (string) the formatted json string
	 */
	public String getAddress(Map<String, Object> paramMap, Map<String, Object> config);
	
	/**
	 * get multi addresses utxos informations
	 * called by getUtxos(ApiController)
	 * 
	 * @param paramMap	values(addresses)
	 * @param config	value(main chain or side chain)
	 * @return          (string) the formatted json string
	 */
	public String getUtxos(Map<String, Object> paramMap, Map<String, Object> config);

	/**
	 * get multi addresses transactions informations
	 * called by getMultiaddr(ApiController)
	 * 
	 * @param paramMap	values(addresses,start,end,from,count)
	 * @param config	value(main chain or side chain)
	 * @return          (string) the formatted json string
	 */
	public String getTxs(Map<String, Object> paramMap, Map<String, Object> config);

	/**
	 * send raw transaction hex data
	 * called by getPushtx(ApiController)
	 * 
	 * @param paramMap	values(rawtx)
	 * @param config	value(main chain or side chain)
	 * @return          (Map(string,object)) formatted string-object map data
	 */
	public Map<String, Object> sendTx(Map<String, Object> paramMap, Map<String, Object> config);
	

	/**
	 * get multi addresses balance value (side chain only)
	 * called by assetAddressBalance(AssetApiController)
	 * 
	 * @param paramMap	values(addresses,asset)
	 * @return          (BigDecimal) the formatted json string
	 */
	public BigDecimal getAssetAddressBalance(Map<String, Object> paramMap);
	
	/**
	 * get single address infomation(balance, transactions) (side chain only)
	 * called by assetAddress(AssetApiController)
	 * 
	 * @param paramMap	values(address,count,skip,asset)
	 * @return          (string) the formatted json string
	 */
	public String getAssetAddress(Map<String, Object> paramMap);
	
	
	
	/**
	 * get multi addresses utxo informations (side chain only)
	 * called by assetUtxos(AssetApiController)
	 * 
	 * @param paramMap	values(addresses,asset)
	 * @return          (string) the formatted json string
	 */
	public String getAssetUtxos(Map<String, Object> paramMap);

	/**
	 * get multi addresses transactions informations (side chain only)
	 * called by assetTxs(AssetApiController)
	 * 
	 * @param paramMap values(addresses,count,skip)
	 * @return          (string) the formatted json string
	 */
	public String getAssetTxs(Map<String, Object> paramMap);
	
	/**
	 * show token-list informations (side chain only)
	 * called by listassets(AsseteApiController)
	 *  
	 * @param paramMap	value(asset)
	 * @return          (string) the formatted json string
	 */
	public String getListAssets(Map<String, Object> paramMap);
	
}