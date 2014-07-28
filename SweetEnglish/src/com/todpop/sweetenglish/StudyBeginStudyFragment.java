package com.todpop.sweetenglish;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.todpop.api.TypefaceActivity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudyBeginStudyFragment extends Fragment{
	LinearLayout frontCard;
	LinearLayout backCard;
	
	Animation inAnimation;
	Animation outAnimation;
	AnimatorSet leftInAnimator;
	Animator leftOutAnimator;
	Animator rightInAnimator;
	Animator rightOutAnimator;
	
	ImageView imgFront;
	ImageView imgBack;
	
	boolean isFront = true;
	
	static StudyBeginStudyFragment init(int val, ArrayList<StudyBeginWord> wordList){
		StudyBeginStudyFragment studyFragment = new StudyBeginStudyFragment();
		
		Bundle args = new Bundle();
		args.putInt("position", val);
		args.putParcelableArrayList("wordList", wordList);
		studyFragment.setArguments(args);
		return studyFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(android.os.Build.VERSION.SDK_INT >= 11){
			leftInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.anim.card_flip_animator_left_in);
			leftOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.anim.card_flip_animator_left_out);
			rightInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.anim.card_flip_animator_right_in);
			rightOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.anim.card_flip_animator_right_out);
			
			leftInAnimator.addListener(new AnimatorFlipDoneListener());
			rightInAnimator.addListener(new AnimatorFlipDoneListener());
		}
		else{
			inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.card_flip_in);
			outAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.card_flip_out);
			
			outAnimation.setAnimationListener(new AnimationFlipDoneListener());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Bundle bundle = getArguments();
		
		int position = bundle.getInt("position");
		StudyBeginWord wordClass = (StudyBeginWord)bundle.getParcelableArrayList("wordList").get(position);
		
		View layoutView = inflater.inflate(R.layout.fragment_study_begin, container, false);
		
		frontCard = (LinearLayout)layoutView.findViewById(R.id.fragment_study_begin_whole_card_front);
		backCard = (LinearLayout)layoutView.findViewById(R.id.fragment_study_begin_whole_card_back);
		
		ImageView countFront = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_on_front); 
		ImageView countBack = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_on_back); 
		
		imgFront = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_img_front);
		imgBack = (ImageView)layoutView.findViewById(R.id.fragment_study_begin_id_word_img_back);
		
		TextView wordFront = (TextView)layoutView.findViewById(R.id.study_word_tv_front);
		TextView wordBack = (TextView)layoutView.findViewById(R.id.study_word_tv_back);
		
		TextView pronounce = (TextView)layoutView.findViewById(R.id.study_word_pron_tv_front);
		
		TextView exFront = (TextView)layoutView.findViewById(R.id.study_word_ex_tv_front);
		TextView exBack = (TextView)layoutView.findViewById(R.id.study_word_ex_tv_back);
		
		DownloadImageTask task = new DownloadImageTask(imgFront, imgBack);
		task.execute("http://todpop.co.kr" + wordClass.getImgUrl());
		
		switch(position){
		case 0:
			countFront.setImageResource(R.drawable.study_8_img_number_1);
			countBack.setImageResource(R.drawable.study_8_img_number_1);
			break;
		case 1:
			countFront.setImageResource(R.drawable.study_8_img_number_2);
			countBack.setImageResource(R.drawable.study_8_img_number_2);
			break;
		case 2:
			countFront.setImageResource(R.drawable.study_8_img_number_3);
			countBack.setImageResource(R.drawable.study_8_img_number_3);
			break;
		case 3:
			countFront.setImageResource(R.drawable.study_8_img_number_4);
			countBack.setImageResource(R.drawable.study_8_img_number_4);
			break;
		case 4:
			countFront.setImageResource(R.drawable.study_8_img_number_5);
			countBack.setImageResource(R.drawable.study_8_img_number_5);
			break;
		case 5:
			countFront.setImageResource(R.drawable.study_8_img_number_6);
			countBack.setImageResource(R.drawable.study_8_img_number_6);
			break;
		case 6:
			countFront.setImageResource(R.drawable.study_8_img_number_7);
			countBack.setImageResource(R.drawable.study_8_img_number_7);
			break;
		case 7:
			countFront.setImageResource(R.drawable.study_8_img_number_8);
			countBack.setImageResource(R.drawable.study_8_img_number_8);
			break;
		case 8:
			countFront.setImageResource(R.drawable.study_8_img_number_9);
			countBack.setImageResource(R.drawable.study_8_img_number_9);
			break;
		case 9:
			countFront.setImageResource(R.drawable.study_8_img_number_10);
			countBack.setImageResource(R.drawable.study_8_img_number_10);
			break;
		}
		
		wordFront.setText(wordClass.getEngWord());
		wordBack.setText(wordClass.getKorWord());
		
		pronounce.setText(wordClass.getPhonetics());
		
		exFront.setText(wordClass.getEngExample());
		exBack.setText(wordClass.getKorExample());

		TypefaceActivity.setFont(wordFront);
		TypefaceActivity.setFont(wordFront);
		TypefaceActivity.setFont(pronounce);
		TypefaceActivity.setFont(exFront);
		TypefaceActivity.setFont(exBack);
		
		frontCard.setOnClickListener(new FlipListener());
		backCard.setOnClickListener(new FlipListener());
		
		return layoutView;
	}
	
	@Override
	public void onDestroyView() {
        super.onDestroyView();
        
        Drawable front = imgFront.getDrawable();
        Drawable back = imgBack.getDrawable();
        
        if(front instanceof BitmapDrawable){
	        Bitmap bitmap = ((BitmapDrawable)front).getBitmap();
	        bitmap.recycle();
	        bitmap = null;
        }
        if(back instanceof BitmapDrawable){
        	Bitmap bitmap = ((BitmapDrawable)back).getBitmap();
        	bitmap.recycle();
        	bitmap = null;
        }
        front.setCallback(null);
    }
	
	private class FlipListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(android.os.Build.VERSION.SDK_INT >= 11){
				if(isFront){
					isFront = false;
					
					leftInAnimator.setTarget(backCard);
					leftOutAnimator.setTarget(frontCard);
					
					leftInAnimator.start();
					leftOutAnimator.start();
				}
				else{
					isFront = true;
					
					rightInAnimator.setTarget(frontCard);
					rightOutAnimator.setTarget(backCard);
					
					rightInAnimator.start();
					rightOutAnimator.start();
				}
			}
			else{
				if(isFront){
					isFront = false;
					
					frontCard.startAnimation(outAnimation);
					backCard.startAnimation(inAnimation);
				}
				else{
					isFront = true;
					
					frontCard.startAnimation(inAnimation);
					backCard.startAnimation(outAnimation);
				}
			}
		}
		
	}
	private class AnimatorFlipDoneListener implements AnimatorListener{

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if(isFront){
				backCard.setVisibility(View.GONE);
			}
			else{
				frontCard.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationStart(Animator animation) {
			if(isFront){
				frontCard.setVisibility(View.VISIBLE);
			}
			else{
				backCard.setVisibility(View.VISIBLE);
			}
		}
		
	}
	private class AnimationFlipDoneListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation arg0) {
			if(isFront){
				backCard.setVisibility(View.GONE);
			}
			else{
				frontCard.setVisibility(View.GONE);
			}			
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
			if(isFront){
				frontCard.setVisibility(View.VISIBLE);
			}
			else{
				backCard.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReferenceFront;
		private final WeakReference<ImageView> imageViewReferenceBack;
		
		public DownloadImageTask(ImageView front, ImageView back){
			imageViewReferenceFront = new WeakReference<ImageView>(front);
			imageViewReferenceBack = new WeakReference<ImageView>(back);
		}
		
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) 
		{
			try{
				if( imageViewReferenceFront != null && result != null){
					final ImageView imageView = imageViewReferenceFront.get();
					if( imageView != null){
						Drawable[] layers = {imageView.getDrawable(), new BitmapDrawable(getResources(), result)};
						TransitionDrawable transDrawable = new TransitionDrawable(layers);
						imageView.setImageDrawable(transDrawable);
						transDrawable.startTransition(300);
					}
				}
				if( imageViewReferenceBack != null && result != null){
					final ImageView imageView = imageViewReferenceBack.get();
					if( imageView != null){
						imageView.setImageBitmap(result);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				result.recycle();
			}
		}
	}		
}