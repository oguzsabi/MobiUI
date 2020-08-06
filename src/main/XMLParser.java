package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class XMLParser {
    public static ArrayList<Paket> paketler = new ArrayList<>();
    public static ArrayList<Tasiyici> tasiyicilar = new ArrayList<>();

    public static void parsePaketler(File file, String gonderiler) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            if (file == null) {
                InputSource inputSource = new InputSource(new StringReader(gonderiler));
                document = builder.parse(inputSource);
            } else {
                document = builder.parse(file);
            }
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("gonderi");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int refGonderi = Integer.parseInt(element.getAttribute("refGonderi"));
//                    double gondericiX = Double.parseDouble(element.getAttribute("gondericiX"));
//                    double gondericiY = Double.parseDouble(element.getAttribute("gondericiY"));
//                    double aliciX = Double.parseDouble(element.getAttribute("aliciX"));
//                    double aliciY = Double.parseDouble(element.getAttribute("aliciY"));
                    double gondericiX = Double.parseDouble(element.getAttribute("gondericiY"));
                    double gondericiY = Double.parseDouble(element.getAttribute("gondericiX"));
                    double aliciX = Double.parseDouble(element.getAttribute("aliciY"));
                    double aliciY = Double.parseDouble(element.getAttribute("aliciX"));
                    double ucret = Double.parseDouble(element.getAttribute("ucret"));
                    int durum = Integer.parseInt(element.getAttribute("durum"));

                    paketler.add(new Paket(refGonderi, gondericiX, gondericiY, aliciX, aliciY, ucret, durum));
                }
            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static void parseTasiyicilar(File file, String tasiyici) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            if (file == null) {
                InputSource inputSource = new InputSource(new StringReader(tasiyici));
                document = builder.parse(inputSource);
            } else {
                document = builder.parse(file);
            }
            document.getDocumentElement().normalize();

            NodeList tasiyiciList = document.getElementsByTagName("tasiyici");

            for (int tasiyiciIndex = 0; tasiyiciIndex < tasiyiciList.getLength(); tasiyiciIndex++) {
                Node tasiyiciNode = tasiyiciList.item(tasiyiciIndex);

                if (tasiyiciNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element tasiyiciElement = (Element) tasiyiciNode;
                    NodeList vardiyaList = tasiyiciElement.getElementsByTagName("vardiya");
                    ArrayList<Vardiya> vardiyalarTemp = new ArrayList<>();

                    for (int vardiyaIndex = 0; vardiyaIndex < vardiyaList.getLength(); vardiyaIndex++) {
                        Node vardiyaNode = vardiyaList.item(vardiyaIndex);

                        if (vardiyaNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element vardiyaElement = (Element) vardiyaNode;
                            NodeList durakList = vardiyaElement.getElementsByTagName("durak");
                            ArrayList<Durak> duraklarTemp = new ArrayList<>();

                            for (int durakIndex = 0; durakIndex < durakList.getLength(); durakIndex++) {
                                Node durakNode = durakList.item(durakIndex);

                                if (durakNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element durakElement = (Element) durakNode;
                                    NodeList gonderiList = durakElement.getElementsByTagName("gonderi");
                                    ArrayList<Gonderi> gonderilerTemp = new ArrayList<>();

                                    for (int gonderiIndex = 0; gonderiIndex < gonderiList.getLength(); gonderiIndex++) {
                                        Node gonderiNode = gonderiList.item(gonderiIndex);

                                        if (gonderiNode.getNodeType() == Node.ELEMENT_NODE) {
                                            Element gonderiElement = (Element) gonderiNode;

                                            int refGonderi = Integer.parseInt(gonderiElement.getAttribute("refGonderi"));
                                            double ucret = Double.parseDouble(gonderiElement.getAttribute("ucret"));
                                            int teslimat = Integer.parseInt(gonderiElement.getAttribute("teslimat"));

                                            gonderilerTemp.add(new Gonderi(refGonderi, ucret, teslimat));
                                        }
                                    }

                                    String durakId = durakElement.getAttribute("durakid");
                                    int gonderiSayisi = Integer.parseInt(durakElement.getAttribute("gonderiSayisi"));
//                                    double X = Double.parseDouble(durakElement.getAttribute("X"));
//                                    double Y = Double.parseDouble(durakElement.getAttribute("Y"));
                                    double X = Double.parseDouble(durakElement.getAttribute("Y"));
                                    double Y = Double.parseDouble(durakElement.getAttribute("X"));
                                    int varisSuresi = Integer.parseInt(durakElement.getAttribute("varisSuresi"));

                                    duraklarTemp.add(new Durak(durakId, gonderiSayisi, X, Y, varisSuresi, gonderilerTemp));
                                }
                            }

                            int vardiyaId = Integer.parseInt(vardiyaElement.getAttribute("vardiyaid"));
                            String rotaId = vardiyaElement.getAttribute("rotaid");
                            int durakSayisi = Integer.parseInt(vardiyaElement.getAttribute("durakSayisi"));
                            String saatBaslangic = vardiyaElement.getAttribute("saatBaslangic");
                            String saatBitis = vardiyaElement.getAttribute("saatBitis");
//                            double baslangicX = Double.parseDouble(vardiyaElement.getAttribute("baslangicX"));
//                            double baslangicY = Double.parseDouble(vardiyaElement.getAttribute("baslangicY"));
//                            double bitisX = Double.parseDouble(vardiyaElement.getAttribute("bitisX"));
//                            double bitisY = Double.parseDouble(vardiyaElement.getAttribute("bitisY"));
                            double baslangicX = Double.parseDouble(vardiyaElement.getAttribute("baslangicY"));
                            double baslangicY = Double.parseDouble(vardiyaElement.getAttribute("baslangicX"));
                            double bitisX = Double.parseDouble(vardiyaElement.getAttribute("bitisY"));
                            double bitisY = Double.parseDouble(vardiyaElement.getAttribute("bitisX"));

                            vardiyalarTemp.add(new Vardiya(
                                    vardiyaId,
                                    rotaId,
                                    durakSayisi,
                                    saatBaslangic,
                                    saatBitis,
                                    baslangicX,
                                    baslangicY,
                                    bitisX,
                                    bitisY,
                                    duraklarTemp
                            ));
                        }
                    }

                    double durumX;
                    double durumY;
                    double kmBeklenti;
                    int dkBeklenti;
                    int refTasiyici = Integer.parseInt(tasiyiciElement.getAttribute("refTasiyici"));
                    try {
                        durumX = Double.parseDouble(tasiyiciElement.getAttribute("durumX"));
                        durumY = Double.parseDouble(tasiyiciElement.getAttribute("durumY"));
                        kmBeklenti = Double.parseDouble(tasiyiciElement.getAttribute("beklentiKM"));
                        dkBeklenti = Integer.parseInt(tasiyiciElement.getAttribute("beklentiDK"));
                    } catch (NumberFormatException e) {
                        durumX = ThreadLocalRandom.current().nextDouble(26.894599, 27.280999);
                        durumY = ThreadLocalRandom.current().nextDouble(38.307100, 38.618600);
                        kmBeklenti = 10;
                        dkBeklenti = 10;
                    }
                    String durumSaat = tasiyiciElement.getAttribute("durumSaat");

                    int ratingSayisi = Integer.parseInt(tasiyiciElement.getAttribute("ratingSayisi"));
                    int ratingToplam = Integer.parseInt(tasiyiciElement.getAttribute("ratingToplam"));
                    int vardiyaSayisi = Integer.parseInt(tasiyiciElement.getAttribute("vardiyaSayisi"));

                    tasiyicilar.add(new Tasiyici(refTasiyici, durumX, durumY, durumSaat, kmBeklenti, dkBeklenti, ratingSayisi, ratingToplam, vardiyaSayisi, vardiyalarTemp));
                }
            }


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseFromAPI(boolean fromProduction) {
        URL url;
        try {
            if (fromProduction) {
                url = new URL("https://auto.mobikargo.com/MobiKargo/OptimizasyonGetir?ApiKey=353CC7DBD83745FEB9EFC27D978AA20253608966614546B48440F712A2B69F89");
            } else {
                url = new URL("https://webapi.mobikargo.com/MobiKargo/OptimizasyonGetir?ApiKey=353CC7DBD83745FEB9EFC27D978AA20253608966614546B48440F712A2B69F89");
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String result = "";
            StringBuilder stringBuilder = new StringBuilder(result);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);
            in.close();

            result = stringBuilder.toString();
            String tasiyici = result.split("\"Tasiyicilar\":")[1];
            String gonderiler = tasiyici.split(",")[1];

            gonderiler = gonderiler.substring(14, gonderiler.length() - 3);
            gonderiler = gonderiler.replace("\\u003c", "<");
            gonderiler = gonderiler.replace("\\u003e", ">");
            gonderiler = gonderiler.replace("\\", "");

            tasiyici = tasiyici.split(",")[0];
            tasiyici = tasiyici.replace("\\u003c", "<");
            tasiyici = tasiyici.replace("\\u003e", ">");
            tasiyici = tasiyici.replace("\\", "");
            tasiyici = tasiyici.substring(1, tasiyici.length() - 1);

            parseTasiyicilar(null, tasiyici);
            parsePaketler(null, gonderiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
