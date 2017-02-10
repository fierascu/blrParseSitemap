package blr;


import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadSitemap {

    static Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
    }

    public static List getLinksFromSitemap(String sitemapFile) {
        try {
            File file = new File(sitemapFile);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

            NodeList nodeLinks = doc.getElementsByTagName("loc");
            List links = new ArrayList<>();

            for (int i = 0; i < nodeLinks.getLength(); i++) {
                String usr = nodeLinks.item(i).getTextContent();
                //log.trace(i + " " + usr);
                if (Character.isDigit(usr.charAt(usr.length() - 1))) {
                    links.add(usr);
                }
            }
            log.trace("Hei, just read " + links.size() + " possible products links!");
            return links;
        } catch (Exception e) {
            log.error(e);
        }
        log.trace("NO LINKS READ!");
        return new ArrayList<>();
    }
}
