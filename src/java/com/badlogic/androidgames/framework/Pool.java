package com.badlogic.androidgames.framework;

import android.util.ArrayMap;

import com.example.dave.gameEngine._Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pool<T> {
    private static Map<Class, Integer> newCalled = new ArrayMap<>();
    private static Map<Class, Integer> createCalled = new ArrayMap<>();
    private static Map<Class, Integer> freeCalled = new ArrayMap<>();
    private static Map<Class, Integer> freeIgnored = new ArrayMap<>();

    public interface PoolObjectFactory<T> {
        public T createObject();
    }

    private final List<T> freeObjects;
    private final PoolObjectFactory<T> factory;
    private final int maxSize;

    public Pool(PoolObjectFactory<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<T>(maxSize);
    }

    public T newObject() {
        T object = null;

        if (freeObjects.isEmpty()) {
            object = factory.createObject();
            if(_Log.LOG_ACTIVE) {
                Integer count = createCalled.get(object.getClass());
                if(count==null) count=0;
                createCalled.put(object.getClass(), count+1);
            }
        }
        else
            object = freeObjects.remove(freeObjects.size() - 1);
        if(_Log.LOG_ACTIVE) {
            _Log.d("Pool", "pool newObject call (" + object.getClass().getSimpleName() + ")");
            if(_Log.LOG_ACTIVE) {
                Integer count = newCalled.get(object.getClass());
                if(count==null) count=0;
                newCalled.put(object.getClass(), count+1);
                //_Log.i("Poole", "Pool efficacy on (" + object.getClass().getSimpleName() + ") is:\t"+log_efficacy(object.getClass()));
                _Log.i("Poole", "\t"+object.getClass().getSimpleName() + "\t"+_log_efficacy(object.getClass()));
            }
        }
        return object;
    }

    public void free(T object) {
        if(_Log.LOG_ACTIVE) {
            Integer count = freeIgnored.get(object.getClass());
            if (count == null) count = 0;
            freeIgnored.put(object.getClass(), count + 1);
            _Log.i("Poole", "\tFree\t"+object.getClass().getSimpleName() + "\t"+(count+1));
        }

        if (freeObjects.size() < maxSize)
            freeObjects.add(object);
        else if(_Log.LOG_ACTIVE) {
            Integer count = freeCalled.get(object.getClass());
            if (count == null) count = 0;
            freeCalled.put(object.getClass(), count + 1);
            _Log.i("Poole", "\tFree Ignored\t"+object.getClass().getSimpleName() + "\t"+(count+1));
        }
    }

    private String log_efficacy(Class _class) {
        Integer _new = newCalled.get(_class);
        Integer _create = createCalled.get(_class);
        return (1F- ((_create*1F)/(_new)) )+".\tcreated: "+_create+" on "+_new+" calls";
    }

    private String _log_efficacy(Class _class) {
        Integer _new = newCalled.get(_class);
        Integer _create = createCalled.get(_class);
        return (1F- ((_create*1F)/(_new)) )+"\t"+_create+"\t"+_new;
    }
}