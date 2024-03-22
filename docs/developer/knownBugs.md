# Known Bugs

All Bugs are planned to be resolved

### Routing in Termination and Supertiles is not lining up

This was not actually a bug of FABulator, but
a bug of the geometry_generator in FABulous.
It was resolved for all tested fabrics, 
so be sure to use the most recent version of
FABulous to generate the geometry file.

### Fasm is not displayed correctly

The bug has been resolved for all tested fabrics,
so be sure to use the most recent version of
FABulous to generate the geometry file and use the
most recent version of FABulator to display
fasm files.

### OutOfMemoryError is thrown when loading a fabric

This can happen because the whole (geometric)
information of a fabric is held in RAM at all
times (even when parts are not currently needed).
This will be changed in the future, as it is very
restricting on the size of importable fabrics.
If your machine has enough RAM, it can be resolved 
by increasing the JVM heap size. 

To increase the heap size to, say, 8 Gigabytes, one
can add

```
<option>-Xmx8g</option>
```
in the pom.xml file in the `<options>` section
within the javafx-maven-plugin section.

To check if the error is resolved, run the application
as usual with ```mvn javafx:run```