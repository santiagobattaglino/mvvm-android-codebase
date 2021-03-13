package com.santiagobattaglino.mvvm.codebase.presentation.ui

import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import com.santiagobattaglino.mvvm.codebase.util.GlideUrlCustomCacheKey
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.GlideApp
import kotlinx.android.synthetic.main.fragment_big_image_dialog.view.*
import kotlinx.android.synthetic.main.fragment_broadcast_ended_viewer.*
import kotlinx.android.synthetic.main.fragment_user_already_joined.*

open class FullScreenDialog : DialogFragment() {

    var callback: OnFullScreenDialogViewClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    interface OnFullScreenDialogViewClickListener {
        fun onFullScreenDialogViewClicked(view: View)
    }

    fun setOnFragmentViewClickListener(callbackFromActivity: OnFullScreenDialogViewClickListener) {
        callback = callbackFromActivity
    }
}

class BroadcastEndedViewerDialog : FullScreenDialog() {

    val mTag: String = javaClass.simpleName

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_broadcast_ended_viewer, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action.setOnClickListener {
            callback?.onFullScreenDialogViewClicked(it)
            dismiss()
        }
    }
}

class BigImageDialog(
    private val data: Media
) : FullScreenDialog() {

    val mTag: String = javaClass.simpleName

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_big_image_dialog, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data.url?.let {
            GlideApp.with(view.context)
                .load(GlideUrlCustomCacheKey(it))
                //.apply(RequestOptions.centerCropTransform())
                .into(view.photo)
        }

        action.setOnClickListener {
            //callback?.onFullScreenDialogViewClicked(it)
            dismiss()
        }
    }
}

class UserAlreadyJoinedDialog : FullScreenDialog() {

    val mTag: String = javaClass.simpleName

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_user_already_joined, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action_user_already_joined.setOnClickListener {
            callback?.onFullScreenDialogViewClicked(it)
            dismiss()
        }
    }
}