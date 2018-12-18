package com.cbsexam;

import cache.OrderCache;
import cache.UserCache;
import com.google.gson.Gson;
import controllers.OrderController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import controllers.UserController;
import model.Order;
import model.User;
import utils.Encryption;

@Path("order")
public class OrderEndpoints {

  private static OrderCache orderCache = new OrderCache();
  private static ArrayList<User> users = new ArrayList<>();


  //Malthe: added standard response if user don't insert token i the URL
  /** @return Responses */
  @GET
  @Path("")
  public Response standardAnswer(){
    return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity("You need a session ID to view orders \n" +
            "You can access it at the following path: /user/login").build();
  }

  /**
   * @param idOrder
   * @return Responses
   */
  @GET
  @Path("/{idOrder}/{token}")
  public Response getOrder(@PathParam("idOrder") int idOrder, @PathParam("token") String token) {

    try{
      users = UserController.getUsers();

      // Call our controller-layer in order to get the order from the DB
      Order order = OrderController.getOrder(idOrder);

        boolean check = true;

        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(order);

        //Malthe: Checks if the granted token exists and the user is logged in
        for(User user: users){
          if(user.getToken() != null && user.getToken().equals(token)){
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
    }catch (Exception e){
      System.out.println(e.getMessage());
      return Response.status(400).entity("Order does not exist").build();
    }

  }

  /** @return Responses */
  @GET
  @Path("/{token}")
  public Response getOrders(@PathParam("token") String token) {

    boolean check = true;
    users = UserController.getUsers();

    // Call our cache-layer in order to get the order from the DB
    ArrayList<Order> orders = orderCache.getOrders(false);

    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(orders);

    //Malthe: Checks if the granted token exists and the user is logged in
    for(User user: users){
      if(user.getToken() != null && user.getToken().equals(token)){
        check = false;
      }
    }

    // TODO: Add Encryption to JSON : fixed
    if(check){
      //Malthe: Add encryption to json rawString object(ref. utils Encryption)
      json = Encryption.encryptDecryptXOR(json);
    }

    // Return a response with status 200 and JSON as type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createOrder(String body) {

    try {
      // Read the json from body and transfer it to a order class
      Order newOrder = new Gson().fromJson(body, Order.class);

      // Use the controller to add the user
      Order createdOrder = OrderController.createOrder(newOrder);

      //Malthe: Force an update since the list of orders will have added one order
      orderCache.getOrders(true);

      // Get the user back with the added ID and return it to the user
      String json = new Gson().toJson(createdOrder);

      // Return the data to the user
      if (createdOrder != null) {
        // Return a response with status 200 and JSON as type
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      } else {

        // Return a response with status 400 and a message in text
        return Response.status(400).entity("Could not create order").build();
      }
    }catch (Exception e){
      return Response.status(400).entity("Failed to create order").build();
    }
  }
}