package com.wonjin.computer.hw4projectwonjin.commonWonjin;

/**
 * Created by computer on 2019-02-08.
 */

public class Settings {

    public int maxContinueBookingSlots;
    public int maxTotalBookingSlots;

    public Settings(){
        maxContinueBookingSlots = 2;
        maxTotalBookingSlots = 4;
    }

    public Settings(String str){
        maxContinueBookingSlots = 2;
        maxTotalBookingSlots = 4;

        String strs[] = str.split("####");
        for(int i=0;i<strs.length;i++){
            if(strs[i].length() == 0)   continue;
            String[] components = strs[i].split(":");
            if(components[0].equals("maxContinueBookingSlots")){
                maxContinueBookingSlots = Integer.parseInt(components[1]);
            }
            if(components[0].equals("maxTotalBookingSlots")){
                maxTotalBookingSlots = Integer.parseInt(components[1]);
            }
        }
    }
    public String toString(){
        String str = "";
        str += "maxContinueBookingSlots"+":"+maxContinueBookingSlots;
        str += "####";
        str += "maxTotalBookingSlots"+":"+maxTotalBookingSlots;

        return str;
    }
}