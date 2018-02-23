package com.patriotcoder.automation.pihomeautomationactor;

import com.ampliciti.db.docussandra.javasdk.Config;
import com.ampliciti.db.docussandra.javasdk.dao.QueryDao;
import com.ampliciti.db.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.mongodb.util.JSON;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActorConfig;
import com.patriotcoder.pihomesecurity.Main;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.docussandra.testhelpers.TestDocussandraManager;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bson.BSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeffrey DeYoung
 */
public class InitUtilsTest {

  public InitUtilsTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  /**
   * Test of generateSelfRegisterJson method, of class InitUtils.
   */
  @Test
  public void testGenerateSelfRegisterJson() throws Exception {
    System.out.println("generateSelfRegisterJson");
    File configFile = new File(".", "actor.config");
    PiActorConfig config = PiActorConfig.buildConfigFromFile(configFile);
    String result = InitUtils.generateSelfRegisterJson(config);
    assertNotNull(result);
    assertTrue(result.contains(config.getPiName()));

    for (ActorAbility ability : config.getAbilities()) {
      assertTrue(result.contains(ability.getName()));
      assertTrue(result.contains(ability.getState().toString()));
      assertTrue(result.contains(ability.getGpioPin().getAddress() + ""));
    }
    JSONParser parser = new JSONParser();
    parser.parse(result);// make sure it's valid JSON
  }

  /**
   * Test of getCurrentIp method, of class InitUtils.
   */
  @Test
  public void testGetCurrentIp() throws Exception {
    System.out.println("getCurrentIp");
    String result = InitUtils.getCurrentIp();
    assertNotNull(result);
    assertTrue(result.contains("."));// not a great test
    assertTrue(result.length() > 8);// not a great test

  }

  /**
   * Test of selfRegister method, of class InitUtils.
   */
  @Test
  public void testSelfRegister() throws Exception {
    System.out.println("selfRegister");
    // setup
    // String docussandraUrl = "http://localhost:8081/";
    String docussandraUrl = "http://localhost:19080/";
    TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
    PiHomeConfig serverConfig = new PiHomeConfig();
    serverConfig.setDocussandraUrl(docussandraUrl);
    Main.setUpDocussandra(serverConfig);
    // end server setup
    File configFile = new File(".", "actor.config");
    PiActorConfig config = PiActorConfig.buildConfigFromFile(configFile);
    config.setDocussandraUrl(docussandraUrl);
    // first register
    InitUtils.selfRegister(config);
    // Thread.sleep(20000);
    // check
    String dbName = "pihomeautomation";
    String tableName = "nodes";
    com.ampliciti.db.docussandra.javasdk.Config docussandraConfig =
        new com.ampliciti.db.docussandra.javasdk.Config(config.getDocussandraUrl());
    // search for this node to see if it exists
    QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
    Query existanceQuery = new Query();
    existanceQuery.setDatabase(dbName);
    existanceQuery.setTable(tableName);
    existanceQuery.setWhere("name = \'" + config.getPiName() + "\'");
    QueryResponseWrapper qrw = queryDao.query(existanceQuery);
    UUID updateUUID = null;
    if (!qrw.isEmpty()) {
      updateUUID = qrw.get(0).getUuid();
    }
    assertNotNull(updateUUID);
    // update register
    InitUtils.selfRegister(config);
    // check again
    qrw = queryDao.query(existanceQuery);
    UUID newUpdateUUID = null;
    if (!qrw.isEmpty()) {
      newUpdateUUID = qrw.get(0).getUuid();
    }
    assertNotNull(newUpdateUUID);
    assertEquals(updateUUID, newUpdateUUID);
  }

  /**
   * Test of createConfigFromJSON method, of class InitUtils.
   */
  @Test
  public void testCreateConfigFromJSON() throws IOException, IllegalArgumentException {
    System.out.println("createConfigFromJSON");
    String json = "{ \"running\" : true , \"abilities\" : [ { \"default_state\" : \"HIGH\" , "
        + "\"name\" : \"MainAir\" , \"state\" : \"OFF\" , \"gpio_pin\" : 7} ,"
        + " { \"default_state\" : \"HIGH\" , \"name\" : \"HouseAir\" , \"state\" : \"OFF\" , \"gpio_pin\" : 0} ,"
        + " { \"default_state\" : \"HIGH\" , \"name\" : \"EquipmentBreaker\" , \"state\" : \"OFF\" , \"gpio_pin\" : 2}"
        + " , { \"default_state\" : \"HIGH\" , \"name\" : \"AirCompressor\" , \"state\" : \"OFF\" , \"gpio_pin\" : 3}]"
        + " , \"ip\" : \"10.200.53.229\" , \"name\" : \"BarnActor\" , \"location\" : \"barn\" , \"type\" : \"actor\"}";
    BSONObject bson = (BSONObject) JSON.parse(json);

    File configFile = new File(".", "actor.config");
    PiActorConfig expected = PiActorConfig.buildConfigFromFile(configFile);
    expected.setDocussandraUrl(null);

    PiActorConfig result = InitUtils.createConfigFromJSON(bson);
    assertEquals(expected, result);
  }

  /**
   * Test of setStates method, of class InitUtils.
   */
  @Test
  public void testSetStates() throws Exception {
    System.out.println("setStates");
    File configFile = new File(".", "actor.config");
    String docussandraUrl = "http://localhost:19080/";
    PiActorConfig config = PiActorConfig.buildConfigFromFile(configFile);
    config.setDocussandraUrl(docussandraUrl);

    TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
    PiHomeConfig serverConfig = new PiHomeConfig();
    serverConfig.setDocussandraUrl(docussandraUrl);
    Main.setUpDocussandra(serverConfig);
    // end server setup

    // PiActor actor = PiActor.getPiActor(config);
    InitUtils.setStates(config, null);// run
    // check
    QueryDao queryDao = new QueryDaoImpl(new Config(docussandraUrl));
    for (ActorAbility aa : config.getAbilities()) {
      Query existanceQuery = new Query();
      existanceQuery.setDatabase(Constants.DB);
      existanceQuery.setTable(Constants.ACTOR_ABILITY_STATUS_TABLE);
      existanceQuery.setWhere("name = '" + config.getPiName() + "_" + aa.getName() + "'");
      QueryResponseWrapper qrw = queryDao.query(existanceQuery);
      assertTrue(qrw.size() == 1);
    }
  }

}
