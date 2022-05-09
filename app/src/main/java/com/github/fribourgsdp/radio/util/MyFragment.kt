package com.github.fribourgsdp.radio.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.fribourgsdp.radio.R

abstract class MyFragment(private val layoutID : Int) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutID, container, false)
    }
    companion object{
        inline fun <reified T : MyFragment> beginTransaction(supportFragmentManager : FragmentManager, bundle: Bundle){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, T::class.java, bundle)
                .commit()
        }
    }
}