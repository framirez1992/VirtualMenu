package com.far.virtualmenu.Utils;

import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Funciones {

    public static String getPhoneID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
}
