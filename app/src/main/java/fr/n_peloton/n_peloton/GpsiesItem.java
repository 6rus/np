package fr.n_peloton.n_peloton;

public class GpsiesItem {

    public String title,  id, data;

    public GpsiesItem(String id, String title) {
        this.id=id;
        this.title=title;
    }

    public String getTitle() {
    if(title.contains("[France]")){
        int end = title.length() -" [France]".length();
        return title.substring(0,end);
    }
    return title;

    }




    public String getGPX(){

       String gpx = "http://www.gpsies.com/download.do?fileId="+id+"&filetype=gpxTrk" ;
        return gpx;

    }
}
