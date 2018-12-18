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

    private static UserCache userCache = new UserCache();
    public static Hashing hashing = new Hashing();
    private static ArrayList<User> usersInCache = userCache.getUsers(false);

    public static ArrayList<User> getUsersInCache() { return usersInCache; }

    //Malthe: added standard response if user don't insert token i the URL
    /** @return Responses */
    @GET
    @Path("")
    public Response standardAnswer(){
        return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity("You need a session ID to view usersInCache \n" +
                "You can access it at the following path: /user/login").build();
    }

    /**
     * @param token
     * @return Responses
     */
    @GET
    @Path("/byID/{token}")
    public Response getUser (@PathParam("token") String token){

        // TODO: What should happen if something breaks down? : fixed

        try {

            if(token.equals("")){
                return Response.status(400).entity("No token").build();
            }

            String json;

            for (User user : usersInCache) {
                if (user.getToken() != null && user.getToken().equals(token)) {

                    json = new Gson().toJson(user);
                    // Return the user with the status code 200
                    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
                }
            }

            json = new Gson().toJson(usersInCache);

            // TODO: Add Encryption to JSON : fixed
            //Add encryption to json rawString object(ref. utils Encryption)
            json = Encryption.encryptDecryptXOR(json);
            return Response.status(400).entity("Session ID not valid\n" + json).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(400).entity("Something went wrong").build();
        }

    }

    /** @return Responses */
    @GET
    @Path("/{token}")
    public Response getUsers (@PathParam("token") String token){

        try{
        //Malthe: Get a list of usersInCache from the cache function
        ArrayList<User> usersWithoutToken = new ArrayList<>();

        boolean check = true;

        //Malthe: Checks if the granted token is valid in DB
        for (User user : usersInCache) {
            if (user.getToken() != null && user.getToken().equals(token)) {

                //Malthe: sets the check to false so the json String object is not encrypted if a valid token is granted
                check = false;

                // Write to log that we are here
                Log.writeLog(this.getClass().getName(), this, "Get all usersInCache", 0);
            }
            //Malthe: Create new User object without password and token
            User user1 = new User(0, user.getFirstname(), user.getLastname(), null, user.getEmail(), user.getCreatedTime(), user.getUsername(), null);

            //Malthe: Add to the arraylist that shoukd be printed
            usersWithoutToken.add(user1);
        }
        // Transfer usersInCache to json in order to return it to the user
        String json = new Gson().toJson(usersWithoutToken);

        // TODO: Add Encryption to JSON : fixed
        //Malthe: Add encryption to json rawString object(ref. utils Encryption) if no user is found
        if (check) {
            json = Encryption.encryptDecryptXOR(json);
            // Write to log that we are here
            Log.writeLog(this.getClass().getName(), this, "Get all usersInCache encrypted", 0);
        }

        // Return the usersInCache with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();

        }catch (Exception e){
            return Response.status(400).entity("Couldn't find usersInCache").build();
        }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser (String body){

        try {
            // Read the json from body and transfer it to a user class
            User newUser = new Gson().fromJson(body, User.class);

            // Use the controller to add the user
            User createUser = UserController.createUser(newUser);

            //Malthe: Force update of the user cache, since there is a new user in the ArrayList of usersInCache
            usersInCache = userCache.getUsers(true);

            // Get the user back with the added ID and return it to the user
            String json = new Gson().toJson(createUser);

            // Checks if the username is already taken
            if (createUser != null) {
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            } else {
                return Response.status(400).entity("Could not create user - username might have been taken").build();
            }
        } catch (Exception e) {
            return Response.status(400).entity("Username already taken").build();
        }

    }

    // TODO: Make a smart way of login in without having to enter ID, maybe not possible : fixed (implemented username)
    // TODO: Make the system able to login usersInCache and assign them a token to use throughout the system : fixed
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser (String login){

        User userLogin = new Gson().fromJson(login, User.class);

        String token = UserController.auth(userLogin);

        //Malthe: Cache update since token will be updated
        usersInCache = userCache.getUsers(true);

        if (token != null) {

            return Response.status(200).entity("Your token is:\n" + token).build();
        }

        // Return a response with status 200 and JSON as type
        return Response.status(400).entity("Not valid login attempt. Please match your input").build();
    }

    // TODO: Make the system able to delete usersInCache : fixed
    @POST
    @Path("/delete/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser (@PathParam("token") String token){

        //Malthe: Autherise the usersInCache token
        for (User user : usersInCache) {
            if (user.getToken() != null && user.getToken().equals(token)) {

                //Malthe: Calls method in usercontroller that deletes found user
                UserController.deleteUser(user.getId());

                //Malthe: Update of the user cache, since there is deleted a User in the ArrayList of usersInCache
                usersInCache = userCache.getUsers(true);

                // Return a response with status 200 and JSON as type
                return Response.status(200).entity("User with id " + user.getId() + " is now deleted").build();
            }

        }

        return Response.status(400).entity("Your session ID is not valid").build();
    }

    // TODO: Make the system able to update usersInCache : fixed
    @POST
    @Path("/update/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser (String updatedUserData, @PathParam("token") String token){

        User updatedUserDataObj = new Gson().fromJson(updatedUserData, User.class);

        for (User user : usersInCache) {
            if (user.getToken() != null && user.getToken().equals(token)) {

                //Malthe: Calls method in usercontroller that updates with new info
                UserController.updateUser(user, updatedUserDataObj);

                //Malthe: Update of the user cache, since there is new information in the ArrayList of usersInCache
                userCache.getUsers(true);

                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
                        "User with ID " + user.getId() + " is now updated to:\n" +
                                "Firstname: " + updatedUserDataObj.getFirstname() + "\n" +
                                "Lastname: " + updatedUserDataObj.getLastname() + "\n" +
                                "Email: " + updatedUserDataObj.getEmail()).build();
            }
        }

        return Response.status(400).entity("Your session ID is not valid").build();
    }

    @POST
    @Path("/updatepassword/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword (String passwordUpdate, @PathParam("token") String token){

        //Malthe: The inputtet password is saved in a user object and the password is saved in a string
        User passwordupdate = new Gson().fromJson(passwordUpdate, User.class);
        String password = passwordupdate.getPassword();

        for (User user : usersInCache) {
            if (user.getToken() != null && user.getToken().equals(token)) {
                //Malthe: Getting the user which password needs to be updated to currentuser
                User usertoUpdate = UserController.getUser(user.getId());

                //Malthe: Setting the salt to currentusers created time
                hashing.setSalt(String.valueOf(usertoUpdate.getCreatedTime()));

                //Malthe: Hashing the new password with createdtime as the salt
                String newPassword = hashing.hashWithSaltSHA(password);

                //Malthe: If the new password is not "new" or too short
                if (usertoUpdate.getPassword().equals(newPassword) || password.length() < 7) {
                    return Response.status(400).entity("Password must be new and 6 characters long").build();
                } else {
                    //Malthe: Updating
                    UserController.updatePassword(user.getId(), newPassword);

                    //Malthe: Forcing a update in the cache since the list of usersInCache has changed
                    userCache.getUsers(true);

                    return Response.status(200).entity("Password is updated and hashed").build();
                }
            }
        }

        return Response.status(400).entity("Session ID not valid").build();

    }

    @GET
    @Path("/logout/{token}")
    public Response logout (@PathParam("token") String token){

        for (User user : usersInCache) {
            if (user.getToken() != null && user.getToken().equals(token)) {

                UserController.logout(user);

                //Malthe: Cache update since token will be updated
                userCache.getUsers(true);

                return Response.status(200).entity("You are now logged out").build();

            }
        }

        // Return a response with status 200 and JSON as type
        return Response.status(400).entity("Session ID not valid").build();
    }

}
