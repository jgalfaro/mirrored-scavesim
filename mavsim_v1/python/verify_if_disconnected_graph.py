import networkx as nx

G = nx.Graph()

vertices = open("../scenarios/vertices_paris.txt", "r")
edges = open("../scenarios/edges_paris.txt", "r")

vertices = vertices.readlines() 
edges = edges.readlines()

for vv in vertices:
	vv = vv.split(" ")
	G.add_node(vv[0])

for ee in edges:
	ee = ee.split(" ")
	G.add_edge(ee[0], ee[1])
	

d = list(nx.connected_component_subgraphs(G))
# d contains disconnected subgraphs

a = []
a.append( [1,2,3] )

a.append( [1,2,3] )

print(a)