#!/bin/bash
#Run the simulation 100 times for 1,3,5,7,10,15,20, and 30 drones

#Set the edges, vertices, and outfile.
edges="edges_unicamp.txt"
vertices="vertices_unicamp.txt"
outfile="unicamp-30.txt"
landmarksToCross=29

A="$(cut -d'.' -f1 <<<"$outfile")"
B="-consumption.txt"
consp="$A$B"

echo "#1" >> "./results/$outfile"
echo "#1" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=1 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done


echo "#3" >> "./results/$outfile"
echo "#3" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=3 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#5" >> "./results/$outfile"
echo "#5" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=5 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#7" >> "./results/$outfile"
echo "#7" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=7 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#10" >> "./results/$outfile"
echo "#10" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=10 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#15" >> "./results/$outfile"
echo "#15" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=15 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#20" >> "./results/$outfile"
echo "#20" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=20 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done

echo "#30" >> "./results/$outfile"
echo "#30" >> "./results/$consp"
for run in {1..100}
do
    echo $run
	ant run -Dagents=30 -Dedges=$edges -Dvertices=$vertices -Doutfile=$outfile -DagentSize=0.003 -DnodeSize=0.001 -DminLandmarks=$landmarksToCross;
done




