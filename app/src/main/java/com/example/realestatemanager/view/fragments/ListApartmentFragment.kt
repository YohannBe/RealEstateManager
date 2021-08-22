package com.example.realestatemanager.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.SwipeGesture
import com.example.realestatemanager.view.activities.MainActivity
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import com.example.realestatemanager.view.myInterface.OnButtonClickedListener
import com.example.realestatemanager.view.myRecyclerView.RecyclerViewAdapterApartment
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.mainActivity.MainActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListApartmentFragment : Fragment(), View.OnClickListener {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var adapter: RecyclerViewAdapterApartment
    private lateinit var mCallback: OnButtonClickedListener
    private lateinit var swipeGesture: SwipeGesture
    private lateinit var listApartmentViewModel: MainActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContext: Context
    private lateinit var communicatorInterface: CommunicatorInterface
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("creation fragment", "ici")
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
        mActivity = activity as MainActivity

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
        realEstateViewModel: MainActivityViewModel,
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
        listApartmentViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideMainActivityViewModelFactory(it)).get(
                MainActivityViewModel::class.java
            )
        }!!
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        listApartmentViewModel.getAllApartment().observe(
            viewLifecycleOwner,
            { list ->
                listApartmentViewModel.getFilter().observe(requireActivity(), {
                    adapter.updateApartmentList(listApartmentViewModel.updateListFilter(it, list))
                })
            })

        floatingButton.setOnClickListener(this)
        this.createCallbackToParentActivity()
        initGesture(listApartmentViewModel, mContext)

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

