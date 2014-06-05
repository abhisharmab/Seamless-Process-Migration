all:
	cd src; make

processmanager:
	java -cp src abhi.ds.SystemOrchestration PM $(IP) $(PORT)

worker:
	java -cp src abhi.ds.SystemOrchestration W $(IP) $(PORT) $(PMIP) $(PMPORT)

clean:
	cd src; make clean

