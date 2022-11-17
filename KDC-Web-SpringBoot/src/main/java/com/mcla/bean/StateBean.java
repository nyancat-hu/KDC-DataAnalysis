package com.mcla.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class StateBean{

	@JSONField(name="tpsLast5Mins")
	private Object tpsLast5Mins;

	@JSONField(name="usagelastMin")
	private Object usagelastMin;

	@JSONField(name="tpsLast10Secs")
	private Object tpsLast10Secs;

	@JSONField(name="gc")
	private Gc gc;

	public void setTpsLast5Mins(Object tpsLast5Mins){
		this.tpsLast5Mins = tpsLast5Mins;
	}

	public Object getTpsLast5Mins(){
		return tpsLast5Mins;
	}

	public void setUsagelastMin(Object usagelastMin){
		this.usagelastMin = usagelastMin;
	}

	public Object getUsagelastMin(){
		return usagelastMin;
	}

	public void setTpsLast10Secs(Object tpsLast10Secs){
		this.tpsLast10Secs = tpsLast10Secs;
	}

	public Object getTpsLast10Secs(){
		return tpsLast10Secs;
	}

	public void setGc(Gc gc){
		this.gc = gc;
	}

	public Gc getGc(){
		return gc;
	}
}