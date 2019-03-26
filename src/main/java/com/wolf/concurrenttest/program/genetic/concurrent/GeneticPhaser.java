package com.wolf.concurrenttest.program.genetic.concurrent;

import java.util.Arrays;
import java.util.concurrent.Phaser;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/28
 */
public class GeneticPhaser extends Phaser {

    private SharedData data;

    public GeneticPhaser(int parties, SharedData data) {
        super(parties);
        this.data = data;
    }

    //所有任务完成此阶段后执行代码
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        int realPhase = phase % 3;
        if (registeredParties > 0) {
            switch (realPhase) {
                case 0://主选择种群完毕
                case 1://cross完毕，重置index
                    data.getIndex().set(0);
                    break;
                case 2://evaluate完毕
                    Arrays.sort(data.getPopulation());
                    if (data.getPopulation()[0].getValue() < data.getBest().getValue()) {
                        data.setBest(data.getPopulation()[0]);//todo 这里的数据修改，phaser应该自己能保证可见性，看多代码似乎没有呢？
                        // o这个方法又最后一个任务线程执行，那么就需要volatile了!，不过由于框架中使用了releaseWaiters，也就是LockSupport，
                        //这个为什么支持可见性？
                    }
                    break;
            }
            return false;
        }
        return true;
    }

    public SharedData getData() {
        return data;
    }

    public void setData(SharedData data) {
        this.data = data;
    }
}
