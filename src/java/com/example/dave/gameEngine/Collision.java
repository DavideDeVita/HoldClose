package com.example.dave.gameEngine;

import com.example.dave.gameEngine.entity_component.Health_Cmpnt;
import com.example.dave.gameEngine.entity_component.ComponentType;
import com.example.dave.gameEngine.entity_component.GameObject;
import com.example.dave.gameEngine.myMultimedia.MySound;
import com.example.dave.gameEngine.myMultimedia.Playable;

public class Collision {
    final GameObject a, b;
    final float x,y;
    final Health_Cmpnt hA, hB;
    final boolean theyHaveHealth;

    Collision(GameObject a, GameObject b) {
        this.a = a;
        this.b = b;
        this.x = ( a.getPhysical().getX() + b.getPhysical().getX() )/ 2;
        this.y = ( a.getPhysical().getY() + b.getPhysical().getY() )/ 2;
        this.hA = a.getHealth();
        this.hB = b.getHealth();
        this.theyHaveHealth = (hA!=null && hB!=null);
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision))
            return false;
        Collision otherCollision = (Collision) other;
        return (a.equals(otherCollision.a) && b.equals(otherCollision.b)) ||
               (a.equals(otherCollision.b) && b.equals(otherCollision.a));
    }

    @gameLoop
    void manageHits(Box dmgFreeZone){
        if (_Log.LOG_ACTIVE)
            _Log.i("onHit", a+" and "+b);
        if(theyHaveHealth &&
                (dmgFreeZone==null || !dmgFreeZone.contains(x, y)) )
            Health_Cmpnt.hit(hA, hB);
        a.onHit(b, hB);
        b.onHit(a, hA);
    }

    /**To distance sounds from each other*/
    private static long timeOfLastCollisionSound = 0, currentTime;
    private static final long SOUND_COLLISION_COOLDOWN = 250_000_000L;
    private static Playable sound;
    void manageSound(){
        if(theyHaveHealth) {
            sound = CollisionSounds.getSound(hA.element, hB.element);
            //sound = CollisionSounds.getSound(a.getClass(), b.getClass());
            if (sound != null) {
                currentTime = System.nanoTime();
                if (currentTime - timeOfLastCollisionSound > SOUND_COLLISION_COOLDOWN) {
                    timeOfLastCollisionSound = currentTime;
                    sound.play(0.4f);
                }
            }
        }
    }
}
