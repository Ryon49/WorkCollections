Source code of cs121's search engine project.



This project use mysql to store main index, including tf-idf and cosine similarity score for single word and json file to store metadata of each webpage. 



So for the case of single word query, the result will fetched directly from Mysql.  When there is a multi-word query, it will go through Mysql and get document id that includes the word and after that read the metadata from json file and then calculate the cosine similarity score.



One interesting thing is that my search engine sometimes does not work for sensitive word cases. But I think isn't mysql query are case-insensitive? What? 


