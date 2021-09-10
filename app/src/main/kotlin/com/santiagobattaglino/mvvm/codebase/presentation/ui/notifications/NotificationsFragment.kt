package com.santiagobattaglino.mvvm.codebase.presentation.ui.notifications

import com.santiagobattaglino.mvvm.codebase.domain.entity.Notification
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseFragment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail.IncidentDetailActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.NotificationsViewModel
import com.santiagobattaglino.mvvm.codebase.service.AppFirebaseMessagingService
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : BaseFragment(), NotificationAdapter.OnViewHolderClick {

    private val mTag = javaClass.simpleName

    private val notificationsViewModel: NotificationsViewModel by viewModel()
    private lateinit var notifications: List<Notification>

    private lateinit var adapter: NotificationAdapter

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            try {
                val newMsg = intent.getStringExtra("new_msg")
                notificationsViewModel.getNotifications()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
        if (isPrimaryNavigationFragment) {
            notificationsViewModel.getNotifications()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notifications_list.adapter = null
    }

    override fun observe() {
        observeNotifications()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter(AppFirebaseMessagingService.REQUEST_ACCEPT)
        )
    }


    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }

    private fun observeNotifications() {
        notificationsViewModel.notificationsUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.notifications?.let { notifications ->
                this.notifications = notifications
                empty_text.isVisible = notifications.isEmpty()
                adapter.mData = notifications
            }
        })
    }

    override fun setUpViews() {
        empty_text.isVisible = true

        ViewCompat.setOnApplyWindowInsetsListener(notifications_container) { _, insets ->
            notifications_container.updatePadding(top = insets.stableInsetTop + 70)
            insets
        }

        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter = NotificationAdapter(this)
        notifications_list.adapter = adapter
        notifications_list.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        notifications_list.setHasFixedSize(false)
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Notification) {
        /*context?.startActivity(
            context?.intentFor<IncidentDetailActivity>(
                Arguments.ARG_INCIDENT_ID to data.incidentId?.toInt()
            )
        )*/
    }

    override fun deleteNotificationById(id: Int) {
        notificationsViewModel.deleteNotificationById(id)
    }
}