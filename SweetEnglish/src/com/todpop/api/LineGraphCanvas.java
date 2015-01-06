package com.todpop.api;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class LineGraphCanvas extends View{
	private int width;
	private int height;
	
	private boolean drawYAxis;
	private boolean drawValuesText;
	
	private int axis;
	private int yDivider;
	private int axisText;
	private int valueText;
	private int valuePointFill;
	private int valuePointStroke;
	private int valueLine;
	
	private Paint.Style dotStyle;
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint dotText = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Path path;
	
	private int maxY;
	private int yDivideTimes;
	private ArrayList<String> xAxisString;
	private ArrayList<Integer> values;
	private float[] yPointsOfValues;
	private float[] xPointsOfValues;

	private float yDivideHeight;
	
	private float yAxisTextWidth;
	private float yAxisTextSize;
	private float yAxisTextLineSpace;
	private float lineWidth;		//axis and graph line and values dot radius
	private float xAxisTextSize;
	
	private float yAxisXcoordinate;
	private float graphHeight;
	
	private Typeface mTypeface;
	
    /**
     * @param c 			context
     * @param maxY 			max value of Y-axis
     * @param yDivideTimes 	value for draw Y-axis dividing lines
     * @param xAsixString	values for x-axis values
     * @param values		y values for each dot
     */
	public LineGraphCanvas(Context c, int maxY, int yDivideTimes, ArrayList<String> xAxisString, ArrayList<Integer> values){
		super(c);
		
		path = new Path();
		
		this.drawYAxis = true;
		this.drawValuesText = false;
		this.maxY = maxY;
		this.yDivideTimes = yDivideTimes;
		this.xAxisString = xAxisString;
		this.values = values;
		
		yPointsOfValues = new float[xAxisString.size()];
		xPointsOfValues = new float[xAxisString.size()];
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		yAxisTextWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, dm);
		yAxisTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
		yAxisTextLineSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm);
		lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
		xAxisTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, dm);
		
		axis = Color.rgb(147, 149, 152);
		yDivider = Color.rgb(209, 211, 212);
		axisText = Color.BLACK;
		valueText = Color.BLACK;
		valuePointFill = Color.rgb(37, 42, 58);
		valuePointStroke = Color.rgb(37, 42, 58);
		valueLine = Color.rgb(2, 181, 237);
		dotStyle = Paint.Style.FILL;
		
		yAxisXcoordinate = yAxisTextWidth + yAxisTextLineSpace;
		
		dotText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm));
		dotText.setColor(valueText);
		dotText.setTextAlign(Paint.Align.CENTER);
		
		mTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/roboto_regular_nanum_bold.ttf.mp3");
	}
	
    /**
     * @param c						context
     * @param drawYAxis				whether you want to draw y-axis or not
     * @param drawValuesText		whether you want to draw text of value above each dot
     * @param maxY	 				max value of Y-axis
     * @param yDivideTimes 			value for draw Y-axis dividing lines
     * @param xAsixString			values for x-axis values
     * @param values				y values for each dot
     * @param axisColor 			both x and y-axis lines color
     * @param yDividerColor 		y dividing line color
     * @param axisTextColor			x and y-axis text color
     * @param valueTextColor 		color of text that shows value above each dot
     * @param valuePointFillColor	each dot fill color
     * @param valuePointStrokeColor each dot stroke color
     * @param valueLineColor		color of line that connects two dots
     * @param valueDotStyle			each dot style (stroke of fill)
     */
	public LineGraphCanvas(Context c, boolean drawYAxis, boolean drawValuesText, int maxY, int yDivideTimes, ArrayList<String> xAxisString, ArrayList<Integer> values,
								int axisColor, int yDividerColor, int axisTextColor, int valueTextColor, int valuePointFillColor, int valuePointStrokeColor, int valueLineColor,
								Paint.Style valueDotStyle){
		super(c);
		
		path = new Path();

		this.drawYAxis = drawYAxis;
		this.drawValuesText = drawValuesText;
		this.maxY = maxY;
		this.yDivideTimes = yDivideTimes;
		this.xAxisString = xAxisString;
		this.values = values;
		
		yPointsOfValues = new float[xAxisString.size()];
		xPointsOfValues = new float[xAxisString.size()];
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		yAxisTextWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, dm);
		yAxisTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
		yAxisTextLineSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm);
		lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
		xAxisTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, dm);
		
		axis = axisColor;
		yDivider = yDividerColor;
		axisText = axisTextColor;
		valueText = valueTextColor;
		valuePointFill = valuePointFillColor;
		valuePointStroke = valuePointStrokeColor;
		valueLine = valueLineColor;
		dotStyle = valueDotStyle;
		
		if(drawYAxis)
			yAxisXcoordinate = yAxisTextWidth + yAxisTextLineSpace;
		else
			yAxisXcoordinate = 0;

		dotText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm));
		dotText.setColor(valueText);
		dotText.setTextAlign(Paint.Align.CENTER);
		
		mTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/roboto_regular_nanum_bold.ttf.mp3");
	}
	
	public void setMaxY(int maxY){
		this.maxY = maxY;
		this.invalidate();
	}
	public void setYDivideTimes(int yDivideTimes){
		this.yDivideTimes = yDivideTimes;
		this.invalidate();
	}
	public void setXAxisString(ArrayList<String> xAxisString){
		this.xAxisString = xAxisString;
		this.invalidate();
	}
	public void setValues(ArrayList<Integer> values){
		this.values = values;
		this.invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		width = this.getWidth();
		height = this.getHeight();
		
		graphHeight = height - xAxisTextSize - (lineWidth * 2);
		
		yDivideHeight = (graphHeight - (yAxisTextSize / 2)) / yDivideTimes;
		
		paint.setTypeface(mTypeface);

		//draw y-axis divide line
		paint.setColor(yDivider);
		paint.setStrokeWidth(1);
		for(int i = 0; i < yDivideTimes; i++){
			canvas.drawLine(yAxisXcoordinate, yDivideHeight * i + (yAxisTextSize / 2),
							width, yDivideHeight * i + (yAxisTextSize / 2), paint);
		}
		
		//setting for axis lines
		paint.setColor(axis);
		paint.setStrokeWidth(lineWidth);
		
		if(drawYAxis){
			//draw y-axis line
			canvas.drawLine(yAxisXcoordinate, 0, 
							yAxisXcoordinate, graphHeight, paint);
			
			//draw y-axis text
			paint.setTextSize(yAxisTextSize);
			paint.setColor(axisText);
			paint.setTextAlign(Paint.Align.RIGHT);
			
			int yAxisInterval = maxY / yDivideTimes;
			
			for(int i = 0; i < yDivideTimes; i++){
				int text = yAxisInterval * (yDivideTimes - i);
				canvas.drawText(String.valueOf(text), yAxisTextWidth, yDivideHeight * i + yAxisTextSize, paint);
			}
			canvas.drawText("00", yAxisTextWidth, graphHeight, paint);

			//setting for axis lines
			paint.setColor(axis);
		}
		
		//draw x-axis line
		canvas.drawLine(yAxisXcoordinate - (lineWidth / 2), graphHeight, 
						width, graphHeight, paint);
		
		//setting x-axis text
		paint.setTextSize(xAxisTextSize);
		paint.setTextAlign(Paint.Align.CENTER);		
		
		float xAxisDivide = (width - yAxisXcoordinate) / xAxisString.size(); 
		float xAxisDivideCenter = xAxisDivide / 2;
		
		//setting values point
		float yAxisDivide = (graphHeight - (yAxisTextSize / 2)) / 100;
		
		//setting for values line
		path.reset();
		float lastDot = (yAxisTextSize / 2) + ((100 - values.get(values.size() - 1)) * yAxisDivide);
		float firstDot = (yAxisTextSize / 2) + ((100 - values.get(0)) * yAxisDivide);
		
		//set values line start point
		path.moveTo(yAxisXcoordinate, firstDot + ((lastDot - firstDot) / 2));
		
		for(int i = 0; i < xAxisString.size(); i++){
			float x = yAxisXcoordinate + (xAxisDivide * i) + xAxisDivideCenter;
			float valuesY = (yAxisTextSize / 2) + ((100 - values.get(i)) * yAxisDivide);
			
			xPointsOfValues[i] = x;
			yPointsOfValues[i] = valuesY;
			
			//draw x-axis text
			canvas.drawText(xAxisString.get(i), x, height, paint);

			//set values line
			path.lineTo(x, valuesY);
			path.moveTo(x, valuesY);
			
			if(drawValuesText){
				//draw values text
				canvas.drawText(String.valueOf(values.get(i)), x, valuesY - (yAxisTextSize * 2), dotText);
			}
		}
		
		//set values line
		paint.setColor(valueLine);
        paint.setStyle(Paint.Style.STROKE);
        
        //set values line end point
        path.lineTo(width, lastDot + ((firstDot - lastDot) / 2));
		
		path.close();
		
		//draw values line
		canvas.drawPath(path, paint);

		//setting values dot
		paint.setColor(valuePointFill);
		paint.setStyle(Paint.Style.FILL);
		
		if(dotStyle == Paint.Style.STROKE){
			//draw values dot
			for(int i = 0; i < xAxisString.size(); i++){
				canvas.drawCircle(xPointsOfValues[i], yPointsOfValues[i], lineWidth * 2, paint);
			}
			
			paint.setStyle(dotStyle);
			paint.setColor(valuePointStroke);
			paint.setStrokeWidth(lineWidth);
			
			for(int i = 0; i < xAxisString.size(); i++){
				canvas.drawCircle(xPointsOfValues[i], yPointsOfValues[i], lineWidth * 2, paint);
			}
		}
		else{
			//draw values dot
			for(int i = 0; i < xAxisString.size(); i++){
				canvas.drawCircle(xPointsOfValues[i], yPointsOfValues[i], lineWidth, paint);
			}		
		}
	}
}
