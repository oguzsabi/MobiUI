package main;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Tasiyici {
    private int refTasiyici;
    private double durumX;
    private double durumY;
    private ArrayList<Vardiya> vardiyalar;
    private double originalKm;
    private int originalZaman;
    private int originalDurak;
    private double ekstraKm;
    private int ekstraZaman;
    private int ekstraDurak;
    public Label tasiyiciLabel;
    public Circle tasiyiciLoc;
    public Arrow arrow;

    public Tasiyici(int refTasiyici, double durumX, double durumY, ArrayList<Vardiya> vardiyalar) {
        this.refTasiyici = refTasiyici;
        this.durumX = durumX;
        this.durumY = durumY;
        this.vardiyalar = vardiyalar;
        this.tasiyiciLabel = new Label(Integer.toString(refTasiyici));

        double[] totalDistanceDuration = Calculator.calculateTasiyiciTotalDistanceDuration(this);
        originalKm = totalDistanceDuration[0];
        originalZaman = (int) totalDistanceDuration[1];
        originalDurak = 0;
        for (Vardiya vardiya: vardiyalar)
            originalDurak += vardiya.getDuraklar().size();

        ekstraKm = 0;
        ekstraZaman = 0;
        ekstraDurak = 0;

        tasiyiciLabel.setScaleX(2);
        tasiyiciLabel.setScaleY(2);
        arrow = new Arrow();
        tasiyiciLoc = new Circle(8);


    }

    public int getRefTasiyici() {
        return refTasiyici;
    }

    public void setRefTasiyici(int refTasiyici) {
        this.refTasiyici = refTasiyici;
    }

    public double getDurumX() {
        return durumX;
    }

    public void setDurumX(double durumX) {
        this.durumX = durumX;
    }

    public double getDurumY() {
        return durumY;
    }

    public void setDurumY(double durumY) {
        this.durumY = durumY;
    }

    public ArrayList<Vardiya> getVardiyalar() {
        return vardiyalar;
    }

    public void setVardiyalar(ArrayList<Vardiya> vardiyalar) {
        this.vardiyalar = vardiyalar;
    }

    public double getOriginalKm() {
        return originalKm;
    }

    public int getOriginalZaman() {
        return originalZaman;
    }

    public int getOriginalDurak() {
        return originalDurak;
    }

    public double getEkstraKm() {
        return ekstraKm;
    }

    public void setEkstraKm(double ekstraKm) {
        this.ekstraKm = ekstraKm;
    }

    public int getEkstraZaman() {
        return ekstraZaman;
    }

    public void setEkstraZaman(int ekstraZaman) {
        this.ekstraZaman = ekstraZaman;
    }

    public int getEkstraDurak() {
        return ekstraDurak;
    }

    public void setEkstraDurak(int ekstraDurak) {
        this.ekstraDurak = ekstraDurak;
    }
}
