package com.rock.werool.piensunmaize.SQLiteLocal_DB;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by user on 2017.08.20.
 */

public class SQLiteQuery extends IntentService{

    // Japadod intent ar cetriem laukiem
    // Key SQLiteQuery.SRC_TYPE             Value SQLiteQuery.SRC_PRODUCT_AVG_PRICE/SRC_PRICE/SRC_NAME_PRICE_ALL
    // Key SQLiteQuery.SRC_NAME             Value String string or null
    // Key SQLiteQuery.SRC_STORE            Value String string or null
    // Key SQLiteQuery.SRC_ADDRESS          Value String string or null

    public static final String SRC_TYPE = "com.rock.werool.piensunmaize.SQLiteLocal_DB.TYPE";   // search type
    public static final int SRC_PRODUCT_AVG_PRICE = 1;                                          // expected result - average price in all stores
    public static final int SRC_PRICE = 2;                                                      // expected result - price one store different search criteria
    public static final int SRC_NAME_PRICE_ALL = 3;                                             // expected result - price for all products in single store

    public static final String SRC_NAME = "com.rock.werool.piensunmaize.SQLiteLocal_DB.NAME";                   // search parameter - product name
    public static final String SRC_STORE = "com.rock.werool.piensunmaize.SQLiteLocal_DB.STORE";                 // search parameter - store name
    public static final String SRC_ADDRESS = "com.rock.werool.piensunmaize.SQLiteLocal_DB.ADDRESS";             // search parameter - store address

    public static final String QUERY_RESULT = "queryResult";                                                    // obtained values


    private Cursor result;
    String [][] resultArray;
    private String query;
    Intent reply;

    private SQLiteHelper helper;
    private SQLiteDatabase database;

    public SQLiteQuery(){
        super(SQLiteQuery.class.getName());
    }

    public SQLiteQuery(String name) {
        super(name);
    }
    // Override IntentService methods

    @Override
    public void onCreate(){
        super.onCreate();
        helper = new SQLiteHelper(getApplicationContext());
        database = helper.getReadableDatabase();
        reply = new Intent("QUERY_RESULT");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int i = intent.getIntExtra(SRC_TYPE, 0);

        String product = intent.getStringExtra(SRC_NAME);
        String store = intent.getStringExtra(SRC_STORE);
        String address = intent.getStringExtra(SRC_ADDRESS);

        if(product == null && store == null && address == null){
            i = 0;
        }

        switch (i){
            case 1: i = 1; // pec produkta nosaukuma atrod videjo produkta cenu. 2D masivs, ind 1 , ind 2 = prodName ++ avgPrice
                if(product != null){
                    reply = searchAvgByName(product);
                    publishResults(reply);
                }
                break;

            case  2: i = 2; // pec nosaukuma, veikala un adreses atrod cenu. 2D masivs, ind1 , ind2 = prodName ++ storeName ++ storeAddress ++ price
                if(product != null && store != null && address != null){
                    reply = searchPriceInStore(product, store, address);
                    publishResults(reply);
                }
                break;

            case 3: i = 3; // pec veikala un adreses visu produktu cena. 2D masivs, ind 1 , ind 2 = prodName ++ Price
                if(store != null && address != null){
                    reply = searchAllInStore(store, address);
                    publishResults(reply);
                }

                break;

            case 4 : i = 0;
                result = null;

                break;
        }

    }

    private void publishResults(Intent intent){
        intent.setAction("QUERY_RESULT");
        sendBroadcast(intent);
    }

    private Intent searchAvgByName(String productName){
        query = "SELECT " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_NAME + ", " + StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_PRICE +
                " FROM " + ProductContract.TABLE_NAME +
                " INNER JOIN " + StoreProductPriceContract.TABLE_NAME +
                " ON " + StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_PRODUCT_ID + " = " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_ID +
                " WHERE " + ProductContract.TABLE_NAME + "." +ProductContract.COLUMN_PRODUCT_NAME + " LIKE '%" + productName + "%' ";
//                "GROUP BY " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_NAME;

        result = database.rawQuery(query, null);
        boolean bl;
        if(result == null){
            bl = true;
        }else{
            bl = false;
        }

        resultArray = cursorToArr(result);
        //String [][] resultArray = {{"a", "b"}, {"c", "d"}};
        Bundle bundle = new Bundle();
        bundle.putSerializable("String[][]", resultArray);
        reply.putExtra(SQLiteQuery.QUERY_RESULT, bundle);
        //reply.putExtra(SQLiteQuery.QUERY_RESULT, resultArray);
        return reply;
    }

