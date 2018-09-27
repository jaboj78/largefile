Large file utilities 
====================
A few Java programs to handle large files.

The programs are developed with a specific task in mind - to fix invalid characters in a 50GB large xml file with approx. 3.055.336.991 lines.

In the absence of a better solution, the problem was solved completing these tasks.

  * Validate the large XML fil using SaxParseToValidate.java
  * Split large XML file into 128 MB file chunks FileSplit.java
  * Create a line index for each part using PartsLineIndex.java
  * Concatenate all parts together again using FileConcat.java
   
By use of the generate line index, a sax parse error line number,can be mapped to a specific part and line number within this part, by use of this formula: [error lineno]-[part start lineno]+1

In the specific case, multiple x1F bytes caused the troble. Ex. the error in line 320.495.609 was corrected by  removing the byte from 6.289.775 in part 040 og 380 parts :-)

