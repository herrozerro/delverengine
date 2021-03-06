package com.interrupt.dungeoneer.gfx.animation.lerp3d;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class LerpedAnimation {
	
	private float time = 0;
	private float total_time = 0;
	public float speed = 1f;

	private float calc_length = 1f;
	
	public boolean playing = false;
	
	public Vector3 startTransform = null;
	public Vector3 startRotation = null;
	
	public Vector3 curTransform = new Vector3();
	public Vector3 curRotation = new Vector3();
	
	public float actionTime = 6f;
	
	public Array<LerpFrame> frames;
	int frame = 0;

	public int startTexOffset = 0;
	public int endTexOffset = 0;
	public int curTexOffset = 0;
	
	private Interpolation lerpFrame = Interpolation.linear;
	
	public LerpedAnimation() { }
	
	public LerpedAnimation(Array<LerpFrame> frames) {
		this.frames = frames;
	}
	
	public float length() {
		float length = 0.0f;
		
		for (LerpFrame frame : frames) {
			length += frame.length;
		}
		
		return length;
	}

    public float timeMod() {
        return time / length();
    }
	
	public void play(float speed) {
		this.speed = speed;
		playing = true;
		time = 0;
		frame = 0;
		total_time = 0;
		calc_length = length();
		
		startTransform = null;
		startRotation = null;
		
		curTransform.set(frames.get(0).transform);
		curRotation.set(frames.get(0).rotation);
	}

    public void play(float speed, Interpolation interpolation) {
        play(speed);
        this.lerpFrame = interpolation;
    }
	
	public void play(float speed, LerpedAnimation previousAnimation) {
		this.speed = speed;
		playing = true;
		time = 0;
		frame = 0;
		total_time = 0;
		calc_length = length();
		
		startTransform = null;
		startRotation = null;
		
		if(previousAnimation != null) {
			startTransform = new Vector3(previousAnimation.curTransform);
			startRotation = new Vector3(previousAnimation.curRotation);
			
			curTransform.set(startTransform);
			curRotation.set(startRotation);
		}
		else {
			curTransform.set(frames.get(0).transform);
			curRotation.set(frames.get(0).rotation);
		}
	}
	
	public void stop() {
		playing = false;
		time = 0;
		frame = 0;
	}
	
	public void animate(float delta) {
		if(playing) {
			time += delta * speed;
			total_time += delta * speed;
			
			if(time > frames.get(frame).length) {
				while(frame + 1 <= frames.size && time > frames.get(frame).length) {
					time -= frames.get(frame).length;
					frame++;
				}
				
				if(frame + 1 > frames.size - 1) {
					playing = false;
					frame = frames.size - 2;
					time = frames.get(frame).length;
				}
			}
			
			Vector3 curFrameTransform = getTransformAtFrame(frame);
			Vector3 curFrameRotation = getRotationAtFrame(frame);
			
			Vector3 nextFrameTransform = getTransformAtFrame(frame + 1);
			Vector3 nextFrameRotation = getRotationAtFrame(frame + 1);

			float t = time / frames.get(frame).length;
			
			curTransform.x = lerpFrame.apply(curFrameTransform.x, nextFrameTransform.x, t);
			curTransform.y = lerpFrame.apply(curFrameTransform.y, nextFrameTransform.y, t);
			curTransform.z = lerpFrame.apply(curFrameTransform.z, nextFrameTransform.z, t);
			
			curRotation.x = lerpFrame.apply(curFrameRotation.x, nextFrameRotation.x, t);
			curRotation.y = lerpFrame.apply(curFrameRotation.y, nextFrameRotation.y, t);
			curRotation.z = lerpFrame.apply(curFrameRotation.z, nextFrameRotation.z, t);

			if(startTexOffset == endTexOffset) {
				curTexOffset = startTexOffset;
			}
			else {
				curTexOffset = Math.round(lerpFrame.apply((float)startTexOffset, (float)endTexOffset, total_time / calc_length));
				if(curTexOffset > endTexOffset) curTexOffset = endTexOffset;
			}
		}
	}

	public int getTexOffset() {
		return curTexOffset;
	}
	
	public Vector3 getTransformAtFrame(int index) {
		if(index == 0 && startTransform != null) return startTransform;
		return frames.get(index).transform;
	}
	
	public Vector3 getRotationAtFrame(int index) {
		if(index == 0 && startRotation != null) return startRotation;
		return frames.get(index).rotation;
	}
}
