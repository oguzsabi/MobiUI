package main;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class Calculator {
    private final static int R = 6371; // Radius of the earth
    private final static double speed = 60; // 60 km/h 1000 m/min
    private final static int dropPickUpTime = 5;
    public final static String[] ilceler = {
            "ALİAĞA",
            "BALÇOVA",
            "BAYINDIR",
            "BAYRAKLI",
            "BERGAMA",
            "BEYDAĞ",
            "BORNOVA",
            "BUCA",
            "ÇEŞME",
            "ÇİĞLİ",
            "DİKİLİ",
            "FOÇA",
            "GAZİEMİR",
            "GÜZELBAHÇE",
            "KARABAĞLAR",
            "KARABURUN",
            "KARŞIYAKA",
            "KEMALPAŞA",
            "KINIK",
            "KİRAZ",
            "KONAK",
            "MENDERES",
            "MENEMEN",
            "NARLIDERE",
            "ÖDEMİŞ",
            "SEFERİHİSAR",
            "SELÇUK",
            "TİRE",
            "TORBALI",
            "URLA",
            };

    private static double[] calculateDistanceDuration(Tasiyici tasiyici, Vardiya vardiya) {
        double[] distanceDuration = {0, 0};

        double tasiyiciLat = tasiyici.getDurumY();
        double tasiyiciLon = tasiyici.getDurumX();

        double[] firstIlce = findClosestIlce(tasiyiciLat, tasiyiciLon);

        double firstIlceLat = firstIlce[1];
        double firstIlceLon = firstIlce[2];

        double vardiyaLat = vardiya.getBaslangicY();
        double vardiyaLon = vardiya.getBaslangicX();

        double[] secondIlce = findClosestIlce(vardiyaLat, vardiyaLon);

        double secondIlceLat = secondIlce[1];
        double secondIlceLon = secondIlce[2];

        calculate(distanceDuration, tasiyiciLat, tasiyiciLon, firstIlce, firstIlceLat, firstIlceLon, vardiyaLat, vardiyaLon, secondIlce, secondIlceLat, secondIlceLon);

        distanceDuration[0] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][0];
        distanceDuration[1] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][1];

        return distanceDuration;
    }

    private static double[] calculateDistanceDuration(Vardiya vardiya, Durak durak2) {
        double[] distanceDuration = {0, 0};

        double vardiyaLat = vardiya.getBaslangicY();
        double vardiyaLon = vardiya.getBaslangicX();

        double[] firstIlce = findClosestIlce(vardiyaLat, vardiyaLon);

        double firstIlceLat = firstIlce[1];
        double firstIlceLon = firstIlce[2];

        double durakLat = durak2.getY();
        double durakLon = durak2.getX();

        double[] secondIlce = findClosestIlce(durakLat, durakLon);

        double secondIlceLat = secondIlce[1];
        double secondIlceLon = secondIlce[2];

        calculate(distanceDuration, vardiyaLat, vardiyaLon, firstIlce, firstIlceLat, firstIlceLon, durakLat, durakLon, secondIlce, secondIlceLat, secondIlceLon);

        distanceDuration[0] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][0];
        distanceDuration[1] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][1];

        return distanceDuration;
    }

    private static double[] calculateDistanceDuration(Durak durak1, Durak durak2) {
        double[] distanceDuration = {0, 0};

        double firstDurakLat = durak1.getY();
        double firstDurakLon = durak1.getX();

        double[] firstIlce = findClosestIlce(firstDurakLat, firstDurakLon);

        double firstIlceLat = firstIlce[1];
        double firstIlceLon = firstIlce[2];

        double secondDurakLat = durak2.getY();
        double secondDurakLon = durak2.getX();

        double[] secondIlce = findClosestIlce(secondDurakLat, secondDurakLon);

        double secondIlceLat = secondIlce[1];
        double secondIlceLon = secondIlce[2];

        calculate(distanceDuration, firstDurakLat, firstDurakLon, firstIlce, firstIlceLat, firstIlceLon, secondDurakLat, secondDurakLon, secondIlce, secondIlceLat, secondIlceLon);

        distanceDuration[0] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][0];
        distanceDuration[1] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][1];

        distanceDuration[1] += dropPickUpTime;
        return distanceDuration;
    }

    private static double[] calculateDistanceDuration(Durak durak1, Vardiya vardiya) {
        double[] distanceDuration = {0, 0};

        double durakLat = durak1.getY();
        double durakLon = durak1.getX();

        double[] firstIlce = findClosestIlce(durakLat, durakLon);

        double firstIlceLat = firstIlce[1];
        double firstIlceLon = firstIlce[2];

        double vardiyaLat = vardiya.getBitisY();
        double vardiyaLon = vardiya.getBitisX();

        double[] secondIlce = findClosestIlce(vardiyaLat, vardiyaLon);

        double secondIlceLat = secondIlce[1];
        double secondIlceLon = secondIlce[2];

        calculate(distanceDuration, durakLat, durakLon, firstIlce, firstIlceLat, firstIlceLon, vardiyaLat, vardiyaLon, secondIlce, secondIlceLat, secondIlceLon);

        distanceDuration[0] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][0];
        distanceDuration[1] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][1];

        return distanceDuration;
    }

    private static double[] calculateDistanceDuration(Vardiya vardiya1, Vardiya vardiya2) {
        double[] distanceDuration = {0, 0};

        double firstVardiyaLat = vardiya1.getBitisY();
        double firstVardiyaLon = vardiya1.getBitisX();

        double[] firstIlce = findClosestIlce(firstVardiyaLat, firstVardiyaLon);

        double firstIlceLat = firstIlce[1];
        double firstIlceLon = firstIlce[2];

        double secondVardiyaLat = vardiya2.getBaslangicY();
        double secondVardiyaLon = vardiya2.getBaslangicX();

        double[] secondIlce = findClosestIlce(secondVardiyaLat, secondVardiyaLon);

        double secondIlceLat = secondIlce[1];
        double secondIlceLon = secondIlce[2];

        calculate(distanceDuration, firstVardiyaLat, firstVardiyaLon, firstIlce, firstIlceLat, firstIlceLon, secondVardiyaLat, secondVardiyaLon, secondIlce, secondIlceLat, secondIlceLon);

        distanceDuration[0] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][0];
        distanceDuration[1] += CSVReader.distanceDurationMatrix[(int)firstIlce[0]][(int)secondIlce[0]][1];

        return distanceDuration;
    }

    private static void calculate(double[] distanceDuration, double firstLat, double firstLon, double[] firstIlce, double firstIlceLat, double firstIlceLon, double secondLat, double secondLon, double[] secondIlce, double secondIlceLat, double secondIlceLon) {
        double distance;
        if (firstIlce[0] != secondIlce[0]) {
            distance = calculateDistance(firstLat, firstLon, firstIlceLat, firstIlceLon);
            distanceDuration[0] += distance;
            distanceDuration[1] += calculateDuration(distance);

            distance = calculateDistance(secondLat, secondLon, secondIlceLat, secondIlceLon);
            distanceDuration[0] += distance;
            distanceDuration[1] += calculateDuration(distance);
        } else {
            distance = calculateDistance(firstLat, firstLon, secondLat, secondLon);
            distanceDuration[0] += distance;
            distanceDuration[1] += calculateDuration(distance);
        }
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = toRad(lat2-lat1);
        double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    // This function can be inside XMLParser to reduce number of loops
    public static double[] calculateTasiyiciTotalDistanceDuration(Tasiyici tasiyici) {
        double[] totalDistanceDuration = {0, 0};
        double[] distanceDuration = calculateDistanceDuration(tasiyici, tasiyici.getVardiyalar().get(0));
        totalDistanceDuration[0] = distanceDuration[0];
        totalDistanceDuration[1] = distanceDuration[1];

        for (int vardiyaIndex = 0; vardiyaIndex < tasiyici.getVardiyalar().size(); vardiyaIndex++) {
            Vardiya vardiya = tasiyici.getVardiyalar().get(vardiyaIndex);
            int numberOfDuraks = vardiya.getDuraklar().size();
            if (numberOfDuraks > 0) {
                distanceDuration = calculateDistanceDuration(vardiya, vardiya.getDuraklar().get(0));
                vardiya.getDuraklar().get(0).setVarisSuresi((int)Math.round(distanceDuration[1]));
                totalDistanceDuration[0] += distanceDuration[0];
                totalDistanceDuration[1] += distanceDuration[1];
            }

            for (int durakIndex = 0; durakIndex < numberOfDuraks; durakIndex++) {
                if (durakIndex + 1 < numberOfDuraks) {
                    distanceDuration = calculateDistanceDuration(vardiya.getDuraklar().get(durakIndex), vardiya.getDuraklar().get(durakIndex + 1));
                    vardiya.getDuraklar().get(durakIndex + 1).setVarisSuresi((int)Math.round(distanceDuration[1]));
                    totalDistanceDuration[0] += distanceDuration[0];
                    totalDistanceDuration[1] += distanceDuration[1];
                }
            }

            if (numberOfDuraks > 0) {
                distanceDuration = calculateDistanceDuration(vardiya.getDuraklar().get(numberOfDuraks - 1), vardiya);
                totalDistanceDuration[0] += distanceDuration[0];
                totalDistanceDuration[1] += distanceDuration[1];
                vardiya.setVardiyaBitisVarisSuresi((int)Math.round(distanceDuration[1]));
            }
        }

        return totalDistanceDuration;
    }

    private static double calculateTasiyiciTotalDuration(ObservableList<DraggableListItem> observableList) {
        double totalDuration = 0;
        int listSize = observableList.size();
        Vardiya vardiya = null;

        for (int i = 0; i < listSize; i++) {
            DraggableListItem item = observableList.get(i);
            if (item.getItemObject() instanceof Tasiyici) {
                totalDuration += calculateDistanceDuration((Tasiyici) item.getItemObject(), (Vardiya) observableList.get(i + 1).getItemObject())[1];
            } else if (item.getItemObject() instanceof Vardiya) {
                if (i + 1 < listSize) {
                    if (observableList.get(i + 1).getItemObject() instanceof Vardiya) {
                        totalDuration += calculateDistanceDuration((Vardiya) item.getItemObject(), (Vardiya) observableList.get(i + 1).getItemObject())[1];
                    } else {
                        totalDuration += calculateDistanceDuration((Vardiya) item.getItemObject(), (Durak) observableList.get(i + 1).getItemObject())[1];
                    }
                }
                vardiya = (Vardiya) item.getItemObject();
            } else if (item.getItemObject() instanceof Durak) {
                if (i + 1 == listSize) {
                    totalDuration += calculateDistanceDuration((Durak) item.getItemObject(), vardiya)[1];
                } else if (i + 1 < listSize) {
                    if (observableList.get(i + 1).getItemObject() instanceof Vardiya) {
                        totalDuration += calculateDistanceDuration((Durak) item.getItemObject(), vardiya)[1];
                    } else {
                        totalDuration += calculateDistanceDuration((Durak) item.getItemObject(), (Durak) observableList.get(i + 1).getItemObject())[1];
                    }
                }
            }
        }
        return totalDuration;
    }

    public static int[] findDuraksSubOptimalIndex(ObservableList<DraggableListItem> observableList, Durak[] duraklar) {
        int firstDurakIndex = 2;
        int secondDurakIndex = 3;
        double bestDuration = 100000000;
        double currentDuration;
        DraggableListItem firstItem = null;
        DraggableListItem secondItem;

        for (int i = 2; i < observableList.size(); i++) {
            firstItem = new DraggableListItem(duraklar[0], "");
            observableList.add(i, firstItem);
            currentDuration = calculateTasiyiciTotalDuration(observableList);
            if (currentDuration < bestDuration) {
                bestDuration = currentDuration;
                firstDurakIndex = i;
            }
            observableList.remove(firstItem);
        }
        observableList.add(firstDurakIndex, firstItem);

        bestDuration = 100000000;
        for (int i = firstDurakIndex + 1; i < observableList.size(); i++) {
            secondItem = new DraggableListItem(duraklar[1], "");
            observableList.add(i, secondItem);
            currentDuration = calculateTasiyiciTotalDuration(observableList);
            if (currentDuration < bestDuration) {
                bestDuration = currentDuration;
                secondDurakIndex = i;
            }
            observableList.remove(secondItem);
        }
        observableList.remove(firstItem);

        if (secondDurakIndex == 3)
            secondDurakIndex = firstDurakIndex + 1;

        if (secondDurakIndex == observableList.size())
            secondDurakIndex++;

        return new int[]{firstDurakIndex, secondDurakIndex};
    }

    private static int calculateDuration(double distance) {
        return (int) Math.round((distance / speed) * 60);
    }

    public static double[] findClosestIlce(double lat, double lon) {
        int closestIlceIndex = 0;
        double closestDistance = calculateDistance(
                lat,
                lon,
                CSVReader.ilceCoordinates[closestIlceIndex][0],
                CSVReader.ilceCoordinates[closestIlceIndex][1]
        );

        for (int i = 1; i < CSVReader.ilceCoordinates.length; i++) {
            double currentDistance = calculateDistance(
                    lat,
                    lon,
                    CSVReader.ilceCoordinates[i][0],
                    CSVReader.ilceCoordinates[i][1]
            );

            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestIlceIndex = i;
            }
        }

        return new double[]
                {
                        closestIlceIndex,
                        CSVReader.ilceCoordinates[closestIlceIndex][0],
                        CSVReader.ilceCoordinates[closestIlceIndex][1]
                };
    }

    private static double toRad(double value) {
        return value * Math.PI / 180;
    }
}
