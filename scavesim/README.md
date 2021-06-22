# SCAVESIM-MAVSIM

Study of topological structures and their impact on search problems driven by [mobile agents](https://en.wikipedia.org/wiki/Mobile_agent). Mobile agents can have direct communication (e.g., [peer-to-peer](https://en.wikipedia.org/wiki/Peer-to-peer) or controller-driven communication) or [indirect communication](https://en.wikipedia.org/wiki/Decentralised_system). Inter-agent communication mechanism, using [whiteboards](https://en.wikipedia.org/wiki/Decentralised_system#Artificial_intelligence_and_robotics), is also explored. When using the whiteboard model, each agent has a local storage area where information can be written and read. Each such whiteboard is accessible in fair mutual exclusion to all incoming agents. Likewise, agent communication can follow a token model, in which agents have available a bounded number of tokens that can be carried, placed in a node and/or on a port of the node, or removed from them. Tokens are identical (i.e., indistinguishable tokens) and no other form of communication or coordination is available to the agents. Our simulations explore the complexity and impact of each of those communication models in terms of [swarm intelligence](https://en.wikipedia.org/wiki/Swarm_intelligence).

Some videocaptures about the different releases follow:

[r1-test](https://youtu.be/_R9daWaxWM0)

[r2-test](http://youtu.be/YbZpcJHpdGc)

[r6-test](http://youtu.be/bMiXqhgFfa8)

[r7-test](http://youtu.be/B-PdgEmdmxQ)

[MAVSIM v1](http://youtu.be/ePn1VMsyFHw)

[MAVSIM v2](http://youtu.be/_F7AbmR-QDM)

Further details available at the [Main Website](http://www-public.imtbs-tsp.eu/~garcia_a/web/prototypes/mavsim/)


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

[MAVSIM v2](https://github.com/jgalfaro/mirrored-scavesim/tree/master/mavsim_v2)

Further details available at the [Main Website of MAVSIM](http://www-public.imtbs-tsp.eu/~garcia_a/web/prototypes/mavsim/)
