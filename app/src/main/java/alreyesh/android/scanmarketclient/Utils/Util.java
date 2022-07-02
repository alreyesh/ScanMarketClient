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
    public static Float getPurchaseLimit(SharedPreferences preferences){
        return preferences.getFloat("limitp",0.0f);
    }
    public static Integer getSelectPurchase(SharedPreferences preferences){
        return preferences.getInt("sp",0);
    }
    public static Boolean getCreateOrEditPurchase(SharedPreferences preferences){
        return preferences.getBoolean("cep",false);
    }


    public static String getTotalCart(SharedPreferences preferences){
        return preferences.getString("total","");
    }
    public static String getProductFromCamera(SharedPreferences preferences){
        return preferences.getString("productcamera","");
    }
    public static String getCodigoFromCamera(SharedPreferences preferences){
        return preferences.getString("codigocamera","");
    }
    public static String getImagenFromCamera(SharedPreferences preferences){
        return preferences.getString("imagencamera","");
    }
    public static String getPrecioFromCamera(SharedPreferences preferences){
        return preferences.getString("preciocamera","");
    }
    public static Boolean getStartNotification(SharedPreferences preferences){
        return preferences.getBoolean("startnotify",false);
    }


    public static void removeSharedPreferences(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("email");
        editor.remove("pass");
        editor.apply();
    }








}
