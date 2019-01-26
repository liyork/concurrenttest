####AQS（AbstractQueuedSynchronizer）是一个用于构建锁和同步器的框架，许多同步器都可以通过AQS很容易并且高效的构造出来。

在基于AQS构建的同步器类中，最基本的操作包括各种形式的获取操作和释放操作。

获取操作是一种依赖状态的操作，并且通常会阻塞直到达到了理想的状态。
释放并不是一个可阻塞的操作，当执行“释放”操作时，所有在请求时被阻塞的线程都会开始执行

AQS提供了一个高效的状态机模型，用来管理同步器类中的状态和。
AQS使用一个整数state以表示状态，并通过getState、setState及compareAndSetState等protected类型方法进行状态转换

state表示状态，其他状态需要自行维护
直接使用不带try前缀的方法，并覆写带try前缀的方法，通过查看protected方法上注解说明返回相应值
而其他非私有方法则使用final修饰，禁止子类覆写

若需要阻塞则用队列，否则直接返回false

####ReentrantLock：
ReentrantLock用state表示“所有者线程已经重复获取该锁的次数”
addWaiter先设定newnode.prev = pred;若compareAndSetTail(pred, node)成功则pred.next = node;

队列刚完成初始化时，存在一个dummy node。插入节点时，tail后移指向新节点，head不变仍然指向dummy node。
直到调用AQS#acquireQueued()时，head才会后移，消除了dummy node。相当于哨兵，不用判断null。

shouldParkAfterFailedAcquire会移除前面的所有取消的节点。

为什么不能直接在AQS#parkAndCheckInterrupt()返回时直接中断？因为返回中断标志能提供更大的灵活性，外界可以自行决定是即时重放、
稍后重放还是压根不重放。Condition在得知AQS#acquireQueued()是被中断的之后，便没有直接复现中断，而是根据REINTERRUPT配置决定是否重放。

ReentrantLock#lock()收敛后，AQS内部的等待队列
除了头节点，剩余节点都被阻塞，线程处于WAITING状态。
除了尾节点，剩余节点都满足waitStatus==SIGNAL，表示释放后需要唤醒后继节点。

waitStatus; // 可取值 0(None of the above)、CANCELLED(1)、SIGNAL(-1)、CONDITION(-2)、PROPAGATE(-3)   

//todo lock应该放在try外面吗？是否会抛出异常？
理论上不要在没有lock时进行unlock否则会抛出异常，可是lock若不在try中，可能会产生异常发生，所以还是在try中进行lock，理论是不支持打断而且
一直等待，在finaly中操作if (reentrantLock.isHeldByCurrentThread())
lock和release中的exclusiveOwnerThread状态依赖于state的volatile的happen-before原则
通过查看CyclicBarrier是放在try之外，而且reentrantlock使用说明也是放在外面

unparkSuccessor
为什么要从尾节点向前遍历，而不能从node向后遍历？这是因为，AQS中的等待队列基于一个弱一致性双向链表实现，允许某些时刻下，
队列在prev方向一致，next方向不一致。
先做的node.prev = t; 然后只有if (compareAndSetTail(t, node)) 成功才执行 t.next = node;，所以这时发生问题？导致pre设置成功，但是
next设置错误？还是由于cancel的原因？

Lock接口对标内置锁，而Condition接口对标内置条件队列

FairSync.tryAcquire，先看下c == 0还要看下if (!hasQueuedPredecessors() && compareAndSetState(0, acquires))可见c的volatile
并不能保证原子，前面判断是为了减少竞争，而后面compareAndSetState才是真正上锁！而且c用的是getState的本地临时变量，只有临时变量才知道有没有
变化过。整体看来先用临时判断是否有竞争，没有则自己竞争，有则自己直接放弃。没有直接cas的目的也是先用本地存放状态，然后进行一些简单操作或者验证，
然后当真正执行时再用cas，这样避免了过长等待,addWaiter的先设前置node也是这个意思。

addWaiter先new Node之后设定Node pred = tail临时变量，若是pred != null，则可操作，否则循环入队列。之后node.prev = pred; 
以上所有操作都是先在cas之前准备一下，若是compareAndSetTail(pred, node)成功则pred.next = node;这样首尾呼应。Try the fast path of enq
人家也没有总是判断一下队列空等条件，因为这是个fast，下面enq就会初始化或者循环入队。那这么看，再发送notify之前判断是否有callback还在也是有必要的。

