package com.patriotcoder.automation.pihomeautomationactor;

import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActor;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.Config;
import com.patriotcoder.automation.pihomeautomationactor.rest.ActionControlller;
import com.patriotcoder.automation.pihomeautomationactor.rest.HealthCheckController;
import com.patriotcoder.automation.pihomeautomationactor.rest.Routes;
import com.patriotcoder.automation.pihomeautomationactor.serialization.SerializationProvider;
import java.io.File;
import java.io.IOException;
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

    private static PiActor initializeActor() throws IOException, IllegalArgumentException
    {
        //get the config from the config file -- contains default settings and name
        Config config = Config.buildConfigFromFile(new File("actor.config"));
        
        //self register with the central DB
        //TODO
        
        //Pull Down any overrides from the central DB
        //TODO
        
        //Create and return the actor
        PiActor actor = PiActor.getPiActor(config);
        return actor;
    }

    /**
     * Creates and starts up the REST server.
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
