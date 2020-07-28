package main;

import javafx.scene.shape.Polygon;

import java.util.ArrayList;


public class Vardiya {
    private int saatBaslangic;
    private int saatBitis;
    private int vardiyaBitisVarisSuresi;
    private double baslangicX;
    private double baslangicY;
    private double bitisX;
    private double bitisY;
    private ArrayList<Durak> duraklar;
    public Polygon vardiyaBaslangicLoc;
    public Polygon vardiyaBitisLoc;
    public Arrow arrow;

    public Vardiya(String saatBaslangic, String saatBitis, double baslangicX, double baslangicY, double bitisX, double bitisY, ArrayList<Durak> duraklar) {
        this.saatBaslangic = stringTimeToIntegerSeconds(saatBaslangic);
        this.saatBitis = stringTimeToIntegerSeconds(saatBitis);
        this.baslangicX = baslangicX;
        this.baslangicY = baslangicY;
        this.bitisX = bitisX;
        this.bitisY = bitisY;
        this.duraklar = duraklar;
        vardiyaBaslangicLoc = new Polygon();
        vardiyaBitisLoc = new Polygon();
        vardiyaBaslangicLoc.getPoints().addAll(0.0, 0.0,
                15.0, 5.0,
                5.0, 15.0);
        vardiyaBitisLoc.getPoints().addAll(0.0, 0.0,
                15.0, 5.0,
                5.0, 15.0);

        arrow = new Arrow();
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

    public String getSaatBaslangicAsString() {
        return integerSecondsToStringTime(saatBaslangic);
    }

    public String getSaatBitisAsString() {
        return integerSecondsToStringTime(saatBitis);
    }

    public int getSaatBaslangicInSeconds() {
        return saatBaslangic;
    }

    public void setSaatBaslangicInSeconds(int saatBaslangic) {
        this.saatBaslangic = saatBaslangic;
    }

    public int getSaatBitisInSeconds() {
        return saatBitis;
    }

    public void setSaatBitisInSeconds(int saatBitis) {
        this.saatBitis = saatBitis;
    }

    public double getBaslangicX() {
        return baslangicX;
    }

    public void setBaslangicX(double baslangicX) {
        this.baslangicX = baslangicX;
    }

    public double getBaslangicY() {
        return baslangicY;
    }

    public void setBaslangicY(double baslangicY) {
        this.baslangicY = baslangicY;
    }

    public double getBitisX() {
        return bitisX;
    }

    public void setBitisX(double bitisX) {
        this.bitisX = bitisX;
    }

    public double getBitisY() {
        return bitisY;
    }

    public void setBitisY(double bitisY) {
        this.bitisY = bitisY;
    }

    public ArrayList<Durak> getDuraklar() {
        return duraklar;
    }

    public void setDuraklar(ArrayList<Durak> duraklar) {
        this.duraklar = duraklar;
    }

    public int getVardiyaBitisVarisSuresi() {
        return vardiyaBitisVarisSuresi;
    }

    public void setVardiyaBitisVarisSuresi(int vardiyaBitisVarisSuresi) {
        this.vardiyaBitisVarisSuresi = vardiyaBitisVarisSuresi;
    }
}