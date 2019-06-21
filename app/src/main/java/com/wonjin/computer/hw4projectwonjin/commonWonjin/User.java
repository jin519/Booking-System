package com.wonjin.computer.hw4projectwonjin.commonWonjin;

/**
 * Created by computer on 2019-02-08.
 */

public class User {
    public String  userName;
    public String  userPassword;
    public String  userGroup;

    public User(String str){
        init(str);
    }

    public User(String userName, String userPassword, String userGroup){
        this.userName = userName;
        this.userPassword = userPassword;
        this.userGroup = userGroup;
    }

    public void init(String str){
        String[] strs = str.split("####");

        userName = "";
        userPassword = "";
        userGroup = "";

        if(strs.length == 0)    return;
        if(strs.length == 1){
            userName = (strs[0] != null)? strs[0]: "";
            return;
        }
        if(strs.length == 2){
            userName = (strs[0] != null)? strs[0]: "";
            userPassword = (strs[1] != null)? strs[1]: "";
            return;
        }
        userName = (strs[0] != null)? strs[0]: "";
        userPassword = (strs[1] != null)? strs[1]: "";
        userGroup = (strs[2] != null)? strs[2]: "";
    }

    public String toString(){
        return userName + "####" + userPassword + "####" + userGroup;
    }

    public boolean isMeByName(String name){
        return name.equals(userName);
    }

    public boolean isMeByPassword(String password){
        return password.equals(userPassword);
    }

    public boolean isMeByGroup(String group) {
        return userGroup.equals(group);
    }

}
