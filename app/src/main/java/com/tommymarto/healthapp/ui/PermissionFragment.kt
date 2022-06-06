package com.tommymarto.healthapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tommymarto.healthapp.ActivityForegroundService
import com.tommymarto.healthapp.App
import com.tommymarto.healthapp.R
import com.tommymarto.healthapp.data.HealthConnectAvailability
import com.tommymarto.healthapp.data.HealthConnectionManager
import com.tommymarto.healthapp.databinding.PermissionFragmentBinding
import com.tommymarto.healthapp.utils.healthConnectManager
import com.tommymarto.healthapp.utils.setApplication
import kotlinx.coroutines.launch

class PermissionFragment : Fragment() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: PermissionFragmentBinding? = null
    private val binding get() = _binding!!

    private fun startNotificationService() {
        val activity = requireActivity()
        setApplication(activity.application as App)
        activity.startService(Intent(activity, ActivityForegroundService::class.java))
    }

    private val permissionLauncher by lazy {
        // request permission
        registerForActivityResult(HealthDataRequestPermissions()) { granted ->
            if (granted.containsAll(HealthConnectionManager.PERMISSIONS)) {
                // start service and navigate only if all permissions are granted
                startNotificationService()
                findNavController().navigate(R.id.action_PermissionFragment_to_ViewPagerHostFragment)
            } else {
                binding.textViewNoPermissions.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PermissionFragmentBinding.inflate(inflater, container, false)

        // create permissionLauncher
        permissionLauncher

        //
        binding.buttonInstallHealthConnect.setOnClickListener {
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.health_connect_download))))
        }

        return binding.root
    }

    // don't ask for permissions twice
    private var alreadyAskedForPermissions = false
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            suspend fun ensurePermissions() {
                if(alreadyAskedForPermissions) {
                    binding.textViewNoPermissions.visibility = View.VISIBLE
                    return
                }

                alreadyAskedForPermissions = true
                if(healthConnectManager.hasAllPermissions()) {
                    // start service and navigate only if all permissions are granted
                    startNotificationService()
                    findNavController().navigate(R.id.action_PermissionFragment_to_ViewPagerHostFragment)
                } else {
                    permissionLauncher.launch(HealthConnectionManager.PERMISSIONS)
                }
            }

            listOf(
                binding.groupNotInstalled,
                binding.textViewNotSupported,
                binding.textViewNoPermissions
            ).forEach { it.visibility = View.INVISIBLE }

            when (healthConnectManager.availability) {
                HealthConnectAvailability.INSTALLED -> ensurePermissions()
                HealthConnectAvailability.NOT_INSTALLED -> binding.groupNotInstalled.visibility = View.VISIBLE
                HealthConnectAvailability.NOT_SUPPORTED -> binding.textViewNotSupported.visibility = View.VISIBLE
            }
        }
    }
}