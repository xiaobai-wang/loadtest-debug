#############################################################################
#
# Agent JVM Configuration
#
# List additional JVM command-line arguments, one by line. These arguments
# will be appended to the default JVM command line for all agents.  
#
#############################################################################

#-showversion
#-Dfoo=bar

#############################################################################
#
# Java Garbage Collector Tuning 
#
# Please check out
# http://java.sun.com/javase/technologies/hotspot/gc/gc_tuning_6.html
# for more information.
#
# These settings should be used for large load tests. They help to avoid
# blocking pauses during GC. Please make sure, that the agents have enough
# memory (Xmx settings) for your particular test. Setting Xms and Xmx to
# the same value helps to tune the VM. 
#
#############################################################################

## enable GC log (for Sun JVMs)
-Xloggc:results/gc.log
-verbose:gc				#report on each GC-event (as much information as possible)
-XX:+PrintGCDetails			#in this case there is "concurrent mode failure", "promotion failed" or "GC" instead of "FullGC" in gc.log
-XX:+PrintGCDateStamps
-XX:+PrintGCTimeStamps
-XX:+PrintGCApplicationStoppedTime
-XX:+PrintTenuringDistribution
-XX:+HeapDumpBeforeFullGC		#helps to localize the "FullGC" when -XX:+PrintGCDetails is activated
-XX:HeapDumpPath=/dev/null		#delete heapdump-file, because it is to big
-XX:+PrintHeapAtGC
-XX:+PrintHeapAtGCExtended
#-XX:+PrintAdaptiveSizePolicy

## Disabling the GC-Ergonomics (default on true)
-XX:-UseAdaptiveSizePolicy				
-XX:-UseAdaptiveGenerationSizePolicyAtMajorCollection        
-XX:-UseAdaptiveGenerationSizePolicyAtMinorCollection             
-XX:-UseAdaptiveNUMAChunkSizing				#TLAB's optmized for NUMA-processor-architecture                          
-XX:-UseAdaptiveSizeDecayMajorGCCost                                
-XX:-UseAdaptiveSizePolicyFootprintGoal                   		


## Disabling explicit GC by direct calling from the Java-Sourcecode
-XX:+DisableExplicitGC		



## Set minimum memory to use
-Xms1G

## Set maximum permitted memory
-Xmx1G

## Enable incremental young GC
# -Xincgc

## Set relation between Old and Young-Generation (default=2)
## default is only active, if (totalHeapSize<=1GB)
-XX:NewRatio=12

## Set the newsize for short living objects. Helps to avoid long 
## garbage collection cycles. This should fit to the Xms/Xmx settings.
#-XX:NewSize=128m
#-XX:MaxNewSize=128m

## Set relation between Eden and each Survivor-Space (default=8)
-XX:SurvivorRatio=1

## Enable concurrent GC to avoid sudden long pauses
-XX:+UseConcMarkSweepGC


## only 1 GC-Thread, because no overhead of synchronisation 
-XX:ParallelGCThreads=1


## Enables incremental mode. Note that the concurrent collector must also be enabled 
## (with -XX:+UseConcMarkSweepGC) for this option to work. 
# -XX:+CMSIncrementalMode

## The maximum pause time goal is specified. This is interpreted as a hint that pause 
## times of <N> milliseconds or less are desired; by default there is no maximum pause time goal. 
## If a pause time goal is specified, the heap size and other garbage collection related parameters
## are adjusted in an attempt to keep garbage collection pauses shorter than the specified value. 
## Note that these adjustments may cause the garbage collector to reduce the overall throughput of 
## the application and in some cases the desired pause time goal cannot be met. 
#-XX:MaxGCPauseMillis=100

## When to start GC for the tenured/old area of the memory. This has to be low enough to
## avoid that threads need memory and can not get any before the GC has finished. This will
## lower the wait time.
-XX:CMSInitiatingOccupancyFraction=30
-XX:+UseCMSInitiatingOccupancyOnly

## Between the initial-marking and the re-marking on CMS there is a precleaning phase. To variate the
## deadline of precleaning-phase (going on iteratively in a few steps) you can adjust the total time.
## <default=5000>
##-XX:CMSMaxAbortablePrecleanTime=50000

## Enable G1 GC
#-XX:+UseG1GC