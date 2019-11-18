package com.far.virtualmenu.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.virtualmenu.Generic.KV2;
import com.far.virtualmenu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Funciones {

    public static String generateCode(){
        Calendar calendar = Calendar.getInstance();
        String year =  String.format("%04d", calendar.get(Calendar.YEAR));
        String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String hour =  String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minute =  String.format("%02d", calendar.get(Calendar.MINUTE));
        String seconds = String.format("%02d", calendar.get(Calendar.SECOND));
        String milliseconds = String.format("%03d", calendar.get(Calendar.MILLISECOND));
        String data = year+month+day+hour+minute+seconds+milliseconds;
        return data;
    }
    public static String getPhoneID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static Date sumaDiasFecha(int dias){
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }
    public static void showKeyBoard(final EditText et){
        et.post(new Runnable() {
            @Override
            public void run() {
                final InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                et.requestFocus(); // needed if you have more then one input
            }
        });
    }

    public static void hideKeyBoard(EditText et, Context c){
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static String getFormatedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
    public static String getFormatedDate(Date d){
        if(d == null){
            return null;
        }
        return getSimpleDateFormat().format(d);
    }

    public static String getFormatedDateNoTime(Date d){
        if(d == null){
            return null;
        }
        return  new SimpleDateFormat("yyyyMMdd").format(d);
    }

    public static String getFormatedDateRepDom(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
    public static String getFormatedDateRepDomHour(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return sdf.format(date);
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    }
    public static SimpleDateFormat getSimpleTimeFormat(){
        return new SimpleDateFormat("HHmmss");
    }
    public static Date parseStringToDate(String date){
        if(date == null){
            return null;
        }
        Date d = new Date();
        try {
            d = getSimpleDateFormat().parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public static boolean fechaMayorQue(String fechaProtagonista, String fecha){
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));
            return c1.after(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static int calcularDias(String fechaProtagonista, String fecha){
        try {
            SimpleDateFormat sdf =Funciones.getSimpleDateFormat();

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));

            double d = c1.getTimeInMillis() - c2.getTimeInMillis();

            long dias = Math.round(d / ( 24 * 60 * 60 * 1000));
            return ((int) dias);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static String gerErrorMessage(int code){
        String message = "UNKNOWN";
        switch (code){
            case CODES.CODE_LICENSE_INVALID:message = "Clave de producto invalida";
                break;
            case CODES.CODE_LICENSE_EXPIRED:message = "La licencia expiro";
                break;
            case CODES.CODE_LICENSE_DISABLED:message = "La licencia fue desabilitada";
                break;
            case CODES.CODE_LICENSE_DEVICES_LIMIT_REACHED:message = "Alcanzo el limite maximo de dispositivos permitidos de la licencia";
                break;
            case CODES.CODE_LICENSE_NO_LICENSE:message = "Debe realizar una carga inicial";
                break;
            case CODES.CODE_USERS_INVALID:message = "Usuario invalido ";
                break;
            case CODES.CODE_USERS_DISBLED:message = "Usuario deshabilitado";
                break;
            case CODES.CODE_DEVICES_UNREGISTERED:message = "Este dispositivo no esta registrado";
                break;
            case CODES.CODE_DEVICES_DISABLED:message = "Este dispositivo  esta deshabilitado";
                break;
            case CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER:message = "Este dispositivo  no esta asignado a este usuario";
                break;

        }
        return message;
    }

    public static void savePreferences(Context context, String key, Object value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        if(value instanceof String){
            edit.putString(key, String.valueOf(value));
        }else if(value instanceof Integer){
            edit.putInt(key, (Integer) value);
        }else if(value instanceof Long){
            edit.putLong(key, (Long) value);
        }else if(value instanceof Boolean){
            edit.putBoolean(key, (Boolean) value);
        }

        edit.commit();
    }

    public static String getCodeuserLogged(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_USERSKEY_CODE, "");
    }
    public static String getRoleUserLogged(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_USERSKEY_USERTYPE, "");
    }
    public static String getPreferences(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
    public static int getPreferencesInt(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, -1);
    }

    public static void clearPreference(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public static Dialog getCustomDialog2Btn(Context context, int color, String title, String msg, int icon, View.OnClickListener positive, View.OnClickListener negative){
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.msg_2_buttons);
        d.setCancelable(false);
        ((CardView)d.findViewById(R.id.cvCard)).setCardBackgroundColor(color);
        ((TextView)d.findViewById(R.id.tvTitle)).setText(title);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        ImageView img = ((ImageView)d.findViewById(R.id.img));
        img.setImageResource(icon);
        ImageViewCompat.setImageTintList(img, ColorStateList.valueOf(color));
        CardView btnPositive = ((CardView)d.findViewById(R.id.btnPositive));
        btnPositive.setCardBackgroundColor(color);
        btnPositive.setOnClickListener(positive);
        ((CardView)d.findViewById(R.id.btnNegative)).setOnClickListener(negative);
        try{
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return d;
    }

    public static void showAlertDependencies(Context c, String msgDependency){
        AlertDialog a = new AlertDialog.Builder(c).create();
        a.setMessage("No se puede eliminar debido a las siguientes dependencias: \n"+msgDependency);
        a.show();
    }

    public static Dialog getAlertDeleteAllDependencies(Context c, String itemName, ArrayList<KV2> tables){
        String msgDependency = "";
        for(KV2 s: tables){
            msgDependency+= s.getCode()+"\n";
        }
        String msg = "Esta seguro que desea eliminar ["+itemName+"]  permanentemente?\nTambien seran eliminadas todas las dependencias en: \n"+msgDependency;
        final Dialog d = getCustomDialog2Btn(c,c.getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        d.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        return d;
    }

    public static Dialog getLoadingDialog(Context context, String msg){
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.loading);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        return d;
    }

    public static Dialog getCustomDialog(Context context, int color, String title, String msg, int icon, View.OnClickListener listener){
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.custom_dialog_1btn);
        ((RelativeLayout)d.findViewById(R.id.rlBackground)).setBackgroundColor(color);
        ((TextView)d.findViewById(R.id.tvTitle)).setText(title);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        ((ImageView)d.findViewById(R.id.img)).setImageResource(icon);
        CardView cvOk = ((CardView)d.findViewById(R.id.cvOk));
        cvOk.setCardBackgroundColor(color);
        cvOk.setOnClickListener(listener);
        try{
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return d;
    }


}
