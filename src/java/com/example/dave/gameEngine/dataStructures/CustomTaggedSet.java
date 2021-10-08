package com.example.dave.gameEngine.dataStructures;

import androidx.annotation.NonNull;

import android.util.ArrayMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CustomTaggedSet<T, V> implements Iterable<V>{
	private final Map<T, Set<V>> tagMap;
	private int size = 0;
	private final Fetcher<V, T> fetcher;
	private final SetBuilder<V> setBuilder;

	public CustomTaggedSet(@NonNull Fetcher<V, T> tf, SetBuilder<V> sb, MapBuilder<T, Set<V>> mb){
		fetcher = tf;
		if(sb==null)
			this.setBuilder = new SetBuilder<V>() {
				@Override
				public Set<V> buildSet() {
					return new HashSet<>();
				}
			};
		else
			this.setBuilder=sb;
		if(mb==null)
			mb = new MapBuilder<T, Set<V>>() {
				@Override
				public Map<T, Set<V>> buildMap() {
					return new ArrayMap<>();
				}
			};
		this.tagMap = mb.buildMap();
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size==0;
	}

	public boolean contains(V value){
		T tag = fetcher.fetch(value);
		Set<V> taggedSet = tagMap.get(tag);
		return taggedSet!=null && taggedSet.contains(value);
	}

	@NonNull
	@Override
	public Iterator<V> iterator() {
		Iterator<V> iterator = new Iterator<V>() {
			Iterator<T> tagIterator = tagMap.keySet().iterator();
			T currKey = tagIterator.hasNext() ? tagIterator.next() : null;
			Iterator<V> valueIterator = currKey!=null ? tagMap.get(currKey).iterator() : null;
			V preComputedRet = null;

			@Override
			public boolean hasNext() {
				if(valueIterator.hasNext()) return true;
				else if (!tagIterator.hasNext()) return false;
				else {
					while (!valueIterator.hasNext()) {
						if(!tagIterator.hasNext())
							return false;
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					preComputedRet = valueIterator.next();
					return true;
				}
			}

			@Override
			public V next() {
				if(preComputedRet!=null){
					V ret = preComputedRet;
					preComputedRet = null;
					return ret;
				}
				else if(valueIterator.hasNext())
					return valueIterator.next();
				else{
					while (!valueIterator.hasNext()) {
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					return valueIterator.next();
				}
			}
		};
		return iterator;
	}

	public Set<V> getByTag(T tag){
		return tagMap.get(tag);
		/*if (tagMap.containsKey(tag)) {
			return tagMap.get(tag);
		}
		return null;*/
	}

	public boolean add(V v) {
		T tag = fetcher.fetch(v);
		Set<V> taggedSet = tagMap.get(tag);
		if (taggedSet==null){
			taggedSet = setBuilder.buildSet();
			taggedSet.add(v);
			tagMap.put(tag, taggedSet);
		}
		else{
			taggedSet.add(v);
			/*tagMap.put(tag, taggedSet);*/
		}
		size++;
		return true;
	}

	public boolean remove(V v) {
		T tag = fetcher.fetch(v);
		Set<V> taggedSet = tagMap.get(tag);
		if (taggedSet==null) return false;
		else{
			if( taggedSet.remove(v) ){
				size--;
				/*tagMap.put(tag, taggedSet);*/
				return true;
			}
			else return false;
		}
	}

	public void clear() {
		for( Set<V> subSet : tagMap.values())
			subSet.clear();
		tagMap.clear();
		size=0;
	}
}

/*
class TaggedSet<T, V> implements Iterable<V>{
	private final Map<T, Set<V>> tagMap = new ArrayMap<>();
	private int size = 0;
	private final TagFetcher<V, T> fetcher;

	public TaggedSet(@NonNull TagFetcher<V, T> tf){
		fetcher = tf;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size==0;
	}

	public boolean contains(V value){
		T tag = fetcher.fetchKey(value);
		Set<V> taggedSet = tagMap.get(tag);
		return taggedSet!=null && taggedSet.contains(value);
	}

	@NonNull
	@Override
	public Iterator<V> iterator() {
		Iterator<V> iterator = new Iterator<V>() {
			Iterator<T> tagIterator = tagMap.keySet().iterator();
			T currKey = tagIterator.hasNext() ? tagIterator.next() : null;
			Iterator<V> valueIterator = currKey!=null ? tagMap.get(currKey).iterator() : null;
			V preComputedRet = null;

			@Override
			public boolean hasNext() {
				if(valueIterator.hasNext()) return true;
				else if (!tagIterator.hasNext()) return false;
				else {
					while (!valueIterator.hasNext()) {
						if(!tagIterator.hasNext())
							return false;
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					preComputedRet = valueIterator.next();
					return true;
				}
			}

			@Override
			public V next() {
				if(preComputedRet!=null){
					V ret = preComputedRet;
					preComputedRet = null;
					return ret;
				}
				else if(valueIterator.hasNext())
					return valueIterator.next();
				else{
					while (!valueIterator.hasNext()) {
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					return valueIterator.next();
				}
			}
		};
		return iterator;
	}

	public Set<V> getByTag(T tag){
		return tagMap.get(tag);
	}

	public boolean add(V v) {
		T tag = fetcher.fetchKey(v);
		Set<V> taggedSet = tagMap.get(tag);
		if (taggedSet==null){
			taggedSet = new HashSet<>();
			taggedSet.add(v);
			tagMap.put(tag, taggedSet);
		}
		else{
			taggedSet.add(v);
			//tagMap.put(tag, taggedSet);
		}
		size++;
		return true;
	}

	public boolean remove(V v) {
		T tag = fetcher.fetchKey(v);
		Set<V> taggedSet = tagMap.get(tag);
		if (taggedSet==null) return false;
		else{
			if( taggedSet.remove(v) ){
				size--;
				//tagMap.put(tag, taggedSet);
				return true;
			}
			else return false;
		}
	}

	public void clear() {
		for( Set<V> subSet : tagMap.values())
			subSet.clear();
		tagMap.clear();
		size=0;
	}
}

class ElementTaggedSet implements Iterable<GameObject>{
	private final EnumMap<GameElement, Set<GameObject>> tagMap = new EnumMap<>(GameElement.class);
	private int size = 0;
	private final TagFetcher<GameObject, GameElement> fetcher;

	public ElementTaggedSet(@NonNull TagFetcher<GameObject, GameElement> tf){
		fetcher = tf;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size==0;
	}

	public boolean contains(GameObject value){
		GameElement tag = fetcher.fetchKey(value);
		Set<GameObject> taggedSet = tagMap.get(tag);
		return taggedSet!=null && taggedSet.contains(value);
	}

	@NonNull
	@Override
	public Iterator<GameObject> iterator() {
		Iterator<GameObject> iterator = new Iterator<GameObject>() {
			Iterator<GameElement> tagIterator = tagMap.keySet().iterator();
			GameElement currKey = tagIterator.hasNext() ? tagIterator.next() : null;
			Iterator<GameObject> valueIterator = currKey!=null ? tagMap.get(currKey).iterator() : null;
			int i=0, size=size();
			//GameObject preComputedRet = null;

			*//*@Override
			public boolean hasNext() {
				if(valueIterator.hasNext()) return true;
				else if (!tagIterator.hasNext()) return false;
				else {
					while (!valueIterator.hasNext()) {
						if(!tagIterator.hasNext())
							return false;
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					preComputedRet = valueIterator.next();
					return true;
				}
			}*//*

			public boolean hasNext(){
				return i<size;
			}

			@Override
			public GameObject next() {
			    if(valueIterator.hasNext()) {
					i++;//With counter
					return valueIterator.next();
				}
				else{
					while (!valueIterator.hasNext()) {
						currKey = tagIterator.next();
						valueIterator = tagMap.get(currKey).iterator();
					}
					i++;//With counter
					return valueIterator.next();
				}
			}
		};
		return iterator;
	}

	public Set<GameObject> getByTag(GameElement tag){
		return tagMap.get(tag);
	}

	public boolean add(GameObject v) {
		GameElement tag = fetcher.fetchKey(v);
		Set<GameObject> taggedSet = tagMap.get(tag);
		if (taggedSet==null){
			taggedSet = new HashSet<>();
			taggedSet.add(v);
			tagMap.put(tag, taggedSet);
		}
		else{
			taggedSet.add(v);
			//tagMap.put(tag, taggedSet);
		}
		size++;
		return true;
	}

	public boolean remove(GameObject v) {
		GameElement tag = fetcher.fetchKey(v);
		Set<GameObject> taggedSet = tagMap.get(tag);
		if (taggedSet==null) return false;
		else{
			if( taggedSet.remove(v) ){
				size--;
				//tagMap.put(tag, taggedSet);
				return true;
			}
			else return false;
		}
	}

	public void clear() {
		for( Set<GameObject> subSet : tagMap.values())
			subSet.clear();
		tagMap.clear();
		size=0;
	}
}
*/