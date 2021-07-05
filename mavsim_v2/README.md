# Error Tolerant Path Planning for Swarms of Micro Aerial Vehicles with Quality Amplification


### Michel Barbeau, Carleton University, School of Computer Science, Canada.

### Joaquin Garcia-Alfaro, Institut Polytechnique de Paris, Telecom SudParis, France.

### Evangelos Kranakis, Carleton University, School of Computer Science, Canada.

### Fillipe Santos, University of Campinas, Brazil.


# MAVSIM v2 JAVA Simulator

Java Simulator to explore planning algorithms for [Micro Aerial
Vehicles (MAVs)](https://en.wikipedia.org/wiki/Micro_air_vehicle) and
[MAV swarms](href="https://en.wikipedia.org/wiki/Swarm_behaviour).
We assume a MAV navigation system without relying on [GPS-like
techniques](https://en.wikipedia.org/wiki/Global_Positioning_System).
The MAVs find their navigation path by using their
sensors and cameras, in order to identify and follow a series of [visual
landmarks](https://en.wikipedia.org/wiki/Landmark). The visual landmarks lead
the MAVs towards the target destination.
MAVs are assumed to be unaware of the terrain and locations of the
landmarks. Landmarks are also assumed to hold a-priori information,
whose interpretation (by the MAVs) is prone to errors. We distinguish
two types of errors, namely, [recognition and advice](https://en.wikipedia.org/wiki/Error#Science_and_engineering).
Recognition errors are due to misinterpretation of
sensed data and a-priori information or confusion of objects (e.g.,
due to faulty sensors). Advice errors are due to outdated or wrong
information associated to the landmarks (e.g., due to weather
conditions). Our path planning algorithm proposes swarm cooperation.
MAVs communicate and exchange information wirelessly, to minimize the
recognition and advice error ratios. By doing this, the [navigation
system](https://en.wikipedia.org/wiki/Navigation_system) experiences a
quality amplification in terms of error
reduction. As a result, our solution successfully provides an adaptive
error tolerant navigation system. Quality amplification is
parametetrized with regard to the number of MAVs. We validate our
approach with theoretical proofs and numeric simulations, using a <a
href= "https://github.com/jgalfaro/mirrored-scavesim">Java
simulator</a>. Our simulations explore the complexity and impact of
each of those communication models in terms of [swarm
intelligence](https://en.wikipedia.org/wiki/Swarm_intelligence).

Some videocaptures about the different releases follow:

[![Watch the video](https://img.youtube.com/vi/_Ea2ci3XnXI/0.jpg)](https://youtu.be/_Ea2ci3XnXI)
[![Watch the video](https://img.youtube.com/vi/CuY3zvl-bKY/0.jpg)](https://youtu.be/CuY3zvl-bKY)

[![Watch the video](https://img.youtube.com/vi/_F7AbmR-QDM/0.jpg)](https://youtu.be/_F7AbmR-QDM)
[![Watch the video](https://img.youtube.com/vi/ePn1VMsyFHw/0.jpg)](https://youtu.be/ePn1VMsyFHw)


# Source code

[java/src](https://github.com/jgalfaro/mirrored-scavesim/tree/main/mavsim_v2/java/src)


# Building & executing the simulator

```bash
ant clean  && ant build
```

# Sample executions

```bash
ant run -Dagents=1 -Dedges="edges_carleton.txt" -Dvertices="vertices_carleton.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=100 -Dpath="path-carleton.txt"
ant run -Dagents=1 -Dedges="edges_unicamp.txt" -Dvertices="vertices_unicamp.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=30 -Dpath="path-unicamp.txt"
ant run -Dagents=1 -Dedges="edges_paris.txt" -Dvertices="vertices_paris.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=50 -Dpath="path-paris.txt"
ant run -Dagents=1 -Dedges="edges_grid10.txt" -Dvertices="vertices_grid10.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=10 -Dpath="path-grid10.txt"
ant run -Dagents=1 -Dedges="edges_grid25.txt" -Dvertices="vertices_grid25.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=20 -Dpath="path-grid25.txt"
```

# Node colors

- Blue node: starting node.
- Red node: arrival node.
- Yellow node: landmark to cross (the drones must cross the yellow landmark -DminLandmarks).
- Green node: landmark (the drones may or not cross these green landmarks).


# Further details

[MAVSIM Website](http://www-public.imtbs-tsp.eu/~garcia_a/web/prototypes/mavsim/)

## References

If using this code for research purposes, please cite:

[1] M. Barbeau, J. Garcia-Alfaro, E. Kranakis, F. Santos. "Error Tolerant Path Planning for Swarms of Micro Aerial Vehicles with Quality Amplification", June 2021. 

[2] M. Barbeau, J. Garcia-Alfaro, E. Kranakis, F. Santos, "Quality Amplification of Error Prone Navigation for Swarms of Micro Aerial Vehicles," IEEE GLOBECOM 2019 Workshop on Computing-Centric Drone Networks 2019 (CCDN 2019), December 2019. 

```
@techreport{Arxiv1906.09505,
  title={Error Tolerant Path Planning for Swarms of Micro Aerial Vehicles with Quality Amplification},
  author={Barbeau, Michel and Garcia-Alfaro, Joaquin and Kranakis, Evangelos and Santos, Fillipe},
  year={June 2021},
  url={https://arxiv.org/abs/1906.09505},
  eprint={1906.09505},
  archivePrefix={arXiv},
  primaryClass={cs.RO}
}

@inproceedings{GC19WkshpBarbeauGarciaKranakisSantos,
  title={Quality Amplification of Error Prone Navigation for Swarms of Micro Aerial Vehicles},
  author={Barbeau, Michel and Garcia-Alfaro, Joaquin and Kranakis, Evangelos and Santos, Fillipe},
  booktitle={2019 IEEE Globecom Workshops (GC Wkshps)},
  pages={1--6},
  year={2019},
  organization={IEEE},
  doi={10.1109/GCWkshps45667.2019.9024394},
  url={https://doi.org/10.1109/GCWkshps45667.2019.9024394},
}
```



