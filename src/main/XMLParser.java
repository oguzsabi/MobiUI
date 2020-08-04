package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XMLParser {
    public static ArrayList<Paket> paketler = new ArrayList<>();
    public static ArrayList<Tasiyici> tasiyicilar = new ArrayList<>();

    public static void parsePaketler(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("gonderi");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int refGonderi = Integer.parseInt(element.getAttribute("refGonderi"));
                    double gondericiX = Double.parseDouble(element.getAttribute("GondericiX"));
                    double gondericiY = Double.parseDouble(element.getAttribute("GondericiY"));
                    double aliciX = Double.parseDouble(element.getAttribute("AliciX"));
                    double aliciY = Double.parseDouble(element.getAttribute("AliciY"));
                    double ucret = Double.parseDouble(element.getAttribute("Ucret"));
                    int durum = Integer.parseInt(element.getAttribute("Durum"));

                    paketler.add(new Paket(refGonderi, gondericiX, gondericiY, aliciX, aliciY, ucret, durum));
                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseTasiyicilar(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
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
                                            double ucret = Double.parseDouble(gonderiElement.getAttribute("Ucret"));
                                            int teslimat = Integer.parseInt(gonderiElement.getAttribute("Teslimat"));

                                            gonderilerTemp.add(new Gonderi(refGonderi, ucret, teslimat));
                                        }
                                    }

                                    String durakId = durakElement.getAttribute("durakid");
                                    int gonderiSayisi = Integer.parseInt(durakElement.getAttribute("gonderiSayisi"));
                                    double X = Double.parseDouble(durakElement.getAttribute("X"));
                                    double Y = Double.parseDouble(durakElement.getAttribute("Y"));
                                    int varisSuresi = Integer.parseInt(durakElement.getAttribute("varisSuresi"));

                                    duraklarTemp.add(new Durak(durakId, gonderiSayisi, X, Y, varisSuresi, gonderilerTemp));
                                }
                            }

                            int vardiyaId = Integer.parseInt(vardiyaElement.getAttribute("vardiyaid"));
                            String rotaId = vardiyaElement.getAttribute("rotaid");
                            int durakSayisi = Integer.parseInt(vardiyaElement.getAttribute("durakSayisi"));
                            String saatBaslangic = vardiyaElement.getAttribute("saatBaslangic");
                            String saatBitis = vardiyaElement.getAttribute("saatBitis");
                            double baslangicX = Double.parseDouble(vardiyaElement.getAttribute("BaslangicX"));
                            double baslangicY = Double.parseDouble(vardiyaElement.getAttribute("BaslangicY"));
                            double bitisX = Double.parseDouble(vardiyaElement.getAttribute("BitisX"));
                            double bitisY = Double.parseDouble(vardiyaElement.getAttribute("BitisY"));

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

                    int refTasiyici = Integer.parseInt(tasiyiciElement.getAttribute("refTasiyici"));
                    double durumX = Double.parseDouble(tasiyiciElement.getAttribute("DurumX"));
                    double durumY = Double.parseDouble(tasiyiciElement.getAttribute("DurumY"));
                    String durumSaat = tasiyiciElement.getAttribute("DurumSaat");
                    double kmBeklenti = Double.parseDouble(tasiyiciElement.getAttribute("kmBeklenti"));
                    int dkBeklenti = Integer.parseInt(tasiyiciElement.getAttribute("dkBeklenti"));
                    int ratingSayisi = Integer.parseInt(tasiyiciElement.getAttribute("ratingSayisi"));
                    int ratingToplam = Integer.parseInt(tasiyiciElement.getAttribute("ratingToplam"));
                    int vardiyaSayisi = Integer.parseInt(tasiyiciElement.getAttribute("vardiyaSayisi"));

                    tasiyicilar.add(new Tasiyici(refTasiyici, durumX, durumY, durumSaat, kmBeklenti, dkBeklenti, ratingSayisi, ratingToplam, vardiyaSayisi, vardiyalarTemp));
                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
