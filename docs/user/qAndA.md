# Q and A

### What System Requirements should be met?
It is advised to run this application on a machine
that has access to at least 16GB of RAM since the
application stores the geometry of the whole of the
fabric in RAM. Eventually, this will be updated.

In order to make sure RAM can be reused for freshly 
loaded fabrics, the old fabric is deleted (or rather,
references to it are dropped) before the new one is
loaded into RAM.


### Why was this project written in Java?
... wouldn't have Qt been a better choice?
Maybe. However, it mostly boils down to two major points.
First is experience with this framework (JavaFX). This 
point should not be underestimated.

The second point is ease of use. Under the hood, this framework
supports hardware acceleration. However, this is completely 
transparent to the software developer and leads to a fairly 
good performance - so the impact of using Java instead of C++ 
might not be as noticeable as first expected. There also is a software-rendering fallback,
if there is no hardware acceleration accessible. This has been
tested and is not as unfeasible as it may sound.

The third point is simple: I just happen to enjoy using JavaFX.


### Where is this project going?
Right now, we offer displaying auto-generated views of
fabrics generated with the FABulous framework:

https://github.com/FPGA-Research-Manchester/FABulous

Also, user designs can be displayed and exploring the 
architecture is supported by several utilities (See: howToUse.md).
Eventually, editing both on an architectural and a user design
level will be supported.

