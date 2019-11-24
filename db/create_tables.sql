-- EDGAR database Financial Statements Data Sets tables as defined in
-- https://www.sec.gov/dera/data/financial-statement-data-sets.html
-- https://www.sec.gov/files/aqfs.pdf

-- IDEA: unique key specifications for each table as presented in
-- https://www.sec.gov/files/aqfs.pdf might be a good key template
-- to employ when storing data in a memory cache.

-- TODOS:
-- Test that PRIMARY KEY enforces uniqueness of records
-- Load data and test

-- Create tables in the order in which they appear.

-- Each record represents an XBRL submission
CREATE TABLE submissions (
       adsh varchar(20) PRIMARY KEY,
       cik  integer NOT NULL,
       name varchar(150) NOT NULL,
       sic integer,
       countryba varchar(2),
       cityba varchar(30) NOT NULL,
       zipba varchar(10),
       bas1 varchar(40),
       bas2 varchar(40),
       baph varchar(12),
       countryma varchar(2),
       stprma varchar(2),
       cityma varchar(30),
       zipma varchar(10),
       mas1 varchar(40),
       mas2 varchar(40),
       countryinc varchar(3) NOT NULL,
       stprinc varchar(2),
       ein integer,
       former varchar(150),
       afs varchar(5),
       wksi boolean NOT NULL,
       fye varchar(4) NOT NULL,	-- mmdd date => Will have to store as varchar
       form varchar(10) NOT NULL,
       period date NOT NULL,	-- yymmdd => Tested
       fy varchar(4) NOT NULL,	-- yyyy => Will have to store as varchar
       fp varchar(2) NOT NULL,
       filed date NOT NULL,	-- yymmdd => Tested
       accepted timestamp NOT NULL, -- yyyy-mm-dd hh:mm:ss => Tested
       prevrpt boolean NOT NULL,
       detail boolean NOT NULL,
       instance varchar(32) NOT NULL,
       nciks integer NOT NULL,
       aciks varchar(120)
);

-- Based on predicted use case
-- CREATE UNIQUE INDEX FOR submissions ON (cik, adsh) 

-- Each record represents the description of any of the tags employed in line items
-- of submitted statements.
CREATE TABLE tags (
       tag varchar(256) NOT NULL,
       version varchar(20) NOT NULL,
       custom boolean NOT NULL,
       abstract boolean NOT NULL,
       datatype varchar(20),
       iord varchar(1) NOT NULL,
       crdr varchar(1),
       tlabel varchar(512),
       doc varchar(2048),
       PRIMARY KEY(tag, version)
);

-- Each record represents a line item for each of the statements of each submission.
-- This stable stores the values for each item in each submitted report.
CREATE TABLE numbers (
       adsh varchar(20) NOT NULL REFERENCES submissions (adsh),
       tag varchar(256) NOT NULL REFERENCES tags (tag),
       version varchar(20) NOT NULL REFERENCES tags (version),
       coreg varchar(256),
       ddate date NOT NULL,	-- yyyymmdd => Tested
       qtrs integer NOT NULL,
       uom varchar(20) NOT NULL,
       value numeric(32, 4),
       footnote varchar(512),
       PRIMARY KEY(adsh, tag, version, ddate, qtrs, coreg, uom) -- Give some thought to the order of the columns in this key
);

-- Each record represents the text, tag and order assigned to each line
-- item in a statement.
-- QUESTION: Does it make sense to chain foreign references?
-- eg: adsh REFERENCES numbers (adsh) instead of submissions (adsh)
CREATE TABLE presentations (
       adsh varchar(20) NOT NULL REFERENCES numbers (adsh),
       report integer NOT NULL,
       line integer NOT NULL,
       stmt varchar(2) NOT NULL,
       inpth boolean NOT NULL,
       rfile varchar(1) NOT NULL,
       tag varchar(256) NOT NULL REFERENCES numbers (tag),
       version varchar(20) NOT NULL REFERENCES numbers (version),
       plabel varchar(512) NOT NULL,
       PRIMARY KEY(adsh, report, line)
);