enq也是设定临时变量Node t = tail;，然后判断if (t == null)，然后compareAndSetHead(new Node())成功了，再设定tail = head;，这时tail和head
都可以被外界用了，不过是个dummy节点。然后又进行for的入队addWaiter。Node t = tail;放在了for里面就是初始化完后再次for时，找下最新的，和其他
刚来的线程一样操作即可，没有特别对待初始化的线程。
阻塞队列不包含head节点，head一般指的是占有锁的线程，head后面的才称为阻塞队列

acquireQueued循环抢占或者等待，之后当前node的pre是head才有抢占的可能，tryAcquire成功才可以，否则阻塞

（进入阻塞队列排队的线程会被挂起，而唤醒的操作是由前驱节点完成的，所以下面要设定前驱节点为status=SIGNAL才能休眠，status是为了唤醒后面的node的）
shouldParkAfterFailedAcquire前驱
a.若是Node.SIGNAL——status asking a release to signal it
b.若是ws > 0，表明Predecessor was cancelled，需要循环前找到非取消节点，正好重新设定了引用忽略了中间的cancell节点。再循环时就是a判断了
c.若非-1、1，则设定前驱为Node.SIGNAL之后循环进入a判断
只有确定前置是SIGNAL才返回true等待，否则(清空前置cancell、设定前置SIGNAL)否返回false，则下次循环时后还可以顺便看看自己是否有竞争机会即不用等
了，若还得等，然后进入shouldParkAfterFailedAcquire，再看前置若是SIGNAL就等了。

release只有获取锁的才能操作，所以不需要竞争。
if (h != null && h.waitStatus != 0)则需要唤醒
if (ws < 0) compareAndSetWaitStatus(node, ws, 0); It is OK if this fails or if status is changed by waiting thread.因为
其他人修改了此时的head节点也是为了入队列，对于获取失败的线程，会先入队，也就意味着head之后只有一个节点，所以这个节点会和当前线程一起操作head。
选择一个非取消的节点进行唤醒。

使用了dummynode，每次入队都是在队列后面添加，从dummynode开始连接-->node，然后被唤醒的节点一定是dummynode之后的第一个节点，才能再从尝试获取
锁然后替换head为后一个节点,然后设定此节点为新dummynode

通知后继节点时若之后node为null或者被取消则从tail开始，因为null或者cancel的节点的next就找不到后来了。。
而且addWaiter是先node.prev = pred;然后cas对tail，这时若从前向后那么 pred.next = node;还没来得及设定肯定是后续找不到的

a线程获取锁后执行，这时b线程执行准备等待进行park，但是a进行了unpark，b是不是就一直等待？
不会。。park之前有人已经unpark， 那么在park时就不会阻塞了。


hasQueuedPredecessors为什么先获取tail再获取head？
addwaiter时先放入head之后是tail，当取head、tail顺序，当head之前插入节点，那么h != t为false，若head和tail之间插入节点，
s = h.next ==null返回false，若是tail之后插入node，则h != t返回false。
要是先获取head再获取tail，那么head之后插入node，h.next就会空指针！

阻塞队列不包含 head 节点，head就是个dummy节点，当前持有锁者会将其的thread设定null，不会和addwaiter产生冲突，减少并发控制??
Note: If head exists, its waitStatus is guaranteed not to be，是不是unparkSuccessor就不用知道到头了？

acquireQueued方法没有问题。。。因为正常返回false，要是被interrupt醒来后会返回Thread.interrupted，之后设定interrupted，
然后获取锁了就返回interrupted。然后外面acquire进行selfInterrupt

shouldParkAfterFailedAcquire把前置节点设置-1后为true

parkAndCheckInterrupt睡眠，醒来后检查是否被中断唤醒的还是被前置节点唤醒的

head永远是dummy节点，永远是持有锁者自己清空的head内thread数据。

NonfairSync会在lock时直接抢占一下，失败，则状态是0再抢占一下，否则入队列
FairSync在lock时，先看状态再看队列，都允许才抢占，否则入队列

####condition
阻塞队列（或sync queue），用于保存等待获取锁的线程的队列。条件队列（condition queue）

