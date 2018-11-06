package com.cbsexam;

import cache.UserCache;
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


    public static UserCache userCache = new UserCache();
    public static Hashing hashing = new Hashing();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON : fixed
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    //Add encryption to json rawString object(ref. utils Encryption)
    json = Encryption.encryptDecryptXOR(json);


    // TODO: What should happen if something breaks down? : (fixed)
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

    //Malthe: Get a list of users from the cache function
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON : fixed
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //Malthe: Add encryption to json rawString object(ref. utils Encryption)
    json = Encryption.encryptDecryptXOR(json);

    //Malthe: Remove comment notations to remove decrypting
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

    //Malthe: Force update of the user cache, since there is a new user in the ArrayList of users
    userCache.getUsers(true);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Checks if the username is already taken
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user - username might been taken").build();
    }
  }

  // TODO: Make a smart way of login in without having to enter ID, maybe not possible : fixed (implemented username)
  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String login) {

    User userLogin = new Gson().fromJson(login, User.class);

    //Malthe: All users are getted in an arraylist
      ArrayList<User> users = userCache.getUsers(false);

    for (User user : users) {
      if (user.getUsername().equals(userLogin.getUsername())) {

        //Malthe: Created time is set to be the salt
        hashing.setSalt(String.valueOf(user.getCreatedTime()));

        //Malthe: The password string is hashed with a salt
        String password = hashing.hashWithSaltSHA(userLogin.getPassword());


        if(user.getPassword().equals(password)){

          String token = user.getUsername()+user.getEmail()+String.valueOf(user.getCreatedTime());

          token = Encryption.encryptDecryptXOR(token);

          return Response.status(200).entity("Your token is:\n" + token).build();
        }

      }
    }
    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Not valid login attempt. Please match your input").build();
  }

  // TODO: Make the system able to delete users : fixed
  @POST
  @Path("/delete/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int id) {

    UserController.deleteUser(id);

      //Malthe: Update of the user cache, since there is deleted a User in the ArrayList of users
      userCache.getUsers(true);

    // Return a response with status 200 and JSON as type
    return Response.status(200).entity("User with id " + id + " is now deleted").build();
  }

  // TODO: Make the system able to update users : fixed
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

        //Malthe: Update of the user cache, since there is new information in the ArrayList of users
        userCache.getUsers(true);
    }

    // Return a response with status 200 and JSON as type

    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
            "User with ID " + userToUpdateID + " is now updated to:\n" +
                    "Firstname: " + updatedUserDataObj.getFirstname() + "\n" +
            "Lastname: " + updatedUserDataObj.getLastname() + "\n" +
            "Email: " + updatedUserDataObj.getEmail()).build();
  }

  @POST
    @Path("/updatepassword/{idUser}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("idUser") int id, String passwordUpdate){

    //Malthe: The inputtet password is saved in a user object and the password is saved in a string
    User passwordupdate = new Gson().fromJson(passwordUpdate, User.class);
    String password = passwordupdate.getPassword();

      //Malthe: Getting the user which password needs to be updated to currentuser
      User usertoUpdate = UserController.getUser(id);

      //Malthe: Setting the salt to currentusers created time
      hashing.setSalt(String.valueOf(usertoUpdate.getCreatedTime()));

      //Malthe: Hashing the new password with createdtime as the salt
      String newPassword = hashing.hashWithSaltSHA(password);

      //Malthe: If the new password is not "new" or too short
      if(usertoUpdate.getPassword().equals(newPassword) || password.length() < 7){
          return Response.status(400).entity("Password must be new and 6 characters long").build();
      }
      //Malthe: Updating
      UserController.updatePassword(id, newPassword);

      //Malthe: Forcing a update in the cache since the list of users has changed
      userCache.getUsers(true);

      return Response.status(200).entity("Password is updated and hashed").build();
  }

}
