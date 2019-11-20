-- EDGAR database Financial Statements Data Sets tables as defined in
-- https://www.sec.gov/dera/data/financial-statement-data-sets.html
-- https://www.sec.gov/files/aqfs.pdf

-- IDEA: unique key specifications for each table as presented in
-- https://www.sec.gov/files/aqfs.pdf might be a good key template
-- to employ when storing data in a memory cache.

-- TODOS:
-- Test if expected date inputs work with date fields. Adjust if necessary.
-- Define foreign keys on tables.
-- Define on what fields will each table be indexed.

-- Each record represents an XBRL submission
CREATE TABLE submissions (
       adsh varchar(20) NOT NULL,
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
       fye varchar(4) NOT NULL,	-- mmdd date => is there a better data type for this?
       form varchar(10) NOT NULL,
       period date NOT NULL,	-- yymmdd
       fy varchar(4) NOT NULL,	-- yyyy => find how to store only year in a date/time type
       fp varchar(2) NOT NULL,
       filed date NOL NULL,	-- yymmdd
       accepted timestamp NOT NULL, -- yyyy-mm-dd hh:mm:ss
       prevrpt boolean NOT NULL,
       detail boolean NOT NULL,
       instance varchar(32) NOT NULL,
       nciks integer NOT NULL,
       aciks varchar(120)
);

-- Each record represents a line item for each of the statements of each submission.
-- This stable stores the values for each item in each submitted report.
CREATE TABLE numbers (
       adsh varchar(20) NOT NULL,
       tag varchar(256) NOT NULL,
       version varchar(20) NOT NULL,
       coreg varchar(256),
       ddate date NOT NULL,	-- yyyymmdd
       qtrs integer NOT NULL,
       uom varchar(20) NOT NULL,
       value numeric(32, 4),
       footnote varchar(512)
);

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
       doc varchar(2048)
);

-- Each record represents the text, tag and order assigned to each line
-- item in a statement.
CREATE TABLE presentations (
       adsh varchar(20) NOT NULL,
       report integer NOT NULL,
       line integer NOT NULL,
       stmt varchar(2) NOT NULL,
       inpth boolean NOT NULL,
       rfile varchar(1) NOT NULL,
       tag varchar(256) NOT NULL,
       version varchar(20) NOT NULL,
       plabel varchar(512) NOT NULL
);

