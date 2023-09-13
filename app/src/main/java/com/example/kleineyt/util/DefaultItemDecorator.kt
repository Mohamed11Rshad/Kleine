package com.example.kleineyt.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DefaultItemDecorator(private val horizontalSpacing : Int , private val verticalSpacing : Int) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //add horizontal spacing
        outRect.right = horizontalSpacing
        outRect.left = horizontalSpacing
        //add vertical spacing
        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing

    }

}