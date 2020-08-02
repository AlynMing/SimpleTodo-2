package com.example.simpletodo;
import android.content.Context;
import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.os.FileUriExposedException;
//import android.os.FileUtils;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    ArrayList<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdaptor itemsAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);//each of the views as a reference
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdaptor.onLongClickListener onLongClickListener = new ItemsAdaptor.onLongClickListener(){//overwrite the method
            @Override
            public void onItemLongClicked(int position) {
                //delete the item from the model
                items.remove(position);
                //notify the adaptor
                itemsAdaptor.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed~" , Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdaptor.onClickListener onClickListener = new ItemsAdaptor.onClickListener() {
            @Override
            public void onItemClicked(int position) {
                String tag = "MainActivity";
                String msg = "Single click at position";
                Log.d(tag, msg+position);
                //create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited to the new activity
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the edit activity(update that in main activity)
                startActivityForResult(i, EDIT_TEXT_CODE);//results we expect back from edit activity
                //need request code to distinguish different intents/activities
            }
        };
        itemsAdaptor = new ItemsAdaptor(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdaptor);
        Context context = this;
        rvItems.setLayoutManager(new LinearLayoutManager(context));//vertical

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                //add item to the model
                items.add(todoItem);
                //Modify adapter that an item is inserted
                int position = items.size()-1;
                itemsAdaptor.notifyItemInserted(position);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added~" , Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }
    //handle the result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //Extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model at the right position with new item text
            items.set(position, itemText);
            //notify the adaptor
            itemsAdaptor.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!" , Toast.LENGTH_SHORT).show();
        }else{
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        String child = "data.txt";
        return new File(getFilesDir(), child);
    }
    //load items yb reading every line of data file
    private void loadItems(){
        try{
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch(IOException e){
            String tag = "MainActivity";
            String msg = "Error reading items";
            Log.e(tag, msg, e);
            items = new ArrayList<>();
        }
    }
    //save items by writing them into data file
    private void saveItems(){
        try{
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e){
            String tag = "MainActivity";
            String msg = "Error reading items";
            Log.e(tag, msg, e);
        }
    }
}