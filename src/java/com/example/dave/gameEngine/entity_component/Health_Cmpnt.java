package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.FloatValue;
import com.example.dave.gameEngine.MyMath;
import com.example.dave.gameEngine._Log;
import com.example.dave.gameEngine.dataDriven.component.Health_Properties;
import com.example.dave.gameEngine.GameElement;
import com.example.dave.gameEngine.gameLoop;

import static com.example.dave.gameEngine.GameElement.*;

public class Health_Cmpnt extends Component {
    private final int healPriority; /*lower healPriority gets healed.. the other damaged*/
    private final float maxHealth;
    public float health;
    private final FloatValue proportionalHealthDmg, constDmg; //used to be d=pv+f
    private final DyingBehaviour dyingBehaviour;
    //private int inDmg;
    private final boolean damageable;
    public /*final*/ GameElement element;

    private long timeOfLastHit=0, timeOfLastHeal=0;
    private Entity lastGOHit=null;
    private final static long hitCooldown = 250_000_000L, healCooldown = 100_000_000L;
    private final static int consecutiveDelay=3;


    Health_Cmpnt(Entity owner, Health_Properties health_p) {
        super(owner);
        this.maxHealth = health_p.maxHealth;
        if (health_p.startHealth==null || health_p.startHealth.leq(0f))
            this.health = maxHealth;
        else
        	this.health=MyMath.min(health_p.startHealth.get(), maxHealth);
        this.healPriority = (health_p.healPriority>=0) ? health_p.healPriority : MyMath.randomInt(1, 5);
        this.damageable=health_p.damageable;
        this.element = health_p.element;
        this.dyingBehaviour = health_p.dyingBehaviour;
        this.proportionalHealthDmg = health_p.proportionalHealthDmg;
        this.constDmg = health_p.constDmg;
        onLifeAltered();
    }

    private void damage(float dmg){
        if(!damageable || dmg <= 0)
            return;
        float preHealth=health;
        this.health-=dmg;
        if(this.health>0)
            this.onDamage(preHealth, dmg);
        else
            this.onDie(preHealth, dmg);
    }

    private void heal(float heal){
        if(!damageable || heal <= 0)
            return;
        this.health+=heal;
        if(health>maxHealth)
            health=maxHealth;
        this.onHeal();
    }

    //When both eE are negative
    private void transferHealthTo(Health_Cmpnt to){
        float oDmg = outDmg(); //Just to log
        float supposedDmg = MyMath.min(-oDmg*eE(to,this), to.maxHealth-to.health, health);
        if(_Log.LOG_ACTIVE){
            _Log.i("Health","transfer heal "+supposedDmg);}
        if(supposedDmg<0) {
            if(_Log.LOG_ACTIVE){
                _Log.e("Health", "outDmg negative(" + oDmg + "): min( -" + oDmg + "*" + eE(to, this) + ", " + to.maxHealth + "-" + to.health + ", " + health);}
            return;
        }
        to.heal(supposedDmg);
        damage(supposedDmg); //Health Transferred (same quantity)
    }

    //When only one eE is negative
    private void healButGetDamaged(Health_Cmpnt to){
        float oDmg = outDmg();
        float supposedDmg = MyMath.min(-oDmg*eE(to,this), to.maxHealth-to.health, health);
        if(_Log.LOG_ACTIVE){
            _Log.i("Health",owner+" will heal "+to.owner+" "+supposedDmg+ "hp. But will get Damaged");}
        if(supposedDmg<0) {
            if(_Log.LOG_ACTIVE){
                _Log.e("Health", "outDmg negative(" + oDmg + "): min( -" + oDmg + "*" + eE(to, this) + ", " + to.maxHealth + "-" + to.health + ", " + health);}
            return;
        }
        to.heal(supposedDmg);
        damage( eE(this,to) * to.outDmg() );
    }

