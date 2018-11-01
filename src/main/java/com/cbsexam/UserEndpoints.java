package com.cbsexam;

import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Hashing;
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


    // TODO: What should happen if something breaks down? : fix
    ArrayList<User> userCheck = new ArrayList<>();
    userCheck = UserController.getUsers();


    if(idUser == 0 || userCheck.size() < idUser){
      return Response.status(400).entity("Inputtet user ID is not valid. 0 in not valid").build();
    }

    // Return the user with the status code 200
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

    //Remove comment notations to remove decrypting
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
      return Response.status(400).entity("Could not create user - username might been taken").build();
    }
  }

  // TODO: Make a smart way of login in without having to enter ID, maybe not possible
  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(@PathParam("idUser") int idUser, String login) {

    User userLogin = new Gson().fromJson(login, User.class);

    ArrayList<User> users;

    //All users are getted in an arraylist
    users = UserController.getUsers();

    for (User user : users) {
      if (user.getFirstname().equals(userLogin.getFirstname()) && idUser == user.getId()) {
        //Inputted "user"'s created time is set to found user.
        userLogin.setCreatedTime(user.getCreatedTime());
        Hashing hashing = new Hashing();

        //Created time is set to be the salt
        hashing.setSalt(String.valueOf(userLogin.getCreatedTime()));

        //The password string is hashed with a salt
        String password = hashing.hashWithSaltMD5(userLogin.getPassword());

        if(user.getPassword().equals(password)){
          return Response.status(200).entity("You are logged in!").build();
        }
      }
    }
    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Not valid login attempt. Please match your input").build();
  }

  // TODO: Make the system able to deleteUpdate users : fix
  @POST
  @Path("/deleteUpdate/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int id) {

    UserController.deleteUser(id);

    // Return a response with status 200 and JSON as type
    return Response.status(200).entity("User with id " + id + " is now deleted").build();
  }

  // TODO: Make the system able to update users : fix
  @POST
  @Path("/update/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("idUser") int userToUpdateID, String updatedUserData) {

    User updatedUserDataObj = new Gson().fromJson(updatedUserData, User.class);

    if(updatedUserDataObj.getEmail().equals("") && updatedUserDataObj.getLastname().equals("") && updatedUserDataObj.getFirstname().isEmpty()) {
        return Response.status(400).entity("ERROR - check input takes firstname, lastname or email").build();
    }
    else if(userToUpdateID != 0){
        UserController.updateUser(userToUpdateID, updatedUserDataObj);
    }

    // Return a response with status 200 and JSON as type

    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
            "User with ID " + userToUpdateID + " is now updated to:\n" +
                    "Firstname: " + updatedUserDataObj.getFirstname() + "\n" +
            "Lastname: " + updatedUserDataObj.getLastname() + "\n" +
            "Email: " + updatedUserDataObj.getEmail()).build();

  }

}
