/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.hdac.common.BeanUtil;
import com.hdac.service.rpc.RpcService;
import com.hdac.service.rpc.RpcServiceImpl;

/**
 * REST API Controller(only used hdac side chain)  
 * 
 * @version 0.8
 * @see     java.util.HashMap
 * @see     java.util.Map
 * @see    	org.springframework.stereotype.Controller
 * @see    	org.springframework.web.bind.annotation.PathVariable
 * @see    	org.springframework.web.bind.annotation.RequestMapping
 * @see    	org.springframework.web.bind.annotation.RequestMethod
 * @see    	org.springframework.web.bind.annotation.RequestParam
 * @see    	org.springframework.web.servlet.ModelAndView
 *
 */
@Controller
@RequestMapping("/asset")
public class AssetApiController
{
	/**
	 * get single address infomation(balance, transactions) (side chain only)
	 * 
	 * @param address	(string) the base58check encoded address
	 * @param count		(number) the number of items to show on found results
	 * @param skip		(number) the staring index on found results
	 * @param asset		(string) token name
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addr/{address}", method=RequestMethod.GET)
	public ModelAndView assetAddress(@PathVariable String address
		, @RequestParam(value="count",	defaultValue="50")	String count
		, @RequestParam(value="skip",	defaultValue="0")	String skip
		, @RequestParam(value="name",	defaultValue="")	String asset)
	{
		ModelAndView view = new ModelAndView("commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("address",	address);
		paramMap.put("asset",	asset);
		paramMap.put("count",	count);
		paramMap.put("skip",	skip);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getAssetAddress(paramMap));
		return view;
	}

	/**
	 * get multi addresses balance value (side chain only)
	 * 
	 * @param addresses	["addresses",...] (string) The base58check encoded address
	 * @param asset		(string) token name
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addrs/{addresses}/balance", method=RequestMethod.GET)
	public ModelAndView assetAddressBalance(@PathVariable String addresses
		, @RequestParam(value="name", defaultValue="") String asset)
	{
		ModelAndView view = new ModelAndView("commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("address",	addresses);
		paramMap.put("asset",	asset);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getAssetAddressBalance(paramMap));
		return view;
	}

	/**
	 * get multi addresses utxo informations (side chain only)
	 * 
	 * @param addresses	["addresses",...] (string) The base58check encoded address
	 * @param asset		(string) token name
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addrs/{addresses}/utxo", method=RequestMethod.GET)
	public ModelAndView assetUtxos(@PathVariable String addresses
		, @RequestParam(value="name", defaultValue="") String asset)
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("addresses",	addresses);
		paramMap.put("asset",		asset);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getAssetUtxos(paramMap));
		return view;
	}


	/**
	 * get multi addresses transactions informations (side chain only)
	 * 
	 * @param addresses	["addresses",...] (string) The base58check encoded address
	 * @param count		(number) the number of items to show on found results
	 * @param skip		(number) the staring index on found results
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addrs/{addresses}/txs", method=RequestMethod.GET)
	public ModelAndView assetTxs(@PathVariable String addresses
		, @RequestParam(value="to",	defaultValue="50")	String count
		, @RequestParam(value="from",	defaultValue="0")	String skip)
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("addresses",	addresses);
		paramMap.put("count",		count);
		paramMap.put("skip",		skip);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getAssetTxs(paramMap));
		return view;
	}

	
	/**
	 * show token-list informations (side chain only)
	 * 
	 * @param asset		(string) token name
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/listassets", method=RequestMethod.GET)
	public ModelAndView listassets(@RequestParam(value="name", defaultValue="") String asset)
	{
		ModelAndView view = new ModelAndView("commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("asset", asset);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getListAssets(paramMap));
		return view;
	}
}