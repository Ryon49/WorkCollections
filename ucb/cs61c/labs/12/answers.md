docwordcount.py

1. use the combination of (word, documentId) as unique identifier so that we can preserve words that span multiple documents

2. use mapFunc() to extract word from tuple, assign 1 with it

3. use reduce function


index.py

final form: (u'speedy', u'post_27f0ecd29fb30437d67bd4b685233b23'), [14, ...]

1. same as docwordcount.py, use (word, documentId) as primary key, then made the position of the word be the value of PairRDD. (also I made the index a array [index] because addition between arrays is a simply append operation)

2. I ignore mapFunc because there is nothing needed to be extract

3. reduceByKey is the combination of groupByKey and mapValues in Spark. groupByKey() will transform input to key-value pair like ((word, documentId), ([index1], [index2], [index3])
and then I use reduce function to flatten value, and it will finally become 
((word, documentId), [index1, index2, index3])
