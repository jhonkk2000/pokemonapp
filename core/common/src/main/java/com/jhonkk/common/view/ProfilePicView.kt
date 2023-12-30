package com.jhonkk.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.jhonkk.common.R
import com.jhonkk.common.extension.dp
import com.jhonkk.common.extension.gone
import com.jhonkk.common.extension.visible

class ProfilePicView: RelativeLayout {

    private var content: MaterialCardView? = null
    private var imageView: ImageView? = null
    private var textView: TextView? = null
    private var contentLoading: FrameLayout? = null
    var status: ProfilePicStatus = ProfilePicStatus.PLACEHOLDER

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.profile_pic_view, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.profile_pic_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        content = this.findViewById(R.id.content)
        imageView = this.findViewById(R.id.iv_profile)
        textView = this.findViewById(R.id.tv_name)
        contentLoading = this.findViewById(R.id.loading_content)
    }

    interface ProfilePicCallback {
        fun activePlaceholder()
        fun activeBitmap()
        fun activeText()
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            contentLoading?.visible()
        } else {
            contentLoading?.gone()
        }
    }

    private var callback: ProfilePicCallback? = null
    fun addCallbackStates(callback: ProfilePicCallback) {
        this.callback = callback
    }

    fun setTextColor(color: String) {
        textView?.setTextColor(Color.parseColor(color))
    }

    private var placeholder = R.drawable.ic_placeholder
    fun setPlaceholder(placeholder: Int) {
        this.placeholder = placeholder
    }

    fun setBackgroundColor(color: String) {
        val darkColor = color.replaceRange(1..2, "80")
        content?.setStrokeColor(ColorStateList.valueOf(Color.parseColor(darkColor)))
        content?.setCardBackgroundColor(Color.parseColor(color))
    }

    fun setupWithBitmap(bitmap: Bitmap) {
        imageView?.visible()
        textView?.gone()
        imageView?.setImageBitmap(bitmap)
        content?.strokeWidth = 0.dp
        callback?.activeBitmap()
    }

    fun setupWithText(text: String) {
        textView?.visible()
        imageView?.gone()
        val split = text.split(" ")
        val firstValue = split.getOrNull(0)?.firstOrNull()
        val secondValue = split.getOrNull(1)?.firstOrNull()
        val finalText = "${firstValue?: ""}${secondValue?:""}"
        textView?.text = finalText.uppercase()
        content?.strokeWidth = 3.dp
        callback?.activeText()
    }

    fun setupPlaceholder() {
        imageView?.visible()
        textView?.gone()
        imageView?.setImageResource(placeholder)
        content?.strokeWidth = 0.dp
        callback?.activePlaceholder()
    }
}

enum class ProfilePicStatus {
    IMAGE,
    TEXT,
    PLACEHOLDER
}