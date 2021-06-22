import xml.etree.ElementTree as ET
import sys
import math
import networkx as nx

net = sys.argv[1]
poly = sys.argv[2]

G = nx.Graph()

name = net.split("/")[-1].split(".")[0]


tree_net = ET.parse("." + net)
root_net = tree_net.getroot()

if (".poly" in poly):
	tree_poli = ET.parse("." +  poly)
	root_poli = tree_poli.getroot()

out_edgs = open("../scenarios/edges_" + name + ".txt", "w")
out_vertices = open("../scenarios/vertices_" + name + ".txt", "w")

dic = {}
col1 = []
col2 = []
edges = []
vertices = []
list_of_landmarks = []
sett = set()

count  = 100
DIST = 100

maxCol1 = 0
minCol1 = 0
maxCol2 = 0
minCol2 = 0


def distance(x1,y1,x2,y2):
	dist = math.sqrt( (x2 - x1)**2 + (y2 - y1)**2) 
	return dist 

def findPos(fromm, to):

	for i in dic:
		if dic[i] == fromm:
			fromm = i
		if dic[i] == to:
			to = i

	for child in root_net.findall('junction'):
		a = 0
		b = 100

		if (child.get('id') == fromm):
	
			x1 = float(child.get('x'))
			y1 = float(child.get('y'))

			#x1 = (b-a) * ( (x1 - minCol1) / (maxCol1 - minCol1) ) + a
			#y1 = (b-a) * ( (y1 - minCol2) / (maxCol2 - minCol2) ) + a 

		elif(child.get('id') == to):

			x2 = float(child.get('x'))
			y2 = float(child.get('y'))

			#x2 = (b-a) * ( (x2 - minCol1) / (maxCol1 - minCol1) ) + a
			#y2 = (b-a) * ( (y2 - minCol2) / (maxCol2 - minCol2) ) + a 

	return distance(x1,y1,x2,y2)

def calc(landmarks, x, y):
	for land in landmarks:
		if(distance(land[0], land[1],x,y) < DIST):
			return False
	return True

if __name__ == '__main__':

	for child in root_net.findall('edge'):
		
		fromm = child.get('from')
		to = child.get('to')

		if (fromm != None and to != None):

			if(fromm not in dic):
				dic[fromm] = "n" + str(count) + "n"
				count += 1

			if(to not in dic):
				dic[to] = "n" + str(count) + "n"
				count += 1
	

	for child in root_net.findall('junction'):
		id_joinction = child.get('id')
		if(id_joinction in dic):
			col1.append(float(child.get('x')))
			col2.append(float(child.get('y')))

	maxCol1, minCol1 =  max(col1), min(col1)
	maxCol2, minCol2 =  max(col2), min(col2)


	for child in root_net.findall('edge'):
		
		fromm = child.get('from')
		to = child.get('to')

		if (fromm != None and to != None):

			if((dic[fromm], dic[to]) not in sett):

				sett.add( (dic[fromm], dic[to]) )
				edges.append( [dic[fromm], dic[to]] )


	#normalize 
	a = 0
	b = 2.5
	
	land = 0
	landmarks = set()
	type_landmark  =set()
	cc = []


	if (".poly" in poly):
		for child in root_poli.findall('poi'):	
			if (child.get('id') in dic ):
				landmarks.add(child.get('id'))
				type_landmark.add(child.get("type"))		


	for child in root_net.findall('junction'):
		
		id_joinction = child.get('id')
		
		if(id_joinction in dic):
		
			vtt = dic[id_joinction]
			
			posX =  float(child.get('x'))
			posY =  float(child.get('y'))

			if id_joinction in landmarks: 
				vtt =  "l" + vtt
				list_of_landmarks.append(vtt) 
				
			x = (b-a) * ( (posX - minCol1) / (maxCol1 - minCol1) ) + a
			y = (b-a) * ( (posY - minCol2) / (maxCol2 - minCol2) ) + a 
			vertices.append( [vtt, x , y] )


	#Verify if G is a disconnected graph.
	for vv in vertices:
		G.add_node(vv[0])

	landmarkAll = 0
	for ee in edges:
		e1 = ee[0]
		e2 = ee[1]

		if( ("l" + e1) in list_of_landmarks):
			e1 = "l" + e1

		if( ("l" + e2) in list_of_landmarks):
			e2 = "l" + e2

		G.add_edge(e1, e2)		

	d = list(nx.connected_component_subgraphs(G))

	numberOfVertices = 0
	for vv in vertices:
		if vv[0] in d[0]:
			numberOfVertices +=1
			out_vertices.write("%s %.3f %.3f\n" % ( vv[0], vv[1] , vv[2]))

			if "l" in vv[0]:
				landmarkAll += 1

	for ee in d[0].edges:
		fromm = ee[0].replace("l", "")
		to = ee[1].replace("l","")
		out_edgs.write("%s %s %s\n" % (fromm, to,  findPos( fromm, to ) ))

	print("Number of Nodes: %s" % numberOfVertices)
	print("Number of Landrmaks: %s" % landmarkAll)
	print("Type of landmarks %s" %(type_landmark))
	print("Generated files: edges_" + name + ".txt and vertices_" + name + ".txt")
