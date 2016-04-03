/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patriotcoder.automation.pihomeautomationactor;

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
 * @author Jeffrey DeYoung
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
                + "#Start of Ability Descriptions\n"
                + "MainAir 7 HIGH\n"
                + "HouseAir 8 HIGH\n"
                + "EquipmentBreaker 9 HIGH\n"
                + "AirCompressor 10 HIGH";
        ArrayList<ActorAbility> abilities = new ArrayList<>(4);
        abilities.add(new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH));
        abilities.add(new ActorAbility("HouseAir", RaspiPin.GPIO_08, PinState.HIGH));
        abilities.add(new ActorAbility("EquipmentBreaker", RaspiPin.GPIO_09, PinState.HIGH));
        abilities.add(new ActorAbility("AirCompressor", RaspiPin.GPIO_10, PinState.HIGH));
        String expectedName = "BarnActor";
        Config expResult = new Config(expectedName, abilities);
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
        abilities.add(new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH));
        abilities.add(new ActorAbility("HouseAir", RaspiPin.GPIO_08, PinState.HIGH));
        abilities.add(new ActorAbility("EquipmentBreaker", RaspiPin.GPIO_09, PinState.HIGH));
        abilities.add(new ActorAbility("AirCompressor", RaspiPin.GPIO_10, PinState.HIGH));
        String expectedName = "BarnActor";
        Config expResult = new Config(expectedName, abilities);
        File configFile = new File(".", "actor.config");
        Config result = Config.buildConfigFromFile(configFile);
        assertEquals(expResult, result);

    }

}
