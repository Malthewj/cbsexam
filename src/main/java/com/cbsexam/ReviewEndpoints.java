package com.cbsexam;
import com.google.gson.Gson;
import controllers.ReviewController;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import controllers.UserController;
import model.Review;
import model.User;
import utils.Encryption;

@Path("search")
public class ReviewEndpoints {

  //Malthe: added standard response if user don't insert token i the URL
  /** @return Responses */
  @GET
  @Path("title/{title}")
  public Response standardAnswer(@PathParam("title") String reviewTitle){
    return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity("You need a session ID to view book titles \n" +
            "You can access it at the following path: /user/login").build();
  }

  /**
   * @param reviewTitle
   * @return Responses
   */
  @GET
  @Path("/title/{title}/{token}")
  public Response search(@PathParam("title") String reviewTitle, @PathParam("token") String token) {

    //Malthe: If this boolean is true the output will be encrypted
    boolean check = true;

    ArrayList<User> users = UserController.getUsers();

    // Call our controller-layer in order to get the order from the DB
    ArrayList<Review> reviews = ReviewController.searchByTitle(reviewTitle);

    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(reviews);

    //Malthe: Checks if the granted token exists and the user is logged in
    for(User user:users){
      if(user.getToken() !=null && user.getToken().equals(token)){
        check = false;
      }
    }

    // TODO: Add Encryption to JSON : fixed
    if(check){
      //Malthe: Add encryption to json rawString object(ref. utils Encryption)
      json = Encryption.encryptDecryptXOR(json);
    }

    // Return a response with status 200 and JSON as type
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  //TODO: Implement search by ID : fixed
  /**
   * @param id
   * @return Responses
   */
  @GET
  @Path("/id/{id}/{token}")
  public Response search(@PathParam("id") int id, @PathParam("token") String token){

    //Malthe: If this boolean is true the output will be encrypted
    boolean check = true;

    ArrayList<User> users = UserController.getUsers();

    ArrayList<Review> reviews = ReviewController.searchByID(id);

    String json = new Gson().toJson(reviews);

    //Malthe: Checks if the granted token exists and the user is logged in
    for(User user:users){
      if(user.getToken() !=null && user.getToken().equals(token)){
        check = false;
      }
    }

    if(check){
      json = Encryption.encryptDecryptXOR(json);
    }

    // Return a response with status 200 and JSON as type
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

}
