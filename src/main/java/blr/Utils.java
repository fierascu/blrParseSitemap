package blr;

import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import static blr.FilesIO.isLocalFile;

public class Utils {
    public static final String DEFAULT_IMG = "default.jpg";
    // enable app features
    public static Boolean APP_FEATURE_CRAWL_SITEMAP = true;
    public static Boolean APP_FEATURE_DOWNLOAD_IMGS = true;
    public static Boolean APP_FEATURE_UPLOAD_IMGS = true;
    public static Boolean APP_FEATURE_TRANSLATE = true;
    // css selectors
    public static String CSS_QUERY_SKU;
    public static String CSS_QUERY_NAME;
    public static String CSS_QUERY_DESCRIPTION;
    public static String CSS_QUERY_PRICE;
    public static String CSS_QUERY_BULL_PRICE;
    public static String CSS_QUERY_BULC_DESC;
    public static String CSS_QUERY_TABLE;
    // paths & files
    public static String WORKING_DIR = "c:\\TEMP\\";
    public static String CSV_DIR = WORKING_DIR + "exportedCsv\\";
    public static String ERR_LOG_FILE = CSV_DIR + "errors.csv";
    public static String EXPORT_FILE = CSV_DIR + "export.csv";
    public static String IMG_DIR = WORKING_DIR + "exportedImg\\";
    public static String SITEMAP_FILE = WORKING_DIR + "sitemap.xml";
    public static String SITEMAPS_FILES[] = {};
    // variables
    public static String SEPARATOR = ";";
    public static char SEPARATOR_CHAR = SEPARATOR.charAt(0);
    public static String SEPARATOR_MULTIPLE_VALUES = "|";
    public static String SEPARATOR_ESCAPED_MULTIPLE_VALUES = "\\|";
    public static char QUOTE_CHAR = '"';
    public static String OTHERS_DEFAULT_CATEGORY_NAME = "Others";
    public static String PARENT_HOME_CATEGORY_NAME = "Home";
    public static int SPLIT_PROD_EXPORT_FILE = 3998;
    public static boolean USE_RANDOM_WAIT = true;
    public static int USE_RANDOM_WAIT_LIMIT = 3000;
    public static String QUANTITY_STOCK = "1000";

    // ftp
    public static int FTP_PORT = 21;
    public static String FTP_SERVER;
    public static String FTP_USER;
    public static String FTP_PASS;
    public static String SERVER_IMG_PATH = "/tmp/img/";
    public static String SERVER_IMG_IMPORT_PATH = "/public_html" + SERVER_IMG_PATH;
    public static String HTTP_PATH_IMG_IMPORT = "http://" + FTP_SERVER + SERVER_IMG_PATH;

    // translate
    public static String TRANS_FROM_LANG = "de";
    public static String TRANS_GOOGLE_KEY = "";
    public static String TRANS_TO_LANG[] = {"ro"};

    // testing
    public static int ONLY_FOR_TESTING_PURPOSE_LIMIT = 20;
    public static int ONLY_FOR_TESTING_PURPOSE_LIMIT_WRITING = ONLY_FOR_TESTING_PURPOSE_LIMIT;
    public static String ONLY_FOR_TESTING_PURPOSE_PRODUCTS[];
    public static boolean ONLY_FOR_TESTING_PURPOSE = true;
    public static boolean ONLY_LOCAL_PRODUCTS = true;

    public static Properties prop = null;
    static Logger log = Logger.getLogger(AppParseExport.class.getName());

    public static void main(String[] args) {
    }


