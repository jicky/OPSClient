package com.zhiye.ops;

/**
 * @author zl
 */

public class ModbusSend {
	private String readOrWrit; /// 读或写类型

	private int slaveId; // 从站地址

	private int start; // 数据开始地址

	private int len; // 数据度

	private short[] shrtArray; // 写数值的长度

	private boolean[] boolArray;
	
	
	public String getReadOrWrit() {
		return readOrWrit;
	}

	public void setReadOrWrit(String readOrWrit) {
		this.readOrWrit = readOrWrit;
	}

	public int getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(int slaveId) {
		this.slaveId = slaveId;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public short[] getShrtArray() {
		return shrtArray;
	}

	public void setShrtArray(short[] shrtArray) {
		this.shrtArray = shrtArray;
	}

	public boolean[] getBoolArray() {
		return boolArray;
	}

	public void setBoolArray(boolean[] boolArray) {
		this.boolArray = boolArray;
	}

	
	
}
