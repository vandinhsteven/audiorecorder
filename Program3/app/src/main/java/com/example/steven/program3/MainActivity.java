package com.example.steven.program3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    protected void onResume(){
        super.onResume();
        ListView list = findViewById(R.id.data_list_view);
        TextView text = findViewById(R.id.text);
        text.setVisibility(View.INVISIBLE);
        try {
            // Reading a file that already exists
            File f = new File(getFilesDir(), "file.ser");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String length = br.readLine();
            System.out.println(length);
            if (length != null) {
                int num = Integer.valueOf(length);
                String[] listItems = new String[num-1];

                for (int i = 1; i < num; i++) {
                    listItems[i-1] = "Audio Recording " + String.valueOf(i);
                }

                // Show the list view with the each list item an element from listItems
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
                list.setAdapter(adapter);

                // Set an OnItemClickListener for each of the list items
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            String AudioSavePathInDevice =
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + (position+1) + ".3gp";
                            System.out.println(AudioSavePathInDevice);
                            mediaPlayer.setDataSource(AudioSavePathInDevice);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }

                });
            }

            if (length == null) {
                list.setEnabled(false);
                list.setVisibility(View.INVISIBLE);
                text.setVisibility(View.VISIBLE);
            }
        }

        catch(IOException e){
            // There's no JSON file that exists, so don't
            // show the list. But also don't worry about creating
            // the file just yet, that takes place in AddText.

            //Here, disable the list view
            list.setEnabled(false);
            list.setVisibility(View.INVISIBLE);

            //show the text view
            text.setVisibility(View.VISIBLE);
        }
    }


    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                /*the R.id.action_favorite is the ID of our button (defined in strings.xml).
                Change Activity here (if that's what you're intending to do, which is probably is).
                 */
                Intent i = new Intent(this, AddText.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}