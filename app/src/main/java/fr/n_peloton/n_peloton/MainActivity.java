package fr.n_peloton.n_peloton;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ListView mView;
    ArrayList<GpsiesItem> gpsiesItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         String flusRss = "https://www.gpsies.com/geoRSS.do?username=n-peloton";

        String fluxCsv ="https://n-peloton.fr/getMapCsv.php";
       gpsiesItems = new ArrayList<>();

        mView=  findViewById(R.id.liste);

        new GetCSVdata().execute(fluxCsv);

        readGpsiesItem();

    }
    private void readGpsiesItem() {
        Log.d("readGpsiesItem","in");

        String[] files_names = new String[gpsiesItems.size()];
        Log.d("gpsiesItems size",""+gpsiesItems.size());
        int i =0;
        for (GpsiesItem gpsiesItem: gpsiesItems) {
            files_names[i]=gpsiesItem.getTitle();
            Log.d("Files", "FileName:" + files_names[i]);
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, files_names);
        if(mView!=null){
            mView.setAdapter(adapter);
        } else{
            Log.d("mview","null");
        }

        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppCompatTextView apt = (AppCompatTextView) view;
               // selectedFilePath = apt.getText().toString();

                Log.d("title ", gpsiesItems.get(position).getTitle());
                Log.d("gpx ", gpsiesItems.get(position).getGPX());
                openGPX(gpsiesItems.get(position));
                }
        });
    }

private void openGPX(GpsiesItem gpsiesItem){


    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(gpsiesItem.getGPX()));
    startActivity(i);


}

private void forceGpxOsmand(GpsiesItem gpsiesItem){
    /** https://www.javatips.net/api/Osmand-master/OsmAnd/src/net/osmand/plus/helpers/ExternalApiHelper.java */
    Uri  uri = Uri.parse("osmand.api://navigate_gpx?force=true");
    Intent i = new Intent(Intent.ACTION_VIEW, uri);
    i.putExtra("data",gpsiesItem.data);
    Log.d("data",gpsiesItem.data);
    startActivity(i);
}



  public static String readGPXAsString(String linkGPX) {
        String result = "";

        InputStream is = null;
            try {
                is = new URL(linkGPX).openStream();
                char current;
                while (is.available() > 0) {
                    current = (char) is.read();
                    result = result + String.valueOf(current);
                }
            } catch (Exception e) {
                Log.d("TourGuide", e.toString());
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.d("IOException", e.getMessage());

                    }
            }
        return result;

    }




    private void readFluxCSV(String fluxCsv){


          GpsiesItem currentItem = null;
            try{
                InputStream is = new URL(fluxCsv).openStream();
                InputStreamReader isr = new InputStreamReader(is);

                CSVReader reader = new CSVReader(isr);
                String [] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    String[] parts = nextLine[0].split(";");
                    if(parts.length>0){
                        currentItem = new GpsiesItem(parts[0],parts[1]);

                        gpsiesItems.add(currentItem);

                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                Log.e(e.getClass().toString(), e.getMessage());
            }


    }






    private class GetCSVdata extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            readFluxCSV(strings[0]);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            readGpsiesItem();
        }
    }
}
