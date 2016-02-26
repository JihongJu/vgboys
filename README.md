# vgboys

This is the controller for [GVG-AI Competition](http://www.gvgai.net/) designed by [Jihong Ju](https://github.com/JihongJu), [Santor Warmerdam](https://github.com/santorwarmerdam), [Boyang Tang](https://github.com/Temila) and [Bo Wang](https://github.com/bwanglzu) under the framework of [gvgai](https://github.com/EssexUniversityMCTS/gvgai).

## Troubleshooting

1. A well known error running is windows is 
```Overspent: (exceeding 6ms): applying ACTION_NIL```
A solution to that is to use WALL_TIME instead of CPU_TIME in tools.ElapsedCpuTimer.java

2. The structure of src/ folder is 
```groovy
- controllers
- core
- levelGenerators
- ontology
- tools
- vgboys
	|- Agent.java
	|- MyAdditionalFile1.java
	|- MyAdditionalFile2.java
- LevelGeneration.java
- Test.java
```
