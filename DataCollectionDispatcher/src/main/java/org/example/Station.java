package org.example;

public class Station {
    private int id;
    private String db_url;
    private double lat;
    private double lng;

    public Station(int id, String db_url, double lat, double lng){
        this.id = id;
        this.db_url = db_url;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDb_url() {
        return db_url;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString(){
        return "Station{"+
                "id="+id+
                ", db_url='" + db_url +
                '\''+
                ", lat='"+ lat+ '\''+
                ", lng='"+lng+'\''+
                '}';
    }
}
