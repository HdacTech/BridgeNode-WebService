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
import com.hdac.controller.ApiController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/webapps/WEB-INF/servlet-context.xml")
@WebAppConfiguration
class ApiControllerTest {

	@InjectMocks
	private ApiController controller;
    private MockMvc mockMvc = null;
    private String addressParam	= "HJXXXXXXXXXXXXXXXXXXXXXXXXXXVarS5i";
    private String addressesParam = "HJXXXXXXXXXXXXXXXXXXXXXXXXXXVarS5i";
    private String rawtxParam = "0100000001d292533524bd86d4585124b182eb2fd1cf96588714025ed9d2a704dd46d07ba4000000006a473044022071be86d91083b4ed79064a8310f8066d31a811f94f702179d83a3157e9c938e10220742f65be7976468220beefe72957694cf6ecd2cd0713a62176f84414d4ebc838012103a65ef4a7bab487866b83617b45a239a8dcc30e7b91ba1c1796f4cf3679d1052effffffff020088526a740000001976a9140f03bf81468d678ce10e6dc2c498bb78c24b016f88ac00000000000000001976a914659eab5b905707209b0ab92eb948b0da4dbc521288ac00000000";
	private String regex = "([0-9]*\\.[0-9]{0,8}|[0-9]+)";
	private String regexSatoshi = "[0-9]+";


