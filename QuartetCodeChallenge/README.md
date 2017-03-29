This is my attempt at the data platform challenge. 

USAGE:
java -jar QuartetCodeChallenge_jar/QuartetCodeChallenge.jar --job job [--source url]
OPTIONS:
job: [ Initial | Nightly | Matching ]
url: the url of the json file

IMPLEMENTATION NOTES:
Each job is in its own class, implements the Job interface.
I don't have a database connection, so I implemented stub endpoints that the
jobs talk to.
I assumed that in real life there's a data service layer that handles
communication with Quartet's data stores.

The stubs are backed by JSON files. A limitation of the stub is that it doesn't
store the data offline unless you tell it to write at the end of the jobs.
