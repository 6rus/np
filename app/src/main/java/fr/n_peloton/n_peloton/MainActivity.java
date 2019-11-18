package fr.n_peloton.n_peloton;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.support.v4.content.FileProvider.getUriForFile;


public class MainActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    //  CardView mView;
    ArrayList<GpsiesItem> gpsiesItems;
    private RecyclerView recyclerView;
    SwipeRefreshLayout sw_refresh;
    WebView webView;
    Long downloadID;
    private DownloadManager mgr =null;

    String FLUX_RSS = "https://www.gpsies.com/geoRSS.do?username=n-peloton";
    String FLUX_CSV ="https://n-peloton.fr/getMapCsv.php";

    @Override
    public void onBackPressed() {
        if(webView!= null){
            webView.destroy();
             setContentView(R.layout.activity_main);
            super.recreate();
        }

        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsiesItems = new ArrayList<>();
        sw_refresh = findViewById(R.id.sw_refresh);



        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        new GetCSVdata().execute(FLUX_CSV);
        fillRecycleView();

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                gpsiesItems.clear();
                new GetCSVdata().execute(FLUX_CSV);
                fillRecycleView();
                sw_refresh.setRefreshing(false);
            }
        });
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



    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
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

    private void openVueAllTrails(GpsiesItem gpsiesItem){
    //https://stackoverflow.com/questions/7858703/slide-in-animation-on-webview-data

         setContentView( R.layout.webview );
        webView = findViewById(R.id.vueWeb);
        mProgressBar = findViewById(R.id.progressBar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebviewClient());
        webView.loadUrl(gpsiesItem.getVueAllTrails());
    }

    private void openMapsOnGpsies(GpsiesItem gpsiesItem){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(gpsiesItem.getLinkGpsies()));
        startActivity(i);

    }


    private void openGPXOsmand(GpsiesItem gpsiesItem){


        haveStoragePermission();

        String file_name =  gpsiesItem.getFileName();
        Uri uri = Uri.parse(gpsiesItem.getGPX());

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(file_name);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name);
        downloadID = mgr.enqueue(request);
    }


    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadID);
            Cursor c = mgr.query(query);
            if(c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if(DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (uriString.substring(0, 7).matches("file://")) {
                        uriString =  uriString.substring(7);
                    }
                    File file = new File(uriString);
                    Uri uriFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    Intent intentGPX = new Intent(Intent.ACTION_VIEW);
                    intentGPX.setDataAndType(uriFile, "application/gpx+xml");
                    intentGPX.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    intentGPX.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intentGPX);
                }
            }
        }
        }
    };



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




    private void readFluxCSV(String FLUX_CSV){


          GpsiesItem currentItem = null;
            try{
                InputStream is = new URL(FLUX_CSV).openStream();
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
        private Button buttonDl;
        private Button buttonOpen;
        private Button buttonOpenOsm;

        //itemView est la vue correspondante à 1 cellule
        public MyViewHolder(View itemView) {
            super(itemView);

            //c'est ici que l'on fait nos findView

            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            buttonDl = (Button) itemView.findViewById(R.id.dl);
            buttonOpen = (Button) itemView.findViewById(R.id.open);
            buttonOpenOsm = (Button) itemView.findViewById(R.id.openosm);
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
                    openVueAllTrails(myItem);
                }
            });

            buttonDl.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGPX(myItem);
                }

            });

            buttonOpen.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMapsOnGpsies(myItem);
                }

            });
            buttonOpenOsm.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGPXOsmand(myItem);

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

    private class MyWebviewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mProgressBar.setVisibility(View.VISIBLE);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
        }

    }


}
