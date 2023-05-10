package com.test.apex.network;

public class ServerAPI {

    public static final String ROOT_URL = "https://apex-admin.000webhostapp.com/";

    public static final String URL_PRODUCT_IMAGE = ROOT_URL + "images/products/";
    public static final String URL_USER_IMAGE = ROOT_URL + "images/users/";

    private static final String ROOT_URL_USER = ROOT_URL + "apiUsers.php?apicall=";
    private static final String ROOT_URL_PRODUCT = ROOT_URL + "apiProducts.php?apicall=";
    private static final String ROOT_URL_TRANSACTION = ROOT_URL + "apiTransactions.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL_USER + "signup";
    public static final String URL_LOGIN_USERNAME = ROOT_URL_USER + "loginusername";
    public static final String URL_LOGIN_EMAIL = ROOT_URL_USER + "loginemail";
    public static final String URL_UPDDATE_USER = ROOT_URL_USER + "updateUser";
    public static final String URL_READ_UPDATED_USER = ROOT_URL_USER + "readUpdatedUser";

    public static final String URL_CREATE_PRODUCT = ROOT_URL_PRODUCT + "createProduct";
    public static final String URL_READ_PRODUCT = ROOT_URL_PRODUCT + "readProduct";
    public static final String URL_UPDATE_PRODUCT = ROOT_URL_PRODUCT + "updateProduct";
    public static final String URL_DELETE_PRODUCT = ROOT_URL_PRODUCT + "deleteProduct";

    public static final String URL_CREATE_TRANSACTION = ROOT_URL_TRANSACTION + "createTransaction";
    public static final String URL_READ_TRANSACTION = ROOT_URL_TRANSACTION + "readTransaction";
    public static final String URL_UPDATE_TRANSACTION = ROOT_URL_TRANSACTION + "updateTransaction";
    public static final String URL_UPDATE_NEW_TRANSACTION = ROOT_URL_TRANSACTION + "updateNewTransaction";

    public static final String RAJAONGKIR_API = "5bac11a3c50d57a2f9daeef8fc14b171";
}
