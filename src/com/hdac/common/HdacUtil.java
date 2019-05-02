/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */
package com.hdac.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.hdac.dao.token.TokenDao;
import com.hdac.dao.token.TokenDaoImpl;
import com.hdac.handler.HdacWebSocketHandler;
import com.hdacSdk.hdacWallet.HdacCoreAddrParams;
import com.hdacSdk.hdacWallet.HdacTransaction;
import com.hdacSdk.hdacWallet.HdacWallet;
import com.hdacSdk.hdacWallet.HdacWalletManager;
import com.hdacSdk.hdacWallet.HdacWalletUtils;
import com.hdacSdk.hdacWallet.HdacWalletUtils.NnmberOfWords;

/**
 * This class will be moved to the contractlib
 * Deprecated
 * 
 * @version 0.8
 * 
 * @see     java.security.MessageDigest
 * @see     java.io.BufferedReader
 * @see     java.io.IOException
 * @see     java.io.InputStreamReader
 * @see     java.io.Reader
 * @see     java.nio.ByteBuffer
 * @see     java.util.ArrayList
 * @see     java.util.Base64
 * @see     java.util.HashMap
 * @see     java.util.List
 * @see     java.util.Map
 * @see     java.util.Properties
 * @see     javax.websocket.ContainerProvider
 * @see     javax.websocket.WebSocketContainer
 * @see     org.apache.commons.lang3.ArrayUtils
 * @see     org.apache.http.HttpEntity
 * @see     org.apache.http.client.methods.CloseableHttpResponse
 * @see     org.apache.http.client.methods.HttpPost
 * @see     org.apache.http.entity.StringEntity
 * @see     org.apache.http.impl.client.CloseableHttpClient
 * @see     org.apache.http.impl.client.HttpClients
 * @see     org.apache.http.util.EntityUtils
 * @see     org.apache.ibatis.io.Resources
 * @see     org.apache.ibatis.session.SqlSession
 * @see     org.bitcoinj.core.Base58
 * @see     org.bitcoinj.core.ECKey
 * @see     org.json.JSONArray
 * @see     org.json.JSONException
 * @see     org.json.JSONObject
 * @see     org.slf4j.Logger
 * @see     org.slf4j.LoggerFactory
 * @see     org.springframework.web.socket.client.WebSocketConnectionManager
 * @see     org.springframework.web.socket.client.standard.StandardWebSocketClient
 */
public class HdacUtil
{
	private static Logger logger = LoggerFactory.getLogger(HdacUtil.class);

	public static ServerConfig _PUBLIC_;
	public static ServerConfig _PRIVATE_;

