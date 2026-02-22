# Sorting Comparison (Maven)

This project was converted into a Maven project. It benchmarks Quick Sort vs Merge Sort and displays a chart using JFreeChart.

How to build and run:

```bash
mvn compile
mvn exec:java -Dexec.mainClass=SortingComparisonGraph
```

If you prefer to create an executable jar:

```bash
mvn package
java -cp target/sorting-comparison-1.0-SNAPSHOT.jar;path-to-jfreechart.jar SortingComparisonGraph
```
