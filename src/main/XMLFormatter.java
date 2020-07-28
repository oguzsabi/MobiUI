package main;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class XMLFormatter {
    static File dest;
    public static void saveNewTasiyiciRoute(Tasiyici selectedTasiyici, Window window) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            Element tasiyiciElement = doc.createElement("tasiyici");
            rootElement.appendChild(tasiyiciElement);

            Attr attr = doc.createAttribute("refTasiyici");
            attr.setValue(Integer.toString(selectedTasiyici.getRefTasiyici()));
            tasiyiciElement.setAttributeNode(attr);

            attr = doc.createAttribute("DurumX");
            attr.setValue(Double.toString(selectedTasiyici.getDurumX()));
            tasiyiciElement.setAttributeNode(attr);

            attr = doc.createAttribute("DurumY");
            attr.setValue(Double.toString(selectedTasiyici.getDurumY()));
            tasiyiciElement.setAttributeNode(attr);

            ArrayList<Vardiya> vardiyalar = selectedTasiyici.getVardiyalar();

            for (Vardiya vardiya: vardiyalar) {
                Element vardiyaElement = doc.createElement("vardiya");
                tasiyiciElement.appendChild(vardiyaElement);

                attr = doc.createAttribute("saatBaslangic");
                attr.setValue(vardiya.getSaatBaslangicAsString());
                vardiyaElement.setAttributeNode(attr);

                attr = doc.createAttribute("saatBitis");
                attr.setValue(vardiya.getSaatBitisAsString());
                vardiyaElement.setAttributeNode(attr);

                attr = doc.createAttribute("BaslangicX");
                attr.setValue(Double.toString(vardiya.getBaslangicX()));
                vardiyaElement.setAttributeNode(attr);

                attr = doc.createAttribute("BaslangicY");
                attr.setValue(Double.toString(vardiya.getBaslangicY()));
                vardiyaElement.setAttributeNode(attr);

                attr = doc.createAttribute("BitisX");
                attr.setValue(Double.toString(vardiya.getBitisX()));
                vardiyaElement.setAttributeNode(attr);

                attr = doc.createAttribute("BitisY");
                attr.setValue(Double.toString(vardiya.getBitisY()));
                vardiyaElement.setAttributeNode(attr);

                Element durakElement;

                for (Durak durak: vardiya.getDuraklar()) {
                    durakElement = doc.createElement("durak");
                    vardiyaElement.appendChild(durakElement);

                    attr = doc.createAttribute("refGonderi");
                    attr.setValue(Integer.toString(durak.getRefGonderi()));
                    durakElement.setAttributeNode(attr);

                    attr = doc.createAttribute("X");
                    attr.setValue(Double.toString(durak.getX()));
                    durakElement.setAttributeNode(attr);

                    attr = doc.createAttribute("Y");
                    attr.setValue(Double.toString(durak.getY()));
                    durakElement.setAttributeNode(attr);

                    attr = doc.createAttribute("varisSuresi");
                    attr.setValue(Integer.toString(durak.getVarisSuresi()));
                    durakElement.setAttributeNode(attr);

                    attr = doc.createAttribute("Ucret");
                    attr.setValue(Double.toString(durak.getUcret()));
                    durakElement.setAttributeNode(attr);

                    attr = doc.createAttribute("Teslimat");
                    attr.setValue(Integer.toString(durak.getTeslimat()));
                    durakElement.setAttributeNode(attr);


                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            File file = new File("yeniVardiya.xml");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().add(extFilterXML);
            dest = fileChooser.showSaveDialog(window);
            if (dest != null) {
                try {
                    Files.move(file.toPath(), dest.toPath(), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            saveNewBekleyenPaketler();
//
//            // write the content into xml file
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//            StreamResult result = new StreamResult(new File("C:\\cars.xml"));
//            transformer.transform(source, result);
//
//            // Output to console for testing
////            StreamResult consoleResult = new StreamResult(System.out);
////            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveNewBekleyenPaketler() {
        try {
           if (dest != null) {
               DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
               DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
               Document doc = dBuilder.newDocument();

               // root element
               Element rootElement = doc.createElement("root");
               doc.appendChild(rootElement);

               for (Paket paket: XMLParser.paketler) {
                   Element tasiyiciElement = doc.createElement("gonderi");
                   rootElement.appendChild(tasiyiciElement);

                   Attr attr = doc.createAttribute("refGonderi");
                   attr.setValue(Integer.toString(paket.getRefGonderi()));
                   tasiyiciElement.setAttributeNode(attr);

                   attr = doc.createAttribute("GondericiX");
                   attr.setValue(Double.toString(paket.getGondericiX()));
                   tasiyiciElement.setAttributeNode(attr);

                   attr = doc.createAttribute("GondericiY");
                   attr.setValue(Double.toString(paket.getGondericiY()));
                   tasiyiciElement.setAttributeNode(attr);

                   attr = doc.createAttribute("AliciX");
                   attr.setValue(Double.toString(paket.getAliciX()));
                   tasiyiciElement.setAttributeNode(attr);

                   attr = doc.createAttribute("AliciY");
                   attr.setValue(Double.toString(paket.getAliciY()));
                   tasiyiciElement.setAttributeNode(attr);

                   attr = doc.createAttribute("Ucret");
                   attr.setValue(Double.toString(paket.getUcret()));
                   tasiyiciElement.setAttributeNode(attr);
               }

               TransformerFactory transformerFactory = TransformerFactory.newInstance();
               Transformer transformer = transformerFactory.newTransformer();
               transformer.setOutputProperty(OutputKeys.INDENT, "yes");
               DOMSource source = new DOMSource(doc);
               File file = new File(dest.toPath().getParent() + "\\atama_bekleyen_gonderiler_yeni.xml");
               StreamResult result = new StreamResult(file);
               transformer.transform(source, result);
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
