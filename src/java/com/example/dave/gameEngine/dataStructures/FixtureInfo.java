package com.example.dave.gameEngine.dataStructures;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.Shape;

public class FixtureInfo implements Indexed{
    private final Body body;
    private Fixture fixture, oldFixture;
    public FixtureDef fd;
    public float dimX, dimY;
    //public Shape shape;

    public FixtureInfo(Body body, FixtureDef fdef) {
        this.body=body;
        this.fixture = body.getFixtureList();
        this.fd = new FixtureDef();
        fd.setShape(fdef.getShape()); //
        fd.setFriction(fdef.getFriction());       // default 0.2
        fd.setRestitution(fdef.getRestitution());    // default 0
        fd.setDensity(fdef.getDensity());     // default 0
    }

    public void updateFixture(Shape shape){
        fd.setShape(shape);
        oldFixture=fixture;
        fixture = body.createFixture(fd);
        body.destroyFixture(oldFixture);
        oldFixture=null;
    }

    @Override
    public String toString(){
        return "finfo";
    }

    private static int instances = 0;
    private final int index=instances++;

    @Override
    public final int getIndex(){
        return this.index;
    }

	public void clear() {
	}
}