node结构：
volatile int waitStatus;
volatile Node prev;
volatile Node next;
volatile Thread thread;
Node nextWaiter;
prev 和 next 用于实现阻塞队列的双向链表，nextWaiter 用于实现条件队列的单向链表
ConditionObject 只有两个属性 firstWaiter 和 lastWaiter；
每个 condition 有一个关联的条件队列，如线程 1 调用 condition1.await() 方法即可将当前线程 1 包装成 Node 后加入到条件队列中，
然后阻塞在这里，不继续往下执行，条件队列是一个单向链表；
调用 condition1.signal() 会将condition1 对应的条件队列的 firstWaiter 移到阻塞队列的队尾，等待获取锁，获取锁后 await 方法返回，
继续往下执行。

unlinkCancelledWaiters中trail记录的是被取消节点的前一个节点，用于直接指向取消节点的next的。而且若有取消节点，若此时trail为null则
表明目前还没有非取消节点，所以移动firstWaiter = next

addConditionWaiter,条件队列中的node状态是CONDITION，单项链表

enq中先设定node.prev = t;然后cas的tail，这时若失败，那么之后的t.next = node是不会有的，但是一旦cas对tail成功后，t.next = node
肯定有的，那么findNodeFromTail就是这个思想，tail肯定是有的,那么prev也是有的。

await会一直等到自己加入了阻塞队列或者被中断才会开启acquireQueued，否则一直park

signal先会断开node在当前条件队列first.nextWaiter = null;，之后会从firstWaiter开始移动一个非取消的node到阻塞队列，
调用的enq并设定当前为CONDITION，前置为SIGNAL。若当前节点被取消则nextWaiter,若前置节点被取消或者cas设定SIGNAL失败则直接唤醒当前节点。

transferAfterCancelledWait返回是在signal之前还是后被中断的，signal之前被中断，肯定是CONDITION状态，所以自己转换进去阻塞队列，但是不
会断开在条件队列的关系。所以需要在await中判断node.nextWaiter != null然后清除unlinkCancelledWaiters。而不是CONDITION状态则是有
signal进行，所以等待signal将自己放入阻塞队列再返回



#####CountDownLatch
常常会将一个比较大的任务进行拆分，然后开启多个线程来执行，等所有线程都执行完了以后，再往下执行其他操作

await--acquireSharedInterruptibly--tryAcquireShared--为0则直接返回不用等待，若不为0则--doAcquireSharedInterruptibly
进行入队，若前置是head则再看state，非0则设定前置-1并等待，若0则设定head和doReleaseShared

countDown-->releaseShared-->tryReleaseShared为0则返回，进行state-1，若为0则--doReleaseShared释放资源并唤醒，h == head表明唤醒
的后继还未占领head，即head未变则退出，否则重新循环拿到最新的head，继续向后唤醒。。

await抢占到资源了会unparkSuccessor，而countDown减到0的也会unparkSuccessor，提高吞吐。

tryReleaseShared用自旋的方式进行cas减一

doReleaseShared中
if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))失败的场景:
1. 当前线程唤醒了下一个，而下一个替换了head，然后当前线程再一次执行for，获取最新head(刚才唤醒的nextnode),这时刚才唤醒的node即新head也会调用cas
可能产生并发。
2. 当前线程准备唤醒一下个，而这时新来的node会将pre设置0->SIGNAL，而当前线程是SIGNAL->0所以冲突了。

当前线程更新SIGNAL->0失败后，这时若h != head，那么表明head没有变更，继续0->Node.PROPAGATE，这样后续来的线程就0/PROPAGATE->SIGNAL

waitStatus value to indicate the next acquireShared should unconditionally propagate
static final int PROPAGATE = -3;


####CyclicBarrier可重复的
每次从开始使用到穿过栅栏当做"一代"
最后一个await到达则开启栅栏，由最后一个线程执行barrierAction
用了reentrantlock的condition实现的。

####Semaphore
类似一个资源池，每个线程需要调用 acquire() 方法获取资源，然后才能执行，执行完后，需要 release 资源，让给其他的线程用。
acquire/release都是以cas方式减去获取的数量

doReleaseShared会不会无限的把后面的所有都唤醒了？然后竞争激烈？
被唤醒的线程的prev时head才允许竞争，并且cas失败还是会等待的。
唤醒多个？release和被唤醒的head都会执行doReleaseShared。一旦感知到head变化了，那么还会继续循环进行当前head的下一个节点通知。似乎有问题？？

