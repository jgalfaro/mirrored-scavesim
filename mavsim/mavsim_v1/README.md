# MAVSIM

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

[MAVSIM v1, test01](http://youtu.be/ePn1VMsyFHw)

[MAVSIM v1, test02](http://youtu.be/dT-giOF35LM)

[MAVSIM v1, test03](http://youtu.be/CuY3zvl-bKY)


Further details available at the [Main Website of MAVSIM](http://www-public.imtbs-tsp.eu/~garcia_a/web/prototypes/mavsim/)
