package com.patriotcoder.automation.pihomeautomationactor.dataobject;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Objects;

/**
 * Single specific ability of an actor.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class ActorAbility {

  /**
   * Name of this specific ability.
   */
  private String name;
  /**
   * GPIO Pin that this is acting on.
   */
  private Pin gpioPin;
  /**
   * The state that the pin should be at the start and end state of the program. The default state.
   */
  private PinState pinState;

  /**
   * The physical (user friendly) state that is associated with the pinState.
   */
  private State state;

  /**
   * What the pin state should be if the state is "ON";
   */
  private PinState onPinState;
  /**
   * What the pin state should be if the state is "OFF";
   */
  private PinState offPinState;

  /**
   * Constructor.
   *
   * @param name Name of this specific ability.
   * @param gpioPin GPIO Pin that this is acting on.
   * @param pinState The state that the pin should be at the start and end state of the program.
   * @param state The physical (user friendly) state that is associated with the pinState.
   */
  public ActorAbility(String name, Pin gpioPin, PinState pinState, State state) {
    this.name = name;
    this.gpioPin = gpioPin;
    this.pinState = pinState;
    this.state = state;
    determineOnOffStates();
  }

  /**
   * Determines our pin states for on/off based on what is in the defaults.
   */
  private void determineOnOffStates() {
    if (state.equals(State.OFF)) {
      offPinState = pinState;
      if (getOffPinState().equals(PinState.HIGH)) {
        onPinState = PinState.LOW;
      } else {
        onPinState = PinState.HIGH;
      }
    } else { // by local rules, the default state must be on
      onPinState = pinState;
      if (getOnPinState().equals(PinState.HIGH)) {
        offPinState = PinState.LOW;
      } else {
        offPinState = PinState.HIGH;
      }
    }
  }

  /**
   * Builder method. Builds an ActorAbility based on a single line of configuration in the format
   * of: $NAME $PIN_NUM $START_AND_END_PIN_STATE
   *
   * @param in String to parse into an ActorAbility.
   * @return A brand new ActorAbility to work with.
   * @throws IllegalArgumentException If the input was not in the expected format.
   */
  public static ActorAbility buildFromConfigLine(String in) throws IllegalArgumentException {
    try {
      String[] splits = in.split("\\s");
      String name = splits[0];
      Pin gpioPin = RaspiPin.getPinByName("GPIO " + splits[1]);
      PinState pinState = PinState.valueOf(splits[2]);
      State state = State.valueOf(splits[3]);
      return new ActorAbility(name, gpioPin, pinState, state);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Input line was not in the expected"
          + " format of: '$NAME $PIN_NUM $DEFAULT_PIN_STATE $DEFAULT_STATE'. Your input was: "
          + in);
    }
  }

  /**
   * Name of this specific ability.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * GPIO Pin that this is acting on.
   *
   * @return the gpioPin
   */
  public Pin getGpioPin() {
    return gpioPin;
  }

  /**
   * The state that the pin should be at the start and end state of the program.
   *
   * @return the startAndEndState
   */
  public PinState getStartAndEndPinState() {
    return pinState;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + Objects.hashCode(this.name);
    hash = 89 * hash + Objects.hashCode(this.gpioPin);
    hash = 89 * hash + Objects.hashCode(this.pinState);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ActorAbility other = (ActorAbility) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.gpioPin, other.gpioPin)) {
      return false;
    }
    if (this.pinState != other.pinState) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ActorAbility{" + "name=" + name + ", gpioPin=" + gpioPin + ", pinState=" + pinState
        + ", state=" + state + ", onPinState=" + onPinState + ", offPinState=" + offPinState + '}';
  }


  /**
   * The physical (user friendly) state that is associated with the pinState.
   *
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * What the pin state should be if the state is "ON";
   * 
   * @return the onPinState
   */
  public PinState getOnPinState() {
    return onPinState;
  }

  /**
   * What the pin state should be if the state is "OFF";
   * 
   * @return the offPinState
   */
  public PinState getOffPinState() {
    return offPinState;
  }

}
