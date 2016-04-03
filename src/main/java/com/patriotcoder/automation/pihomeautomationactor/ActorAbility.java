package com.patriotcoder.automation.pihomeautomationactor;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Single specific ability of an actor.
 * @author Jeffrey DeYoung
 */
public class ActorAbility
{
    private String name;
    private Pin gpioPin;
    private PinState startAndEndState;
    
    public ActorAbility(String name, Pin gpioPin, PinState startAndEndState){
        this.name = name;
        this.gpioPin = gpioPin;
        this.startAndEndState = startAndEndState;
    }
    
    public static ActorAbility buildFromConfigLine(String in) throws IllegalArgumentException{
        String[] splits = in.split("\\s");
        String name = splits[0];
        Pin gpioPin = RaspiPin.getPinByName(splits[1]);
        PinState state = PinState.valueOf(splits[2]);
        return new ActorAbility(name, gpioPin, state);
    }
}
