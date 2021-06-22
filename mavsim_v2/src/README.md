# Building & executing the simulator

ant clean  && ant build

# Exemples
ant run -Dagents=1 -Dedges="edges_carleton.txt" -Dvertices="vertices_carleton.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=100 -Dpath="path-carleton.txt"
ant run -Dagents=1 -Dedges="edges_unicamp.txt" -Dvertices="vertices_unicamp.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=30 -Dpath="path-unicamp.txt"
ant run -Dagents=1 -Dedges="edges_paris.txt" -Dvertices="vertices_paris.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=50 -Dpath="path-paris.txt"
ant run -Dagents=1 -Dedges="edges_grid10.txt" -Dvertices="vertices_grid10.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=10 -Dpath="path-grid10.txt"
ant run -Dagents=1 -Dedges="edges_grid25.txt" -Dvertices="vertices_grid25.txt" -Doutfile="teste.txt" -DagentSize=0.01 -DnodeSize=0.003 -DminLandmarks=20 -Dpath="path-grid25.txt"

# Node colors

- Blue node: starting node.
- Red node: arrival node.
- Yellow node: landmark to cross (the drones must cross the yellow landmark -DminLandmarks).
- Green node: landmark (the drones may or not cross these green landmarks).


