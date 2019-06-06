package at.ac.univie.hci.rhymr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SavedSearch extends AppCompatActivity { //displaying saved searches by user

    ArrayList<String> dopuna = new ArrayList<>();
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search);
        Intent intent = getIntent();

        ArrayList<String> arrayList;
        arrayList=intent.getStringArrayListExtra("saved_searches");
        dopuna.addAll(arrayList);
        fill_List(dopuna);

        text = findViewById(R.id.textView3);

        if(dopuna.size()==0)
            text.setText("You have no saved searches! HINT: Try saving some by clicking the 'Disk' button on the results page.");
    }


    private void fill_List(ArrayList<String> za_listu) {

        ListView saved_search_list = findViewById(R.id.saved_search_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, za_listu);
        saved_search_list.setAdapter(arrayAdapter);

        //clickable list -> Starting search for user-chosen favorite
        saved_search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String click = (String) parent.getItemAtPosition(position);
                click = click.replaceAll("\\s", "");
                Intent myNewIntent = new Intent(SavedSearch.this, Results.class);
                myNewIntent.putExtra("click", click);
                SavedSearch.this.startActivity(myNewIntent);
            }
        });

    }
}
