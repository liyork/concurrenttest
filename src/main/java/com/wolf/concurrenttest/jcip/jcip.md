java concurrency in practice

http://www.javaconcurrencyinpractice.com

Threads are an inescapable feature of the java language, they can simplify the development of complex systems by turning int complicated
asynchronous code into simpler straight-line code.

exploiting concurrency effetively will only become more important.

Several motivating factors led to the development of operating systems that allowed multiple programs to execute simultaneously:
Resource utilization. it is more efficient to use that wait time to let another program run
Fairness. let them share the computer via finer-grained time slicing
Convenience. easier or more desirable to write several programs that each perform a single task and have them coordiante with each other

finding the right balance of sequentiality and asychrony is often a characteristic of efficient people

threads allow multiple streams of program control flow to coexist within a process.

since the basic unit of scheduling is the thread, a program with only one thread can run on at most one processor at a tim.

managing multiple priorities and deadlines and switching from task to task usally carries some overhead.

safety cannot be compromised.

have to be familiar with concurrency and thread safety


wirting thread-safe code is, at its core, about managing access to state, and in particular to shared, mutable state

whether an object needs to be thread-safe depends on whether it will be accessed from multiple threads.
this is a proerty of how the object is used in a program, not what it does.

whenever more than one thread access a givne state variable, and one of them might write to it, they all must coorinate their access to it
using synchronization.

mutltiple threads access the same mutable state variable:
1.don't share the state variable across threads.
2.make the state variable immutable;or
2.use synchronization whenever accessing the state variable

better encapsulated your program state, the easier it is to make your pragram thread-safe and to help maintainers keep it that way.

when designing thread-safe classes, good object-oriented techniques - encapsulation, immutability, and clear specification of invariants

good practice first to make your code right, and then make it fast.

if our definition of thread safety is fuzzy, it is because we lack a clear definition of correctness
correctness means that a class conforms to its specification.

a class is thread-safe if it behaves correctly when accessed from multiple threads.

Stateless objects are always thread-safe

i++
this is an example of a read-mofify-write operation, in which the resulting state is derived from the previous state
the possibility of incorrect results in the presence of unlucky timing is so important in concurrent programming that it has a name: a race condition

race condition example
check-then-act/read-modify-write
to avoid race conditions, there must be a way to prevent other threads from using a variable while we're in the middle of modifying it, so we can ensure that other threads can observe of modify the state only before we start or after we finish, but not in the middle

the definition of thread safety requires that invariants be preserved regardless of timing or interleaving of operations in multiple threads.

to preserve state consistency, update related state variables in a single atomic operation.

reentrancy means that locks are acquired on a per-thread rather than per-invocation basis.
Reentrancy is implemented by associating with each lock an acquisition count and an owning thread.

for each mutable state variable that may be accessed by more than one thread, all access to that variable must be performed with the same lock held

every shared, mutable variable should be guarded by exactly one lock.多个不互斥

only mutable data that will be accessed from multiple threads needs to be guarded by locks.

for every invariant that involves more than one variable, all the variables involved in that invariant must be guarded by the same lock

poor concurrency: the number of simultaneous invocations is limited not by the avaliability of processing resources, but by the structure of the application itself.

there is frequently a tension between simplicity and performance. when imiplementing a synchronization policy, resist the temptation to prematurely sacrifice simplicity for the sake of performance
优先简单+可读，稍后性能，当然简单+可读+适当性能是最好的

avoid holding locks during lengthy computations or operations at risk of completing quickly such as network or console I/O



synchronized blocks and methods can ensure that operations execute atomically, has another significant aspect: memory visibility.

in general, there is no guarantee that the reading thread will see a value written by another thread on a timely basis, or even at all. in order to ensure visibility of memory writes across threads, use synchronization.

in the absence of synchronization, the compiler, processor, and runtime can do some downright weird things to the order in which operations appear to execute.

对于 64位的值long、double，可能会产生out-of-thin-air值，若是没哟用volatile

when thread a executes a synchronized block, and subsequently thread B enters a synchronized block guarded by the same lock, the values of variables that were visible to A prior to releasing the lock are
guaranteed to be visible to B upon acquiring the lock.
A(Everything before the unlock on M...) B(... is visible to everything after the lock on M)

locking is not just about mutual exclusion; it is also about memory visibility. to ensure that all threads see the most up-to-date values of shared mutable variables, the reading and writing threads must synchronized on a common lock.

volatile variables, to ensure that updates to a variable are propagated predictably to other threads.
when a field is declared volatile, the compiler and runtime are put on notice that this variable is shared and that operatiosn on it should not be reordered with other memory operations.
valatile variables are not cached in registers or in caches where they are hidden form other processor, so a read of a volatile variable always returns the most recent write by andy thread.

when thread A writes to a volatile variable and subsequently thread B reads that same varable,
the values of all variables that were visibile to A prior to writing to the volatile variable become visible to B after reading the volatile variable.
A写入同变量前的所有可见变量都对B的读取同变量以后的代码可见

use volatile variables only when they simplify implementing and verifying your synchronization policy;
avoid using volatile variables when verifying correctness would require subtle reasoning about visibility.
good uses of volatile variables include ensuring the visibility of their own state, that of the object they
refer to, or indicating that an important lifecycle event(such as initialization or shutdown) has occurred.

checking a status flag to determine when to exit a loop.
volatile boolean asleep;
...
while (!asleep)
countSomeSheep();

the most commmon use for volatile variables is as a completion, interruption, or status flags.

Locking can guarantee both visibility and atomicity; volatile variables can only guarantee visibility.

can use volatile variables only when all the follwing criteria are met:
+ writes to the variable do not depend on its current value, or you can ensure that only a single thread
  ever updates the value
+ the variable does not participate in invariants with other state variables; and
+ Locking is not required for any other reason while the variable is being accessed.

this is a compelling reason to use encapsulation: it makes it practical to analyze programs for correctness
and harder to violate design constraints accidentally.

an object is in a predictable, consistent state only after its constructor returns, so publishing an object from within its constructor can publish an incompletely constructed object.
一个对象是可预测的，一致状态，仅仅是它的构造器返回了。
所以发布一个对象从构造器内，可能会有未完全构造的可能。

do not allow the this reference to escape during construction. 构造完成再放出this的引用

构造器内创建线程时，会将this暴露给线程，这时可能this还未正确完全构造完成。不要马上start，而是提供一个start方法开始

thread confinement, simplest ways to achieve thread safety.
when an object is confined to a thread, such usage is automatically thread-safe even if the confined object itself is not
jdbc池，只有借用的线程归还了才能再借，这就是线程安全

stack confinement is a special case of thread confinement in which an object can only be reached through local variable. is simpler to maintain and less fragile than ad-hoc thread confinement.

threadLocal alllows you to associate a per-thread value with a value-holding object.
Thread-Local provides get and set accessor methods that maintain a separate copy of the value for each thread that uses it, so a get returns the most recent value passed to set from the currently executing thread.
每个线程内的一份值的拷贝

like global variables, thread-local variables can detract from reusability and introduce hidden couplings among classes, and should therefore be used with case.
要有特殊用处采用，毕竟耦合何种，还很隐蔽，

if an object's state cannot be modified, these risks and complexities simply go away.
an immutable object is one whose state cannot be changed after construction. are inherently thread-safe,
their invariants are established by the constructor, and if their state cannot be changed, these invariants
always hold

