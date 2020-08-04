package main;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class Paket {
    private int refGonderi;
    private double gondericiX;
    private double gondericiY;
    private double aliciX;
    private double aliciY;
    private double ucret;
    private int durum;
    public Label gondericiLabel;
    public Label aliciLabel;
    public Rectangle gondericiLoc;
    public Rectangle aliciLoc;
    public Arrow arrow;

    public Paket(int refGonderi, double gondericiX, double gondericiY, double aliciX, double aliciY, double ucret, int durum) {
        this.refGonderi = refGonderi;
        this.gondericiX = gondericiX;
        this.gondericiY = gondericiY;
        this.aliciX = aliciX;
        this.aliciY = aliciY;
        this.ucret = ucret;
        this.durum = durum;
        gondericiLabel = new Label(Integer.toString(refGonderi));
        aliciLabel = new Label(Integer.toString(refGonderi));
        gondericiLoc = new Rectangle();
        aliciLoc = new Rectangle();
        arrow = new Arrow();

        arrow.setStroke(Color.GRAY);
        arrow.setArrowHeadStroke(Color.GRAY);
        arrow.prefWidth(5);

        gondericiLoc.setFill(Color.BLACK);
        gondericiLoc.setWidth(10);
        gondericiLoc.setHeight(10);
        gondericiLabel.setTextFill(Color.BLACK);
        gondericiLabel.setScaleX(2);
        gondericiLabel.setScaleY(2);

        aliciLoc.setFill(Color.TRANSPARENT);
        aliciLoc.setStroke(Color.BLACK);
        aliciLoc.setStrokeWidth(2);
        aliciLoc.setWidth(10);
        aliciLoc.setHeight(10);
        aliciLabel.setTextFill(Color.BLACK);
        aliciLabel.setScaleX(2);
        aliciLabel.setScaleY(2);
    }

    public Durak[] convertPaketToDuraklar() {
        Durak durak0 = new Durak(UUID.randomUUID().toString(), 1, this.gondericiX, this.gondericiY, 20, new ArrayList<>(Collections.singletonList(new Gonderi(this.refGonderi, 0, 0))));
        Durak durak1 = new Durak(UUID.randomUUID().toString(), 1, this.aliciX, this.aliciY, 20, new ArrayList<>(Collections.singletonList(new Gonderi(this.refGonderi, this.ucret, 1))));
        return new Durak[]{durak0, durak1};
    }

    public int getRefGonderi() {
        return refGonderi;
    }

    public void setRefGonderi(int refGonderi) {
        this.refGonderi = refGonderi;
    }

    public double getGondericiX() {
        return gondericiX;
    }

    public void setGondericiX(double gondericiX) {
        this.gondericiX = gondericiX;
    }

    public double getGondericiY() {
        return gondericiY;
    }

    public void setGondericiY(double gondericiY) {
        this.gondericiY = gondericiY;
    }

    public double getAliciX() {
        return aliciX;
    }

    public void setAliciX(double aliciX) {
        this.aliciX = aliciX;
    }

    public double getAliciY() {
        return aliciY;
    }

    public void setAliciY(double aliciY) {
        this.aliciY = aliciY;
    }

    public double getUcret() {
        return ucret;
    }

    public void setUcret(double ucret) {
        this.ucret = ucret;
    }

    public int getDurum() {
        return durum;
    }

    public void setDurum(int durum) {
        this.durum = durum;
    }
}
