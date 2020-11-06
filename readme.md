# profiler

**_TLDR:_** company financial profile building project based on [SEC's Financial Statements Data Sets](https://www.sec.gov/dera/data/financial-statement-data-sets.html).

## Description

This project depends and builds upon the related project [profiler-content](https://github.com/humbertotm/profiler-content). 
Once the PostgreSQL database has been initialized and populated by the aforementioned project, you're all set to execute the main profiling task in this project.

Output consists of a collection of maps containing a set of financial ratios and measures for a specific company as denoted by **cik** and a specific year. 

Data employed to calculate these comes from the **NUM** dataset as described in [section 5.3 -- NUM](https://www.sec.gov/files/aqfs.pdf) of the _Financial Statement Data Sets_ spec.

Of particular interest for this purpose are those `num` records for **10-K** forms (annual reports).

Output profile maps are persisted to a MongoDB instance with the following document structure:

```javascript
{
	"_id": "ObjectId(...)",  // ObjectId as assigned by Mongo
	"cik": "somecik",        // Must be present
	"ticker": "abc",         // If there's a mapping for cik
	"year": 2019,
	"profile": {
		"ratio_0": 1234.00,
		"ratio_1": 2345.20,
		...
	}
}
```

## How to run

The project is fully containerized. It requires three containers up and running (or its own instances if you decide to go without `docker`):
  * Clojure - Leiningen app container
  * PostgreSQL database container with source data previously populated
  * MongoDB database container where output profiles will be persisted

#### Requirements
If you want to execute using `docker`, all you need is having docker installed and you're good to go.

Otherwise, you will need to install the following:
  * Clojure
  * Leiningen
  * PostgreSQL
  * MongoDB
  
After setting up Postgres and Mongo on your own, make sure to modify the `:env` map in the `profiles.clj` files to provide the proper data to establish a connection with both databases. 

#### Prerequisites

1. Create and populate the input PostgreSQL database. See [profiler-content](https://github.com/humbertotm/profiler-content). Creating the data volume employed by this database is addressed in that project.
2. Create the data volume to be employed by the MongoDB container to store output data with:
```shell
$ docker volume create screener-mongo-data
```
3. If this has task has been executed before, cleanup the MongoDB instance with the following commands:
```shell
# Log into the running mongodb container
$ docker exec -it screener-mongodb /bin/bash
# Once in the container, log into the database instance
$ mongo -u mongoadmin -p mongoadmin
# Once logged into the mongodb instance, set the appropriate database
> use profiler
# Delete all profiles collection records
> db.profiles.remove({})
```

#### Execution

There are two ways in which this can be done

1. Log into `profiler-lein` container and execute the command manually
   1. Initialize the containers with
   ```shell
   $ docker-compose up -d
   ```
   2.
   ```shell
   # Log into the running leiningen app container
   $ docker exec -it profiler-lein /bin/bash
   # Execute the task
   $ lein run
   ```

2. Modify the Leiningen app `Dockerfile` to execute full task upon container initialization
   1. Replace the last line
   ```
   CMD ["lein", "repl", ":headless", ":host", "0.0.0.0", ":port", "36096"]
   ```
   with
   ```
   CMD ["lein", "run"]
   ```
   2. Initialize the containers
   ```shell
   $ docker-compose up -d
   ```


## Reexecuting
See **_Prerequisites_**.




## Tips
  * If your IDE supports an nREPL client, you can hook up to a Leiningen REPL process running on `0.0.0.0:36096` within the container.
  
## Testing
Execute `lein test` within the `profiler-lein` container for the full test suite.

## TODOS
  * Add support to execute full profiling process within a range of years
  ```shell
  # Fully delimited range
  $ lein run --start=2009 --end=2020
  ```
  
  ```shell
  # Open ended only with start specified
  $ lein run --start=2009
  ```
  
  ```shell
  # Open ended only with end specified
  $ lein run --end=2020
  ```
  In both open ended cases, the open end limit would be the first/last year of data available in the input database.

