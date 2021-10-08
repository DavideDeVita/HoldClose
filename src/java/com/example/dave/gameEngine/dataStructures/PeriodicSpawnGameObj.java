package com.example.dave.gameEngine.dataStructures;

import com.badlogic.androidgames.framework.Pool;
import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MainActivity;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine.R;
import com.example.dave.gameEngine.dataDriven.GameObj_Properties;

public class PeriodicSpawnGameObj implements PooleOriented<PeriodicSpawnGameObj>{
	private int minCooldown, maxCooldown;
	private RandomExtractor<GameObj_Properties> extractor;
	private Box spawnZone;

	public static PeriodicSpawnGameObj _new(){
		return periodicSpawnPool.newObject();
	}

	public void set(final int cooldownMillisec, final Integer maximumDelay, final RandomExtractor<GameObj_Properties> extractor, final Box spawnZone){
		minCooldown = cooldownMillisec;
		maxCooldown = cooldownMillisec + (maximumDelay==null ? 0 : maximumDelay);
		this.extractor=extractor;
		this.spawnZone = spawnZone;
		this.cooldown = computeCooldown();
	}

	/*function related variables and fields*/
	private long currTime=0, lastTime=0L, cooldown;

	public void check(GameSection gs){
		currTime = System.nanoTime();
		if (currTime >= lastTime + cooldown){
			spawn(gs);
			lastTime = currTime;
			cooldown = computeCooldown();
		}
	}

	private long computeCooldown(){
		return MyMath.randomInt(minCooldown, maxCooldown) * 1_000_000L;
	}

	private void spawn(GameSection gs){
		float spawnX, spawnY;
		spawnX = spawnZone.randomX();
		spawnY = spawnZone.randomY();
		gs.createGameObject(extractor.extract(), spawnX, spawnY);
	}

	private final static Pool<PeriodicSpawnGameObj> periodicSpawnPool = new Pool<>(
			new Pool.PoolObjectFactory<PeriodicSpawnGameObj>(){
				@Override
				public PeriodicSpawnGameObj createObject() {
					return new PeriodicSpawnGameObj();
				}
			},
			MainActivity.readIntOnDemand(R.integer.Spawn_Pool_size)
	);

	@Override
	public PeriodicSpawnGameObj getFromPoole() {
		return periodicSpawnPool.newObject();
	}

	@Override
	public void sendToPoole() {
		periodicSpawnPool.free(this);
	}

	@Override
	public void free() {
		extractor.free();
		sendToPoole();
	}
}