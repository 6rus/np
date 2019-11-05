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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ListView mView;
    ArrayList<GpsiesItem> gpsiesItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         String fluxRss = "https://www.gpsies.com/geoRSS.do?username=n-peloton";
       gpsiesItems = new ArrayList<>();

        mView=  findViewById(R.id.liste);

        new GetXMLdata().execute(fluxRss);

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
                Log.d("gpx ", gpsiesItems.get(position).link);
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


    private void readFluxRss(String fluxrss){


            XmlPullParserFactory parserFactory;
            try {
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream is = new URL(fluxrss).openStream();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                processParsing(parser);

            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException",e.getMessage());

            } catch (IOException e) {
                Log.e("IOException",e.getMessage());
            }


    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        int eventType = parser.getEventType();
        GpsiesItem currentItem = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if ("item".equals(eltName)) {
                        currentItem = new GpsiesItem();
                        gpsiesItems.add(currentItem);
                    } else if (currentItem != null) {
                        if ("title".equals(eltName)) {
                            currentItem.title = parser.nextText();
                        } else if ("link".equals(eltName)) {

                            currentItem.link = parser.nextText();

                        } else if ("description".equals(eltName)) {
                            currentItem.description = parser.nextText();
                        }
                    }

                    break;
            }

            eventType = parser.next();
        }

    }


    private void printGpiesItems(ArrayList<GpsiesItem> gpsiesItems) {
        StringBuilder builder = new StringBuilder();

        for (GpsiesItem gpsiesItem: gpsiesItems) {


            builder.append(gpsiesItem.title).append("\n").
                    append(gpsiesItem.link).append("\n").
                    append(gpsiesItem.description).append("\n\n");
        }

        String[] files_names = new String[gpsiesItems.size()];
        int i =0;
        for (GpsiesItem gpsiesItem: gpsiesItems) {
            files_names[i]=gpsiesItem.title;
            Log.d("Files", "FileName:" + files_names[i]);
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, files_names);
        if(mView!=null){
            mView.setAdapter(adapter);
        } else{
            Log.d("test","null");
        }


        Log.d("items",builder.toString());
    }

    private class GetXMLdata extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            readFluxRss(strings[0]);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            readGpsiesItem();
        }
    }
}
