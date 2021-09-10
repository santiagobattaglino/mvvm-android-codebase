package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.detail

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.entity.Update
import com.santiagobattaglino.mvvm.codebase.domain.model.AddReactionRequest
import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import com.santiagobattaglino.mvvm.codebase.domain.model.PlayVideo
import com.santiagobattaglino.mvvm.codebase.domain.model.WebSocketMessage
import com.santiagobattaglino.mvvm.codebase.presentation.ui.BigImageDialog
import com.santiagobattaglino.mvvm.codebase.presentation.ui.BroadcastEndedViewerDialog
import com.santiagobattaglino.mvvm.codebase.presentation.ui.FullScreenDialog
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.comments.CommentsActivity
import com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.add.MediaAdapter
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.NotificationsViewModel
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.UpdatesViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.util.*
import kotlinx.android.synthetic.main.activity_incident_detail.*
import kotlinx.android.synthetic.main.app_bar_layout_media.*
import kotlinx.android.synthetic.main.include_incident_bottom_bar.*
import kotlinx.android.synthetic.main.include_layout_comments.*
import kotlinx.android.synthetic.main.include_layout_reactions.*
import kotlinx.android.synthetic.main.item_incident_normal.*
import kotlinx.android.synthetic.main.popup_reactions.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.io.FileWriter
import java.io.IOException

