package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MapScreen implements Initializable {
    @FXML ImageView mapImage;
    @FXML Pane mapPane;
    @FXML ScrollPane mapScrollPane;
    @FXML Slider zoomSlider;
    @FXML ListView<DraggableListItem> rotaInfo;
    @FXML ListView<DraggableListItem> tasiyiciInfo;
    @FXML ListView<DraggableListItem> paketInfo;
    @FXML Button vardiyaKaydet;
    @FXML Button bosPaketGosterGizle;
    @FXML Button butunRotalariGosterGizle;
    @FXML Button digerTasiyicilariGosterGizle;
    @FXML VBox wrapperLayout;
    @FXML VBox rightVBox;
    @FXML Label rotaOlanakLabel;
    @FXML Label ekstraKmLabel;
    @FXML Label ekstraZamanLabel;
    @FXML Label ekstraDurakLabel;
    @FXML ImageView locationMark;
    @FXML ImageView locationMark1;
    @FXML CheckMenuItem optimalPaketEkleme;
    private Group zoomGroup;

    // Constants
    private final double latitudeDivider = 0.0001344136;
    private final double longitudeDivider = 0.0001716615;
    private final double maxLat = 38.616870;
    private final double maxLon = 27.290039;
    private final int paketLabelOffset = 15;
    private final Color[] tasiyiciColors = {
            Color.GREEN,
            Color.ORANGE,
            Color.RED,
            Color.BROWN,
            Color.ROSYBROWN,
            Color.PURPLE,
            Color.PINK,
            Color.YELLOW,
            Color.DARKCYAN,
            Color.DARKGREEN,
            Color.DARKMAGENTA,
            Color.TURQUOISE,
            Color.DARKTURQUOISE,
            Color.MAROON,
            Color.LIGHTBLUE,
            Color.LIGHTCORAL,
            Color.CRIMSON,
            Color.DARKSEAGREEN,
            Color.LIME,
            Color.GOLD
    };
    private double mapWidth;

    // selectedShapes is used for getting circle data (instance it belongs to) for later use
    private final ArrayList<Shape> selectedShapes = new ArrayList<>();
    // addedShapes is used for removing new vardiya or durak circles
    private final ArrayList<Shape> addedShapes = new ArrayList<>();
    // addedLabels is used for removing durak labels
    private final ArrayList<Label> addedLabels = new ArrayList<>();
    // emptyPaketArrows keeps packets which are waiting to be delivered. They are removed or added.
    private final ArrayList<Arrow> emptyPaketArrows = new ArrayList<>();
    // rotaLines keeps the new rota lines for the selected tasiyici
    private final ArrayList<Arrow> rotaLines = new ArrayList<>();
    // addedFiles keeps the added files so that they won't be added again
//    private final ArrayList<File> addedFiles = new ArrayList<>();
    private final ArrayList<Paket> convertedPaketler = new ArrayList<>();
    private final ArrayList<Durak[]> duraklarFromPaketler = new ArrayList<>();
    private ObservableList<DraggableListItem> rotaInfoList;
    private ListView<DraggableListItem> durakInfo;
    private boolean isTasiyiciSelected;
    private boolean isDurakSelected = false;
    private Tasiyici selectedTasiyici;
    private DraggableListItem selectedListItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CSVReader.readDistanceDurationCSV();
        CSVReader.readDistrictCSV();

        removeLocationMark();
        locationMark.setImage(new Image("res/images/locationMark.png"));
        locationMark.setFitWidth(60);
        locationMark.setFitHeight(60);
        locationMark.setOnMouseClicked(locationMarkClicked);

        locationMark1.setImage(new Image("res/images/locationMark.png"));
        locationMark1.setFitWidth(60);
        locationMark1.setFitHeight(60);
        locationMark1.setOnMouseClicked(locationMarkClicked);

        rotaInfoList = rotaInfo.getItems();
        rotaInfo.setCellFactory(param -> new DraggableListCell());
        tasiyiciInfo.setCellFactory(param -> new DraggableListCell());
        paketInfo.setCellFactory(param -> new DraggableListCell());
        durakInfo = new ListView<>();
        durakInfo.setMaxWidth(501);
        durakInfo.setMaxHeight(200);
        durakInfo.setCellFactory(param -> new DraggableListCell());

        isTasiyiciSelected = false;
        rotaOlanakLabel.setVisible(false);
        vardiyaKaydet.setDisable(true);
        digerTasiyicilariGosterGizle.setDisable(true);
        butunRotalariGosterGizle.setDisable(true);
        bosPaketGosterGizle.setDisable(true);

        mapScrollPane.setPannable(true);
        mapScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mapScrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            double sliderVal = zoomSlider.getValue();
            if (event.getDeltaY() > 0) {
                zoomSlider.setValue(sliderVal + 0.1);
            } else {
                zoomSlider.setValue(sliderVal + -0.1);
            }
            event.consume();
        });

        mapWidth = mapImage.getImage().getWidth();

        zoomSlider.setMin(0.414);
        zoomSlider.setMax(4);
        zoomSlider.setValue(0.414);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(mapScrollPane.getContent());
        mapScrollPane.setContent(contentGroup);
        zoom(0.414);
    }

    @FXML
    public void zoomIn() {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + 0.1);
    }

    @FXML
    public void zoomOut() {
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + -0.1);
    }

    private void zoom(double scaleValue) {
        double scrollH = mapScrollPane.getHvalue();
        double scrollV = mapScrollPane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        mapScrollPane.setHvalue(scrollH);
        mapScrollPane.setVvalue(scrollV);
    }

    private void markBekleyenPaketler() {
        ArrayList<Paket> paketler = XMLParser.paketler;

        for (Paket paket : paketler) {
            Rectangle gondericiLoc = paket.gondericiLoc;

            gondericiLoc.setUserData(paket);
            gondericiLoc.setOnMouseClicked(paketLocClicked);
            Label gondericiLabel = paket.gondericiLabel;

            double latY = (maxLat - paket.getGondericiY()) / latitudeDivider;
            double lonX = mapWidth - ((maxLon - paket.getGondericiX()) / longitudeDivider);

            gondericiLoc.setLayoutX(lonX);
            gondericiLoc.setLayoutY(latY);
            gondericiLabel.setLayoutX(lonX + paketLabelOffset);
            gondericiLabel.setLayoutY(latY + paketLabelOffset);

            gondericiLoc.setTranslateX(-gondericiLoc.getWidth()/2);
            gondericiLoc.setTranslateY(-gondericiLoc.getHeight()/2);
            mapPane.getChildren().addAll(gondericiLoc, gondericiLabel);


            Rectangle aliciLoc = paket.aliciLoc;

            aliciLoc.setUserData(paket);
            aliciLoc.setOnMouseClicked(paketLocClicked);
            Label aliciLabel = paket.aliciLabel;

            latY = (maxLat - paket.getAliciY()) / latitudeDivider;
            lonX = mapWidth - ((maxLon - paket.getAliciX()) / longitudeDivider);

            aliciLoc.setLayoutX(lonX);
            aliciLoc.setLayoutY(latY);
            aliciLabel.setLayoutX(lonX + paketLabelOffset);
            aliciLabel.setLayoutY(latY + paketLabelOffset);

            aliciLoc.setTranslateX(-aliciLoc.getWidth()/2);
            aliciLoc.setTranslateY(-aliciLoc.getHeight()/2);
            mapPane.getChildren().addAll(aliciLoc, aliciLabel);

            drawArrow(gondericiLoc, aliciLoc);
        }
    }

    private void markTasiyicilar() {
        ArrayList<Tasiyici> tasiyicilar = XMLParser.tasiyicilar;
        for (int i = 0; i < tasiyicilar.size(); i++) {
            Circle tasiyiciLoc = tasiyicilar.get(i).tasiyiciLoc;
            tasiyiciLoc.setOnMouseClicked(tasiyiciLocClicked);
            tasiyiciLoc.setUserData(tasiyicilar.get(i));
            Label tasiyiciLabel = tasiyicilar.get(i).tasiyiciLabel;
            tasiyiciLabel.setTextFill(tasiyiciColors[i]);
            tasiyiciLoc.setFill(tasiyiciColors[i]);

            double latY = (maxLat - tasiyicilar.get(i).getDurumY()) / latitudeDivider;
            double lonX = mapWidth - ((maxLon - tasiyicilar.get(i).getDurumX()) / longitudeDivider);

            tasiyiciLoc.setLayoutX(lonX);
            tasiyiciLoc.setLayoutY(latY);
            tasiyiciLabel.setLabelFor(tasiyiciLoc);
            int tasiyiciLabelOffset = 7;
            tasiyiciLabel.setLayoutX(lonX + tasiyiciLabelOffset);
            tasiyiciLabel.setLayoutY(latY + tasiyiciLabelOffset);

            mapPane.getChildren().addAll(tasiyiciLoc, tasiyiciLabel);
        }
    }

    private void markSelectedTasiyiciVardiyalar() {
        ArrayList<Vardiya> vardiyalar = selectedTasiyici.getVardiyalar();
        Color selectedTasiyiciColor = (Color)selectedTasiyici.tasiyiciLoc.getFill();

        for (Vardiya vardiya : vardiyalar) {
            Shape vardiyaBaslangicLoc = vardiya.vardiyaBaslangicLoc;
            Shape vardiyaBitisLoc = vardiya.vardiyaBitisLoc;
            vardiyaBaslangicLoc.setFill(selectedTasiyiciColor);
            vardiyaBitisLoc.setFill(Color.TRANSPARENT);
            vardiyaBitisLoc.setStroke(selectedTasiyiciColor);
            vardiyaBitisLoc.setStrokeWidth(2);
            vardiyaBaslangicLoc.setUserData(vardiya);
            vardiyaBitisLoc.setUserData(vardiya);
            vardiyaBaslangicLoc.setOnMouseClicked(vardiyaLocClicked);
            vardiyaBitisLoc.setOnMouseClicked(vardiyaLocClicked);
            addedShapes.add(vardiyaBaslangicLoc);
            addedShapes.add(vardiyaBitisLoc);

            double latY = (maxLat - vardiya.getBaslangicY()) / latitudeDivider;
            double lonX = mapWidth - ((maxLon - vardiya.getBaslangicX()) / longitudeDivider);

            vardiyaBaslangicLoc.setLayoutX(lonX);
            vardiyaBaslangicLoc.setLayoutY(latY);

            mapPane.getChildren().remove(vardiyaBaslangicLoc);
            mapPane.getChildren().add(vardiyaBaslangicLoc);

            latY = (maxLat - vardiya.getBitisY()) / latitudeDivider;
            lonX = mapWidth - ((maxLon - vardiya.getBitisX()) / longitudeDivider);

            vardiyaBitisLoc.setLayoutX(lonX);
            vardiyaBitisLoc.setLayoutY(latY);

            mapPane.getChildren().remove(vardiyaBitisLoc);
            mapPane.getChildren().add(vardiyaBitisLoc);

            addToListView(-1, vardiya, rotaInfo);

            ArrayList<Durak> duraklar = vardiya.getDuraklar();

            for (Durak durak : duraklar) {
                Rectangle durakLoc = durak.durakLoc;
                durakLoc.setFill(selectedTasiyiciColor);
                durakLoc.setUserData(durak);
                durakLoc.setTranslateX(-durakLoc.getWidth() / 2);
                durakLoc.setTranslateY(-durakLoc.getHeight() / 2);
                durakLoc.setOnMouseClicked(durakLocClicked);
                Label durakLabel = durak.durakLabel;
                durakLabel.setTextFill(selectedTasiyiciColor);
                addedShapes.add(durakLoc);
                addedLabels.add(durakLabel);

                latY = (maxLat - durak.getY()) / latitudeDivider;
                lonX = mapWidth - ((maxLon - durak.getX()) / longitudeDivider);

                durakLoc.setLayoutX(lonX);
                durakLoc.setLayoutY(latY);
                durakLabel.setLayoutX(lonX + paketLabelOffset);
                durakLabel.setLayoutY(latY + paketLabelOffset);

                mapPane.getChildren().removeAll(durakLoc, durakLabel);
                mapPane.getChildren().addAll(durakLoc, durakLabel);

                addToListView(-1, durak, rotaInfo);
            }
        }
        drawNewRouteArrows();
    }

    private void markAllOtherVardiyalar() {
        ArrayList<Tasiyici> tasiyicilar = XMLParser.tasiyicilar;

        for (Tasiyici tasiyici : tasiyicilar) {
            if (tasiyici != selectedTasiyici) {
                ArrayList<Vardiya> vardiyalar = tasiyici.getVardiyalar();
                Color selectedTasiyiciColor = (Color) tasiyici.tasiyiciLoc.getFill();

                for (Vardiya vardiya : vardiyalar) {
                    Shape vardiyaBaslangicLoc = vardiya.vardiyaBaslangicLoc;
                    Shape vardiyaBitisLoc = vardiya.vardiyaBitisLoc;
                    vardiyaBaslangicLoc.setFill(selectedTasiyiciColor);
                    vardiyaBitisLoc.setFill(Color.TRANSPARENT);
                    vardiyaBitisLoc.setStroke(selectedTasiyiciColor);
                    vardiyaBitisLoc.setStrokeWidth(2);
                    vardiyaBaslangicLoc.setUserData(vardiya);
                    vardiyaBitisLoc.setUserData(vardiya);
                    vardiyaBaslangicLoc.setOnMouseClicked(vardiyaLocClicked);
                    vardiyaBitisLoc.setOnMouseClicked(vardiyaLocClicked);
                    addedShapes.add(vardiyaBaslangicLoc);
                    addedShapes.add(vardiyaBitisLoc);

                    double latY = (maxLat - vardiya.getBaslangicY()) / latitudeDivider;
                    double lonX = mapWidth - ((maxLon - vardiya.getBaslangicX()) / longitudeDivider);

                    vardiyaBaslangicLoc.setLayoutX(lonX);
                    vardiyaBaslangicLoc.setLayoutY(latY);

                    mapPane.getChildren().remove(vardiyaBaslangicLoc);
                    mapPane.getChildren().add(vardiyaBaslangicLoc);

                    latY = (maxLat - vardiya.getBitisY()) / latitudeDivider;
                    lonX = mapWidth - ((maxLon - vardiya.getBitisX()) / longitudeDivider);

                    vardiyaBitisLoc.setLayoutX(lonX);
                    vardiyaBitisLoc.setLayoutY(latY);

                    mapPane.getChildren().remove(vardiyaBitisLoc);
                    mapPane.getChildren().add(vardiyaBitisLoc);

                    ArrayList<Durak> duraklar = vardiya.getDuraklar();


                    for (Durak durak : duraklar) {
                        Rectangle durakLoc = durak.durakLoc;
                        durakLoc.setFill(selectedTasiyiciColor);

                        durakLoc.setUserData(durak);
                        durakLoc.setOnMouseClicked(durakLocClicked);
                        Label durakLabel = durak.durakLabel;
                        durakLabel.setTextFill(selectedTasiyiciColor);
                        addedShapes.add(durakLoc);
                        addedLabels.add(durakLabel);

                        latY = (maxLat - durak.getY()) / latitudeDivider;
                        lonX = mapWidth - ((maxLon - durak.getX()) / longitudeDivider);

                        durakLoc.setLayoutX(lonX);
                        durakLoc.setLayoutY(latY);
                        durakLoc.setTranslateX(-durakLoc.getWidth()/2);
                        durakLoc.setTranslateY(-durakLoc.getHeight()/2);
                        durakLabel.setLayoutX(lonX + paketLabelOffset);
                        durakLabel.setLayoutY(latY + paketLabelOffset);

                        mapPane.getChildren().removeAll(durakLoc, durakLabel);
                        mapPane.getChildren().addAll(durakLoc, durakLabel);
                    }
                }
            }
        }
    }

    private void selectTasiyici(Tasiyici tasiyici) {
        addToListView(-1, tasiyici, rotaInfo);

        isTasiyiciSelected = !isTasiyiciSelected;
        if (isTasiyiciSelected) {
            vardiyaKaydet.setDisable(false);
            digerTasiyicilariGosterGizle.setDisable(false);

            removeRotaArrows();
            selectedTasiyici = tasiyici;
            markAllOtherVardiyalar();
            markSelectedTasiyiciVardiyalar();

            ekstraKmLabel.setText(String.format("%.1f", selectedTasiyici.getEkstraKm()));
            ekstraZamanLabel.setText(Integer.toString(selectedTasiyici.getEkstraZaman()));
            ekstraDurakLabel.setText(Integer.toString(selectedTasiyici.getEkstraDurak()));

            selectedShapes.clear();
            selectedShapes.add(tasiyici.tasiyiciLoc);
        } else {
            returnToDefaultState();
        }
    }

    private final EventHandler<MouseEvent> tasiyiciLocClicked = t -> {
        Tasiyici tasiyici = ((Tasiyici)((Circle)t.getSource()).getUserData());
        rotaInfo.getItems().clear();
        selectTasiyici(tasiyici);
    };

    private final EventHandler<MouseEvent> paketLocClicked = t -> {
        Shape selectedShape = (Shape) t.getSource();
        Paket paket = (Paket) selectedShape.getUserData();

        if (isTasiyiciSelected) {
            if (isShapeNotSelectedBefore(selectedShape)) {
//                selectedShapes.add((Shape)t.getSource());
                vardiyaKaydet.setDisable(false);
                convertPaketToDuraklar(paket);
                drawNewRouteArrows();
            }
        } else {
            if (isShapeNotSelectedBefore(selectedShape)) {
                paket.arrow.setStroke(Color.BLUE);
                paket.arrow.setArrowHeadStroke(Color.BLUE);
                selectedShapes.add(selectedShape);
                addToListView(-1, paket, rotaInfo);
            }
        }
    };

    private final EventHandler<MouseEvent> vardiyaLocClicked = t -> {
        Shape selectedShape = (Shape) t.getSource();
        Vardiya vardiya = (Vardiya) selectedShape.getUserData();
        vardiya.arrow.setStroke(Color.BLUE);
        vardiya.arrow.setArrowHeadStroke(Color.BLUE);
    };

    private final EventHandler<MouseEvent> durakLocClicked = t -> {
        isDurakSelected = !isDurakSelected;
        Shape selectedShape = (Shape) t.getSource();
        Durak durak = (Durak) selectedShape.getUserData();
        highlightClickedDurakInListView(durak);
        if (isDurakSelected) {
            durak.arrow.setStroke(Color.BLUE);
            durak.arrow.setArrowHeadStroke(Color.BLUE);
        } else if (isTasiyiciSelected) {
            durak.arrow.setStroke((Color)selectedTasiyici.tasiyiciLoc.getFill());
            durak.arrow.setArrowHeadStroke((Color)selectedTasiyici.tasiyiciLoc.getFill());
        }
        if (!isTasiyiciSelected && isShapeNotSelectedBefore(selectedShape)) {
            selectedShapes.add(selectedShape);
            addToListView(-1, durak, rotaInfo);
        }
    };

    private final EventHandler<ActionEvent> hideEmptyPakets = t -> disableAllEmptyPackets();

    private final EventHandler<ActionEvent> showEmptyPakets = t -> enableAllEmptyPackets();

    private final EventHandler<ActionEvent> addAllRoutes = t -> drawAllTasiyiciRoutes();

    private final EventHandler<ActionEvent> removeAllRoutes = t -> removeRotaArrows();

    private final EventHandler<ActionEvent> removeOtherTasiyicilar = t -> removeOtherTasiyicilar();

    private final EventHandler<ActionEvent> addOtherTasiyicilar = t -> addOtherTasiyicilar();

    private final EventHandler<MouseEvent> locationMarkClicked = t -> {
      rotaInfo.getSelectionModel().clearSelection();
      locationMark.setVisible(false);
      locationMark1.setVisible(false);
    };

    @FXML
    private void disableAllEmptyPackets() {
        for (Paket paket: XMLParser.paketler) {
            paket.gondericiLoc.setVisible(false);
            paket.gondericiLabel.setVisible(false);
            paket.aliciLoc.setVisible(false);
            paket.aliciLabel.setVisible(false);
        }
        bosPaketGosterGizle.setText("Bos Paketleri Goster");
        bosPaketGosterGizle.setOnAction(showEmptyPakets);
        hidePaketArrows();
    }

    @FXML
    private void enableAllEmptyPackets() {
        for (Paket paket: XMLParser.paketler) {
            paket.arrow.setStroke(Color.GRAY);
            paket.arrow.setArrowHeadStroke(Color.GRAY);
            paket.gondericiLoc.setVisible(true);
            paket.gondericiLabel.setVisible(true);
            paket.aliciLoc.setVisible(true);
            paket.aliciLabel.setVisible(true);
        }
        bosPaketGosterGizle.setText("Bos Paketleri Gizle");
        bosPaketGosterGizle.setOnAction(hideEmptyPakets);
        showPaketArrows();
    }

    @FXML
    private void drawAllTasiyiciRoutes() {
        if (isTasiyiciSelected) {
            selectedTasiyici = null;
            isTasiyiciSelected = false;
            clearEkstraLabels();

            // if other tasiyicilar are hidden when we press butunRotalariGosterGizle then we also make other tasiyicilar visible
            digerTasiyicilariGosterGizle.setDisable(true);
            if (digerTasiyicilariGosterGizle.getText().contains("Goster")) {
                addOtherTasiyicilar();
            }
            rotaInfo.getItems().clear();
        }
        vardiyaKaydet.setDisable(true);

        removeRotaArrows();
        for (Tasiyici tasiyici: XMLParser.tasiyicilar) {
            ArrayList<Vardiya> vardiyalar = tasiyici.getVardiyalar();
            Color selectedTasiyiciColor = (Color)tasiyici.tasiyiciLoc.getFill();

            for (int vardiyaIndex = 0; vardiyaIndex < vardiyalar.size(); vardiyaIndex++) {
                Vardiya vardiya = vardiyalar.get(vardiyaIndex);

                Shape vardiyaBaslangicLoc = vardiya.vardiyaBaslangicLoc;
                Shape vardiyaBitisLoc = vardiya.vardiyaBitisLoc;

                if (vardiyaIndex == 0) {
                    drawArrow(tasiyici.tasiyiciLoc, vardiyaBaslangicLoc, selectedTasiyiciColor);
                }

                ArrayList<Durak> duraklar = vardiya.getDuraklar();

                for (int durakIndex = 0; durakIndex < duraklar.size(); durakIndex++) {
                    Durak durak = duraklar.get(durakIndex);
                    Rectangle durakLoc = durak.durakLoc;

                    if (durakIndex == 0) {
                        drawArrow(vardiyaBaslangicLoc, durakLoc, selectedTasiyiciColor);
                    }

                    if (durakIndex - 1 >= 0) {
                        drawArrow(duraklar.get(durakIndex - 1).durakLoc, durak.durakLoc, selectedTasiyiciColor);
                    }

                }
                if (vardiyaIndex == 0) {
                    if (vardiya.getDuraklar().size() > 0) {
                        drawArrow(duraklar.get(duraklar.size() - 1).durakLoc, vardiyaBitisLoc, selectedTasiyiciColor);
                    }
                }
                else {
                    if (duraklar.size() > 0) {
                        drawArrow(duraklar.get(duraklar.size() - 1).durakLoc, vardiyaBitisLoc, selectedTasiyiciColor);
                    } else {
                        drawArrow(vardiyaBaslangicLoc, vardiyaBitisLoc, Color.GRAY);
                    }
                    drawArrowBetweenVardiyaEndToStart(vardiyalar.get(vardiyalar.size() - 2).vardiyaBitisLoc, vardiya.vardiyaBaslangicLoc);
                }
            }
        }
        butunRotalariGosterGizle.setOnAction(removeAllRoutes);
        butunRotalariGosterGizle.setText("Butun Rotalari Gizle");
    }

    @FXML
    private void removeOtherTasiyicilar() {
        rotaInfoList.clear();
        addToListView(-1, selectedTasiyici, rotaInfo);
        removeAddedLabels();
        removeAddedShapes();
        disableAllOtherTasiyicilar();
        markSelectedTasiyiciVardiyalar();
        digerTasiyicilariGosterGizle.setText("Diger Tasiyicilari Goster");
        digerTasiyicilariGosterGizle.setOnAction(addOtherTasiyicilar);
    }

    private void addOtherTasiyicilar() {
        markAllOtherVardiyalar();
        enableAllOtherTasiyicilar();
        digerTasiyicilariGosterGizle.setText("Diger Tasiyicilari Gizle");
        digerTasiyicilariGosterGizle.setOnAction(removeOtherTasiyicilar);
    }

    private void showPaketArrows() {
        for (Arrow line: emptyPaketArrows) {
            line.setVisible(true);
        }
    }

    private void hidePaketArrows() {
        for (Arrow line: emptyPaketArrows) {
            line.setVisible(false);
        }
    }

    private void removeRotaArrows() {
        ObservableList<Node> mapPaneChildren = mapPane.getChildren();
        for (Arrow arrow: rotaLines) {
            mapPaneChildren.remove(arrow);
        }
        butunRotalariGosterGizle.setOnAction(addAllRoutes);
        butunRotalariGosterGizle.setText("Butun Rotalari Goster");
        rotaLines.clear();
    }

    private void removeAddedShapes() {
        ObservableList<Node> mapPaneChildren = mapPane.getChildren();
        for (Shape shape: addedShapes) {
            mapPaneChildren.remove(shape);
        }
        addedShapes.clear();
    }

    private void removeAddedLabels() {
        ObservableList<Node> mapPaneChildren = mapPane.getChildren();
        for (Label label: addedLabels) {
            mapPaneChildren.remove(label);
        }
        addedLabels.clear();
    }

    private void removeOldTasiyiciMarks() {
        returnToDefaultState();

        for (Tasiyici tasiyici: XMLParser.tasiyicilar) {
            mapPane.getChildren().remove(tasiyici.tasiyiciLoc);
            mapPane.getChildren().remove(tasiyici.tasiyiciLabel);
            mapPane.getChildren().remove(tasiyici.arrow);

            for (Vardiya vardiya: tasiyici.getVardiyalar()) {
                mapPane.getChildren().remove(vardiya.vardiyaBaslangicLoc);
                mapPane.getChildren().remove(vardiya.vardiyaBitisLoc);
                mapPane.getChildren().remove(vardiya.arrow);

                for (Durak durak: vardiya.getDuraklar()) {
                    mapPane.getChildren().remove(durak.durakLoc);
                    mapPane.getChildren().remove(durak.durakLabel);
                    mapPane.getChildren().remove(durak.arrow);
                }
            }
        }

        XMLParser.tasiyicilar.clear();
    }

    private void removeOldPaketMarks() {
        returnToDefaultState();

        for (Paket paket: XMLParser.paketler) {
            mapPane.getChildren().remove(paket.gondericiLoc);
            mapPane.getChildren().remove(paket.gondericiLabel);
            mapPane.getChildren().remove(paket.aliciLoc);
            mapPane.getChildren().remove(paket.aliciLabel);
            mapPane.getChildren().remove(paket.arrow);
        }

        XMLParser.paketler.clear();
    }

    private void removeLocationMark() {
        locationMark.setVisible(false);
        locationMark1.setVisible(false);
    }

    private void clearEkstraLabels() {
        ekstraKmLabel.setText("0.0");
        ekstraZamanLabel.setText("0");
        ekstraDurakLabel.setText("0");
    }

    private void disableAllOtherTasiyicilar() {
        for (Tasiyici tasiyici: XMLParser.tasiyicilar) {
            if (tasiyici != selectedTasiyici) {
                tasiyici.tasiyiciLoc.setVisible(false);
                tasiyici.tasiyiciLabel.setVisible(false);
            }
        }
    }

    private void enableAllOtherTasiyicilar() {
        for (Tasiyici tasiyici: XMLParser.tasiyicilar) {
            tasiyici.tasiyiciLoc.setVisible(true);
            tasiyici.tasiyiciLabel.setVisible(true);
        }
    }

    private void convertPaketToDuraklar(Paket paket) {
        XMLParser.paketler.remove(paket);
        paketInfo.getItems().removeIf(item -> item.getItemObject() == paket);
        emptyPaketArrows.remove(paket.arrow);
        convertedPaketler.add(paket);
        Color selectedTasiyiciColor = (Color) selectedTasiyici.tasiyiciLoc.getFill();

        Durak[] duraklar = paket.convertPaketToDuraklar();
        duraklarFromPaketler.add(duraklar);
        duraklar[0].arrow = paket.arrow;
        duraklar[0].arrow.setStroke(selectedTasiyiciColor);
        duraklar[0].durakLoc = paket.gondericiLoc;
        duraklar[0].durakLoc.setUserData(duraklar[0]);
        duraklar[0].durakLoc.setFill(selectedTasiyiciColor);
        duraklar[0].durakLabel = paket.gondericiLabel;
        duraklar[0].durakLabel.setTextFill(selectedTasiyiciColor);

        duraklar[1].arrow.setStroke(selectedTasiyiciColor);
        duraklar[1].durakLoc = paket.aliciLoc;
        duraklar[1].durakLoc.setUserData(duraklar[1]);
        duraklar[1].durakLoc.setStroke(selectedTasiyiciColor);
        duraklar[1].durakLabel = paket.aliciLabel;
        duraklar[1].durakLabel.setTextFill(selectedTasiyiciColor);

        if (optimalPaketEkleme.isSelected()) {
            int[] durakIndexes = Calculator.findDuraksSubOptimalIndex(rotaInfoList, duraklar);
            addToListView(durakIndexes[0], duraklar[0], rotaInfo);
            addToListView(durakIndexes[1], duraklar[1], rotaInfo);
        } else {
            int index = findDuraksFirstFeasibleIndex(duraklar);
            addToListView(index, duraklar[0], rotaInfo);
            addToListView(index + 1, duraklar[1], rotaInfo);
        }

        selectedTasiyici.setEkstraDurak(selectedTasiyici.getEkstraDurak() + 2);
        ekstraDurakLabel.setText(Integer.toString(selectedTasiyici.getEkstraDurak()));
        changePaketLocClickedToDurakLocClicked(duraklar);
    }

    private void convertDuraklarToPaket(Durak[] duraklar) {
        Paket paket = null;

        for (int i = 0; i < convertedPaketler.size(); i++) {
            Paket p = convertedPaketler.get(i);
            if (duraklar[0].getGonderiler().get(0).getRefGonderi() == p.getRefGonderi()) {
                paket = p;
                convertedPaketler.remove(p);
            }
        }

        if (paket != null) {
            int durak0 = 0;
            int durak1 = 0;
            for (int i = 0; i < rotaInfoList.size(); i++) {
                DraggableListItem item = rotaInfoList.get(i);
                if (item.getItemObject() == duraklar[0]) {
                    durak0 = i;
                } else if (item.getItemObject() == duraklar[1]) {
                    durak1 = i;
                }
            }

            selectedTasiyici.setEkstraDurak(selectedTasiyici.getEkstraDurak() - 2);
            ekstraDurakLabel.setText(Integer.toString(selectedTasiyici.getEkstraDurak()));
            rotaInfoList.remove(durak1);
            rotaInfoList.remove(durak0);
            duraklarFromPaketler.remove(duraklar);
            XMLParser.paketler.add(paket);
            addToListView(-1, paket, paketInfo);
            rotaLines.remove(paket.arrow);

            paket.gondericiLoc = duraklar[0].durakLoc;
            paket.gondericiLoc.setFill(Color.BLACK);
            paket.gondericiLoc.setUserData(paket);
            paket.gondericiLoc.setOnMouseClicked(paketLocClicked);
            paket.gondericiLabel.setTextFill(Color.BLACK);
            paket.aliciLoc = duraklar[1].durakLoc;
            paket.aliciLoc.setStroke(Color.BLACK);
            paket.aliciLoc.setUserData(paket);
            paket.aliciLoc.setOnMouseClicked(paketLocClicked);
            paket.aliciLabel.setTextFill(Color.BLACK);

            drawArrow(paket.gondericiLoc, paket.aliciLoc);
            drawNewRouteArrows();
        }
    }

    private void drawArrow(Rectangle first, Rectangle second) {
        Arrow arrow = ((Paket) first.getUserData()).arrow;
        arrow.setStartX(first.getLayoutX());
        arrow.setStartY(first.getLayoutY());
        arrow.setEndX(second.getLayoutX());
        arrow.setEndY(second.getLayoutY());
        arrow.setStroke(Color.GRAY);
        arrow.setArrowHeadStroke(Color.GRAY);
        mapPane.getChildren().remove(arrow);
        mapPane.getChildren().add(arrow);
        emptyPaketArrows.add(arrow);
    }

    private void drawArrow(Shape first, Shape second, Color color) {
        Arrow arrow = getArrowInstance(first);
        arrow.setStartX(first.getLayoutX());
        arrow.setStartY(first.getLayoutY());
        arrow.setEndX(second.getLayoutX());
        arrow.setEndY(second.getLayoutY());
        arrow.setStroke(color);
        arrow.setArrowHeadStroke(color);
        mapPane.getChildren().remove(arrow);
        mapPane.getChildren().add(arrow);
        rotaLines.add(arrow);
    }

    private void drawNewRouteArrows() {
        removeRotaArrows();
        changeVardiyaAndDurakOrder();
        updateVarisSureleri();

        if (isRouteFeasible()) {
            rotaOlanakLabel.setVisible(true);
            rotaOlanakLabel.setTextFill(Color.GREEN);
            rotaOlanakLabel.setText("Rota Uygulanabilir");
        } else {
            rotaOlanakLabel.setVisible(true);
            rotaOlanakLabel.setTextFill(Color.RED);
            rotaOlanakLabel.setText("Rota Uygulanamaz");
        }

        ArrayList<Vardiya> vardiyalar = selectedTasiyici.getVardiyalar();
        Color selectedTasiyiciColor = (Color)selectedTasiyici.tasiyiciLoc.getFill();

        for (int vardiyaIndex = 0; vardiyaIndex < vardiyalar.size(); vardiyaIndex++) {
            Vardiya vardiya = vardiyalar.get(vardiyaIndex);

            Shape vardiyaBaslangicLoc = vardiya.vardiyaBaslangicLoc;
            Shape vardiyaBitisLoc = vardiya.vardiyaBitisLoc;

            if (vardiyaIndex == 0) {
                drawArrow(selectedTasiyici.tasiyiciLoc, vardiyaBaslangicLoc, selectedTasiyiciColor);
            }

            ArrayList<Durak> duraklar = vardiya.getDuraklar();

            for (int durakIndex = 0; durakIndex < duraklar.size(); durakIndex++) {
                Durak durak = duraklar.get(durakIndex);
                Rectangle durakLoc = durak.durakLoc;

                if (durakIndex == 0) {
                    drawArrow(vardiyaBaslangicLoc, durakLoc, selectedTasiyiciColor);
                }

                if (durakIndex - 1 >= 0) {
                    drawArrow(duraklar.get(durakIndex - 1).durakLoc, durak.durakLoc, selectedTasiyiciColor);
                }

            }
            if (vardiyaIndex == 0) {
                if (vardiya.getDuraklar().size() > 0) {
                    drawArrow(duraklar.get(duraklar.size() - 1).durakLoc, vardiyaBitisLoc, selectedTasiyiciColor);
                } else if (vardiyaIndex + 1 < vardiyalar.size()){
                    drawArrow(vardiyaBaslangicLoc, vardiyaBitisLoc, selectedTasiyiciColor);
                }
            }
            else {
                if (duraklar.size() > 0) {
                    drawArrow(duraklar.get(duraklar.size() - 1).durakLoc, vardiyaBitisLoc, selectedTasiyiciColor);
                } else {
                    drawArrow(vardiyaBaslangicLoc, vardiyaBitisLoc, Color.GRAY);
                }
                drawArrowBetweenVardiyaEndToStart(vardiyalar.get(vardiyalar.size() - 2).vardiyaBitisLoc, vardiya.vardiyaBaslangicLoc);
            }
        }
    }

    private void drawArrowBetweenVardiyaEndToStart(Shape first, Shape second) {
        // This method is used because vardiyaBaslangic and vardiyaBitis share the same arrow thus, if the same arrow is
        // added there will be an exception.

        Arrow arrow = new Arrow();
        arrow.setStartX(first.getLayoutX());
        arrow.setStartY(first.getLayoutY());
        arrow.setEndX(second.getLayoutX());
        arrow.setEndY(second.getLayoutY());
        arrow.setStroke(Color.GRAY);
        mapPane.getChildren().remove(arrow);
        mapPane.getChildren().add(arrow);
        rotaLines.add(arrow);
    }

    private Arrow getArrowInstance(Shape s) {
        Arrow arrow = null;
        if (s.getUserData() instanceof Tasiyici) {
            arrow = ((Tasiyici) s.getUserData()).arrow;
        } else if (s.getUserData() instanceof Paket) {
            arrow = ((Paket) s.getUserData()).arrow;
        } else if (s.getUserData() instanceof Durak) {
            arrow = ((Durak) s.getUserData()).arrow;
        } else if (s.getUserData() instanceof Vardiya) {
            arrow = ((Vardiya) s.getUserData()).arrow;
        }
        return arrow;
    }

    private void highlightClickedDurakInListView(Durak durak) {
        if (isTasiyiciSelected) {
            for (DraggableListItem item: rotaInfoList) {
                if (item.getItemObject() instanceof Durak) {
                    if (item.getItemObject() == durak) {
                        rotaInfo.getSelectionModel().select(item);
                        rotaInfo.requestFocus();
                    }
                }
            }
        }
    }

    private void changeVardiyaAndDurakOrder() {
        ArrayList<Vardiya> vardiyalar = selectedTasiyici.getVardiyalar();
        boolean vardiyaBitis = true;
        int vardiyaIndex = 0;
        vardiyalar.get(vardiyaIndex).getDuraklar().clear();

        for (DraggableListItem item: rotaInfoList) {
            if (item.getItemObject() instanceof Durak) {
                if (vardiyaIndex < vardiyalar.size()) {
                    vardiyalar.get(vardiyaIndex).getDuraklar().add((Durak)item.getItemObject());
                }
            } else if (item.getItemObject() instanceof Vardiya) {
                vardiyaBitis = !vardiyaBitis;
                if (vardiyaBitis) {
                    vardiyaIndex++;
                    if (vardiyaIndex < vardiyalar.size()) {
                        vardiyalar.get(vardiyaIndex).getDuraklar().clear();
                    }
                }
            }
        }
    }

    private void changePaketLocClickedToDurakLocClicked(Durak[] duraklar) {
        duraklar[0].durakLoc.setOnMouseClicked(durakLocClicked);
        duraklar[1].durakLoc.setOnMouseClicked(durakLocClicked);
    }

    private void updateVarisSureleri() {
        double[] newTotalDistanceDuration = Calculator.calculateTasiyiciTotalDistanceDuration(selectedTasiyici);
        selectedTasiyici.setEkstraKm(newTotalDistanceDuration[0] - selectedTasiyici.getOriginalKm());
        ekstraKmLabel.setText(String.format("%.1f", newTotalDistanceDuration[0] - selectedTasiyici.getOriginalKm()));

        selectedTasiyici.setEkstraZaman((int) newTotalDistanceDuration[1] - selectedTasiyici.getOriginalZaman());
        ekstraZamanLabel.setText(Integer.toString((int) newTotalDistanceDuration[1] - selectedTasiyici.getOriginalZaman()));

        for (int i = 0; i < rotaInfoList.size(); i++) {
            DraggableListItem item = rotaInfoList.get(i);
            rotaInfoList.remove(item);
            addToListView(i, item.getItemObject(), rotaInfo);
        }
    }

    private int findDuraksFirstFeasibleIndex(Durak[] duraklar) {
        for (int i = 2; i < rotaInfoList.size(); i++) {
            addToListView(i, duraklar[0], rotaInfo);
            addToListView(i + 1, duraklar[1], rotaInfo);
            changeVardiyaAndDurakOrder();
            updateVarisSureleri();
            if (isRouteFeasible()) {
                rotaInfoList.remove(i);
                rotaInfoList.remove(i);
                return i;
            }
            rotaInfoList.remove(i);
            rotaInfoList.remove(i);
        }

        return rotaInfoList.size();
    }

    private boolean isRouteFeasible() {
        if (selectedTasiyici != null) {
            ArrayList<Vardiya> vardiyalar = selectedTasiyici.getVardiyalar();

            for (Vardiya vardiya: vardiyalar) {
                int vardiyaSuresi = vardiya.getSaatBitisInSeconds() - vardiya.getSaatBaslangicInSeconds();
                int durakToplamSure = vardiya.getVardiyaBitisVarisSuresi() * 60;

                for (Durak durak: vardiya.getDuraklar()) {
                    durakToplamSure += durak.getVarisSuresi() * 60;
                }

                if (vardiyaSuresi < durakToplamSure) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isShapeNotSelectedBefore(Shape s) {
        for (Shape shape: selectedShapes) {
            if (shape == s) {
                if (s.getUserData() instanceof Paket) {
                    Paket paket = (Paket) s.getUserData();
                    paket.arrow.setStroke(Color.GRAY);
                    paket.arrow.setArrowHeadStroke(Color.GRAY);
                    rotaInfoList.removeIf(item -> item.getItemObject() == paket);
                    selectedShapes.remove(s);
                }
                return false;
            }
        }
        return true;
    }

    private boolean isSourceSelectedBeforeDestination(int targetItemIndex) {
        Durak selectedDurak = (Durak) selectedListItem.getItemObject();
        int startIndex = targetItemIndex;
        int endIndex = rotaInfo.getSelectionModel().getSelectedIndex();

        if (startIndex > endIndex) {
            int temp = endIndex;
            endIndex = startIndex + 1;
            startIndex = temp + 1;
        }

        for (int i = startIndex; i < endIndex; i++) {
            DraggableListItem item = rotaInfoList.get(i);
            if (item.getItemObject() instanceof Durak) {
                Durak targetDurak = (Durak) item.getItemObject();
                for (Gonderi targetGonderi: targetDurak.getGonderiler()) {
                    for (Gonderi selectedGonderi: selectedDurak.getGonderiler()) {
                        if (targetGonderi.getRefGonderi() == selectedGonderi.getRefGonderi()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

//    private boolean isFileNotAddedBefore(File file) {
//        for (File addedFile: addedFiles) {
//            if (addedFile.equals(file)) {
//                return false;
//            }
//        }
//        return true;
//    }

    private void addDurakInfo() {
        try {
            rightVBox.getChildren().add(durakInfo);
        } catch (IllegalArgumentException e) {
            durakInfo.getItems().clear();
        }

        Durak selectedDurak = (Durak) selectedListItem.getItemObject();
        for (Gonderi gonderi: selectedDurak.getGonderiler()) {
            addToListView(-1, gonderi, durakInfo);
        }
    }

    private void removeDurakInfo() {
        rightVBox.getChildren().remove(durakInfo);
        durakInfo.getItems().clear();
    }

    private void addToTasiyiciInfo() {
        for (Tasiyici tasiyici: XMLParser.tasiyicilar) {
            addToListView(-1, tasiyici, tasiyiciInfo);
        }
    }

    private void addToPaketInfo() {
        for (Paket paket: XMLParser.paketler) {
            addToListView(-1, paket, paketInfo);
        }
    }

    private void addToListView(int index, Object o, ListView<DraggableListItem> listView) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
            StringBuilder listCellStringBuilder = new StringBuilder(o.getClass().getName().substring(5).toUpperCase());
            String ilceName;

            //☐ ⬛ ▽ ▼ ⬤ ✋
            if (o instanceof Tasiyici) {
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Tasiyici) o).getDurumY(), ((Tasiyici) o).getDurumX())[0]];
                listCellStringBuilder.append("\n").append(ilceName).append("\n");
            } else if (o instanceof Vardiya) {
                listCellStringBuilder.append("   ▼ ▽\n");
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Vardiya) o).getBaslangicY(), ((Vardiya) o).getBaslangicX())[0]];
                listCellStringBuilder.append(ilceName).append(" -> ");
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Vardiya) o).getBitisY(), ((Vardiya) o).getBitisX())[0]];
                listCellStringBuilder.append(ilceName).append("\n");
            } else if (o instanceof Durak) {
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Durak) o).getY(), ((Durak) o).getX())[0]];
                for (Durak[] duraklar: duraklarFromPaketler) {
                    if (duraklar[0] == o || duraklar[1] == o) {
                        listCellStringBuilder.append("  (Sonradan Eklendi ✋)");
                    }
                }

                listCellStringBuilder.append("\n").append(ilceName).append("\n");
            } else if (o instanceof  Paket) {
                listCellStringBuilder.append("\n");
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Paket) o).getGondericiY(), ((Paket) o).getGondericiX())[0]];
                listCellStringBuilder.append(ilceName).append(" -> ");
                ilceName = Calculator.ilceler[(int) Calculator.findClosestIlce(((Paket) o).getAliciY(), ((Paket) o).getAliciX())[0]];
                listCellStringBuilder.append(ilceName).append("\n");
            } else if (o instanceof Gonderi) {
                listCellStringBuilder.append("\n");
            }

            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                String propertyName = propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(o);

                if (propertyName.equals("refTasiyici"))
                    listCellStringBuilder.append("Tasiyici no: ").append(value).append("\n");
                else if (propertyName.equals("durakId")) {
                    if (value.toString().length() > 32)
                        listCellStringBuilder.append("Durak no: ").append(value.toString(), 0, 3).append("\n");
                    else
                        listCellStringBuilder.append("Durak no: ").append(value).append("\n");
                }
                else if (propertyName.equals("refGonderi"))
                    listCellStringBuilder.append("Paket no: ").append(value).append("\n");
                else if (propertyName.equals("teslimat") && Integer.parseInt(value.toString()) == 0)
                    listCellStringBuilder.append("Teslim ALINACAK" + "\n");
                else if (propertyName.equals("teslimat") && Integer.parseInt(value.toString()) == 1)
                    listCellStringBuilder.append("Teslim EDILECEK" + "\n");
                else if (propertyName.equals("x") || propertyName.equals("durumX"))
                    listCellStringBuilder.append("X: ").append(value).append(", ");
                else if (propertyName.equals("y") || propertyName.equals("durumY"))
                    listCellStringBuilder.append("Y: ").append(value).append("\n");
                else if (propertyName.equals("baslangicX"))
                    listCellStringBuilder.append("Baslangic - X:").append(value).append(", ");
                else if (propertyName.equals("baslangicY"))
                    listCellStringBuilder.append("Y: ").append(value).append("\n");
                else if (propertyName.equals("bitisX"))
                    listCellStringBuilder.append("Bitis - X: ").append(value).append(", ");
                else if (propertyName.equals("bitisY"))
                    listCellStringBuilder.append("Y: ").append(value).append("\n");
                else if (propertyName.equals("gondericiX"))
                    listCellStringBuilder.append("Gonderici - X: ").append(value).append(", ");
                else if (propertyName.equals("gondericiY"))
                    listCellStringBuilder.append("Y: ").append(value).append("\n");
                else if (propertyName.equals("aliciX"))
                    listCellStringBuilder.append("Alici - X: ").append(value).append(", ");
                else if (propertyName.equals("aliciY"))
                    listCellStringBuilder.append("Y: ").append(value).append("\n");
                else if (propertyName.equals("varisSuresi"))
                    listCellStringBuilder.append("Varis Suresi: ").append(value).append(" dakika\n");
                else if (propertyName.equals("saatBaslangicAsString"))
                    listCellStringBuilder.append("Zaman Araligi: ").append(value).append(" - ");
                else if (propertyName.equals("saatBitisAsString"))
                    listCellStringBuilder.append(value).append("\n");
            }
            listCellStringBuilder = new StringBuilder(listCellStringBuilder.toString());

            if (index < 0) {
                listView.getItems().add(new DraggableListItem(o, listCellStringBuilder.toString()));
            } else {
                listView.getItems().add(index, new DraggableListItem(o, listCellStringBuilder.toString()));
            }

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void returnToDefaultState() {
        isTasiyiciSelected = false;
        selectedTasiyici = null;
        vardiyaKaydet.setDisable(true);
        rotaOlanakLabel.setVisible(false);
        digerTasiyicilariGosterGizle.setDisable(true);
        selectedShapes.clear();
        rotaInfo.getItems().clear();

        clearEkstraLabels();
        removeLocationMark();
        removeDurakInfo();

        digerTasiyicilariGosterGizle.setText("Diger Tasiyicilari Gizle");
        digerTasiyicilariGosterGizle.setOnAction(removeOtherTasiyicilar);

        enableAllOtherTasiyicilar();
        markAllOtherVardiyalar();
        enableAllEmptyPackets();
        removeRotaArrows();
        addedShapes.clear();
        addedLabels.clear();
//        removeAddedShapes();
//        removeAddedLabels();
    }

    @FXML
    public void mapClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            returnToDefaultState();
        }
    }

    @FXML
    public void addTasiyiciXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Tasiyici XML");
        File file = fileChooser.showOpenDialog(wrapperLayout.getScene().getWindow());
        if (file != null) {
            removeOldTasiyiciMarks();
            tasiyiciInfo.getItems().clear();
            XMLParser.parseTasiyicilar(file);
            markTasiyicilar();
            markAllOtherVardiyalar();
//            addedFiles.add(file);
            butunRotalariGosterGizle.setDisable(false);
            tasiyiciInfo.getItems().clear();
            addToTasiyiciInfo();
        }
