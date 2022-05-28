package com.tommymarto.healthapp.utils

import androidx.fragment.app.Fragment
import com.tommymarto.healthapp.App
import com.tommymarto.healthapp.data.HealthConnectionManager


var Fragment.healthConnectManager: HealthConnectionManager
    get() = (requireActivity().application as App).healthConnectManager
    private set(value) {}