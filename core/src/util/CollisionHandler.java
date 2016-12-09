// Drew Misicko

package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import com.misicko.gdx.game1.WorldController;
import com.misicko.gdx.game1.Assets;

import objects.AbstractGameObject;
import objects.Dirt;
import objects.Pusheen;
import objects.GenericPowerup;
import objects.Pusheen.JUMP_STATE;
import objects.SuperCookie;

import com.misicko.gdx.game1.Level;

public class CollisionHandler implements ContactListener
{
    private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;

    private WorldController world;

    public CollisionHandler(WorldController w)
    {
    	world = w;
        listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
    }
    
    public void addListener(short categoryA, short categoryB, ContactListener listener)
    {
        addListenerInternal(categoryA, categoryB, listener);
        addListenerInternal(categoryB, categoryA, listener);
    }
    
    @Override
    public void beginContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();


       // processContact(contact);
        processContact(contact);

        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.beginContact(contact);
        }
    }

    
    @Override
    public void endContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.endContact(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.preSolve(contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.postSolve(contact, impulse);
        }
    }

    private void addListenerInternal(short categoryA, short categoryB, ContactListener listener)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            listenerCollection = new ObjectMap<Short, ContactListener>();
            listeners.put(categoryA, listenerCollection);
        }
        listenerCollection.put(categoryB, listener);
    }

    private ContactListener getListener(short categoryA, short categoryB)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            return null;
        }
        return listenerCollection.get(categoryB);
    }

    
    private void processContact(Contact contact)
    {
    	Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        AbstractGameObject objA = (AbstractGameObject)fixtureA.getBody().getUserData();
        AbstractGameObject objB = (AbstractGameObject)fixtureB.getBody().getUserData();

        if (objA instanceof Pusheen)
        {
        	processPlayerContact(fixtureA, fixtureB);
        }
        else if (objB instanceof Pusheen)
        {
        	processPlayerContact(fixtureB, fixtureA);
        }
    }
    
    private void processPlayerContact(Fixture playerFixture, Fixture objFixture)
    {
        if (objFixture.getBody().getUserData() instanceof Dirt)
        {
        	world.resetJump();
        	world.level.pusheen.grounded = true;
        	world.level.pusheen.jumping = false;
        	world.level.pusheen.jumpState = JUMP_STATE.GROUNDED;
        }

    	else if (objFixture.getBody().getUserData() instanceof GenericPowerup)
        {
            AudioManager.instance.play(Assets.instance.sounds.pickupGenericPowerup);
            //AudioManager.instance.play(Assets.instance.sounds.pickupSuperCookie);
            //AudioManager.instance.play(Assets.instance.sounds.jump);
            //AudioManager.instance.play(Assets.instance.sounds.liveLost);

            GenericPowerup genericPowerup = (GenericPowerup) objFixture.getBody().getUserData();
            //world.score += genericPowerup.getScore();
            world.flagForRemoval(genericPowerup);
        }
    	else if (objFixture.getBody().getUserData() instanceof SuperCookie)
        {
            //AudioManager.instance.play(Assets.instance.sounds.pickupGenericPowerup);
            AudioManager.instance.play(Assets.instance.sounds.pickupSuperCookie);
            //AudioManager.instance.play(Assets.instance.sounds.jump);
            //AudioManager.instance.play(Assets.instance.sounds.liveLost);

            SuperCookie superCookie = (SuperCookie) objFixture.getBody().getUserData();
            //world.score += genericPowerup.getScore();
            world.flagForRemoval(superCookie);
        }
    }
}