本身share模式的设计就是为了同时有多个线程获得权限，而且释放时有多个一起争抢，因为资源是共享的。


#####LinkedBlockingQueue
头出队，尾入队，两把锁两个唤醒条件，各自不影响

put中
if (c + 1 < capacity)  notFull.signal(); c标识入队前的数量，只随机通知一个等待该条件的生产者线程。即“单次通知”，目的是减少无效竞争。
条件notFull可能由出队成功触发（必要的），也可能由入队成功触发（也是必要的，避免“信号不足”的问题）。因为内部take使用的
是signal，目的是减少竞争，那么就需要put时也按条件唤醒同伴put。
if (c == 0) signalNotEmpty();只有入队前是空的才通知消费者
增加dummy node可解决该问题（或者叫哨兵节点什么的）。定义Node(item, next)，描述如下：

队列长度为1时，到底入队和出队之间存在竞争吗？使用dummy节点避免同时操作一个节点

初始化链表时，创建dummy node：
dummy = new Node(null, null)
head = dummy.next // head 为 null <=> 队列空
tail = dummy // tail.item 为 null <=> 队列空

在队尾入队时，tail后移：
tail.next = new Node(newItem, null)
tail = tail.next

在队头出队时，dummy后移，同步更新head：
oldItem = head.item
dummy = dummy.next
dummy.item = null
head = dummy.next
return oldItem

综上：入队时操作的时tail.next和tail引用，而入队时操作的是dummy.item和dummy引用。
若只有一个节点，那么出队时,dummy先移动，然后更新item，这个是tail所在节点，但是tail不会
操作这个节点item，只会操作一下当前节点的next

LinkedBlockingQueue中，初始化时last = head = new Node<E>(null);，之后入队last = last.next = node;
出队 Node<E> h = head;
      Node<E> first = h.next;//赋值临时变量，一会操作，先解决老的head
      h.next = h; // help GC 老的head自引用
      head = first; //head后移
      E x = first.item; //用于返回
      first.item = null; //成为dummy节点
      return x;
      
对于放入：count自增一定要晚于enqueue执行，否则take()方法的while循环检查会失效，因为看到count变了，
但是队列没东西   

nanos = notFull.awaitNanos(nanos);

与ArrayBlockingQueue差别：
1. LinkedBlockingQueue底层用链表实现：ArrayBlockingQueue底层用数组实现
2. LinkedBlockingQueue支持不指定容量的无界队列（长度最大值Integer.MAX_VALUE）；ArrayBlockingQueue必须指定容量，无法扩容
3. LinkedBlockingQueue支持懒加载：ArrayBlockingQueue不支持
4. ArrayBlockingQueue入队时不生成额外对象：LinkedBlockingQueue需生成Node对象，消耗时间，且GC压力大
5. LinkedBlockingQueue的入队和出队分别用两把锁保护，无竞争，二者不会互相影响；
ArrayBlockingQueue的入队和出队共用一把锁，入队和出队存在竞争，一方速度高时另一方速度会变低。
6. 不考虑分配对象、GC等因素的话，ArrayBlockingQueue并发性能要低于LinkedBlockingQueue
可以看到，LinkedBlockingQueue整体上是优于ArrayBlockingQueue的。应优先使用LinkedBlockingQueue。

#####
通常认为乐观锁的性能比悲观所更高，特别是在某些复杂的场景。这主要由于悲观锁在加锁的同时，也会把某些不会造成破坏的操作保护起来；
而乐观锁的竞争则只发生在最小的并发冲突处，如果用悲观锁来理解，就是“锁的粒度最小”

####条件队列
条件队列装入的数据项是等待先验条件成立而被挂起的线程

并发情况下，使用while来等待我们需要的消息会带来很多问题，不yeild或者休眠会浪费CPU时间片，yeild或者休眠可能丢失正在等待的先验条件。
用条件队列来完成，对象请求操作系统挂起当前线程的是为了减少对CPU的无谓消耗，对象释放对象锁的是为了能让其他线程进入当前对象，
让其他线程进入当前对象是为了能有机会让让其他线程执行后使得先验条件成立

wait和notifyAll作用的一定是一个对象，否则抛出异常java.lang.IllegalMonitorStateException

