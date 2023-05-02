package alreyesh.android.scanmarketclient.utils;


import android.content.Context;
import android.content.SharedPreferences;



public class Util {
    public static SharedPreferences getSP(Context context){
        return context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);

    }


    public  static String getUserAccount(SharedPreferences preferences){
        return preferences.getString("account"," ");
    }
    public static String getUserMailPrefs(SharedPreferences preferences){
        return preferences.getString("email"," ");
    }
    public static String getProduct(SharedPreferences preferences){
        return preferences.getString("productos","");
    }

    public static String getUserPassPrefs(SharedPreferences preferences){
        return preferences.getString("pass"," ");
    }

    public static Integer getPurchaseId(SharedPreferences preferences){
        return preferences.getInt("idp" ,-1);
    }
    public static String getPurchaseName(SharedPreferences preferences){
        return preferences.getString("np","");
    }
    public static Integer getPurchaseColor(SharedPreferences preferences){
        return preferences.getInt("cp",-1);
    }
    public static Float getPurchaseLimit(SharedPreferences preferences){
        return preferences.getFloat("limitp",0.0f);
    }
    public static Integer getSelectPurchase(SharedPreferences preferences){
        return preferences.getInt("sp",0);
    }
    public static Integer getIconPurchase(SharedPreferences preferences){
        return preferences.getInt("iconp",0);
    }
    public static Boolean getCreateOrEditPurchase(SharedPreferences preferences){
        return preferences.getBoolean("cep",false);
    }
    public static Boolean getDecisionPurchase(SharedPreferences preferences){
        return preferences.getBoolean("newp",false);
    }

    public static String getTotalCart(SharedPreferences preferences){
        return preferences.getString("total","");
    }
    public static String getProductFromCamera(SharedPreferences preferences){
        return preferences.getString("productcamera","");
    }
    public static String getPredictFromCamera(SharedPreferences preferences){
        return preferences.getString("predictcamera","");
    }
    public static String getCategoryFromCamera(SharedPreferences preferences){
        return preferences.getString("categorycamera","");
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
    public static String getOrderCod(SharedPreferences preferences){
        return preferences.getString("ordercod","");
    }
    public static String getOrderDate(SharedPreferences preferences){
        return preferences.getString("orderdate","");
    }
    public static String getOrderTotal(SharedPreferences preferences){
        return preferences.getString("ordertotal","");
    }
    public static String getOrderDetail(SharedPreferences preferences){
        return preferences.getString("orderdetail","");
    }

    public static Boolean getNotiTurn(SharedPreferences preferences){
        return preferences.getBoolean("turnnoti",false);
    }

    public static String getTitle(SharedPreferences preferences){
        return preferences.getString("titlenoti","");
    }
    public static String getTitleShort(SharedPreferences preferences){
        return preferences.getString("shortnoti","");
    }
    public static String getDescrip(SharedPreferences preferences){
        return preferences.getString("descripnoti","");
    }

    public static String getQRCode(SharedPreferences preferences){
        return preferences.getString("qr","");
    }
    public static Boolean getStateQr(SharedPreferences preferences){
        return preferences.getBoolean("qrstate",false);

    }

    public static void removeSharedPreferences(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static Boolean getTurnNotify(SharedPreferences preferences){
    return preferences.getBoolean("turnm",true);
    }







}
