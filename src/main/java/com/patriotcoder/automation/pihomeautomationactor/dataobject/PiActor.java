package com.patriotcoder.automation.pihomeautomationactor.dataobject;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor class that manipulates the PI's GPIO ports.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class PiActor
{

    private static final Logger logger = LoggerFactory.getLogger(PiActor.class);
    private static PiActor instance;
    private static final GpioController gpio = GpioFactory.getInstance();
    private static HashMap<String, GpioPinDigitalOutput> outputs;
    private static HashMap<String, ActorAbility> actorMap;

    /**
     * Constructor. Private for singleton.
     *
     * @param config Config object for the Actor.
     */
    private PiActor(PiActorConfig config)
    {
        logger.debug("Initalizing PI actor.");
        outputs = new HashMap<>(config.getAbilities().size());
        actorMap = new HashMap<>(config.getAbilities().size());
        for (ActorAbility aa : config.getAbilities())
        {
            GpioPinDigitalOutput output = gpio.provisionDigitalOutputPin(aa.getGpioPin(), aa.getName(), aa.getStartAndEndPinState());
            output.setShutdownOptions(true, aa.getStartAndEndPinState(), PinPullResistance.OFF);
            outputs.put(aa.getName(), output);
            actorMap.put(aa.getName(), aa);
        }
        logger.debug("Done initalizing PI actor.");
    }

    public void performAction(Action action) throws IllegalArgumentException
    {
        GpioPinDigitalOutput output = outputs.get(action.getName());
        ActorAbility aa = actorMap.get(action.getName());
        logger.debug("Performing action: " + action.toString() + " on " + output.toString());

        if (output == null)
        {
            throw new IllegalArgumentException("This is not a vaild action for this device.");
        }
        if (action.getState().equalsIgnoreCase("ON"))
        {
            //output.low();
            output.setState(aa.getOnPinState());
        } else if (action.getState().equalsIgnoreCase("OFF"))
        {
            //output.high();
            output.setState(aa.getOffPinState());
        } else
        {
            throw new IllegalArgumentException("This is not a vaild state: " + action.getState());
        }
    }

    /**
     * Builder method.
     *
     * @param config Config Object for this Actor.
     * @return a ready to use PiActor object.
     */
    public static PiActor getPiActor(PiActorConfig config)
    {
        if (instance == null)
        {
            instance = new PiActor(config);
        }
        return instance;
    }

//old test code
//    public static void doStuff()
//    {
//        GpioPinDigitalOutput relay1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, // PIN NUMBER
//                "Relay 1", // PIN FRIENDLY NAME (optional)
//                PinState.HIGH);      // PIN STARTUP STATE (optional)
//        relay1.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
//
//        GpioPinDigitalOutput relay2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, // PIN NUMBER
//                "Relay 2", // PIN FRIENDLY NAME (optional)
//                PinState.HIGH);      // PIN STARTUP STATE (optional)
//        relay2.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
//
//        GpioPinDigitalOutput relay3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, // PIN NUMBER
//                "Relay 3", // PIN FRIENDLY NAME (optional)
//                PinState.HIGH);      // PIN STARTUP STATE (optional)
//        relay3.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
//
//        GpioPinDigitalOutput relay4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, // PIN NUMBER
//                "Relay 4", // PIN FRIENDLY NAME (optional)
//                PinState.HIGH);      // PIN STARTUP STATE (optional)
//        relay4.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
//
//        while (true)
//        {
//            //run till dead
//            relay1.low();
//            sleep();
//            relay1.high();
//            relay2.low();
//            sleep();
//            relay2.high();
//            relay3.low();
//            sleep();
//            relay3.high();
//            relay4.low();
//            sleep();
//            relay4.high();
//
//            relay3.low();
//            sleep();
//            relay3.high();
//            relay2.low();
//            sleep();
//            relay2.high();
//        }
//
//    }
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
