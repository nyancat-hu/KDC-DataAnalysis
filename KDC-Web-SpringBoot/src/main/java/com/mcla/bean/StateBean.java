package com.mcla.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class StateBean {

	@JSONField(name="tpsLast5Mins")
	private Object tpsLast5Mins;

	@JSONField(name="usagelastMin")
	private Object usagelastMin;

	@JSONField(name="tpsLast10Secs")
	private Object tpsLast10Secs;

	@Override
	public String toString() {
		return "StateBean{" +
				"tpsLast5Mins=" + tpsLast5Mins +
				", usagelastMin=" + usagelastMin +
				", tpsLast10Secs=" + tpsLast10Secs +
				", gc=" + gc +
				'}';
	}

	@JSONField(name="gc")
	private Map<String, Map<Long,Double>> gc;

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

	public void setGc(Map<String, Map<Long,Double>> gc){
		this.gc = gc;
	}

	public Map<String, Map<Long,Double>> getGc(){
		return gc;
	}
}