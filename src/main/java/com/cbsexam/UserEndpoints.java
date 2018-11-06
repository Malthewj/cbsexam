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

      // TODO: What should happen if something breaks down? : fixed
    try{
        // Use the ID to get the user from the controller.
        User user = UserController.getUser(idUser);

        // TODO: Add Encryption to JSON : fixed
        // Convert the user object to json in order to return the object
        String json = new Gson().toJson(user);

        //Add encryption to json rawString object(ref. utils Encryption)
        json = Encryption.encryptDecryptXOR(json);


        ArrayList<User> userCheck = new ArrayList<>();
        userCheck = UserController.getUsers();


        if(idUser == 0 || userCheck.size() < idUser){
            return Response.status(400).entity("Inputtet user ID is not valid. 0 in not valid").build();
        }

        // Return the user with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    }catch (Exception e){
        return Response.status(400).entity("User with granted ID does not exist").build();
    }

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
  // TODO: Make the system able to login users and assign them a token to use throughout the system : fixed
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

          String token = user.getUsername()+user.getEmail();

          hashing.setSalt(String.valueOf(user.getCreatedTime()));

          token = hashing.hashWithSaltSHA(token);


          return Response.status(200).entity("Your token is:\n" + token).build();
        }

      }
    }
    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Not valid login attempt. Please match your input").build();
  }

  // TODO: Make the system able to delete users : fixed
  @POST
  @Path("/delete/{idUser}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int id, @PathParam("token") String token) {

      try{
          //Malthe: Checks if the user is logged in and have granted a token
          if(token.equals("")){
              return Response.status(400).entity("You're not logged in yet").build();
          }

          User userDeleting = UserController.getUser(id);
          hashing.setSalt(String.valueOf(userDeleting.getCreatedTime()));

          String tokencheck = hashing.hashWithSaltSHA(userDeleting.getUsername() + userDeleting.getEmail());

          //Malthe: Checks if the user is admin since admin can delete everyone
          if(token.equals("1935ffc5d59e235d1e7b2ba3c13a76f21af80c96358c802e222c720febe52972")){
              UserController.deleteUser(id);

              //Malthe: Update of the user cache, since there is deleted a User in the ArrayList of users
              userCache.getUsers(true);

              // Return a response with status 200 and JSON as type
              return Response.status(200).entity("User with id " + id + " is now deleted").build();
          }

          else if(tokencheck.equals(token)){
              UserController.deleteUser(id);

              //Malthe: Update of the user cache, since there is deleted a User in the ArrayList of users
              userCache.getUsers(true);

              // Return a response with status 200 and JSON as type
              return Response.status(200).entity("User with id " + id + " is now deleted").build();
          }


      }catch (Exception e){
          return Response.status(400).entity("User with granted ID does not exist").build();
      }
      return Response.status(400).entity("You can only delete yourself").build();
  }

  // TODO: Make the system able to update users : fixed
  @POST
  @Path("/update/{idUser}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("idUser") int userToUpdateID,
                             String updatedUserData, @PathParam("token") String token) {

    try{
        //Malthe: Checks if the user is logged in and have granted a token
        if(token.equals("")){
            return Response.status(400).entity("You're not logged in yet").build();
        }

        User updatedUserDataObj = new Gson().fromJson(updatedUserData, User.class);

        User userToUpdate = UserController.getUser(userToUpdateID);

        hashing.setSalt(String.valueOf(userToUpdate.getCreatedTime()));

        String tokenCheck = userToUpdate.getUsername() + userToUpdate.getEmail();

        tokenCheck = hashing.hashWithSaltSHA(tokenCheck);

        if(updatedUserDataObj.getEmail().equals("") && updatedUserDataObj.getLastname().equals("") && updatedUserDataObj.getFirstname().isEmpty()) {
            return Response.status(400).entity("ERROR - check input takes firstname, lastname or email").build();
        }
        else if(tokenCheck.equals(token)){
            UserController.updateUser(userToUpdate.getId(), updatedUserDataObj);

            //Malthe: Update of the user cache, since there is new information in the ArrayList of users
            userCache.getUsers(true);

            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
                    "User with ID " + userToUpdateID + " is now updated to:\n" +
                            "Firstname: " + updatedUserDataObj.getFirstname() + "\n" +
                            "Lastname: " + updatedUserDataObj.getLastname() + "\n" +
                            "Email: " + updatedUserDataObj.getEmail()).build();
        }

    }catch (Exception e){
        return Response.status(400).entity("User with granted ID does not exist").build();
    }

    return Response.status(400).entity("You can only update yourself").build();

  }

  @POST
    @Path("/updatepassword/{idUser}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("idUser")int id,
                                   String passwordUpdate, @PathParam("token") String token){

      try{
          //Malthe: Checks if the user is logged in and have granted a token
          if(token.equals("")){
              return Response.status(400).entity("You're not logged in yet").build();
          }

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

          String tokenCheck = usertoUpdate.getUsername() + usertoUpdate.getEmail();

          tokenCheck = hashing.hashWithSaltSHA(tokenCheck);

          if(tokenCheck.equals(token)){
              //Malthe: Updating
              UserController.updatePassword(id, newPassword);

              //Malthe: Forcing a update in the cache since the list of users has changed
              userCache.getUsers(true);

              return Response.status(200).entity("Password is updated and hashed").build();
          }


      }catch (Exception e){
          return Response.status(400).entity("User not found").build();
      }

      return Response.status(400).entity("You can only update your own password").build();
  }

}
