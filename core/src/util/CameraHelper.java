/*
 * Drew Misicko
 */

package util;

import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import objects.AbstractGameObject;


public class CameraHelper {
	private static final String TAG = CameraHelper.class.getName();
	
	private final float MAX_ZOOM_IN = 0.25f;
	private final float MAX_ZOOM_OUT = 10.0f;
	
	private Vector2 position;
	private float zoom;
	//private Sprite target;
	private AbstractGameObject target;
	
	// from chapter 8 to smooth camera movement, and have rocks move up and down.
	private final float FOLLOW_SPEED = 4.0f;
	
	public CameraHelper () {
		position = new Vector2();
		zoom = 1.0f;
	}
	
	public void update (float deltaTime) {
		if (!hasTarget()) return;
		
		//position.x = target.getX() + target.getOriginX();
		//position.y = target.getY() + target.getOriginY();
		
		//position.x = target.position.x + target.origin.x;// removed in chapter 8
		//position.y = target.position.y + target.origin.y;// removed in chapter 8
		
		//added in chapter 8 to smooth camera movement
		position.lerp(target.position, FOLLOW_SPEED * deltaTime);
		
		// Prevent camera from moving down too far, from assignment 6
		position.y = Math.max(-1f, position.y);
	}
	public void setPosition (float x, float y) {
		this.position.set(x, y);
	}
	public Vector2 getPosition () { return position; }
	
	public void addZoom (float amount) { setZoom(zoom + amount); }
	public void setZoom (float zoom) {
		this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}
	public float getZoom () { return zoom; }
	
	public void setTarget(AbstractGameObject target) 
	{
		this.target = target; 
	}
	public AbstractGameObject getTarget ()
	{
		return target; 
	}
	public boolean hasTarget () { return target != null; }
	public boolean hasTarget (AbstractGameObject target) 
	{
		return hasTarget() && this.target.equals(target);
	}
	
	public void applyTo (OrthographicCamera camera) {
		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.zoom = zoom;
		camera.update();
	}
}