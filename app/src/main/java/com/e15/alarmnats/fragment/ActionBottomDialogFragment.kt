package com.e15.alarmnats.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.e15.alarmnats.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionBottomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.systemcategory ->{

                systemCategory.isVisible=false

                customCategory.isVisible=false

                lnSystemCategory.isVisible=false

                lnCustomCategory.isVisible=false

                tvSystemCategory.isVisible=false

                tvCustomCategory.isVisible=false

                tvSystemCategory.isClickable=false

                tvCustomCategory.isClickable=false

                choice =1;

                childFragmentManager.beginTransaction().replace(
                        R.id.frameListCategories,
                        TaskBaseOnCustomCategoryFragment()
                )
                        .commit()

            }

            R.id.customcategory ->{

                systemCategory.isVisible=false

                customCategory.isVisible=false

                lnSystemCategory.isVisible=false

                lnCustomCategory.isVisible=false

                tvSystemCategory.isVisible=false

                tvCustomCategory.isVisible=false

                tvSystemCategory.isClickable=false

                tvCustomCategory.isClickable=false

                choice =2;

                childFragmentManager.beginTransaction().replace(
                        R.id.frameListCategories,
                        TaskBaseOnCustomCategoryFragment()
                )
                        .commit()

            }

        }
    }

    lateinit var lnSystemCategory: LinearLayout

    lateinit var lnCustomCategory: LinearLayout

    lateinit var tvSystemCategory: TextView

    lateinit var tvCustomCategory: TextView

    lateinit var systemCategory: CardView

    lateinit var customCategory: CardView

    companion object {
        var choice=0

        lateinit var contexts: Context;

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_adding_classify_task, container, false)

        var sharedPreferences: SharedPreferences.Editor? =contexts.getSharedPreferences("choiceCategory", Context.MODE_PRIVATE).edit()

        lnSystemCategory=view.findViewById(R.id.lnSystemCategory)

        lnCustomCategory=view.findViewById(R.id.lnCustomCategory)

        systemCategory=view.findViewById<CardView>(R.id.systemcategory)

        customCategory=view.findViewById(R.id.customcategory)

        systemCategory.setOnClickListener(this)

        customCategory.setOnClickListener(this)

        tvSystemCategory=view.findViewById(R.id.tvsystemcategory);

        tvCustomCategory=view.findViewById(R.id.tvCustomCategory)

        TaskBaseOnCustomCategoryFragment.context=contexts

//        view.layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT

        return view;

    }

}