An object is immutable if :
+ its state cannot be modified after construction;
+ all its fields are final; and
+ it is properly constructed (the this reference does not escape during construction)

it is a good practice to make all fields final unless they need to be mutable


javaMemory Model offers a special guarantee of initialization safety for sharing immutable objects.
immutable objects, can be safely accessed even when synchronization is not used to publish the object reference.不可变对象是字段是final的

To publish an object safely, bot the reference to the object and the object's state must be made visible
to other threads at the same time. A properly constructed object can be safely published by :
+ initializaing an object reference from a static initializer
+ Storing a reference to it into a volatile field or AtomicReference
+ Storing a reference to it into a final field of a properly constructed object; or
+ Storing a reference to it into a field that is properly guarded by a lock

effectively immutable:
Objects that are not technically immutable, but whose state will not be modified after publication

Safely published effectively immutable objects can be used safely by any thread without additional synchronization

The publication requirements for an object depend on its mutability:
+ immutable objects can be published through any mechanism
+ Effectively immutable objects must be safely published;
+ Mutable objects must be safely published, and must be either thread-safe or guarded by a lock

When you publish an object, you should document how the object can be accessed.


The most useful for using and sharing objects in a concurrent program are:
+ Thread-confined. A thread-confined objects is owned exclusively by and confined to one thread, and can
  be modified by its owning thread.
+ Shared read-only. A shared read-only object can be accessed concurrently by multiple threads without
  additional synchronization, but cannot be modified by any thread. Shared read-only objects include
  immutalbe and effectively immutable objects
+ Shared thread-safe. A thread-safe object performs synchronization internally, so multiple threads can
  freely access it through its public interface without further synchronization.
+ Guarded. A guarded object can be accessed only with a specific lock held. Guarded objects include
  those that are encapsulated within other thread-safe objects and published objects that are known to be
  guarded by a specific lock.
  对于要发布给别人用的状态，必须要保证安全的更新及可见性，对于方法有文档说明是安全的还是用户自己保证。


The design process for a thread-safe class should include these three basic elements:
+ Identify the variables that form the object's state;
+ Identify the invariants that constrain the state variables;
+ Establish a policy for managing concurrent access to the object's state

constrains: field state valid range, postcondition
Constrains placed on states or state transitions by invariants and post-conditions create additional
synchronization or encapsulation requirements.

You cannot ensure thread safety without understantding an object's invariants and post-conditions.
Constrants on the valid values or state transitions fro state variables can create atomicity and
encapsulation requirements.

Ownership inplies control

The ServerletContext object implemented by the servlet container must be thread-safe, because it will
necessarily be accessed by multiple threads.

Encapsulating data within an object confines access to the data to the object's methods, making it
easier to ensure that the data is always accessed with the appropriate lock held.

Instance confinement is one of the easiest ways to build thread-safe classes.

Confinement makes it easier to build thread-safe classes because a class that confines its state
can be analyzed for thread safety without having to examine the whole program.

if a class is composed of multiple independent thread-safe state variables and has no operations that
have any invalid state transitions, then it can delegate thread safety to the underlying state variables.

if a state variable is thread-safe, does not participate in any invariants that constrain its value,
and has no prohibited state transitions for any of its operations, then it can safely be published.
变量线程安全，不和其他有任何关联，没有参与任何一致性约束，则可以放心发布

reuse can reduce development effort, development risk, and maintenance cost.

document a class's thread safety guarantees for its clients; document its synchronization policy for
its maintainers.

Each use of synchronized, bolatile, or any thread-safe class reflects a synchronization policy defining
a strategy for ensuring the interity of data in the face of concurrent access. that policy is an element
of your programs design, and should be documented.

Crafting a synchronization policy requres a number of decisions: which variables to make volatile, which
variables to guard with lock, which lock(s) guard which variables, which variables to make immutable or
confine to a thread, which operations must be atomic, etc.

don't force clients to make risky guesses.
don't make your customers or colleagues have to make guesses like this.

you want to be compliant with the standard so your code works properly with any JDBC driver.

fail-fast - meaning that if they detect that the colleciton has changed since iteration begin, they throw
the unchecked ConcurrentModificationException.

just as encapsulating an object's state makes it easier to preserve its invariants, encapsulating its
synchronization makes it eaiser to enforce its synchronization policy

Replacing synchronized collections with concurrent collections can offer dramatic scalability improvements
with little risk

ConcurrentHashMap uses a finer-grained locking mechanism called lock striping to allow a greater concurrently with
writers, and a limited number of writers can modify the map concurrently.

The iterators returned by ConcurrentHashMap are wekly consistent instead of fail-fast.

the copy-on-wirte collections derive their safety from the fact that as long as an effectively immutable object
is properly published, no futher synchronization is required when accessing it.
are reasonable to use only when iteration is far more common than modification.遍历远大于修改

bounded queues are a powerful resource management tool for building reliable applications: they make your porgram
more robust to overload by throttling activities that threaten to produce more work than can be handled.

Synchronous queues are generally suitable only when there are enough consumers that there nearly always will be
one ready to take the handoff，直接传递，省去过一下队列的消耗

the producer-consumer pattern offers a thread-friendly means of decomposing the desktop search problem into simpler
components. Factoring file-crawling and indexing into separate activities results in code that is more readable
and reuseable than with a monolithic activity that does both; each of the activities has only a single task to do,
and the blocking queue handles all the flow control, so the code for each is simpler and clearer.

deques lend themselves to a ralted patter called work stealing.

a producer-consumer design has one shared work queue for all consumers; in a work stealing design, every consumer
has its own deque.
it a consumer exhausts the work in its own deque, it can steal work from the tail of someone else's deque.
多个队列，偷取还从后面开始

work stealing is well suited to problems in which consumers are also producers - when performing a unit of work is
likely to result in the identification of more work.不断产生新的task

threads may block, or pause, for several reasons: waiting for I/O completion, waiting to acquire a lock, waiting to
wake up from Thread.sleep, or waiting for the result of a computation in another thread.

when a method can throw InterruptedException, it is telling you that it is a blocking method, and further that if
it is interrupted, it will make an effort to stop blocking early.

blocking queues not only do they act as containers for objects, but they can also coordiante the control flow of
producer and consumer threads because take and put block until the queue enters the desired state(not empty or not full)

A synchronizer is any object that coordinates the control flow of threads based on its state.

All synchronizers share certain structural properties: they encapsulate state that determines whether threads arriving
at the synchronizer should be allowed to pass or forced to wait, provide methods to manipulate that state, and provide
methods to wait efficiently for the synchronizer to enter the desired state.

A latch is a synchronizer that can delay the progress of threads until it reaches its terminal state.
once the latch reaches the terminal state, it cannot change state again, so it remains open forever.
可用于：直到所有资源都已初始化则进行计算。其他依赖的服务都开始，本服务才开始。等待所有参与者都准备好，本参与者才能开始

future describes an abstract result-bearing computation.
the result-bearing equivalent of Runnable, and can be in one of three states: waiting to run, runnning, or completed.
Completion subsumes all the ways a computation can complete, including normal completion, cancellation, and exception.

FutureTask represents a computational process that may or may not already have completed.


main concepts and rules presented in Part I:
+ it's the mutable state, stupid.
  All concurrency issues boil down to coordinating to mutable state. the less mutable state, the easier it is to ensure thread safety
