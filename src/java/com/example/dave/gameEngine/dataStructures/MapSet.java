package com.example.dave.gameEngine.dataStructures;

import java.util.Map;
import java.util.Set;

public class MapSet<K, V> implements Cloneable {
	private final Map<K, Set<V>> map;
	private final SetBuilder<V> setBuilder;
	private final MapBuilder<K, Set<V>> mapBuilder;

	public MapSet(SetBuilder<V> sb, MapBuilder<K, Set<V>> mb){
		if(sb==null)
			throw new IllegalArgumentException("Set Builder is null");
		else
			this.setBuilder=sb;
		if(mb==null)
			throw new IllegalArgumentException("Map Builder is null");
		this.map = mb.buildMap();
		this.mapBuilder=mb;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(K key){
		return map.containsKey(key);
	}

	public boolean put(K k, V v) {
		Set<V> set = map.get(k);
		if(set==null)
			set = setBuilder.buildSet();
		set.add(v);
		map.put(k, set);
		return true;
	}

	public boolean addAll(K k, Set<V> v) {
		Set<V> set = map.get(k);
		if(set==null)
			set = setBuilder.buildSet();
		set.addAll(v);
		map.put(k, set);
		return true;
	}

	public Set<V> get(K k) {
		return map.get(k);
	}

	public Set<K> keySet(){
		return map.keySet();
	}

	@Override
	public MapSet<K, V> clone(){
		MapSet<K, V> clone = new MapSet<>(setBuilder, mapBuilder);
		for(K k : keySet()){
			clone.addAll(k, get(k));
		}
		return clone;
	}

	public void clear() {
		map.clear();
	}
}