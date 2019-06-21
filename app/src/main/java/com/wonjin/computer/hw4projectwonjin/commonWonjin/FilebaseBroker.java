package com.wonjin.computer.hw4projectwonjin.commonWonjin;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by computer on 2019-02-15.
 */

public class FilebaseBroker extends DatabaseBroker {

    String  defaultGroup = "한성대테니스장@@@@제1스터디룸@@@@제2스터디룸@@@@세미나실";
    String  defaultUser = "root####root####@@@@gdhong####gdhong####한성대테니스장";
    String  defaultSettings = "maxContinueBookingSlots:2####maxTotalBookingSlots:4";

    // group -----------------------------------------------------------------------
    public  void setGroupOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener){

        groupDataBrokerListener = onDataBrokerListener;

        try {
            groupDatabaseStr = defaultGroup;

            String fileName = rootPath + "-groupDatabase.txt";
            FileInputStream fileInputStream = context.openFileInput(fileName);
            if(fileInputStream.available() > 0) {
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                groupDatabaseStr = new String(buffer);
            }
            fileInputStream.close();
        }catch(IOException e){
        }

        if(groupDataBrokerListener != null){
            groupDataBrokerListener.onChange(groupDatabaseStr);
        }
    }

    public ArrayList<String> loadGroupDatabase(Context context){

        String[] groupDatabase = groupDatabaseStr.split("@@@@");

        ArrayList<String>   arrayList = new ArrayList<>();
        for(int i=0;i<groupDatabase.length;i++){
            if(groupDatabase[i].length() == 0)  continue;
            arrayList.add(groupDatabase[i]);
        }

        return arrayList;
    }

    public void saveGroupDatabase(Context context, ArrayList<String> groupDatabase){
        groupDatabaseStr = "";
        for(int i=0;i<groupDatabase.size();i++){
            if(groupDatabase.get(i).length() == 0) continue;
            groupDatabaseStr += groupDatabase.get(i);
            if(i != groupDatabase.size()-1){
                groupDatabaseStr += "@@@@";
            }
        }
        try {
            String fileName = rootPath + "-groupDatabase.txt";
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(groupDatabaseStr.getBytes());
            fileOutputStream.close();
        }catch(IOException e){
        }

        if(groupDataBrokerListener != null){
            groupDataBrokerListener.onChange(groupDatabaseStr);
        }
    }

    // user -----------------------------------------------------------------------
    public  void setUserOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener){
        userOnDataBrokerListener = onDataBrokerListener;

        try {
            userDatabaseStr = defaultUser;

            String fileName = rootPath + "-userDatabase.txt";
            FileInputStream fileInputStream = context.openFileInput(fileName);
            if(fileInputStream.available() > 0) {
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                userDatabaseStr = new String(buffer);
            }
            fileInputStream.close();
        }catch(IOException e){
        }

        if(userOnDataBrokerListener != null){
            userOnDataBrokerListener.onChange(userDatabaseStr);
        }
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
        try {
            String fileName = rootPath + "-userDatabase.txt";
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(userDatabaseStr.getBytes());
            fileOutputStream.close();
        }catch(IOException e){
        }

        if(userOnDataBrokerListener != null){
            userOnDataBrokerListener.onChange(userDatabaseStr);
        }
    }


    // booking -----------------------------------------------------------------------
    public  void setBookingOnDataBrokerListener(Context context, String userGroup, OnDataBrokerListener onDataBrokerListener){

        this.userGroup = userGroup;
        bookingOnDataBrokerListener = onDataBrokerListener;

        try {
            bookingDatabaseStr = "";

            String  fileName = rootPath+"-bookingDatabase-"+userGroup+".txt";
            FileInputStream fileInputStream = context.openFileInput(fileName);
            if(fileInputStream.available() > 0) {
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                bookingDatabaseStr = new String(buffer);
            }
            fileInputStream.close();
        }catch(IOException e){

        }

        if(bookingOnDataBrokerListener != null){
            bookingOnDataBrokerListener.onChange(bookingDatabaseStr);
        }
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

        Calendar calendar = Calendar.getInstance();
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

        try {
            String  fileName = rootPath+"-bookingDatabase-"+userGroup+".txt";
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(bookingDatabaseStr.getBytes());
            fileOutputStream.close();
        }catch(IOException e){
        }

        if(bookingOnDataBrokerListener != null){
            bookingOnDataBrokerListener.onChange(bookingDatabaseStr);
        }
    }

    // settings -----------------------------------------------------------------------
    public  void setSettingsOnDataBrokerListener(Context context, OnDataBrokerListener onDataBrokerListener){
        settingsOnDataBrokerListener = onDataBrokerListener;

        try {
            settingsDatabaseStr = defaultSettings;

            String  fileName = rootPath+"-settingsDatabase.txt";
            FileInputStream fileInputStream = context.openFileInput(fileName);
            if(fileInputStream.available() > 0) {
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                settingsDatabaseStr = new String(buffer);
            }
            fileInputStream.close();
        }catch(IOException e){
        }

        if(settingsOnDataBrokerListener != null){
            settingsOnDataBrokerListener.onChange(settingsDatabaseStr);
        }
    }

    public Settings loadSettingsDatabase(Context context){
        Settings settings = new Settings(settingsDatabaseStr);
        return settings;
    }

    public void saveSettingsDatabase(Context context, Settings settingsDatabase){
        settingsDatabaseStr = settingsDatabase.toString();

        try {
            String  fileName = rootPath+"-settingsDatabase.txt";
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(settingsDatabaseStr.getBytes());
            fileOutputStream.close();
        }catch(IOException e){
        }

        if(settingsOnDataBrokerListener != null){
            settingsOnDataBrokerListener.onChange(settingsDatabaseStr);
        }
    }


    public void setCheckDatabaseRoot(DatabaseBroker.OnDataBrokerListener onDataBrokerListener){

        checkOnDataBrokerListener = onDataBrokerListener;
        if (checkOnDataBrokerListener != null) {
            checkOnDataBrokerListener.onChange("test");
        }
    }

    public void resetDatabase(Context context){

        String[] fileNames = context.fileList();
        for(int i=0;i<fileNames.length;i++) {
            context.deleteFile(fileNames[i]);
        }

    }
}