+ Make fields final unless they need to be mutable.
+ Immutable objects are automatically thread-safe
  Immutable objects simplify concurrent programming tremendously, they are simpler and safer, and can be shared freely
  without locking or defensive copying
+ Encapsulation makes it practical to manage the complexity.
  you could write a thread-safe program with all data stored in global variables, but why would you want to?
  Encapsulating data within objects makes it easier to preserve their invariants; encapsulating synchronization within
  objects makes it easier to comply with their synchronization policy.
+ Guard each mutable variable with a lock
+ Guard all variables in an invariant with the same lock
+ Hold locks for the duration of compound actions.
+ A program that accesses a mutable variable from multiple threads without synchronization is a broken program.
+ Don't rely on clever reasoning about why you don't need to synchronize.
+ Include thread safety in the design processor explicitly document that your class is not thread-safe
+ Document your synchronization policy.




Part II: Structuring Concurrent Applications

Most concurrent applications are organized around the execution of task: abstract, discrete units of work.
Dividing the work of an applicaiton into task simplifies program organization, facilitates error recovery by providing
natural transaction boundaries, and promotes concurrency by providing a natural structure for parallelizing work.

for greater flexibility in sheduling and load balancing tasks, each task should also represent a small fraction of your
applicaiton's processing capacity

Most server applications offer a natural choice of task boundary: individual client requests.

there are a number of possible policies for scheduling tasks within an application, some of which exploit the potential
for concurrency better than others.

each thread maintains two execution stacks, one for java code and one for native code.

up to a certain point, more threads can improve throughput, but beyond that point creating more threads just slows down
your application, and creating one thread too many can cause your entire application to crash horribly.

tasks are logical units of work, and threads are a mechanism by which tasks can run asynchronously.

The primary abstraction for task execution in the java class libraries is not Thread, but Executor.
it provides a standard means of decoupling task submission from task execution, describing tasks with Runnable.

Executor is based on the producer-consumer pattern, where activities that submit tasks are the producers(producing
units of work to be done) and the threads that execute tasks are the consumers(consuming those units of work).

The value of decoupling submission from execution is that it lets you easily specify, and subsequently change without
great difficulty, the execution policy for a given class of tasks.

Execution policies are a resource management tool, and the optimal policy depends on the available computing resources
and your quality-of-service requirements.


A thread pool, manages a homogeneous pool of worker threads. A thread pool is tightly bound to a work queue holding
tasks waiting to be executed. Worker threads have a simple life: request the next task from the work queue, execute it,
and go back to waitting for another task.

newFixedThreadPool, a fixed-size thread pool creates threads as tasks are submitted, up to the maximum pool size, and
then attempts to keep the pool size constant(一直一致，有意外则补上)

newCachedThreadPool, a acched thread pool has more flexibility to resp ilde threads when the current isze of the pool
execeed the demand for processing, and to add new threads when demand increses, but places no bounds on the size of the
pool  可动态增长收缩，不过线程没有界限

newSingleThreadExecutor, a single-threaded executor createds a single worker thread to process tasks, replacing it if
it dies unexpectedly. 单线程执行，意外停掉的线程还会补上

newScheduledThreadPool, a fixed-size thread pool that supports delayed and periodic task execution, similar to TImer

using an Executor opens the door to all sorts of additional ooprtunities for tuning, management, monitoring, logging,
error reporting, and other possibilityes that would have been far more difficult to add without a task execution framework

JVM can't exit until all the (non-daemon) threads have terminated

to address the issue of execution service lifecycle, the ExecutorService interface extends Executor, adding a number
of methods for lifecycle management.

The lifecycle implied by ExecutorService has three states - running, shutting down, adn terminated.
ExecutorServiecs are initially created in the running state.
The shutdown method initiates a graceful shutdown: no new tasks are accepted but previously submitted tasks are allowed
to complete - including those that have not yet begun execution.
The shutdownNow method initiates an abrupt shutdown: it attempts to cancel outstanding tasks and does not start any
tasks that are queued but not begun.

Once all tasks have completed, the ExecutorService transitions to the terminated state.
可以用awaitTermination等待直到这个状态，或者不断用方法isTerminated测试

Callable is a better abstraction: it expects that the main entry point, call, will return a value and anticipates that it might throw an exception.

The lifecycle of a task executed by an Executor has four phases: created, submitted, started, and completed.
in the Executor framework, tasks that have been submitted but not yet started can always be cancelled, and tasks that
have started can sometimes be cancelled if they are responsive to interruption.
Cancelling a task that has already completed has no effect.

Future represents the lifecycle of a task and provides methods to test whether the task has completed or been cancelled,
retrieve its result, and cancel the task.

getxx depending on the task state(not yet started, running, completed).
it returns immediately or throws an Exception if the task has already completed,
but if not it blocks until the task completeds.
if the task completes by throwing an exception, get rethrows it wrapped in an ExecutionException
if it was cancelled, get throws CancellationException
if gets throws ExecutionException, the underlying exception can be retrieved with getCause

obtaining significant performance improvements by trying to parallelize sequential heterogeneous tasks can be tricky.

the real performance payoff of dividing a program's workload into task comes when there are a large number of
independent, homogeneous task that can be processed concurrently.

the Executor framework permits you to decouple task submission from execution policy and supports a rich veriety
of execution policies;
to maximize the benefit of decomposing an application into tasks, you must identify sensible task boundaries.



java provides interruption, a cooperative mechanism that lets one thread ask another to stop what is is doing.

the cooperative approach is requird because we rarely want a task, thread, or service to stop immediately,
since that could leave shared data structrues in an inconsistent state.
instead, task and services can be coded so that, when requested, they clean up any work currently in progress and
then terminate. provides greater flexibility, since the task code itself is usually better able to assess the
cleanup required that is the code requesting cancellation.

Dealing well with failure, shutdown, and cancellation is one of the characteristics that distinguish a well-behaved
applicaiton from one that merely works.

Threre is nothing in the API or language specification that ties interruption to any specific cancellation semantics,
but in practice, using interruption for anything but cancellation is fragile and difficult to sustain in large app.

Blocking library methods like Thread.sleep and Object.wait try to detect when a thread has been interrupted and
return realy.
They respond to interruption by clearing the interrupted status and throwing InterruptedException, indicating that the
blocking operation completed early due to interruption.

设定装填，然后当进入阻塞方法时才会触发异常

Calling interrupt does not necessarily stop the target thread from doing what it is doing; it merely delivers the message
that interruption has been requestd.仅仅设定状态，要不要执行、什么时候执行，要看他自己感知和决定

well behaved methods may totally ignore such requests so long as they leave te interuption request in place so that calling
code can do something with it.忽略这种请求，保留中断请求，以便调用代码可以做些什么

Interruption is usually the most sensible way to implement cancellation

just as tasks should have a cancellation policy, threads should have an interruption policy.
An interruptino policy determines how a thread interprets an interruption request - what it does(if anything) when one
is detected, what units of work are considered atomic with respect to interruption, and how quickly it reacts to interruption

The most sensible interruption policy is some form of thread-level or service-level cancellatoin: exit as quickly as
practical, cleaning up if necessary, and possibly notifying some owning entity that the thread is exiting.

A single interrupt request may have more than one desired recipient interrupting a worker thread in a thred pool can
mean both "cancel the current task" and "shut down the work thread"

