package com.example.dave.gameEngine.dataStructures;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MySparseArray<E>{
	private final SparseArray<E> sparseArray;
	private final Fetcher<E, Integer> fetcher;

	public MySparseArray(Fetcher<E, Integer> fetcher){
		sparseArray= new SparseArray<>();
		this.fetcher = fetcher;
	}

	public MySparseArray(Fetcher<E, Integer> fetcher, final int size){
		sparseArray= new SparseArray<>(size);
		this.fetcher = fetcher;
	}

	public MySparseArray(){
		sparseArray= new SparseArray<>();
		fetcher=new Fetcher<E, Integer>() {
			@Nullable
			@Override
			public Integer fetch(@NonNull E from) {
				if ( !(from instanceof Indexed)){
					throw new ClassCastException("MySparseArray simple constructor. "+from.getClass().getSimpleName()+" does not implement Indexed");
				}
				return ((Indexed)from).getIndex();
			}
		};
	}

	public MySparseArray(final int size){
		sparseArray= new SparseArray<>(size);
		fetcher=new Fetcher<E, Integer>() {
			@Nullable
			@Override
			public Integer fetch(@NonNull E from) {
				if ( !(from instanceof Indexed)){
					throw new ClassCastException("MySparseArray simple constructor. "+from.getClass().getSimpleName()+" does not implement Indexed");
				}
				return ((Indexed)from).getIndex();
			}
		};
	}

	private MySparseArray(Fetcher<E, Integer> fetcher, SparseArray<E> toClone) {
		this.fetcher=fetcher;
		this.sparseArray=toClone;
	}

	public int size() {
		return sparseArray.size();
	}

	public E valueAt(int index) {
		return sparseArray.valueAt(index);
	}

	public int indexOf(E elem){
		return sparseArray.indexOfKey(fetcher.fetch(elem));
	}

	public void keyAt(int index){
		sparseArray.keyAt(index);
	}

	public E get(int key){
		return sparseArray.get(key);
	}

	public E get(int key, E defaultValue){
		return sparseArray.get(key, defaultValue);
	}

	public void setValueAt(int index, E elem){
		sparseArray.setValueAt(index, elem);
	}

	public void put(E elem){
		sparseArray.put(fetcher.fetch(elem), elem);
	}

	public void update(E elem){
		sparseArray.put(fetcher.fetch(elem), elem);
	}
	
	public void clear(){
		sparseArray.clear();
	}

	public void remove(E elem){
		sparseArray.remove(fetcher.fetch(elem));
	}

	public void removeAt(int index){
		sparseArray.removeAt(index);
	}

	public MySparseArray<E> clone(){
		return new MySparseArray<>(fetcher, sparseArray.clone());
	}

	@Override
	public String toString(){
		return sparseArray.toString();
	}
}
