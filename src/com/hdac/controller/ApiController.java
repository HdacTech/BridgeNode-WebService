/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.hdac.common.BeanUtil;
import com.hdac.common.HdacUtil;
import com.hdac.common.JsonUtil;
import com.hdac.service.rpc.RpcService;
import com.hdac.service.rpc.RpcServiceImpl;

/**
 * REST API Controller(used both hdac main chain and hdac side chain)  
 * 
 * @version 0.8
 * @see     java.util.HashMap
 * @see     java.util.Map
 * @see    	javax.servlet.http.HttpServletResponse
 * @see    	org.springframework.stereotype.Controller
 * @see    	org.springframework.web.bind.annotation.PathVariable
 * @see    	org.springframework.web.bind.annotation.RequestMapping
 * @see    	org.springframework.web.bind.annotation.RequestMethod
 * @see    	org.springframework.web.bind.annotation.RequestParam
 * @see    	org.springframework.web.servlet.ModelAndView
 *
 */
@Controller
public class ApiController
{
	/**
	 * get signle address infomation(balance, transactions)
	 * 
	 * @param address	(string) the base58check encoded address
	 * @param start		(number) the start block height
	 * @param end		(number) the end block height
	 * @param from		(number) the staring index on found results
	 * @param count		(number) the number of items to show on found results
	 * @param path		(string) default path=public
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addr/{address}", method=RequestMethod.GET)
	public ModelAndView getAddress(@PathVariable String address
		, @RequestParam(value="start",	defaultValue="-1")		String start
		, @RequestParam(value="end",	defaultValue="-1")		String end
		, @RequestParam(value="from",	defaultValue="0")		String from
		, @RequestParam(value="count",	defaultValue="50")		String count
		, @RequestParam(value="path",	defaultValue="public")	String path)
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("address",		address);
		paramMap.put("start",		start);
		paramMap.put("end",			end);
		paramMap.put("from",		from);
		paramMap.put("count",		count);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getAddress(paramMap, HdacUtil.getServerType(path)));
		return view;
	}

	/**
	 * get multi addresses utxo informations
	 * 
	 * @param addresses	["addresses",...] (string) The base58check encoded address
	 * @param path		(string) default path=public
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/addrs/{addresses}/utxo", method=RequestMethod.GET)
	public ModelAndView getUtxos(@PathVariable String addresses
		, @RequestParam(value="path", defaultValue="public") String path)
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("addresses", addresses);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getUtxos(paramMap, HdacUtil.getServerType(path)));
		return view;
	}


	/**
	 * get multi addresses transactions informations
	 * 
	 * @param addresses	["addresses",...] (string) The base58check encoded address
	 * @param start		(number) the start block height
	 * @param end		(number) the end block height
	 * @param from		(number) the staring index on found results
	 * @param count		(number) the number of items to show on found results
	 * @param path		(string) default path=public
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value={"/addrs/{addresses}/txs", "/addrs/{addresses}/statics"}, method=RequestMethod.GET)
	public ModelAndView getMultiaddr(@PathVariable String addresses
		, @RequestParam(value="start",	defaultValue="-1")		String start
		, @RequestParam(value="end",	defaultValue="-1")		String end
		, @RequestParam(value="from",	defaultValue="0")		String from
		, @RequestParam(value="to",		defaultValue="50")		String count
		, @RequestParam(value="path",	defaultValue="public")	String path)
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("addresses",	addresses);
		paramMap.put("start",		start);
		paramMap.put("end",			end);
		paramMap.put("from",		from);
		paramMap.put("count",		count);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getTxs(paramMap, HdacUtil.getServerType(path)));
		return view;
	}

	/**
	 * send raw transaction hex data
	 * 
	 * @param response	(HttpServletResponse) 
	 * @param rawtx		(string) transaction hex data
	 * @param path		(string) default path=public
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/tx/send", method=RequestMethod.POST)
	public ModelAndView getPushtx(HttpServletResponse response
		, @RequestParam(value="rawtx") String rawtx
		, @RequestParam(value="path", defaultValue="public") String path)
	{
		ModelAndView view = new ModelAndView("commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rawtx", rawtx);

		RpcService service = (RpcService)BeanUtil.getBean(RpcServiceImpl.class);
		Map<String, Object> resultMap = service.sendTx(paramMap, HdacUtil.getServerType(path)); 

		String result = "";
		boolean success = (boolean)resultMap.get("success");
		if (success)
		{
			response.setStatus(HttpServletResponse.SC_OK);
			result = JsonUtil.toJsonString(resultMap).toString();
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = resultMap.get("txid").toString();
		}

		view.addObject("jsonStr", result);
		return view;
	}
	
	@RequestMapping(value="/getinfo", method=RequestMethod.GET)
	public ModelAndView getInfo(@RequestParam(value="path",	defaultValue="public")	String path)
	{
		ModelAndView view = new ModelAndView("commonAjax");
						
		RpcServiceImpl service = (RpcServiceImpl)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.getInfo(HdacUtil.getServerType(path)));
		
		return view;
	}
}