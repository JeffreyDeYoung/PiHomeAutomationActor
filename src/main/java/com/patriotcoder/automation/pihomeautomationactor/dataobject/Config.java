package com.patriotcoder.automation.pihomeautomationactor.dataobject;

import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Configuration class for this application. Describes what this Pi actor does
 * and how to use it.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class Config
{

    /**
     * Name for this Pi Actor.
     */
    private String piName;

    /**
     * List of abilities for this Pi Actor.
     */
    private ArrayList<ActorAbility> abilities;

    /**
     * Constructor.
     *
     * @param piName Name of this Pi.
     * @param abilities List of abilities for this Pi.
     */
    public Config(String piName, ArrayList<ActorAbility> abilities)
    {
        this.piName = piName;
        this.abilities = abilities;
    }

    /**
     * Builds a config object from a config String. String should be in the
     * format of:" 
     * $NAME_OF_ACTOR
     * $Ability_Config_from_ActorAbility. 
     * ...
     * Line comments (#) are allowed. (Must be the first character of the line.)
     *"
     * @param in String to parse into config object.
     * @return A new Config object.
     * @throws IllegalArgumentException If the input was not in the expected
     * format.
     */
    public static Config buildConfigFromString(String in) throws IllegalArgumentException
    {
        try
        {
            String[] spilts = in.split("\n");
            int indexStart = 0;
            while(spilts[indexStart].startsWith("#")){//ignore heading comments
                indexStart++;
            }
            String name = spilts[indexStart];
            indexStart++;
            ArrayList<ActorAbility> abilities = new ArrayList<>(spilts.length - 1);
            for (int i = indexStart; i < spilts.length; i++)
            {
                if (!spilts[i].startsWith("#"))//if its not a comment
                {
                    abilities.add(ActorAbility.buildFromConfigLine(spilts[i]));
                }
            }
            return new Config(name, abilities);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Input config string was not in the expected format. Your input was: \n" + in);

        }
    }

    /**
     * Builds a config object from a config File. File should be in the format
     * of:" 
     * $NAME_OF_ACTOR
     * $Ability_Config_from_ActorAbility. 
     * ...
     * Line comments (#) are allowed. (Must be the first character of the line.)
     *"
     * @param configFile File to parse into config object.
     * @return A new Config object.
     * @throws IllegalArgumentException If the input was not in the expected
     * format.
     * @throws IOException If the file can't be read.
     */
    public static Config buildConfigFromFile(File configFile) throws IllegalArgumentException, IOException
    {
        String in = new String(Files.readAllBytes(configFile.toPath()));
        return Config.buildConfigFromString(in);
    }

    /**
     * Name for this Pi Actor.
     *
     * @return the piName
     */
    public String getPiName()
    {
        return piName;
    }

    /**
     * List of abilities for this Pi Actor.
     *
     * @return the abilities
     */
    public ArrayList<ActorAbility> getAbilities()
    {
        return abilities;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.piName);
        hash = 23 * hash + Objects.hashCode(this.abilities);
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
        final Config other = (Config) obj;
        if (!Objects.equals(this.piName, other.piName))
        {
            return false;
        }
        if (!Objects.equals(this.abilities, other.abilities))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Config{" + "piName=" + piName + ", abilities=" + abilities + '}';
    }

}
