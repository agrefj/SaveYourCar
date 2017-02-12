package ru.agrefj.saveyourcar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class SaveYourCar extends ApplicationAdapter {
	private Texture carImage;
	private Texture leftBorder;
	private Texture rightBorder;
	private Texture road;
	private Texture roadSurface;
	private Texture pitTexture;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Array<Rectangle> pits;
	private long lastDropTime;

	private Rectangle car;

	Integer carWidth = 64;
	Integer carLength =64;

	Integer cameraWidth = 480;
	Integer cameraLenth = 800;

	Integer borderSize = 96;

	Integer leap =1;
	Integer speed = 1;
	Integer speedCount =0;

	long startTime = 0;


	@Override
	public void create () {
		carImage = new Texture(Gdx.files.internal("car.png"));
		leftBorder = new Texture(Gdx.files.internal("leftBorder.png"));
		rightBorder = new Texture(Gdx.files.internal("rightBorder.png"));
		roadSurface = new Texture(Gdx.files.internal("roadSurface.png"));
		road = new Texture(Gdx.files.internal("road.png"));
		pitTexture = new Texture(Gdx.files.internal("pit.png"));
		roadSurface.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//		road.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//		leftBorder.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//		rightBorder.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);

		batch = new SpriteBatch();

		car = new Rectangle();
		car.x = cameraWidth/2-carWidth/2;
		car.y = 20;
		car.width = carWidth;
		car.height = carLength;


		startTime = TimeUtils.nanoTime();


		pits = new Array<Rectangle>();
		spawnPit();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		lastDropTime = TimeUtils.nanoTime();

		Gdx.app.log("nanoTime", String.valueOf(TimeUtils.nanoTime()));
		Gdx.app.log("lastDropTime", String.valueOf(String.valueOf(lastDropTime)));

		if((TimeUtils.nanoTime() - lastDropTime) > 1000000000) {
			Gdx.app.log("ok,", "we are here");
			spawnPit(); //each second
		}

		Iterator<Rectangle> iter = pits.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 96 < 0) iter.remove();
		}



		if(Gdx.input.isTouched()) {;
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if(touchPos.x <= carWidth/2+borderSize) {
				car.x = borderSize;
			} else if(touchPos.x > (cameraWidth - carWidth/2 - borderSize) ){
				car.x = cameraWidth - carWidth-borderSize ;
			}
			else {
				car.x = touchPos.x -carWidth/2;
			}
		}



		batch.setProjectionMatrix(camera.combined);
		batch.begin();
//		batch.draw(road,96,0);
//		batch.draw(leftBorder,0,0);
//		batch.draw(rightBorder,384,0);
		batch.draw(roadSurface,0,0);

		for(Rectangle pit: pits) {
			batch.draw(pitTexture, pit.x, pit.y);
		}

		batch.draw(roadSurface,0, 0, 0 , leap, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		leap -=speed;
//		speedCount +=1;
//		if(speedCount < 10000000){
//			speed++;
//			speedCount = 0;
//		}



		if (TimeUtils.timeSinceNanos(startTime) > 1000000000) { //each second
			if(speed<35){
				speed++;


			}
			Gdx.app.log("Speed", String.valueOf(speed));

			startTime = TimeUtils.nanoTime();
		}

		batch.draw(carImage, car.x, car.y);
		batch.end();



	}

	
	@Override
	public void dispose () {

	}


	private void spawnPit() {
		Rectangle pit = new Rectangle();
		pit.x=192;
		pit.y=800;
		pit.width = 96;
		pit.height = 96;
		pits.add(pit);
		lastDropTime = TimeUtils.nanoTime();
	}
}
