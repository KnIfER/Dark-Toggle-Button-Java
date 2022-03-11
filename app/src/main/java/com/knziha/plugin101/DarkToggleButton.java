package com.knziha.plugin101;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

public class DarkToggleButton extends View implements DarkToggleInterface {
	/** 画布旋转角度 */
	private final static int rotation=0;
	/** 月蚀面X轴偏移比例 */
	private final static int maskCxRatio=1;
	/** 月蚀面Y轴偏移比例 */
	private final static int maskCyRatio=2;
	/** 月蚀面半径比例 */
	private final static int maskRadiusRatio=3;
	/** 日月面半径比例 */
	private final static int circleRadiusRatio=4;
	/** 动画属性数量。上面5个个变量 + 太阳8方圆点的画布缩放数值 */
	private final static int animatePropsCnt = 5 + 8;
	/** 太阳周围旋绕8方圆点，代表光芒。 */
	private final static int SurroundCircleNum = 8;
	/** 动画属性数组，存储当前状态的变量，等于|切换前|与|切换后|变量之间的插值。 */
	private final float[] values = new float[animatePropsCnt];
	/** 动画属性数组，存储切换后的变量。 */
	private final float[] targetValues = new float[animatePropsCnt];
	/** 动画属性数组，存储切换前的变量。 */
	private final float[] lastValues = new float[animatePropsCnt];
	/** 表示正在切换为或已切换至太阳状态。 */
	private boolean stateIsSun;
	
	/** 日月面最大直径（包围盒的宽度和高度） */
	private int size;
	private Paint paint;
	//Paint mskPaint;
	private Path mskPath = new Path();
	private float density;
	
	/** 弹簧插值器。
	 * https://blog.csdn.net/l474297694/article/details/79916864
	 * http://inloop.github.io/interpolator/
	 * pow(2, -10 * x) * sin((x - factor / 4) * (2 * PI) / factor) + 1 */
	public static class SpringInterpolator implements Interpolator {
		private float factor;
		public SpringInterpolator(float factor) {
			this.factor = factor;
		}
		@Override
		public float getInterpolation(float input) {
			return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
		}
	}
	
	/** 插值器 */
	Interpolator interpolator = new SpringInterpolator(0.9f);
	float progress;
	boolean animating;
	long animatorTime;
	long duration = 1500;
	
	public DarkToggleButton(Context context) {
		this(context, null);
	}
	
	public DarkToggleButton(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.YELLOW);
		paint.setFilterBitmap(true);
		
		// 若用 mskPaint 清除绘制的月面显示出月牙，则没有 clipPath 锯齿缺点，但是需要给视图关闭硬件加速。
		//mskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//mskPaint.setColor(Color.BLACK);
		//mskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); // need to use software layer type.
		
		// 初始化数值
		stateIsSun = true;
		values[rotation] = 180;
		values[maskCxRatio] = 1;
		values[maskCyRatio] = 0;
		values[maskRadiusRatio] = 0.125f;
		values[circleRadiusRatio] = 0.2f;
		for (int i = 0; i < 8; i++) {
			values[5+i] = 1;
		}
		
