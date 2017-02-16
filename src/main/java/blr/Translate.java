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

import static blr.App.prepareCats;
import static blr.App.writeProductsToFile;
import static blr.Utils.*;

public class Translate {

    static Logger log = Logger.getLogger(App.class.getName());

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

    public static void translateCats(List<ProdPojo> drl, String[] toTranslateLanguages, String KEY){
        /*
        1. get unique cats
        2. get translated values in multimap
        3. return translated
        4. in transProd, search and repace from dictionary
         */
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
            // oneProdToTranslate.add(p.getCats()); //dont' translate cats
            oneProdToTranslate.add(p.getProperties());
            oneProdToTranslate.add(p.getBulkDesc());

            Map<String, List<String>> translatedText = getTranslation(toTranslatedLanguages, oneProdToTranslate, KEY);
            for (Map.Entry<String, List<String>> entry : translatedText.entrySet()) {
                String l = entry.getKey();
                List<String> pl = entry.getValue();

                int contor = 0;
                ProdPojo newTrasProd = ProdPojo.newInstance(
                        p, //oldProd
                        pl.get(contor), // get(0) = title
                        pl.get(++contor), // get(1) = description
                        pl.get(++contor), // get(2) = keywords
                        pl.get(++contor), // get(3) = cats // replace this with categories from dictionary
                        pl.get(++contor), // get(4) = properties
                        pl.get(++contor) // get(5) = bulkDesc
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
            prepareCats(pl, l);
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
        } catch (GeneralSecurityException | IOException e) {
            isError = true;
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
                isError = true;
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
                    log.trace(transText);
                    returnedTranslatedText.add(transText);
                }
                returnedMap.put(lang, returnedTranslatedText);
            }
        }
        return returnedMap;
    }
}