package com.wonjin.computer.hw4projectwonjin.commonWonjin;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by computer on 2019-02-15.
 */

public abstract class DatabaseBroker {

    public interface OnDataBrokerListener{
        public void onChange(String databaseStr);
    }

    // group -----------------------------------------------------------------------
    protected String  groupDatabaseStr = "";
    protected OnDataBrokerListener groupDataBrokerListener = null;

    // user -----------------------------------------------------------------------
    protected String  userDatabaseStr = "";
    protected OnDataBrokerListener userOnDataBrokerListener = null;

    // booking -----------------------------------------------------------------------
    protected String  bookingDatabaseStr = "";
    protected OnDataBrokerListener bookingOnDataBrokerListener = null;
    protected  String      userGroup;

    // settings -----------------------------------------------------------------------
    protected String  settingsDatabaseStr = "";
    protected OnDataBrokerListener settingsOnDataBrokerListener = null;

    // database -----------------------------------------------------------------------
    DatabaseBroker.OnDataBrokerListener checkOnDataBrokerListener = null;

    public abstract  void setGroupOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener);
    public abstract ArrayList<String> loadGroupDatabase(Context context);
    public abstract void saveGroupDatabase(Context context, ArrayList<String> groupDatabase);

    public abstract  void setUserOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener);
    public abstract ArrayList<User> loadUserDatabase(Context context);
    public abstract void saveUserDatabase(Context context, ArrayList<User> userDatabase);

    public abstract  void setBookingOnDataBrokerListener(Context context, String userGroup, OnDataBrokerListener onDataBrokerListener);
    public abstract String[] loadBookingDatabase(Context context, String userGroup);
    public abstract void saveBookingDatabase(Context context, String userGroup, String[] bookingDatabase);

    public abstract  void setSettingsOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener);
    public abstract Settings loadSettingsDatabase(Context context);
    public abstract void saveSettingsDatabase(Context context, Settings settingsDatabase);

    public abstract void setCheckDatabaseRoot(DatabaseBroker.OnDataBrokerListener onDataBrokerListener);
    public abstract void resetDatabase(Context context);


    public  String rootPath = "";
    public static DatabaseBroker createDatabaseObject(String rootPath){
        DatabaseBroker  databaseBroker;
        if(rootPath.equals("test")){
            databaseBroker = new FilebaseBroker();
        }else{
            databaseBroker = new FirebaseBroker();    // it will be replaced as FirebaseBroker
        }
        databaseBroker.rootPath = rootPath;
        return databaseBroker;
    }
}