whether a task interprets interruption as cancellation or tasks some other action on interruption, it should take care
to preserve the executing thread's interruption status.
If it is not simply going to propagate InterruptedException to its caller, it should restore the interruption status
after catching InterruptedException:
`Thread.currentThread().interrupt()`

A thread should be interrupted only by its owner; the owner can encapsulate knowledge of the thread's interruption
policy in an prropriate cancellation mechanism such as shutdown method.
Because each thread has its own interruption policy, you should not interrupt a thread unless you know what interruption
means th that thread.


when you call an interruptible blocking methdo such as Thread.sleep or BlockingQueue.put, there are two practical
strategies for handling InterruptedException:
+ Propagate the exception (possibly after some task-specific clenup), making your method an interruptible blocking method,
+ Restore the interruption status so that code higher up on the call stack can deal with it

most code does not know what thread it will run in and should preserve the interrupted status.

Only code that implements a thread's interruption policy may swallow an interruption request. General-purpose task
and library code should never swallow interruption requests.

Synchronous socket I/O in java.io. The common form of blocking I/O in server applications is reading or writing to a
socket. the read and write methods in InputStream and OutputStream are not responsive to interruption, but closing
the underlying sokcet makes throw a SocketException.


it makes sense to thing of a thread as having an owner, and this is usually the class that created the thread. So a thread
pool owns its worker threads, and if those threads need to be interrupted, the thread pool should take care of it.

the service should provide lifecycle mtehods for shutting itself down that also shut down the owned threads; then the
application can shut down the service, and the service can shut down the threads.

Provide lifecycle methods whenever a thread-owning service has a lifetime longer than that of the method that created it.


the leading cause of premature thread death is RuntimeException.

the less familiar you are with the code being called, the more skeptical you should be abount its behavior

两种类型线程，normal and deamon. when the jvm starts up, all the threads it creates are deamon threads, except the main
thread. when a new thread is created, it inherits the daemon status of the thread that created it. 属性可继承

when a thread exits, the jvm performs an inventory of running threads, and if only threads that are left are deamon
threads, it initiates an orderly shutdown.

daemon threads are best saved for "housekeeping" tasks, such as a background thread that periodically removes expired
entries from an in-memory cache.

Deamon threads are not a good substitute for properly managing the lifecycle of services within an application.

in most cases, the combination of finally blocks and explicit close methdos does a better job of resource management
than finalizer

Avoid finalizers



Single-threaded forms an implicit coupling between the task and the execution policy - the tasks require their executor
to be single-threaded.

ThreadLocal makes sense to use in pool thteads only if the thread-local value has a lifetime that is bounded by that of
a task; Thread-local should not be used in pool threads to communicate values between tasks.

thread starvation deadlock:
if a tasks that depnd on other tasks execute in a thread pool, they can deadlock.
or in larger thread pools if all threads are executing tasks that are blocked waiting for other tasks still on the work
queue.

whenever you submit to an Executor tasks that are not independent, be aware of the possibility of thread starvation
deadlock, and doucuemnt any pool sizing or configuration constrains in the code or configuration file where the Executor
is configured.

Thread pools can have responsiveness problems if tasks can block for exnteded periods of time.
a thread pool can become clogged with long-running task, increasing the service time even for short tasks.
if the pool size is tool smal relative to the expected steady-state number of logrunning tasks, eventually all the pool
threads will be running long-running task and responsiveness will suffer
由于线程太少加上有长时间运行的task，导致所有服务都很慢
一种技术就是用时间控制task，若时间到则标记此task失败并中止他或重入队列。
if a thread pool is frequently full of blocked tasks, this may also be a sign that the pool


thread pool sizes should rarely be hard-coded; instead pool sizes should be provided by a configuration mechanism or
computed dynamically by consulting Runtime.availableProcessors.

to size a thread pool properly, you need to understand your computing environment, your resource budget, and the nature
of you tasks.
how may processors does the deployment system have? how much memory? Do tasks perform mostly computation, I/O or some
combination? Do they require a scarce resource, such as a JDBC connection? if you have different categories of tasks
with very different behaviors, consider using multiple thread pools so each can be tuned accroding to its workload

for compute-intensive tasks, an Ncpu-processor system usually achieves optimum utilization with a thread pool of Ncpu+1
threads.(Even compute-intensive threads occasionally take a page fault or pause for some other reason, so an "extra"
runnable thread prevents CPU cycles from going unused when this happends.)由于缺页异常或其他原因暂停，有额外线程补充
for tasks that also include I/O or other blocking operations, you want a larger pool, since not all of the threads will
be schdulable at all times. must estimate the ratio of waiting time to compute time for your tasks. the size of the
thread pool can be tuned by running the application using several different pool sizes under a benchmark load and
observing the level of CPU utilization

Nthreads = Ncpu * Ucpu * (1 + W/C)

other resources that can contribute to sizing constraints are memory, file handles, socket handles, and database
connections.
Calculating pool size constraints for these types fo resources is easier: just add up how much of that resouce each
task requires and divide that into the total quantity available. the rsult will be an uppoer bound on the pool size

the core size, the implementation attempts to maintain the pool at this size even when there are no tasks to execute,
and will not crate more threads than this unless the work queue is full.(是有不断地submit才能增长，除非prestartAllCoreThteads)

the maximum pool size is the upper bound on how many pool threads can be active at once.

a thread that has been idel for longer than the keep-alive time becomes a cnadidate for reaping and can be terminated
if the current pool size exceeds the core size.

the default for newFixedThreadPool and newSingleThreadExecutor is to use an unbounded LinkedBlockingQueue.
a more stable resource management strategy is to use a bounded queue, such as an ArrayBLockingQueue or a bound
LinkedBlockingQueue or Priority-BlockingQueye.
Bounded queues help prevent resource exhaustion but introduce the question of what to do with new tasks when the queue
is full.
With a bounded work queeu, the queue size and pool size must be tuned together.

SynchronousQueue is a practical choice only if the pool is unbounded or if rejecting excess tasks is acceptable.
The newCachedThreadPool factory use a SynchronousQueue. is a good default choice.
a fixed size thread pool is a good choice when you need to limit the number of concurrent tasks for resouce-management
purposes.

Bouding either the thread pool or the work queue is suitable only when tasks are independent.

there are a nmber of reasons to use a custom thread factory. might want to specify an uncaughtExceptionHandler for
pool threads, or instantiate an instance of a custom Thread class, such as one that performs debug logging. or maybe
want to give pool threads more meaningful names to simplify interpreting thread dumps and error logs.

ThreadPoolExecutor was designed for extension, providing several "hooks" for subclasses to override beforeExecute,
afterExecute, and terminate that can be used to extend the behavior of ThreadPoolExecutor

Loops whose bodies contain nontrivial computation or perform potentially blocking I/O are frequently good candidates
for parallelization, as long as the iteratoins are independent.

Sequential loop iterations are suitable for parallelization when each iteration is independent of the others and the
work done in each iteration of the loop body is significant enough to offset the cost of managing a new task.



Moder GUI frameworks use a model that is only slightly different: they create a dedicated event dispatch thread(EDT)
for handling GUI events.

tasks that evexute in the event thread must return control to the event thread quickly.
to initiate a longrunning task, you must run that task in another thread so control can reutnr quickly to the event thread

