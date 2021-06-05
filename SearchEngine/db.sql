use SearchEngine 
Drop TABLE  DocumentsTable;
Drop TABLE  KeywordsInDocTable;
Drop TABLE  Keywords;
CREATE TABLE DocumentsTable
(
	doc_url varchar(500) NOT NULL,
	doc_description varchar(max) NULL,
	word_count int NOT NULL,
	PRIMARY KEY (doc_url)
);
CREATE TABLE KeywordsInDocTable
(
	keyword varchar(250) NOT NULL,
	doc_url varchar(250) NOT NULL,
	term_freq int NOT NULL,
	score int NOT NULL,
	PRIMARY KEY (doc_url, keyword),
);
CREATE TABLE Keywords
(
	keyword varchar(250) NOT NULL,
	idf int NOT NULL,
	PRIMARY KEY (keyword)
);