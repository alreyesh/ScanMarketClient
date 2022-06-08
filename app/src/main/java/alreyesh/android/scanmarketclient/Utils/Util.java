package alreyesh.android.scanmarketclient.Utils;

import android.content.SharedPreferences;

public class Util {
    public  static String getUserAccount(SharedPreferences preferences){
        return preferences.getString("account","");
    }
    public static String getUserMailPrefs(SharedPreferences preferences){
        return preferences.getString("email","");
    }
    public static String getProduct(SharedPreferences preferences){
        return preferences.getString("productos","");
    }

    public static String getUserPassPrefs(SharedPreferences preferences){
        return preferences.getString("pass","");
    }

    public static Integer getPurchaseId(SharedPreferences preferences){
        return preferences.getInt("idp" ,0);
    }
    public static String getPurchaseName(SharedPreferences preferences){
        return preferences.getString("np","");
    }
    public static Integer getPurchaseColor(SharedPreferences preferences){
        return preferences.getInt("cp",0);
    }
    public static String getTotalCart(SharedPreferences preferences){
        return preferences.getString("total","");
    }


    public static void removeSharedPreferences(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("email");
        editor.remove("pass");
        editor.apply();
    }








}
