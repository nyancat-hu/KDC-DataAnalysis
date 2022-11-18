package com.mcla.realtime.bean.state;

import com.alibaba.fastjson.annotation.JSONField;

import java.time.LocalDateTime;
import java.util.Map;

public class StateBean{

	@Override
	public String toString() {
		return "StateBean{" +
				"tpsLast5Mins=" + tpsLast5Mins +
				", usagelastMin=" + usagelastMin +
				", tpsLast10Secs=" + tpsLast10Secs +
				", gc=" + gc +
				", time='" + time + '\'' +
				'}';
	}

	@JSONField(name="tpsLast5Mins")
	private Object tpsLast5Mins;

	@JSONField(name="usagelastMin")
	private Object usagelastMin;

	@JSONField(name="tpsLast10Secs")
	private Object tpsLast10Secs;

	@JSONField(name="gc")
	private Map<String, Map<Long,Double>> gc;

	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

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