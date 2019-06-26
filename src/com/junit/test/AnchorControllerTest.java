package com.junit.test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hdac.controller.AnchorController;

class AnchorControllerTest {
	
	@InjectMocks
	private AnchorController controller;
    private MockMvc mockMvc = null;
    private String testTxid	= "ab708c0b6790a9e6f90dbadb5f8aba4c8374787f92334607d048f3e3c7e27018";
	
	void init() 
	{
		MockitoAnnotations.initMocks(this);
		
		if (this.mockMvc == null)
			this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	void testGetVerify() 
	{
		System.out.println("***** start testGetVerify");
		init();
		testVerify(testTxid);
		System.out.println("***** end testGetVerify");
	}
	
	void testVerify(String txid) 
	{
		MvcResult mvcResult = null;
		String path = "/rest/verify";
		
		try {
			mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(path).param("txid", txid)).andDo(print()).andReturn();
			Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject resultJson = new JSONObject(mvcResult.getModelAndView().getModelMap().get("jsonStr").toString());
		
		Assert.assertTrue(resultJson.has("mainData"));
		Assert.assertTrue(resultJson.has("matchingPublicTx"));
		Assert.assertTrue(resultJson.has("libHash"));
		Assert.assertTrue(resultJson.has("tokenQty"));
		Assert.assertTrue(resultJson.has("sideBlockHeight"));
		Assert.assertTrue(resultJson.has("publicAddress"));
		Assert.assertTrue(resultJson.has("matchingResult"));
		Assert.assertTrue(resultJson.has("sideHashData"));
	}

}
