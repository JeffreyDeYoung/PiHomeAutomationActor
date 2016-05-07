package com.patriotcoder.automation.pihomeautomationactor.dataobject;

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
public class PiActorConfig
{

    /**
     * Name for this Pi Actor.
     */
    private String piName;

    /**
     * Location for this Pi Actor. This should be a short string that describes
     * the geographical (physical) area that this Pi assists in managing.
     */
    private String location;

    /**
     * URL for the Docussandra server.
     */
    private String docussandraUrl;

    /**
     * List of abilities for this Pi Actor.
     */
    private ArrayList<ActorAbility> abilities;

    /**
     * Constructor.
     *
     * @param piName Name of this Pi.
     * @param location Geographical (physical) location of this pi.
     * @param docussandraUrl URL for the central Docussandra server.
     * @param abilities List of abilities for this Pi.
     */
    public PiActorConfig(String piName, String location, String docussandraUrl, ArrayList<ActorAbility> abilities)
    {
        this.piName = piName;
        if (location != null)
        {
            this.location = location.toLowerCase();
        }
        this.abilities = abilities;
        this.docussandraUrl = docussandraUrl;
    }

    /**
     * Builds a config object from a config String. String should be in the
     * format of:" $NAME_OF_ACTOR $Ability_Config_from_ActorAbility. ... Line
     * comments (#) are allowed. (Must be the first character of the line.) "
     *
     * @param in String to parse into config object.
     * @return A new Config object.
     * @throws IllegalArgumentException If the input was not in the expected
     * format.
     */
    public static PiActorConfig buildConfigFromString(String in) throws IllegalArgumentException
    {
        try
        {
            String[] splits = in.split("\n");
            ArrayList<String> splitsList = new ArrayList<>(splits.length);
            for (String line : splits)
            {
                if (!line.startsWith("#"))
                {//ignore comments
                    splitsList.add(line);
                }
            }
            String name = splitsList.get(0);
            String location = splitsList.get(1);
            String docussandraUrl = splitsList.get(2);
            ArrayList<ActorAbility> abilities = new ArrayList<>(splitsList.size() - 3);
            for (int i = 3; i < splitsList.size(); i++)
            {
                abilities.add(ActorAbility.buildFromConfigLine(splitsList.get(i)));
            }
            return new PiActorConfig(name, location, docussandraUrl, abilities);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Input config string was not in the expected format. Your input was: \n" + in);

        }
    }

    /**
     * Builds a config object from a config File. File should be in the format
     * of:" $NAME_OF_ACTOR $Ability_Config_from_ActorAbility. ... Line comments
     * (#) are allowed. (Must be the first character of the line.) "
     *
     * @param configFile File to parse into config object.
     * @return A new Config object.
     * @throws IllegalArgumentException If the input was not in the expected
     * format.
     * @throws IOException If the file can't be read.
     */
    public static PiActorConfig buildConfigFromFile(File configFile) throws IllegalArgumentException, IOException
    {
        String in = new String(Files.readAllBytes(configFile.toPath()));
        return PiActorConfig.buildConfigFromString(in);
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
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.piName);
        hash = 29 * hash + Objects.hashCode(this.location);
        hash = 29 * hash + Objects.hashCode(this.docussandraUrl);
        hash = 29 * hash + Objects.hashCode(this.abilities);
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
        final PiActorConfig other = (PiActorConfig) obj;
        if (!Objects.equals(this.piName, other.piName))
        {
            return false;
        }
        if (!Objects.equals(this.location, other.location))
        {
            return false;
        }
        if (!Objects.equals(this.docussandraUrl, other.docussandraUrl))
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
        return "PiActorConfig{" + "piName=" + piName + ", location=" + location + ", docussandraUrl=" + docussandraUrl + ", abilities=" + abilities + '}';
    }

 
    /**
     * URL for the Docussandra server.
     *
     * @return the docussandraUrl
     */
    public String getDocussandraUrl()
    {
        return docussandraUrl;
    }

    /**
     * URL for the Docussandra server.
     *
     * @param docussandraUrl the docussandraUrl to set
     */
    public void setDocussandraUrl(String docussandraUrl)
    {
        this.docussandraUrl = docussandraUrl;
    }

    /**
     * Location for this Pi Actor. This should be a short string that describes
     * the geographical area that this Pi assists in managing.
     *
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

}
