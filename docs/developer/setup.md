# Setup

### User Setup
To build and run FABulator:

```
mvn javafx:run
```

**Or alternatively:**

Build an executable jar:
```
mvn clean package
```
And run with
```
java -jar <output_jar>
```

### Development Setup in IntelliJ

to set up this project in IntelliJ, open a new
project from Version Control (github) and checkout
the correct branch. 
Then, add a new Run Configuration in IntelliJ with
the following settings:
- SDK: Java 17 SDK
- Main Class: fabulator.FABulator

The Development Setup is now completed. Pressing Run
should start the Application as usual.