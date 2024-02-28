# How To Use

### Display fabrics
after having successfully generated a geometry csv file
with the [FABulous project](https://github.com/FPGA-Research-Manchester/FABulous),
you're ready to import it into the UI:

Go to  `File > Open`

and select the *geometry* csv file of the fabric you want to display.

CAREFUL: Be sure *not* to select the *fabric* csv file. The geometry csv file
has to be created explicitly, which can be done in the FABulous Shell with the commands

```
load_fabric
gen_fabric      # only needed once to generate matrix.csv files
gen_geometry
```

This will create a ```<fabric_name>_geometry.csv``` file.

Alternatively, the whole FABulous flow can be triggered by

```
load_fabric
run_FABulous_fabric
```

This will create the same ```<fabric_name>_geometry.csv``` file.

### Display user designs
after having generated a fasm file for a user design, you're
ready to import it into the UI:

Go to  `File > Select FASM`

and select the fasm file for the user design you want to display.
Nets can be selected in the `Netlist` Tab.

DISCLAIMER: Be aware that this feature is still experimental.

### Searching and Navigation
You can search for elements of the fabric by using the search menu
at the bottom. By the way, you can also enter regular expressions
there. For more information about the format of accepted regular 
expressions, refer to [Oracle Docs](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#:~:text=Summary%20of%20regular%2Dexpression%20constructs).

You can also navigate through the fabric in the `Fabric Elements`
Tab.

### Explore HDL
By clicking on elements which are specified by HDL code (like bels), you 
can explore their respective HDL code in the `HDL Code` Tab.

### View programmable connections in a switch matrix
Clicking on a port of a switch matrix will show
its programmable connections. Be sure to properly
click on the port itself, not the wire on top.
Also note that the matrix.csv files must be generated
for this feature.
This is done in the FABulous shell (see above).

### Highlight wires
By right-clicking a wire, a color to highlight
it with can be selected.
Be aware that this is not available for the 
programmable connections inside a switch matrix.