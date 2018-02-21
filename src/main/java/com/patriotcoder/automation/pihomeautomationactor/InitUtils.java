package com.patriotcoder.automation.pihomeautomationactor;

import com.ampliciti.db.docussandra.javasdk.Config;
import com.ampliciti.db.docussandra.javasdk.SDKUtils;
import com.ampliciti.db.docussandra.javasdk.dao.DocumentDao;
import com.ampliciti.db.docussandra.javasdk.dao.QueryDao;
import com.ampliciti.db.docussandra.javasdk.dao.impl.DocumentDaoImpl;
import com.ampliciti.db.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.ampliciti.db.docussandra.javasdk.exceptions.RESTException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.util.JSON;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.Action;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActor;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActorConfig;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.State;
import com.pearson.docussandra.domain.objects.Database;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import com.pearson.docussandra.domain.objects.Table;
import com.pearson.docussandra.exception.IndexParseException;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;
import org.bson.BSONObject;
import org.bson.types.BasicBSONList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Class that performs some initial startup tasks for this application.
 *
 * @author Jeffrey DeYoung
 */
public class InitUtils
{
    
    private PiActorConfig config;
    
    public InitUtils(PiActorConfig config)
    {
        this.config = config;
    }

    /**
     * Generates the JSON needed to self register a node.
     *
     * @param config Configuration for this node.
     * @return A string of the self-registry JSON.
     */
    public static String generateSelfRegisterJson(PiActorConfig config) throws UnknownHostException
    {
        JSONObject selfRegister = new JSONObject();
        selfRegister.put("name", config.getPiName());
        selfRegister.put("ip", getCurrentIp());
        selfRegister.put("running", true);
        selfRegister.put("location", config.getLocation());
        selfRegister.put("type", "actor");
        JSONArray abilityArray = new JSONArray();
        for (ActorAbility ability : config.getAbilities())
        {
            JSONObject abilityJsonObject = new JSONObject();
            abilityJsonObject.put("name", ability.getName());
            abilityJsonObject.put("gpio_pin", ability.getGpioPin().getAddress());
            abilityJsonObject.put("default_state", ability.getStartAndEndPinState().toString());
            abilityJsonObject.put("state", ability.getState().toString());
            abilityArray.add(abilityJsonObject);
        }
        selfRegister.put("abilities", abilityArray);
        return selfRegister.toJSONString();
    }

    /**
     * Creates a PiActorConfig based on a BSON object. Used for parsing the
     * response from a call to the server.
     *
     * @param bson BSONOject that contains the data for an actor config.
     * @return PiActorConfig based on the BSON.
     */
    public static PiActorConfig createConfigFromJSON(BSONObject bson)
    {
        String name = (String) bson.get("name");
        String location = (String) bson.get("location");
        ArrayList<ActorAbility> abilities = new ArrayList<>();
        BasicBSONList abilityBson = (BasicBSONList) bson.get("abilities");
        for (Object ability : abilityBson)
        {
            BSONObject bsonAbility = (BSONObject) ability;
            String abilityName = (String) bsonAbility.get("name");
            int pinNum = (Integer) bsonAbility.get("gpio_pin");
            Pin pin = RaspiPin.getPinByName("GPIO " + pinNum);
            PinState pinState;
            String pinStateString = (String) bsonAbility.get("default_state");
            if (pinStateString.equalsIgnoreCase("LOW"))
            {
                pinState = PinState.LOW;
            } else
            {
                pinState = PinState.HIGH;
            }
            State state = State.valueOf((String) bsonAbility.get("state"));
            ActorAbility aa = new ActorAbility(abilityName, pin, pinState, state);
            abilities.add(aa);
        }
        PiActorConfig toReturn = new PiActorConfig(name, location, null, abilities);
        return toReturn;
    }

