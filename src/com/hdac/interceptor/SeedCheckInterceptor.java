/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hdac.common.BeanUtil;
import com.hdac.service.token.TokenService;
import com.hdac.service.token.TokenServiceImpl;

/**
 * SeedCheckInterceptor before bridge node start
 *  check seed value, websocket
 * 
 * @version 0.8
 * 
 * @see		javax.servlet.http.HttpServletRequest
 * @see     javax.servlet.http.HttpServletResponse
 * @see     org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 */
public class SeedCheckInterceptor extends HandlerInterceptorAdapter
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		TokenService service = (TokenService)BeanUtil.getBean(TokenServiceImpl.class);
		service.checkRootSeed();

		service.checkWebSocket();
		return true;
    }
}