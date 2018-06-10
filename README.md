Instructions for running and creating the application fat jar.
1.	Download and install gradle.
2.	Checkout project from git hub location.
3.	Open gitbash / terminal and cd into directory where source is located.
4.	run “gradle clean build bootJar” which will build the fat jar file. 
5.	Cd in build/libs and run the following command :

java -jar sainsbury-site-scraper-test-0.1.0.jar
6.	The output from running the will be displayed on the page. 

Run Test.   
1.	To run the tests on their own run the following :

gradle test – when in the root directory of the project. 

Technologies used to create/develop the console application.
Gradle - industry standard build tool 
Eclipse - Industry standard development tool 
JSOUP - Java library for extracting html 
Spring Boot - Industry standard framework amalgamating Spring MVC / Security / etc. Also provides means for generating jar file via gradle. 
Jackson - Standard Java lbrary for generating json. 
Mokito  -  Industry standard mocking framework .
Powermock - Industry standard framework used to mock static classes where mokito on it's own cannot mock.
Log4J - Added, but not used. 

Other Notes. 
Due the tight timescales I have omitted to add logging and I would normally not return a json string to the console directly. 
This should ideally be wrapped with a wrapper class so that exceptions can also be handled easily, and returned in the json response.  
  
I would also refactor the tests, and add in additional test to ensure that other criteria is tested fully.