    void init() {
		System.out.println("init");
		MockitoAnnotations.initMocks(this);
		
		if (this.mockMvc == null)
			this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
//    @Before
//    void setup(WebApplicationContext wac) {
//		System.out.println("@Before setup");
//		MockitoAnnotations.initMocks(this);
//    	mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }    
    
	@Test
	final void testGetAddress() throws Exception {
		System.out.println("testGetAddress()");
		init();
		
		testGetAddress(addressParam, "public");
		testGetAddress(addressParam, "private");
	}

	void testGetAddress(String address, String path) throws Exception {
		System.out.println("testGetAddress() path : " + path);
		
		String addrpath = "/addr/" + address;
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("path", path))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());
        

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0) {
		
			System.out.println("result : " + jsonStr);
			
//			String regex = "([0-9]*\\.[0-9]{0,8}|[0-9]+)";
//			
//			String a = "0.00000001";
//			String b = "999999";
//			String c = "0.000000011";
//			
//			Assert.assertTrue(Pattern.matches(regex, a));
//			Assert.assertTrue(Pattern.matches(regex, b));
//			Assert.assertTrue(Pattern.matches(regex, c));
			
			//check json param
			Assert.assertTrue(jsonStr.has("balance"));
			Assert.assertTrue(Pattern.matches(regex, StringUtil.nvl(jsonStr.get("balance"))));
			
			Assert.assertTrue(jsonStr.has("balanceSat"));
			Assert.assertTrue(Pattern.matches(regexSatoshi, StringUtil.nvl(jsonStr.get("balanceSat"))));
			
			Assert.assertTrue(jsonStr.has("addrStr"));
			Assert.assertTrue(jsonStr.has("transactions"));
			Assert.assertTrue(jsonStr.has("txApperances"));
		}
	}
	
	@Test
	final void testGetUtxos() throws Exception {
		System.out.println("testGetUtxos()");
		init();
		testGetUtxos(addressesParam, "public");
		testGetUtxos(addressesParam, "private");
	}
	
	void testGetUtxos(String addresses, String path) throws Exception {
		System.out.println("testGetUtxos() path : " + path);
		
		String addrpath = "/addrs/" + addresses + "/utxo";
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("path", path))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());
        

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONArray jsonStr = new JSONArray(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0) {
			System.out.println("result : " + jsonStr);
			
			JSONObject result = jsonStr.getJSONObject(0);

			//check json param
			Assert.assertTrue(result.has("scriptPubKey"));
			Assert.assertTrue(result.has("address"));
			Assert.assertTrue(result.has("txid"));
			Assert.assertTrue(result.has("confirmations"));
			Assert.assertTrue(result.has("unspent_hash"));
			Assert.assertTrue(result.has("vout"));
			Assert.assertTrue(result.has("amount"));
			Assert.assertTrue(result.has("satoshis"));
			Assert.assertTrue(Pattern.matches(regex, StringUtil.nvl(result.get("amount"))));
			Assert.assertTrue(Pattern.matches(regexSatoshi, StringUtil.nvl(result.get("satoshis"))));
		}
	}

	@Test
	final void testGetMultiaddr() throws Exception {
		System.out.println("testGetMultiaddr()");
		init();
		
		testGetMultiaddr(addressesParam, "public", "txs");
		testGetMultiaddr(addressesParam, "public", "statics");
		testGetMultiaddr(addressesParam, "private", "txs");		
		testGetMultiaddr(addressesParam, "private", "statics");
	}
	
	final void testGetMultiaddr(String addresses, String path, String subpath) throws Exception {
		System.out.println("testGetMultiaddr() path : " + path);
		
		String addrpath = "/addrs/" + addresses + "/" + subpath;
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(addrpath).param("path", path))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

        Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());
        

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0) {
			System.out.println("result : " + jsonStr);
			
			//check json param
			Assert.assertTrue(jsonStr.has("totalItems"));
			Assert.assertTrue(jsonStr.has("from"));
			Assert.assertTrue(jsonStr.has("to"));
			Assert.assertTrue(jsonStr.has("items"));
			
			JSONArray arrTxs = jsonStr.getJSONArray("items");
			if (arrTxs.length() > 0) {
				JSONObject txdata = arrTxs.getJSONObject(0);
				System.out.println("txdata : " + txdata);

				//check json param
				Assert.assertTrue(txdata.has("version"));
				Assert.assertTrue(txdata.has("blockhash"));
				Assert.assertTrue(txdata.has("blockheight"));
				Assert.assertTrue(txdata.has("fees"));
				Assert.assertTrue(txdata.has("locktime"));
				Assert.assertTrue(txdata.has("vin"));
				Assert.assertTrue(txdata.has("vout"));
				Assert.assertTrue(txdata.has("time"));
				Assert.assertTrue(txdata.has("size"));
				Assert.assertTrue(txdata.has("blocktime"));
				Assert.assertTrue(txdata.has("data"));
				
			}
		}		
	}

	@Test
	final void testGetPushtx() throws Exception {
		System.out.println("testGetPushtx()");
		init();
		
		testGetPushtx(rawtxParam, "public");
		testGetPushtx(rawtxParam, "private");
	}
	final void testGetPushtx(String rawtx, String path) throws Exception {
		System.out.println("testGetPushtx() path : " + path);
		
		String addrpath = "/tx/send";
		
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(addrpath).param("rawtx", rawtx).param("path", path))
				.andDo(print()) //if you don't want to see data, block this line
		        .andReturn();

		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),
        		mvcResult.getResponse()
                .getStatus());
        

		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0) {
			System.out.println("result : " + jsonStr);
			
			Assert.assertTrue(jsonStr.has("code"));
			Assert.assertTrue(jsonStr.has("message"));
		}
	}
	
	@Test
	final void testgetInfo() throws Exception {
		init();
		
		testgetInfo("public");
		testgetInfo("private");
	}
	
	final void testgetInfo(String path) throws Exception {

		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/getinfo").param("path", path))
				.andDo(print())
		        .andReturn();

//		System.out.println("mvcResult.getModelAndView().getModelMap() : " + mvcResult.getModelAndView().getModelMap());

		//check jsonrpc return status
		Assert.assertEquals(HttpStatus.OK.value(),
        		mvcResult.getResponse()
                .getStatus());

		//get json data
		JSONObject jsonStr = new JSONObject(StringUtil.nvl(mvcResult.getModelAndView().getModelMap().get("jsonStr")));
		if (jsonStr.length() > 0) {
			JSONObject result = jsonStr.getJSONObject("result");
			System.out.println("result : " + result);
			
			//check json param
			Assert.assertTrue(result.has("version"));
			Assert.assertTrue(result.has("protocolversion"));
			Assert.assertTrue(result.has("chainname"));
			Assert.assertTrue(result.has("protocol"));
			Assert.assertTrue(result.has("nodeaddress"));
			Assert.assertTrue(result.has("burnaddress"));
			Assert.assertTrue(result.has("balance"));
			Assert.assertTrue(result.has("reindex"));
			Assert.assertTrue(result.has("blocks"));
			Assert.assertTrue(result.has("chain-blocks"));
			Assert.assertTrue(result.has("difficulty"));
			Assert.assertTrue(result.has("relayfee"));
			Assert.assertTrue(result.has("connections"));
			Assert.assertTrue(result.has("keypoololdest"));
			Assert.assertTrue(result.has("keypoolsize"));
			Assert.assertTrue(result.has("testnet"));
		}
	}	
}
