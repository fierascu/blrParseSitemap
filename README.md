# blrParseSitemap


## Synopsis

This project is a java implementation of a crawler which reads one or many sitemap.xml files, extracts product links, crawls all links and extracts product data.
Each parsed link is saved in an export.csv output file which can be parsed individually offline.
All parsing errors are logged in errors.csv. With the resulted export.csv it extracts products and category files in original site language.
The images for those products are downloaded locally and then uploaded on an ftp server. All the products and categories are translated in one or more languages.
Translated files will have language abbreviations in the name. Currently, files are split in 3998 chunks of products.
Can use an external configuration file and enabled features from it.

## Used

- IntelliJ IDE,
- Maven for dependencies,
- Jsoup library for DOM parsing,
- Opencsv library for csv support,
- Log4j for logging,
- Google translate apis.

## Installation

Just run it from intelliJ or maven.

## Tests

Used local files for multiple parsing test.

## Possible future improvements

More accurate translation and reduce translate bandwidth: Parse only the categories and use a dictionary to replace in products.

## License

Released under Apache License