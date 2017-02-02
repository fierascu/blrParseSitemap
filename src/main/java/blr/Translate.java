package blr;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.TranslationsResource;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static blr.AppParseExport.writeCatsToFile;
import static blr.AppParseExport.writeProductsToFile;
import static blr.Utils.*;

public class Translate {

    static Logger log = Logger.getLogger(AppParseExport.class.getName());

    public static void main(String[] args) throws Exception {

        // for test purpose only
        List<String> textToTranslate = Arrays.asList("Hallo!", "Ich suche nach einem Programm.", "Übersetzungen Produkt.");

        Map<String, List<String>> rawTranslations = getTranslation(TRANS_TO_LANG, textToTranslate, TRANS_GOOGLE_KEY);

        // list the translation
        log.trace(rawTranslations);

    }

    public static void testTranslate() {

        // for test purpose only
        List<String> textToTranslate = Arrays.asList("Hallo!", "Ich suche nach einem Programm.", "Übersetzungen Produkt.");

        Map<String, List<String>> rawTranslations = getTranslation(TRANS_TO_LANG, textToTranslate, TRANS_GOOGLE_KEY);

        // list the translation
        log.trace(rawTranslations);

    }

    public static void translate(List<ProdPojo> drl, String[] toTranslatedLanguages, String KEY) {
        // translate columns:  title, description, keywords, cats, properties, bulkDesc
        // categories also
        // create one long list with all the words to translate
        // don't forget to paginate
        List<String> allTextToTranslateL = new ArrayList<>();
        List<String> allTextFromTranslateL = new ArrayList<>();
        for (ProdPojo p : drl) {

            // use a character to not brake the order if it's empty string
            /*
            if (p.getTitle() == null || p.getTitle().isEmpty()){
                p.setTitle("-");
            }
            */
            allTextToTranslateL.add(p.getTitle());

            /*
            if (p.getDescription() == null || p.getDescription().isEmpty()){
                p.setDescription("-");
            }
            */
            allTextToTranslateL.add(p.getDescription());

            /*
            if (p.getKeywords() == null || p.getKeywords().isEmpty()){
                p.setKeywords("-");
            }
            */
            allTextToTranslateL.add(p.getKeywords());

            /*
            if (p.getCats() == null || p.getCats().isEmpty()){
                p.setCats("-");
            }
            */
            allTextToTranslateL.add(p.getCats());

            /*
            if (p.getProperties() == null || p.getProperties().isEmpty()){
                p.setProperties("-");
            }
            */
            allTextToTranslateL.add(p.getProperties());

            /*
            if (p.getBulkDesc() == null || p.getBulkDesc().isEmpty()){
                p.setBulkDesc("-");
            }
            */
            allTextToTranslateL.add(p.getBulkDesc());
        }

        Map<String, List<String>> translatedText = new HashMap<>();
        translatedText = getTranslation(toTranslatedLanguages, allTextToTranslateL, KEY);


        for (Map.Entry<String, List<String>> entry : translatedText.entrySet()) {
            String l = entry.getKey();
            List<String> pl = entry.getValue();
            //log.trace(entry.getKey() + "/" + entry.getValue());

            int noOfColumnsOnPage = 6;
            if (pl.size() != drl.size() * noOfColumnsOnPage) {
                log.error("Translate size is different from input size. Keeping original text.");
            } else {
                int contor = 0;
                //title, description, keywords, cats, properties, bulkDesc = 6
                while (contor < pl.size()) {
                    for (ProdPojo p : drl) {
                        p.setTitle(cleanDesc(pl.get(contor))); // get(0)
                        p.setDescription(trim(cleanDesc(pl.get(++contor)), 3000)); //get(1)
                        p.setKeywords(cleanDesc(pl.get(++contor)));
                        p.setCats(cleanDesc(pl.get(++contor)));
                        p.setProperties(cleanDesc(pl.get(++contor)));
                        p.setBulkDesc(cleanDesc(pl.get(++contor)));

                        // fill the others columns
                        p.setShortDesc(trim(cleanDesc(p.getDescription()), 150));
                        // increase pagination
                        contor++;
                    }
                }

                // write files for each languages
                writeCatsToFile(drl, l);
                writeProductsToFile(drl, l);
            }
        }
    }


