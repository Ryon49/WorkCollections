#!/bin/bash
mysql -u root --password=admin < createtable.sql


for SQL in unigrams*.sql;
do
    mysql -u root --password=admin cs121 < $SQL;
done