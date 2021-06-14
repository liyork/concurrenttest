package com.wolf.concurrenttest.netty.deprecated.one;

import java.io.Serializable;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/11
 * Time: 16:01
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class Command implements Serializable {

	private static final long serialVersionUID = 7590999461767050471L;

	private String actionName;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
