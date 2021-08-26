package com.example.realestatemanager

import android.content.Context
import com.example.realestatemanager.utils.compareDateAfter
import com.example.realestatemanager.utils.compareDateBefore
import com.example.realestatemanager.utils.getLocationByAddress
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock


@RunWith(JUnit4::class)
class ToolTest {

    private val date1 = "21.01.1993"
    private val date2 = "26.08.2021"
    private val date3 = "12.02.2021"
    private val address = "25 rue jules david lilas 93260 france"
    private val context: Context = mock(Context::class.java)
    @Test
    fun compareDateBefore() {
        assertTrue(compareDateBefore(date3, date1))
        assertFalse(compareDateBefore(date1, date3))
    }

    @Test
    fun compareDateAfter(){
        assertFalse(compareDateAfter(date3, date1))
        assertTrue(compareDateAfter(date1, date3))
    }

    @Test
    fun locationThroughAddress(){
        val latLng = LatLng(0.0, 0.0)
        assertEquals(latLng, getLocationByAddress(context,address))
    }
}