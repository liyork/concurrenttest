#####wait/notify/notifyall正确使用

状态依赖类

search：
acquire lock(on object state) // 测试前需要获取锁，以保证测试时条件不变
while (precondition does not hold) { // pre-check防止信号丢失；re-check防止过早唤醒
  release lock // 如果条件尚未满足，就释放锁，允许其他线程修改条件
  wait until precondition might hold, interrupted or timeout expires
  acquire lock // 再次测试前需要获取锁，以保证测试时条件不变
}
do sth // 如果条件已满足，就执行动作
release lock // 最后再释放锁

java中wait推荐方式：
synchronized (obj) {
               while (<condition does not hold>)
                   obj.wait();
               ... // Perform action appropriate to condition
           }

update：
acquire lock(on object state)
do sth, to make precondition might be hold
release lock

1. wait的前置条件要用while判断，防止被意外唤醒，或被唤醒后再竞争锁之间有任何状态再次变动。
释放锁->等待通知->收到通知->竞争锁->重新获取锁
wait进入对象的等待池，被唤醒后进入竞锁池

2. 使用先检查再wait，防止信号丢失。while-do而不是do-while。
因为：先上锁，再while判断，若是不符合则释放锁，这时要有notify就能感知到。
do-while本身不合理，因为只有符合条件才wait，先do没有条件直接做不合理。。

3. notify，因为只能唤醒一个，若是这个没有因为获得条件而执行，可能大家都一直等待了。。。还是得具体场景分析,如果可以避免信号劫持，请尽量使用notify()。
如何同时解决信号劫持与无效竞争？
一个条件队列只用来维护一个条件，不公用就不会被乱唤醒
每个线程被唤醒后执行的操作相同

4. 条件队列的正确姿势
全程加锁
while-do wait
要想使用notify，必须保证单进单出，否则notifyall

5. 优化方案：
若是全程加锁，势必造成所有的consuemr和provider同一时间都是串行的。所以能否最小化加锁？提高并行度？提高双重检查锁的并发度？

#####
条件队列的使用要求我们在调用await或signal时持有与该条件队列唯一相关的锁。

使用cas+wait/notifyall是否有问题?
cas本身保证了只有一个线程持有锁，然后只有此线程可以释放，所以在finally中进行释放，这样即使有问题也有状态进行保证，让再次想获取锁的线程能看到
之前发生的(happen-before)，若是没有cas成功则等待，到时可能会产生信号丢失，因为没有整体上锁，然后cas成功的先执行notifyall，然后cas失败
的线程可能丢失了，不过由于我这里使用的wait(500)也就是500ms后还能感知到状态变化。

#####
阻塞方法BlockingQueue#put()和BlockingQueue#take()
非阻塞方法BlockingQueue#offer()和BlockingQueue#poll()
LinkedBlockingQueue的性能在大部分情况下优于ArrayBlockingQueue


