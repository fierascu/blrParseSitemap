package blr;


import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReadSitemap {

    private static Logger log = Logger.getLogger(ReadSitemap.class.getName());

    public static void main(String[] args) {
    }

    public static List getLinksFromSitemap(String sitemapFile) {
        List<String> links = new ArrayList<>();
        if (sitemapFile.endsWith(".txt")) {
            try (Stream<String> stream = Files.lines(Paths.get(sitemapFile))) {
                stream
                        .filter(f -> f.length() > 0 && Character.isDigit(f.charAt(f.length() - 1)))
                        .forEach(links::add);
            } catch (IOException e) {
                log.error(e);
            }
            log.trace("Hei, just read " + links.size() + " possible products links from " + sitemapFile);
            return links;
        } else {
            try {
                File file = new File(sitemapFile);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                NodeList nodeLinks = doc.getElementsByTagName("loc");

                for (int i = 0; i < nodeLinks.getLength(); i++) {
                    String usr = nodeLinks.item(i).getTextContent();
                    if (usr.length() > 0 && Character.isDigit(usr.charAt(usr.length() - 1))) {
                        links.add(usr);
                    }
                }
                log.trace("Hei, just read " + links.size() + " possible products links from " + sitemapFile);
                return links;
            } catch (Exception e) {
                log.error(e);
            }
        }
        log.trace("NO LINKS READ!");
        return links;
    }
}
