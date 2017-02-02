package blr;

import java.util.Random;

import static blr.Utils.OTHERS_DEFAULT_CATEGORY_NAME;

public class CatPojo {
    //id;Active (0/1);Name*;Parent Category;Root category (0/1);Description;Meta-title;Meta-keywords;Meta-description;URL rewritten;Image URL;ID ou nom de la boutique
    String id = "";
    String active = "1";
    String name;
    String parentCategory;
    String rootCategory;
    String description;
    String metaTitle;
    String metaKeywords;
    String metaDescription;
    String urlRewritten;
    String imageUrl = "";
    String storeId = "1";

    CatPojo() {
        id = "" + new Random().nextInt(1000000);
    }

    CatPojo(String idC, String nameC, String parentCategoryC, String rootCategoryC) {
        id = idC;
        name = nameC;
        parentCategory = parentCategoryC;
        rootCategory = rootCategoryC;
    }

    public static String getCategoryOrDefaultCat(String category) {
        return category.isEmpty() ? OTHERS_DEFAULT_CATEGORY_NAME : category;
    }

    @Override
    public String toString() {
        return name;
    }
}
