package com.mcla.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class Gc{

	@JSONField(name="PS MarkSweep")
	private PSMarkSweep pSMarkSweep;

	@JSONField(name="PS Scavenge")
	private PSScavenge pSScavenge;

	public void setPSMarkSweep(PSMarkSweep pSMarkSweep){
		this.pSMarkSweep = pSMarkSweep;
	}

	public PSMarkSweep getPSMarkSweep(){
		return pSMarkSweep;
	}

	public void setPSScavenge(PSScavenge pSScavenge){
		this.pSScavenge = pSScavenge;
	}

	public PSScavenge getPSScavenge(){
		return pSScavenge;
	}
}