JVM对多线程的支持有两种： 
1 互斥：通过对象锁(Class对象或业务对象)来实现 
2 协同：通过Object类的wait，notify,notifyAll方法来实现
体现在Java语言层面上,就是内置锁和内置条件队列.
内置锁即synchronized
内置条件队列指的是Object.wait(),Object.notify(),Object.notifyAll()三种方法涉及的队列
正常的队列中存储的都是对象,而条件队列中存储的是"处于等待状态的线程",这些线程在等待某种特定的条件变成真
"条件队列中的线程一定是执行不下去了才处于等待状态",这个"执行不下去的条件"叫做"条件谓词".
wait()方法返回时并不一定意味着正在等待的条件谓词变成真了，都必须再次测试条件谓词，所以用while

当JVM在生成对象的时候，会自动的为每一个对象关联一个锁。这就保证了每个对象有对象锁，而每个类对象有类锁

最好不要再同一个条件队列上多个条件谓词，这样唤醒所有则会产生竞争，唤醒单个则会产生信号丢失。

显式锁为Lock,显示条件队列为Condition对象
每个内置锁只能有一个与之关联的内置条件队列，与之不同的是，每个Lock上可以有多个与他关联的Condition,这就使得我们对Condition的控制更加细粒度化
即上的还是同一把锁，但是唤醒的队列可以有选择了。

只有同时满足下列两个条件时，才能使用单一的notify
条件1. 所有的等待线程类型相同：只有一个条件谓词与条件队列相关，并且每个线程从wait返回后将执行相同的操作。
条件2. 单进单出：条件变量上的每次通知，最多只能唤醒一个线程来执行。


同步队列节点来源：
1、同步队列依赖一个双向链表来完成同步状态的管理，当前线程获取同步状态失败后，同步器会将线程构建成一个节点，并将其加入同步队列中。
2、通过signal或signalAll将条件队列中的节点转移到同步队列。（由条件队列转化为同步队列）

条件队列节点来源：
1、调用await方法阻塞线程； 
2、当前线程存在于同步队列的头结点，调用await方法进行阻塞（从同步队列转化到条件队列）

可总结为： 
1、同步队列与条件队列节点可相互转化 
2、一个线程只能存在于两个队列中的一个

pthread_cond_wait总和一个互斥锁结合使用。在调用pthread_cond_wait前要先获取锁。pthread_cond_wait函数执行时先自动释放指定的锁，
然后等待条件变量的变化。在函数调用返回之前，自动将指定的互斥量重新锁住。
调用pthread_cond_signal后要立刻释放互斥锁，因为pthread_cond_wait的最后一步是要将指定的互斥量重新锁住，如果pthread_cond_signal
之后没有释放互斥锁，pthread_cond_wait仍然要阻塞。
pthread_cond_wait() 所做的第一件事就是同时对互斥对象解锁并等待条件 mycond 发生.要求解锁并阻塞是一个原子操作
此时，pthread_cond_wait() 调用还未返回。对互斥对象解锁会立即发生，但等待条件 mycond 通常是一个阻塞操作，这意味着线程将睡眠，
在它苏醒之前不会消耗 CPU 周期。这正是我们期待发生的情况。线程将一直睡眠，直到特定条件发生，在这期间不会发生任何浪费 CPU 时间的繁忙查询。
从线程的角度来看，它只是在等待 pthread_cond_wait() 调用返回
当有人notifyal时，pthread_cond_wait() 将执行最后一个操作：重新锁定 mymutex。一旦 pthread_cond_wait() 锁定了互斥对象，
那么它将返回并允许 1 号线程继续执行

pthread_mutex_lock
    xxxxxxx
pthread_cond_signal
pthread_mutex_unlock
LinuxThreads或者NPTL里面，就不会有这个问题，因为在Linux 线程中，有两个队列，分别是cond_wait队列和mutex_lock队列， 
cond_signal只是让线程从cond_wait队列移到mutex_lock队列，而不用返回到用户空间，不会有性能的损耗。所以在Linux中推荐使用这种模式。


#####两种写法区别
方式一：
synchronized(lock){
    while(!conditionPredition){//由其他线程修改并唤醒
        lock.wait();//醒来后，先获取锁后才能再次执行下一步的while的判断
    }
    doSomething();
}
方式二：
while(!conditionPredition){
    synchronized(lock){
        lock.wait();//醒来后，需要获取锁，才能执行判断
    }
}
doSomething();
conditionPredition = true

