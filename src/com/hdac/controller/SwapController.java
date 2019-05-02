/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hdac.common.BeanUtil;
import com.hdac.common.JsonUtil;
import com.hdac.service.token.TokenService;
import com.hdac.service.token.TokenServiceImpl;

/**
 * Bridge node Web View
 * 
 * @version 0.8
 * @see     java.util.HashMap
 * @see     java.util.Map
 * @see		org.json.JSONException
 * @see    	org.springframework.stereotype.Controller
 * @see    	org.springframework.web.bind.annotation.RequestMapping
 * @see    	org.springframework.web.bind.annotation.RequestMethod
 * @see    	org.springframework.web.bind.annotation.RequestParam
 * @see    	org.springframework.web.multipart.MultipartFile
 * @see    	org.springframework.web.servlet.ModelAndView
 *
 */
@Controller
public class SwapController
{
	/**
	 * show registered token data (GET)
	 * 
	 * @param request	(HttpServletRequest) 
	 * @param response	(HttpServletResponse)
	 * @return			(jsp) web age
	 */
	@RequestMapping(value="/list.do", method=RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("home/list");
		return view;
	}

	/**
	 * show registered detailed token data (POST)
	 * 
	 * @param request	(HttpServletRequest)
	 * @param response	(HttpServletResponse)
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/listData.do", method=RequestMethod.POST)
	public ModelAndView listData(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("/commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();

		Map<String, Object> resultMap = new HashMap<String, Object>();

		TokenService service = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		resultMap.put("list", service.getTokenList(paramMap));

		view.addObject("jsonStr", JsonUtil.toJsonString(resultMap).toString());
		return view;
	}

	/**
	 * show registered detailed token data (GET)
	 * 
	 * @param request	(HttpServletRequest) 
	 * @param response	(HttpServletResponse)
	 * @return			(jsp) web age
	 */
	@RequestMapping(value="/info.do", method=RequestMethod.GET)
	public ModelAndView info(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("home/info");
		return view;
	}


	/**
	 * show selected token data  (POST)
	 * 
	 * @param request	(HttpServletRequest)
	 * @param response	(HttpServletResponse)
	 * @param no		(string) token number
	 * @return          (string) the formatted json string
	 */
	@RequestMapping(value="/infoData.do", method=RequestMethod.POST)
	public ModelAndView infoData(HttpServletRequest request, HttpServletResponse response
		, @RequestParam("no") String no)
	{
		ModelAndView view = new ModelAndView("/commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("no", no);

		Map<String, Object> resultMap = new HashMap<String, Object>();

		TokenService service = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		resultMap.put("info", service.getTokenInfo(paramMap));

		view.addObject("jsonStr", JsonUtil.toJsonString(resultMap).toString());
		return view;
	}


	/**
	 * input token data (GET)
	 * 
	 * @param request	(HttpServletRequest) 
	 * @param response	(HttpServletResponse)
	 * @return			(jsp) web age
	 */
	@RequestMapping(value="/form.do", method=RequestMethod.GET)
	public ModelAndView formReport(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("home/form");
		return view;
	}

	/**
	 * submit token data (POST)
	 * 
	 * @param request	(HttpServletRequest) 
	 * @param response	(HttpServletResponse)
	 */
	/**
	 * @param tokenName			(string) token name
	 * @param tokenCap			(int) token amount max limit
	 * @param tokenSwapRatio	(string) token swap ratio
	 * @param serverInfo		(string) bridge node server info
	 * @param binary			(string) contactlib		
	 * @param binaryPath		(string) contactlib path		
	 * @param binaryHash		(string) contactlib hash
	 * @param recordAddress		(string) recordAddress
	 * @param anchoringAddress	(string) anchoringAddress
	 * 
	 * @return          		(string) the formatted json string
	 * @throws JSONException	json exception
	 */
	@RequestMapping(value = "/submitReport.do", method={RequestMethod.POST})
	public ModelAndView submitReport(
		@RequestParam("tokenName")			String tokenName
		, @RequestParam("tokenCap")			String tokenCap
		, @RequestParam("tokenSwapRatio")	String tokenSwapRatio
		, @RequestParam("serverInfo")		String serverInfo
		, @RequestParam("binary")			MultipartFile binary
		, @RequestParam("binaryHash")		String binaryHash
		, @RequestParam("binaryPath")		String binaryPath
		, @RequestParam("recordAddress")	String recordAddress
		, @RequestParam("anchoringAddress")	String anchoringAddress
	) throws JSONException
	{
		ModelAndView view = new ModelAndView("/commonAjax");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tokenName",			tokenName);
		paramMap.put("tokenCap",			tokenCap);
		paramMap.put("tokenSwapRatio",		tokenSwapRatio);
		paramMap.put("serverInfo",			serverInfo);
		paramMap.put("binary",				binary);
		paramMap.put("binaryHash",			binaryHash);
		paramMap.put("binaryPath",			binaryPath);
		paramMap.put("pointNumber",			8);

		TokenService tokenservice = (TokenService)BeanUtil.getBean(TokenServiceImpl.class); //hyyang
		Map<String, Object> resultMap = tokenservice.insertToken(paramMap);
		view.addObject("jsonStr", JsonUtil.toJsonString(resultMap).toString());

		return view;
	}

	/**
	 * save token info to database or file (POST)
	 * 
	 * @param data		tokeninfo data
	 * @return          (string) the formatted json string
	 * @throws JSONException json exception
	 */	
	@RequestMapping(value = "/insertTokenInfo.do", method={RequestMethod.POST})
	public ModelAndView insertTokenInfo(@RequestParam("data") String data) throws JSONException
	{
		ModelAndView view = new ModelAndView("/commonAjax");

		TokenService tokenservice = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		boolean result = tokenservice.insertTokenInfo(data);
		view.addObject("jsonStr", result ? "true" : "false");

		return view;
	}
	
	//--> for main
	@RequestMapping(value="/getmain", method=RequestMethod.POST)
	public ModelAndView getMainData() throws JSONException
	{
		ModelAndView view = new ModelAndView("commonAjax");
						
		TokenService tokenservice = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		view.addObject("jsonStr", tokenservice.getMainPageData());
		
		return view;
	}
	
	@RequestMapping(value="/main.do", method=RequestMethod.GET)
	public ModelAndView mainT(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("home/main");
		return view;
	}
	//<-- for main
}