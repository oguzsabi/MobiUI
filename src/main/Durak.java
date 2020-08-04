package main;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Durak {
    private String durakId;
    private int gonderiSayisi;
    private double X;
    private double Y;
    private int varisSuresi;
    private ArrayList<Gonderi> gonderiler;
    public Rectangle durakLoc;
    public Label durakLabel;
    public Arrow arrow;

    public Durak(double x, double y, int varisSuresi) {
        X = x;
        Y = y;
        this.varisSuresi = varisSuresi;
        durakLabel = new Label(durakId);
        durakLabel.setTextFill(Color.BLUE);
        durakLabel.setScaleX(2);
        durakLabel.setScaleY(2);
        durakLoc = new Rectangle();
        durakLoc.setWidth(10);
        durakLoc.setHeight(10);
        arrow = new Arrow();
    }

    public Durak(String durakId, int gonderiSayisi, double x, double y, int varisSuresi, ArrayList<Gonderi> gonderiler) {
        this.durakId = durakId;
        this.gonderiSayisi = gonderiSayisi;
        X = x;
        Y = y;
        this.varisSuresi = varisSuresi;
        this.gonderiler = gonderiler;
        durakLabel = new Label(durakId);
        if (durakId.length() > 32) {
            durakLabel.setText(durakId.substring(0, 3));
        }
        durakLabel.setTextFill(Color.BLUE);
        durakLabel.setScaleX(2);
        durakLabel.setScaleY(2);
        durakLoc = new Rectangle();
        durakLoc.setWidth(10);
        durakLoc.setHeight(10);
        arrow = new Arrow();
    }

//    public Paket convertDuraklarToPaket(Durak gonderici, Durak alici) {
//        return new Paket(gonderici.refGonderi, gonderici.X, gonderici.Y, alici.X, alici.Y, alici.ucret);
//    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public int getVarisSuresi() {
        return varisSuresi;
    }

    public void setVarisSuresi(double varisSuresi) {
        this.varisSuresi = (int) varisSuresi;
    }

    public String getDurakId() {
        return durakId;
    }

    public void setDurakId(String durakId) {
        this.durakId = durakId;
    }

    public int getGonderiSayisi() {
        return gonderiSayisi;
    }

    public void setGonderiSayisi(int gonderiSayisi) {
        this.gonderiSayisi = gonderiSayisi;
    }

    public ArrayList<Gonderi> getGonderiler() {
        return gonderiler;
    }

    public void setGonderiler(ArrayList<Gonderi> gonderiler) {
        this.gonderiler = gonderiler;
    }
}
