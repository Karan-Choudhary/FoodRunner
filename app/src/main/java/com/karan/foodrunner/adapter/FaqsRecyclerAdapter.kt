package com.karan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.model.FaqItems

class FaqsRecyclerAdapter (context: Context, private val quesAnsList: ArrayList<FaqItems>) : RecyclerView.Adapter<FaqsRecyclerAdapter.FaqsViewHolder>(){

    class FaqsViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtQuestion:TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer:TextView = view.findViewById(R.id.txtAns)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faqs_single_row,parent,false)
        return FaqsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqsViewHolder, position: Int) {
        val quesAns = quesAnsList[position]
        holder.txtQuestion.text = quesAns.ques
        holder.txtAnswer.text = quesAns.ans
    }

    override fun getItemCount(): Int {
        return quesAnsList.size
    }
}