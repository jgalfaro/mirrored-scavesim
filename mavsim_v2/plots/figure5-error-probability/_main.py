import numpy as np
import scipy as sp
import scipy.stats
import random
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties

fig, ax = plt.subplots()
results = []

def ci_plot(values):

	ci = scipy.stats.t.interval(0.95, len(values)-1, loc=np.mean(values), scale=scipy.stats.sem(values))
	results.append((ci[0], np.mean(values), ci[1]))


def plott(mark,text):
	global results

	#Horizontal Line
	for i in range(len(results)-1):
		plt.plot([i, i+1], [results[i][1],  results[i+1][1]],  '-', color = '.9',markersize=10)


	#Vertical line
	for i in range(len(results)):
		plt.plot([i, i], [results[i][0],  results[i][2]],  'k-', markersize=10)

	#POINTS
	for i in range(len(results)):
		plt.plot(i, results[i][0],  'k_', markersize=6)

	for i in range(len(results)):
		plt.plot(i, results[i][1],  mark, markersize=6, markerfacecolor='white')

	for i in range(len(results)):
		plt.plot(i, results[i][2],  'k_', markersize=6)


	#configure

	plt.plot(i, results[i][1],  mark, markersize=6, label=text, markerfacecolor='white')

	plt.legend(loc='center left', bbox_to_anchor=(1, 0.89), edgecolor="black", fontsize=12)
	results = []




if __name__ == '__main__':

	name = [
			["m","\n"],
		]

	for k in range(1):

		data1 = open("./"+name[k][0]+"10.txt", "r")
		data2 = open("./"+name[k][0]+"20.txt", "r")
		data3 = open("./"+name[k][0]+"30.txt", "r")

		data1 = data1.readlines()[1:]
		ls = []
		for i in data1:
			if "#" not in i:
				ls.append(float(i.rstrip()))
			else:
				ci_plot(ls)
				s=i.split('#')[1]
				s=s.replace('\n','')
				print("A",end='')
				print(s,end='')
				print("=[",end='')
				print(ls,end='')
				print("]",end='\n')
				ls = []
		ci_plot(ls)
		print("A30=[",end='')
		print(ls,end='')
		print("]",end='\n')

		plott("ko","90%")

		data2 = data2.readlines()[1:]
		ls = []
		for i in data2:
			if "#" not in i:
				ls.append(float(i.rstrip()))
			else:
				ci_plot(ls)
				s=i.split('#')[1]
				s=s.replace('\n','')
				print("B",end='')
				print(s,end='')
				print("=[",end='')
				print(ls,end='')
				print("]",end='\n')
				ls = []
		ci_plot(ls)
		print("B30=[",end='')
		print(ls,end='')
		print("]",end='\n')

		plott("kD","80%")

		data3 = data3.readlines()[1:]
		ls = []
		for i in data3:
			if "#" not in i:
				ls.append(float(i.rstrip()))
			else:
				ci_plot(ls)
				s=i.split('#')[1]
				s=s.replace('\n','')
				print("C",end='')
				print(s,end='')
				print("=[",end='')
				print(ls,end='')
				print("]",end='\n')
				ls = []
		ci_plot(ls)
		print("C30=[",end='')
		print(ls,end='')
		print("]",end='\n')

		plott("ks","70%")


		xlabels = ["",1,3,5,7,10,15,20,30]
		#ax.set_xticklabels(xlabels)
		plt.yticks(fontsize=16)
		plt.xticks(fontsize=16)

		plt.gcf().subplots_adjust(top=0.85)
		plt.gcf().subplots_adjust(right=0.83)
		plt.gcf().subplots_adjust(bottom=0.15)


		plt.title(name[k][1], fontsize=18)
		plt.xlabel('Number of MAVs', fontsize=16)
		plt.ylabel("$p_m$", fontsize=16)

		plt.ylim(0.75, 1.02)


		plt.grid()
		plt.savefig("./"+name[k][0]+".pdf", format='pdf', dpi=1200 )
		#plt.show()