class IncidentDetailActivity : BaseActivity(), UpdateAdapter.OnViewHolderClick,
    FullScreenDialog.OnFullScreenDialogViewClickListener, MediaAdapter.OnViewHolderClick {

    private val tag = javaClass.simpleName

    private val incidentsViewModel: IncidentsViewModel by viewModel()
    private val notificationsViewModel: NotificationsViewModel by viewModel()
    private val updatesViewModel: UpdatesViewModel by viewModel()

    private var lastKnownLocation: Location? = null
    private var lastKnownLocationTime: String? = null

    private lateinit var adapter: UpdateAdapter

    private var incidentId = 0
    private var incident: Incident? = null

    private var popupWindowView: View? = null
    private var popupWindow: PopupWindow? = null

    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var mediaAdapter: MediaAdapter
    private val mediaList = mutableListOf<Media>()
    private lateinit var dataSourceFactory: DataSource.Factory

    override fun observe() {
        observeIncident()
        observeUpdates()
        observeCommentsCount()
        observeReactionsSingleIncident()
        observePlayVideo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_detail)

        setUpAppBar()
        setUpViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        updates_list.adapter = null
    }

    override fun onResume() {
        super.onResume()
        intent.extras?.getInt(Arguments.ARG_INCIDENT_ID)?.let {
            incidentId = it
            incidentsViewModel.getIncidentById(it, false)
            incidentsViewModel.getCommentsCount(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            tag,
            "onActivityResult requestCode: $requestCode, resultCode: $resultCode, data: $data"
        )
        if (requestCode == Arguments.LIVE_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                when (data?.extras?.getString(Arguments.ARG_STOP_BROADCAST)) {
                    WebSocketMessage.MESSAGE_STOP_HQ -> {
                        Log.d(tag, "onActivityResult go to stop hq")
                        val dialog = BroadcastEndedViewerDialog()
                        val ft = supportFragmentManager.beginTransaction()
                        dialog.setOnFragmentViewClickListener(this)
                        dialog.show(ft, dialog.mTag)
                    }
                    WebSocketMessage.MESSAGE_STOP_USER -> {
                        Log.d(tag, "onActivityResult go to stop user")
                        // TODO check if we need to redirect or display something here
                    }
                }
            }
        }
    }

    override fun onFullScreenDialogViewClicked(view: View) {
        when (view.id) {
            R.id.action -> {
                incident?.let {
                    incidentsViewModel.setLiveStopped(it.id)
                    finish()
                }
            }
        }
    }

    private fun setUpAppBar() {
        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        params.height = resources.displayMetrics.heightPixels / 2
        params.behavior = behavior
    }

    private fun setUpViews() {
        setUpUpdatesList()
        setUpMediaList()
    }

    private fun setUpMediaList() {
        mediaAdapter = MediaAdapter(this, true)
        media_list.adapter = mediaAdapter
        val layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )
        media_list.layoutManager = layoutManager
        media_list.setHasFixedSize(false)
    }

    private fun setUpUpdatesList() {
        adapter = UpdateAdapter(this)
        updates_list.adapter = adapter
        updates_list.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        updates_list.setHasFixedSize(false)
    }

    private fun observeIncident() {
        incidentsViewModel.incidentUiData.observe(this, {
            it.error?.let { error ->
                //handleError(tag, error)
                if (error.isNoContent()) {
                    //longToast(R.string.incident_not_exists)
                    notificationsViewModel.deleteNotificationByIncidentId(error.msg)
                    error.msg?.let { incidentId ->
                        incidentsViewModel.deleteIncident(incidentId.toInt())
                    }
                    finish()
                }
            }

            it.incident?.let { incident ->
                this.incident = incident
                setIncident(incident)
            }
        })
    }

    private fun setIncident(incident: Incident) {
        setIncidentData(incident)
        incident.media?.let {
            setMedia(it)
        }
    }

    private fun setMedia(mediaList: List<Media>) {
        this.mediaList.clear()
        this.mediaList.add(
            Media(
                0,
                Media.TYPE_CONTENT_MAP,
                null,
                null,
                null
            )
        )
        this.mediaList.addAll(mediaList)
        mediaAdapter.mData = this.mediaList
    }

    private fun showMapSection() {
        map.isVisible = true
        photo.isVisible = false
        video.isVisible = false
    }

    private fun showPhotoSection() {
        map.isVisible = false
        photo.isVisible = true
        video.isVisible = false
    }

    private fun showVideoSection() {
        map.isVisible = false
        photo.isVisible = false
        video.isVisible = true
    }

    private fun observeUpdates() {
        updatesViewModel.updatesUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.updates?.let { updates ->
                adapter.mData = updates
            }
        })
    }

    private fun observeCommentsCount() {
        incidentsViewModel.commentsCountUiData.observe(this, {
            comments.text = it.toString()
        })
    }

    private fun observeReactionsSingleIncident() {
        incidentsViewModel.reactionsSingleUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.incident?.let { incident ->
                //incidentsViewModel.getIncident(incidentId)
                setUpReactionsSingleIncident(this, incident)
            }
        })
    }

    private fun observePlayVideo() {
        incidentsViewModel.playVideoUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.playVideo?.let { playVideo ->
                Log.d(tag, playVideo.toString())

                try {
                    val playListFile = createPlayListFile(playVideo.incident)
                    val writer = FileWriter(playListFile)
                    writer.append(playVideo.content)
                    writer.flush()
                    writer.close()

                    initPlayer(this)
                    setVideo(playListFile.absolutePath, true)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }

    @Throws(IOException::class)
    private fun createPlayListFile(incidentId: String): File {
        return File.createTempFile(
            "playlist_${incidentId}_",
            ".m3u8",
            getExternalFilesDir(null)
        ).apply {
            deleteOnExit()
        }
    }

    private fun setIncidentData(incident: Incident) {
        normal_title.text = incident.title
        normal_time.text = String.format(
            " · %s %s",
            getString(R.string.updated),
            PrettyTime().format(incident.updatedAt.toDate())
        )

        normal_ocurred_time.isVisible = true
        normal_ocurred_time.text = String.format(
            "%s %s",
            getString(R.string.occurred_on),
            getAtLocalTime(incident.at)
        )

        incident.distance?.let {
            normal_distance.text = it
        }

        normal_address.text = incident.address
        normal_description.maxLines = 100
        normal_description.text = incident.description

        comments_layout.setOnClickListener {
            /*startActivity(
                intentFor<CommentsActivity>(
                    Arguments.ARG_INCIDENT_TITLE to incident.title,
                    Arguments.ARG_INCIDENT_ID to incident.id
                )
            )*/
        }

        setUpReactions(this, incident)

        share_layout.setOnClickListener {
            val time = getAtLocalTime(incident.at)
            /*share(
                "${incident.title}\n" +
                        "${incident.description}\n" +
                        "${incident.address}\n" +
                        "$time\n" +
                        "\n" +
                        "${BuildConfig.HQ_BASE_URL}/#/deeplinks/incidents/${incident.id}"
            )*/

            /*val content: ShareLinkContent = ShareLinkContent.Builder()
                .setQuote("lelele")
                .setContentUrl(Uri.parse("${BuildConfig.HQ_BASE_URL}/#/deeplinks/incidents/${incident.id}"))
                .build()

            val shareDialog = ShareDialog(this)
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC)*/
        }
    }

    private fun setUpReactions(context: Context, incident: Incident) {
        default_reaction.text = getMostSelectedReaction(context, incident)
        reactions.text = getReactionsCount(incident).toString()
        reactions_layout.setOnClickListener {
            setUpReactionPopupWindow(incident.id)
            setPopupWindowReactions(
                popupWindowView,
                incident.fearfulFaceReactions,
                incident.handsPressedReactions,
                incident.redAngryFaceReactions,
                incident.redHeartReactions
            )
        }
    }

    private fun setUpReactionsSingleIncident(context: Context, incident: Incident) {
        default_reaction.text = getMostSelectedReaction(context, incident)
        reactions.text = getReactionsCount(incident).toString()
        setPopupWindowReactions(
            popupWindow?.contentView,
            incident.fearfulFaceReactions,
            incident.handsPressedReactions,
            incident.redAngryFaceReactions,
            incident.redHeartReactions
        )
    }

    // TODO avoid refreshing all with Flow. onResume is calling and restarting the player when the user adds a reaction
    private fun setUpReactionPopupWindow(incidentId: Int) {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupWindowView = inflater.inflate(R.layout.popup_reactions, null)

        popupWindow = PopupWindow(
            popupWindowView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(reactions_layout, 16, -100)

        popupWindowView?.close?.setOnClickListener {
            popupWindow?.dismiss()
        }

        popupWindowView?.layout_reaction_1?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.FEARFUL_FACE,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_2?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.HANDS_PRESSED_TOGETHER,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_3?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.RED_ANGRY_FACE,
                    incidentId
                )
            )
        }
        popupWindowView?.layout_reaction_4?.setOnClickListener {
            popupWindow?.dismiss()
            incidentsViewModel.addReactionSingleIncident(
                AddReactionRequest(
                    AddReactionRequest.RED_HEART,
                    incidentId
                )
            )
        }
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Update) {
        // TODO click update
    }

    private fun setPhoto(url: String) {
        photo.isVisible = true
        //topAppBar.menu.getItem(0).isVisible = true
        GlideApp.with(this)
            .load(GlideUrlCustomCacheKey(url))
            //.apply(RequestOptions.centerCropTransform())
            .into(photo)
    }

    private fun setVideo(url: String, isFromCloudRecording: Boolean = false) {
        video.isVisible = true
        val mediaSource = if (!isFromCloudRecording) {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
        } else {
            HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url))
        }
        exoPlayer?.prepare(mediaSource)
        exoPlayer?.playWhenReady = true
    }

    private fun initPlayer(context: Context) {
        exoPlayer = SimpleExoPlayer.Builder(context).build()
        dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.getString(R.string.app_name))
        )
        video.player = exoPlayer
        exoPlayer?.volume = 1f
        exoPlayer?.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Media) {
        exoPlayer?.stop()
        when (view.id) {
            R.id.go_to_map -> {
                showMapSection()
            }
            R.id.go_to_photo -> {
                showPhotoSection()
                data.url?.let {
                    setPhoto(it)
                }

                photo.setOnClickListener {
                    val dialog = BigImageDialog(data)
                    val ft = supportFragmentManager.beginTransaction()
                    dialog.setOnFragmentViewClickListener(this)
                    dialog.show(ft, dialog.mTag)
                }
            }
            R.id.go_to_video -> {
                showVideoSection()
                data.url?.let {
                    initPlayer(this)
                    setVideo(it)
                }
            }
            R.id.go_to_cloud_recording -> {
                showVideoSection()
                val playlistFileName = Uri.parse(data.url).lastPathSegment
                playlistFileName?.let { fileName ->
                    incidentsViewModel.postPlayVideo(
                        PlayVideo(
                            incidentId.toString(),
                            fileName
                        )
                    )
                }
            }
        }
    }
}