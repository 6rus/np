package fr.n_peloton.n_peloton;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


  //  CardView mView;
    ArrayList<GpsiesItem> gpsiesItems;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         String flusRss = "https://www.gpsies.com/geoRSS.do?username=n-peloton";

        String fluxCsv ="https://n-peloton.fr/getMapCsv.php";
       gpsiesItems = new ArrayList<>();

        new GetCSVdata().execute(fluxCsv);



        fillRecycleView();

    }


    private  void fillRecycleView(){

        recyclerView = (RecyclerView) findViewById(R.id.liste);
        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapter en grille comme une RecyclerView, avec 2 cellules par ligne
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //puis créer un MyAdapter, lui fournir notre liste de villes.
        //cet adapter servira à remplir notre recyclerview
        recyclerView.setAdapter(new MyAdapter(gpsiesItems));
    }


 /***  private void readGpsiesItem() {
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
    } * **/

private void openGPX(GpsiesItem gpsiesItem){


    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(gpsiesItem.getGPX()));
    startActivity(i);


}


private void openMapsOnGpsies(GpsiesItem gpsiesItem){
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(gpsiesItem.getLinkGpsies()))git;
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
            fillRecycleView();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewView;
        private ImageView imageView;

        //itemView est la vue correspondante à 1 cellule
        public MyViewHolder(View itemView) {
            super(itemView);

            //c'est ici que l'on fait nos findView

            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }

        //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
        public void bind(final GpsiesItem myItem) {
            textViewView.setText(myItem.getTitle());

            textViewView.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGPX(myItem);
                }

            });

            imageView.setOnClickListener(new AdapterView.OnClickListener(){
                @Override
                public void onClick(View view) {
                    openMapsOnGpsies(myItem);
                }
            });

            Picasso.get().load(myItem.getImageUrl()).centerCrop().fit().into(imageView);
        }


    }


    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<GpsiesItem> list;

        //ajouter un constructeur prenant en entrée une liste
        public MyAdapter(ArrayList<GpsiesItem> list) {
            this.list = list;
        }

        //cette fonction permet de créer les viewHolder
        //et par la même indiquer la vue à inflater (à partir des layout xml)
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.map_card,viewGroup,false);
            return new MyViewHolder(view);
        }

        //c'est ici que nous allons remplir notre cellule avec le texte/image de chaque MyObjects
        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            GpsiesItem myObject = list.get(position);
            myViewHolder.bind(myObject);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

}
