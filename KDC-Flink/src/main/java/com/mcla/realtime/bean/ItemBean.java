package com.mcla.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class ItemBean{

	@JSONField(name="isSpawn")
	private String isSpawn;

	@JSONField(name="itemName")
	private String itemName;

	@JSONField(name="world")
	private String world;

	@Override
	public String toString() {
		return "ItemBean{" +
				"isSpawn='" + isSpawn + '\'' +
				", itemName='" + itemName + '\'' +
				", world='" + world + '\'' +
				", itemAmount='" + itemAmount + '\'' +
				", x='" + x + '\'' +
				", eventName='" + eventName + '\'' +
				", y='" + y + '\'' +
				", serverName='" + serverName + '\'' +
				", z='" + z + '\'' +
				", tag='" + tag + '\'' +
				", chunkX='" + chunkX + '\'' +
				", chunkZ='" + chunkZ + '\'' +
				'}';
	}

	@JSONField(name="itemAmount")
	private String itemAmount;

	@JSONField(name="x")
	private String x;

	@JSONField(name="eventName")
	private String eventName;

	@JSONField(name="y")
	private String y;

	@JSONField(name="serverName")
	private String serverName;

	@JSONField(name="z")
	private String z;

	@JSONField(name="tag")
	private String tag;

	@JSONField(name="chunkX")
	private String chunkX;

	@JSONField(name="chunkZ")
	private String chunkZ;

	public String getIsSpawn(){
		return isSpawn;
	}

	public String getItemName(){
		return itemName;
	}

	public String getWorld(){
		return world;
	}

	public String getItemAmount(){
		return itemAmount;
	}

	public String getX(){ return x; }

	public String getEventName(){
		return eventName;
	}

	public String getY(){
		return y;
	}

	public String getServerName(){
		return serverName;
	}

	public void setIsSpawn(String isSpawn) {
		this.isSpawn = isSpawn;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public void setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
	}

	public void setX(String x) {
		this.x = x;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public void setY(String y) {
		this.y = y;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setZ(String z) {
		this.z = z;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setChunkX(String chunkX) {
		this.chunkX = chunkX;
	}

	public void setChunkZ(String chunkZ) {
		this.chunkZ = chunkZ;
	}

	public String getZ(){
		return z;
	}

	public String getTag(){
		return tag;
	}

	public String getChunkX(){
		return chunkX;
	}

	public String getChunkZ(){
		return chunkZ;
	}
}