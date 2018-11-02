package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it : fixed
//Used in orderendpoint in methods getOrders and createOrder
public class OrderCache {

    private ArrayList<Order> orders;

    private long createdTime;

    private long ttl;

    public OrderCache(){
        this.ttl = Config.getCacheTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate){
        if(forceUpdate || (this.ttl + this.createdTime) <= (System.currentTimeMillis()/1000L)
        || orders == null){

            ArrayList<Order> actualListOfOrders = OrderController.getOrders();

            this.orders = actualListOfOrders;
            this.createdTime = System.currentTimeMillis()/1000L;
        }

        return this.orders;
    }

}