		density = context.getResources().getDisplayMetrics().density;
	}
	
	/** 切换日月状态，并开始播放动画。 */
	public void toggle() {
		stateIsSun = !stateIsSun;
		// 根据日月状态，设置插值终点。
		if (stateIsSun) {
			targetValues[rotation] = 180;
			targetValues[maskCxRatio] = 1;
			targetValues[maskCyRatio] = 0;
			targetValues[maskRadiusRatio] = 0.125f;
			targetValues[circleRadiusRatio] = 0.2f;
		} else {
			targetValues[rotation]        =  45;
			targetValues[maskCxRatio]     =  0;
			targetValues[maskCyRatio]     =  -0.32f;
			targetValues[maskRadiusRatio] =  0.35f;
			targetValues[circleRadiusRatio] = 0.35f;
		}
		float val = stateIsSun?1:0;
		for (int i = 0; i < 8; i++) {
			targetValues[5+i] = val;
		}
		// 拷贝当前状态数值，作为插值起点。
		System.arraycopy(values, 0, lastValues, 0, animatePropsCnt);
		animating=true;
		animatorTime = System.currentTimeMillis();
		invalidate();
	}
	
	PaintFlagsDrawFilter mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		size = (int) (density * 200);
		int R = size/2;
		if(R==0) R=getWidth()/2;
		int CX = (int) (getWidth()*0.5f);
		int CY = (int) (getHeight()*0.5f);
		canvas.save();
		canvas.rotate(values[rotation], CX, CY); //旋转画布
		canvas.setDrawFilter( mSetfil );
		
		canvas.save();
		mskPath.reset();
		mskPath.addCircle(CX+R*values[maskCxRatio], CY+R*values[maskCyRatio], R*values[maskRadiusRatio], Path.Direction.CCW);
		canvas.clipPath(mskPath, Region.Op.DIFFERENCE); // 月蚀，遮挡月面显示出月牙。clipPath 有锯齿。
		// 绘制日月圆面
		canvas.drawCircle(CX, CY, R*values[circleRadiusRatio], paint);
		canvas.restore();
		// 绘制月蚀，遮挡月面显示出月牙。使用 PorterDuff.Mode.CLEAR 需关闭硬件加速。
		//canvas.drawCircle(CX+R*values[maskCxRatio], CY+R*values[maskCyRatio], R*values[maskRadiusRatio], paint);
		
		if (animating) {
			handleAnimation();
		}
		
		// 绘制太阳的8方圆点
		float SurroundCircleScale;
		for (int i = 0; i < SurroundCircleNum; i++) {
			SurroundCircleScale = values[i+5];
			if(SurroundCircleScale<0.2) break;
			canvas.save();  // 8方太阳圆点，次第缩放画布
			canvas.scale(SurroundCircleScale, SurroundCircleScale, CX, CY);
			float radians = (float) (Math.PI / 2 - i * 2 * Math.PI / SurroundCircleNum);
			float d = R / 3;
			float cx = (float) (CX + d * Math.cos(radians));
			float cy = (float) (CY - d * Math.sin(radians));
			canvas.drawCircle(cx, cy, R*0.05f, paint);
			canvas.restore();
		}
		canvas.restore();
	}
	
	/** 处理动画属性 */
	private void handleAnimation() {
		long now = System.currentTimeMillis();
		long elapsed = now - animatorTime;
		if(elapsed>=duration) animating = false;
		progress = Math.min(1, elapsed*1.f/duration);
		float interp = interpolator.getInterpolation(progress);
		boolean toSun = stateIsSun;
		// 5个动画属性的interp值是一致的，若正在切换为月亮，则8方圆点也使用一致的interp值。
		for (int i = 0; i < 5+(toSun?0:8); i++) {
			// 计算插值。
			values[i] =  (1-interp)*lastValues[i] + interp*targetValues[i];
		}
		if (toSun) {
			int idx;
			// 8方太阳圆点，用delayUnit次第缩放画布，分别计算interp值。
			float delayUnit = Math.max(5, Math.min(50, 1500f * 0.067f + 55)); // SpringForce.STIFFNESS_MEDIUM 等于 1500f
			for (int i = 0; i < 8; i++) {
				idx = 5+i;
				// 计算插值。
				interp = interpolator.getInterpolation(Math.max(0, Math.min(1, (now-animatorTime-delayUnit*i)*1.f/duration)));
				values[idx] =  Math.min(1, (1-interp)*lastValues[idx] + interp*targetValues[idx]);
			}
		}
		if (animating) {
			// onDraw中触发重绘。应该比直接invalidate更节省性能，类比于js的requestAnimationFrame和setTimeout。
			postInvalidateOnAnimation();
		}
	}
}
