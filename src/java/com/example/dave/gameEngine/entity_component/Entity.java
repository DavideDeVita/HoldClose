package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.GameSection;
import com.example.dave.gameEngine.MainActivity;

import java.util.EnumMap;
import java.util.Map;

import static com.example.dave.gameEngine.MyMath.X;
import static com.example.dave.gameEngine.MyMath.Y;

public abstract class Entity{
    private final Map<ComponentType, Component> components;
    final GameSection gs;
    private static int counter=0;
    final int id;

    protected Entity(GameSection gs) {
        components = new EnumMap<ComponentType, Component>(ComponentType.class);
        this.gs = gs;
        this.id = MainActivity.identifiedGameObjectCacheSize + counter++;
    }

    protected Entity(GameSection gs, int explicitId) {
        components = new EnumMap<ComponentType, Component>(ComponentType.class);
        this.gs = gs;
        this.id=explicitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity that = (Entity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return id; }

    public void addComponent(Component c){
        if(c!=null)
            components.put(c.type(), c);
    }

    public Component getComponent(ComponentType cType){
        return components.get(cType);
    }

    public Physical_Cmpnt getPhysical( ){
        return (Physical_Cmpnt) components.get(ComponentType.Physics);
    }

    public Drawable_Cmpnt getDrawable( ){
        return (Drawable_Cmpnt) components.get(ComponentType.Drawable);
    }

    public Health_Cmpnt getHealth( ){
        return (Health_Cmpnt) components.get(ComponentType.Health);
    }

    public Kinematic_Cmpnt getKinematic( ){
        return (Kinematic_Cmpnt) components.get(ComponentType.Kinematic);
    }

    public Resizeable_Cmpnt getResizeable( ){
        return (Resizeable_Cmpnt) components.get(ComponentType.Resizeable);
    }

    public Control_Cmpnt getControllable( ){
        return (Control_Cmpnt) components.get(ComponentType.Control);
    }

    public Flags_Cmpnt getFlags( ){
        return (Flags_Cmpnt) components.get(ComponentType.Flags);
    }

    public AI_cmpnt getAI( ){
        return (AI_cmpnt)components.get(ComponentType.AI);
    }

    public Animation_Cmpnt getAnimation() {
        return (Animation_Cmpnt)components.get(ComponentType.Animation);
    }

    public Summoner_Cmpnt getSummoner() {
        return (Summoner_Cmpnt)components.get(ComponentType.Summoner);
    }

    public boolean hasComponent(ComponentType cType){
        return components.containsKey(cType);
    }

    public abstract void die();

    public void remove(){
        for(Component cmpnt : components.values()){
            cmpnt.clear();
        }
        components.clear();
    }

    public static void resetIdCounter(){
        counter=0;
    }

    public float[] direct(Entity other, boolean towards){
        float ret[] = new float[2];
        if(other==null) return ret;

        ret[X] = other.getPhysical().getX() - this.getPhysical().getX();
        ret[Y] = other.getPhysical().getY() - this.getPhysical().getY();
        if(!towards){
            ret[X]*=-1; ret[Y]*=-1;
        }
        //_Log.d("Direct", "Direct ("+this.getPhysical().getX()+", "+this.getPhysical().getY()+") towards ("+other.getPhysical().getX()+", "+other.getPhysical().getY()+")");
        return ret;
    }

    public float[] directX(Entity other, boolean towards){
        float ret[] = new float[]{0, 0};
        if(other==null) return ret;

        ret[X] = other.getPhysical().getX() - this.getPhysical().getX();

        if(!towards)  ret[X]*=-1;
        return ret;
    }

    public float[] directY(Entity other, boolean towards){
        float ret[] = new float[]{0, 0};
        if(other==null) return ret;

        ret[Y] = other.getPhysical().getY() - this.getPhysical().getY();

        if(!towards)  ret[Y]*=-1;
        return ret;
    }

    public float[] direct(float[] ret, Entity other, boolean towards){
        if(other==null) return ret;

        ret[X] = other.getPhysical().getX() - this.getPhysical().getX();
        ret[Y] = other.getPhysical().getY() - this.getPhysical().getY();
        if(!towards){
            ret[X]*=-1; ret[Y]*=-1;
        }
        //_Log.d("Direct", "Direct ("+this.getPhysical().getX()+", "+this.getPhysical().getY()+") towards ("+other.getPhysical().getX()+", "+other.getPhysical().getY()+")");
        return ret;
    }

    public float[] directX(float[] ret, Entity other, boolean towards){
        if(other==null) return ret;

        ret[X] = other.getPhysical().getX() - this.getPhysical().getX();

        if(!towards)  ret[X]*=-1;
        return ret;
    }

    public float[] directY(float[] ret, Entity other, boolean towards){
        if(other==null) return ret;

        ret[Y] = other.getPhysical().getY() - this.getPhysical().getY();

        if(!towards)  ret[Y]*=-1;
        return ret;
    }
}
