package com.cbsexam;

import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON : fix
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    //Add encryption to json rawString object(ref. utils Encryption)
    json = Encryption.encryptDecryptXOR(json);

    /*
    if(json.equals(null)){

      return Response.status(400);

    }*/

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();



  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = UserController.getUsers();

    // TODO: Add Encryption to JSON : fix
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //Add encryption to json rawString object(ref. utils Encryption)
    json = Encryption.encryptDecryptXOR(json);

    //Remove comment notations to decrypt
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to delete users : fix
  @POST
  @Path("/delete/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int id) {

    UserController.deleteUser(id);

    // Return a response with status 200 and JSON as type
    return Response.status(200).entity("User with id " + id + " is now deleted").build();
  }

  // TODO: Make the system able to update users : fix, not yet able to only change ONE attribute
  @POST
  @Path("/update/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("idUser") int userToUpdateID, String userToUpdate) {

    User userUpdates = new Gson().fromJson(userToUpdate, User.class);

    if(userUpdates.getEmail().equals("") || userUpdates.getLastname().equals("") || userUpdates.getFirstname().isEmpty()) {
      return Response.status(400).entity("ERROR - check input takes firstname, lastname and email").build();
    }
    else if(userToUpdateID != 0){
      UserController.updateUser(userToUpdateID, userUpdates);
    }

    // Return a response with status 200 and JSON as type

    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
            "User with ID " + userToUpdateID + " is now updated to:\n" +
                    "Firstname: " + userUpdates.getFirstname() + "\n" +
            "Lastname: " + userUpdates.getLastname() + "\n" +
            "Email: " + userUpdates.getEmail()).build();

  }

}