	static
	{
		_PUBLIC_ = new ServerConfig();
		_PRIVATE_ = new ServerConfig();

		try
		{
			setServerConfig(_PUBLIC_, "config/rpc-hdac.properties");
			setServerConfig(_PRIVATE_, "config/rpc-sidechain.properties");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setServerConfig(ServerConfig config, String resource)
	{
		Properties properties = new Properties();

		try
		{
			Reader reader = Resources.getResourceAsReader(resource);
			properties.load(reader);

			config.setRpcIp(properties.getProperty("rpcIp"));
			config.setRpcPort(properties.getProperty("rpcPort"));
			config.setRpcUser(properties.getProperty("rpcUser"));
			config.setRpcPassword(properties.getProperty("rpcPassword"));
			config.setChainName(properties.getProperty("chainName"));

			config.setWsHost(properties.getProperty("wsHost"));
			config.setWsIp(properties.getProperty("wsIp"));
			config.setWsPort(properties.getProperty("wsPort"));

			reader.close();
		}
		catch (Exception e)
		{
			logger.error("hdac rpc exception " + e);
		}
	}

	public static ServerConfig getServerType(String path)
	{
		if ("public".equals(path))
			return HdacUtil._PUBLIC_;

		return HdacUtil._PRIVATE_;
	}

	public static WebSocketConnectionManager getWebSocket(ServerConfig config) throws JSONException
	{
		String host = config.getWsHost();
		String address = config.getWsIp() + ":" + config.getWsPort();

		JSONObject obj = new JSONObject();
		obj.put("user", config.getRpcUser());
		obj.put("pass", config.getRpcPassword());
		
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxBinaryMessageBufferSize(8192 * 8192);
		container.setDefaultMaxTextMessageBufferSize(8192 * 8192);
		
		WebSocketConnectionManager webSocketManager = new WebSocketConnectionManager(new StandardWebSocketClient(container), new HdacWebSocketHandler(obj, host), address);

		webSocketManager.setOrigin(host);
		webSocketManager.setAutoStartup(false);
		webSocketManager.start();
		
		return webSocketManager;
	}

	private static String sendRPC(String body, ServerConfig config) throws JSONException
	{
		StringBuilder result = new StringBuilder();
		CloseableHttpResponse response1 = null;

//		logger.debug("RPC CONFIG = " + config.getRpcIp() + " : " + config.getRpcPort() + " / " + config.getChainName());
		try
		{
			StringBuilder auth = new StringBuilder("Basic ");
			//auth.append(Base64.encode("hdacrpc:hdac1234".getBytes()));
			auth.append(Base64.getEncoder().encodeToString((config.getRpcUser() + ":" + config.getRpcPassword()).getBytes("UTF-8")));

			CloseableHttpClient httpclient = HttpClients.createDefault();
			//HttpPost httpPost = new HttpPost(_RPC_);
			HttpPost httpPost = new HttpPost(config.getRpcIp() + ":" + config.getRpcPort());

			httpPost.addHeader("content-type", "application/json");
			httpPost.addHeader("Authorization", auth.toString());

			HttpEntity entity = new StringEntity(body);
	        httpPost.setEntity(entity);

			response1 = httpclient.execute(httpPost);

		    HttpEntity entity1 = response1.getEntity();

		    BufferedReader rd = new BufferedReader(new InputStreamReader(entity1.getContent()));

		    String line = "";
		    while ((line = rd.readLine()) != null)
		    {
		    	result.append(line);
		    }

		    //logger.debug(result);

		    EntityUtils.consume(entity1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		    try
		    {
		    	if (response1 != null)
		    		response1.close();
			}
		    catch (IOException e)
		    {
			}
		}

		return result.toString();
	}

//	public static String makeRPC(String method, String params, ServerConfig config) throws JSONException
//	{
//		
//		String body = "";
//		try
//		{
//			body = getBody(method, new Object[0]);
//			if (!"[]".equals(params))
//			{
//				logger.debug("params = " + params);
//				body = body.replace("\"params\":[]", ("\"params\":" + params));
//			}
//			logger.debug("String body = " + body);
//			
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}		
//		return sendRPC(body, config);
//	}

	public static JSONObject getDataObject(String method, JSONObject params, ServerConfig config)
	{
		Object[] obj = new Object[1];
		obj[0] = params;

		return getDataObject(method, obj, null, config);
	}
	public static JSONObject getDataObject(String method, Object[] params, ServerConfig config)
	{
		return getDataObject(method, params, null, config);
	}
	public static JSONObject getDataObject(String method, Object[] params, JSONObject options, ServerConfig config)
	{
		try
		{
			String result = getDataFromRPC(method, params, options, config);
			JSONObject obj = new JSONObject(result);

			if (obj.has("result"))
				return obj.getJSONObject("result");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new JSONObject();
	}

	public static JSONArray getDataArray(String method, JSONObject params, ServerConfig config)
	{
		Object[] obj = new Object[1];
		obj[0] = params;

		return getDataArray(method, obj, null, config);
	}
	public static JSONArray getDataArray(String method, Object[] params, ServerConfig config)
	{
		return getDataArray(method, params, null, config);
	}
	public static JSONArray getDataArray(String method, Object[] params, JSONObject options, ServerConfig config)
	{
		try
		{
			String result = getDataFromRPC(method, params, options, config);
			JSONObject obj = new JSONObject(result);

//			logger.debug("obj : " + obj);
			if (obj.has("result"))
				return obj.getJSONArray("result");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new JSONArray();
	}

	public static String getDataFromRPC(String method, Object[] params, ServerConfig config) throws JSONException
	{
		return getDataFromRPC(method, params, null, config);
	}
	public static String getDataFromRPC(String method, Object[] params, JSONObject options, ServerConfig config) throws JSONException
	{
		String body = "";
		try
		{
			body = getBody(method, params, options);
			logger.debug("String body = " + body);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
		return sendRPC(body, config);
	}

	private static String getBody(String method, Object[] params) throws JSONException
	{
		return getBody(method, params, null);
	}
	private static String getBody(String method, Object[] params, JSONObject options) throws JSONException
	{
		long id = (long)(Math.random() * 10000);

		JSONObject obj = new JSONObject();
		obj.put("jsonrpc",	"2.0");
		obj.put("method",	method);
		obj.put("params",	params);
		obj.put("id",		id);

		if (options != null)
			obj.put("options",	options);

		return obj.toString();
	}
	
	public static String getKey()
	{
		return "IOT";//HdacUtil.chainName;
	}

	public static List<String> getSeedWord(String passPhrase)
	{
		HdacWalletUtils.NnmberOfWords[] num =
		{
			NnmberOfWords.MNEMONIC_12_WORDS,
			NnmberOfWords.MNEMONIC_15_WORDS,
			NnmberOfWords.MNEMONIC_18_WORDS,
			NnmberOfWords.MNEMONIC_21_WORDS,
			NnmberOfWords.MNEMONIC_24_WORDS,
		};
		int rand = (int)(Math.random() * 5);
		List<String> seedWords = HdacWalletUtils.getRandomSeedWords(num[rand]);

		HdacWallet hdacWallet = getHdacWallet(seedWords, passPhrase);

		if (hdacWallet.isValidWallet())
			return seedWords;

		return null;
	}

	public static HdacWallet getHdacWallet(List<String> seedWords, String passPhrase)
	{
		HdacCoreAddrParams params = new HdacCoreAddrParams(true);	// hdac network parameter (true : public network / false : private network)
		return HdacWalletManager.generateNewWallet(seedWords, passPhrase, params);
	}

	public static List<String> encodeSeed(List<String> seed, String key)
	{
		List<String> encSeed = new ArrayList<String>();
		for (String word : seed)
		{
			encSeed.add(CipherUtil.AesEncode(word, key));
		}
		return encSeed;
	}

	public static List<String> decodeSeed(List<String> seed, String key)
	{
		List<String> decSeed = new ArrayList<String>();
		for (String word : seed)
		{
			decSeed.add(CipherUtil.AesDecode(word, key));
		}
		return decSeed;
	}

	public static String getRawTransaction(HdacWallet wallet, JSONArray data, Map<String, Object> paramMap)
	{
		logger.debug("getRawTransaction data " + data);

		HdacTransaction transaction = new HdacTransaction(wallet.getNetworkParams());
//		HTransaction transaction = new HTransaction(wallet);
		String contractString = "";
		
		if (paramMap.size() > 0)
			contractString = JsonUtil.toJsonString(paramMap).toString();

		double balance = 0;
		try
		{
			int len = data.length();
	    	for (int i = 0; i < len; i++)
	    	{
				JSONObject utxo;
				utxo = data.getJSONObject(i);
				balance += utxo.getDouble("amount");
				
				transaction.addInput(data.getJSONObject(i));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}

		logger.debug("balance " + balance);

		//for checking balance 
		long lBalance = (long)(balance * Math.pow(10, 8));
		long fee = (long)(2 * Math.pow(10, 6) + contractString.length() * Math.pow(10, 3));
		long remain = lBalance - fee;

		logger.debug("lBalance " + lBalance);
		logger.debug("fee " + fee);
		logger.debug("remain " + remain);
		
		if (remain >= 0)
		{
			transaction.addOutput(wallet.getHdacAddress(), remain);
			transaction.addOpReturnOutput(JsonUtil.toJsonString(paramMap).toString(), "UTF-8");
			//transaction.addAssetOutput(address, txid, amount);
			try
			{
				int len = data.length();
				
		    	for (int i = 0; i < len; i++)
				{
					JSONObject utxo = data.getJSONObject(i);
					ECKey sign = wallet.getHdacSigKey(utxo.getString("address"));
					if (sign != null)
						transaction.setSignedInput(i, utxo, sign);
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			String raw_tx = transaction.getTxBuilder().build().toHex();
			logger.debug("raw_tx " + raw_tx);
			return raw_tx;
		}
		else
		{
			logger.debug("Raw Transaction : not enough hdac");
			logger.debug("Invalid raw transaction");
		}
		return null;
	}


//	public static String getAssetRawTransaction(HdacWallet wallet, Map<String, Object> paramMap) throws JSONException
//	{
//		logger.debug("getAssetRawTransaction paramMap " + paramMap);
//
//		HdacTransaction transaction = new HdacTransaction(wallet.getNetworkParams());
//		String SendAddress = StringUtil.nvl(paramMap.get("sendaddress"));
//		String remainAddress = StringUtil.nvl(paramMap.get("addresses"));
//		String txid = StringUtil.nvl(paramMap.get("tokenTxid"));
//		double assetValue = (double)paramMap.get("assetValue");
//		
//		JSONArray data = new JSONArray(StringUtil.nvl(paramMap.get("utxos")));
//
//		long balance = 0;
//		long assetbalance = 0;
//		try
//		{
//			int len = data.length();
//			logger.debug("len " + len);
//	    	for (int i = 0; i < len; i++)
//	    	{
//				JSONObject utxo;
//				utxo = data.getJSONObject(i);
//				assetbalance += (utxo.getJSONArray(Constants.strASSETS).getJSONObject(0).getDouble("qty") * Math.pow(10, 8));
//				balance += (utxo.getDouble("amount") * Math.pow(10, 8));
//				
//				transaction.addInput(data.getJSONObject(i));
//			}
//		}
//		catch (JSONException e)
//		{
//			e.printStackTrace();
//			return null;
//		}
//
//		logger.debug("balance " + balance);
//		long assetBalance = (long)(assetValue * Math.pow(10, 8));
//		//for checking balance 
//		long assetlBalance = assetbalance;
//		long assetremain = assetlBalance - assetBalance;
//		logger.debug("assetValue " + assetValue);
//		logger.debug("assetbalance " + assetbalance);
//		
//		long lBalance = balance;
//		long remain = lBalance;
//		
//		if (lBalance >= 0)
//		{
//			transaction.addAssetOutput(SendAddress, txid, assetBalance, (long)0);
//			transaction.addAssetOutput(remainAddress, txid, assetremain, remain);
//			
//			try
//			{
//				int len = data.length();
//		    	for (int i = 0; i < len; i++)
//				{
//					JSONObject utxo = data.getJSONObject(i);
//					ECKey sign = wallet.getHdacSigKey(utxo.getString("address"));
//					if (sign != null)
//						transaction.setSignedInput(i, utxo, sign, true);
////						transaction.addSignedInput(utxo, sign);
//				}
//			}
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//
//			String raw_tx = transaction.getTxBuilder().build().toHex();
//			logger.debug("raw_tx " + raw_tx);
//			return StringUtil.toSmallLetter(raw_tx, 0);
//		}
//		else
//		{
//			logger.debug("Raw Transaction : not enough hdac");
//			logger.debug("Invalid raw transaction");
//		}
//		return null;
//	}	

//	public static String getSwapRawTransaction(HdacWallet wallet, Map<String, Object> paramMap) throws JSONException
//	{
//		logger.debug("getSwapRawTransaction paramMap " + paramMap);
//
//		HdacTransaction transaction = new HdacTransaction(wallet.getNetworkParams());
//		String SendAddress = (String)paramMap.get("sendaddress");
//		String remainAddress = (String)paramMap.get("addresses");
//		double value = (double)paramMap.get("value");
//		
//		JSONArray data = new JSONArray(StringUtil.nvl(paramMap.get("utxos")));
//
//		logger.debug("data " + data);
//		long totalbalance = 0;
//		try
//		{
//			int len = data.length();
//			logger.debug("len " + len);
//	    	for (int i = 0; i < len; i++)
//	    	{
//				JSONObject utxo;
//				utxo = data.getJSONObject(i);
//				totalbalance += (utxo.getDouble("amount") * Math.pow(10, 8));
//
//				transaction.addInput(data.getJSONObject(i));
//			}
//		}
//		catch (JSONException e)
//		{
//			e.printStackTrace();
//			return null;
//		}
//
//		long Balance = (long)(value * Math.pow(10, 8));
//		//for checking balance 
//		long fee = (long)(2 * Math.pow(10, 6));
//		long remain = totalbalance - Balance - fee;
//
//		logger.debug("totalbalance " + totalbalance);
//		logger.debug("Balance " + Balance);
//		logger.debug("remain " + remain);
//		
//		if (totalbalance >= 0)
//		{
//			transaction.addOutput(SendAddress, Balance);
//			if (remain > 0)
//				transaction.addOutput(remainAddress, remain);
//			
//			try
//			{
//				int len = data.length();
//		    	for (int i = 0; i < len; i++)
//				{
//					JSONObject utxo = data.getJSONObject(i);
//					ECKey sign = wallet.getHdacSigKey(utxo.getString("address"));
//
//					if (sign != null)
//						transaction.setSignedInput(i, utxo, sign);
//				}
//			}
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//
//			String raw_tx = transaction.getTxBuilder().build().toHex();
//			logger.debug("raw_tx " + raw_tx);
//			return StringUtil.toSmallLetter(raw_tx, 0);
//		}
//		else
//		{
//			logger.debug("Raw Transaction : not enough hdac");
//			logger.debug("Invalid raw transaction");
//		}
//		return null;
//	}	
	
	public static HdacWallet getTokenHdacWallet()
	{
		SqlSession sqlSession = SqlMapConfig.getSqlSession();
		
		TokenDao tokenDao = (TokenDao)BeanUtil.getBean(TokenDaoImpl.class);
		tokenDao.setSqlSession(sqlSession);

		Map<String, Object> tokenparamMap = new HashMap<String, Object>();

		List<String> seedWords = tokenDao.getSeed(tokenparamMap);
		List<String> seed = HdacUtil.decodeSeed(seedWords, HdacUtil.getKey());
		logger.debug("seed : " + seed);

		return getHdacWallet(seed, null);
	}


//	public static String convPubkeyToHdacAddress(HdacCoreAddrParams params, byte[] buf) {
//	       if(buf==null) return null;
//			logger.debug("buf len : " + buf.length);
//	       
//	       byte[] hash = HashUtil.RIPEMD160(HashUtil.SHA256(buf));
//	       byte[] payload = new byte[hash.length + 1];
//	       payload[0] = (byte)(buf.length == 33 ? params.getAddressHeader() : params.getP2shHeader());
//	       for(int i=0;i<hash.length;i++) {
//	          payload[i+1] = hash[i];
//	       }
//	       
//	       ByteBuffer payloadHash = ByteBuffer.wrap(HashUtil.SHA256(HashUtil.SHA256(payload)));
//	       
//	       payloadHash.flip(); //position 0
//	       payloadHash.limit(4);
//	       
//	       byte[] checksum = new byte[4];
//	       for(int cs_id=0;cs_id<checksum.length;cs_id++) {
//	          checksum[cs_id] = payloadHash.get(cs_id);
//	       }
//	       ArrayUtils.reverse(checksum);
//	       byte[] hdacChecksum = StringUtil.toByteArray(params.getAddressChecksumValue());
//	       ArrayUtils.reverse(hdacChecksum);
//	       int length = hdacChecksum.length;//Math.max(checksum.length, hdacChecksum.length);
//	       byte[] checksumBuf = new byte[length];
//	       for (int i = 0; i < length; ++i) {
//	          byte xor = (byte)(0xff & ((int)(i<checksum.length?checksum[i]:0) ^ (int)(i<hdacChecksum.length?hdacChecksum[i]:0)));
//	          checksumBuf[i] = xor;
//	        }
//	       
//	       byte[] hdacAddrByte = new byte[payload.length + length];
//	       int hdacAddrByte_index = 0;
//	       for(;hdacAddrByte_index<payload.length;hdacAddrByte_index++) {
//	          hdacAddrByte[hdacAddrByte_index] = payload[hdacAddrByte_index];
//	       }
//	       
//	       ArrayUtils.reverse(checksumBuf);
//	       for(int i=0; i<checksumBuf.length; i++) {
//	          hdacAddrByte[hdacAddrByte_index + i] = checksumBuf[i];
//	       }       
//	       return Base58.encode(hdacAddrByte);       
//	}
//	    
	//-->for verify
	public static List<String> makeHash(List<String> list) {
		
		List<String> tempArray = new ArrayList<String>();
		int arraySize = list.size();
		
		//--> if the size is not even number, add value of copy of last value   
		/*if(arraySize % 2 != 0) {
			arraySize = arraySize + 1;
			list.add(list.get(list.size()-1));
		}*/
		//<--
						
		for(int i = 0; i < arraySize; i += 2) 
		{
			/*String reverse1 = hexReverse(list.get(i).toString());
			String reverse2 = hexReverse(list.get(i + 1).toString());
			tempArray.add(binaryHash(reverse1, reverse2));*/
			
			byte[] hex1 = reverseHexStringToByteArray(list.get(i).toString());
			byte[] hex2 = reverseHexStringToByteArray(list.get(i + 1).toString());
			tempArray.add(binaryHash(hex1, hex2));
		}
		
		if(tempArray.size() > 1) makeHash(tempArray);
						
		return tempArray;
	}

	private static byte[] SHA256(byte[] str)
	{
		try
		{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str); 

			return sh.digest();
		}
		catch (Exception e)
		{
		}
		return null;
	}
	

	public static byte[] reverse(byte[] arr) {
		
		byte[] buf = new byte[arr.length];
		for (int i = 0; i < arr.length; i++)
		{
			buf[i] = (byte)((arr[arr.length - i - 1] >>> (arr.length * i)) & 0xFF);
		}

		return buf;		
	}
		
	private static String binaryHash(byte[] hex1, byte[] hex2)	{

		byte[] sum_byte = ArrayUtils.addAll(hex1, hex2);

		// execute double hash
		byte[] hash_byte = SHA256(SHA256(sum_byte));

		// change binary to String
		StringBuilder sb = new StringBuilder();
		for (byte bb : hash_byte)
		{
			sb.insert(0, String.format("%02X", bb & 0xFF)); 
		}
		return sb.toString();
	}
	
	private static byte[] reverseHexStringToByteArray(String str) {

		int len = str.length() / 2;
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++)
		{
			data[i] = (byte)((Character.digit(str.charAt((len - i - 1) * 2), 16) << 4) + Character.digit(str.charAt((len - i) * 2 - 1), 16));
		}
		return data;
	}
	//-->for verify
}