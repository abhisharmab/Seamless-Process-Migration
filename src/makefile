JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

%.class: %.java
	$(JC) $(JFLAGS) $< 
	
.java.class:
	$(JC) $(JFLAGS) $*.java


CLASSES = \
	abhi/ds/Factorial.java\
	abhi/ds/GrepProcess.java\
	abhi/ds/HeartBeatRequestHandler.java\
	abhi/ds/HeartBeatSender.java\
	abhi/ds/HelperUtility.java\
	abhi/ds/MigratableProcess.java\
	abhi/ds/ProcessManager.java\
	abhi/ds/ProcessManagerHeartBeatListener.java\
	abhi/ds/Signal.java\
	abhi/ds/SystemOrchestration.java\
	abhi/ds/TransactionalFileInputStream.java\
	abhi/ds/TransactionalFileOutputStream.java\
	abhi/ds/WorkerHealthChecker.java\
	abhi/ds/WorkerInfo4Display.java\
	abhi/ds/WorkerManager.java\
	abhi/ds/WorkerRequestHandler.java\
	abhi/ds/WorkerRequestListener.java

default: classes

classes: $(CLASSES:.java=.class)


clean:
	$(RM) abhi/ds/*.class