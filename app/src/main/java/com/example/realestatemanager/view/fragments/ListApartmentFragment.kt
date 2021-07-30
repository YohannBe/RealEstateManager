package com.example.realestatemanager.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.SwipeGesture
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import com.example.realestatemanager.view.myInterface.OnButtonClickedListener
import com.example.realestatemanager.view.myRecyclerView.RecyclerViewAdapterApartment
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListApartmentFragment : Fragment(), View.OnClickListener {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var adapter: RecyclerViewAdapterApartment
    private lateinit var mCallback: OnButtonClickedListener
    private lateinit var swipeGesture: SwipeGesture
    private lateinit var realEstateViewModel: RealEstateAgentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContext: Context
    private lateinit var communicatorInterface: CommunicatorInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        communicatorInterface = activity as CommunicatorInterface
        adapter =
            RecyclerViewAdapterApartment(
                context,
                communicatorInterface
            )
        mContext = context


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_appartment, container, false)

        initElements(view)
        return view
    }

    private fun initGesture(
        realEstateViewModel: RealEstateAgentViewModel?,
        context: Context
    ) {
        swipeGesture = object : SwipeGesture(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> realEstateViewModel?.deleteRealEstate(
                        adapter.getObject(
                            viewHolder.absoluteAdapterPosition
                        )
                    )
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initElements(view: View) {
        floatingButton = view.findViewById(R.id.floating_button_add_apartment)

        recyclerView = view.findViewById(R.id.recycler_view_list_apartment)
        realEstateViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideViewModelFactory(it)).get(
                RealEstateAgentViewModel::class.java
            )
        }!!
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        realEstateViewModel?.getAllApartment()?.observe(
            viewLifecycleOwner,
            Observer { list: List<RealEstate> ->
                adapter.updateApartmentList(list)
            })

        floatingButton.setOnClickListener(this)
        this.createCallbackToParentActivity()
        initGesture(realEstateViewModel, mContext)

    }


    companion object {

    }

    override fun onClick(v: View?) {
        mCallback.onButtonClicked(v)
    }

    private fun createCallbackToParentActivity() {
        try {
            mCallback = activity as OnButtonClickedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnButtonClickedListener")
        }
    }

}