    public static void initializeProperties() {
        // set constants from properties
        String configPropertiesPath = ".\\src\\main\\resources\\config.properties";
        prop = getProperties(configPropertiesPath);

        // enable app features
        APP_FEATURE_CRAWL_SITEMAP = Boolean.valueOf(prop.getProperty("APP_FEATURE_CRAWL_SITEMAP", String.valueOf(APP_FEATURE_CRAWL_SITEMAP)));
        APP_FEATURE_DOWNLOAD_IMGS = Boolean.valueOf(prop.getProperty("APP_FEATURE_DOWNLOAD_IMGS", String.valueOf(APP_FEATURE_DOWNLOAD_IMGS)));
        APP_FEATURE_UPLOAD_IMGS = Boolean.valueOf(prop.getProperty("APP_FEATURE_UPLOAD_IMGS", String.valueOf(APP_FEATURE_UPLOAD_IMGS)));
        APP_FEATURE_TRANSLATE = Boolean.valueOf(prop.getProperty("APP_FEATURE_TRANSLATE", String.valueOf(APP_FEATURE_TRANSLATE)));

        // css selectors
        CSS_QUERY_SKU = prop.getProperty("CSS_QUERY_SKU");
        CSS_QUERY_NAME = prop.getProperty("CSS_QUERY_NAME");
        CSS_QUERY_DESCRIPTION = prop.getProperty("CSS_QUERY_DESCRIPTION");
        CSS_QUERY_PRICE = prop.getProperty("CSS_QUERY_PRICE");
        CSS_QUERY_BULL_PRICE = prop.getProperty("CSS_QUERY_BULL_PRICE");
        CSS_QUERY_BULC_DESC = prop.getProperty("CSS_QUERY_BULC_DESC");
        CSS_QUERY_TABLE = prop.getProperty("CSS_QUERY_TABLE");

        // paths & files
        WORKING_DIR = prop.getProperty("WORKING_DIR_NAME", WORKING_DIR);
        CSV_DIR = WORKING_DIR + prop.getProperty("CSV_DIR_NAME", "exportedCsv");
        IMG_DIR = WORKING_DIR + prop.getProperty("IMG_DIR_NAME", "exportedImg");
        SITEMAP_FILE = WORKING_DIR + prop.getProperty("SITEMAP_FILE_NAME", "sitemap.xml");
        SITEMAPS_FILES = prop.getProperty("SITEMAPS_FILES", "{}").split(",");
        ERR_LOG_FILE = CSV_DIR + prop.getProperty("ERR_LOG_FILE_NAME", "errors.csv");
        EXPORT_FILE = CSV_DIR + prop.getProperty("EXPORT_FILE_NAME", "export.csv");

        // variables
        SEPARATOR = prop.getProperty("SEPARATOR", SEPARATOR);
        SEPARATOR_MULTIPLE_VALUES = prop.getProperty("SEPARATOR_MULTIPLE_VALUES", SEPARATOR_MULTIPLE_VALUES);
        SEPARATOR_ESCAPED_MULTIPLE_VALUES = prop.getProperty("SEPARATOR_ESCAPED_MULTIPLE_VALUES", SEPARATOR_ESCAPED_MULTIPLE_VALUES);
        QUOTE_CHAR = prop.getProperty("QUOTE_CHAR", String.valueOf(QUOTE_CHAR)).charAt(0);
        SEPARATOR_CHAR = SEPARATOR.charAt(0);
        USE_RANDOM_WAIT = Boolean.valueOf(prop.getProperty("USE_RANDOM_WAIT", String.valueOf(USE_RANDOM_WAIT)));
        USE_RANDOM_WAIT_LIMIT = Integer.valueOf(prop.getProperty("USE_RANDOM_WAIT_LIMIT", String.valueOf(USE_RANDOM_WAIT_LIMIT)));
        SPLIT_PROD_EXPORT_FILE = Integer.valueOf(prop.getProperty("SPLIT_PROD_EXPORT_FILE", String.valueOf(SPLIT_PROD_EXPORT_FILE)));
        PARENT_HOME_CATEGORY_NAME = prop.getProperty("PARENT_HOME_CATEGORY_NAME", PARENT_HOME_CATEGORY_NAME);
        OTHERS_DEFAULT_CATEGORY_NAME = prop.getProperty("OTHERS_DEFAULT_CATEGORY_NAME", OTHERS_DEFAULT_CATEGORY_NAME);
        QUANTITY_STOCK = (prop.getProperty("QUANTITY_STOCK", QUANTITY_STOCK));

        // ftp
        FTP_SERVER = prop.getProperty("FTP_SERVER");
        FTP_PORT = Integer.valueOf(prop.getProperty("FTP_PORT", String.valueOf(FTP_PORT)));
        FTP_USER = prop.getProperty("FTP_USER");
        FTP_PASS = prop.getProperty("FTP_PASS");
        SEPARATOR = prop.getProperty("SEPARATOR", SEPARATOR);
        SERVER_IMG_PATH = prop.getProperty("SERVER_IMG_PATH", SERVER_IMG_PATH);
        SERVER_IMG_IMPORT_PATH = "/public_html" + SERVER_IMG_PATH;
        HTTP_PATH_IMG_IMPORT = "http://" + FTP_SERVER + SERVER_IMG_PATH;

        // translate
        TRANS_GOOGLE_KEY = prop.getProperty("TRANS_GOOGLE_KEY");
        TRANS_FROM_LANG = prop.getProperty("TRANS_FROM_LANG", TRANS_FROM_LANG);
        TRANS_TO_LANG = prop.getProperty("TRANS_TO_LANG").split(",");

        // testing
        ONLY_FOR_TESTING_PURPOSE_LIMIT = Integer.valueOf(prop.getProperty("ONLY_FOR_TESTING_PURPOSE_LIMIT", String.valueOf(ONLY_FOR_TESTING_PURPOSE_LIMIT)));
        ONLY_FOR_TESTING_PURPOSE = Boolean.valueOf(prop.getProperty("ONLY_FOR_TESTING_PURPOSE", String.valueOf(ONLY_FOR_TESTING_PURPOSE)));
        ONLY_LOCAL_PRODUCTS = Boolean.valueOf(prop.getProperty("ONLY_LOCAL_PRODUCTS", String.valueOf(ONLY_LOCAL_PRODUCTS)));
        ONLY_FOR_TESTING_PURPOSE_PRODUCTS = prop.getProperty("ONLY_FOR_TESTING_PURPOSE_PRODUCTS", "").split(",");

        // printProp(prop);
    }

