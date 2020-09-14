package com.exazero.exainfinitewheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *   Created by jazcorra96 on 9/9/2020
 */
class ExaInfiniteWheel @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {

    private var tickCount = 10
    private var tickColor = Color.WHITE
    private var tickMaxHeight = 0f
    private var tickWidth = 0f
    private var indicatorColor = Color.WHITE
    private var indicatorHeight = 0f
    private var isInfinite = true

    private var halfWidth = 0
    private var halfHeight = 0
    private var tickSpace = 0f

    private var position = 0
    private var positionOffset = 0f
    private var accumulatedScroll = 0f

    private val paintTick = Paint()
    private val paintIndicator = Paint()


    private val mGestureDetector: GestureDetector
    private val mScroller: Scroller

    private var mListener: ExaInfiniteWheelEvent? = null

    private val flingAnimator = ValueAnimator.ofInt(0, 1000)

    fun setOnDragListener(l: ExaInfiniteWheelEvent?){
        mListener = l
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExaInfiniteWheel)
        tickCount = a.getInt(R.styleable.ExaInfiniteWheel_exa_tick_count, resources.getInteger(R.integer.exa_tick_count))
        tickColor = resources.getColor(a.getResourceId(R.styleable.ExaInfiniteWheel_exa_tick_color, R.color.exa_tick_color), context.theme)
        tickMaxHeight = a.getDimension(R.styleable.ExaInfiniteWheel_exa_tick_max_height, resources.getDimension(R.dimen.exa_tick_max_height))
        tickWidth = a.getDimension(R.styleable.ExaInfiniteWheel_exa_tick_width, resources.getDimension(R.dimen.exa_tick_width))
        indicatorColor = resources.getColor(a.getResourceId(R.styleable.ExaInfiniteWheel_exa_indicator_color, R.color.exa_indicator_color), context.theme)
        indicatorHeight = a.getDimension(R.styleable.ExaInfiniteWheel_exa_indicator_height, resources.getDimension(R.dimen.exa_indicator_height))
        isInfinite = a.getBoolean(R.styleable.ExaInfiniteWheel_exa_infinite, resources.getBoolean(R.bool.exa_infinite))
        a.recycle()
        mGestureDetector = GestureDetector(context, this)
        mGestureDetector.setIsLongpressEnabled(false)

        mScroller = Scroller(context)
        updateView()
        /*
        flingAnimator.addUpdateListener {
            Log.d(TAG, "Scroller = ${mScroller.currX.toFloat()}")
            if(it.animatedValue == 1 || !mScroller.computeScrollOffset()){
                if(!mScroller.isFinished){
                    mScroller.abortAnimation()
                    flingAnimator.end()
                    computeCurrentPosition()
                    invalidate()
                }
            }
            else{
                accumulatedScroll = mScroller.currX.toFloat()
                computePositionOffset()
                invalidate()
            }

        }
         */
    }

    private fun updateView(){
        paintTick.apply {
            color = tickColor
            strokeWidth = tickWidth
        }
        paintIndicator.apply {
            color = indicatorColor
            strokeWidth = tickWidth

        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val resultW = MeasureSpec.getSize(widthMeasureSpec)
        val resultH = MeasureSpec.getSize(heightMeasureSpec)
        halfHeight = resultH/2
        halfWidth = resultW/2
        tickSpace = resultW/(tickCount+1)/1f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for(i in 0..tickCount){
            (i * tickSpace - positionOffset).let {tickX ->
                computeTickHeight(tickX).let {tickH ->
                    canvas?.drawLine(tickX, halfHeight - tickH/2f, tickX, halfHeight + tickH/2f, paintTick)
                }
            }
        }
        canvas?.drawLine(halfWidth/1f, halfHeight - indicatorHeight/2f, halfWidth/1f, halfHeight + indicatorHeight/2f, paintIndicator)
        //canvas?.drawLine(0f, 0f, width/1f, height/1f, paintIndicator)
    }

    private fun computeTickHeight(posX: Float): Float{
        return (halfWidth - abs(posX - halfWidth)) * tickMaxHeight / halfWidth
    }

    private fun computePositionOffset(){
        positionOffset = accumulatedScroll % tickSpace
    }

    private fun computeCurrentPosition(){
        position = (accumulatedScroll / tickSpace).roundToInt()
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return if(mGestureDetector.onTouchEvent(event)){
            true
        } else{

            when(event?.actionMasked){
                MotionEvent.ACTION_UP -> {
                    snapToClosestTick()
                    mListener?.onDragEnd(position)
                    true
                }
                else ->{
                    super.onTouchEvent(event)
                }
            }

        }
    }



    fun snapToClosestTick(){
        positionOffset = 0f
        computeCurrentPosition()
        accumulatedScroll = 0f
        invalidate()
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        Log.d(TAG, "onDown: e1 = $p0")
        accumulatedScroll = 0f
        computeCurrentPosition()
        mListener?.onDragStart(position)
        return true
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {

        snapToClosestTick()
        //mScroller.fling()
        //mScroller.startScroll(positionOffset.toInt(), 0,0,0, 0)
        //mScroller.fling(0, 0, p2.toInt(), 0, -200000, -200000, 0,0)
        //mScroller.finalX = 2000000
        //flingAnimator.duration = mScroller.duration.toLong()
        //flingAnimator.start()
        //Log.d(TAG, "onFling: vx = $p2 \t vy = $p3 \t duration = ${mScroller.duration.toLong()}")
        return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        Log.d(TAG, "onScroll: dx = $p2 \t dy = $p3")
        accumulatedScroll += p2
        computePositionOffset()
        computeCurrentPosition()
        mListener?.onDragMove(position)
        invalidate()
        return true
    }

    override fun onLongPress(p0: MotionEvent?) {}
    override fun onShowPress(p0: MotionEvent?) {}
    override fun onSingleTapUp(p0: MotionEvent?): Boolean = true




    companion object{
        private val TAG = this::class.java.simpleName
    }
}