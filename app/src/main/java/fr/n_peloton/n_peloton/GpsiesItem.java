package fr.n_peloton.n_peloton;


import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GpsiesItem {
    public String title;
    public String   id;
    public Double km;
    public Long positiveElevation;
    public Long negativeElevation;
    public long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> tags;

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public GpsiesItem(String id, String title) {
        this.id=id;
        this.title=title;
    }
    public GpsiesItem(String id, String title, Double km, Long positiveElevation, Long negativeElevation) {
        this.id=id;
        this.title=title;
        this.km=km;
        this.positiveElevation = positiveElevation;
        this. negativeElevation =negativeElevation;
    }

    public String getKmPrint(){

        return  km + "km";

    }
    public String getElevationPrint(){

        return  positiveElevation + "m↑ " +negativeElevation+"m↓";

    }


    public String showDate(){

        //convert seconds to milliseconds
        Date date = new Date(time*1000L);
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("dd/MM/yyyy");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String java_date = jdf.format(date);



       return java_date;

    }

    public  String getDescription(){
        String tagsString = "";
        for (int i = 0; i < tags.size(); i++) {

            tagsString+=" #"+tags.get(i);
        }
        return  getKmPrint() + "  " + getElevationPrint() ;

    }

    public String getTitlePrint() {
    if(title.contains("[France]")){
        int end = title.length() -" [France]".length();
        return title.substring(0,end).trim();
    }
    return title.trim();

    }

    public String getLinkGpsies(){

        String gpx = "https://n-peloton.fr/map/"+id ;
        return gpx;

    }

    public String getFileName(){
        String file_name = getTitlePrint().replaceAll("[^A-Za-z0-9]", "");
        return file_name;
    }


    public String getGPX(){

        String gpx = "https://n-peloton.fr/gpx/"+id;
        return gpx;

    }

    public String getImageUrl(){
        String imageUrl = "https://n-peloton.fr/staticmap/" +id;
        return imageUrl ;
    }


    public String getVueAllTrails(){
        //https://n-peloton.fr/map/#6eb42f63bcb96085
        String vue = "https://n-peloton.fr/mapFull/"+id;
        return vue ;
    }
}
