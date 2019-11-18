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

    public String getLinkGpsies(){

        String gpx = "https://www.gpsies.com/map.do?fileId="+id ;
        return gpx;

    }

    public String getFileName(){
        String file_name = getTitle().replaceAll("[^A-Za-z0-9]", "")+ ".gpx";
        return file_name;
    }


    public String getGPX(){

        String gpx = "https://n-peloton.fr/gpx/gpx.php?id="+id + "&name="+getFileName() ;
        return gpx;

    }

    public String getImageUrl(){
        String imageUrl = "https://n-peloton.fr/gpx/image.php?id=" +id;
        return imageUrl ;
    }


    public String getVueAllTrails(){
        String vue = "https://www.alltrails.com/widget/map?file_id="+id+"&referrer=gpsies&l=fr&&layer=true";
        return vue ;
    }
}
