/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patriotcoder.automation.pihomeautomationactor;

import com.pi4j.io.gpio.Pin;
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
 * @author Jeffrey DeYoung
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
        String in = "MainAir 7 HIGH";
        ActorAbility expResult = new ActorAbility("MainAir", RaspiPin.GPIO_07, PinState.HIGH);
        ActorAbility result = ActorAbility.buildFromConfigLine(in);
        assertEquals(expResult, result);
    }

    
}
