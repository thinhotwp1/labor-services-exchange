package com.example.config;


import com.example.model.TypeUser;

public class UserCurrent {

    private static final ThreadLocal<TypeUser> currentUser = new ThreadLocal<>();

    public static void setTypeUser(TypeUser typeUser) {
        currentUser.set(typeUser);
    }

    public static TypeUser getCurrentUser() {
        return currentUser.get();
    }
}
