drop database if exists cs121;

create database cs121;

use cs121;

create table unigrams (
  word varchar(51),
  docId varchar(6),
  tf_idf double,
  cos_score double,
  freq int,
  phase varchar(400),
  title_tag int,
  h1_tag int,
  h2_tag int,
  h3_tag int,
  b_tag int,
  strong_tag int,
  else_tag int,

  primary key (word, docId)
);

create index word_index on unigrams(word);

create index word_docId_index on unigrams(word, docId);