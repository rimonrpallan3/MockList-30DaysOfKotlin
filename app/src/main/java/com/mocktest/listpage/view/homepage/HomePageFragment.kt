package com.mocktest.listpage.view.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.mocktest.listpage.utils.setRxOnClickListener
import com.mocktest.listpage.utils.setRxOnEditTextChangeAfter
import com.mocktest.listpage.view.BaseFragment
import com.mocktest.listpage.databinding.FragmentHomePageBinding
import com.mocktest.listpage.utils.Utils
import com.mocktest.listpage.viewmodel.homepage.HomePageViewModel
import com.mocktest.listpage.viewobservers.homePageViewObserver.HomePageViewObserver
import kotlinx.android.synthetic.main.appbar_layout.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomePageFragment : BaseFragment() {

    private var mViewObserver = HomePageViewObserver()
    private val mViewModel: HomePageViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomePageBinding.inflate(inflater, container, false)
        binding.data = mViewObserver
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadButtonFunctionality()
        loadPagedData()
    }

    override fun onResume() {
        super.onResume()
        loadPagedData()
    }


    /**
     * Function to load the Data from json file using pagination
     */

    private fun loadPagedData(){
        mViewModel.getPagedDataList().observe(viewLifecycleOwner, Observer { page ->
            mViewObserver.setAdapterDataList(page)
            mViewObserver.setDataVisibility(true)
            mViewObserver.setNoDataVisibility(false)
            tvHeading.text = mViewModel.getTitle()
        })
    }

    /**
     * Function to handle different button clicks
     */

    private fun loadButtonFunctionality(){

        ivSearchListIcon.setRxOnClickListener {
            llBackButton.visibility = View.GONE
            tvHeading.visibility = View.GONE
            etSearchListView.visibility = View.VISIBLE
            etSearchListView.requestFocus()
            ivSearchListIcon.visibility = View.GONE
            Utils.showKeyboard(activity!!)
        }

        etSearchListView.setRxOnEditTextChangeAfter {
            if (it.length >= 3){
                mViewModel.getSearchDataList(it, mViewObserver.getAdapterDataList()).observe(viewLifecycleOwner, Observer { page ->
                    if (page.isNotEmpty()){
                        mViewObserver.setAdapterDataList(page)
                        mViewObserver.setDataVisibility(true)
                        mViewObserver.setNoDataVisibility(false)
                    }else{
                        mViewObserver.setDataVisibility(false)
                        mViewObserver.setNoDataVisibility(true)
                    }
                })
            }
            if (it.isEmpty()){
                loadPagedData()
            }
        }

        etSearchListView.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= etSearchListView.right - etSearchListView.compoundDrawables.get(drawableRight).getBounds().width()) {
                    // your action here
                    Utils.hideKeyboard(activity!!)
                    ivSearchListIcon.visibility = View.VISIBLE
                    llBackButton.visibility = View.VISIBLE
                    tvHeading.visibility = View.VISIBLE
                    etSearchListView.visibility = View.GONE
                    etSearchListView.text?.clear()
                    loadPagedData()
                    return@OnTouchListener true
                }
            }
            false
        })

        llBackButton.setRxOnClickListener {
            activity!!.finish()
        }

    }


}