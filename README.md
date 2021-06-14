# AAU - Clean Code - Webcrawler Project
[![Java Gradle Test CI](https://github.com/craumix/aau-cc-webcrawler/actions/workflows/gradle-tests.yaml/badge.svg)](https://github.com/craumix/aau-cc-webcrawler/actions/workflows/gradle-tests.yaml)

This is a Project for the Course "Clean Code" in the Summer Semester of 2021 at the [University of Klagenfurt](https://www.aau.at/en/). The Goal for this Project is to implement a Web-Crawler that has **at least** the following features:

- Input the URL as command line argument
- Analyze the page content of the provided URL and output:
  - The number of words, links, images and videos contained by that page
  - Optionally, you can provide more statistics if you wish so
- Find the links to other websites and recursively do the analysis for those websites (it is enough if you analyze the pages at a depth of 2 without visiting further links, you might also allow the user to configure this depth via command line)
- Find broken links (404 Errors)
- Report the results from above in a simple report stored to a .txt file or output to the console (or in some other way, if you wish)

<hr>

*Note: This Project is targeted towards the JDK 15*

Exection Examples:  
`./gradlew run --args="-h"`  
To display help for available arguments 

`./gradlew run --args="-u github.com"`  
To run with default parameters on http://github.com   

`./gradlew run --args="-u https://github.com -d 3 -t 4 -s -o results.txt"`  
To run on https://github.com with:
- Crawling-Depth **-d** of 3
- 4 Threads **-t**
- Omitting duplicates **-s**
- Outputing **-o** to results.txt

<hr>

Running Unit Tests:
`./gradlew clean test`

<hr>

Building a runnable Jar:  
`./gradlew jar`

<hr>

**Please Note:**
Running with a large depth (probably something > 4) and many Threads can lead to a DOS like amount of request and may get you blacklisted.
