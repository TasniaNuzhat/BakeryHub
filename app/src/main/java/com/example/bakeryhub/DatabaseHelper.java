package com.example.bakeryhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database and Table Information
    public static final String DATABASE_NAME = "BakeryHub_DB";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_REGISTER = "register";
    public static final String TABLE_ORDER = "orders";

    public static final String COL_ID = "userId";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_MOBILE = "mobile";

    // Products Table
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "_id";
    public static final String COL_PRODUCT_NAME = "productName";
    public static final String COL_PRODUCT_PRICE = "productPrice";
    public static final String COL_PRODUCT_IMAGE = "productImage";

    // Cart Table
    public static final String TABLE_CART = "cart";
    public static final String COL_CART_ID = "id";
    public static final String COL_CART_PRODUCT_ID = "product_id";
    public static final String COL_CART_QUANTITY = "quantity";
    public static final String  COL_USER_ID = "cartUserId";

    public static final String COL_ORDER_ID = "orderId";
    public static final String COL_ORDER_STATUS = "orderStatus";
    public static final String COL_ORDER_PRODUCT_PRICE = "orderTotalPrice";
    public static final String COL_ORDER_PRODUCT_ID = "orderProductId";
    public static final String COL_ORDER_PRODUCT_NAME = "orderProductName";
    public static final String COL_TOTAL_ORDER_PRICE = "totalOrderPrice";
    public static final String COL_ORDER_USER_ID = "orderUserId";
    public static final String COL_ORDER_PRODUCT_QUANTITY = "orderProductQuantity";
    public static final String COL_TRANSACTION_ID = "TransactionId";





    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {





            db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (" +
                    COL_ID + " TEXT PRIMARY KEY, " +
                    COL_USERNAME + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_MOBILE + " TEXT)"
            );
        db.execSQL( "CREATE TABLE " + TABLE_ORDER + " (" +
                COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ORDER_STATUS + " TEXT, " +
                COL_ORDER_PRODUCT_PRICE + " REAL NOT NULL, " +
                COL_ORDER_PRODUCT_ID + " INTEGER, " +
                COL_ORDER_PRODUCT_NAME + " TEXT, " +
                COL_TOTAL_ORDER_PRICE + " REAL, " +
                COL_ORDER_USER_ID + " TEXT, " +
                COL_ORDER_PRODUCT_QUANTITY + " INTEGER, " +
                COL_TRANSACTION_ID + " INTEGER)"  ) ;


        // Create Products Table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT NOT NULL, " +  // Added NOT NULL constraint
                COL_PRODUCT_PRICE + " REAL NOT NULL, " + // Added NOT NULL constraint
                COL_PRODUCT_IMAGE + " BLOB)";
        db.execSQL(createProductsTable);

        // Create Cart Table
        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CART_PRODUCT_ID + " INTEGER, " +
                COL_USER_ID + " TEXT, " +
                COL_CART_QUANTITY + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_CART_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COL_PRODUCT_ID + ") ON DELETE CASCADE)";
        db.execSQL(createCartTable);

        // Enable foreign keys for cascade functionality
        //db.execSQL("PRAGMA foreign_keys = ON;");
    }

    public boolean insertUser(String userId,String username, String email, String password, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, userId);
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_MOBILE, mobile);
        long result = db.insert(TABLE_REGISTER, null, contentValues);
        return result != -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +TABLE_REGISTER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);

            onCreate(db);
        }
    }

    // Insert Product into Products Table
    public boolean insertProduct(String name, double price, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_IMAGE, image);

        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1; // Return true if insertion is successful
    }

    // Get All Products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products", null);
    }


    // Get Product by Name
    public Cursor getProductByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});
    }

    public int updateProduct(int id, String name, double price, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_IMAGE, image);

        return db.update(TABLE_PRODUCTS, values, COL_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
    }




    // Delete Product by Name
    public void deleteProduct(String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_PRODUCT_NAME + " = ?", new String[]{productName});
        db.close();
    }

    // Add Product to Cart
    public boolean addToCart(int productId, int quantity, String cartUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + COL_CART_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
            if (cursor.moveToFirst()) {

                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_QUANTITY));
                ContentValues values = new ContentValues();
                values.put(COL_CART_QUANTITY, currentQuantity + quantity);
                db.update(TABLE_CART, values, COL_CART_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
            } else {

                ContentValues values = new ContentValues();
                values.put(COL_CART_PRODUCT_ID, productId);
                values.put(COL_CART_QUANTITY, quantity);
                values.put(COL_USER_ID, cartUserId);

                db.insert(TABLE_CART, null, values);
            }
            cursor.close();
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    // Get All Cart Items
    public Cursor getCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c." + COL_CART_ID + ", p." + COL_PRODUCT_NAME + " AS name, p." + COL_PRODUCT_PRICE + " AS price, p." + COL_PRODUCT_IMAGE + " AS image, c." + COL_CART_QUANTITY + " AS quantity " +
                "FROM " + TABLE_CART + " c " +
                "INNER JOIN " + TABLE_PRODUCTS + " p ON c." + COL_CART_PRODUCT_ID + " = p." + COL_PRODUCT_ID;
        return db.rawQuery(query, null);
    }



    // Update Cart Item Quantity
    public void updateCartItemQuantity(int itemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CART_QUANTITY, quantity);
        db.update(TABLE_CART, values, COL_CART_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    // Delete Cart Item
    public void deleteCartItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COL_CART_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", null, null); // Deletes all rows in the 'cart' table
        db.close();
    }



    // Calculate Cart Total Price
    public double getCartTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(p." + COL_PRODUCT_PRICE + " * c." + COL_CART_QUANTITY + ") AS total " +
                "FROM " + TABLE_CART + " c " +
                "INNER JOIN " + TABLE_PRODUCTS + " p " +
                "ON c." + COL_CART_PRODUCT_ID + " = p." + COL_PRODUCT_ID;

        Cursor cursor = db.rawQuery(query, null);
        double total = 0.0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }



    public boolean insertOrder(String userId, int transactionId, int productId, String productName, double price, int quantity,String status) {
        SQLiteDatabase db = null;
        boolean isInserted = false;

        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COL_TRANSACTION_ID, transactionId);
            values.put(COL_ORDER_USER_ID, userId);
            values.put(COL_ORDER_PRODUCT_ID, productId);
            values.put(COL_ORDER_PRODUCT_NAME, productName);
            values.put(COL_ORDER_PRODUCT_PRICE, price);
            values.put(COL_ORDER_PRODUCT_QUANTITY, quantity);
            values.put(COL_ORDER_STATUS, "Order Confirmed");
            values.put(COL_TOTAL_ORDER_PRICE, price * quantity);

            long result = db.insert(TABLE_ORDER, null, values);
            if (result != -1) {
                isInserted = true;
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }

        return isInserted;
    }
}
