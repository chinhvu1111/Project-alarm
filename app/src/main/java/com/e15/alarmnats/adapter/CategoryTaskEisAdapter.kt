package com.e15.alarmnats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.R
import com.e15.alarmnats.fragment.TaskBaseOnCustomCategoryFragment
import com.e15.alarmnats.viewholder.ViewHolderListCategoriesEIS

class CategoryTaskEisAdapter: RecyclerView.Adapter<ViewHolderListCategoriesEIS>() {

    lateinit var listCategory:ArrayList<Category>

    lateinit var contexts: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderListCategoriesEIS {

        var view= LayoutInflater.from(contexts).inflate(R.layout.item_category_eise_adding, parent, false)

        var viewHolder= ViewHolderListCategoriesEIS(view);

        return viewHolder

    }

    override fun getItemCount(): Int {

        return listCategory.size

    }

    override fun onBindViewHolder(holder: ViewHolderListCategoriesEIS, position: Int) {

        holder.tvId.setText(listCategory.get(position).hasIdCategory)

        holder.tvcategory.setText(listCategory.get(position).title)

        holder.tvId.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View?) {

                TaskBaseOnCustomCategoryFragment.recyclelistCategories.isVisible=false

                TaskBaseOnCustomCategoryFragment.recyclelistCategories.isClickable=false

                var editor=contexts.getSharedPreferences("choiceCategory", Context.MODE_PRIVATE).edit()

                editor.putString("choice",listCategory.get(position).title)

                editor.apply()

                TaskBaseOnCustomCategoryFragment.AsyncTaskActivity().execute()

            }

        })

        //Display category to create task (base on category)
        holder.tvcategory.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                TaskBaseOnCustomCategoryFragment.recyclelistCategories.isVisible=false

                TaskBaseOnCustomCategoryFragment.recyclelistCategories.isClickable=false

                var editor=contexts.getSharedPreferences("choiceCategory", Context.MODE_PRIVATE).edit()

                editor.putString("choice",listCategory.get(position).title)

                editor.apply()

                TaskBaseOnCustomCategoryFragment.AsyncTaskActivity().execute()

            }
        })

    }

}