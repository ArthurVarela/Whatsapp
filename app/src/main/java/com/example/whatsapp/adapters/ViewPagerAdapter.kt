package com.example.whatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsapp.fragments.ChatListFragment
import com.example.whatsapp.fragments.ContactsFragment

class ViewPagerAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecyle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecyle) {

    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        when ( position ) {
             1 -> return ContactsFragment()
        }
        return ChatListFragment()
    }


}