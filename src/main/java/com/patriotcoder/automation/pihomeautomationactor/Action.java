/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patriotcoder.automation.pihomeautomationactor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jeffrey DeYoung
 */
public class Action
{
    @JsonProperty
    private String name;
    @JsonProperty
    private String state;

    public Action()
    {
    }
        
    
    public Action(String name, String state){
        this.name = name;
        this.state = state;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    @Override
    public String toString()
    {
        return "Action{" + "name=" + name + ", state=" + state + '}';
    }
    
}
