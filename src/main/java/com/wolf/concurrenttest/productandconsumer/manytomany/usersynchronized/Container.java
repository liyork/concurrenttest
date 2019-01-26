package com.wolf.concurrenttest.productandconsumer.manytomany.usersynchronized;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Description:对容器的操作都上锁(同一时间只有生产或消费中的一个使用方法)
 * <p/>
 * Date: 2016/6/12
 * Time: 16:13
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Container <T> {

	private final int capacity;

	private final List<T> list;

	public Container(int capacity){
		this.capacity = capacity;
		list = new ArrayList<T>(capacity);
	}

	public List<T> getList(){
		return list;
	}

	/**
	 * 添加产品
	 * @param product
	 */
	public synchronized boolean add(T product){
		if(list.size()<capacity){
			list.add(product);
			return true;
		}
		return false;
	}

	/**
	 * 满
	 * @return
	 */
	public synchronized boolean isFull(){
		if(list.size()>=capacity){
			return true;
		}
		return false;
	}

	public synchronized boolean isEmpty(){
		return list.isEmpty();
	}

	public synchronized T get(){
		if(list.size()>0){
			return list.remove(0);
		}
		return null;
	}


	public synchronized int getSize(){
		return list.size();
	}

	public int getCapacity(){
		return capacity;
	}
}