    public static void translateOneProdPojoAtTime(List<ProdPojo> drl, String[] toTranslatedLanguages, String KEY) {
        /*
        1. 1 list = 1 pojo
        2. 1 prod pojo => 2 values in a map, with lang as key
        3. for map add the 2 to the list of pojos
        4. write those pojos
         */

        // create the map
        Map<String, List<ProdPojo>> translatedTextAllLangAllProds = new HashMap<>();
        for (String lang : toTranslatedLanguages) {
            translatedTextAllLangAllProds.put(lang, new ArrayList<>());
        }

        // iterate through prods to create 2 translated prods
        for (ProdPojo p : drl) {
            log.info("translating prod: " + p.getId() + "-" + p.getTitle());
            List<String> oneProdToTranslate = new ArrayList<>();
            oneProdToTranslate.add(p.getTitle());
            oneProdToTranslate.add(p.getDescription());
            oneProdToTranslate.add(p.getKeywords());
            oneProdToTranslate.add(p.getCats());
            oneProdToTranslate.add(p.getProperties());
            oneProdToTranslate.add(p.getBulkDesc());

            Map<String, List<String>> translatedText = getTranslation(toTranslatedLanguages, oneProdToTranslate, KEY);
            for (Map.Entry<String, List<String>> entry : translatedText.entrySet()) {
                String l = entry.getKey();
                List<String> pl = entry.getValue();

                int contor = 0;
                ProdPojo newTrasProd = ProdPojo.newInstance(p,
                        pl.get(contor), // get(0)
                        pl.get(++contor), // get(1)
                        pl.get(++contor), // get(2)
                        pl.get(++contor), // get(3)
                        pl.get(++contor), // get(4)
                        pl.get(++contor) // get(5)
                );

                log.trace("translated text: " + pl.toString());
                // add translates prod
                translatedTextAllLangAllProds.get(l).add(newTrasProd);
            }
        }

        // iterate translated prods an write them to files
        for (Map.Entry<String, List<ProdPojo>> entry : translatedTextAllLangAllProds.entrySet()) {
            String l = entry.getKey();
            List<ProdPojo> pl = entry.getValue();

            // write files for each languages
            writeCatsToFile(pl, l);
            writeProductsToFile(pl, l);
        }
    }

    public static Map<String, List<String>> getTranslation(String[] toTranslatedLanguages, List<String> textToTranslate, String KEY) {
        Map returnedMap = new HashMap<String, List<String>>();
        boolean isError = false;

        // set key created via google cloud console
        final TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(KEY);

        // Set up the HTTP transport and JSON factory
        HttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // set up translate
        final com.google.api.services.translate.Translate translate = new com.google.api.services.translate.Translate.Builder(httpTransport, jsonFactory, null)
                .setApplicationName("SimpleTranslate")
                .setTranslateRequestInitializer(KEY_INITIALIZER)
                .build();

        for (String lang : toTranslatedLanguages) {
            List<TranslationsResource> translatedTexts = null;
            try {
                translatedTexts = translate.translations().list(textToTranslate, lang).execute().getTranslations();
            } catch (IOException e) {
                log.error(e);
                if (e.getMessage().startsWith("403 Forbidden")) {
                    log.error("SETUP GOOGLE TRANSLATE ACCOUNT!");
                }
            }

            if (isError || translatedTexts == null) {
                // if it's a error return same text
                returnedMap.put(lang, textToTranslate);
            } else {
                List<String> returnedTranslatedText = new ArrayList<>();

                for (TranslationsResource transRes : translatedTexts) {
                    String transText = transRes.getTranslatedText();
                    // log.trace(transText);
                    returnedTranslatedText.add(transText);
                }
                returnedMap.put(lang, returnedTranslatedText);
            }
        }
        return returnedMap;
    }
}