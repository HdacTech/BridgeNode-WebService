package com.junit.test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.hdac.comm.StringUtil;
import com.hdac.controller.AssetApiController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/webapps/WEB-INF/servlet-context.xml")
@WebAppConfiguration
class AssetApiControllerTest
{
	@InjectMocks
	private AssetApiController controller;
    private MockMvc mockMvc = null;
    private String addressParam	= "HMe7prd9ceJyVc6ETi54UVv9ptaUrQf28y";
    private String addressesParam = "HMe7prd9ceJyVc6ETi54UVv9ptaUrQf28y";
    private String reg1 = "([0-9]*\\.[0-9]{0,8}|[0-9]+)";
    private String reg2 = "[0-9]+";
    
    void init()
    {
		System.out.println("init");
		MockitoAnnotations.initMocks(this);

		if (this.mockMvc == null)
			this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	void testAssetAddress() throws Exception
	{
		System.out.println("testAssetAddress()");
		init();
		
		testAssetAddress(addressParam, "Thor");
	}

	void testAssetAddress(String address, String name) throws Exception
	{
		String addrpath = "/asset/addr/" + address;
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("name", name))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0)
		{
			System.out.println("result : " + jsonStr);

			//check json param
			Assert.assertTrue(jsonStr.has("assets"));
			Assert.assertTrue(jsonStr.has("balance"));
			Assert.assertTrue(jsonStr.has("balanceSat"));
			Assert.assertTrue(jsonStr.has("addrStr"));
			Assert.assertTrue(jsonStr.has("transactions"));
			Assert.assertTrue(jsonStr.has("txApperances"));
		}
	}

	@Test
	void testAssetAddressBalance() throws Exception
	{
		System.out.println("testAssetAddressBalance()");
		init();

		testAssetAddressBalance(addressesParam);
	}

	void testAssetAddressBalance(String addresses) throws Exception
	{
		String addrpath = "/asset/addrs/" + addresses + "/balance";
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		String strBalance = StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr"));
		System.out.println("result : " + strBalance);

        Assert.assertTrue(Pattern.matches(reg1, strBalance));
	}

	@Test
	void testAssetUtxos() throws Exception
	{
		System.out.println("testAssetUtxos()");
		init();

		testAssetUtxos(addressesParam, "Thor");
	}

	void testAssetUtxos(String addresses, String name) throws Exception
	{
		String addrpath = "/asset/addrs/" + addresses + "/utxo";

		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("name", name))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONArray arr = new JSONArray(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		int len = arr.length();
		for (int i = 0; i < len; i++)
		{
			JSONObject obj = arr.getJSONObject(i);

	        //check json param
	        Assert.assertTrue(obj.has("scriptPubKey"));
	        Assert.assertTrue(obj.has("address"));
	        Assert.assertTrue(obj.has("txid"));
	        Assert.assertTrue(obj.has("confirmations"));
	        Assert.assertTrue(obj.has("unspent_hash"));
	        Assert.assertTrue(obj.has("vout"));
	        Assert.assertTrue(obj.has("amount"));
	        Assert.assertTrue(obj.has("satoshis"));
	        Assert.assertTrue(Pattern.matches(reg1, StringUtil.nvl(obj.get("amount"))));
	        Assert.assertTrue(Pattern.matches(reg2, StringUtil.nvl(obj.get("satoshis"))));
		}
	}

	@Test
	void testAssetTxs() throws Exception
	{
		System.out.println("testAssetTxs()");
		init();

		testAssetTxs(addressesParam);
	}

	void testAssetTxs(String addresses) throws Exception
	{
		String addrpath = "/asset/addrs/" + addresses + "/txs";

		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONArray arr = new JSONArray(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		int len = arr.length();
		for (int i = 0; i < len; i++)
		{
			JSONObject obj = arr.getJSONObject(i);

			//check json param
			Assert.assertTrue(obj.has("version"));
			Assert.assertTrue(obj.has("blockhash"));
			Assert.assertTrue(obj.has("blockheight"));
			Assert.assertTrue(obj.has("fees"));
			Assert.assertTrue(obj.has("locktime"));
			Assert.assertTrue(obj.has("vin"));
			Assert.assertTrue(obj.has("vout"));
			Assert.assertTrue(obj.has("time"));
			Assert.assertTrue(obj.has("size"));
			Assert.assertTrue(obj.has("blocktime"));
			Assert.assertTrue(obj.has("data"));
		}
	}

	@Test
	void testListassets() throws Exception
	{
		System.out.println("testListassets()");
		init();

		testListassets("Thor");
	}

	void testListassets(String name) throws Exception
	{
		String addrpath = "/asset/listassets";

		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("name", name))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0)
		{
			//check json param
			Assert.assertTrue(jsonStr.has("result"));
		}
	}
}
