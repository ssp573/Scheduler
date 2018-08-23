# Scheduler

There are 5 files in this repository. One is for the class Process. An object of the class Process is created for every Process in the input. The class Scheduler uses these processes and runs 4 functions; one for each type of scheduler. The other 3 classes are SortbyArrival, SortbyIndex and SortbyC. These are implementing Comparator to help compare ArrayLists of Processes by Arrival time, by index and by CPU time left respectively in the class Scheduler. To run follow the following commands-\
compile: \
javac Scheduler.java

run:\
For normal output-\
java Scheduler <filename>\
(Note that the file must be in the current working\
directory. If not, give full path.)\
For detailed output-\
java Scheduler --verbose <filename>

(Note that the file must be in the current working directory. If not, give full path.)\

Note: I have used the name "random-numbers" for the random numbers file. Please make sure that you have the same name and the file is in the current working directory.
