package com.tommymarto.healthapp.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.tommymarto.healthapp.App
import com.tommymarto.healthapp.MainActivity
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.data.HealthConnectAvailability
import com.tommymarto.healthapp.databinding.DayFragmentBinding
import com.tommymarto.healthapp.databinding.PermissionFragmentBinding
import com.tommymarto.healthapp.utils.healthConnectManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PermissionFragment : Fragment() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: PermissionFragmentBinding? = null
    private val binding get() = _binding!!

    private val permissionLauncher by lazy {
        registerForActivityResult(HealthDataRequestPermissions()) { granted ->
            if (granted.containsAll(this@PermissionFragment.healthConnectManager.PERMISSIONS)) {
                findNavController().navigate(R.id.action_PermissionFragment_to_ViewPagerHostFragment)
            } else {
                binding.textViewNoPermissions.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = PermissionFragmentBinding.inflate(inflater, container, false)

        // create permissionLauncher
        permissionLauncher

        binding.buttonInstallHealthConnect.setOnClickListener {
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.health_connect_download))))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            suspend fun ensurePermissions() {
                if(healthConnectManager.hasAllPermissions()) {
                    findNavController().navigate(R.id.action_PermissionFragment_to_ViewPagerHostFragment)
                } else {
                    permissionLauncher.launch(healthConnectManager.PERMISSIONS)
                }
            }

            when (healthConnectManager.availability) {
                HealthConnectAvailability.INSTALLED -> ensurePermissions()
                HealthConnectAvailability.NOT_INSTALLED -> binding.groupNotInstalled.visibility = View.VISIBLE
                HealthConnectAvailability.NOT_SUPPORTED -> binding.textViewNotSupported.visibility = View.VISIBLE
            }
        }
    }
}