The Swing single-thread rule: Swing components and models should be created, modified, and queried only from the
event-dispatching thread.

there are a few exception.
+ a small number of swing methods may be called safely from any thread; be identified in javadoc
+ SwingUtilities.isEventDispatchThread, which determins wheter the current thread is the event thread;
+ SwingUtilities.invokeLater, which schedules a Runnable for execution on the event thread (callable from any thread)
+ SwingUtilities.invokeAndWait, which schedules a Runnable task for execution on the event thread and blocks the current
  thread until it completes(callable only from a non-GUI thread)
+ methods to enqueue a repaint or revalidation request on the event queue.(callable from any thread); and
+ methods for adding and removing listeners(can be called from any thread, but listeners will always be invoked in event thread)

Sequential, single-threaded execution is a sensible execution policy when tasks are short-lived, scheduling predictability
is not important, or it is imperative that tasks not execute concurrently.

short-running tasks, the entire action can stay in the event thread; for longer-running tasks, some of the processing
should be offloaded to another thread.

So long as tasks are short-lived and access only GUI objects(or other thread-confined or thread-safe applicaiton object),
you can almost totally ignore threading concerns and do everything from the event thread, and the right thing happens.

may execute tasks that may take longer than the user is willing to wait, must run in another thread so that the GUI
remians responsive while they run.
we can create our own Executor for processing long-running tasks.

a program that has both a presentation-domain and an applicationdomain data model is said to have a split-model design

if the data model is large or updates are very frequent, or if one or both sides of the split contain infomation that is not visible to the side, it can be more efficient to send incremental updtes instead of entire snapshots.

Consider a split-model design when a data model must be shared by more than one thread and implementing a thread-safe
data model would be inadvisiable because of blocking, consistency, or complexity reasons.
太多访问可能会有阻塞等问题，所以拆分+通知，可能更好

thread confinement is forced on the developer for reasons that have nothing to do with avoiding synchronization or deadloack

GUI frameworks are nearly always implementd as single-threaded subsystems in which all presentation-related code runs
as tasks in an event thread.



there is often a tension between safety and liveness.

a program will be free of lock-ordering deadlocks if all threads acquire the locks they need in a fixed global order.

invoking an alien method with a lock held is asking for liveness trouble.
the alien method might acquire other locks(risking deadlock) or block for an unexpectedly long time, stalling other
threads that need the lock you hold

calling a method with no locks held is called an open call, and classes that rely on open calls are more well-behaved
and composable than classes that make calls with locks held.

restricting yourself to open calls makes it far easier to identify the code paths that acquire multiple locks and
therefor to ensure that locks are acquired in a consistent order.

strive to use open calls throughout your program. Programs that rely on open calls are far easier to anlyze for
deadlock-freedom than those that allow calls to alien methods with locks held.

资源锁
+ 两个线程池用于db(x,y)，a线程用x而阻塞等待y，而b线程用y而阻塞等待x，导致死锁
+ thread-starvation deadlock, 当前线程等待线程池中的另一个task的结果，但是那个task可能执行不了，对于singleThreadExecutor

a program that never acquires more than one lock at a time cannot experience lock-ordering deadlock.

if must acquire multiple locks, lock ordering must be a part of your design: try to minimize the number of potential
locking interactions, and follow and document a lock-ordering protocol for locks that may be acquired together.

audit your code for deadlock freedom using a two-part strategy:
first, ientify where multiple locks could be acquired, and then perform a global analysis of all such instance to ensure
that lock ordering is consistent across your entire program.
using open calls whenever possible simplifies this analysis substantially.

another technique for detecting and recovering fro mdeadlocks is to use the timed tryLock feature of the explicit Lock class instead of intrinsic locking.

a thread dump includes a stack trace for each running thread, similar to the stack trace that accompanies an exception.
also include locking information, such as which locks are held by each thread, in which stack frame they were acquired,
and which lock a blocked thread is waiting to acquire.

avoid the temptation to use thread priorities, since they increase platform dependence and can cause liveness problems.
most concurrent applications can use the default priority for all threads.

poor responsiveness can also be caused by poor lock management.

活锁，可能发生在有错误时放入队列，但是又从对头取，循环往复
this form of livelock often coms from overeager error-recovery code that mistakenly treats an unrecoverable error as
recoverable one.

livelock can also occur when multiple cooperating threads change their state in response to the others in such a
way that no thread can ever make progress.像是窄胡同中俩人相遇，两个人朝同一方向移动
the solution for this variety of livelock is to introduce some randomness into the retry mechanism.
retrying with randome waits and back-offs can be equally effective for avoiding livelock in concurrent applications.


using threads can improve resource utilization by letting applications more easily exploit processing capacity, and
can improve responsiveness by letting applications begin processing new tasks immediately while existing tasks are still
running.

first make your program right, then make it fast - and then only if your performance requirements and measurements tell
you it needs to be faster.

improving performance means doing more work with fewer resources.

using multiple threads introduces some performance costs, include the overhead associated with coordinating between
threads(locking, signaling, and memory synchronization), increased context switching, thread creating and terdown,
and scheduling overhead.

in using concurrency to achieve better performance, we are trying to do two things: utilize the processing resources
we have more effectively, and enable our program to exploit additional processing resources if they becom available.

we want to keep the CPUs busy with useful work.

threading offers a means to keep the CPU(s) "hotter" by decomposing the application so there is always work to be done
by an available processor.

application performance can be measured in a number of ways, such s service time, latency, throughout, effeiciency,
scalability, or capacity.
service time, latency are measures of "how fast" a given unit of work can be processed or acknowledged;
capacity, throughput are measures of "how much" work can be performed with a given quantity of computing resources

Scalability describes the ability to improve throughput or capacity when additional computing resources(such as addition
CPUs, memory, storage, or I/O bandwidth) are added.

when tuning for performance, the goal is usually to do the same work with less effort, such as by reusing previously
computed results through caching or replacing an O(n^2) algorithm with an O(nlogn) one.减少资源使用及优化
when tuning for scalability, you are instead trying to find ways to parallelize the probelem so you can take advantage
of additional processing resources to do more work with more resources.并发并行

in order to achieve higher scalability or better hardware utilization, we often end up increasing the amount of work
done to process each individual task, such as when we divide tasks into multiple "pipelined" subtasks.

if you are asked to implement an efficient sort routine, you need to know something about the sizes of data sets it
will have to process, along with metrics that tell you whether you are trying to optimize average-case time, worst-case
time, or predicatability.

avoid premature optimization. first make it right, then make it fast - if it not already fast enough

Most performance decisions involve multiple variables and are highly situational.Before deciding that one approach is
"faster" than another, ask yourself some questions:
+ what do you mean by "faster"?
+ under what conditoins will this approach actually be faster? under light or heavy load? with large or small data
  set? can you support your answer with measurements?
+ how often are these conditions likely to arise in your situation? can you support your answer with measurements?
+ is this code likely to be used in other situations where the conditions may be different?
+ what hidden costs, such as increased development or maintenace risk, are you trading for this improved performance?
  is this a good tradeoff?

any performance tuning exercise be accompanied by concrete performance requrements(so you know both when to tune and
when to stop tuning) and with a measurement program in place using a realistic configuration and load profile.
measure again after tuning to verify that you've achieved the desired improvements.

measure, don't guess.

the free perfbar application

