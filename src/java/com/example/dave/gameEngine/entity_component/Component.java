package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine._Log;

public abstract class Component {
    protected final Entity owner;

    protected Component(Entity owner) {
        this.owner = owner;
        owner.addComponent(this);
    }

    @Override
    public final int hashCode(){
        return owner.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return owner.equals(component.owner);
    }

    public abstract ComponentType type();

    public void notify(String whatChanged, Object howIsNow){}

	public void clear(){
        if(_Log.LOG_ACTIVE){
            _Log.d("clear method not implemented in "+this);}
    }

	@Override
	public String toString(){
	    return type()+"";
    }
}
