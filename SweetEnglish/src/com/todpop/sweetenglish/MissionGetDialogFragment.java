package com.todpop.sweetenglish;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MissionGetDialogFragment extends DialogFragment{
	public static final int NEW_COMER = 0;
	public static final int PLAN = 1;
	public static final int INVITE = 2;
	public static final int ALARM = 3;
	public static final int ATTEND_3 = 4;
	public static final int ATTEND_7 = 5;
	public static final int ATTEND_10 = 6;
	public static final int ATTEND_1MONTH = 7;
	public static final int CLEAR_BASIC = 8;
	public static final int CLEAR_MIDDLE = 9;
	public static final int CLEAR_HIGH = 10;
	public static final int CLEAR_TOEIC = 11;
	public static final int MASTER_BASIC = 12;
	public static final int MASTER_MIDDLE = 13;
	public static final int MASTER_HIGH = 14;
	public static final int MASTER_TOEIC = 15;
	public static final int INFINITY_80 = 16;
	public static final int INFINITY_100 = 17;
	public static final int GOAL_10 = 18;
	public static final int GOAL_30 = 19;
	public static final int GOAL_50 = 20;
	public static final int GOAL_1MONTH = 21;
	public static final int LOCK_BG = 22;
	public static final int LOCK_CATEGORY = 23;
	public static final int LOCK_WORD = 24;
	public static final int TIME_1 = 25;
	public static final int TIME_3 = 26;
	public static final int TIME_5 = 27;
	public static final int TIME_EARLY = 28;
	public static final int TIME_OWL = 29;
	public static final int WORDBOOK_3GROUP = 30;
	public static final int WORDBOOK_30 = 31;
	public static final int WORDBOOK_CELLOPHANE = 32;
	public static final int WORDBOOK_TEST = 33;
	
	private ImageView badgeImg;
	private ImageView sparkleLeftUp;
	private ImageView sparkleLeftDown;
	private ImageView sparkleRightUp;
	private ImageView sparkleRightDown;
	
	private Animation largeAni;
	private Animation smallAni;
	
	static MissionGetDialogFragment newInstance(int type){
		MissionGetDialogFragment fragment = new MissionGetDialogFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.dialog_mission_get, container, false);
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		LinearLayout linear = (LinearLayout)v.findViewById(R.id.mission_get_linear);
		
		linear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
		});
		
		badgeImg = (ImageView)v.findViewById(R.id.mission_get_badge);
		sparkleLeftUp = (ImageView)v.findViewById(R.id.mission_get_left_up_sparkle);
		sparkleLeftDown = (ImageView)v.findViewById(R.id.mission_get_left_down_sparkle);
		sparkleRightUp = (ImageView)v.findViewById(R.id.mission_get_right_up_sparkle);
		sparkleRightDown = (ImageView)v.findViewById(R.id.mission_get_right_down_sparkle);

		loadAnimation();
		startAnimation();

		Bundle args = getArguments();
		
		int type = args.getInt("type");
		switch(type){
		case NEW_COMER:
			badgeImg.setImageResource(R.drawable.mission_img_badge_newcomer);
			break;
		case PLAN:
			badgeImg.setImageResource(R.drawable.mission_img_badge_plan);
			break;
		case INVITE:
			badgeImg.setImageResource(R.drawable.mission_img_badge_invite);
			break;
		case ALARM:
			badgeImg.setImageResource(R.drawable.mission_img_badge_alarm);
			break;
		case ATTEND_3:
			badgeImg.setImageResource(R.drawable.mission_img_badge_attend_3);
			break;
		case ATTEND_7:
			badgeImg.setImageResource(R.drawable.mission_img_badge_attend_7);
			break;
		case ATTEND_10:
			badgeImg.setImageResource(R.drawable.mission_img_badge_attend_10);
			break;
		case ATTEND_1MONTH:
			badgeImg.setImageResource(R.drawable.mission_img_badge_attend_1month);
			break;
		case CLEAR_BASIC:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear_basic);
			break;
		case CLEAR_MIDDLE:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear_middle);
			break;
		case CLEAR_HIGH:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear_high);
			break;
		case CLEAR_TOEIC:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear_toeic);
			break;
		case MASTER_BASIC:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear2_basic);
			break;
		case MASTER_MIDDLE:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear2_middle);
			break;
		case MASTER_HIGH:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear2_high);
			break;
		case MASTER_TOEIC:
			badgeImg.setImageResource(R.drawable.mission_img_badge_clear2_toeic);
			break;
		case INFINITY_80:
			badgeImg.setImageResource(R.drawable.mission_img_badge_infinity_80);
			break;
		case INFINITY_100:
			badgeImg.setImageResource(R.drawable.mission_img_badge_infinity_100);
			break;
		case GOAL_10:
			badgeImg.setImageResource(R.drawable.mission_img_badge_studygoal_10);
			break;
		case GOAL_30:
			badgeImg.setImageResource(R.drawable.mission_img_badge_studygoal_30);
			break;
		case GOAL_50:
			badgeImg.setImageResource(R.drawable.mission_img_badge_studygoal_50);
			break;
		case GOAL_1MONTH:
			badgeImg.setImageResource(R.drawable.mission_img_badge_studygoal_1month);
			break;
		case LOCK_BG:
			badgeImg.setImageResource(R.drawable.mission_img_badge_screenlock_bgchange);
			break;
		case LOCK_CATEGORY:
			badgeImg.setImageResource(R.drawable.mission_img_badge_screenlock_category);
			break;
		case LOCK_WORD:
			badgeImg.setImageResource(R.drawable.mission_img_badge_screenlock_word);
			break;
		case TIME_1:
			badgeImg.setImageResource(R.drawable.mission_img_badge_time_1);
			break;
		case TIME_3:
			badgeImg.setImageResource(R.drawable.mission_img_badge_time_3);
			break;
		case TIME_5:
			badgeImg.setImageResource(R.drawable.mission_img_badge_time_5);
			break;
		case TIME_EARLY:
			badgeImg.setImageResource(R.drawable.mission_img_badge_time_earlybird);
			break;
		case TIME_OWL:
			badgeImg.setImageResource(R.drawable.mission_img_badge_time_owl);
			break;
		case WORDBOOK_3GROUP:
			badgeImg.setImageResource(R.drawable.mission_img_badge_wordbook_3group);
			break;
		case WORDBOOK_30:
			badgeImg.setImageResource(R.drawable.mission_img_badge_wordbook_30);
			break;
		case WORDBOOK_CELLOPHANE:
			badgeImg.setImageResource(R.drawable.mission_img_badge_wordbook_cellophane);
			break;
		case WORDBOOK_TEST:
			badgeImg.setImageResource(R.drawable.mission_img_badge_wordbook_test);
			break;
		}
		return v;
	}
	private void loadAnimation(){
		largeAni = AnimationUtils.loadAnimation(getActivity(), R.anim.mission_particle_scale_large);
		smallAni = AnimationUtils.loadAnimation(getActivity(), R.anim.mission_particle_scale_small);
	}
	private void startAnimation(){
		sparkleLeftUp.startAnimation(largeAni);
		sparkleLeftDown.startAnimation(smallAni);
		sparkleRightUp.startAnimation(largeAni);
		sparkleRightDown.startAnimation(smallAni);
	}
}
