package dev.atomixsoft.solar_eclipse.client.graphics.ui.base;

import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The main elements of our UI system. They will handle all drawable and animated UI elements from Windows, to Buttons.</p>
 */
public abstract class Actor {
    private static int ACTOR_ID_ITER = 0;

    protected final List<Actor> m_Children;
    protected Actor m_Parent;

    protected int m_Id = -1;
    protected Vector2f m_Position;
    protected Vector2f m_Size;

    public Actor() {
        m_Children = new ArrayList<>();
        m_Parent = null;

        m_Position = new Vector2f();
        m_Size = new Vector2f();
        m_Id = ACTOR_ID_ITER++;
    }

    public void draw(SpriteBatch batch) {
        // TODO: Sort Children so they don't overlap, might leave to "Type Hierarchy"
        for(Actor child : m_Children)
            child.draw(batch);
    }

    public void act(float delta) {
        for (Actor child : m_Children)
            child.act(delta);
    }

    public void dispose() {
        m_Children.forEach(Actor::onRemove);
        m_Children.clear();
    }

    public abstract void onAdd();
    public abstract void onRemove();

    protected void adjustToParent() {
        if(m_Parent == null) return;

        Vector2f oldPos = m_Position;
        m_Position.set(m_Parent.getPosition().add(oldPos.x, oldPos.y));
    }

    public void addChild(Actor child) {
        m_Children.add(child);

        child.m_Parent = this;
        onAdd();
    }

    public void removeChild(Actor child) {
        m_Children.remove(child);

        child.m_Parent = null;
        onRemove();
    }

    public void setPosition(Vector2f position) {
        m_Position = position;
    }

    public void setSize(Vector2f size) {
        m_Size = size;
    }

    public Actor getParent() {
        return m_Parent;
    }

    public int getUUID() {
        return m_Id;
    }

    public Vector2f getPosition() {
        return m_Position;
    }

    public Vector2f getSize() {
        return m_Size;
    }

}