以上两写法都能保证醒来后第一时间先要获取锁，然后才能进行判断。只不过第一种写法，同一时间内只能有一个人执行doSomething即使条件满足，
因为被synchronized整体罩住了。而第二种性能就高了，因为目的很明确，条件符合就可以执行doSomething，被wait的只能获取锁后再判断，若
是第一次来的线程若是满足直接执行doSomething，类似于双重检查锁的思想，第二种需要conditionPredition是volatile的，因为第一种是用
synchronized的happen-before保证conditionPredition可见性,而第二种由于第一次来的直接判断conditionPredition并没有与任何上锁
的线程发生happen-before顺序，所以有可能告诉cpu的缓存中对于此线程不可见
不过也得看实际场景，若是就需要先获取锁的才能再看是否符合条件，那么就得用第一种，若是一次限制一个线程执行，那么第二种也是可以的，oo不行了，
第二个线程来后，判断conditionPredition符合向下执行，而之前醒来的线程也conditionPredition符合也执行了，确实有并发问题，应该先判断，
若不可以则返回，尝试获取锁后再判断，这样能保证只有一个人获取锁然后进行修改。

方式三：
while(!cas(conditionPredition)){
    synchronized(lock){
        lock.wait();
    }
}
doSomething();

要是conditionPredition换成cas呢，相当于两个锁，是否有冲突？
应该不会，其实cas本身就是锁，获取cas的锁了，然后再上锁也是为了应付wait，那么大家其实用的是两把锁，一把用于排他，一把用于等待，只有获取
排他的锁才能进行通知等待的线程。似乎是用了虎头锁，然后蛇的队列。。

其实用cas就是想得能第一时间知道并发失败，然后就等待或者再尝试，被唤醒的线程还可以和刚来的线程竞争，即不公平。
而本身先用synchronized，那么其实就是直接让你等待,稍后获取锁后再次判断conditionPredition然后后续操作，前提是先获取锁，获取不到则等待。

确保构成条件谓词的状态变量被锁保护,而这个锁正是与条件队列相关联的???不相关呢？上面说的似乎也可以
条件谓词是依赖于许多个状态变量的，对于这些状态变量必须要由一个锁来保护，在每次测试条件谓词的时候必须先获取该锁，
则就要求了锁对象和条件队列对象必须是同一个对象。

双重检查锁
volatile isLoadingConf = false

if (isLoadingConf) {
    return;
}

synchronized (this) {
    if (isLoadingConf) {
        return;
    }
    
    isLoadingConf = true;
}

没有等待的概念，仅仅是同步+可见性问题，
        
综上：还是得有实际业务场景：
1. 若是都是设定值，而这个假若是个多操作需要原子(如i++)，那么就需要进行原子保护，这时若是都要排队设定，那么就用synchronized。
2. 若是对于初始化只有并且仅有一个设定，那么就不要让其他人等待了，cas，不行赶紧撤。
3. 若是同一时间只有一个可以进行加载，那么同2，只有一个人获取锁，其他不行的就不加载了。
4. 若是同一时间，只有一个可以加载，但是其他线程可能需要等待一下结果或者当第一个失败时再尝试加载一下，那么就需要cas知道失败，然后等待
然后再准备尝试或者被唤醒。
5. 对于cas+wait，目前看来似乎没有太大问题，只有cas成功的，也就是一个线程才能notifyall，其他失败的都得wait，醒来后还是同样可以
与其他wait或与第一次来的一起竞争，成功的执行，失败的继续等待，不公平，但是吞吐高。
6. cas场景有可能是不断的实验，直到入队成功，而synchronized则需要等待同步的入队，哪个效率高呢？应该是看并发量，太大可能就公平一点，
若少则就cas一下。

synchronized的缺点就是并发来的所有人都得等着获取锁，而只有拿到锁了，才知道是否符合自己条件再决定是否执行。
cas或者lock的优点就是可以看一下，不行，马上就能做下一步决定。

#####
一般直接使用synchronized，只有需要使用到可定时的，可轮询的，可中断的锁获取操作，公平队列的时候才可以考虑使用ReentrantLock。

 在对于读写操作中，也可以利用显式锁中的读-写锁进行，即ReentrantReadWriteLock.
 
monitor 功能太单一了，就是获取独占锁，而且条件队列单一
AQS 相比 monitor，功能要丰富很多，比如我们可以设置超时时间，可以用线程中断进行退出，可以选择公平/非公平模式等，