    private Intent searchPriceInStore(String productName, String storeName, String storeAddress){
        query = "SELECT " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_NAME + ", " +
                StoreContract.TABLE_NAME + "." + StoreContract.COLUMN_STORE_NAME + ", " +
                StoreContract.TABLE_NAME + "." + StoreContract.COLUMN_STORE_ADDRESS + ", " +
                StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_PRICE +
                " FROM " + ProductContract.TABLE_NAME +
                " INNER JOIN " + StoreProductPriceContract.TABLE_NAME +
                " ON " + StoreProductPriceContract.COLUMN_PRODUCT_ID + " = " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_ID +
                " INNER JOIN " + StoreContract.TABLE_NAME +
                " ON " + StoreContract.TABLE_NAME + "." + StoreContract.COLUMN_STORE_ID + " = " + StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_STORE_ID +
                " WHERE " + ProductContract.COLUMN_PRODUCT_NAME + " LIKE '%" + productName + "%'" +
                " AND " + StoreContract.COLUMN_STORE_NAME + " LIKE '%" + storeName + "%'" +
                " AND " + StoreContract.COLUMN_STORE_ADDRESS + " LIKE '%" + storeAddress + "%'";
        result = database.rawQuery(query, null);
        resultArray = cursorToArr(result);
        //reply.putExtra(SQLiteQuery.QUERY_RESULT, resultArray);
        //String [][] resultArray2 = {{"a", "b", "c", "d"}, {"e", "f", "g", "h"}};
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("String[][]", resultArray);
        reply.putExtra(SQLiteQuery.QUERY_RESULT, bundle2);
        return reply;
    }

    private Intent searchAllInStore(String storeName, String storeAddress){
       query = "SELECT " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_NAME + ", " + StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_PRICE +
               " FROM " + ProductContract.TABLE_NAME +
               " INNER JOIN " + StoreProductPriceContract.TABLE_NAME +
               " ON " + StoreProductPriceContract.COLUMN_PRODUCT_ID + " = " + ProductContract.TABLE_NAME + "." + ProductContract.COLUMN_PRODUCT_ID +
               " INNER JOIN " + StoreContract.TABLE_NAME +
               " ON " + StoreContract.TABLE_NAME + "." + StoreContract.COLUMN_STORE_ID + " = " + StoreProductPriceContract.TABLE_NAME + "." + StoreProductPriceContract.COLUMN_STORE_ID +
               " WHERE " + StoreContract.COLUMN_STORE_NAME + " LIKE '%" + storeName + "%'" +
               " AND " + StoreContract.COLUMN_STORE_ADDRESS + " LIKE '%" + storeAddress + "%'";
        result = database.rawQuery(query, null);
        resultArray = cursorToArr(result);
        //reply.putExtra(SQLiteQuery.QUERY_RESULT, resultArray);

        //String [][] resultArray3 = {{"a", "b"}, {"c", "d"}};
        Bundle bundle3 = new Bundle();
        bundle3.putSerializable("String[][]", resultArray);
        reply.putExtra(SQLiteQuery.QUERY_RESULT, bundle3);
        return reply;
    }

    private String [][] cursorToArr(Cursor cursor){
        String [][] arr;
        String [] columns;
        String string;
        int i = 0;
        int j = 0;

        if(cursor.moveToFirst()){
            arr = new String[cursor.getCount()][cursor.getColumnCount()];

            int a = cursor.getCount();
            int b = cursor.getColumnCount();

            columns = cursor.getColumnNames();
            String test = cursor.getString(cursor.getColumnIndexOrThrow(columns[0]));
            //test = cursor.getString(cursor.getColumnIndexOrThrow(columns[1]));
            if (cursor.getString(cursor.getColumnIndexOrThrow(columns[0])) == null)
                return null;
            //"Aggregate SQL functions such as SUM() always return a result row. The result itself can be null."
            for(i = 0; i < cursor.getCount(); i++){
                for(j = 0; j < cursor.getColumnCount(); j++){
                    string = cursor.getString(cursor.getColumnIndexOrThrow(columns[j]));
                    arr[i][j] = string;
                }
                cursor.moveToNext();
                j = 0;
            }
            cursor.close();
        }else{
            arr = null;
        }
        return arr;
    }


}