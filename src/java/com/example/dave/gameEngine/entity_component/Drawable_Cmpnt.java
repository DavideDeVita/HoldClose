package com.example.dave.gameEngine.entity_component;

import com.example.dave.gameEngine.Box;
import com.example.dave.gameEngine.dataDriven.component.Drawable_Properties;

public abstract class Drawable_Cmpnt extends Component {
    protected Drawable_Cmpnt(Entity owner) {
        super(owner);
    }

    @Override
    public final ComponentType type(){
        return ComponentType.Drawable;
    }

    public abstract boolean draw();

    public enum Motive{Bitmap, Monochrome, Animated};
}

abstract class DrawablePhysicalObj_Cmpnt extends Drawable_Cmpnt {
    protected final Physical_Cmpnt physicalEgo;
    protected float x, y, angle; //Così non alloco ogni volta
    private float screen_x, screen_y, screen_angle; //Così non alloco ogni volta
    private final float epsilon; //tollerance on draw

    public DrawablePhysicalObj_Cmpnt(Entity owner, Drawable_Properties draw_p) {
        super(owner);
        physicalEgo = owner.getPhysical();
        if (physicalEgo==null) throw new NullPointerException("No Physical Component found in DrawablePhysicObj_Cmpnt");
        this.epsilon=draw_p.eps;
    }

    @Override
    public boolean draw(){
        // Physical position of the center
        x = physicalEgo.getX(); y = physicalEgo.getY(); angle = physicalEgo.getAngle();
        // Cropping
        Box view = owner.gs.currentView;
        if (view.contains(x, y, epsilon)) {
            // Screen position
            screen_x = owner.gs.toPixelsX(x);
            screen_y = owner.gs.toPixelsY(y);
            screen_angle = owner.gs.toScreenAngle(angle);
            this.draw(screen_x, screen_y, screen_angle);
            return true;
        }
        else
            return false;
    }

    public abstract void draw(float x, float y, float angle);

    public abstract void updateScreenSize(float physDimX, float physDimY);
}
