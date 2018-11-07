package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cache.UserCache;
import model.User;
import utils.Hashing;
import utils.Log;

import javax.ws.rs.core.Response;

public class UserController {

    private static UserCache userCache = new UserCache();
  private static DatabaseController dbCon;
  private static Hashing hashing = new Hashing();

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    //Malthe: Added created_at and username which is used in login method
    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"),
                    rs.getString("username"),
                    rs.getString("token"));


        // return the create object
        return user;
      } else {

        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    //Malthe: Added created_at and username which is used in login method
    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"),
                    rs.getString("username"),
                    rs.getString("token"));

        user.setToken(null);
        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    Hashing hashing = new Hashing();

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it : fixed
    hashing.setSalt(String.valueOf(user.getCreatedTime()));

    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at, username) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
            + hashing.hashWithSaltSHA(user.getPassword())
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ", '"
            + user.getUsername() + "')");



    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

  public static void deleteUser(int id){
    Log.writeLog(UserController.class.getName(), id, "Deleting a user in DB", 0);

    if(dbCon == null){
      dbCon = new DatabaseController();
    }

    dbCon.deleteUpdate("DELETE FROM user WHERE id = " + id);

  }


  public static void updateUser(int userUpdatingID, User userUpdates) {

      User currentUser = getUser(userUpdatingID);

    Log.writeLog(UserController.class.getName(), currentUser, "Actually updating a user in DB", 0);


      if(userUpdates.getEmail()==null){
          userUpdates.setEmail(currentUser.getEmail());
      }
      if(userUpdates.getLastname()==null){
          userUpdates.setLastname(currentUser.getLastname());
      }
      if(userUpdates.getFirstname()==null){
          userUpdates.setFirstname(currentUser.getFirstname());
      }

    if(dbCon == null){
      dbCon = new DatabaseController();
    }

    //Malthe: Creating the SQL statement
    String sql = "UPDATE user SET first_name = '" + userUpdates.getFirstname() + "'" +
            ", last_name = '" + userUpdates.getLastname() + "'" +
            ", email = '" + userUpdates.getEmail() + "'" +
            " WHERE id = " + userUpdatingID;

    dbCon.deleteUpdate(sql);

  }

    public static void updatePassword(int id, String password) {

      Log.writeLog(UserController.class.getName(), id, "Updating users password", 0);

      if(dbCon == null){
          dbCon = new DatabaseController();
      }

      String sql = "UPDATE user SET password = '" + password + "' WHERE id = " + id;

      dbCon.deleteUpdate(sql);
    }

    public static String auth(User userLogin) {

        //Malthe: All users are getted in an arraylist
        ArrayList<User> users = getUsers();

        for (User user : users) {
            if (user.getUsername().equals(userLogin.getUsername())) {

                //Malthe: Created time is set to be the salt
                hashing.setSalt(String.valueOf(user.getCreatedTime()));

                //Malthe: The password string is hashed with a salt
                String password = hashing.hashWithSaltSHA(userLogin.getPassword());

                if(user.getPassword().equals(password)){

                    String token = user.getUsername()+user.getEmail();

                    hashing.setSalt(String.valueOf(System.currentTimeMillis()/1000L));

                    token = hashing.hashWithSaltSHA(token);

                    if(dbCon == null){
                        dbCon = new DatabaseController();
                    }

                    dbCon.deleteUpdate("UPDATE user SET token = '" + token + "' WHERE id= " + user.getId());

                    return token;
                }

            }
        }
        return null;
    }

}