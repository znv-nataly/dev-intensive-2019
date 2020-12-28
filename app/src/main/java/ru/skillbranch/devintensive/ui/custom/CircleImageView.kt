package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import ru.skillbranch.devintensive.R

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0

): ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_SIZE = 40
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_INITIALS = "??"
    }

    private var borderWidth: Float = dpToPx(DEFAULT_BORDER_WIDTH)
    private var borderColor: Int =
        DEFAULT_BORDER_COLOR
    private var initials: String =
        DEFAULT_INITIALS

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private val borderRect = Rect()
    private val density = context.resources.displayMetrics.density

    private lateinit var srcBm: Bitmap
    private var isAvatarMode = false


    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderWidth = typedArray.getDimension(
                R.styleable.CircleImageView_cv_borderWidth,
                dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor = typedArray.getColor(
                R.styleable.CircleImageView_cv_borderColor,
                DEFAULT_BORDER_COLOR
            )
            typedArray.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
        isAvatarMode = drawable != null
        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("M_AvatarImageViewMask", """
            onMeasure:
            width: ${MeasureSpec.toString(widthMeasureSpec)}
            height: ${MeasureSpec.toString(heightMeasureSpec)}
        """.trimIndent())

        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)

        Log.d("M_AvatarImageViewMask", "onMeasure after set size: $measuredWidth $measuredHeight")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("M_AvatarImageViewMask", "onSizeChanged")

        if (w == 0) return

        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }
        prepareShader(w, h)
    }

    override fun onDraw(canvas: Canvas?) { // NOT allocate, ONLY DRAw
        if (canvas == null) return

        if (drawable != null && isAvatarMode) {
            drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }

        // resize rect
        val half = (borderWidth / 2).toInt()
        borderRect.set(viewRect)
        borderRect.inset(half, half)
        canvas.drawOval( // обводка STROKE
            borderRect.left.toFloat(),
            borderRect.top.toFloat(),
            borderRect.right.toFloat(),
            borderRect.bottom.toFloat(),
            borderPaint)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAvatarMode = isAvatarMode
        savedState.borderWidth = borderWidth
        savedState.borderColor = borderColor
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state)
            isAvatarMode = state.isAvatarMode
            borderWidth = state.borderWidth
            borderColor = state.borderColor

            with(borderPaint) {
                color = borderColor
                strokeWidth = borderWidth
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setInitials(initials: String) {
        this.initials = initials
        if (!isAvatarMode) {
            invalidate()
        }
    }

    fun setBorderColor(borderColor: Int) {
        if (isColorResource(borderColor)) {
            borderPaint.color = context.getColor(borderColor)
        } else {
            borderPaint.color = borderColor
        }
        this.borderColor = borderPaint.color
        this.invalidate()
    }

    fun setBorderColor(hex: String) {
        borderPaint.color = Color.parseColor(hex)
        borderColor = borderPaint.color
        this.invalidate()
    }

    fun setBorderWidth(@Dimension width: Int) {
        borderWidth = dpToPx(width)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    @Dimension
    fun getBorderWidth(): Int {
        return Math.round(borderWidth/density)
    }

    fun getBorderColor(): Int = borderColor

    /**
     * Является ли числове значение идентификатором ресурса
     */
    private fun isColorResource(value: Int): Boolean {
        return try {
            ResourcesCompat.getColor(resources, value, null)
            true
        } catch (e: Resources.NotFoundException) {
            false
        }
    }

    private fun setup() {
        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        // prepare buffer this
        // Bitmap.Config.ARGB_8888 - rgb + alpha канал
        // Bitmap.Config.ALPHA_8 - только alpha канал
        if (w == 0 || drawable == null) return
        srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when(MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> dpToPx(DEFAULT_SIZE).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(
            viewRect.left.toFloat(),
            viewRect.top.toFloat(),
            viewRect.right.toFloat(),
            viewRect.bottom.toFloat(),
            avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialsPaint.color = context.getColor(R.color.color_accent)
        canvas.drawOval(
            viewRect.left.toFloat(),
            viewRect.top.toFloat(),
            viewRect.right.toFloat(),
            viewRect.bottom.toFloat(),
            initialsPaint)
        with(initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }
        // descent - нижняя граница шрифта
        // ascent - верхняя граница шрифта
        val offsetY = (initialsPaint.descent() + initialsPaint.ascent()) / 2
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initialsPaint)
    }

//    private fun initialsToColor(letters: String): Int {
//        val b = letters[0].toByte()
//        val len = bgColors.size
//        val d = b / len.toDouble()
//        val index = ((d - truncate(d)) * len).toInt()
//        return bgColors[index]
//    }

    private fun dpToPx(dp: Int): Float {
        return dp.toFloat() * density
    }

    private class SavedState: BaseSavedState, Parcelable {

        var isAvatarMode: Boolean = true
        var borderWidth: Float = 0f
        var borderColor: Int = 0

        constructor(superState: Parcelable?): super(superState)

        constructor(src: Parcel): super(src) {
            isAvatarMode = src.readInt() == 1
            borderWidth = src.readFloat()
            borderColor = src.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            // порядок записи и считывания имеет значение
            out.writeInt(if (isAvatarMode) 1 else 0)
            out.writeFloat(borderWidth)
            out.writeInt(borderColor)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR: Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState = SavedState(source)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)

        }

    }

}