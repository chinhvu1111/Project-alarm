package com.e15.alarmnats.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.e15.alarmnats.R
import com.e15.alarmnats.utils.Utils

class ColorCircle: View {
    var radius: Float = 0.toFloat()
    val conerRadius: Float = 0.toFloat()

    //The Paint class holds the style and
    // color information about how to (draw geometries), text and bitmaps.
    private var paint: Paint? = null
    private var conerPaint: Paint? = null
    private var color: Int = 0
    private var center: Int = 0
    internal var context: Context
    internal var isConer = false
    private val STROKE_WIDTH = Utils.dpToPx(3)

    constructor(context: Context) : super(context) {
        this.context = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        this.context = context
        init()
    }

    fun setColor(color: Int) {
        paint!!.color = color
        //Cause an invalidate to happen on a subsequent cycle through the event loop.
        // Use this to invalidate the View from a non-UI thread.
        //This method can be invoked from outside of the UI thread only when this View is attached to a window.
        postInvalidate()
    }

    fun setConer(coner: Boolean) {
        isConer = coner
        postInvalidate()
    }

    fun setColorAndConer(color: Int, isConer: Boolean) {
        paint!!.color = color
        this.isConer = isConer
        postInvalidate()
    }

    fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.FILL
        conerPaint = Paint(paint)

        val array = context.obtainStyledAttributes(R.styleable.ColorCircle)
        color = array.getColor(R.styleable.ColorCircle_color, resources.getColor(R.color.color_circle_gray))

        paint!!.color = color
        conerPaint!!.style = Paint.Style.STROKE
        conerPaint!!.strokeWidth = STROKE_WIDTH.toFloat()
        conerPaint!!.color = Color.WHITE
    }


    override fun onDraw(canvas: Canvas) {
        if (isConer) {
            radius = (center - STROKE_WIDTH).toFloat()
            canvas.drawCircle(center.toFloat(), center.toFloat(), radius, paint!!)
            canvas.drawCircle(center.toFloat(), center.toFloat(), radius, conerPaint!!)
        } else {
            radius = center.toFloat()
            canvas.drawCircle(center.toFloat(), center.toFloat(), radius, paint!!)
        }

        //        radius = center ;
        //        canvas.drawCircle(center,center,radius,paint);
        //        if(isConer){
        //            canvas.drawCircle(center,center,radius,conerPaint);
        //        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        center = measuredHeight / 2
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}