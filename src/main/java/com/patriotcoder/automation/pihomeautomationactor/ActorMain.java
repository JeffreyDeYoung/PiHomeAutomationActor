package com.patriotcoder.automation.pihomeautomationactor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author Jeffrey DeYoung
 */
public class ActorMain
{

    public static void main(String args)
    {
        System.out.print("Starting up Pi Actor...");
        // create gpio controller instance
        final GpioController gpio = GpioFactory.getInstance();
        // provision gpio pins #04 as an output pin and make sure is is set to LOW at startup
        GpioPinDigitalOutput relay1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, // PIN NUMBER
                "Relay 1", // PIN FRIENDLY NAME (optional)
                PinState.LOW);      // PIN STARTUP STATE (optional)
        relay1.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        relay1.high();
        while(true){
            //run till dead
        }
        
        
    }
}
