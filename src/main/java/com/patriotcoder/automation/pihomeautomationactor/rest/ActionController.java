package com.patriotcoder.automation.pihomeautomationactor.rest;

import com.patriotcoder.automation.pihomeautomationactor.dataobject.Action;
import com.patriotcoder.automation.pihomeautomationactor.dataobject.PiActor;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for Database entities.
 */
public class ActionController
{

    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);

    private PiActor actor;

    public ActionController(PiActor actor)
    {
        super();
        this.actor = actor;
    }

    public void create(Request request, Response response)
    {
        try
        {
            logger.info("State change request made.");
            Action action = request.getBodyAs(Action.class, "No action specified");
            logger.debug("Trying to perform action: " + action.toString());
            actor.performAction(action);            
//        // Construct the response for create...
            response.setResponseCreated();
            logger.info("State change request completed.");
        } catch (Exception e)
        {
            logger.error("Problem performing action", e);
        }
    }

    public void update(Request request, Response response)
    {
        create(request, response);
    }

}
