比较atomic和reentrantlock

each iteration generates a random number and also performs a number of "busy-work" iterations that operate strictly
on thread-local data

high contention levels locking tends to outperform atomic variables, 
but at more realistic contention levels atomic variables outperform locks.

this is because a lock reacts to contention by suspending threads, reducing CPU usage and synchronization traffic on
shared memory bus.
with atomic variables, reacts to contention by trying again immediately, which is usually the right approach but in 
a high contention environment just creates more contention.

上述情况竞争太激烈，不过要根据现实情况看
in practice, atomics tend to scale better than locks because atomics deal more effectively with typical contention levels.

with low to moderate contention, atomics offer better scalability;
with high contention, locks offer better contention avoidance.