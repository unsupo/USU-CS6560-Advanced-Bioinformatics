This is the home directory for all assignments for Advanced Bioinformatics at Utah State University.  The course is actually CS5560 and CS6560 (the assignments are for both, but my project for CS6560 is included as well)

Since this is the first assignment i'll work under the assumption that you have access to maven.  Otherwise its a free download of small size.

simply run the following command in this directory (the one with the pom.xml file)
mvn clean install

this will download all the associated jar files for this project.

If you don't wish to use maven then that's fine, the main algorithm doesn't need it.

All the associated files for this and future assignments will be located in:
src/main/resources/assignment*

and all classes in
src/main/java/assignment*

all test classes in
src/test/java/assignment*
