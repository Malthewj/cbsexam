package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it : fixed
//Malthe: Used in user endpoint in methods: getUsersInCache, createUser, deleteUser, updateUser
public class UserCache {

    private ArrayList<User> users;

    private long createdTime;

    private long ttl;

    public UserCache(){
        this.ttl = Config.getCacheTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate){

        if(forceUpdate || (this.createdTime + this.ttl) <= (System.currentTimeMillis() / 1000L)
        || this.users == null){
            ArrayList<User> actualUserArrayList = UserController.getUsers();

            this.users = actualUserArrayList;
            this.createdTime = System.currentTimeMillis() / 1000L;
        }

        return this.users;
    }

}