Amdahl's law:
F is the fraction of the calculation that must be executed serially,can achieve a speedup of at most:
Speedup <= 1 / (F + (1-F)/N)
若n无限，则最大速度是1/F，1/2的处理要执行顺序化，

in order to predict what kind of speedup is possible from running your application on a multiprocessor system,
you also need to identify the sources of serialization in your tasks.

all concurrent applications have some sources of serialization

performance optimizations should always be considered in light of actual performance requirements.

scheduling and interthread coordiantion have performance costs; for therads to offer a performance improvement,
the performance benefits of parallelization must outweigh the costs introduced by concurrency.

if there are more runnable threads than CPUs, eventually the OS will preempt one thread so that another can use the CPU,
this causes a context switch, which requires saving the execution context of the currently running thread and restoring
the execution context of the newly scheduled thread.

when a thread blocks because it is waiting for a contended lock, the JVM usually suspends the thread and allows it to be
switched out.
a program that does more blocking(blocking I/O, waiting for contended locks, or waiting on condition variables) incurs
more context switches.

vmstat command report the number of context switches and percentage of time spent in the kernel.
high kernel usage(over 10%) often indicates heavy scheduling activity, which may be caused by blocking due to I/O or
lock contention.

the visibility guarantees provided by synchronized and volatile may ential using special instructions called memory
barriers that can flush or invalidate caches, flush hardware write buffers, and stal execution pipelines.

if a lock object is accessible only to the current thread, the JVM is permitted to optimize away a lock acquisition.
more sophisticated JVMs can use escape analysis to identify when a local object reference is never published to the
heap and is therefore thread-local.
and stack-confined variables are automatically thread-local. 方法内用的vector，高级jvm会减少锁，lock elision

compilers can also perform lock coarsening, the merging of adjacent synchronized blocks using the same lock.

Don't worry excessively about the cost of uncontended synchronization. the basic mechanism is already quit fast, and
JVMs can perform additional optimizations that further reduce or eliminate the cost. instead, forcus optimization
efforts on areas where lock contention actually occurs.更多关注有竞争的锁使用

the jvm can implement blocking either via spin-waiting or by suspending the blocked thread through the operating system.
which is more eeficient depends on the realationship between context switch overhead and the time until the lock becomes
available
spin-waiting is preferable for short waits and suspension is prferable for long waits.
some jvms choose between the two adaptively based on profiling data of past wait times, but most just suspend thrads
waiting for a lock.

serialization hurts scalability and context switches hurt performance. contended locking causes both.

the principal threat to scalability in concurrent applications is the exclusive resource lock.

two factors influence the likelihood of contention for a lock: how often that lock is requested and how long it si held
once acquired.


There are three ways to reduce lock contention:
+ Reduce the duration for which locks are held
+ Reduce the frequency with which locks are requested; or
+ Replace exclusive locks with coordiantion mechanisms that permit greater concurrency.

hold locks as briefly as possible. moving code that doesn't require the lock out of synchronized blocks, especially for
expensive operations and potentially blocking operations such as I/O

if lock requests were instaead distributed over a larger set of locks, there would be less contention.
fewer threads would be blocked waiting for locks, thus increasing scalability.

lock splitting can sometimes be extended to partition locking on a variablesized set of independent objects, in which
case it is called lock striping.像是concurrenthashmap
one of the downsides of lock striping is that locking the collection for exlusive access is more difficult and costly
than with a single lock.全局锁就不好弄了

lock granularity cannot be reduced when there are variables that are required for every operation. common optimizations
such as caching frequently computed values can introduce "hot fields" that limit scalabiliy
例如hashmap的size计算，简单的方式是调用size时计算，优化方案是添加和删除时修改count，轻微的增长put/remove消耗但是size方法从
O(n)->O(1)，但是这样每次操作都会更新共享数据count，这个优化用了把size给cache，这个count被称为host field
concurrenthashmap避免此问题，每个stripe各自累加

读写锁，就比普通锁要好，对于读多的场景。

for read-only data structures, immutability can eliminate the need for locking entirely

if your calss has a small number of host fields that do not participate in invariants with other variables, replacing them
with atomic variables may improve scalability.

tools vmstat and mpstata can tell you just how "hot" the proessors are running.

Asymmetric utilization indicates that most fo the computation si going on in a small set of threads, and your applicatoin
will not be able to take advantage of additional processors.

if the CPUs are not fully utilized, you need to figure out why. threa are several likely causes:
+ insufficient load.
+ I/O bound. test an application is disk-bound using iostat or perfmon, and whether it is bandwith-limited by monitoring
  traffic levels on your network.
+ Externally bound. can test for this by usign a profiler or database administration tools to determine how much time
  is being spent waiting for answers from the external service.
+ Lock contention. Profiling tools can tell you how much lock contention your app is experiencing and which locks are
  "hot". or through random sampling, triggering a few thread dumps and looking for threads contending for locks.

one of the columns reported by vmstat is the number of threads that are runnable but not currently running because a
CPU is not available

allocating objects is usually cheaper than synchronizig.

we should hold locks as briefly as possible.

moving the I/O out of the request-processing thread is likely to shorten the mean service time for request processing.
通过队列，单consumer可以专心处理I/O，减少了producer的时间、锁竞争、上下文切换。


developing unit tests - identifying invariants and post-conditinos that are amenable to mechanical checking.

the chanllenge to constructing effective safety tests for concurrent classes is identifying easily checked properties
that will, with high probability, fail if something goes wrong, while at the same time not letting the failure-auditing
code limit concurrency artificailly. it is best if checking the test property does not require any synchronization.
面向并发测试良好的代码

test data should be generated randomly

to maximize the chance of detecting timng-sensitive data races, there should be more active threads than CPUs, so that
at any given time some threads are running and some are switched out, thus reducing the predictability of interactions
between threads.

callbacks to client-provided code can be helpful in constructing test cases;
callback are often made at known points in an object's lifecycle that are good opportunities to assert invariants.

a useful trick for increasing the number of interleavings, and therefore more effectively exploring the state space of
your programs, is to use Thread.yield to encourage more context switches during operations that access shared state.

it may be somewhat puzzling at first that adding a lot more threads degrades performance only slightly.
the reason is hard to see from the data, but easy to see on a CPU performance meter such as perfbar while the test
is running: even with many thread, not much computation is going on, and most of it is spent blocking and unblocking
threads.

throughput of LinkedBolocking/ArrayBlockingQueue/SemaphoreBoundedBuffer
even though it has more allocation and GC overhead, alinked queue allows more concurent acess by puts and tasks than an
array-based queue because the best linked queue algorithms allow the head and tail to be updated independently.
bacause allocation is usually threadlocal, algorithms that can reduce contention by doing more allocation usually
scale better.

sometimes it is more important to know how long an individual action might take to complete, and in this case we want to
measure the vaiance of service time.

Histograms of task completion tiems are normally the best way to visulize variance in service time.
Variances are only slightly more difficult to measure than averages - you need to keep track of per-task completion times
in addition to aggregate completion time.
nonfair semaphores provide much better throughput and fair semmaphores provides lower vaiance.

there are two strategies for preventing garbage collection from biasing your result.
+ ensure that garbage collection dose not run at all during your test(invoke the JVM with -verbose:gc to find out)
+ make sure that the garbage collector runs a number of times during your run so that the test program adequately reflects
  the cost of ongoing allocation and garbage collection.
  the latter strategy is often better it requires a longer test and is more likely to reflect real-world performance.

