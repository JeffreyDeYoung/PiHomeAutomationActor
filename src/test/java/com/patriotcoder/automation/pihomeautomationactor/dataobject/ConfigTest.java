package com.patriotcoder.automation.pihomeautomationactor.dataobject;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class ConfigTest
{

    public ConfigTest()
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
     * Test of buildConfigFromString method, of class Config.
     */
    @Test
    public void testBuildConfigFromString()
    {
        System.out.println("buildConfigFromString");
        String in = "#This is a sample configuration file.\n"
                + "#Name of this actor\n"
                + "BarnActor\n"
                + "#Docussandra URL\n"
                + "http://10.0.0.1\n"
                + "#Start of Ability Descriptions\n"
                + "MainAir 7 HIGH OFF\n"
                + "HouseAir 8 HIGH OFF\n"
                + "EquipmentBreaker 9 HIGH OFF\n"
                + "AirCompressor 10 HIGH OFF";
        ArrayList<ActorAbility> abilities = new ArrayList<>(4);
        abilities.add(new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("HouseAir", RaspiPin.GPIO_08, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("EquipmentBreaker", RaspiPin.GPIO_09, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("AirCompressor", RaspiPin.GPIO_10, PinState.HIGH, State.OFF));
        String expectedName = "BarnActor";
        Config expResult = new Config(expectedName, "http://10.0.0.1", abilities);
        Config result = Config.buildConfigFromString(in);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildConfigFromFile method, of class Config.
     */
    @Test
    public void testBuildConfigFromFile() throws Exception
    {
        System.out.println("buildConfigFromFile");
        ArrayList<ActorAbility> abilities = new ArrayList<>(4);
        abilities.add(new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("HouseAir", RaspiPin.GPIO_00, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("EquipmentBreaker", RaspiPin.GPIO_02, PinState.HIGH, State.OFF));
        abilities.add(new ActorAbility("AirCompressor", RaspiPin.GPIO_03, PinState.HIGH, State.OFF));
        String expectedName = "BarnActor";
        Config expResult = new Config(expectedName, "http://localhost:8081", abilities);
        File configFile = new File(".", "actor.config");
        Config result = Config.buildConfigFromFile(configFile);
        assertEquals(expResult, result);

    }

}
