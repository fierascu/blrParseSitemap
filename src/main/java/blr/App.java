package blr;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static blr.CatPojo.getCategoryOrDefaultCat;
import static blr.FilesIO.*;
import static blr.Translate.translateOneProdPojoAtTime;
import static blr.Utils.*;


public class App {
    static Logger log = Logger.getLogger(App.class.getName());

   public static void main(String[] args) {
        initializeProperties();
        CreatePaths();
        if (APP_FEATURE_CRAWL_SITEMAP) {
            ReadProduct.crawlFromSitemap();
            pauseRandom(); // needed for streams to close
        }
        List<ProdPojo> ppl = extractProdPojos();
        writeCatsToFile(ppl, TRANS_FROM_LANG);
        writeProductsToFile(ppl, TRANS_FROM_LANG);
        if (APP_FEATURE_DOWNLOAD_IMGS) {
            download(ppl);
        }
        if (APP_FEATURE_UPLOAD_IMGS) {
            uploadFtp();
        }
        if (APP_FEATURE_TRANSLATE) {
            translateOneProdPojoAtTime(ppl, TRANS_TO_LANG, TRANS_GOOGLE_KEY);
        }
    }

    public static List<ProdPojo> extractProdPojos() {
        List<ProdPojo> drl = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(EXPORT_FILE), SEPARATOR_CHAR, QUOTE_CHAR, 1)) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line

                if (isTestingWriting()) {
                    break;
                }

