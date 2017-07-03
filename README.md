# JenaUtils

Set of utility classes for all jena-based projects

- BaseRDF handle the generation of select query Execution with remote or local bases
- QueryResultIterator allows to query a base with SELECT queries. It add a LIMIT to the query and move the result windows as it iterate.
- UtilOntology extract basic ontological informations such as list of properties, list of classes, basic hierarchies, etc. 
- Utils contains the list of URIs used in RDFS ans OWL.