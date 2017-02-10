package blr;

import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static blr.ReadSitemap.getLinksFromSitemap;
import static blr.Utils.*;


public class ReadProduct {
    static Logger log = Logger.getLogger(App.class.getName());


    public static void main(String[] args) {
    }

    public static void crawlFromSitemap() {
        // links.stream().sorted().forEach(log::info);

        List<ProdPojo> prodList = new ArrayList<>();

        try (CSVWriter writer = new CSVWriter(new FileWriter(EXPORT_FILE), SEPARATOR_CHAR, QUOTE_CHAR)) {
            // feed in your array (or convert your data to an array)
            writer.writeNext(ProdPojo.getHeader().split(SEPARATOR));
        } catch (Exception e) {
            log.error(e);
        }

        List<String> links = new ArrayList<>();
        if (ONLY_LOCAL_PRODUCTS) {
            links = Arrays.asList(ONLY_FOR_TESTING_PURPOSE_PRODUCTS);
        } else {
            for (String sitemap : SITEMAPS_FILES) {
                links.addAll(getLinksFromSitemap(WORKING_DIR + sitemap));
            }
        }
        for (String link : links) {
            if (isTesting()) break;

            ProdPojo prod = getProd(link);
            if (prod != null && !prod.getSku().isEmpty()) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(EXPORT_FILE, true), SEPARATOR_CHAR, QUOTE_CHAR)) {
                    writer.writeNext(prod.getRow().split(SEPARATOR));
                } catch (Exception e) {
                    log.error(e);
                }

            } else {
                log.trace("No product found at " + link);
            }
        }

        log.info("Wrote: " + EXPORT_FILE);
    }

    public static ProdPojo getProd(String prodStr) {
        try {
            Document doc = parseJsoup(prodStr);
            if (doc == null) {
                log.trace("Hei, no prod here " + prodStr + " after tried to parse it with Jsoup.");
                return null;
            }

            Boolean isProduct = false;

            ProdPojo prod = new ProdPojo();
            prod.setOriginalLink(prodStr);

            String sku = doc.select(CSS_QUERY_SKU).text();
            prod.setSku(sku);
            prod.setId(sku.replaceAll("[^0-9.]", ""));

            String name = doc.select(CSS_QUERY_NAME).text();

            prod.setDescription(cleanDesc(doc.select(CSS_QUERY_DESCRIPTION).text()));

            for (Element meta : doc.select("meta")) {
                String metaNameAttributeKeywords = meta.attr("name");
                if (metaNameAttributeKeywords.equalsIgnoreCase("keywords")) {
                    prod.setKeywords(meta.attr("content").replace(",", SEPARATOR_MULTIPLE_VALUES).trim());
                } else if (metaNameAttributeKeywords.equalsIgnoreCase("description")) {
                    prod.setShortDesc(meta.attr("content"));
                }

                String metaProd = meta.attr("property");
                if (metaProd.equalsIgnoreCase("og:type")) {
                    if (meta.attr("content").equalsIgnoreCase("product")) {
                        isProduct = true;
                    }
                } else if (metaProd.equalsIgnoreCase("og:site_name")) {
                    prod.setSiteName(meta.attr("content"));
                } else if (metaProd.equalsIgnoreCase("og:title")) {
                    prod.setTitle(meta.attr("content").split(";")[0]);
                } else if (metaProd.equalsIgnoreCase("og:image")) {
                    prod.setImage(meta.attr("content"));
                } else if (metaProd.equalsIgnoreCase("product:brand")) {
                    prod.setBrand(meta.attr("content"));
                } else if (metaProd.equalsIgnoreCase("product:product_link")) {
                    prod.setProductLink(meta.attr("content"));
                } else if (metaProd.equalsIgnoreCase("product:price")) {
                    prod.setPrice(cleanPrice(meta.attr("content")));
                }
            }

            for (Element meta : doc.select("title")) {
                if (meta.attr("itemprop").equalsIgnoreCase("name")) {
                    // Special character, must escape it with \\
                    List<String> cats = new ArrayList<>(Arrays.asList(meta.text().split("\\|")));
                    if (cats.size() > 2) {
                        cats.remove(0);
                        cats.remove(cats.size() - 1);
                    }

                    cats = Lists.reverse(cats);
                    String cat = "";
                    for (int i = 0; i < cats.size(); i++) {
                        cat += SEPARATOR_MULTIPLE_VALUES + cats.get(i).trim();
                    }
                    if (cat.length()>=1) {
                        cat = cat.substring(1, cat.length());
                    } else {
                        cat = prod.getBrand();
                    }
                    prod.setCats(cat);
                }
            }

            String properties = "";
            for (Element table : doc.select(CSS_QUERY_TABLE)) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    //log.info(tds.text());
                    properties += tds.text() + SEPARATOR_MULTIPLE_VALUES;
                }
            }
            if (properties.length() > 1) {
                properties = properties.substring(0, properties.length() - 1);
            }
            prod.setProperties(properties);
            prod.setBulkDesc(doc.select(CSS_QUERY_BULC_DESC).text());
            prod.setBulkPrice(doc.select(CSS_QUERY_BULL_PRICE).text().trim());
            prod.setPrice(doc.select(CSS_QUERY_PRICE).text().trim().replace(" €", "").replace(".", ""));

            if (prod.getBrand() == null || prod.getBrand().isEmpty()) {
                prod.setBrand("Other");
            }

            if (prod.getTitle() == null || prod.getTitle().isEmpty()){
                prod.setTitle(name);
            }
            if (prod.getImage() == null || prod.getImage().isEmpty()){
                prod.setImage("/" + DEFAULT_IMG);
            }

            if (isProduct) {
                log.trace(prod);
                return prod;
            } else {
                log.trace("Hei, no prod here " + prodStr + " after not found product tag.");
                return null;
            }
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return null;
    }

}
