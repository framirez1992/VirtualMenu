package com.far.virtualmenu.Utils;

public class CODES {
    //LICENCIAS
    public static final int CODE_LICENSE_INVALID = 1000;
    public static final int CODE_LICENSE_EXPIRED = 2000;
    public static final int CODE_LICENSE_DISABLED = 3000;
    public static final int CODE_LICENSE_DEVICES_LIMIT_REACHED = 4000;
    public static final int CODE_LICENSE_NO_LICENSE = 5000;
    public static final int CODE_LICENSE_VALID = 6000;

    //DEVICES
    public static final int CODE_DEVICES_ENABLED = 1100;
    public static final int CODE_DEVICES_DISABLED = 1200;
    public static final int CODE_DEVICES_UNREGISTERED = 1300;

    //USERS
    public static final int CODE_USERS_INVALID = 2100;
    public static final int CODE_USERS_DISBLED = 2200;
    public static final int CODE_USERS_ENABLED = 2300;

    public static final String USER_SYSTEM_CODE_SU = "0";
    public static final String USER_SYSTEM_CODE_ADMIN = "1";
    public static final String USER_SYSTEM_CODE_USER = "2";

    //USERS DEVICES
    public static final int CODE_DEVICES_NOT_ASSIGNED_TO_USER = 3100;

    //PREFERENCES
    //LOGIN
    public static final String PREFERENCE_USERSKEY_CODE = "USERSKEY_CODE";//CODIGO DE USUARIOI
    public static final String PREFERENCE_USERSKEY_USERTYPE = "USERSKEY_USERTYPE";//TIPO DE USUARIO
    public static final String PREFERENCE_LOGIN_BLOQUED = "LOGIN_BLOQUED";//LOGEO BLOQUEADO
    public static final String PREFERENCE_LOGIN_BLOQUED_REASON = "LOGIN_BLOQUED_REASON";//RAZON DE BLOQUEO DE LOGIN
    public static final String PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS = "LOGIN_BLOQUED_TOKEN_ATTEMPS";//NUMERO DE INTENTOS TOKEN



    /////////////////////////////////////////////
    //ACTIVITY EXTRA KEYS
    public static final String EXTRA_SECURITY_ERROR_CODE = "SECURITY_ERROR_CODE";
    public static final String EXTRA_ADMIN_LICENSE = "EXTRA_ADMIN_LICENSE";
}
