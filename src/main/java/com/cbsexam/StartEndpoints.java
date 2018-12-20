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

        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
                "\tWELCOME\n" +
                "\tThis is a backend system containing endpoints\n" +
                "\tYou can use the following endpoints:\n" +
                "\t/user/'insertToken'\n" +
                "\t/product/'insertToken'\n" +
                "\t/order/'insertToken'\n" +
                "\t/search/title/*/'insertToken'\n").build();

    }

}
