# SparqlBean
Java Interfaces and utility classes to handle sparql interaction with the underlying RDF triplestore.

# Background
When it comes to data persistence, there has always been a discussion regarding two methods: object-relational mapping (object-table synchronization) and embedding the query language directly into the source code as semi-configuration files.
The most popular projects between these two methodologies are hibernate for OR mapping and ibatis (now [mybatis](https://github.com/mybatis/mybatis-3)) for SQL configuration.
One of the goals for these "persistence layers" was the possibility to completely change the underlying data store vendor with a simple configuration change.  In reality, this can prove to be very difficult as this forces OR mapping projects to support all of the various vendors and their slight tweaks to the SQL language.
With ibatis, it was acknowledged that this is not reasonable, and so having direct access to the SQL can clearly show what modifications are needed.  This in itself is quite a burden.

By having RDF and Sparql standardized, the vendor-lock in issue is no longer.  Despite this, there is still the possibility of vendors making slight tweaks for performance sake, or to cover specific functionality that is not clearly specified by W3C.

# Mission
This project follows the ibatis methodology for RDF datastores.  This provides flexibility and freedom to use the entirety of the sparql language, the same way ibatis utilized SQL.  It is based upon the spring framework, in order to provide inversion of control for RDF triplestore vendor specific details such as JDBC drivers or HTTP server for REST.

This framework should not cause roadblocks between what the application developer needs versus the data store.  It is also usable as a tool to provide agility to the developer to switch out quickly RDF triplestores.

# Compatibility
Since SPARQL is a W3C standardized language, it is safe to assume that the code based on this project will work for any vendor.  Obviously it is the users responsibility to document if there is non-standardized Sparql being used and that how it is not re-usable for all RDF triplestores.


# Credit
Initially this development was funded by the Integrated Database Project by MEXT (Ministry of Education, Culture, Sports, Science & Technology) and the Program for Coordination Toward Integration of Related Databases by JST (Japan Science and Technology Agency) as part of the [International Glycan Repository project](http://www.glytoucan.org).

# Support Sponsorship
Development and support has continued under [Sparqlite LLC](http://sparqlite.com)

# run test cases
assuming the aokinobu/docker-virtuoso instance is running with default network, it is possible to run the test cases for this project to connect to the virtuoso server, and run sparql queries, test inserts, all under transaction and rollback.

assuming docker-compose with support for v3 yml files, the following will execute all tests:
```
docker-compose run --rm maven test
 ```
