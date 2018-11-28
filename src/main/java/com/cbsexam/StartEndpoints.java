package com.cbsexam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class StartEndpoints {

    @GET
    @Path("")
    public Response getStartPage(){

        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("\n\n\n\t\t\tWELCOME\n" +
                "\t\t\tThis is a backend system containing endpoints\n" +
                "\t\t\tYou can use the following endpoints:\n" +
                "\t\t\t/User\n" +
                "\t\t\t/Product\n" +
                "\t\t\t/Order\n" +
                "\t\t\t/Search\n").build();

    }

}