    /**
     * Performs a self registration in Docussandra. Will update the registration
     * if it already exists.
     *
     * @param config Config to register with.
     * @throws RESTException
     * @throws ParseException
     * @throws IOException
     * @throws IndexParseException
     * @return Any config that was stored on the server for this PI.
     */
    public static PiActorConfig selfRegister(PiActorConfig config) throws RESTException, ParseException, IOException, IndexParseException
    {
        Config docussandraConfig = new Config(config.getDocussandraUrl());
        //search for any existing registrations
        QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
        Query existanceQuery = new Query();
        existanceQuery.setDatabase(Constants.DB);
        existanceQuery.setTable(Constants.NODES_TABLE);
        existanceQuery.setWhere("name = '" + config.getPiName() + "'");
        QueryResponseWrapper qrw = queryDao.query(existanceQuery);
        UUID updateUUID = null;
        if (!qrw.isEmpty()) //if we have an existing registration
        {
            updateUUID = qrw.get(0).getUuid();//grab the UUID
            config = createConfigFromJSON(qrw.get(0).getObject());//and the current information
            config.setDocussandraUrl(docussandraConfig.getBaseUrl());//the builder object doesn't set the url
        }

        //create or update the registration
        DocumentDao docDao = new DocumentDaoImpl(docussandraConfig);
        Table actorNodeTable = new Table();
        actorNodeTable.setDatabaseByObject(new Database(Constants.DB));
        actorNodeTable.setName(Constants.NODES_TABLE);
        Document registerDoc = new Document();
        registerDoc.setTable(actorNodeTable);
        registerDoc.setObjectAsString(generateSelfRegisterJson(config));
        if (updateUUID == null)
        {
            docDao.create(actorNodeTable, registerDoc);//create; it hasn't been registered
        } else
        {
            registerDoc.setUuid(updateUUID);//update; this is only to update the timestamps of registration
            docDao.update(registerDoc);
        }
        return config;
    }
    
    public static void setStates(PiActorConfig config, PiActor actor) throws IOException, IndexParseException, ParseException, RESTException
    {
        Config docussandraConfig = new Config(config.getDocussandraUrl());
        QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
        DocumentDao docDao = new DocumentDaoImpl(docussandraConfig);
        Table stateTable = new Table();
        stateTable.setDatabaseByString(Constants.DB);
        stateTable.setName(Constants.ACTOR_ABILITY_STATUS_TABLE);
        final ObjectReader r = SDKUtils.getObjectMapper().reader(Action.class);
        for (ActorAbility aa : config.getAbilities())
        {
            //search for any existing states
            Query existanceQuery = new Query();
            existanceQuery.setDatabase(Constants.DB);
            existanceQuery.setTable(Constants.ACTOR_ABILITY_STATUS_TABLE);
            existanceQuery.setWhere("name = '"  + config.getPiName() + "_" + aa.getName() + "'");
            QueryResponseWrapper qrw = queryDao.query(existanceQuery);
            if (qrw.isEmpty())
            {
                //it doesn't exist; let's set the default state into Docussandra
                Document stateDoc = new Document();
                stateDoc.setTable(stateTable);
                stateDoc.setObjectAsString("{\"name\": \"" + config.getPiName() + "_" + aa.getName() + "\", \"state\":\"" + aa.getState() + "\"}");
                docDao.create(stateTable, stateDoc);
            } else {
                BSONObject object = qrw.get(0).getObject();
                Action a = (Action)r.readValue(JSON.serialize(object));
                a.setName(a.getName().split("\\Q_\\E")[1]);//pull the pi name out
                actor.performAction(a);
            }
        }
        
    }

    //stolen (then modified) from stackoverflow: http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
    /**
     * Returns an <code>String</code> object encapsulating what is most likely
     * the machine's LAN IP address.
     * <p/>
     * This method is intended for use as a replacement of JDK method
     * <code>InetAddress.getLocalHost</code>, because that method is ambiguous
     * on Linux systems. Linux systems enumerate the loopback network interface
     * the same way as regular LAN network interfaces, but the JDK
     * <code>InetAddress.getLocalHost</code> method does not specify the
     * algorithm used to select the address returned under such circumstances,
     * and will often return the loopback address, which is not valid for
     * network communication. Details
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
     * <p/>
     * This method will scan all IP addresses on all network interfaces on the
     * host machine to determine the IP address most likely to be the machine's
     * LAN address. If the machine has multiple IP addresses, this method will
     * prefer a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually
     * IPv4) if the machine has one (and will return the first site-local
     * address if the machine has more than one), but if the machine does not
     * hold a site-local address, this method will return simply the first
     * non-loopback address found (IPv4 or IPv6).
     * <p/>
     * If this method cannot find a non-loopback address using this selection
     * algorithm, it will fall back to calling and returning the result of JDK
     * method <code>InetAddress.getLocalHost</code>.
     * <p/>
     *
     * @throws UnknownHostException If the LAN address of the machine cannot be
     * found.
     * @return Ip address.
     */
    public static String getCurrentIp() throws UnknownHostException
    {
        try
        {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
            {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();)
                {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress())
                    {
                        if (inetAddr.isSiteLocalAddress() && !iface.getName().contains("docker"))
                        {
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null)
                        {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null)
            {
                return candidateAddress.getHostAddress();
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null)
            {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e)
        {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
    
}
