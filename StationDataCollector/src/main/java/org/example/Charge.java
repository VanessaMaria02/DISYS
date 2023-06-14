package org.example;

public class Charge {

    private int id;

    private double kwh;

    private  int customer_id;

    public Charge(int id, double kwh, int customer_id){
        this.id = id;
        this.kwh = kwh;
        this.customer_id = customer_id;
    }

    public int getId() {
        return id;
    }

    public double getKwh() {
        return kwh;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    @Override
    public String toString(){
        return "Charge{"+
                "id="+id+
                ", kwh="+kwh+
                ", customer_id="+customer_id+
                "}";
    }
}
