# Error Tolerant Path Planning for Swarms of Micro Aerial Vehicles with Quality Amplification [github repository]

===

### Michel Barbeau, Carleton University, School of Computer Science, Canada.

### Joaquin Garcia-Alfaro, Institut Polytechnique de Paris, Telecom SudParis, France.

### Evangelos Kranakis, Carleton University, School of Computer Science, Canada.

### Fillipe Santos, University of Campinas, Brazil.


## Abstract

We present an error tolerant path planning algorithm for Micro Aerial
Vehicle (MAV) swarms. We assume navigation without GPS-like
techniques. The MAVs find their path using sensors and cameras,
identifying and following a series of visual landmarks. The visual
landmarks lead the MAVs towards their destination. MAVs are assumed to
be unaware of the terrain and locations of the landmarks. They hold
a-priori information about landmarks, whose interpretation is prone to
errors. Errors are of two types, *recognition* or *advice*.
Recognition errors follow from misinterpretation of sensed data or a
priori information, or confusion of objects, e.g., due to faulty
sensors. Advice errors are consequences of outdated or wrong
information about landmarks, e.g., due to weather conditions. Our path
planning algorithm is cooperative. MAVs communicate and exchange
information wirelessly, to minimize the number of *recognition* and
*advice* errors. Hence, the quality of the navigation decision process
is amplified. Our solution successfully achieves an adaptive error
tolerant navigation system. Quality amplification is parametetrized
with respect to the number of MAVs. We validate our approach with
theoretical proofs and numeric simulations




