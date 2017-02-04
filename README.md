# blrParseSitemap


## Synopsis

This project is a java implementation o a crawler which reads one ore many sitemap.xml files, extracts products links, crawls all links and extract product data. Each parsed link is saved in an export.csv output file which can be parsed individual offline. All parsing errors are logged in errors.csv.
With the resulted export.csv it extracts products and categories files in original site language. The images for those products are downloaded locally and then uploaded on a ftp server.
All the products and categories are translated in one or more languages. Translated files will have language prescurtation in the name. Currently files are splitted in 3998 chunks of products.

## Used

- IntelliJ IDE,
- Maven for dependencies,
- Jsoup library for DOM parsing
- Opencsv library for csv support
- Log4j for logging
- Google translate apis

## Installation

Just run it from intelliJ or maven.

## Tests

Used local files for multiple parsing test.

## Posible future improuvments

- More accurate translation and reduce translate bandwidth: Parse only the categories and use a dictionary to replace in products,
- Use an external configuration file and if not found use default config.properties,
- Add in configuration file which actions should be run.


## License

Released under Apache License