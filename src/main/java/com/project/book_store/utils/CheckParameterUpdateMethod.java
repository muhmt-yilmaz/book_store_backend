package com.project.book_store.utils;


import com.project.book_store.entity.abstracts.User;
import com.project.book_store.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod {

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){

        return user.getTc().equalsIgnoreCase(baseUserRequest.getTc())
                && user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber())
                && user.getEmail().equalsIgnoreCase(baseUserRequest.getEmail());
    }
}
