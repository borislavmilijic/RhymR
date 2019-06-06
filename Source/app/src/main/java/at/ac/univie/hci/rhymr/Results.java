package at.ac.univie.hci.rhymr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Results extends Activity {

    String api_url = "https://api.datamuse.com/words?";
    String rhyme = "rel_rhy=";
    String syn = "rel_syn=";
    String app_rhy = "rel_nry=";
    String max_results = "&max=30";
    String user_query;
    String request_rhy;
    String request_syn;
    String request_hom;
    ArrayList<String> savedSearch = new ArrayList<>();

    ImageButton saveButton, saved_search_button;
    ListView list;
    TextView text_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        text_query = findViewById(R.id.query_word);

        saved_search_button = findViewById(R.id.saved_search_button);
        saved_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Opening saved searches.", Toast.LENGTH_LONG).show();
                Intent myNewIntent = new Intent(Results.this, SavedSearch.class);
                savedSearch = ValuePreserver.getInstance().saved_list;
                myNewIntent.putStringArrayListExtra("saved_searches", savedSearch);
                Results.this.startActivity(myNewIntent);
            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Using Singleton class to save the user-input in the current app-session
                if(!ValuePreserver.getInstance().saved_list.contains(user_query)) {
                    ValuePreserver.getInstance().saved_list.add(user_query);
                    Toast.makeText(getApplicationContext(), "Search saved", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "You already saved that term!", Toast.LENGTH_LONG).show();

            }
        });


        handleIntent(getIntent());
    }

    @Override //Using handleIntent because user could/will make multiple searches
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) { //for searches from the Homescreen
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.replaceAll("\\s", "");
            text_query.setText(query);
            user_query = query;
            request_rhy = api_url+rhyme+query+max_results;
            request_syn = api_url+syn+query+max_results;
            request_hom = api_url+ app_rhy +query+max_results;

            new DatamuseQuery(request_rhy, "rhyme").execute();
            new DatamuseQuery(request_syn, "synonym").execute();
            new DatamuseQuery(request_hom, "approx_rhy").execute();
        } else { //for searches from previously saved terms
            String click = intent.getStringExtra("click");
            text_query.setText(click);
            request_rhy = api_url+rhyme+click+max_results;
            request_syn = api_url+syn+click+max_results;
            request_hom = api_url+ app_rhy +click+max_results;

            new DatamuseQuery(request_rhy, "rhyme").execute();
            new DatamuseQuery(request_syn, "synonym").execute();
            new DatamuseQuery(request_hom, "approx_rhy").execute();
        }
    }

    private void fill_List(ArrayList<String> results, String list_name) {

        if (list_name.equals("rhyme"))
            list = findViewById(R.id.list);

        if (list_name.equals("synonym"))
            list = findViewById(R.id.list_syn);

        if (list_name.equals("approx_rhy"))
            list = findViewById(R.id.list_hom);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        list.setAdapter(arrayAdapter);
    }

    @SuppressLint("StaticFieldLeak")
    private class DatamuseQuery extends AsyncTask<Void,Void,Void> {

        private String api_url;
        private ArrayList<String> words;
        private String json_rez;
        String list_name;

        DatamuseQuery(String url, String list_name) {
            this.list_name = list_name;
            this.api_url = url;
            this.words = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                URL url = new URL(api_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Getting JSON from API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder resultBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null)
                    resultBuilder.append(line);

                reader.close();

                json_rez = resultBuilder.toString();

                //JSON parsing
                try {
                    JSONArray jArray = new JSONArray(json_rez);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        words.add(jObject.getString("word"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            //calling a Activity method to fill the tables with results
            fill_List(words, list_name);
        }
    }
}