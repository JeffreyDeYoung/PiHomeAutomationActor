package com.patriotcoder.automation.pihomeautomationactor.rest;

import com.patriotcoder.automation.pihomeautomationactor.PiActor;
import org.restexpress.Request;
import org.restexpress.Response;


/**
 * REST controller for Database entities.
 */
public class ActionControlller
{

    //private static final UrlBuilder LOCATION_BUILDER = new UrlBuilder();
    
    private PiActor actor;
    
    public ActionControlller(PiActor actor)
    {
        super();
        this.actor = actor;
    }

    public void create(Request request, Response response)
    {
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
//        response.setResponseCreated();
//
//        // enrich the resource with links, etc. here...
//        TokenResolver resolver = HyperExpress.bind(Constants.Url.DATABASE, saved.name());
//
//        // Include the Location header...
//        String locationPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.DATABASE);
//        response.addLocationHeader(LOCATION_BUILDER.build(locationPattern, resolver));
//
//        // Return the newly-created resource...
//        return saved;
    }

    public void update(Request request, Response response)
    {
        create(request, response);
    }


}
