# Known Bugs

All Bugs are planned to be resolved

### Routing in Termination and Supertiles is not lining up

This was not actually a bug of FABulator, but
a bug of the geometry_generator in FABulous.
It was resolved for all tested fabrics by a fix. 
However, fabrics with larger supertiles of
at least width or height 3 will need to be 
tested.

### Fasm is not displayed correctly

The bug has been resolved by a fix for all tested fabrics.
Further testing will need to be done to make sure of it 
being fully resolved.

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
<options>
    <option>-Xmx8g</option>
</options>
```
in the pom.xml file right after the line containing
```<mainClass>FABulator/fabulator.FABulator</mainClass>```
in the javafx-maven-plugin section.

To check if the error is resolved, run the application
as usual with ```mvn javafx:run```