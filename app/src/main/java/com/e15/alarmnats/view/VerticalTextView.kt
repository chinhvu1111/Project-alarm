package com.e15.alarmnats.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.TextView

class VerticalTextView : TextView {

    var _width: Int = 0;

    var _height: Int = 0;

    var _bounds: Rect = Rect();

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        _height = measuredHeight

        //Like getMeasuredWidthAndState(),
        // but only returns the raw width component
        // (that is the result is masked by MEASURED_SIZE_MASK).
        _width = measuredWidth

        setMeasuredDimension(_width, _height)

    }

    //    The Canvas class holds the "draw" calls. To draw something,
//     you need 4 basic components:
//     1, A Bitmap to hold the pixels,
//     2, a Canvas to host the (draw calls) (writing into the bitmap),
//     3, (a drawing primitive) (e.g. Rect, Path, text, Bitmap),
//     4, a paint (to describe the (colors) and (styles) for the drawing).
    override fun onDraw(canvas: Canvas?) {

        canvas!!.save();

        //Preconcat the current matrix with the specified translation
        //
        //Params:
        //dx – The distance to translate in X`
        //dy – The distance to translate in Y
        canvas.translate(_width.toFloat(), _height.toFloat()*5/8)

        canvas.rotate((-90).toFloat());

        var paint: TextPaint = paint

        paint.setColor(textColors.defaultColor);

        //Return the (text) that TextView is displaying.
        // If setText(CharSequence) was called with an argument of
        // BufferType.SPANNABLE or BufferType.EDITABLE,
        // you can cast the (return value) from this method to (Spannable) or (Editable), respectively.
        var text:String= text()

        //Retrieve the (text boundary box) and store to bounds.
        // Return in bounds (allocated by the caller) the smallest rectangle that encloses
        // all of the characters, with an implied origin at (0,0). Note that styles are ignored even if
        paint.getTextBounds(text, 0, text.length,_bounds)

        //Draw the text, with origin at (x,y), using the specified paint.
        // The origin is interpreted based on the Align setting in the paint.
        //
        //Params:
        //text – The text to be drawn
        //x – The x-coordinate of the origin of the text being drawn
        //y – The y-coordinate of the (baseline) of the text being drawn
        //paint – The paint used for the text (e.g. color, size, style)

        //height()
        //the rectangle's height.
        // This does not check for a valid rectangle (i.e. top <= bottom) so the result may be negative.
        canvas.drawText(text, compoundPaddingLeft.toFloat(), ((_bounds.height()-_width)/2).toFloat(),paint)

        //This call balances (a previous call) to save(),
        // and is used to remove all modifications to the matrix/clip state since
        // the last save call. It is an error to call restore() more times than save() was called.
        canvas.restore()

    }

    fun text():String{
        return super.getText().toString()
    }

}