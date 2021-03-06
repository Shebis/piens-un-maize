package com.rock.werool.piensunmaize.favourites;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.rock.werool.piensunmaize.R;
import com.rock.werool.piensunmaize.remoteDatabase.IDatabaseResponseHandler;
import com.rock.werool.piensunmaize.remoteDatabase.Product;
import com.rock.werool.piensunmaize.remoteDatabase.RemoteDatabase;
import com.rock.werool.piensunmaize.search.by_product.SelectStoreActivity;

import java.util.ArrayList;

public class FavouriteProductsActivity extends AppCompatActivity {
    MyCustomAdapter dataAdapter;
    String [][] array;
    RemoteDatabase remoteDB;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //SHOW ICON
        getSupportActionBar().setLogo(R.mipmap.applogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.applogo);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // /SHOW ICON
        Spannable word = new SpannableString("bread n milk");
        word.setSpan(new ForegroundColorSpan(Color.rgb(177, 227, 251)), 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(word);


        remoteDB = new RemoteDatabase("http://zesloka.tk/piens_un_maize_db/", this);


        remoteDB.GetFavoriteProducts(new IDatabaseResponseHandler<Product>() {
            @Override
            public void onArrive(ArrayList<Product> data) {
                array = new String[data.size()][3];
                ArrayList<String> al = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    array[i][0] = data.get(i).getName();
                    array[i][1] = Double.toString(data.get(i).getAvaragePricePrice());
                    array[i][2] = Long.toString(data.get(i).getId());
                    al.add("q");
                }
                displayListView(al);
            }
            @Override
            public void onError(VolleyError error) {

            }
        });

    }
    private void displayListView(ArrayList<String> inputList) {
        dataAdapter = new MyCustomAdapter(this, R.layout.itemname_price_remove, inputList);
        ListView listView = (ListView)findViewById(R.id.listviewfavouriteproduct);
        listView.setAdapter(dataAdapter);
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> productList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> productList) {
            super(context, textViewResourceId, productList);
            this.productList = productList;
        }

        private class ViewHolder {
            TextView name;
            TextView averagePrice;
            ImageView remove;
            long productId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.itemname_price_remove, null);

                holder = new ViewHolder();                              //makes holder object with the values of the fields
                holder.name = (TextView) convertView.findViewById(R.id.productName);
                holder.averagePrice = (TextView) convertView.findViewById(R.id.productPrice);
                holder.remove = (ImageView) convertView.findViewById(R.id.delete_btn);

                holder.name.setText(array[position][0]);
                holder.averagePrice.setText(array[position][1]);
                holder.productId = Long.parseLong(array[position][2]);

                final String clickedProductName = holder.name.getText().toString();
                final String clickedProductAveragePrice = holder.averagePrice.getText().toString();
                final long clickedProductId = holder.productId;
                final int positionOfElement = position;

                convertView.setTag(holder);                             //Important! Stores the holder in the View (row)

                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SelectStoreActivity.class);  //TODO For some reason Martins has SelectStoreActivity.class here
                        intent.putExtra("clickedProductName", clickedProductName);      //Passes parameters to the activity
                        intent.putExtra("clickedProductAveragePrice", clickedProductAveragePrice);    //.putExtra(variableName, variableValue)
                        intent.putExtra("clickedProductId", clickedProductId);
                        startActivity(intent);
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SelectStoreActivity.class);  //TODO For some reason Martins has SelectStoreActivity.class here
                        intent.putExtra("clickedProductName", clickedProductName);      //Passes parameters to the activity
                        intent.putExtra("clickedProductAveragePrice", clickedProductAveragePrice);    //.putExtra(variableName, variableValue)
                        intent.putExtra("clickedProductId", clickedProductId);
                        startActivity(intent);
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    double clickedProductAveragePriceD = Double.parseDouble(clickedProductAveragePrice);
                    Product tempProduct = new Product(clickedProductId, clickedProductName, "dummy", "dummy", clickedProductAveragePriceD);
                    @Override
                    public void onClick(View view) {        //TODO implement actions on click

                        remoteDB.DeleteFavoriteProduct(tempProduct, new IDatabaseResponseHandler<String>() {
                            @Override
                            public void onArrive(ArrayList<String> data) {

                            }

                            @Override
                            public void onError(VolleyError error) {

                            }
                        });

                        productList.remove(positionOfElement);
                        String[][] array1 = new String[array.length][4];
                        int n = 0;
                        for (int i = 0; i < array.length; i++) {
                            if (i != positionOfElement) {
                                array1[n][0] = array[i][0];
                                array1[n][1] = array[i][1];
                                array1[n][2] = array[i][2];
                                n++;
                            }
                        }
                        array = array1;
                        notifyDataSetChanged();

                    }
                });

            }


            else {
                holder = (ViewHolder) convertView.getTag();                         //If row is already created then get the holder from it
            }

            holder.name.setText(array[position][0]);

            return convertView;
        }
    }

}

