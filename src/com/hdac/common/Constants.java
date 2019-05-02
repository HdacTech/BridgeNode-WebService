/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.common;

/**
 * This class will be moved to the contractlib
 * Deprecated
 * 
 * @version 0.8
 */
public class Constants
{
	public static final boolean DEBUG = true;

//	public static final String cmdBLOCK0			= getStr("{\"event\":[\"BLOCK:0\"]}");
//	public static final String cmdBLOCK1			= getStr("{\"event\":[\"BLOCK:1\"]}");
	public static final String cmdBLOCK2			= getStr("{\"event\":[\"BLOCK:2\"]}");
//	public static final String cmdTX1				= getStr("{\"event\":[\"TX:1\"]}");
	public static final String cmdTX2				= getStr("{\"event\":[\"TX:2\"]}");
	
	public static final int nINFO = 0;
	public static final int nAUTH = 1;
	public static final int nBLOCK = 2;
	public static final int nTX = 3;

	public static final String strINFO				= getStr("info");
	public static final String strAUTH				= getStr("auth");
	public static final String strBLOCK				= getStr("block");
	public static final String strTX				= getStr("tx");

//	public static final String strSUCCESS			= getStr("success");

//	public static final String strPUBLIC			= getStr("public");
//	public static final String strPRIVATE			= getStr("private");

//	public static final String strMainChainName		= getStr("HDAC");
//	public static final String strSideChainName		= getStr("sideChain");
//	public static final String strWSMAIN			= getStr("MainWebSocket");
//	public static final String strWSSIDE			= getStr("SideWebSocket");
	

//	public static final String strNO				= getStr("no");
//	public static final String strAMOUNT			= getStr("tokenCap");
//	public static final String strADDRESS			= getStr("recordAddress");
//	public static final String strNAME				= getStr("tokenName");
//	public static final String strSWAP_RATIO		= getStr("tokenSwapRatio");
//	public static final String strCONTRACTADDRESS	= getStr("contractAddress");
//	public static final String strTXID				= getStr("tokenTxid");
//	public static final String strCONTRACTTXID		= getStr("contractTxid");
//
//	public static final String strTOKENINFO			= getStr("tokeninfo");
//	
//	public static final String strWSHOST			= getStr("wsHost");
//
//	public static final String strTXVOUT			= getStr("vout");
//	public static final String strTXSCRIPTPUBKEY	= getStr("scriptPubKey");
//	public static final String strTXADDRESS			= getStr("address");
//	public static final String strTXADDRESSES		= getStr("addresses");
//	public static final String strTXASSETNAME		= getStr("assetName");
//	public static final String strASSET				= getStr("asset");
	public static final String strASSETS			= getStr("assets");

//	public static final String strTXVIN				= getStr("vin");
//	public static final String strTXSCRIPTSIG		= getStr("scriptSig");
//	public static final String strTXASM				= getStr("asm");
//	public static final String strTXTXID			= getStr("txid");
//	
//	public static final String strVALUE				= getStr("value");
	public static final String strTOTAL				= getStr("total");
	
	private static String getStr(String s)
	{
		return new String(s);
	}
}
