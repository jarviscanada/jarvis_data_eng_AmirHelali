# Java-Grep App

This is a Java app that works like the grep command-line utility. It allows for pattern searching within text files and creating an output file that stores the lines matched by the search pattern.
This app was developed using the following tools and technologies: core Java, Maven, Docker, slf4j, IntelliJ Idea, lambdas and streams.

# Quick Start

```
# Usage
mvn clean compile package
java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.JavaGrepImplement [Regex pattern] [Input file/directory] [Output-file]

#example
cd core_java/grep
mvn clean compile package
java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.JavaGrepImplement .*Romeo.*Juliet.* ./data ./out/grep.txt
```

# Implementation
## Pseudocode
```Java
matchedLines = []
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)
writeToFile(matchedLines)
```
**Note:** The original implementation was updated to utilize streams and lambdas.

# Test

Testing was done manually by preparing sample data and running various test cases and comparing the results to make sure they were correct and as intended.
It also made sure that exception/error handling was done correctly.

# Deployment

The application was dockerized to ensure easy deployment and distribution. Docker file was created based on openjdk8-alpine. A docker image was created and verified locally before running a docker container to make sure everything worked accordingly. Finally the image was pushed to [Docker Hub](https://hub.docker.com/r/ahelali/grep).

# Improvements
- Automate testing.
- Improve exception handling.
- Improve memory efficiency.