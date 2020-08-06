package main;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Tasiyici {
    private int refTasiyici;
    private double durumX;
    private double durumY;
    private int durumSaat;
    private String durumTarih;
    private double kmBeklenti;
    private int dkBeklenti;
    private int ratingSayisi;
    private int ratingToplam;
    private int vardiyaSayisi;
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

    public Tasiyici(int refTasiyici, double durumX, double durumY, String durumSaat, double kmBeklenti, int dkBeklenti, int ratingSayisi, int ratingToplam, int vardiyaSayisi, ArrayList<Vardiya> vardiyalar) {
        this.refTasiyici = refTasiyici;
        this.durumX = durumX;
        this.durumY = durumY;
        this.durumTarih = durumSaat.split(" ")[0];
        this.durumSaat = stringTimeToIntegerSeconds(durumSaat.split(" ")[1]);
        this.kmBeklenti = kmBeklenti;
        this.dkBeklenti = dkBeklenti;
        this.ratingSayisi = ratingSayisi;
        this.ratingToplam = ratingToplam;
        this.vardiyaSayisi = vardiyaSayisi;
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

    private int stringTimeToIntegerSeconds(String time) {
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]) * 3600;
        int minutes = Integer.parseInt(timeArray[1]) * 60;
        return hour + minutes;
    }

    private String integerSecondsToStringTime(int seconds) {
        int hour = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if (minutes == 0) {
            return hour + ":00";
        }

        return hour + ":" + minutes;
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

    public int getDurumSaatInSeconds() {
        return durumSaat;
    }

    public void setDurumSaatInSeconds(int durumSaat) {
        this.durumSaat = durumSaat;
    }

    public String getDurumSaatAsStringTime() {
        return integerSecondsToStringTime(durumSaat);
    }

    public double getKmBeklenti() {
        return kmBeklenti;
    }

    public void setKmBeklenti(double kmBeklenti) {
        this.kmBeklenti = kmBeklenti;
    }

    public int getDkBeklenti() {
        return dkBeklenti;
    }

    public void setDkBeklenti(int dkBeklenti) {
        this.dkBeklenti = dkBeklenti;
    }

    public int getRatingSayisi() {
        return ratingSayisi;
    }

    public void setRatingSayisi(int ratingSayisi) {
        this.ratingSayisi = ratingSayisi;
    }

    public int getRatingToplam() {
        return ratingToplam;
    }

    public void setRatingToplam(int ratingToplam) {
        this.ratingToplam = ratingToplam;
    }

    public int getVardiyaSayisi() {
        return vardiyaSayisi;
    }

    public void setVardiyaSayisi(int vardiyaSayisi) {
        this.vardiyaSayisi = vardiyaSayisi;
    }
}
