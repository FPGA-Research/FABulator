# Structure of this project

### Software structure
The software is structured by components and their task.
For example, there is a package for builder classes,
for classes representing parts of the fabrics geometry
and so on...

The geometry is structured hierarchically: 
The Fabric object holds tiles, Tile objects hold switch 
matrices, ...

### Package descriptions
The following contains a list of the projects packages 
and a description of their contents.

| Package  | description                                                                                                     |
|----------|-----------------------------------------------------------------------------------------------------------------|
| async    | contains classes handling async operations                                                                      |
| geometry | contains classes specifying the geometry of (parts of) the fabric. These are parsed from the geometry csv file. |
| language | contains classes for handling language and text related tasks                                                   |
| lookup   | contains data structures used for looking up information about the fabric                                       |
| memory   | contains classes concerned with memory management, will be expanded in the future                               |
| parse    | contains classes concerned with parsing information from files                                                  |
| settings | contains classes loading/displaying/persisting settings                                                         |
| ui       | contains classes representing UI related objects, such as views, menus,...                                      |
| util     | contains util classes                                                                                           |


### Within the ui package
The following contains a list of the packages just within the ui package

| Package | description                                                                                                      |
|---------|------------------------------------------------------------------------------------------------------------------|
| builder | contains builder classes                                                                                         |
| fabric  | contains classes that represent (parts of) the rendered fabric                                                   |
| icon    | contains classes concerned with loading/providing icons                                                          |
| menu    | contains classes specifying a UI menu                                                                            |
| style   | contains classes loading/managing UI styling                                                                     |
| view    | contains classes representing a UI view. Sometimes, the line between this package and the menu package is blurry |
| window  | contains classes representing a UI window                                                                        |


