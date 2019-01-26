package com.wolf.concurrenttest.actualcombat;

import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Description:无锁vector
 * 为什么要用移位这种方式获取一阶索引和二阶索引
 * 使用二维数组是便于扩展，若一维的话，扩展势必要进行复制，二维的话先留出空间，扩展时直接设定即可。
 *
 * 若不用前导0和异或操作，那么可能需要定义两个atomicinteger变量，用于buketindex和elementindex。可能移位性能好。
 * <br/> Created on 2018/3/22 9:35
 *
 * @author 李超
 * @since 1.0.0
 */
public class LockFreeVector<E> extends AbstractList<E> {
    private static final boolean debug = false;
    /**
     * Size of the first bucket. sizeof(bucket[i+1])=2*sizeof(bucket[i])
     */
    private static final int FIRST_BUCKET_SIZE = 8;//为什么要用8？？？因为初始设定第一个数组长度为8，设多少都行，后期*2扩展，然后会用前导0判断一阶索引.5也可以，但是加到第3个元素就要扩展了

    /**
     * number of buckets. 30 will allow 8*(2^30-1) elements
     */
    private static final int N_BUCKET = 30;

    /**
     * We will have at most N_BUCKET number of buckets. And we have
     * sizeof(buckets.get(i))=FIRST_BUCKET_SIZE**(i+1)
     */
    private final AtomicReferenceArray<AtomicReferenceArray<E>> buckets;

    private AtomicReference<Operator<E>> operator;
    private static final int zeroNumFirst = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE);

    /**
     * Constructor.
     */
    public LockFreeVector() {
        buckets = new AtomicReferenceArray<AtomicReferenceArray<E>>(N_BUCKET);
        buckets.set(0, new AtomicReferenceArray<E>(FIRST_BUCKET_SIZE));
        operator = new AtomicReference<Operator<E>>(new Operator<E>(0, null));
    }

    /**
     * add e at the end of vector.
     * 每次动态扩展，后续buketindex上的元素逻辑上接力前一个buketindex元素，不用内容拷贝。
     *
     * @param e element added
     */
    public void push_back(E e) {
        //1.设定operator
        Operator<E> desc;
        Operator<E> newd;
        do {
            desc = operator.get();
            desc.completeWrite();//执行写入动作，替上一个线程补刀
            //desc.size   Vector 本身的大小
            //FIRST_BUCKET_SIZE  第一个一维数组的大小
            int pos = desc.size + FIRST_BUCKET_SIZE;//元素位置
            int zeroNumPos = Integer.numberOfLeadingZeros(pos);  // 取出pos 的前导零
            int bucketIndex = zeroNumFirst - zeroNumPos;  //一维数组下标(前导零少1表示进了一位，加入第16个元素时进位,也就是要扩展了)

            if (buckets.get(bucketIndex) == null) {
                int newLen = 2 * buckets.get(bucketIndex - 1).length();
                if (debug)
                    System.out.println("New Length is:" + newLen);
                buckets.compareAndSet(bucketIndex, null, new AtomicReferenceArray<E>(newLen));
            }
            //最高1的位置向右移动--前导0的位，异或pos得到除最高位的剩余1，表示元素index
            //为什么要用0x80000000？？？因为1000 0000 0000 0000 0000 0000 0000 0000是int最大了
            int elementIndex = (0x80000000 >>> zeroNumPos) ^ pos;   //在这个一位数组中，我在哪个位置
            newd = new Operator<E>(desc.size + 1, new WriteDescriptor<E>(buckets.get(bucketIndex), elementIndex, null, e));
        } while (!operator.compareAndSet(desc, newd));
        //2.操作operator
        operator.get().completeWrite();
    }

    /**
     * Remove the last element in the vector.
     *
     * @return element removed
     */
    public E pop_back() {
        Operator<E> desc;
        Operator<E> newd;
        E elem;
        do {
            desc = operator.get();
            desc.completeWrite();

            int pos = desc.size + FIRST_BUCKET_SIZE - 1;
            int bucketInd = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE)
                    - Integer.numberOfLeadingZeros(pos);
            int idx = Integer.highestOneBit(pos) ^ pos;
            elem = buckets.get(bucketInd).get(idx);
            newd = new Operator<E>(desc.size - 1, null);
        } while (!operator.compareAndSet(desc, newd));

        return elem;
    }

    /**
     * Get element with the index.
     *
     * @param index index
     * @return element with the index
     */
    @Override
    public E get(int index) {
        int pos = index + FIRST_BUCKET_SIZE;
        int zeroNumPos = Integer.numberOfLeadingZeros(pos);
        int bucketInd = zeroNumFirst - zeroNumPos;
        int idx = (0x80000000 >>> zeroNumPos) ^ pos;
        return buckets.get(bucketInd).get(idx);
    }

    /**
     * Set the element with index to e.
     *
     * @param index
     *            index of element to be reset
     * @param e
     *            element to set
     */
    /**
     * {@inheritDoc}
     */
    public E set(int index, E e) {
        int pos = index + FIRST_BUCKET_SIZE;
        int bucketInd = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE)
                - Integer.numberOfLeadingZeros(pos);
        int idx = Integer.highestOneBit(pos) ^ pos;
        AtomicReferenceArray<E> bucket = buckets.get(bucketInd);
        while (true) {
            E oldV = bucket.get(idx);
            if (bucket.compareAndSet(idx, oldV, e))
                return oldV;
        }
    }

    /**
     * reserve more space.
     *
     * @param newSize new size be reserved
     */
    public void reserve(int newSize) {
        int size = operator.get().size;
        int pos = size + FIRST_BUCKET_SIZE - 1;
        int i = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE)
                - Integer.numberOfLeadingZeros(pos);
        if (i < 1)
            i = 1;

        int initialSize = buckets.get(i - 1).length();
        while (i < Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE)
                - Integer.numberOfLeadingZeros(newSize + FIRST_BUCKET_SIZE - 1)) {
            i++;
            initialSize *= FIRST_BUCKET_SIZE;
            buckets.compareAndSet(i, null, new AtomicReferenceArray<E>(
                    initialSize));
        }
    }

    /**
     * size of vector.
     *
     * @return size of vector
     */
    public int size() {
        return operator.get().size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E object) {
        push_back(object);
        return true;
    }


    static class Operator<E> {
        public int size;
        volatile WriteDescriptor<E> writeop;

        /**
         * Create a new operator.
         *
         * @param size    Size of the vector
         * @param writeop Executor write operation
         */
        public Operator(int size, WriteDescriptor<E> writeop) {
            this.size = size;
            this.writeop = writeop;
        }

        public void completeWrite() {
            WriteDescriptor<E> tmpOp = writeop;
            if (tmpOp != null) {
                tmpOp.doIt();
                writeop = null; // this is safe since all write to writeop use
                // null as r_value.
            }
        }
    }

    static class WriteDescriptor<E> {
        public E oldV;
        public E newV;
        public AtomicReferenceArray<E> addr;
        public int addr_ind;

        /**
         * Creating a new operator.
         *
         * @param addr     Operation address
         * @param addr_ind Index of address
         * @param oldV     old operand
         * @param newV     new operand
         */
        public WriteDescriptor(AtomicReferenceArray<E> addr, int addr_ind,
                               E oldV, E newV) {
            this.addr = addr;
            this.addr_ind = addr_ind;
            this.oldV = oldV;
            this.newV = newV;
        }

        /**
         * set newV.
         */
        public void doIt() {
            addr.compareAndSet(addr_ind, oldV, newV);
        }
    }


}