//id, siteName, title, description, keywords, image, brand, price, productLink, sku, shortDesc, cats, properties, bulkDesc, bulkPrice, originalLink

                ProdPojo lineValue = new ProdPojo();
                int col = 0;
                if (nextLine.length > col) {
                    lineValue.setId(nextLine[col]);
                    col++; //1
                }
                if (nextLine.length > col) {
                    lineValue.setSiteName(nextLine[col]);
                    col++; //2
                }
                if (nextLine.length > col) {
                    lineValue.setTitle(nextLine[col]);
                    col++;//3
                }
                if (nextLine.length > col) {
                    lineValue.setDescription(nextLine[col]);
                    col++; //4
                }
                if (nextLine.length > col) {
                    lineValue.setKeywords(nextLine[col]);
                    col++; //5
                }
                if (nextLine.length > col) {
                    lineValue.setImage(nextLine[col]);
                    col++; //6
                }
                if (nextLine.length > col) {
                    lineValue.setBrand(nextLine[col]);
                    col++; //7
                }
                if (nextLine.length > col) {
                    lineValue.setPrice(nextLine[col]);
                    col++; //8
                }
                if (nextLine.length > col) {
                    lineValue.setProductLink(nextLine[col]);
                    col++; //9
                }
                if (nextLine.length > col) {
                    lineValue.setSku(nextLine[col]);
                    col++; //10
                }
                if (nextLine.length > col) {
                    lineValue.setShortDesc(nextLine[col]);
                    col++; //11
                }
                if (nextLine.length > col) {
                    lineValue.setCats(nextLine[col]);
                    col++; //12
                }
                if (nextLine.length > col) {
                    lineValue.setProperties(nextLine[col]);
                    col++; //13
                }
                if (nextLine.length > col) {
                    lineValue.setBulkDesc(nextLine[col]);
                    col++; //14
                }
                if (nextLine.length > col) {
                    lineValue.setBulkPrice(nextLine[col]);
                    col++; //15
                }
                if (nextLine.length > col) {
                    lineValue.setOriginalLink(nextLine[col]);
                    col++; //16
                }

                if (lineValue != null) {
                    drl.add(lineValue);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        log.info("Extracting products from export and I understood " + drl.size() + " lines");
        return drl;
    }

    public static void writeCatsToFile(List<ProdPojo> drl, String language) {
        log.info("Received a list of lines of " + drl.size() + " categories from the products");
        //create categories
        List<CatPojo> cats = new ArrayList<>();
        Set<String> uniqueCats = new HashSet<String>();

        // extract unique cats
        for (ProdPojo dr : drl) {
            if (!dr.getCats().isEmpty()) {
                uniqueCats.add(dr.getCats());
            }
        }
        cats.addAll(extractCats(uniqueCats));

        //write categories to file
        String outputCsvCat = CSV_DIR + "Categories_" + language + "_" + getTimestamp() + ".csv";
        String headerResultCat = "id;Active (0/1);Name*;Parent Category;Root category (0/1);description;Meta-title;Meta-keywords;Meta-description;URL rewritten;Image URL;ID ou nom de la boutique";
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsvCat), SEPARATOR_CHAR, QUOTE_CHAR)) {
            // feed in your array (or convert your data to an array)
            writer.writeNext(headerResultCat.split(SEPARATOR));

            for (CatPojo cat : cats) {
                String[] entries = (
                        cat.id + SEPARATOR + //id
                                cat.active + SEPARATOR + // Active (0/1)
                                cat.name + SEPARATOR +// Name*
                                cat.parentCategory + SEPARATOR +// Parent Category
                                cat.rootCategory + SEPARATOR +// Root category (0/1)
                                SEPARATOR +// description
                                SEPARATOR +// Meta-title
                                SEPARATOR +// Meta-keywords
                                SEPARATOR +// Meta-description
                                getUrlRew(cat.name) + SEPARATOR +// URL rewritten
                                cat.imageUrl + SEPARATOR +// Image URL
                                cat.storeId // ID ou nom de la boutique;
                ).split(SEPARATOR);
                writer.writeNext(entries);
            }

            log.info("Wrote: " + outputCsvCat);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static void writeProductsToFile(List<ProdPojo> drl, String language) {
        String outputCsvProd = CSV_DIR + "Products_" + language + "_" + getTimestamp();
        String headerResultProd = "id;Active (0/1);Name;Categories;Price tax excl;Tax rules id;Wholesale price;On sale (0/1);Discount amount;Discount percent;Discount from (yyy-mm-dd);Discount to (yyy-mm-dd);Reference #;Supplier reference #;Supplier;Manufacturer;EAN13;UPC;Ecotax;Weight;Quantity;Short description;description;Tags;Meta-title;Meta-keywords;Meta-description;URL rewritten;Text when in-stock;Text if back-order allowed;Available for order (0=No/1=Yes);Product creation date;Show price (0=No/1=Yes);Image URLs;Delete existing images (0=No/1=Yes);Feature(Name:Value:Position);Available online only (0=No/1=Yes);Condition (new/used/refurbished);ID / Name of shop";

        List<String[]> bulkProdList = new ArrayList<>();
        int contor = 0;
        for (int i = 0; i < drl.size(); i++) {
            ProdPojo dr = drl.get(i);
            String[] entries = (
                    dr.getId() + SEPARATOR +  //id
                            "1" + SEPARATOR + //Active (0/1)
                            dr.getTitle() + SEPARATOR + //Name
                            getCategoryOrDefaultCat(dr.getCats()) + SEPARATOR +//Categories
                            cleanPrice(dr.getPrice()) + SEPARATOR +//Price tax excl
                            "1" + SEPARATOR +//Tax rules id
                            SEPARATOR +//Wholesale price
                            "1" + SEPARATOR +//On sale (0/1)
                            SEPARATOR +//Discount amount
                            SEPARATOR +//Discount percent
                            SEPARATOR +//Discount from (yyy-mm-dd)
                            SEPARATOR +//Discount to (yyy-mm-dd)
                            SEPARATOR +//Reference #
                            SEPARATOR +//Supplier reference #
                            dr.getBrand() + SEPARATOR +//Supplier
                            dr.getBrand() + SEPARATOR +//Manufacturer
                            SEPARATOR +//EAN13
                            SEPARATOR +//UPC
                            SEPARATOR +//Ecotax
                            SEPARATOR +//Weight
                            QUANTITY_STOCK + SEPARATOR +//Quantity
                            dr.getShortDesc() + SEPARATOR +//Short description
                            trim(dr.getDescription(), 3000) + SEPARATOR +//description
                            dr.getKeywords() + SEPARATOR +//Tags
                            trim(dr.getShortDesc(), 128) + SEPARATOR +//Meta-title
                            dr.getKeywords() + SEPARATOR +//Meta-keywords
                            dr.getShortDesc() + SEPARATOR +//Meta-description
                            getUrlRew(dr.getTitle()) + SEPARATOR +//URL rewritten
                            "In stock" + SEPARATOR +//Text when in-stock
                            "Out stock" + SEPARATOR +//Text if back-order allowed
                            "1" + SEPARATOR +//Available for order (0=No/1=Yes)
                            SEPARATOR +//Product creation date
                            SEPARATOR +//Show price (0=No/1=Yes)
                            getImageUrl(dr.getImage()) + SEPARATOR +//Image URLs
                            SEPARATOR +//Delete existing images (0=No/1=Yes)
                            "sku: " + dr.getSku() + "|" + dr.getProperties() + bulk(dr.getBulkDesc(), dr.getBulkPrice(), dr.getPrice()) + SEPARATOR + //Feature(Name:Value:Position)
                            "0" + SEPARATOR +//Available online only (0=No/1=Yes)
                            "new" + SEPARATOR +//Condition (new/used/refurbished)
                            "1"//ID / Name of shop
            ).split(SEPARATOR);

            bulkProdList.add(entries);

            String outputCsvProdFileName = outputCsvProd + "_" + i + ".csv";
            if (contor > SPLIT_PROD_EXPORT_FILE && i < drl.size()) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsvProdFileName), SEPARATOR_CHAR, QUOTE_CHAR)) {
                    // feed in your array (or convert your data to an array)
                    writer.writeNext(headerResultProd.split(";"));
                    for (String[] bl : bulkProdList) {
                        writer.writeNext(bl);
                    }
                    bulkProdList.clear();
                    contor = 0;
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                contor++;
            }

            if (i + 1 == drl.size()) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsvProdFileName), SEPARATOR_CHAR, QUOTE_CHAR)) {
                    // feed in your array (or convert your data to an array)
                    writer.writeNext(headerResultProd.split(";"));
                    for (String[] bl : bulkProdList) {
                        writer.writeNext(bl);
                    }
                    bulkProdList.clear();
                } catch (Exception e) {
                    log.error(e);
                }
            }

        }

        log.info("Wrote: " + outputCsvProd);
    }


    private static Collection<? extends CatPojo> extractCats(Set<String> uniqueCats) {
        int noCats = 1000; // starting number for category id
        List<CatPojo> cats = new ArrayList<>();
        CatPojo newLabelCat = new CatPojo(noCats++ + "", OTHERS_DEFAULT_CATEGORY_NAME, PARENT_HOME_CATEGORY_NAME, "0");
        cats.add(newLabelCat);

        for (String uniqueCat : uniqueCats) {
            String[] tokens = uniqueCat.split(SEPARATOR_ESCAPED_MULTIPLE_VALUES);
            // find cat if exist
            for (int i = 0; i < tokens.length; i++) {
                List<CatPojo> newCats = new ArrayList<>();
                //for (CatPojo cat : cats) {
                //if (!tokens[i].equalsIgnoreCase(cat.name)){// &&
                if (!cats.toString().contains(tokens[i])) {
                    //cat don't exist, create a new one
                    String rootCat = "0";// always not root cat
                    String parentCat = i == 0 ? PARENT_HOME_CATEGORY_NAME : tokens[i - 1].trim();//get the previous cat aka token
                    newCats.add(new CatPojo(noCats++ + "", tokens[i].trim(), parentCat, rootCat));
                }
                if (newCats.size() > 0) {
                    cats.addAll(newCats);
                    // log.trace(noCats + "=>" + newCats.toString());
                }
            }
        }
        log.trace("Looking trough categories and found " + cats.size() + " uniques categories.");
        return cats;
    }

}