when a class is first loaded, the JVM executes it by interpreting the bytecode. at some point, if a method is run often
enough, the dynamic compiler kicks in and converts it to machine code; when compilation completes, it switchs from
interpretation to direct execution.

one way to prevent compliation fro mbiasing your results is to run your program for a long time so that compilation
and interpreted execution represent a small fraction of the total run time.
another approach is to use an unmeasured "warm-up" run, in which your code is executed enough to be fully compiled
when you actually start timing.
-XX:+PrintCompilation prints out a message when dynamic compilation runs

running the same test several times in the same JVM instance can be used to validate the testing methodoloty.
the first group of results should be discarded as warm-up; seeing inconsistent results in the remaining groups
suggests that the test should be examined further to determin whey the timing results are not repeatable.

runtime compilers use profiling information to help optimize the code being compiled.
jvm is permitted to use information specific to the execution in order to produce better code

concurrent applications tend to interleave two very different sorts of work:
+ accessing ahred data, such as fetching the next task from a shared work queue
+ thread-local computation

if n threads are fetching tasks from a shared work queue and executing them, and the tasks are compute-intensive and
long-running, there will be almost no contention; throughput is dominated by teh availability of CPU resources.
on the other hand, if the tasks are very short-lived, there will be a lot of contention for the work queue and throughput
is dominatedby the cost of synchronization.

you should still prefer -server to -client for both production and testing on multiprocessor systems.
编译器+虚拟机太强大，可能会对测试用例进行删减或者优化。。

the goal of testing is not so much to find errors as it is to increase confidence that the code works as expected.

code review is no substitue for testing.

notify and notifyall should be called only when the state associated with the condition queue has changed.
a synchronized block that calls notify or notifyall but dose not modify any state is likely to be an error.

when waiting on a condition queue, objcet.wait or condition.await should be called in a loop, with teh appropriate lock
held, after testing some state predicate.

Calling Thread.sleep with a lock held can prevent other threds from making progress for a long time and is therefor
a potnetially serious liveness hazard.



reenttrantlock implements lock, providing the same mutual exlusion and memory-visibility guarantees as synchronized.

using timed or pooled lock acquisition(TryLock) lets you regin control if you cannot acquire all the required locks,
release the ones you did acquire, and try again.

Performance is a moving target

one reason barging locks perform so much better than fair locks under heavy contention is that there can be a significant
delay between when a suspended thread is resumed and when it actually runs.

ReentrantLock is an advanced tool for situations where intrinsic locking is not practical. Use it if you need its
advanced features: timed, pooled, or interruptible lock acquisition, fair queuing, or non-block-structured locking.

read-write lock: a resource can be accessed by multiple readers or a single writer at a time, but not both
in practice, read-write locks can improve performance for frequently accessed read-mostly data structures on multiprocessor
system;
whether they are an imrpovement in any given situation is best determined via profilin.

read-write locks can imrpove concurrency when locks are typically held for a moderately long time and most operations
do not modify the guarded resources.

using Reentrantlock only when you need features that synchronized lacks.




state-dependent methods on concurrent objects can sometimes get away with failing when their preconditions are not met,
but there is often a better alternative: wait for teh precondition to become true.

Document the condition predicates asscociate with a condition queue and teh operations that wait on them

an important three-way relationship in a condition wait involving locking, the wait method, and a condition predicate.

every call to wait is implicitly associated with a specific condition predicate. when calling wait regarding a prticular
condition predicate, the caller must already hold the lock associated with the condition queue, and that lock must also
guard the state vairables from which the condition predicate si composed.

when you wake up from wait you must test the condition predicate again, and go back to waiting(or fail) if it is not true

when using condition waits(Object.wait or Condition.await):
+ Always have a condition predicate some test of object state that must hold before proceeding.
+ Always test the condition predicate before calling wait, and again after returning from wait
+ Always call wait in a loop
+ Ensure that the state variables making up the condition predicate are guarded by the lock associated with the condition
  queue;
+ Hold the lock associtated with the condition queue when calling wait, notify, or notifyAll; and
+ Do not realease the lock after checking the condition predicate but before action on it
  lock，谓词与lock绑定，和lock绑定的condition queue，三者紧密相连

liveness failures:
+ deadlock
+ livelock
+ missed signals

A missed signal occurs when a thread must wait for a specific condition that is already true, but fails to check the
condition predicate before waiting. wait前一定要先检查predicate

whenever you wait on a condition, make sure that someone will perform a notification whenever the condition predicate
becomes true

the notifying thread should release the lock quickly to ensure that the waiting threads are unblocked as soon as possible

because multiple threads could be waiting on the same condition
(a/b线程等待不同条件，但是c通知一个b，可能唤醒a则b就无法被唤醒了)  queue for different condition
predicates, using notify instead of notifyAll can be dangerous, primarily because single notification is prone to a
problem akin to missed signals.

Single notify can be used instead of notifyAll only when both of the following conditions hold:
+ Uniform waiters. Only one condition predicate is associated with the condition queue, and each thread executes the
  smae logic upon returning from wait; and 同一种
+ One-in, one-out. A notification on the condition variable enables at most one thread to proceed. 1对1

notifyAll可以保证正确，但是可能产生惊群现象

first make it right, and then make it fast if it is not already fast enough,不过也要适当提前考虑。要有自己的规则

a state-dependent calss should either fully expose(and document) its waiting and notification protocols to subcalsses,
or prevent subcalsses from participating in them at all.

design and document for inheritance, or else prohibit it

encapsulate the condition queue so that it is not accessible outside the calss hierarchy in which it is used.

should define and document an entry and exit protocol.
the entry protocol is the operation's condition predicate; the exit protocol involves examining any state variables
that have been changed by the operation to see if they might have caused some other condition predicate to become true,
and is so, nofifying on the associated condition queue.

just as Lock is a generalization of intrinsic locks, Condition is a generalization of intrinsic condition queues.
intrinsic lock have only one associated condition queue.

Condition offers a richer feature set than intrinsic condition queues: multiple wait sets per lock, interruptible and
uninterruptible condition waits, deadline-based waiting, and a choice of fair or nonfair queuing.

use Condition fi you need its advanced features such as fair queueing or multiple wait sets per lock

AQS is a framework for building locks and synchronizers, and a suprisingly broad range of synchronizers can be built
easily and efficiently using it.
AQS was designed for scalability, and all the synchronizers in java.util.concurrent that are built with AQS benefit from this
seeing how the standard synchronizers are implements can help clarify how they work

the basic operations that an AQS-based synchronizer performs are some variants of acquire and release.
Acquisition is the state-dependent operation and can always block. Release is not a blocking operation

AQS takes on the task of managing some of the state for the synchronizer class: it manages a single integer of state
information that can be manipulated through the protected getState, setState, and compareAndSetState methods.

a synchornizer supporting exclusive acquisition should implement the tryAcquire, tryRelease, isHeldExecusively,
and those supporting shared acquisition should implement tryAcquireShared and tryReleaseShared.
aqs通过这些方法回调并定义返回类型，得知是该如何处理acuqire or release



Non-blocking algorithms are considerably more complicated to design and implement than lock-based alternatvies, but
they can offer significant scalability and liveness advantages.

