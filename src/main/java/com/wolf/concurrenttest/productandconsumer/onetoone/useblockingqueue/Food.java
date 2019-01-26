package com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/12
 * Time: 12:59
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Food {

	private int id;
	private String name;

	public String getName() {
		return name;
	}

	public Food() {
	}

	public Food(int id){
		this.id = id;
		System.out.println("生产"+id);
	}

	public Food(String name){
		this.name = name;
		System.out.println("生产"+id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
