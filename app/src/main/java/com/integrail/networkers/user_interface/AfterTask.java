package com.integrail.networkers.user_interface;


import com.integrail.networkers.primary_operations.networking.NetworkConnection;

/**
 * Created by integrailwork on 5/20/17.
 */

public interface AfterTask {
    public void update(NetworkConnection connection, int index);
}
