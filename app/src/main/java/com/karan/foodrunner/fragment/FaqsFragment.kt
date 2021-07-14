package com.karan.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.FaqsRecyclerAdapter
import com.karan.foodrunner.model.FaqItems

class FaqsFragment : Fragment() {

    lateinit var recyclerFaq: RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FaqsRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)

        val quesAnsList = arrayListOf<FaqItems>(
            FaqItems(getString(R.string.question1),getString(R.string.answer1)),
            FaqItems(getString(R.string.question2),getString(R.string.answer2)),
            FaqItems(getString(R.string.question3),getString(R.string.answer3)),
            FaqItems(getString(R.string.question4),getString(R.string.answer4)),
            FaqItems(getString(R.string.question1),getString(R.string.answer1)),
            FaqItems(getString(R.string.question2),getString(R.string.answer2))
        )

        recyclerFaq = view.findViewById(R.id.recyclerFAQs)
        layoutManager = LinearLayoutManager(activity as Context)

        recyclerAdapter = FaqsRecyclerAdapter(activity as Context,quesAnsList)
        recyclerFaq.adapter = recyclerAdapter
        recyclerFaq.layoutManager = layoutManager

        return view
    }
}