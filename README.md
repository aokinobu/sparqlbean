# SparqlBean
Java Interfaces and utility classes to handle sparql interaction with the underlying RDF triplestore.

# Background
The separation of sparql from object mapping methods is to prevent issues that may arise from the third-party mapping methods.  This provides flexibility and freedom to use specialized sparql even though it is a standardized language.  The framework should not cause roadblocks between what the application developer needs versus the data store.  It should also make full use of sparql standardization by having the ability to completely change the underlying RDF data store vendor.


# Credit
Initially this development was funded by the Integrated Database Project by MEXT (Ministry of Education, Culture, Sports, Science & Technology) and the Program for Coordination Toward Integration of Related Databases by JST (Japan Science and Technology Agency) as part of the [International Glycan Repository project](http://www.glytoucan.org).

# Support Sponsorship
Development and support has continued under Sparqlite LLC 