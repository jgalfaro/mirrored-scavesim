CC=ant

all: 5

2:
	for j in `seq 30 10 100`;do for i in `seq 1 105`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/2agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=2 -Dnodes=$$j  | tee -a TESTS/2agents-100-to-30/$$j-nodes-results.txt;done;done

3:
	for j in `seq 30 10 100`;do for i in `seq 1 80`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/3agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=3 -Dnodes=$$j  | tee -a TESTS/3agents-100-to-30/$$j-nodes-results.txt;done;done

4:
	for j in `seq 30 10 100`;do for i in `seq 1 80`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/4agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=4 -Dnodes=$$j  | tee -a TESTS/4agents-100-to-30/$$j-nodes-results.txt;done;done

5:
	for j in `seq 40 10 100`;do for i in `seq 1 80`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/5agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=5 -Dnodes=$$j  | tee -a TESTS/5agents-100-to-30/$$j-nodes-results.txt;done;done

6:
	for j in `seq 30 10 100`;do for i in `seq 1 110`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/6agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=6 -Dnodes=$$j  | tee -a TESTS/6agents-100-to-30/$$j-nodes-results.txt;done;done

7:
	for j in `seq 30 10 100`;do for i in `seq 1 110`;do echo "**** Simulation number $$i with $$j nodes *****" | tee -a TESTS/7agents-100-to-30/$$j-nodes-results.txt; ant run -Dagents=7 -Dnodes=$$j  | tee -a TESTS/7agents-100-to-30/$$j-nodes-results.txt;done;done

clean:

