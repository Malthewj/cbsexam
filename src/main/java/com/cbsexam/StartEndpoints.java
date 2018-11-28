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

        return Response.status(200).type(MediaType.TEXT_HTML).entity(
                "<p>WELCOME</p><br>" +
                "<p>This is a backend system containing endpoints</p><br>" +
                "<p>You can use the following endpoints:</p><br>" +
                "<a href=user/'insertToken'>User</a> <br>" +
                "<a href=product/'insertToken'>Products</a> <br>" +
                "<a href=order/'insertToken'>Order</a> <br>" +
                "<a href=search/title/*/'insertToken'>Search</a>").build();

    }

}
