package com.wonjin.computer.hw4projectwonjin.commonWonjin;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by computer on 2019-02-15.
 */

public class FirebaseBroker extends DatabaseBroker {
    String  defaultGroup = "제1스터디그룹@@@@제2스터디그룹@@@@제3스터디그룹@@@@제4스터디그룹";
    String  defaultUser = "root####root####@@@@jmlee####jmlee####제1스터디그룹";
    String  defaultSettings = "maxContinueBookingSlots:2####maxTotalBookingSlots:4";

    // group -----------------------------------------------------------------------
    public void setGroupOnDataBrokerListener(Context context, DatabaseBroker.OnDataBrokerListener onDataBrokerListener) {

        groupDataBrokerListener = onDataBrokerListener;

        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("group");
        databaseReferenceForGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupDatabaseStr = dataSnapshot.getValue(String.class);
                if (groupDatabaseStr == null) {
                    groupDatabaseStr = defaultGroup;
                }

                if (groupDataBrokerListener != null) {
                    groupDataBrokerListener.onChange(groupDatabaseStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                groupDatabaseStr = defaultGroup;
            }
        });
    }

    public ArrayList<String> loadGroupDatabase(Context context) {

        String[] groupDatabase = groupDatabaseStr.split("@@@@");

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < groupDatabase.length; i++) {
            if (groupDatabase[i].length() == 0) continue;
            arrayList.add(groupDatabase[i]);
        }

        return arrayList;
    }

    public void saveGroupDatabase(Context context, ArrayList<String> groupDatabase) {

        groupDatabaseStr = "";
        for (int i = 0; i < groupDatabase.size(); i++) {
            if (groupDatabase.get(i).length() == 0) continue;
            groupDatabaseStr += groupDatabase.get(i);
            if (i != groupDatabase.size() - 1) {
                groupDatabaseStr += "@@@@";
            }
        }

        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("group");
        databaseReferenceForGroup.setValue(groupDatabaseStr);
        databaseReferenceForGroup.keepSynced(true);
    }

    // user -----------------------------------------------------------------------
    public void setUserOnDataBrokerListener(Context context, DatabaseBroker.OnDataBrokerListener onDataBrokerListener){

        userOnDataBrokerListener = onDataBrokerListener;

        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("user");
        databaseReferenceForGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("jmlee", "here1"+rootPath);
                userDatabaseStr = dataSnapshot.getValue(String.class);
                if (userDatabaseStr == null) {
                    userDatabaseStr = defaultUser;
                }

                if (userOnDataBrokerListener != null) {
                    userOnDataBrokerListener.onChange(userDatabaseStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("jmlee", "here2");
                Log.i("jmlee", error.getMessage());
                userDatabaseStr = defaultUser;
            }
        });
    }

    public ArrayList<User> loadUserDatabase(Context context){
        String[] userDatabase = userDatabaseStr.split("@@@@");
        ArrayList<User>   arrayList = new ArrayList<>();


        for(int i=0;i<userDatabase.length;i++){
            if(userDatabase[i].length() == 0)  continue;
            arrayList.add(new User(userDatabase[i]));
        }

        return arrayList;
    }

    public void saveUserDatabase(Context context, ArrayList<User> userDatabase){
        userDatabaseStr = "";
        for(int i=0;i<userDatabase.size();i++){
            userDatabaseStr += userDatabase.get(i).toString();
            if(i != userDatabase.size()-1){
                userDatabaseStr += "@@@@";
            }
        }
        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("user");
        databaseReferenceForGroup.setValue(userDatabaseStr);
        databaseReferenceForGroup.keepSynced(true);
    }


    // booking -----------------------------------------------------------------------
    public void setBookingOnDataBrokerListener(Context context, String userGroup, DatabaseBroker.OnDataBrokerListener onDataBrokerListener){

        this.userGroup = userGroup;
        bookingOnDataBrokerListener = onDataBrokerListener;

        DatabaseReference databaseReferenceForBooking = FirebaseDatabase.getInstance().getReference().child(rootPath).child("booking").child(userGroup);
        databaseReferenceForBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingDatabaseStr = dataSnapshot.getValue(String.class);

                if(bookingDatabaseStr == null){
                    bookingDatabaseStr = "";
                }

                if(bookingOnDataBrokerListener != null){
                    bookingOnDataBrokerListener.onChange(bookingDatabaseStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                bookingDatabaseStr = "";
            }
        });
    }

    public String[] loadBookingDatabase(Context context, String userGroup){

        String[]   bookingDatabase = new String[50];
        for(int i=0;i<bookingDatabase.length;i++){
            bookingDatabase[i] = "";
        }

        if(bookingDatabaseStr.length()==0 || userGroup != this.userGroup){
            return bookingDatabase;
        }

        String[] bookings = bookingDatabaseStr.split("@@@@");
        for(int i=1;i<bookings.length;i++){
            if(bookings[i].length()==0){
                continue;
            }
            String[] info= bookings[i].split(":");
            int        time = Integer.parseInt(info[0]);    // 330: 3시 30분
            int     index = (time/100)*2+((time%100)/30);
            bookingDatabase[index] = info[1];
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);

        if(!bookings[0].equals(dateStr)) {
            bookingDatabase[0] = bookingDatabase[48];
            bookingDatabase[1] = bookingDatabase[49];

            for(int i=2;i<bookingDatabase.length;i++){
                bookingDatabase[i]= "";
            }
        }

        return bookingDatabase;
    }

    public void saveBookingDatabase(Context context, String userGroup, String[] bookingDatabase){

        Calendar    calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);

        bookingDatabaseStr = dateStr;
        for(int i=0;i<bookingDatabase.length;i++){
            if(bookingDatabase[i].length() == 0)    continue;

            bookingDatabaseStr += "@@@@";
            int index = (i/2)*100+(i%2)*30;
            bookingDatabaseStr += index + ":" + bookingDatabase[i];
        }

        this.userGroup = userGroup;

        DatabaseReference databaseReferenceForBooking = FirebaseDatabase.getInstance().getReference().child(rootPath).child("booking").child(userGroup);
        databaseReferenceForBooking.setValue(bookingDatabaseStr);
        databaseReferenceForBooking.keepSynced(true);
    }

    // settings -----------------------------------------------------------------------
    public void setSettingsOnDataBrokerListener(Context context, DatabaseBroker.OnDataBrokerListener onDataBrokerListener){

        settingsOnDataBrokerListener = onDataBrokerListener;

        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("settings");
        databaseReferenceForGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingsDatabaseStr = dataSnapshot.getValue(String.class);
                if (settingsDatabaseStr == null) {
                    settingsDatabaseStr = defaultSettings;
                }

                if (settingsOnDataBrokerListener != null) {
                    settingsOnDataBrokerListener.onChange(settingsDatabaseStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                settingsDatabaseStr = defaultSettings;
            }
        });
    }

    public Settings loadSettingsDatabase(Context context){
        Settings settings = new Settings(settingsDatabaseStr);
        return settings;
    }

    public void saveSettingsDatabase(Context context, Settings settingsDatabase){

        settingsDatabaseStr = settingsDatabase.toString();

        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child(rootPath).child("settings");
        databaseReferenceForGroup.setValue(settingsDatabaseStr);
        databaseReferenceForGroup.keepSynced(true);
    }

    public void setCheckDatabaseRoot(DatabaseBroker.OnDataBrokerListener onDataBrokerListener){

        checkOnDataBrokerListener = onDataBrokerListener;
        Log.i("jmlee", "here");
        DatabaseReference databaseReferenceForGroup = FirebaseDatabase.getInstance().getReference().child("databaseRoot");
        databaseReferenceForGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String databaseRootStr = dataSnapshot.getValue(String.class);

                String[] databaseRoots = databaseRootStr.split("@@@@");
                for (int i = 0; i < databaseRoots.length; i++) {
                    if (rootPath.equals(databaseRoots[i])) {
                        if (checkOnDataBrokerListener != null) {
                            checkOnDataBrokerListener.onChange(databaseRoots[i]);
                            return;
                        }
                    }
                }

                if (checkOnDataBrokerListener != null) {
                    checkOnDataBrokerListener.onChange("");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                if (checkOnDataBrokerListener != null) {
                    checkOnDataBrokerListener.onChange("");
                }
            }
        });
    }


    public void resetDatabase(Context context){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(rootPath);
        databaseReference.removeValue();

    }
}
