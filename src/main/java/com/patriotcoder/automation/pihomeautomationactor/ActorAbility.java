package com.patriotcoder.automation.pihomeautomationactor;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Objects;

/**
 * Single specific ability of an actor.
 *
 * @author Jeffrey DeYoung
 */
public class ActorAbility
{

    /**
     * Name of this specific ability.
     */
    private String name;
    /**
     * GPIO Pin that this is acting on.
     */
    private Pin gpioPin;
    /**
     * The state that the pin should be at the start and end state of the
     * program.
     */
    private PinState startAndEndState;

    /**
     * Constructor.
     *
     * @param name Name of this specific ability.
     * @param gpioPin GPIO Pin that this is acting on.
     * @param startAndEndState The state that the pin should be at the start and
     * end state of the program.
     */
    public ActorAbility(String name, Pin gpioPin, PinState startAndEndState)
    {
        this.name = name;
        this.gpioPin = gpioPin;
        this.startAndEndState = startAndEndState;
    }

    /**
     * Builder method. Builds an ActorAbility based on a single line of
     * configuration in the format of: $NAME $PIN_NUM $START_AND_END_PIN_STATE
     *
     * @param in String to parse into an ActorAbility.
     * @return A brand new ActorAbility to work with.
     * @throws IllegalArgumentException If the input was not in the expected format.
     */
    public static ActorAbility buildFromConfigLine(String in) throws IllegalArgumentException
    {
        try
        {
            String[] splits = in.split("\\s");
            String name = splits[0];
            Pin gpioPin = RaspiPin.getPinByName("GPIO " + splits[1]);
            PinState state = PinState.valueOf(splits[2]);
            return new ActorAbility(name, gpioPin, state);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Input line was not in the expected"
                    + " format of: '$NAME $PIN_NUM $START_AND_END_PIN_STATE'. Your input was: " + in);
        }
    }

    /**
     * Name of this specific ability.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * GPIO Pin that this is acting on.
     * @return the gpioPin
     */
    public Pin getGpioPin()
    {
        return gpioPin;
    }

    /**
     * The state that the pin should be at the start and end state of the
     * program.
     * @return the startAndEndState
     */
    public PinState getStartAndEndState()
    {
        return startAndEndState;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.gpioPin);
        hash = 89 * hash + Objects.hashCode(this.startAndEndState);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ActorAbility other = (ActorAbility) obj;
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.gpioPin, other.gpioPin))
        {
            return false;
        }
        if (this.startAndEndState != other.startAndEndState)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "ActorAbility{" + "name=" + name + ", gpioPin=" + gpioPin + ", startAndEndState=" + startAndEndState + '}';
    }
    
    
}
