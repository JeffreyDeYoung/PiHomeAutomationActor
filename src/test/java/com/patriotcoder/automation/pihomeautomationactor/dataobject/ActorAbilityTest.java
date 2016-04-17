/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patriotcoder.automation.pihomeautomationactor.dataobject;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
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
public class ActorAbilityTest
{

    public ActorAbilityTest()
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
     * Test of buildFromConfigLine method, of class ActorAbility.
     */
    @Test
    public void testBuildFromConfigLine()
    {
        System.out.println("buildFromConfigLine");
        String in = "MainAir 7 HIGH OFF";
        ActorAbility expResult = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH, State.OFF);
        ActorAbility result = ActorAbility.buildFromConfigLine(in);
        assertEquals(expResult, result);
    }

    /**
     * Test default off/low.
     */
    @Test
    public void testDefaultOffLow()
    {
        System.out.println("testDefaultOffLow");
        ActorAbility object = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.LOW, State.OFF);
        assertEquals(PinState.LOW, object.getOffPinState());
        assertEquals(PinState.HIGH, object.getOnPinState());
    }

    /**
     * Test default off/high.
     */
    @Test
    public void testDefaultOffHigh()
    {
        System.out.println("testDefaultOffHigh");
        ActorAbility object = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH, State.OFF);
        assertEquals(PinState.HIGH, object.getOffPinState());
        assertEquals(PinState.LOW, object.getOnPinState());
    }

    /**
     * Test default on/high.
     */
    @Test
    public void testDefaultOnHigh()
    {
        System.out.println("testDefaultOnHigh");
        ActorAbility object = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH, State.ON);
        assertEquals(PinState.LOW, object.getOffPinState());
        assertEquals(PinState.HIGH, object.getOnPinState());
    }

    /**
     * Test default on/low.
     */
    @Test
    public void testDefaultOnLow()
    {
        System.out.println("testDefaultOnLow");
        ActorAbility object = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.LOW, State.ON);
        assertEquals(PinState.HIGH, object.getOffPinState());
        assertEquals(PinState.LOW, object.getOnPinState());
    }
}
