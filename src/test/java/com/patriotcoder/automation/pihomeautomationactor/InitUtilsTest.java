package com.patriotcoder.automation.pihomeautomationactor;

import com.docussandra.javasdk.dao.QueryDao;
import com.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.docussandra.testhelpers.TestDocussandraManager;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActorConfig;
import com.patriotcoder.pihomesecurity.Main;
import com.patriotcoder.pihomesecurity.dataobjects.PiHomeConfig;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import java.io.File;
import java.util.UUID;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Jeffrey DeYoung
 */
public class InitUtilsTest
{
    
    public InitUtilsTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

/**
     * Test of generateSelfRegisterJson method, of class InitUtils.
     */
    @Test
    public void testGenerateSelfRegisterJson() throws Exception
    {
        System.out.println("generateSelfRegisterJson");
        File configFile = new File(".", "actor.config");
        PiActorConfig config = PiActorConfig.buildConfigFromFile(configFile);
        String result = InitUtils.generateSelfRegisterJson(config);
        assertNotNull(result);
        assertTrue(result.contains(config.getPiName()));

        for (ActorAbility ability : config.getAbilities())
        {
            assertTrue(result.contains(ability.getName()));
            assertTrue(result.contains(ability.getState().toString()));
            assertTrue(result.contains(ability.getGpioPin().getAddress() + ""));
        }
        JSONParser parser = new JSONParser();
        parser.parse(result);//make sure it's valid JSON
    }

    /**
     * Test of getCurrentIp method, of class InitUtils.
     */

    @Test
    public void testGetCurrentIp() throws Exception
    {
        System.out.println("getCurrentIp");
        String result = InitUtils.getCurrentIp();
        assertNotNull(result);
        assertTrue(result.contains("."));//not a great test
        assertTrue(result.length() > 8);//not a great test

    }

    /**
     * Test of selfRegister method, of class InitUtils.
     */
    @Test
    @Ignore("Something isn't right here; need to investigate.")
    public void testSelfRegister() throws Exception
    {
        System.out.println("selfRegister");
        //setup
        String docussandraUrl = "http://localhost:19080/";
        TestDocussandraManager.getManager().ensureTestDocussandraRunning(true);
        PiHomeConfig serverConfig = new PiHomeConfig();
        serverConfig.setDocussandraUrl(docussandraUrl);
        Main.setUpDocussandra(serverConfig);
        //end server setup
        File configFile = new File(".", "actor.config");
        PiActorConfig config = PiActorConfig.buildConfigFromFile(configFile);
        config.setDocussandraUrl(docussandraUrl);
        //first register
        InitUtils.selfRegister(config);
        //Thread.sleep(20000);
        //check
        String dbName = "pihomeautomation";
        String tableName = "nodes";
        com.docussandra.javasdk.Config docussandraConfig = new com.docussandra.javasdk.Config(config.getDocussandraUrl());
        //search for this node to see if it exists
        QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
        Query existanceQuery = new Query();
        existanceQuery.setDatabase(dbName);
        existanceQuery.setTable(tableName);
        existanceQuery.setWhere("name = \'" + config.getPiName() + "\'");
        QueryResponseWrapper qrw = queryDao.query(dbName, existanceQuery);
        UUID updateUUID = null;
        if (!qrw.isEmpty())
        {
            updateUUID = qrw.get(0).getUuid();
        }
        assertNotNull(updateUUID);
        //update register
        InitUtils.selfRegister(config);
        //check again
        qrw = queryDao.query(dbName, existanceQuery);
        UUID newUpdateUUID = null;
        if (!qrw.isEmpty())
        {
            updateUUID = qrw.get(0).getUuid();
        }
        assertNotNull(newUpdateUUID);
        assertEquals(updateUUID, newUpdateUUID);
    }
    
}
