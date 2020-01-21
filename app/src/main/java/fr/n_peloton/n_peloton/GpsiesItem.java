package fr.n_peloton.n_peloton;

public class GpsiesItem {

    public String title,  id, km, elevation;

    public GpsiesItem(String id, String title) {
        this.id=id;
        this.title=title;
    }
    public GpsiesItem(String id, String title, String km, String elevation) {
        this.id=id;
        this.title=title;
        this.km=km;
        this.elevation = elevation;
    }

    public String getKm(){

        return  km + "km";

    }
    public String getElevation(){

        return  elevation.replace("+", "\u2191").replace("-"," \u2193") ;

    }


    public  String getDescription(){

        return  getKm() + "  " + getElevation();

    }

    public String getTitle() {
    if(title.contains("[France]")){
        int end = title.length() -" [France]".length();
        return title.substring(0,end).trim();
    }
    return title.trim();

    }

    public String getLinkGpsies(){

        String gpx = "https://n-peloton.fr/maps/?fileId="+id +"&name="+ getFileName();
        return gpx;

    }

    public String getFileName(){
        String file_name = getTitle().replaceAll("[^A-Za-z0-9]", "");
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
        String vue = "https://n-peloton.fr/maps/?fileId="+id +"&name="+getFileName();
        return vue ;
    }
}
