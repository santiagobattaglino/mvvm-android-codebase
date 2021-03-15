package com.santiagobattaglino.mvvm.codebase.presentation.ui.incidents.add

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.santiagobattaglino.mvvm.codebase.BuildConfig
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import com.santiagobattaglino.mvvm.codebase.presentation.ui.access.Validator
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BasePermissionFragment
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.IncidentsViewModel
import com.santiagobattaglino.mvvm.codebase.util.*
import com.santiagobattaglino.mvvm.codebase.util.IOUtils.copy
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.app_bar_layout_media.*
import kotlinx.android.synthetic.main.fragment_add_incident.*
import kotlinx.android.synthetic.main.fragment_add_incident.title
import kotlinx.android.synthetic.main.popup_add_photo.view.*
import kotlinx.android.synthetic.main.popup_add_video.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddIncidentFragment : BasePermissionFragment(), Toolbar.OnMenuItemClickListener,
    MediaAdapter.OnViewHolderClick {

    private val mTag = javaClass.simpleName

    private val incidentsViewModel: IncidentsViewModel by viewModel()

    private lateinit var photoPath: String
    private var photoFile: File? = null

    private lateinit var photoResizedPath: String
    private var photoResizedFile: File? = null

    private lateinit var videoPath: String
    private var videoFile: File? = null

    private lateinit var videoResizedPath: String
    private var videoResizedFile: File? = null

    private var lastKnownLocation: Location? = null
    private var lastKnownAddress: Address? = null

    private var exoPlayer: SimpleExoPlayer? = null
    private val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    private var popupWindow: PopupWindow? = null

    private lateinit var mediaAdapter: MediaAdapter
    private val mediaList = mutableListOf<Media>()
    private var incidentId = 0
    private var calendarDateUTC = getCalendar()
    private lateinit var dataSourceFactory: DataSource.Factory
    private var category: Category? = null
    private var datePicker: MaterialDatePicker<Long>? = null
    private var timePicker: TimePickerDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_incident, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        media_list.adapter = null
    }

    override fun observe() {
        observeCreateIncident()
        observeAddMedia()
        observePhotoResize()
        observeMediaList()
        observeCheckPlace()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.remove_photo -> {
                photoFile = null
                photo.isVisible = false
                topAppBar.menu.getItem(0).isVisible = false
            }
            R.id.take_photo -> takePhoto()
            R.id.pick_photo -> pickFromGallery("image/*")
            R.id.take_video -> takeVideo()
            R.id.pick_video -> pickFromGallery("video/*")
        }
        return true
    }

    override fun setUpViews() {
        ViewCompat.setOnApplyWindowInsetsListener(add_incident_container) { _, insets ->
            add_incident_container.updatePadding(top = insets.stableInsetTop + 70)
            insets
        }

        setUpAppBar()

        arguments?.getParcelable<Category>(Arguments.ARG_CATEGORY)?.let { category ->
            this.category = category

            title.setText(category.name)

            add.setOnClickListener {
                if (Validator.isValidName(
                        title,
                        true,
                        getString(R.string.error_incident_title)
                    ) && Validator.isValidName(
                        description,
                        true,
                        getString(R.string.error_incident_description)
                    )
                ) {
                    it.isEnabled = false
                    progress.isVisible = true
                    // TODO check place removed
                }
            }
        }

        add_photo.setOnClickListener {
            openAddMultimediaPopUpWindow(Media.TYPE_CONTENT_PHOTO)
        }

        add_video.setOnClickListener {
            openAddMultimediaPopUpWindow(Media.TYPE_CONTENT_VIDEO)
        }

        setUpMediaList()

        date.text =
            Date(calendarDateUTC.timeInMillis).formatTo(
                getDateFormat(),
                TimeZone.getDefault()
            )
        time.text =
            Date(calendarDateUTC.timeInMillis).formatTo(
                getTimeFormat(),
                TimeZone.getDefault()
            )

        setUpDatePicker()
        date.setOnClickListener {
            datePicker?.show(childFragmentManager, "datePicker")
        }

        setUpTimePicker()
        time.setOnClickListener {
            timePicker?.show(childFragmentManager, "timePicker")
        }

        Log.d(mTag, Date(calendarDateUTC.timeInMillis).formatTo())
    }

    private fun setUpDatePicker() {
        val datePickerBuilder = getDatePicker()
        datePickerBuilder.setSelection(calendarDateUTC.timeInMillis)
        datePicker = datePickerBuilder.build()
        datePicker?.addOnPositiveButtonClickListener {
            val dateCal = getCalendar()
            dateCal.timeInMillis = it
            calendarDateUTC.set(Calendar.YEAR, dateCal.get(Calendar.YEAR))
            calendarDateUTC.set(Calendar.MONTH, dateCal.get(Calendar.MONTH))
            calendarDateUTC.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH))
            date.text = Date(it).formatTo(getDateFormat())
            Log.d(mTag, Date(calendarDateUTC.timeInMillis).formatTo())

            val calendarLocalTime = getCalendar(TimeZone.getDefault())
            if (calendarDateUTC.get(Calendar.DAY_OF_YEAR) < calendarLocalTime.get(Calendar.DAY_OF_YEAR)) {
                timePicker?.setMaxTime(null)
            } else {
                timePicker?.setMaxTime(
                    calendarLocalTime.get(Calendar.HOUR_OF_DAY),
                    calendarLocalTime.get(Calendar.MINUTE),
                    calendarLocalTime.get(Calendar.SECOND)
                )
            }
        }
    }

    private fun setUpTimePicker() {
        val calendarLocalTime = getCalendar(TimeZone.getDefault())
        timePicker = getTimePicker(calendarLocalTime)
        timePicker?.setOnTimeSetListener { _, hourOfDay, minute, second ->
            val rawOffset = getCalendar(TimeZone.getDefault()).get(Calendar.ZONE_OFFSET)
            val hourOffset = TimeUnit.HOURS.convert(rawOffset.toLong(), TimeUnit.MILLISECONDS)
            Log.d(mTag, "offset $hourOfDay - ${hourOffset.toInt()}")
            calendarDateUTC.set(
                Calendar.HOUR_OF_DAY,
                hourOfDay - hourOffset.toInt()
            )
            calendarDateUTC.set(Calendar.MINUTE, minute)
            calendarDateUTC.set(Calendar.SECOND, second)
            val timePicked = Date(calendarDateUTC.timeInMillis).formatTo(
                getTimeFormat(),
                TimeZone.getDefault()
            )
            time.text = timePicked
            Log.d(mTag, Date(calendarDateUTC.timeInMillis).formatTo())
        }
        if (calendarDateUTC.get(Calendar.DAY_OF_YEAR) < calendarLocalTime.get(Calendar.DAY_OF_YEAR)) {
            timePicker?.setMaxTime(null)
        } else {
            timePicker?.setMaxTime(
                calendarLocalTime.get(Calendar.HOUR_OF_DAY),
                calendarLocalTime.get(Calendar.MINUTE),
                calendarLocalTime.get(Calendar.SECOND)
            )
        }
    }

    private fun setUpMediaList() {
        mediaAdapter = MediaAdapter(this)
        media_list.adapter = mediaAdapter
        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
        media_list.layoutManager = layoutManager
        media_list.setHasFixedSize(false)

        mediaList.add(
            Media(
                0,
                Media.TYPE_CONTENT_MAP,
                null,
                null,
                null
            )
        )
        incidentsViewModel.setMediaList(mediaList.toList())
    }

    @SuppressLint("InflateParams")
    private fun openAddMultimediaPopUpWindow(typeContent: String) {
        val inflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popupWindowView = if (typeContent == Media.TYPE_CONTENT_PHOTO) {
            inflater.inflate(R.layout.popup_add_photo, null).apply {
                this.take_photo.setOnClickListener {
                    takePhoto()
                    popupWindow?.dismiss()
                }

                this.pick_photo.setOnClickListener {
                    pickFromGallery("image/*")
                    popupWindow?.dismiss()
                }
            }
        } else {
            inflater.inflate(R.layout.popup_add_video, null).apply {
                this.take_video.setOnClickListener {
                    takeVideo()
                    popupWindow?.dismiss()
                }

                this.pick_video.setOnClickListener {
                    pickFromGallery("video/*")
                    popupWindow?.dismiss()
                }
            }
        }

        popupWindow = PopupWindow(
            popupWindowView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAtLocation(add_incident_container, Gravity.CENTER, 0, 0)
    }

    private fun setUpAppBar() {
        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        context?.let {
            params.height = it.resources.displayMetrics.heightPixels / 2
        }
        params.behavior = behavior

        //topAppBar.title = getString(R.string.add_incident)
        topAppBar.setOnMenuItemClickListener(this)
        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // TODO coming back from settings
            //if (hasCameraPermission()) {
            //takePhoto()
            //}
        } else if (requestCode == takePhotoReq && resultCode == RESULT_OK) {
            try {
                resizePhoto()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == takeVideoReq && resultCode == RESULT_OK) {
            try {
                resizeVideo()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == pickPhotoFromGalleryReq && resultCode == RESULT_OK) {
            data?.data?.let {
                val inputStream: InputStream? = context?.contentResolver?.openInputStream(it)
                try {
                    photoFile = photoInputStreamToFile(inputStream)
                    // TODO getPhotoMetadataDistance()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                resizePhoto()
            }
        } else if (requestCode == pickVideoFromGalleryReq && resultCode == RESULT_OK) {
            data?.data?.let {
                val inputStream: InputStream? = context?.contentResolver?.openInputStream(it)
                inputStream?.let { inputStreamFromPickedVideo ->
                    val videoSize = inputStreamFromPickedVideo.available() / 1024 / 1024

                    if (videoSize <= Constants.UPLOAD_VIDEO_SIZE_LIMIT) {
                        try {
                            videoFile = videoInputStreamToFile(inputStream)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        resizeVideo()
                    } else {
                        context?.toast(
                            String.format(
                                getString(R.string.video_file_size_too_big),
                                videoSize.toString()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getPhotoMetadataDistance() {
        photoFile?.let { file ->
            copyExif(file.absolutePath, file.absolutePath)
            val mediaLocation = getMediaLocation(file.absolutePath)
            lastKnownLocation?.let { lastKnownLocation ->
                // TODO getDistance removed
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermPhotoReq -> {
                if (hasCameraPermission()) {
                    takePhoto()
                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
            cameraPermVideoReq -> {
                if (hasCameraPermission()) {
                    takeVideo()
                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
            storagePermReqPhotoGallery -> {
                if (hasStoragePermission()) {
                    pickFromGallery("image/*")
                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
            storagePermReqVideoGallery -> {
                if (hasStoragePermission()) {
                    pickFromGallery("video/*")
                } else {
                    EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults, this
                    )
                }
            }
        }
    }

    private fun pickFromGallery(type: String) {
        if (hasStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = type
            if (type == "image/*") {
                startActivityForResult(intent, pickPhotoFromGalleryReq)
            } else {
                startActivityForResult(intent, pickVideoFromGalleryReq)
            }
        } else {
            if (type == "image/*") {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    storagePermReqPhotoGallery,
                    *storagePermissions
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    storagePermReqVideoGallery,
                    *storagePermissions
                )
            }
        }
    }

    private fun takePhoto() {
        if (hasCameraPermission()) {
            context?.let {
                dispatchTakePictureIntent(it)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                cameraPermPhotoReq,
                *cameraPermissions
            )
        }
    }

    private fun takeVideo() {
        if (hasCameraPermission()) {
            context?.let {
                dispatchTakeVideoIntent(it)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                cameraPermVideoReq,
                *cameraPermissions
            )
        }
    }

    private fun dispatchTakePictureIntent(context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createPhotoFile()
                } catch (ex: IOException) {
                    Log.e(mTag, ex.message.toString())
                    null
                }
                photoFile?.also { photoFileFromGallery ->
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFileFromGallery
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, takePhotoReq)
                    this.photoFile = photoFileFromGallery
                }
            }
        }
    }

    private fun dispatchTakeVideoIntent(context: Context) {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            //val size = 1 * 1024 * 1024 // 1 MB
            //takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, size.toLong())
            //takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5L)
            takeVideoIntent.resolveActivity(context.packageManager)?.also {
                val videoFile: File? = try {
                    createVideoFile()
                } catch (ex: IOException) {
                    Log.e(mTag, ex.message.toString())
                    null
                }
                videoFile?.also { videoFileFromGallery ->
                    val videoURI: Uri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        videoFileFromGallery
                    )
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                    startActivityForResult(takeVideoIntent, takeVideoReq)
                    this.videoFile = videoFileFromGallery
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createPhotoFile(): File {
        return File.createTempFile(
            "add_incident_${timeStamp}_",
            ".jpg",
            context?.getExternalFilesDir(null)
        ).apply {
            photoPath = absolutePath
            deleteOnExit()
        }
    }

    @Throws(IOException::class)
    private fun createPhotoResizedFile(): File {
        return File.createTempFile(
            "add_incident_resized_${timeStamp}_",
            ".jpg",
            context?.getExternalFilesDir(null)
        ).apply {
            photoResizedPath = absolutePath
            deleteOnExit()
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File {
        return File.createTempFile(
            "add_incident_${timeStamp}_",
            ".mp4",
            context?.getExternalFilesDir(null)
        ).apply {
            videoPath = absolutePath
            deleteOnExit()
        }
    }

    @Throws(IOException::class)
    private fun createVideoResizedFile(): File {
        return File.createTempFile(
            "add_incident_resized_${timeStamp}_",
            ".mp4",
            context?.getExternalFilesDir(null)
        ).apply {
            videoResizedPath = absolutePath
            deleteOnExit()
        }
    }

    @Throws(IOException::class)
    fun photoInputStreamToFile(input: InputStream?): File? {
        val tempFile = File.createTempFile(
            "add_incident_${timeStamp}_",
            ".jpg",
            context?.getExternalFilesDir(null)
        )
        photoPath = tempFile.absolutePath
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { out ->
            copy(input, out)
        }
        return tempFile
    }

    @Throws(IOException::class)
    fun videoInputStreamToFile(input: InputStream?): File? {
        val tempFile = File.createTempFile(
            "add_incident_${timeStamp}_",
            ".mp4",
            context?.getExternalFilesDir(null)
        )
        videoPath = tempFile.absolutePath
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { out ->
            copy(input, out)
        }
        return tempFile
    }

    private fun observeCreateIncident() {
        incidentsViewModel.incidentUiData.observe(this, {
            it.error?.let { error ->
                add.isEnabled = true
                progress.isVisible = false
                if (error.isDescriptionLong()) {
                    context?.longToast(getString(R.string.error_description_too_long))
                } else {
                    context?.longToast(error.toString())
                }
            }

            it.incident?.let { incident ->
                this.incidentId = incident.id
                if (mediaList.size == 1) {
                    add.isEnabled = true
                    progress.isVisible = false
                    //activity?.finish()
                    //context?.startActivity<BottomNavActivity>()
                    findNavController().navigate(
                        R.id.incident_created_fragment,
                        bundleOf(Arguments.ARG_INCIDENT_ID to incident.id)
                    )
                } else {
                    add.text = getString(R.string.uploading_media)
                    incidentsViewModel.addMedia(
                        incident.id,
                        mediaList,
                        640,
                        480
                    )
                }
            }
        })
    }

    private fun observeAddMedia() {
        incidentsViewModel.addMediaUiData.observe(this, {
            it.error?.let { error ->
                add.isEnabled = true
                add.text = getString(R.string.create_incident)
                progress.isVisible = false
                context?.longToast(error.toString())
            }

            it.mediaResponseList?.let { mediaResponseList ->
                Log.d(mTag, "$mediaResponseList")
                add.isEnabled = true
                add.text = getString(R.string.create_incident)
                progress.isVisible = false
                findNavController().navigate(
                    R.id.incident_created_fragment,
                    bundleOf(Arguments.ARG_INCIDENT_ID to incidentId)
                )
            }
        })
    }

    private fun buildIncidentData(categoryId: Int): Incident {
        return Incident(
            title = title.text.toString(),
            description = description.text.toString(),
            latitude = 0.0,
            longitude = 0.0,
            address = lastKnownAddress?.getAddressLine(0),
            city = lastKnownAddress?.adminArea,
            incidentsCategoryId = categoryId,
            at = Date(calendarDateUTC.timeInMillis).formatTo()
        )
    }

    private fun resizeVideo() {
        videoResizedFile = try {
            createVideoResizedFile()
        } catch (ex: IOException) {
            Log.e(mTag, ex.message.toString())
            null
        }

        videoResizedFile?.let { _ ->
            VideoCompressor.start(
                videoPath,
                videoResizedPath,
                object : CompressionListener {
                    override fun onProgress(percent: Float) {
                        Log.d(mTag, "onProgress: $percent")
                    }

                    override fun onStart() {
                        Log.d(mTag, "onStart")
                        downsampling.isVisible = true
                    }

                    override fun onSuccess() {
                        Log.d(mTag, "onSuccess")
                        addVideoToMediaList()
                        downsampling.isVisible = false
                    }

                    override fun onFailure() {
                        Log.d(mTag, "onFailure")
                        downsampling.isVisible = false
                    }

                    override fun onCancelled() {
                        Log.d(mTag, "onCancelled")
                        downsampling.isVisible = false
                    }
                }, VideoQuality.LOW, isMinBitRateEnabled = false
            )
        }
    }

    private fun resizePhoto() {
        photoResizedFile = try {
            createPhotoResizedFile()
        } catch (ex: IOException) {
            Log.e(mTag, ex.message.toString())
            null
        }

        context?.let { context ->
            photoFile?.let { photoFile ->
                photoResizedFile?.let { photoResizedFile ->
                    incidentsViewModel.resizePhotoTaken(
                        context,
                        photoFile,
                        photoResizedFile,
                        Constants.DOWNSAMPLE_SIZE
                    )
                }
            }
        }
    }

    private fun observePhotoResize() {
        incidentsViewModel.resizePhotoUiData.observe(this, { isResized ->
            if (!isResized) {
                context?.longToast(error.toString())
            } else {
                addPhotoToMediaList()
            }
        })
    }

    private fun observeMediaList() {
        incidentsViewModel.mediaListUiData.observe(this, { mediaList ->
            mediaAdapter.mData = mediaList
            val lastMedia = mediaList.last()
            when (lastMedia.typeContent) {
                Media.TYPE_CONTENT_MAP -> {
                    showMapSection()
                }
                Media.TYPE_CONTENT_PHOTO -> {
                    showPhotoSection()
                    lastMedia.url?.let {
                        setPhoto(File(it))
                    }
                }
                Media.TYPE_CONTENT_VIDEO -> {
                    showVideoSection()
                    lastMedia.url?.let {
                        context?.let { context ->
                            initPlayer(context)
                        }
                        setVideo(File(it))
                    }
                }
            }
        })
    }

    private fun observeCheckPlace() {
        incidentsViewModel.checkPlaceUiData.observe(this, {
            it.error?.let { error ->
                handleError(mTag, error)
            }

            it.checkPlace?.let { checkPlace ->
                checkPlace.isAllowed?.let { isAllowed ->
                    if (!isAllowed) {
                        findNavController().navigate(R.id.add_incident_to_place_not_allowed)
                    } else {
                        category?.let { category ->
                            incidentsViewModel.createIncident(buildIncidentData(category.id))
                        }
                    }
                }
            }
        })
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

    override fun dataViewClickFromList(view: View, position: Int, data: Media) {
        exoPlayer?.stop()
        when (view.id) {
            R.id.remove_media -> {
                mediaList.remove(data)
                incidentsViewModel.setMediaList(mediaList.toList())
            }
            R.id.go_to_map -> {
                showMapSection()
            }
            R.id.go_to_photo -> {
                showPhotoSection()
                data.url?.let {
                    setPhoto(File(it))
                }
            }
            R.id.go_to_video -> {
                showVideoSection()
                data.url?.let {
                    context?.let { context ->
                        initPlayer(context)
                    }
                    setVideo(File(it))
                }
            }
        }
    }

    private fun addPhotoToMediaList() {
        mediaList.add(
            Media(
                mediaList.size,
                Media.TYPE_CONTENT_PHOTO,
                photoResizedPath,
                null,
                null
            )
        )
        incidentsViewModel.setMediaList(mediaList.toList())
    }

    private fun addVideoToMediaList() {
        mediaList.add(
            Media(
                mediaList.size,
                Media.TYPE_CONTENT_VIDEO,
                videoResizedPath,
                null,
                null
            )
        )
        incidentsViewModel.setMediaList(mediaList.toList())
    }

    private fun setPhoto(photoFile: File) {
        photo.isVisible = true
        //topAppBar.menu.getItem(0).isVisible = true
        GlideApp.with(this@AddIncidentFragment)
            .load(photoFile)
            //.apply(RequestOptions.centerCropTransform())
            .into(photo)
    }

    private fun setVideo(videoFile: File) {
        video.isVisible = true
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(videoFile))
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
}