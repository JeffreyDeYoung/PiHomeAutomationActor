package com.patriotcoder.automation.pihomeautomationactor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Actor class that manipulates the PI's GPIO ports.
 * @author Jeffrey DeYoung
 */
public class PiActor
{
    
    private static final GpioController gpio = GpioFactory.getInstance();
    
    public PiActor(Config config){
        
    }

    public static void doStuff()
    {
        GpioPinDigitalOutput relay1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, // PIN NUMBER
                "Relay 1", // PIN FRIENDLY NAME (optional)
                PinState.HIGH);      // PIN STARTUP STATE (optional)
        relay1.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

        GpioPinDigitalOutput relay2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, // PIN NUMBER
                "Relay 2", // PIN FRIENDLY NAME (optional)
                PinState.HIGH);      // PIN STARTUP STATE (optional)
        relay2.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

        GpioPinDigitalOutput relay3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, // PIN NUMBER
                "Relay 3", // PIN FRIENDLY NAME (optional)
                PinState.HIGH);      // PIN STARTUP STATE (optional)
        relay3.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

        GpioPinDigitalOutput relay4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, // PIN NUMBER
                "Relay 4", // PIN FRIENDLY NAME (optional)
                PinState.HIGH);      // PIN STARTUP STATE (optional)
        relay4.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);

        while (true)
        {
            //run till dead
            relay1.low();
            sleep();
            relay1.high();
            relay2.low();
            sleep();
            relay2.high();
            relay3.low();
            sleep();
            relay3.high();
            relay4.low();
            sleep();
            relay4.high();

            relay3.low();
            sleep();
            relay3.high();
            relay2.low();
            sleep();
            relay2.high();
        }

    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(250);
        } catch (InterruptedException e)
        {
            ;//eh
        }
    }
}
