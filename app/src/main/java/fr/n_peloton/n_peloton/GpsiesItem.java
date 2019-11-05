package fr.n_peloton.n_peloton;

public class GpsiesItem {

    public String title, link, description,data;

    public GpsiesItem(String title,String link,String description){
        this.link=link;
        this.title=title;
        this.description=description;

    }

    public GpsiesItem(){
        this.link="NULL";
        this.title="NULL";
        this.description="NULL";
    }

    public String getTitle() {

        int end = title.length() -" [Frankreich]".length();
        return title.substring(0,end);
    }


    public String getId(){

        int start = link.indexOf("=")+1;
        int end= link.length();
        return link.substring(start,end);

    }

    public String getGPX(){

       String gpx = "http://www.gpsies.com/download.do?fileId="+getId()+"&filetype=gpxTrk" ;
        return gpx;

    }
}
