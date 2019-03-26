使用jmh执行

分别在两台不同架构机器，执行如下算法10次
串行版本、并行版本、Arrays.sort、Arrays.parallelSort

结论：
Arrays.parallelSort更好。
并发优于串行
串行时Arrays.sort比串行版本更快

加速比：S=T(serial)/T(concurrent)