    public static Properties getProperties(String configPropertiesPath) {
        try {
            Properties prop = new Properties();
            // load a properties file
            prop.load(new FileInputStream(configPropertiesPath));
            //prop.load(new FileInputStream("./config.properties"));
            // printProp(prop);
            return prop;
        } catch (Exception e) {
            log.error("Error getProperties: " + e);
            return null;
        }
    }

    public static void printProp(Properties prop) {
        // get the property value and print it out
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            log.trace(key + "=" + value);
        }
        log.trace("Read " + prop.size() + " properties.");
    }

    public static Document parseJsoup(String url) {
        boolean isLocal = false;
        // starts with a path like c: or d:
        if (url != null && (isLocalFile(url))) {
            isLocal = true;
        }
        Document doc = null;
        try {
            if (isLocal) {
                File urlAsFile = new File(url);
                doc = Jsoup.parse(urlAsFile, "UTF-8");
            } else {
                int tries = 3;
                if (tries >= 0 && doc == null) {
                    pauseRandom();
                    doc = jsoupGet(url);
                    tries--;
                }

            }

        } catch (Exception e) {
            log.error("Exception from parseFromJsoup: " + e.getMessage());
            writeErr(url, e.getMessage());
        }
        return doc;
    }

    private static void writeErr(String url, String message) {
        // TODO if file don't exist, create with header  else only append rows

        try (CSVWriter writer = new CSVWriter(new FileWriter(ERR_LOG_FILE, true), SEPARATOR_CHAR, CSVWriter.NO_QUOTE_CHARACTER)) {
            writer.writeNext((url + SEPARATOR + message).split(SEPARATOR));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void pauseRandom() {
        if (USE_RANDOM_WAIT) {
            int sleepFor = ThreadLocalRandom.current().nextInt(0, USE_RANDOM_WAIT_LIMIT);
            log.trace("sleepFor:" + sleepFor);
            try {
                Thread.sleep(sleepFor);
            } catch (Exception e) {
                log.error("sleepForError", e);
            }
        }
    }

    public static String getImageUrl(String image_url) {
        String imageName = DEFAULT_IMG;
        String[] tokens = image_url.split("/");
        if (tokens.length > 1) {
            imageName = tokens[tokens.length - 1];
        }
        return HTTP_PATH_IMG_IMPORT + imageName.toLowerCase();
        //return "img/import/" + tokens[tokens.length - 1];
    }


    // from "öäü" will produce "oau"
    public static String normalizeString(String str) {
        String normalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        String resultString = normalizedString.replaceAll("[^\\x00-\\x7F]", "");
        return resultString;
    }

    public static String removeUnusualCharacter(String src) {
        return removeNonAsciiChars(src).replace("&amp;", "-").replace("&", "-")
                .replace("{", "").replace("}", "")
                .replace("[", "").replace("]", "")
                .replace("(", "").replace(")", "")
                .replace("%", "")
                .replace("$", "")
                .replace(" ", "-")
                .replace(",", "-")
                .replace("/", "")
                .replace("&gt;", "")
                .replace("&lt;", "")
                .replace("&#38;", "")
                .replace("&quote;", "")
                .replace(";", "")
                .replace("  ", " ")
                .replace("\\", "")
                .replace("--", "-")
                .replace("--", "-")
                .trim();
    }

    public static String cleanDesc(String src) {
        return src.replace("&amp;", "-")
                .replace("&gt;", ">")
                .replace("&lt;", "<")
                .replace("&#38;", "")
                .replace("&quote;", "")
                .replace(";", ". ")
                .replace("\r\n", ". ")
                .replace("\r", ". ")
                .replace("\n", ". ")
                .trim();
    }

    public static String removeNonAsciiChars(String str) {
        return str.replaceAll("[^\\x00-\\x7F]", "");
    }

    public static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 5) + " ...";
        } else {
            return s;
        }
    }

    public static Document jsoupGet(String url) {
        Document doc = null;
        try {
            Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.69 Safari/537.36")
                    .maxBodySize(0)
                    .header("Accept-Encoding", "gzip, deflate")
                    .referrer("http://www.google.com")
                    .timeout(10000);
            Connection.Response resp = con.execute();
            if (resp.statusCode() == 200) {
                doc = con.get();
            }
            return doc;
        } catch (Exception e) {
            writeErr(url, e.getMessage());
            log.error("Can't parse link: " + url, e);
        }
        return null;
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public static String bulk(String bulkDesc, String bulkPrice, String price) {
        if ((bulkDesc.isEmpty() || bulkPrice.isEmpty()) || cleanPrice(price).equals(cleanPrice(bulkPrice))) {
            return "";
        }
        return SEPARATOR_MULTIPLE_VALUES + "bulk: " + bulkDesc + " " + bulkPrice;
    }

    public static String cleanPrice(String price) {
        String cleanPrice = price.trim().replace(" €", "").replace(".", "").trim();
        String[] tokens = cleanPrice.split(" ");
        // Special case: ab 139,00 || statt 499,00 nur 249,00
        if (tokens.length >= 1) {
            return tokens[tokens.length - 1];
        } else {
            return cleanPrice;
        }
    }


    public static boolean isTesting() {
        if (ONLY_FOR_TESTING_PURPOSE && ONLY_FOR_TESTING_PURPOSE_LIMIT >= 0) {
            ONLY_FOR_TESTING_PURPOSE_LIMIT--;
        }
        if (ONLY_FOR_TESTING_PURPOSE_LIMIT <= 0) {
            return true;
        }
        return false;
    }


    public static boolean isTestingWriting() {
        if (ONLY_FOR_TESTING_PURPOSE && ONLY_FOR_TESTING_PURPOSE_LIMIT >= 0) {
            ONLY_FOR_TESTING_PURPOSE_LIMIT_WRITING--;
        }
        if (ONLY_FOR_TESTING_PURPOSE_LIMIT_WRITING <= 0) {
            return true;
        }
        return false;
    }


    public static String getUrlRew(String name) {
        return removeUnusualCharacter(normalizeString(name.toLowerCase()));
    }


}
