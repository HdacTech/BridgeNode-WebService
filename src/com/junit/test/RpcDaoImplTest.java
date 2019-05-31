package com.junit.test;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hdac.common.BeanUtil;
import com.hdac.controller.AssetApiController;
import com.hdac.dao.rpc.RpcDao;
import com.hdac.dao.rpc.RpcDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/webapps/WEB-INF/servlet-context.xml")
@WebAppConfiguration
class RpcDaoImplTest
{
	@InjectMocks
	private AssetApiController controller;
    private MockMvc mockMvc = null;
    private String reg1 = "([0-9]*\\.[0-9]{0,8}|[0-9]+)";

    void init()
    {
		System.out.println("init");
		MockitoAnnotations.initMocks(this);

		if (this.mockMvc == null)
			this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	void testGetMultiBalance() throws Exception
	{
		System.out.println("testGetMultiBalance()");
		init();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("address", "HMe7prd9ceJyVc6ETi54UVv9ptaUrQf28y");
		paramMap.put("asset", "Thor");

		testGetMultiBalance(paramMap);
	}

	void testGetMultiBalance(Map<String, Object> paramMap) throws Exception
	{
		RpcDao rpcdao = (RpcDao)BeanUtil.getBean(RpcDaoImpl.class);
		BigDecimal balance = rpcdao.getMultiBalance(paramMap);

        Assert.assertTrue(Pattern.matches(reg1, String.valueOf(balance)));
	}

	@Test
	void testGetMultiBalances() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetFilterListAddresses() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetAssetTxList() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetAssetUtxos() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetAssetTxs() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetUtxosNew() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetTxsNew() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetBlock() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetAddressBalance() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetAddressList() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testAssetIssue() {
		fail("Not yet implemented"); // TODO
	}

}
