package com.example.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    TextView confirmedTextView, activeTextView, recoveredTextView, deceasedTextView, timeTextView;
    ListView listView;

    public class StateData
    {
        private String state, confirmed, active, recovered, deceased;

        public StateData(String state, String confirmed, String active, String recovered, String deceased) {
            this.state = state;
            this.confirmed = confirmed;
            this.active = active;
            this.recovered = recovered;
            this.deceased = deceased;
        }
    }

    ArrayList<StateData> stateData = new ArrayList<>();


    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return stateData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            view = getLayoutInflater().inflate(R.layout.customlayout,null);

            TextView stateTextView, cnfmdTextView, actvTextView, rcvrdTextView, dcsdTextView;

            stateTextView = view.findViewById(R.id.stateTextView);
            cnfmdTextView = view.findViewById(R.id.cnfmdTextView);
            actvTextView = view.findViewById(R.id.actvTextView);
            rcvrdTextView = view.findViewById(R.id.rcvrdTextView);
            dcsdTextView = view.findViewById(R.id.dcsdTextView);

            stateTextView.setText(stateData.get(i).state);
            cnfmdTextView.setText(stateData.get(i).confirmed);
            actvTextView.setText(stateData.get(i).active);
            rcvrdTextView.setText(stateData.get(i).recovered);
            dcsdTextView.setText(stateData.get(i).deceased);


            return view;
        }
    }



    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                String result = "";
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Can't load data....Please try again :(", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String stateInfo = jsonObject.getString("statewise");

                JSONArray arr = new JSONArray(stateInfo);

                String message="";

                for (int i=0;i<arr.length();i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    if (i == 0)
                    {
                        String confirmed = jsonPart.getString("confirmed");
                        String active = jsonPart.getString("active");
                        String recovered = jsonPart.getString("recovered");
                        String deceased = jsonPart.getString("deaths");
                        String deltaconfirmed = jsonPart.getString("deltaconfirmed");
                        String deltarecovered = jsonPart.getString("deltarecovered");
                        String deltadeceased = jsonPart.getString("deltadeaths");
                        String time = jsonPart.getString("lastupdatedtime");

                        confirmed = deltaconfirmed.equals("0")? confirmed : "↑ " + deltaconfirmed + "\n" + confirmed;
                        recovered = deltarecovered.equals("0")? recovered : "↑ " + deltarecovered + "\n" + recovered;
                        deceased = deltadeceased.equals("0")? deceased : "↑ " + deltadeceased + "\n" + deceased;

                        confirmedTextView.setText("CONFIRMED\n" + confirmed);
                        activeTextView.setText("ACTIVE\n" + active);
                        recoveredTextView.setText("RECOVERED\n" + recovered);
                        deceasedTextView.setText("DECEASED\n" + deceased);
                        timeTextView.setText(time);

                    } else
                    {
                        String state = jsonPart.getString("state");
                        String confirmed = jsonPart.getString("confirmed");
                        String active = jsonPart.getString("active");
                        String recovered = jsonPart.getString("recovered");
                        String deceased = jsonPart.getString("deaths");
                        String deltaconfirmed = jsonPart.getString("deltaconfirmed");
                        String deltarecovered = jsonPart.getString("deltarecovered");
                        String deltadeceased = jsonPart.getString("deltadeaths");

                        confirmed = deltaconfirmed.equals("0")? confirmed : "↑ " + deltaconfirmed + "\n" + confirmed;
                        recovered = deltarecovered.equals("0")? recovered : "↑ " + deltarecovered + "\n" + recovered;
                        deceased = deltadeceased.equals("0")? deceased : "↑ " + deltadeceased + "\n" + deceased;

                        stateData.add(new StateData(state, confirmed, active, recovered, deceased));
                    }
                }

                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Can't load data...Please try again :(", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        confirmedTextView = findViewById(R.id.confirmedTextView);
        activeTextView = findViewById(R.id.activeTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);
        deceasedTextView = findViewById(R.id.deceasedTextView);
        timeTextView = findViewById(R.id.timeTextView);
        listView = findViewById(R.id.listView);


        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://api.covid19india.org/data.json");

        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Can't load data...Please try again :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