    private static long hitTime;
    @gameLoop
    public static void hit(Health_Cmpnt d, Health_Cmpnt v){
        hitTime=System.nanoTime();
        if(_Log.LOG_ACTIVE){
            _Log.i("Health","hitting "+d.element+" and "+v.element);}
        if(eE(d,v)>0 && eE(v,d)>0) { //both damaged
            float vDmg = eE(v,d) * d.outDmg();
            if(d.manageHitCooldown(v.owner, hitTime))
                d.damage(eE(d,v) * v.outDmg());
            //else _Log.w("Health", "to soon for d "+d.owner);
            if(v.manageHitCooldown(d.owner, hitTime))
                v.damage(vDmg);
            //else _Log.w("Health", "to soon for v "+v.owner);
        }
        else if(eE(d,v)<0 && eE(v, d)<0){ //Both could be healed
            if(_Log.LOG_ACTIVE){
                _Log.d("Health", "Both healable");}
            if(d.healPriority<v.healPriority && d.manageHealCooldown(v.owner, hitTime)) //d healed
                v.transferHealthTo(d);
            else if (v.healPriority<d.healPriority && v.manageHealCooldown(d.owner, hitTime)) //v healed
                d.transferHealthTo(v);
            else
                ;//_Log.i("Health","same priority or too soon.. "+d.healPriority+" "+v.healPriority);
        }
        else if (eE(d,v)<0 ) { //only d could be healed
            if(d.manageHealCooldown(v.owner, hitTime))
                v.healButGetDamaged(d);
        }
        else if (eE(v,d)<0) { //only v could be healed
            if (v.manageHealCooldown(d.owner, hitTime))
                d.healButGetDamaged(v);
        }
        else
        if(_Log.LOG_ACTIVE){
            _Log.w("Health", "impossible else case");}
    }

    private float outDmg() {
        return proportionalHealthDmg.get() *health + constDmg.get();
    }

    private boolean manageHitCooldown(Entity hitBy, long hitTime){
        if(hitBy.equals(lastGOHit)) {
            if (hitTime - timeOfLastHit > hitCooldown*consecutiveDelay) {
                timeOfLastHit = hitTime;
                return true;
            }
            return false;
        }
        else if (hitTime - timeOfLastHit > hitCooldown){
            timeOfLastHit = hitTime;
            lastGOHit = hitBy;
            return true;
        }
        return false;
    }

    private boolean manageHealCooldown(Entity hitBy, long healTime){
        if(_Log.LOG_ACTIVE){
            _Log.d("Health_Heal", "healTime "+((healTime*1f)/1_000_000_000L)+"\tlast heal "+((timeOfLastHeal*1f)/1_000_000_000L));}
        if(hitBy.equals(lastGOHit)) {
            if (healTime - timeOfLastHeal > healCooldown*consecutiveDelay) {
                timeOfLastHeal = healTime;
                return true;
            }
            return false;
        }
        else if (healTime - timeOfLastHeal > healCooldown){
            timeOfLastHeal = healTime;
            lastGOHit = hitBy;
            return true;
        }
        return false;
    }

    private void onDamage(float preH, float dmg) {
        onLifeAltered();
        if(_Log.LOG_ACTIVE){
            _Log.i("Health",owner+" damaged. From "+preH+" hp to "+health+"/"+maxHealth+" ("+dmg+" damage)");}
    }

    private void onDie(float preH, float dmg) {
        if(_Log.LOG_ACTIVE){
            _Log.i("Health",owner+" deadly hit: "+dmg+" damages having "+preH+" hp");
            _Log.i(owner+" deadly hit: "+dmg+" damages having "+preH+" hp");}
        dyingBehaviour.onDie(this);
        //owner.remove();
    }

    private void onHeal() {
        onLifeAltered();
        if(_Log.LOG_ACTIVE){
            _Log.i("Health",owner+ " healed "+health+"/"+maxHealth);}
    }

    private void onLifeAltered() {
        if(owner.hasComponent(ComponentType.Resizeable))
            owner.getResizeable().resize((1.f*health)/maxHealth);
    }

    @Override
    public ComponentType type() { return ComponentType.Health;}

    private static float eE(Health_Cmpnt dmgReceiver, Health_Cmpnt dmgDealer){
        return elementEffect[dmgReceiver.element.ordinal()][dmgDealer.element.ordinal()];
    }

    /*Mortal, Phoenix, Klesis**/
    public enum DyingBehaviour {
        Die {
            @Override
            protected void onDie(Health_Cmpnt This) {
                if(_Log.LOG_ACTIVE){
                    _Log.i("Health", This.owner+" dead and removed");}
                This.owner.die();
            }
        }, Resurrect {
            @Override
            protected void onDie(Health_Cmpnt This) {
                if(_Log.LOG_ACTIVE){
                    _Log.i("Health", This.owner+" resurrected.. full Hp");}
                This.health=This.maxHealth;
                This.onLifeAltered();
            }
        }, Linger {
            @Override
            protected void onDie(Health_Cmpnt This) {
                if(_Log.LOG_ACTIVE){
                    _Log.i("Health", This.owner+" lingers between life and death.. he won't let go");}
                This.health=1;
                This.onLifeAltered();
            }
        };

        protected abstract void onDie(Health_Cmpnt This);
    };
}
