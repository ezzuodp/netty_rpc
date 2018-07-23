1. TLAB (TThread Local Alloc Buffer)
    
2. JVM NEW Object 过程
	2.1  优先在TLAB中分配 
	2.2  TLAB不足，调用 allocate_new_tlab()方法，从eden新分配一块裸的空间出来（这一步可能会失败），
	     如果失败说明eden没有足够空间来分配这个新TLAB，就会触发YGC,进行Eden区+有对象的Survivor区(设为S0区)垃圾回收
	     把存活的对象用复制算法拷贝到一个空的Survivor(S1)中，此时Eden区被清空，另外一个Survivor S0也为空。
		 若发现Survivor区满了，则将这些对象拷贝到old区或者Survivor没满但某些对象足够Old,也拷贝到Old区
		 (每次Young GC都会使Survivor区存活对象值+1，直到阈值)。 
		 Old区也会进行垃圾收集(Major GC), 发生一次 Major GC 至少伴随一次Young GC，一般比 Young GC慢十倍以上。
		 JVM在Old区申请不到内存，会进行Full GC。

4. CMS
	New:OLd [ 1 : 2]
	NEW<{EDEN:S0:S1}，其中比例[8:1:1]
	
	**大致分为以下6个步骤**
	1. 初始标记：标记一下GC Roots能直接关联到的对象，会“Stop The World”
		2018-05-18T15:16:00.410+0800: 367379.643: [GC (CMS Initial Mark) [1 CMS-initial-mark: 571255K(707840K)] 581327K(1014528K), 0.0080693 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
    
    2. 并发标记：GC Roots Tracing，可以和用户线程并发执行。
        2018-05-18T15:16:00.419+0800: 367379.651: [CMS-concurrent-mark-start]
        2018-05-18T15:16:00.841+0800: 367380.073: [CMS-concurrent-mark: 0.422/0.422 secs] [Times: user=0.47 sys=0.00, real=0.42 secs] 
    
    3. 并发预清理: 
		2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-preclean-start]
		2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-preclean: 0.016/0.016 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
		2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-abortable-preclean-start]
		2015-05-26T16:23:08.446-0200: 65.550: [CMS-concurrent-abortable-preclean: 0.167/1.074 secs] [Times: user=0.20 sys=0.00, real=1.07 secs]

    4. 重新标记：标记期间产生的对象存活的再次判断，修正对这些对象的标记，执行时间相对并发标记短，会“Stop The World”。
        2015-05-26T16:23:08.447-0200: 65.550: [GC (CMS Final Remark) [YG occupancy: 387920 K (613440 K)]65.550: [Rescan (parallel) , 0.0085125 secs]65.559: [weak refs processing, 0.0000243 secs]65.559: [class unloading, 0.0013120 secs]65.560: [scrub symbol table, 0.0008345 secs]65.561: [scrub string table, 0.0001759 secs][1 CMS-remark: 10812086K(11901376K)] 11200006K(12514816K), 0.0110730 secs] [Times: user=0.06 sys=0.00, real=0.01 secs]
        
    5. 并发清除：清除对象,可以和用户线程并发执行。
        2018-05-18T15:16:06.063+0800: 367385.295: [CMS-concurrent-sweep-start]
        2018-05-18T15:16:06.193+0800: 367385.426: [CMS-concurrent-sweep: 0.130/0.130 secs] [Times: user=0.13 sys=0.00, real=0.13 secs]
    
    6. 并发重置：
		2018-05-18T15:16:06.193+0800: 367385.426: [CMS-concurrent-reset-start]
        2018-05-18T15:16:06.194+0800: 367385.427: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
    
	CMS最主要解决了pause time，但是会占用CPU资源，牺牲吞吐量。CMS默认启动的回收线程数是（CPU数量+3）/ 4，当CPU<4个时，
	会影响用户线程的执行。另外一个缺点就是内存碎片的问题了，碎片会给大对象的内存分配造成麻烦，如果老年代的可用的连续空间也
	无法分配时，会触发full gc。并且full gc时如果发生young gc会被young gc打断，执行完young gc之后再继续执行full gc。
	
	Concurrent Mark and Sweep
    The official name for this collection of garbage collectors is “Mostly Concurrent Mark and Sweep Garbage Collector”. It uses the parallel stop-the-world mark-copy algorithm in the Young Generation and the mostly concurrent mark-sweep algorithm in the Old Generation.
    This collector was designed to avoid long pauses while collecting in the Old Generation. It achieves this by two means. Firstly, it does not compact the Old Generation but uses free-lists to manage reclaimed space. Secondly, it does most of the job in the mark-and-sweep phases concurrently with the application. This means that garbage collection is not explicitly stopping the application threads to perform these phases. It should be noted, however, that it still competes for CPU time with the application threads. By default, the number of threads used by this GC algorithm equals to ¼ of the number of physical cores of your machine.
    This combination is a good choice on multi-core machines if your primary target is latency. Decreasing the duration of an individual GC pause directly affects the way your application is perceived by end-users, giving them a feel of a more responsive application. As most of the time at least some CPU resources are consumed by the GC and not executing your application’s code, CMS generally often worse throughput than Parallel GC in CPU-bound applications.
    
    As with previous GC algorithms, let us now see how this algorithm is applied in practice by taking a look at the GC logs that once again expose one minor and one major GC pause:
    {
    2015-05-26T16:23:07.219-0200: 64.322: [GC (Allocation Failure) 64.322: [ParNew: 613404K->68068K(613440K), 0.1020465 secs] 10885349K->10880154K(12514816K), 0.1021309 secs] [Times: user=0.78 sys=0.01, real=0.11 secs]
    2015-05-26T16:23:07.321-0200: 64.425: [GC (CMS Initial Mark) [1 CMS-initial-mark: 10812086K(11901376K)] 10887844K(12514816K), 0.0001997 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    2015-05-26T16:23:07.321-0200: 64.425: [CMS-concurrent-mark-start]
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-mark: 0.035/0.035 secs] [Times: user=0.07 sys=0.00, real=0.03 secs]
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-preclean-start]
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-preclean: 0.016/0.016 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-abortable-preclean-start]
    2015-05-26T16:23:08.446-0200: 65.550: [CMS-concurrent-abortable-preclean: 0.167/1.074 secs] [Times: user=0.20 sys=0.00, real=1.07 secs]
    2015-05-26T16:23:08.447-0200: 65.550: [GC (CMS Final Remark) [YG occupancy: 387920 K (613440 K)]65.550: [Rescan (parallel) , 0.0085125 secs]65.559: [weak refs processing, 0.0000243 secs]65.559: [class unloading, 0.0013120 secs]65.560: [scrub symbol table, 0.0008345 secs]65.561: [scrub string table, 0.0001759 secs][1 CMS-remark: 10812086K(11901376K)] 11200006K(12514816K), 0.0110730 secs] [Times: user=0.06 sys=0.00, real=0.01 secs]
    2015-05-26T16:23:08.458-0200: 65.561: [CMS-concurrent-sweep-start]
    2015-05-26T16:23:08.485-0200: 65.588: [CMS-concurrent-sweep: 0.027/0.027 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
    2015-05-26T16:23:08.485-0200: 65.589: [CMS-concurrent-reset-start]
    2015-05-26T16:23:08.497-0200: 65.601: [CMS-concurrent-reset: 0.012/0.012 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
    }
    * Minor GC
    First of the GC events in log denotes a minor GC cleaning the Young space. Let’s analyze how this collector combination behaves in this regard:
    
    2015-05-26T16:23:07.219-02001: 64.3222:[GC (Allocation Failure4) 64.322: [ParNew5: 613404K->68068K6(613440K) 7, 0.1020465 secs8] 10885349K->10880154K 9(12514816K)10, 0.1021309 secs11][Times: user=0.78 sys=0.01, real=0.11 secs]12
    
    2015-05-26T16:23:07.219-0200 – Time when the GC event started.
    64.322 – Time when the GC event started, relative to the JVM startup time. Measured in seconds.
    GC – Flag to distinguish between Minor & Full GC. This time it is indicating that this was a Minor GC.
    Allocation Failure – Cause of the collection. In this case, the GC is triggered due to a requested allocation not fitting into any region in Young Generation.
    ParNew – Name of the collector used, this time it indicates a parallel mark-copy stop-the-world garbage collector used in the Young Generation, designed to work in conjunction with Concurrent Mark & Sweep garbage collector in the Old Generation.
    613404K->68068K – Usage of the Young Generation before and after collection.
    (613440K)  – Total size of the Young Generation.
    0.1020465 secs – Duration for the collection w/o final cleanup.
    10885349K->10880154K  – Total used heap before and after collection.
    (12514816K) – Total available heap.
    0.1021309 secs – The time it took for the garbage collector to mark and copy live objects in the Young Generation. This includes communication overhead with ConcurrentMarkSweep collector, promotion of objects that are old enough to the Old Generation and some final cleanup at the end of the garbage collection cycle.
    [Times: user=0.78 sys=0.01, real=0.11 secs] – Duration of the GC event, measured in different categories:
    user – Total CPU time that was consumed by Garbage Collector threads during this collection
    sys – Time spent in OS calls or waiting for system event
    real – Clock time for which your application was stopped. With Parallel GC this number should be close to (user time + system time) divided by the number of threads used by the Garbage Collector. In this particular case 8 threads were used. Note that due to some activities not being parallelizable, it always exceeds the ratio by a certain amount.
    From the above we can thus see that before the collection the total used heap was 10,885,349K and the used Young Generation share was 613,404K. This means that the Old Generation share was 10,271,945K. After the collection, Young Generation usage decreased by 545,336K but total heap usage decreased only by 5,195K. This means that 540,141K was promoted from the Young Generation to Old.
    
    Java ParalleGC
    
    Full GC
    Now, just as you are becoming accustomed to reading GC logs already, this chapter will introduce a completely different format for the next garbage collection event in the logs. The lengthy output that follows consists of all the different phases of the mostly concurrent garbage collection in the Old Generation. We will review them one by one but in this case we will cover the log content in phases instead of the entire event log at once for more concise representation. But to recap, the whole event for the CMS collector looks like the following:
    
    2015-05-26T16:23:07.321-0200: 64.425: [GC (CMS Initial Mark) [1 CMS-initial-mark: 10812086K(11901376K)] 10887844K(12514816K), 0.0001997 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    2015-05-26T16:23:07.321-0200: 64.425: [CMS-concurrent-mark-start]
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-mark: 0.035/0.035 secs] [Times: user=0.07 sys=0.00, real=0.03 secs]
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-preclean-start]
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-preclean: 0.016/0.016 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-abortable-preclean-start]
    2015-05-26T16:23:08.446-0200: 65.550: [CMS-concurrent-abortable-preclean: 0.167/1.074 secs] [Times: user=0.20 sys=0.00, real=1.07 secs]
    2015-05-26T16:23:08.447-0200: 65.550: [GC (CMS Final Remark) [YG occupancy: 387920 K (613440 K)]65.550: [Rescan (parallel) , 0.0085125 secs]65.559: [weak refs processing, 0.0000243 secs]65.559: [class unloading, 0.0013120 secs]65.560: [scrub symbol table, 0.0008345 secs]65.561: [scrub string table, 0.0001759 secs][1 CMS-remark: 10812086K(11901376K)] 11200006K(12514816K), 0.0110730 secs] [Times: user=0.06 sys=0.00, real=0.01 secs]
    2015-05-26T16:23:08.458-0200: 65.561: [CMS-concurrent-sweep-start]
    2015-05-26T16:23:08.485-0200: 65.588: [CMS-concurrent-sweep: 0.027/0.027 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
    2015-05-26T16:23:08.485-0200: 65.589: [CMS-concurrent-reset-start]
    2015-05-26T16:23:08.497-0200: 65.601: [CMS-concurrent-reset: 0.012/0.012 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
    Just to bear in mind – in real world situation Minor Garbage Collections of the Young Generation can occur anytime during concurrent collecting the Old Generation. In such case the major collection records seen below will be interleaved with the Minor GC events covered in previous chapter.
    
    Phase 1: Initial Mark. This is one of the two stop-the-world events during CMS. The goal of this phase is to mark all the objects in the Old Generation that are either direct GC roots or are referenced from some live object in the Young Generation. The latter is important since the Old Generation is collected separately.
    
    CMS initial mark
    
    2015-05-26T16:23:07.321-0200: 64.421: [GC (CMS Initial Mark2[1 CMS-initial-mark: 10812086K3(11901376K)4] 10887844K5(12514816K)6, 0.0001997 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]7
    
    2015-05-26T16:23:07.321-0200: 64.42 – Time the GC event started, both clock time and relative to the time from the JVM start. For the following phases the same notion is used throughout the event and is thus skipped for brevity.
    CMS Initial Mark – Phase of the collection – “Initial Mark” in this occasion – that is collecting all GC Roots.
    10812086K – Currently used Old Generation.
    (11901376K) – Total available memory in the Old Generation.
    10887844K – Currently used heap
    (12514816K) – Total available heap
    0.0001997 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] – Duration of the phase, measured also in user, system and real time.
    Phase 2: Concurrent Mark. During this phase the Garbage Collector traverses the Old Generation and marks all live objects, starting from the roots found in the previous phase of “Initial Mark”. The “Concurrent Mark” phase, as its name suggests, runs concurrently with your application and does not stop the application threads. Note that not all the live objects in the Old Generation may be marked, since the application is mutating references during the marking.
    
    CMS concurrent marking
    
    In the illustration, a reference pointing away from the “Current object” was removed concurrently with the marking thread.
    
    2015-05-26T16:23:07.321-0200: 64.425: [CMS-concurrent-mark-start]
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-mark1: 035/0.035 secs2] [Times: user=0.07 sys=0.00, real=0.03 secs]3
    CMS-concurrent-mark – Phase of the collection – “Concurrent Mark” in this occasion – that is traversing the Old Generation and marking all live objects.
    035/0.035 secs – Duration of the phase, showing elapsed time and wall clock time correspondingly.
    [Times: user=0.07 sys=0.00, real=0.03 secs] – “Times” section is less meaningful for concurrent phases as it is measured from the start of the concurrent marking and includes more than just the work done for the concurrent marking.
    Phase 3: Concurrent Preclean. This is again a concurrent phase, running in parallel with the application threads, not stopping them. While the previous phase was running concurrently with the application, some references were changed. Whenever that happens, the JVM marks the area of the heap (called “Card”) that contains the mutated object as “dirty” (this is known as Card Marking).
    
    CMS dirty cards
    
    In the pre-cleaning phase, these dirty objects are accounted for, and the objects reachable from them are also marked. The cards are cleaned when this is done.
    
    CMS concurrent preclean
    
    Additionally, some necessary housekeeping and preparations for the Final Remark phase are performed.
    
    2015-05-26T16:23:07.357-0200: 64.460: [CMS-concurrent-preclean-start]
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-preclean1: 0.016/0.016 secs2] [Times: user=0.02 sys=0.00, real=0.02 secs]3
    CMS-concurrent-preclean – Phase of the collection – “Concurrent Preclean” in this occasion – accounting for references being changed during previous marking phase.
    0.016/0.016 secs – Duration of the phase, showing elapsed time and wall clock time correspondingly.
    [Times: user=0.02 sys=0.00, real=0.02 secs] – The “Times” section is less meaningful for concurrent phases as it is measured from the start of the concurrent marking and includes more than just the work done for the concurrent marking.
    Phase 4: Concurrent Abortable Preclean. Again, a concurrent phase that is not stopping the application’s threads. This one attempts to take as much work off the shoulders of the stop-the-world Final Remark as possible. The exact duration of this phase depends on a number of factors, since it iterates doing the same thing until one of the abortion conditions (such as the number of iterations, amount of useful work done, elapsed wall clock time, etc) is met.
    
    2015-05-26T16:23:07.373-0200: 64.476: [CMS-concurrent-abortable-preclean-start]
    2015-05-26T16:23:08.446-0200: 65.550: [CMS-concurrent-abortable-preclean1: 0.167/1.074 secs2] [Times: user=0.20 sys=0.00, real=1.07 secs]3
    CMS-concurrent-abortable-preclean – Phase of the collection “Concurrent Abortable Preclean” in this occasion
    0.167/1.074 secs – Duration of the phase, showing elapsed and wall clock time respectively. It is interesting to note that the user time reported is a lot smaller than clock time. Usually we have seen that real time is less than user time, meaning that some work was done in parallel and so elapsed clock time is less than used CPU time. Here we have a little amount of work – for 0.167 seconds of CPU time, and garbage collector threads were doing a lot of waiting. Essentially, they were trying to stave off for as long as possible before having to do an STW pause. By default, this phase may last for up to 5 seconds.
    [Times: user=0.20 sys=0.00, real=1.07 secs] – The “Times” section is less meaningful for concurrent phases, as it is measured from the start of the concurrent marking and includes more than just the work done for the concurrent marking.
    This phase may significantly impact the duration of the upcoming stop-the-world pause, and has quite a lot of non-trivial configuration options and fail modes.
    
    Phase 5: Final Remark. This is the second and last stop-the-world phase during the event. The goal of this stop-the-world phase is to finalize marking all live objects in the Old Generation. Since the previous preclean phases were concurrent, they may have been unable to keep up with the application’s mutating speeds. A stop-the-world pause is required to finish the ordeal.
    
    Usually CMS tries to run final remark phase when Young Generation is as empty as possible in order to eliminate the possibility of several stop-the-world phases happening back-to-back.
    
    This event looks a bit more complex than previous phases:
    
    2015-05-26T16:23:08.447-0200: 65.5501: [GC (CMS Final Remark2) [YG occupancy: 387920 K (613440 K)3]65.550: [Rescan (parallel) , 0.0085125 secs]465.559: [weak refs processing, 0.0000243 secs]65.5595: [class unloading, 0.0013120 secs]65.5606: [scrub string table, 0.0001759 secs7][1 CMS-remark: 10812086K(11901376K)8] 11200006K(12514816K) 9, 0.0110730 secs10] [[Times: user=0.06 sys=0.00, real=0.01 secs]11
    
    2015-05-26T16:23:08.447-0200: 65.550 – Time the GC event started, both clock time and relative to the time from the JVM start.
    CMS Final Remark – Phase of the collection – “Final Remark” in this occasion – that is marking all live objects in the Old Generation, including the references that were created/modified during previous concurrent marking phases.
    YG occupancy: 387920 K (613440 K) – Current occupancy and capacity of the Young Generation.
    [Rescan (parallel) , 0.0085125 secs] – The “Rescan” completes the marking of live objects while the application is stopped. In this case the rescan was done in parallel and took 0.0085125 seconds.
    weak refs processing, 0.0000243 secs]65.559 – First of the sub-phases that is processing weak references along with the duration and timestamp of the phase.
    class unloading, 0.0013120 secs]65.560 – Next sub-phase that is unloading the unused classes, with the duration and timestamp of the phase.
    scrub string table, 0.0001759 secs – Final sub-phase that is cleaning up symbol and string tables which hold class-level metadata and internalized string respectively. Clock time of the pause is also included.
    10812086K(11901376K) – Occupancy and the capacity of the Old Generation after the phase.
    11200006K(12514816K)  – Usage and the capacity of the total heap after the phase.
    0.0110730 secs – Duration of the phase.
    [Times: user=0.06 sys=0.00, real=0.01 secs] – Duration of the pause, measured in user, system and real time categories.
    After the five marking phases, all live objects in the Old Generation are marked and now garbage collector is going to reclaim all unused objects by sweeping the Old Generation:
    
    Phase 6: Concurrent Sweep. Performed concurrently with the application, without the need for the stop-the-world pauses. The purpose of the phase is to remove unused objects and to reclaim the space occupied by them for future use.
    
    CMS concurrent sweep
    
    2015-05-26T16:23:08.458-0200: 65.561: [CMS-concurrent-sweep-start] 2015-05-26T16:23:08.485-0200: 65.588: [CMS-concurrent-sweep1: 0.027/0.027 secs2] [[Times: user=0.03 sys=0.00, real=0.03 secs] 3
    
    CMS-concurrent-sweep – Phase of the collection “Concurrent Sweep” in this occasion, sweeping unmarked and thus unused objects to reclaim space.
    0.027/0.027 secs – Duration of the phase, showing elapsed time and wall clock time correspondingly.
    [Times: user=0.03 sys=0.00, real=0.03 secs]  – “Times” section is less meaningful on concurrent phases, as it is measured from the start of the concurrent marking and includes more than just the work done for the concurrent marking.
    Phase 7: Concurrent Reset. Concurrently executed phase, resetting inner data structures of the CMS algorithm and preparing them for the next cycle.
    
    2015-05-26T16:23:08.485-0200: 65.589: [CMS-concurrent-reset-start] 2015-05-26T16:23:08.497-0200: 65.601: [CMS-concurrent-reset1: 0.012/0.012 secs2] [[Times: user=0.01 sys=0.00, real=0.01 secs]3
    
    CMS-concurrent-reset – The phase of the collection – “Concurrent Reset” in this occasion – that is resetting inner data structures of the CMS algorithm and preparing for the next collection.
    0.012/0.012 secs – Duration of the the phase, measuring elapsed and wall clock time respectively.
    [Times: user=0.01 sys=0.00, real=0.01 secs] – The “Times” section is less meaningful on concurrent phases, as it is measured from the start of the concurrent marking and includes more than just the work done for the concurrent marking.
    All in all, the CMS garbage collector does a great job at reducing the pause durations by offloading a great deal of the work to concurrent threads that do not require the application to stop. However, it, too, has some drawbacks, the most notable of them being the Old Generation fragmentation and the lack of predictability in pause durations in some cases, especially on large heaps.
	
3. G1 
	G1 Region数最大：2048, MinSize = 1M, MaxSize = 32M .
	最大设置 2048 * 32 M = 64 G
