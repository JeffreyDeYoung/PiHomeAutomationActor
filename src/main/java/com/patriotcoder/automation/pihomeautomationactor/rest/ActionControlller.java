package com.patriotcoder.automation.pihomeautomationactor.rest;

import com.patriotcoder.automation.pihomeautomationactor.ActorMain;
import com.patriotcoder.automation.pihomeautomationactor.PiActor;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * REST controller for Database entities.
 */
public class ActionControlller
{

    private static final Logger logger = LoggerFactory.getLogger(ActionControlller.class);
    //private static final UrlBuilder LOCATION_BUILDER = new UrlBuilder();
    
    private PiActor actor;
    
    public ActionControlller(PiActor actor)
    {
        super();
        this.actor = actor;
    }

    public void create(Request request, Response response)
    {
        logger.info("State change request made.");
        actor.doStuff();
//        String name = request.getHeader(Constants.Url.DATABASE, "No database name provided");
//        Database database = request.getBodyAs(Database.class);
//
//        if (database == null)
//        {
//            database = new Database();
//        }
//
//        database.name(name);
//        Database saved = null;//databases.create(database);
//
//        // Construct the response for create...
        response.setResponseCreated();
    }

    public void update(Request request, Response response)
    {
        create(request, response);
    }


}
