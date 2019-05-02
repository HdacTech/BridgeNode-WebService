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
import com.hdac.service.rpc.RpcServiceImpl;


/**
 * Anchoring Web View
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
public class AnchorController
{
// do not connect directly from outside
//	@RequestMapping(value="/rest/sendRPC", method=RequestMethod.POST)
//	public ModelAndView sendRPC(@RequestParam(value="method") String method
//			, @RequestParam(value="params", defaultValue="[]") String params
//			, @RequestParam(value="path", defaultValue="public") String path
//			, HttpServletResponse response) throws JSONException
//	{
//		ModelAndView view = new ModelAndView("commonAjax");
//
//		view.addObject("jsonStr", HdacUtil.makeRPC(method, params, HdacUtil.getServerType(path)));
//		return view;
//	}

	//--> for verify
	@RequestMapping(value="/rest/verify", method=RequestMethod.POST)
	public ModelAndView getVerify(
		  @RequestParam(value="txid") String txid,
		  @RequestParam(value="contractLib", required=false) MultipartFile contractLib) throws JSONException
	{
		ModelAndView view = new ModelAndView("commonAjax");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("txid", txid);
		paramMap.put("file", contractLib);
				
		RpcServiceImpl service = (RpcServiceImpl)BeanUtil.getBean(RpcServiceImpl.class);
		view.addObject("jsonStr", service.verifyTx(paramMap));
		
		return view;
	}

	@RequestMapping(value="/verify.do", method=RequestMethod.GET)
	public ModelAndView verify(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView view = new ModelAndView("home/verify");
		return view;
	}
	//<-- for verify
}