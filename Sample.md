# Introduction #

All samples are in the Folder ${install\_dir}/jss/samples

The samples illustrate the usage of jss as well as the usage of the jsslib which is included in this project. The jsslib is a Java library of useful functions in the context of shell scripts (but not limited to shell scripts).

All samples will display a short description if you start them without any arguments.


# xls2txt #
> ### Convert Excel files to Textfiles. ###
> This sample use jsslib.excel which makes use of [Apache POI Project](http://poi.apache.org)

> ## Usage ##
```
./xls2txt sourcefile.xls destinationfile.txt [...OPTIONS]
```
> ## Options ##
```
-format x     Formating the textfile:
                x = Integer    -> fixed column width
                x = "\;"       -> separator ;
                x = ,          -> separator ,
                x = tab        -> separator tabulator
```