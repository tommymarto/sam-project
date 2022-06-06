package com.tommymarto.healthapp.utils

import androidx.fragment.app.Fragment
import com.tommymarto.healthapp.App
import com.tommymarto.healthapp.data.HealthConnectionManager
import com.tommymarto.healthapp.data.db.DBClient

var Fragment.healthConnectManager: HealthConnectionManager
    get() = (requireActivity().application as App).healthConnectManager
    private set(_) {}

var Fragment.dbClient: DBClient
    get() = (requireActivity().application as App).dbClient
    private set(_) {}