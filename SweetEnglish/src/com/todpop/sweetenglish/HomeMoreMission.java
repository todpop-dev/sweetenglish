package com.todpop.sweetenglish;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.todpop.api.TrackUsageTime;
import com.todpop.api.TypefaceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HomeMoreMission extends TypefaceActivity {
	private LinearLayout mainLayout;

	private PopupWindow detailPopup;
	private ImageView detailImg;
	private TextView title;
	private TextView content;
	
	TrackUsageTime tTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_more_mission);
		
		mainLayout = (LinearLayout)findViewById(R.id.activity_home_more_mission);

        View detailView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_mission_detail, null);
        ImageButton closeBtn = (ImageButton)detailView.findViewById(R.id.mission_detail_close);
        closeBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				detailPopup.dismiss();
			}
        });
        detailImg = (ImageView)detailView.findViewById(R.id.mission_detail_image);
        title = (TextView)detailView.findViewById(R.id.mission_detail_title);
        content = (TextView)detailView.findViewById(R.id.mission_detail_content);
        detailPopup = new PopupWindow(detailView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

		((SweetEnglish)getApplication()).getTracker(SweetEnglish.TrackerName.APP_TRACKER);
		tTime = TrackUsageTime.getInstance(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "P8GD9NXJB3FQ5GSJGVSX");
		FlurryAgent.logEvent("Home More Goal");
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		tTime.start();
	}
	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		tTime.stop();
	}
	
	// on click
	public void showDetail(View view){
		switch(view.getId()){
		case R.id.mission_linearlayout_newcomer:
			detailImg.setImageResource(R.drawable.mission_img_badge_newcomer);
			title.setText("신입생환영");
			content.setText("신입생에게 환영의 의미로 주는 뱃지.\n짭짤한영어를 쓰는 학생이라면 모두 받을 수 있어요.\n환영합니다. 열심히 공부하세요!");
			break;
		case R.id.mission_linearlayout_plan:
			detailImg.setImageResource(R.drawable.mission_img_badge_plan);
			title.setText("학습목표설정");
			content.setText("나의 학습목표 개수를 설정하여\n열심히 공부하는 학생에게 주는 뱃지.\n점차 목표 암기단어 수를 늘려나가세요!");
			break;
		case R.id.mission_linearlayout_invite:
			detailImg.setImageResource(R.drawable.mission_img_badge_invite);
			title.setText("친구초대");
			content.setText("친구들과 함께 공부하세요!\n친구를 10명 초대하면 받을 수 있는 뱃지.\n좋은 건 다 같이 나눠쓰세요!");
			break;
		case R.id.mission_linearlayout_alarm:
			detailImg.setImageResource(R.drawable.mission_img_badge_alarm);
			title.setText("학습알람설정");
			content.setText("학습할 시간을 설정하는\n학습알람기능을 사용해보세요.\n열정적으로 공부하는 당신에게 주는 뱃지.");
			break;
		case R.id.mission_linearlayout_attend_3:
			detailImg.setImageResource(R.drawable.mission_img_badge_attend_3);
			title.setText("3일 연속출석");
			content.setText("3일동안 꾸준히 공부하는\n학생에게 보상으로 주어지는 뱃지.\n더 열심히 해서 1주일을 채워보세요!");
			break;
		case R.id.mission_linearlayout_attend_7:
			detailImg.setImageResource(R.drawable.mission_img_badge_attend_7);
			title.setText("7일 연속출석");
			content.setText("7일동안 꾸준히 공부하는\n학생에게 보상으로 주어지는 뱃지.\n더 열심히 해서 10일을 채워보세요!");
			break;
		case R.id.mission_linearlayout_attend_10:
			detailImg.setImageResource(R.drawable.mission_img_badge_attend_10);
			title.setText("10일 연속출석");
			content.setText("10일동안 꾸준히 공부하는\n학생에게 보상으로 주어지는 뱃지.\n더 열심히 해서 1달을 채워보세요!");
			break;
		case R.id.mission_linearlayout_attend_1month:
			detailImg.setImageResource(R.drawable.mission_img_badge_attend_1month);
			title.setText("7일 연속출석");
			content.setText("1달동안 꾸준히 공부하는\n학생에게 보상으로 주어지는 뱃지.\n계속 꾸준히 공부해보세요!");
			break;
		case R.id.mission_linearlayout_clear_basic:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear_basic);
			title.setText("기초단어클리어");
			content.setText("기초카테고리 단어를 다 공부했다면\n받을 수 있는 뱃지.\n이제 초보수준을 벗어나셨네요!");
			break;
		case R.id.mission_linearlayout_clear_middle:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear_middle);
			title.setText("중학단어클리어");
			content.setText("중학카테고리 단어를 다 공부했다면\n받을 수 있는 뱃지.\n이제 어린이 영어신문은 읽을 수 있겠군요!");
			break;
		case R.id.mission_linearlayout_clear_high:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear_high);
			title.setText("수능단어클리어");
			content.setText("수능카테고리 단어를 다 공부했다면\n받을 수 있는 뱃지.\n이제 간단한 회화 정도는 가능하겠네요!");
			break;
		case R.id.mission_linearlayout_clear_toeic:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear_toeic);
			title.setText("토익단어클리어");
			content.setText("토익카테고리 단어를 다 공부했다면\n받을 수 있는 뱃지.\n이제 외국인이랑 친구해도 되겠어요!");
			break;
		case R.id.mission_linearlayout_clear2_basic:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear2_basic);
			title.setText("기초단어마스터");
			content.setText("기초카테고리의 모든 스테이지를\n별 2개 받았다면 주는 뱃지.\n정말 대단하시네요!");
			break;
		case R.id.mission_linearlayout_clear2_middle:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear2_middle);
			title.setText("중학단어마스터");
			content.setText("중학카테고리의 모든 스테이지를\n별 2개 받았다면 주는 뱃지.\n정말 대단하시네요!");
			break;
		case R.id.mission_linearlayout_clear2_high:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear2_high);
			title.setText("고등단어마스터");
			content.setText("고등카테고리의 모든 스테이지를\n별 2개 받았다면 주는 뱃지.\n정말 대단하시네요!");
			break;
		case R.id.mission_linearlayout_clear2_toeic:
			detailImg.setImageResource(R.drawable.mission_img_badge_clear2_toeic);
			title.setText("토익단어마스터");
			content.setText("토익카테고리의 모든 스테이지를\n별 2개 받았다면 주는 뱃지.\n정말 대단하시네요!");
			break;
		case R.id.mission_linearlayout_infi_80:
			detailImg.setImageResource(R.drawable.mission_img_badge_infinity_80);
			title.setText("무한도전80개");
			content.setText("무한도전 문제 중 80개 이상을 맞추면 축하의 의미로 주어지는 뱃지.\n다음번엔 다 맞춰보세요!");
			break;
		case R.id.mission_linearlayout_infi_100:
			detailImg.setImageResource(R.drawable.mission_img_badge_infinity_100);
			title.setText("무한도전100점");
			content.setText("정답으로 무한도전 모든 문제를 맞추면\n축하의 의미로 주어지는 뱃지.\n당신은 영어 천재이신가봐요!");
			break;
		case R.id.mission_linearlayout_goal_10:
			detailImg.setImageResource(R.drawable.mission_img_badge_studygoal_10);
			title.setText("주간목표 10개도달");
			content.setText("10개 이상의 학습목표량을 1주일 간\n잘 외우면 받을 수 있는 뱃지.\n영어공부 열심히 하시네요!");
			break;
		case R.id.mission_linearlayout_goal_30:
			detailImg.setImageResource(R.drawable.mission_img_badge_studygoal_30);
			title.setText("주간목표 30개도달");
			content.setText("30개 이상의 학습목표량을 1주일 간\n잘 외우면 받을 수 있는 뱃지.\n영어공부에 흥미가 생기셨나봐요!");
			break;
		case R.id.mission_linearlayout_goal_50:
			detailImg.setImageResource(R.drawable.mission_img_badge_studygoal_50);
			title.setText("주간목표 50개도달");
			content.setText("50개 이상의 학습목표량을 1주일 간\n잘 외우면 받을 수 있는 뱃지.\n와우, 정말 대단하시네요!");
			break;
		case R.id.mission_linearlayout_goal_1month:
			detailImg.setImageResource(R.drawable.mission_img_badge_studygoal_1month);
			title.setText("주간목표 1달연속도달");
			content.setText("자신의 학습목표량을 1달 간\n100% 도달하면 받을 수 있는 뱃지.\n역시 영어는 꾸준히 하는게 최고죠!");
			break;
		case R.id.mission_linearlayout_time_1:
			detailImg.setImageResource(R.drawable.mission_img_badge_time_1);
			title.setText("1시간 이상 학습");
			content.setText("차근차근 열심히 암기해서 총 누적시간이\n1시간이 넘었을 때 받는 뱃지.\n계속해서 힘내세요!");
			break;
		case R.id.mission_linearlayout_time_3:
			detailImg.setImageResource(R.drawable.mission_img_badge_time_3);
			title.setText("3시간 이상 학습");
			content.setText("차근차근 열심히 암기해서 총 누적시간이\n3시간이 넘었을 때 받는 뱃지.\n벌써 3시간입니다. 힘내세요!");
			break;
		case R.id.mission_linearlayout_time_5:
			detailImg.setImageResource(R.drawable.mission_img_badge_time_5);
			title.setText("5시간 이상 학습");
			content.setText("차근차근 열심히 암기해서 총 누적시간이\n5시간이 넘었을 때 받는 뱃지.\n열심히 공부하셨네요. 더욱 힘내세요!");
			break;
		case R.id.mission_linearlayout_time_earlybird:
			detailImg.setImageResource(R.drawable.mission_img_badge_time_earlybird);
			title.setText("얼리버드");
			content.setText("아침 6시-8시 사이에 30회 이상\n학습할 때 주는 뱃지.\n남들보다 이른 시간에 활동하는 당신, 멋져요!");
			break;
		case R.id.mission_linearlayout_time_owl:
			detailImg.setImageResource(R.drawable.mission_img_badge_time_owl);
			title.setText("올빼미족");
			content.setText("저녁 10시-12시 사이에 30회 이상\n학습할 때 주는 뱃지.\n밤에 공부가 더 잘되시는군요!");
			break;
		case R.id.mission_linearlayout_wordbook_3group:
			detailImg.setImageResource(R.drawable.mission_img_badge_wordbook_3group);
			title.setText("단어장에서 그룹만들기");
			content.setText("내단어장에서 그룹을\n3개 이상 만들면 받을 수 있는 뱃지.\n그룹으로 손쉽게 단어를 외워보세요");
			break;
		case R.id.mission_linearlayout_wordbook_30:
			detailImg.setImageResource(R.drawable.mission_img_badge_wordbook_30);
			title.setText("단어장에 단어추가하기");
			content.setText("내단어장에 단어를 30개 이상\n추가했을 때 받을 수 있는 뱃지.\n헷갈리고 모르는 단어를 한눈에!");
			break;
		case R.id.mission_linearlayout_wordbook_cellophane:
			detailImg.setImageResource(R.drawable.mission_img_badge_wordbook_cellophane);
			title.setText("셀로판지 이용하기");
			content.setText("내단어장에서 단어나 뜻을\n가려주는 셀로판지를 이용하면 주는 뱃지.\n손쉽게 활용해서 단어를 외워보세요!");
			break;
		case R.id.mission_linearlayout_wordbook_test:
			detailImg.setImageResource(R.drawable.mission_img_badge_wordbook_test);
			title.setText("단어장에서 테스트하기");
			content.setText("내단어장에 모아놓은 단어들로\n테스트를 보면 받을 수 있는 뱃지.\n지금 당장 테스트 해보세요!");
			break;
		case R.id.mission_linearlayout_screenlock_bgchange:
			detailImg.setImageResource(R.drawable.mission_img_badge_screenlock_bgchange);
			title.setText("잠금화면 배경설정");
			content.setText("잠금화면의 배경을 변경하면\n받을 수 있는 뱃지.\n입맛대로 바꾸는 잠금화면!");
			break;
		case R.id.mission_linearlayout_screenlock_category:
			detailImg.setImageResource(R.drawable.mission_img_badge_screenlock_category);
			title.setText("잠금화면 카테고리설정");
			content.setText("잠금화면에 노출되는 단어를\n카테고리로 설정하면 받을 수 있는 뱃지.\n나의 레벨에 맞는 단어로 설정하세요!");
			break;
		case R.id.mission_linearlayout_screenlock_word:
			detailImg.setImageResource(R.drawable.mission_img_badge_screenlock_word);
			title.setText("잠금화면 단어설정");
			content.setText("잠금화면에 노출되는 단어를\n내단어장으로 설정하면 받을 수 있는 뱃지.\n내가 외우고 싶은 단어들로 설정해보세요!");
			break;
		}
		detailPopup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	}
	public void onClickBox(View view){
		Intent intent = new Intent(this, HomeMoreMissionBox.class);
		startActivity(intent);
	}
	public void onClickBack(View view)
	{
		finish();
	}
}



