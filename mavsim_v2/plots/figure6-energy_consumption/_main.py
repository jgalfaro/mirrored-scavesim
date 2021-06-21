import numpy as np
import scipy as sp
import scipy.stats
import random
import matplotlib.pyplot as plt
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties



fig, ax = plt.subplots()
results = []
color = ["orange","purple", "olive"]
c=0
barWidth = 0.3

def ci_plot(values):

	ci = scipy.stats.t.interval(0.95, len(values)-1, loc=np.mean(values), scale=scipy.stats.sem(values))
	results.append((ci[0], np.mean(values), ci[1]))


def plott(mark,text, tap):

	global results, barWidth, c
	

	# width of the bars
	
	bars1 = []
	for i in results:
		bars1.append(i[1])
	
	r1 = [x + tap for x in np.arange(len(bars1))]
	
	plt.bar(r1, bars1, width = barWidth, color = color[c], edgecolor = 'white', capsize=2, label=text)

	#BOTTOM POINT
	for i in range(len(results)):
		plt.plot(i+tap, results[i][0],  'k_', markersize=6)

	#UP POINT
	for i in range(len(results)):
		plt.plot(i+tap, results[i][2],  'k_', markersize=6)

	#Vertical line
	for i in range(len(results)):
		plt.plot([i+tap, i+tap], [results[i][0],  results[i][2]],  'k-', markersize=10)
	

	#configure
	
	#plt.plot(i, results[i][1],  mark, markersize=6, label=text, markerfacecolor='white')

	plt.legend(loc='center left', bbox_to_anchor=(1, 0.89), edgecolor="black", fontsize=12)
	results = []
	c += 1
	
	

if __name__ == '__main__':
	
	name = [
			["m", "\n"],
		]

	for k in range(1):

		data1 = open("./"+name[k][0]+"10-consumption.txt", "r")
		data2 = open("./"+name[k][0]+"20-consumption.txt", "r")
		data3 = open("./"+name[k][0]+"30-consumption.txt", "r")

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

		plott("ko","90%",0)
		
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

		plott("kD","80%",0.3)
	

	
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
		plott("ks","70%", 0.6)

		plt.xticks([r + barWidth for r in range(8)], [1,3,5,7,10,15,20,30])

		#plt.ticklabel_format(axis='y',style='sci',scilimits=(1,5))
		
		#ylabels = [0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.8,1.0]
		#ax.set_yticklabels(ylabels)


		plt.yticks(fontsize=16)
		plt.xticks(fontsize=16)

		plt.gcf().subplots_adjust(right=0.82)
		plt.gcf().subplots_adjust(left=0.15)
		

		plt.title(name[k][1], fontsize=18)
		plt.xlabel('Number of MAVs', fontsize=16)
		plt.ylabel("Energy Consumption (J)", fontsize=16)


		plt.savefig("./"+name[k][0]+"-consumption.pdf", format='pdf', dpi=1200 )
		#plt.show()
	
		c = 0
       
