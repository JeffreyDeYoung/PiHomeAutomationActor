package com.patriotcoder.automation.pihomeautomationactor;

import com.docussandra.javasdk.Config;
import com.docussandra.javasdk.dao.DocumentDao;
import com.docussandra.javasdk.dao.QueryDao;
import com.docussandra.javasdk.dao.impl.DocumentDaoImpl;
import com.docussandra.javasdk.dao.impl.QueryDaoImpl;
import com.docussandra.javasdk.exceptions.RESTException;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.ActorAbility;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActorConfig;
import com.pearson.docussandra.domain.objects.Database;
import com.pearson.docussandra.domain.objects.Document;
import com.pearson.docussandra.domain.objects.Query;
import com.pearson.docussandra.domain.objects.QueryResponseWrapper;
import com.pearson.docussandra.domain.objects.Table;
import com.pearson.docussandra.exception.IndexParseException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Jeffrey DeYoung
 */
public class InitUtils
{

    private PiActorConfig config;
    
    public InitUtils(PiActorConfig config){
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

    /**
     * Performs a self registration in Docussandra. Will update the registration if it already exists.
     * @param config
     * @throws RESTException
     * @throws ParseException
     * @throws IOException
     * @throws IndexParseException
     */
    public static void selfRegister(PiActorConfig config) throws RESTException, ParseException, IOException, IndexParseException
    {
        String dbName = "pihomeautomation";
        String tableName = "nodes";
        Config docussandraConfig = new Config(config.getDocussandraUrl());
        QueryDao queryDao = new QueryDaoImpl(docussandraConfig);
        Query existanceQuery = new Query();
        existanceQuery.setDatabase(dbName);
        existanceQuery.setTable(tableName);
        existanceQuery.setWhere("name = '" + config.getPiName() + "'");
        QueryResponseWrapper qrw = queryDao.query(dbName, existanceQuery);
        UUID updateUUID = null;
        if (!qrw.isEmpty())
        {
            updateUUID = qrw.get(0).getUuid();
        }
        DocumentDao docDao = new DocumentDaoImpl(docussandraConfig);
        Table actorNodeTable = new Table();
        actorNodeTable.database(new Database(dbName));
        actorNodeTable.name(tableName);
        Document registerDoc = new Document();
        registerDoc.objectAsString(generateSelfRegisterJson(config));
        if (updateUUID == null)
        {
            docDao.create(actorNodeTable, registerDoc);
        } else
        {
            registerDoc.setUuid(updateUUID);
            docDao.update(registerDoc);
        }
    }
    
}
