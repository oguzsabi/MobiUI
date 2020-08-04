package main;

public class Gonderi {
    private int refGonderi;
    private double ucret;
    private int teslimat;

    public Gonderi(int refGonderi, double ucret, int teslimat) {
        this.refGonderi = refGonderi;
        this.ucret = ucret;
        this.teslimat = teslimat;
    }

    public int getRefGonderi() {
        return refGonderi;
    }

    public void setRefGonderi(int refGonderi) {
        this.refGonderi = refGonderi;
    }

    public double getUcret() {
        return ucret;
    }

    public void setUcret(double ucret) {
        this.ucret = ucret;
    }

    public int getTeslimat() {
        return teslimat;
    }

    public void setTeslimat(int teslimat) {
        this.teslimat = teslimat;
    }
}