Atomic variables can also be used as "better volatile variables" even if you are not developing non-blocking algorithms.
them ideal for counters, sequence generators, and statistics gathering while offering better scalability.

locking disadvantages:
suspending and resuming a thread has a lot of overhead and generally entails a lengthy interruption.
when a thread is waiting for a lock, it cannot do anything else
if a thread holding a lock is permanently blocked, any threads waiting for the lock can never make progress.
locking si simply a heavyweight mehcanism for fine-grained operations

volatile provide similar visibility guarantees, they cannot be used to construct atomic compound actions.
cannot be used when one variable depends on another, or when the new value of a variable depends on its old value

because a thread that loses a CAS is not blocked, it can decide whether it wants to try again, take some other recovery
action, or do nothing.

in reality, CAS-based counters significantly outperform lock-based counters if there is even a small amount of contention,
and often even if there is no contention.

with algorithms based on atomic variables instead of locks, threads are more likely to be able to proceed without delay
and have an easier time recovering if they do experience contention.

an algorithm is called non-blocking
if failure or suspension of any thread cannot cause failure or suspension of anohter thread
an algorithm is called lock-free
if at each step, some thread can make progress.

algorithms that use CAS exlusively for coordination between threads can, if constructed correctly, be both non-blocking
and lock-free.

the key to creating non-blocking algorithms is figuring out how to limit the scope of atomic changes to a single
variable while maintaining data consistency.

non-blocking algorighms: some work is done speculatively and may have to be redone.

non-blocking algorithms derive their thread safety from the fact that, like locking, compareAndSet provides both
atomicity and visibility guarantees.
compareAndSet has the memory effects of a volatile write. get has the memory effects of a volatile read.

the trick to building non-blocking algorithms is to limit the scope of atomic changes to a single variable.

many of the advances in concurrent performance from one JVM version to the next come from the use of non-blocking
algorighms, both within the JVM and in the platform libraries.



`aVariable = 3`
a memory model addresses the question "Under what conditions does a thread that reads aVariable see the value 3"
in the absence of sdynchronization, there are a number fo a reasons a thread might not immediately - or ever - see the
results of an operation in another thread,
Compielers may generate instructions in a different order than the "obvious" one suggested by the source code, or store
variables in registers instead of in memory; processors may execute instructions in parallel or out of order; caches
may vary the order in which writes to variables are committed to main memory; and values stored in processor-local caches
may not be visible to other processors.
These factors can prevent a thread from seeing the most up-to-date value for a variable and can cause memory actions
in other threads to appear to happen out of order - if you don't use adequate synchronization.

java language specification reuires the jvm to maintain within thread as-if-serial semantics: as long as the program has
same result as if it were executed in program order in a strictly sequential environment, all these games are premissible.

the JMM specifies the minimal guarantees the JVM must make about when writes to variables become visible to other threads.
it was designed to balance the need for predictability and ease of progarm development with the realities of implementing
high-performance JVMs on a wide range of popular processor architectures.

in order to shield the java developer from the differences between memory models across architectures, java provides
its own memory model, and the JVM deals with the differences between the JMM and the underlying platform's memory
model by inserting memory barriers at the appropriate places.



the actions in each thread have no dataflow dependence on each other, and accrodingly can be executed out of order.
Reordering at the memory level can make programs behave unexpectedly.

Synchronization inhibits the compiler, runtime, and hardware from reordering memory operations in ways that would violate
the visibility guarantees provided by the JMM

on most pupular processor architectures, the memory model is strong enough that the performance cost of a volatile read
is in line with that of a nonvolatile read.

the Java Memory Model is specified in terms of actions, which include reads and writes to variables, locks and unlocks
of monitors, and starting and joining with threads.
the JMM defines a partial ordering called happens-before on all actions within the program.


the rules for happens-before are:
+ Program order rule. Each action in a thread happens-before every action in that thread that comes later in the program
  order.线程内执行顺序如程序源码一样
+ Monitor lock rule. An unlock on a monitor lock happens-before every subsequent lock on that same monitor lock.放锁前于后来的获取锁
+ Volatile variable rule. A write to a volatile field happends-before every subsequent read of that same fiedl.
  写入一个volatile字段值，先于每个后续的读取操作
+ Thread start rule. A call to Thread.start on a thread happens-before every action in the started thread.
  start方法先于线程内的任意操作
+ Thread termination rule. Any action in a thread happens-before any other thread detects that thread has terminated,
  either by successfully return from Thread.join or by Thread.isAlive returning false.
  线程内任意操作，前于其他依赖检测当前线程是否终止，Thread.join成功返回/Thread.isAlive返回false
+ Interruption rule. A thread calling interrupt on another thread happens-before the interrupted thread detects the
  interrupt(either by having InterruptedException thrown, or invoking isInterrupted or interrupted.)
  调用interrupt先于被拦即线程感知到
+ Finalizer rule. The end of a constructor for an object happens-before the start of the finalizer for that object
  构造器的结尾前于终结器的开始
+ Transitivity. If A happens-before B, and B happens-before C, then A happends-before C.


Because A releases lock M and B subsequently acquires M, all the actions in A before releasing the lock are therefore
ordered before teh actions in B after acquiring the lock.


Other happens-before orderings guaranteed by the class library include:
+ Placing an item in a thread-safe collection happens-before another thread retrieves that item from the collection;
  对于同步集合，放入前于获取同一元素
+ Counting down on a CountDownLatch happends-before a thread returns from await on that latch;
  countdown方法前于await返回
+ Releasing a permit to a Semaphore happens-before acquiring a permit from that same Semaphore;
  release前于acquire方法
+ Actions taken by the task represented by a Future happens-before another thread successfully returns from Future.get
  动作执行前于get
+ Submitting a Runnable or Callable to an Executor happens-before the task begins execution; and
  提交任务前于开始执行
+ A thread arriving at a CycliBarrier or Exchanger happens-before the other threads are released from that same barrier
  or exchange point. If CyclicBarrier uses a barrier action, arrving at the barrier happens-before the barrier action,
  which in turn happens-before threads are released from the barrier.
  到达前于release，到达前于barrierAction前于release


if you do not ensure that publishing the shared reference happens-before another thread loads that shared reference,
then the write of the reference to the new object can be reordered with the writes to its fields.

With the exception of immutable objects, it is not safe to use an object that has been initialized by another thread
unless the publication happens-before the consuming thread uses it.

because the BlockingQueue implementations have sufficient internal synchronization to ensure that the put happens-before
the take.
Similarly, using a shared variable guarded by a lock or a shared volatile variable ensures that reads and writes of
that variable are ordered by happens-before.

the treatment of static fields with initialiers is somewhat and offers additional thread-safey guarantees.
Static initializers are run by the JVM at class initialization time, after class loading but before the class is used
by any thread. 加载之后，使用之前，进行静态初始化，jvm获取锁，仅能用于不可变且在构造时初始化的属性

Initialization safety guarantees that for properly constructed objects, all threads will see the correct values of final
fields that were set by the constructor, regardless of how the object is published. Further, any variables that can
be reached through a final field of a properly constructed object are also guaranteed to b evisible to other threads.

for objects with final fields, initialization safety prohibits reordering any part of construction with the initial
load of a reference to that object.

Initialization safety makes visibility guarantees only for the values that are reachable through final field as of
the time the constructor finishes. for values reachable through non-final fields, or values that may chagne after
construction, you must synchronization to ensure visibility.
