package com.todpop.api;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class MemorizedRateAllCanvas extends View{
	private float outterRadius;
	private float outterStroke;
	private float progressRadius;
	private float progressStroke;
	private float innerRadius;
	private float angledLineLength;
	private float straightLineLength;
	private float koreanTextSize;
	private float numberTextSize;
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int total;
	private int memorized;
	
	private int centerWidth;
	private int centerHeight;
	
	RectF progress;
	
	Typeface mTypeface;
	
	public MemorizedRateAllCanvas(Context c, int inTotal, int inCorrect){
		super(c);

		mTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/roboto_regular_nanum_bold.ttf.mp3");
		
		total = inTotal;
		memorized = inCorrect;
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		outterRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, dm);
		outterStroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
		progressRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 67, dm);
		progressStroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, dm);
		innerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, dm);
		angledLineLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, dm);
		straightLineLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21, dm);
		koreanTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, dm);
		numberTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, dm);
	}
	
	public void setTotalAndCorrect(int inTotal, int inCorrect){
		total = inTotal;
		memorized = inCorrect;
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		centerWidth = this.getWidth() / 2;
		centerHeight = this.getHeight() / 2;

		progress = new RectF(centerWidth - progressRadius, centerHeight - progressRadius, centerWidth + progressRadius, centerHeight + progressRadius);

		//outter gray circle
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(outterStroke);
		paint.setARGB(255, 236, 236, 236);
		
		canvas.drawCircle(centerWidth, centerHeight, outterRadius, paint);
		
		//circular progress 
		paint.setStrokeWidth(progressStroke);
		paint.setARGB(255, 1, 180, 237);
        paint.setStrokeCap(Paint.Cap.BUTT);
        float degree = 360 * (float)memorized / total;
        
		canvas.drawArc(progress, -90, degree, false, paint);

		//inner dark circle
		paint.setStyle(Paint.Style.FILL);
		paint.setARGB(255, 37, 42, 58);
		
		canvas.drawCircle(centerWidth, centerHeight, innerRadius, paint);

		//target line
		paint.setARGB(255, 1, 180, 237);
		paint.setStrokeWidth(3);
		float targetLineX = (float)(centerWidth + outterRadius * Math.cos(40));
		float targetLineY = (float)(centerHeight - outterRadius * Math.sin(40));
		
		canvas.drawLine(targetLineX, targetLineY, targetLineX - angledLineLength, targetLineY - angledLineLength, paint);
		canvas.drawLine(targetLineX - angledLineLength, targetLineY - angledLineLength, targetLineX - angledLineLength - straightLineLength, targetLineY - angledLineLength, paint);
		
		//memorized line
		float memorizedLineDegree = degree / 2;
		if(memorizedLineDegree > 135)
			memorizedLineDegree = 135;

		float memorizedLineX;
		float memorizedLineY;
		float toX;
		float toY;
		float toXEnd;
		float extra = progressRadius + progressStroke;

		if(memorizedLineDegree == 90){
			memorizedLineX = centerWidth + progressRadius;
			memorizedLineY = centerHeight;
			toX = centerWidth + extra;// + progressRadius
			toY = centerHeight;
		}
		else if(memorizedLineDegree < 90){
			double cos = Math.cos(Math.toRadians(memorizedLineDegree));
			double sin = Math.sin(Math.toRadians(memorizedLineDegree));
			
			memorizedLineX = (float)(centerWidth + progressRadius * sin);
			memorizedLineY = (float)(centerHeight - progressRadius * cos);
			toX = (float)(centerWidth + extra * sin);
			toY = (float)(centerHeight - extra * cos);
		}
		else{
			double cos = Math.cos(Math.toRadians(memorizedLineDegree));
			double sin = Math.sin(Math.toRadians(memorizedLineDegree));
			
			memorizedLineX = (float)(centerWidth + progressRadius * sin);
			memorizedLineY = (float)(centerHeight - progressRadius * cos);
			toX = (float)(centerWidth + extra * sin);
			toY = (float)(centerHeight - extra * cos);
		}
		
		if(toX > (centerWidth * 2) - (koreanTextSize * 5)){
			toX = (centerWidth * 2) - (koreanTextSize * 5);
			toXEnd = toX;
		}
		else{
			toXEnd = toX + numberTextSize;
			if(toXEnd > (centerWidth * 2) - (koreanTextSize * 5)){
				toXEnd = (centerWidth * 2) - (koreanTextSize * 5);
			}
			canvas.drawLine(toX, toY, toXEnd, toY, paint);
		}
		canvas.drawLine(memorizedLineX, memorizedLineY, toX, toY, paint);
				
		//total text word
		paint.setTextAlign(Paint.Align.RIGHT);
		paint.setTextSize(koreanTextSize);
		paint.setARGB(255, 37, 42, 58);
		paint.setTypeface(mTypeface);

		canvas.drawText("학습량", targetLineX - (angledLineLength + straightLineLength) - 3, targetLineY - (2 * outterStroke), paint);
		
		//memorized text word
		paint.setTextAlign(Paint.Align.LEFT);
		
		canvas.drawText("암기 단어", toXEnd + 3, toY + (2 * outterStroke), paint);
		
		//total text number
		paint.setTextAlign(Paint.Align.RIGHT);
		paint.setTextSize(numberTextSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		canvas.drawText(String.valueOf(total), targetLineX - (angledLineLength + straightLineLength) - 5, targetLineY + numberTextSize, paint);
		
		//memorized text number
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setARGB(255, 1, 180, 237);
		
		canvas.drawText(String.valueOf(memorized), toXEnd + 5, toY + numberTextSize + (2 * outterStroke), paint);

	}
}
