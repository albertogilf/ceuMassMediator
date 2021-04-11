# CMM *Aspergillus* DB files
## Mysql backup file
MySQL logic backup containing the compounds, organisms and references with MS purposes. This database is currently in production to perform [*m/z* searches](http://ceumass.eps.uspceu.es/batch_advanced_search.xhtml). The user should select the *Aspergillus* database. 

## CSV files
Three files containing the data from compounds curated for the *Aspergillus* database (compounds_aspergillus_metabolome_db.csv), organisms(organisms_aspergillus_metabolome_db.csv) and references (references_aspergillus_metabolome_db.csv)

## E-R model
The Entity-Relation model contains a MySQL E-R Model that can be imported using MySQL Workbench utility. To open it, MySQL Workbench shall be instaled and the mwb file shall be open with the MySQL Workbench application. The user can see and navigate the model there.

## gold_standard_*Aspergillus*
CSV file containing 26 compounds identified by targeted experiment present in *Aspergillus* fumigatus and/or *Aspergillus* nidulans. It contains bibliography references linking the compounds to *Aspergillus* and

## Dependencies 
To query the Mysql backup file any RDBMS compatible with MySQL syntax. MySQL or MariaDB are tested and recommended.


