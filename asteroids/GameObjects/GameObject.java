package asteroids.GameObjects;

public class GameObject 
{
	public static int width, height;

	protected double  angle;
	protected double  angSpeed;
	protected double  x, y;
	protected double  xspeed, yspeed;
	protected Sprite objSprite;
	
	public GameObject()
	{
		this.angle = 0;
		this.angSpeed = 0;
		this.x = this.y = 0;
		this.xspeed = this.yspeed = 0;
		this.objSprite = new Sprite();
	}

	public boolean update() 
	{
		boolean wrapped;

		this.angle += this.angSpeed;
		if (this.angle < 0)
			this.angle += 2 * Math.PI;
		if (this.angle > 2 * Math.PI)
			this.angle -= 2 * Math.PI;
		wrapped = false;
		this.x += this.xspeed;
		if (this.x < -width / 2) {
			this.x += width;
			wrapped = true;
		}
		if (this.x > width / 2) {
			this.x -= width;
			wrapped = true;
		}
		this.y -= this.yspeed;
		if (this.y < -height / 2) {
			this.y += height;
			wrapped = true;
		}
		if (this.y > height / 2) {
			this.y -= height;
			wrapped = true;
		}

		return wrapped;
	}
	
	public void draw()
	{
		this.objSprite.draw(x, y, angle, width, height);
	}
	
	public boolean checkCollision(GameObject obj)
	{
		return this.objSprite.isColliding(obj.getObjSprite());
	}
	
	public Sprite getObjSprite(){ return this.objSprite;}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getAng(){return angle;}
}
