package com.patriotcoder.automation.pihomeautomationactor;

import com.docussandra.javasdk.dao.DocumentDao;
import com.docussandra.javasdk.dao.QueryDao;
import com.docussandra.javasdk.dao.impl.DocumentDaoImpl;
import com.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.docussandra.javasdk.exceptions.RESTException;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActor;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.Config;
import com.patriotcoder.automation.pihomeautomationactor.rest.ActionControlller;
import com.patriotcoder.automation.pihomeautomationactor.rest.HealthCheckController;
import com.patriotcoder.automation.pihomeautomationactor.rest.Routes;
import com.patriotcoder.automation.pihomeautomationactor.serialization.SerializationProvider;
import com.pearson.docussandra.domain.objects.Database;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import com.pearson.docussandra.domain.objects.Table;
import com.pearson.docussandra.exception.IndexParseException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.restexpress.RestExpress;
import org.restexpress.plugin.version.VersionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for this application.
 *
 * @author https://github.com/JeffreyDeYoung
 */
public class ActorMain
{

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ActorMain.class);

    /**
     * Main method. Entry point for the application.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("Starting up Pi Actor...");
        logger.info("(Logger)Starting up Pi Actor...");
        try
        {
            PiActor actor = initializeActor();
            RestExpress server = initializeServer(actor);
            server.awaitShutdown();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    logger.info("Shutting down Pi Actor...");
                }
            }, "Shutdown-thread"));
        } catch (Exception e)
        {
            logger.error("Runtime exception when starting/running the PiAutomationActor. Could not start.", e);
        }
    }

    private static PiActor initializeActor() throws IOException, IllegalArgumentException, RESTException, ParseException, IndexParseException
    {
        //get the config from the config file -- contains default settings and name
        Config config = Config.buildConfigFromFile(new File("actor.config"));

        //self register with the central DB
        selfRegister(config);

        //Pull Down any overrides from the central DB
        //TODO
        //Create and return the actor
        PiActor actor = PiActor.getPiActor(config);
        return actor;
    }

    private static void selfRegister(Config config) throws RESTException, ParseException, IOException, IndexParseException
    {
        String dbName = "pihomeautomation";
        String tableName = "nodes";
        com.docussandra.javasdk.Config docussandraConfig = new com.docussandra.javasdk.Config(config.getDocussandraUrl());
        //search for this node to see if it exists
        QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
        Query existanceQuery = new Query();
        existanceQuery.setDatabase(dbName);
        existanceQuery.setTable(tableName);
        existanceQuery.setWhere("name = \"" + config.getPiName() + "\"");        
        QueryResponseWrapper qrw = queryDao.query(dbName, existanceQuery);
        UUID updateUUID = null;
        if(!qrw.isEmpty()){
            updateUUID = qrw.get(0).getUuid();
        }
        //create/update
        DocumentDao docDao = new DocumentDaoImpl(docussandraConfig);
        Table actorNodeTable = new Table();
        actorNodeTable.database(new Database(dbName));
        actorNodeTable.name(tableName);
        Document registerDoc = new Document();
        registerDoc.objectAsString(generateSelfRegisterJson(config));
        if(updateUUID == null){
            docDao.create(actorNodeTable, registerDoc);
        } else {
            registerDoc.setUuid(updateUUID);
            docDao.update(registerDoc);
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
     */
    public static String getCurrentIp() throws UnknownHostException
    {
        try
        {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
            {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();)
                {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress())
                    {

                        if (inetAddr.isSiteLocalAddress() && !iface.getName().contains("docker"))
                        {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null)
                        {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null)
            {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress.getHostAddress();
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
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

    /**
     * Generates the JSON needed to self register a node.
     *
     * @param config Configuration for this node.
     * @return A string of the self-registry JSON.
     */
    public static String generateSelfRegisterJson(Config config) throws UnknownHostException
    {
        JSONObject selfRegister = new JSONObject();
        selfRegister.put("name", config.getPiName());
        selfRegister.put("ip", getCurrentIp());
        selfRegister.put("running", true);
        selfRegister.put("type", "actor");
        JSONArray abilityArray = new JSONArray();
        for (ActorAbility ability : config.getAbilities())
        {
            JSONObject abilityJsonObject = new JSONObject();
            abilityJsonObject.put("name", ability.getName());
            abilityJsonObject.put("gpio_pin", ability.getGpioPin().getAddress());
            abilityJsonObject.put("default_state", ability.getState().toString());
            abilityArray.add(abilityJsonObject);
        }
        selfRegister.put("abilities", abilityArray);
        return selfRegister.toJSONString();
    }

    /**
     * Creates and starts up the REST server.
     *
     * @param actor PiActor to run the REST server for.
     * @return A running REST server.
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private static RestExpress initializeServer(PiActor actor)
    {
        RestExpress.setSerializationProvider(new SerializationProvider());
        //Identifiers.UUID.useShortUUID(true);
        logger.info("-----Attempting to start up Pi Actor-----");
        RestExpress server = new RestExpress()
                .setName("Pi Actor")
                //.setBaseUrl(config.getBaseUrl())
                .setExecutorThreadCount(3)
                //.addPostprocessor(new LastModifiedHeaderPostprocessor())
                //.addMessageObserver(new SimpleLogMessageObserver())
                //.addPreprocessor(new RequestApplicationJsonPreprocessor())
                //.addPreprocessor(new RequestXAuthCheck())
                .setMaxContentSize(512000);//half a meg

        new VersionPlugin("1.0")
                .register(server);
        Routes.define(new HealthCheckController(), new ActionControlller(actor), server);
        //Relationships.define(server);
        //configurePlugins(config, server);
        //mapExceptions(server);
        server.bind(8080);
        logger.info("-----Pi Actor initalized.-----");
        return server;
    }

}
