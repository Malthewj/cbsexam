package com.cbsexam;

import cache.ProductCache;
import cache.UserCache;
import com.google.gson.Gson;
import controllers.ProductController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import controllers.UserController;
import model.Product;
import model.User;
import utils.Encryption;

@Path("product")
public class ProductEndpoints {

  private static ProductCache productCache = new ProductCache();
  private static ArrayList<User> users = new ArrayList<>();

  //Malthe: added standard response if user don't insert token i the URL
  /** @return Responses */
  @GET
  @Path("")
  public Response standardAnswer(){
    return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity("You need a session ID to view products \n" +
            "You can access it at the following path: /user/login").build();
  }

  /**
   * @param idProduct
   * @return Responses
   */
  @GET
  @Path("/{idProduct}/{token}")
  public Response getProduct(@PathParam("idProduct") int idProduct, @PathParam("token") String token) {

    try{
        users = UserController.getUsers();
        // Call our controller-layer in order to get the order from the DB
        Product product = ProductController.getProduct(idProduct);

        boolean check = true;

        // We convert the java object to json with GSON library imported in Maven
        String json = new Gson().toJson(product);

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
    catch (Exception e){
      System.out.println(e.getMessage());
      return Response.status(400).entity("Product does not exist").build();
    }
  }

  /** @return Responses */
  @GET
  @Path("/{token}")
  public Response getProducts(@PathParam("token") String token) {

    boolean check = true;
    users = UserController.getUsers();

    // Call our cache-layer in order to get the product from the DB
    //Malthe: forceUpdate set to false since we only want to update if new products added
    ArrayList<Product> products = productCache.getProducts(false);

    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(products);

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
  public Response createProduct(String body) {

    // Read the json from body and transfer it to a product class
    Product newProduct = new Gson().fromJson(body, Product.class);

    // Use the controller to add the user
    Product createdProduct = ProductController.createProduct(newProduct);

    //Malthe: ForceUpdate set to true to force a update of the cache
    productCache.getProducts(true);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createdProduct);

    // Return the data to the user
    if (createdProduct != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create product").build();
    }
  }
}
