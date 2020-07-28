package main;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Durak {
    private int refGonderi;
    private double X;
    private double Y;
    private int varisSuresi;
    private double ucret;
    private int teslimat;
    public Rectangle durakLoc;
    public Label durakLabel;
    public Arrow arrow;

    public Durak(int refGonderi, double x, double y, int varisSuresi, double ucret, int teslimat) {
        this.refGonderi = refGonderi;
        X = x;
        Y = y;
        this.varisSuresi = varisSuresi;
        this.ucret = ucret;
        this.teslimat = teslimat;
        durakLabel = new Label(Integer.toString(refGonderi));
        durakLabel.setTextFill(Color.BLUE);
        durakLabel.setScaleX(2);
        durakLabel.setScaleY(2);
        durakLoc = new Rectangle();
        durakLoc.setWidth(10);
        durakLoc.setHeight(10);
        arrow = new Arrow();
    }

    public Paket convertDuraklarToPaket(Durak gonderici, Durak alici) {
        return new Paket(gonderici.refGonderi, gonderici.X, gonderici.Y, alici.X, alici.Y, alici.ucret);
    }

    public int getRefGonderi() {
        return refGonderi;
    }

    public void setRefGonderi(int refGonderi) {
        this.refGonderi = refGonderi;
    }

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
