package com.emc.settlement.backend.fileupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.TestConfig;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
public abstract class BaseTest {

	@Autowired
	protected UtilityFunctions utilityFunctions;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	public void verifyJamMessagesEntry(String eveId, String exceptionMessage) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM jam_messages WHERE Eve_id =?", eveId);
		assertNotNull(list);
		assertTrue(list.size()>0);
		assertNotNull(list.get(0));
		assertTrue(list.get(0).size()>0);
		assertEquals(exceptionMessage, list.get(0).get("TEXT"));
		list.clear();
	}

	public void verifyJamEventsEntry(String eveId) {
		List<Map<String, Object>> list;

		list = jdbcTemplate.queryForList("SELECT * FROM JAM_EVENTS WHERE id = ?", eveId);
		assertNotNull(list);
		assertTrue(list.size()>0);
		list.clear();
	}

	public void verifyNemEbtEventsEntry(String ebtEventsRowId) {
		List<Map<String, Object>> list;

		list = jdbcTemplate.queryForList("SELECT * FROM nem_ebt_events WHERE id = ?", ebtEventsRowId);
		assertNotNull(list);
		assertTrue(list.size()>0);
		list.clear();
	}

	public void verifyNEMSettlementRawFiles(String ebtEventsRowId) {
		List<Map<String, Object>> list;

		list = jdbcTemplate.queryForList("SELECT * FROM NEM_SETTLEMENT_RAW_FILES WHERE EBE_ID = ?", ebtEventsRowId);
		assertNotNull(list);
		assertTrue(list.size()>0);
		list.clear();
	}
}
