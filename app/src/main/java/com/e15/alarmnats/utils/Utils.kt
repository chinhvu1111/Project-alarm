package com.e15.alarmnats.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TaskManagementActivity

class Utils{
    companion object {
        val DARK_THEME = 0
        val LIGHT_THEME = 1
        var months = arrayOf("Tháng 1 ngày", "Tháng 2 ngày", "Tháng 3 ngày", "Tháng 4 ngày", "Tháng 5 ngày", "Tháng 6 ngày", "Tháng 7 ngày", "Tháng 8 ngày",
                "Tháng 9 ngày", "Tháng 10 ngày", "Tháng 11 ngày", "Tháng 12 ngày")

        fun showToastMessage(str: String, context: Context) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }

        //hide keyboard from (any View)
        fun hideKeyboard(context: Context, view: View) {

            //Central system API to the overall input method framework (IMF) architecture,
            // which arbitrates interaction between applications and the current input method.
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }

        fun hideKeyboard(context: Context) {

            if(context is TaskManagementActivity){
                (context as TaskManagementActivity).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }

        }

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        //Updating themestyle via (SharedPreference file)
        fun updateThemeStyle(theme: Int, context: Context) {
            val pref = context.getSharedPreferences(context.resources.getString(R.string.user_pref), Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putInt(context.resources.getString(R.string.theme_style), theme)
            editor.commit()
        }

        fun getThemeStyle(context: Context): Int {
            val sharepref = context.getSharedPreferences(context.resources.getString(R.string.user_pref), Context.MODE_PRIVATE)
            return sharepref.getInt(context.resources.getString(R.string.theme_style), 1) //default is dark theme
        }

    }
}