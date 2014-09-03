package com.aopujidian.launcher.slide;

import java.util.Random;

import com.aopujidian.launcher.R;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class Animations {
	private static final int [] POSITIONS = new int[]{-4, 4};
	private static int ANIMATION_DURATION = 3 * 1000;
	private static int RANDOM_X;
	private static int RANDOM_Y;
	
	private static float START_SCALE = 1.0f;
	
	private static float END_SCALE = 1.025f;
	
	public static void setAnimationDuration(int duration) {
		ANIMATION_DURATION = duration;
	}

	private static void randomPosition(){
		Random random = new Random(100);
		RANDOM_X = POSITIONS[random.nextInt(100) % 2];
		RANDOM_Y = POSITIONS[random.nextInt(100) % 2];
	}
	
	public static Animation inAnimation(Context context){
		randomPosition();
		TranslateAnimation translate = new TranslateAnimation(0, RANDOM_X, 0, RANDOM_Y);
        translate.setDuration(ANIMATION_DURATION);
        ScaleAnimation scale = new ScaleAnimation(START_SCALE, END_SCALE, START_SCALE, END_SCALE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
        scale.setDuration(ANIMATION_DURATION);
        
        AnimationSet animationSetIn = new AnimationSet(false);
//        animationSetIn.addAnimation(translate);
//        animationSetIn.addAnimation(scale);
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animationSetIn.addAnimation(fadeIn);
        return fadeIn;
	}
	
	public static Animation outAnimation(Context context){
		TranslateAnimation translateOut = new TranslateAnimation(RANDOM_X, RANDOM_X * 2, RANDOM_Y, RANDOM_Y * 2);
        translateOut.setDuration(ANIMATION_DURATION);
        ScaleAnimation scaleOut = new ScaleAnimation(END_SCALE, END_SCALE * 2, END_SCALE, END_SCALE * 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
        scaleOut.setDuration(ANIMATION_DURATION);
        
        AnimationSet animationSetOut = new AnimationSet(false);
//        animationSetOut.addAnimation(translateOut);
//        animationSetOut.addAnimation(scaleOut);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        animationSetOut.addAnimation(fadeOut);
        return fadeOut;
	}
}
