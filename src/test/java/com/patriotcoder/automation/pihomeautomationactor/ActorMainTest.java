package com.patriotcoder.automation.pihomeautomationactor;

import com.docussandra.javasdk.testhelper.TestUtils;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.Config;
import java.io.File;
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
 * @author https://github.com/JeffreyDeYoung
 */
public class ActorMainTest
{

    public ActorMainTest()
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
     * Test of getCurrentIp method, of class ActorMain.
     */
    @Test
    public void testGetCurrentIp() throws Exception
    {
        System.out.println("getCurrentIp");
        String result = ActorMain.getCurrentIp();
        assertNotNull(result);
        assertTrue(result.contains("."));//not a great test
        assertTrue(result.length() > 8);//not a great test

    }

    /**
     * Test of generateSelfRegisterJson method, of class ActorMain.
     */
    @Test
    public void testGenerateSelfRegisterJson() throws Exception
    {
        System.out.println("generateSelfRegisterJson");
        File configFile = new File(".", "actor.config");
        Config config = Config.buildConfigFromFile(configFile);
        String result = ActorMain.generateSelfRegisterJson(config);
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
     * Test of main method, of class ActorMain.
     */
    @Test
    @Ignore
    public void testMain()
    {
        System.out.println("main");
        String[] args = null;
        ActorMain.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selfRegister method, of class ActorMain.
     */
    @Test
    public void testSelfRegister() throws Exception
    {
        System.out.println("selfRegister");
        TestUtils.establishTestServer();
        File configFile = new File(".", "actor.config");
        Config config = Config.buildConfigFromFile(configFile);
        config.setDocussandraUrl("http://localhost:19808/");
        ActorMain.selfRegister(config);
    }

}
