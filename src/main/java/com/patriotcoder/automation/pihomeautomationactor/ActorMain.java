package com.patriotcoder.automation.pihomeautomationactor;

import com.patriotcoder.automation.pihomeautomationactor.rest.ActionControlller;
import com.patriotcoder.automation.pihomeautomationactor.rest.HealthCheckController;
import com.patriotcoder.automation.pihomeautomationactor.rest.Routes;
import com.patriotcoder.automation.pihomeautomationactor.serialization.SerializationProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.restexpress.RestExpress;
import org.restexpress.plugin.version.VersionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jeffrey DeYoung
 */
public class ActorMain
{

    private static final Logger logger = LoggerFactory.getLogger(ActorMain.class);

    public static void main(String[] args)
    {
        System.out.print("Starting up Pi Actor...");
        System.out.print("(Logger)Starting up Pi Actor...");
        try
        {
            RestExpress server = initializeServer(args);
            server.awaitShutdown();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    logger.info("Shutting down Pi Actor...");
                    //CacheFactory.shutdownCacheManger();
                }
            }, "Shutdown-thread"));
        } catch (RuntimeException e)
        {
            logger.error("Runtime exception when starting/running Docussandra/RestExpress. Could not start.", e);
        }
    }

    public static RestExpress initializeServer(String[] args)
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

        Config config = new Config();
        Routes.define(new HealthCheckController(), new ActionControlller(new PiActor(config)), server);
        //Relationships.define(server);
        //configurePlugins(config, server);
        //mapExceptions(server);

//        //required pi security
//        piAuthenticator = getKeyMapAuthenticator(config.getSecurityConfig());
//        preprocessor = new PiAuthenticationPreprocessor(piAuthenticator);
//        if (config.getPort() == 0)
//        {//no port? calculate it off of the version number
//            server.setPort(calculatePort(config.getProjectVersion()));
//        } else
//        {
//            server.setPort(config.getPort());
//        }
        server.bind(8080);
        logger.info("-----Pi Actor initalized.-----");
        return server;
    }

}