//        if (file != null && isFileNotAddedBefore(file)) {
//            XMLParser.parseTasiyicilar(file);
//            markTasiyicilar();
//            markAllOtherVardiyalar();
//            addedFiles.add(file);
//            butunRotalariGosterGizle.setDisable(false);
//        }
    }

    @FXML
    public void addBekleyenPaketXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Bekleyen Paket XML");
        File file = fileChooser.showOpenDialog(wrapperLayout.getScene().getWindow());
        if (file != null) {
            removeOldPaketMarks();
            paketInfo.getItems().clear();
            XMLParser.parsePaketler(file);
            markBekleyenPaketler();
//            addedFiles.add(file);
            bosPaketGosterGizle.setDisable(false);
            bosPaketGosterGizle.setText("Bos Paketleri Gizle");
            bosPaketGosterGizle.setOnAction(hideEmptyPakets);
            paketInfo.getItems().clear();
            addToPaketInfo();
        }
//        if (file != null && isFileNotAddedBefore(file)) {
//            XMLParser.parsePaketler(file);
//            markBekleyenPaketler();
//            addedFiles.add(file);
//            bosPaketGosterGizle.setDisable(false);
//            bosPaketGosterGizle.setText("Bos Paketleri Gizle");
//            bosPaketGosterGizle.setOnAction(hideEmptyPakets);
//        }
    }

    @FXML
    public void saveNewVardiya() {
        if (isTasiyiciSelected) {
            if (rotaOlanakLabel.getText().contains("Uygulanamaz")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Dikkat!");
                alert.setTitle("Rota Değiştirildi!");
                alert.setContentText("Taşıyıcı rotayı zamanında bitiremeyebilir.\nYine de kaydetmek istiyor musunuz?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                        XMLFormatter.saveNewTasiyiciRoute(selectedTasiyici, wrapperLayout.getScene().getWindow());
                    }
                });
            } else {
                XMLFormatter.saveNewTasiyiciRoute(selectedTasiyici, wrapperLayout.getScene().getWindow());
            }
        }
    }

    private class DraggableListCell extends ListCell<DraggableListItem> {
        public DraggableListCell() {
            ListCell thisCell = this;
            selectedListItem = null;
            setStyle("-fx-border-width: 0.5; -fx-border-color: lightgray; -fx-padding: 5; -fx-highlight-fill: #0096C9;");

            setContentDisplay(ContentDisplay.CENTER);
            setAlignment(Pos.CENTER_LEFT);

            setOnDragDetected(event -> {
                removeLocationMark();
                if (
                        getItem() == null
                        || event.getButton() == MouseButton.SECONDARY
                        || getItem().getItemObject() instanceof Tasiyici
                        || getItem().getItemObject() instanceof Vardiya
                        || getItem().getItemObject() instanceof Paket)
                {
                    return;
                }

                if (selectedListItem == null) {
                    selectedListItem = rotaInfo.getSelectionModel().getSelectedItem();
                }

                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().getItemString());
                dragboard.setContent(content);

                event.consume();
            });

            setOnDragOver(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            });

            setOnDragEntered(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(0.3);
                }
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(1);
                }
            });

            setOnDragDropped(event -> {
                if (getItem() == null) {
                    return;
                }

                removeLocationMark();
                boolean success = false;

                if (getItem().getItemString() != null && getIndex() != 0 && getIndex() != 1) {
                    ObservableList<DraggableListItem> items = getListView().getItems();
                    int targetItemIndex = items.indexOf(getItem());

                    if (isSourceSelectedBeforeDestination(targetItemIndex)) {
                        vardiyaKaydet.setDisable(false);
                        rotaInfoList.remove(selectedListItem);
                        rotaInfoList.add(targetItemIndex, selectedListItem);

                        drawNewRouteArrows();

                        rotaInfo.getSelectionModel().select(rotaInfoList.indexOf(selectedListItem));
                        selectedListItem = null;
                        success = true;
                    } else {
                        selectedListItem = null;
                    }
                }

                event.setDropCompleted(success);

                event.consume();
            });

            setOnMouseClicked(event -> {
                if (getItem() == null) {
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY) {
                    Object selectedObject = getItem().getItemObject();

                    if (getItem() != selectedListItem) {
                        int xOffset = 30;
                        int yOffset = 60;

                        if (selectedObject instanceof Tasiyici) {
                            removeDurakInfo();
                            locationMark.setLayoutX(((Tasiyici) selectedObject).tasiyiciLoc.getLayoutX() - xOffset);
                            locationMark.setLayoutY(((Tasiyici) selectedObject).tasiyiciLoc.getLayoutY() - yOffset);

                            locationMark.setVisible(true);
                            locationMark1.setVisible(false);
                        } else if (selectedObject instanceof Vardiya) {
                            removeDurakInfo();
                            locationMark.setLayoutX(((Vardiya) selectedObject).vardiyaBaslangicLoc.getLayoutX() - xOffset);
                            locationMark.setLayoutY(((Vardiya) selectedObject).vardiyaBaslangicLoc.getLayoutY() - yOffset);

                            locationMark1.setLayoutX(((Vardiya) selectedObject).vardiyaBitisLoc.getLayoutX() - xOffset);
                            locationMark1.setLayoutY(((Vardiya) selectedObject).vardiyaBitisLoc.getLayoutY() - yOffset);

                            locationMark.setVisible(true);
                            locationMark1.setVisible(true);
                        } else if (selectedObject instanceof Durak) {
                            selectedListItem = getItem();
                            addDurakInfo();
                            locationMark.setLayoutX(((Durak) selectedObject).durakLoc.getLayoutX() - xOffset);
                            locationMark.setLayoutY(((Durak) selectedObject).durakLoc.getLayoutY() - yOffset);

                            locationMark.setVisible(true);
                            locationMark1.setVisible(false);
                        } else if (selectedObject instanceof Paket) {
                            removeDurakInfo();
                            locationMark.setLayoutX(((Paket) selectedObject).gondericiLoc.getLayoutX() - xOffset);
                            locationMark.setLayoutY(((Paket) selectedObject).gondericiLoc.getLayoutY() - yOffset);

                            locationMark1.setLayoutX(((Paket) selectedObject).aliciLoc.getLayoutX() - xOffset);
                            locationMark1.setLayoutY(((Paket) selectedObject).aliciLoc.getLayoutY() - yOffset);

                            locationMark.setVisible(true);
                            locationMark1.setVisible(true);
                        }

                        selectedListItem = getItem();
                    } else {
                        if (getItem().getItemObject() instanceof Durak) {
                            removeDurakInfo();
                        }

                        removeLocationMark();
                        getListView().getSelectionModel().clearSelection();
                        selectedListItem = null;
                    }
                }
            });

            setOnMouseDragged(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    selectedListItem = null;
                }
            });

            setOnContextMenuRequested(event -> {
                if (getItem() == null || getItem().getItemObject() instanceof Vardiya) {
                    return;
                } else if (getItem().getItemObject() instanceof Tasiyici) {
                    selectTasiyici((Tasiyici) getItem().getItemObject());
                    return;
                }

                if (getItem().getItemObject() instanceof Paket) {
                    if (isTasiyiciSelected) {
                        vardiyaKaydet.setDisable(false);
                        convertPaketToDuraklar((Paket) getItem().getItemObject());
                        drawNewRouteArrows();
                        return;
                    }
                }

                boolean durakWasAPaket = false;
                Durak[] oldPaket = null;

                if (selectedListItem != null && getItem() == selectedListItem) {
                    for (Durak[] durak: duraklarFromPaketler) {
                        if (selectedListItem.getItemObject() instanceof Durak) {
                            Durak selectedDurak = (Durak) selectedListItem.getItemObject();
                            if (durak[0].getGonderiler().get(0).getRefGonderi() == selectedDurak.getGonderiler().get(0).getRefGonderi() && selectedListItem.getItemString().contains("Sonradan Eklendi")) {
                                oldPaket = durak;
                                durakWasAPaket = true;
                                break;
                            }
                        }
                    }
                } else if (getItem() != selectedListItem) {
                    selectedListItem = null;
                }

                if (durakWasAPaket) {
                    removeLocationMark();
                    removeDurakInfo();
                    convertDuraklarToPaket(oldPaket);
                    selectedListItem = null;
                    getListView().getSelectionModel().clearSelection();
                }
            });
        }

        @Override
        protected void updateItem(DraggableListItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Label header = new Label("empty");

                if (item.getItemObject() instanceof Tasiyici) {
                    header.setText("TASIYICI");
                }
                else if (item.getItemObject() instanceof Durak) {
                    header.setText("DURAK");
                    header.setTextFill(Color.GREEN);
                } else if (item.getItemObject() instanceof Vardiya) {
                    header.setText("VARDIYA");
                    header.setTextFill(Color.SIENNA);
                } else if (item.getItemObject() instanceof Paket) {
                    header.setText("PAKET");
                } else if (item.getItemObject() instanceof Gonderi) {
                    header.setText("GONDERI");
                }

                Pane pane = new Pane();
                Label label = new Label(getListView().getItems().get(getListView().getItems().indexOf(item)).getItemString());
                header.setScaleX(1.5);
                header.setScaleY(1.5);
                header.setTranslateX(170);
                pane.getChildren().addAll(label, header);
                setGraphic(pane);
            }
        }
    }
}