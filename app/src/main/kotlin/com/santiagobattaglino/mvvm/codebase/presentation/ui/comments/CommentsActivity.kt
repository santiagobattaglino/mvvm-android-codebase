package com.santiagobattaglino.mvvm.codebase.presentation.ui.comments

import com.santiagobattaglino.mvvm.codebase.data.repository.SharedPreferenceUtils
import com.santiagobattaglino.mvvm.codebase.domain.entity.Comment
import com.santiagobattaglino.mvvm.codebase.presentation.ui.base.BaseTabsActivity
import com.santiagobattaglino.mvvm.codebase.presentation.viewmodel.CommentsViewModel
import com.santiagobattaglino.mvvm.codebase.util.Arguments
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.activity_comments.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CommentsActivity : BaseTabsActivity(), CommentAdapter.OnViewHolderClick {

    private val tag = javaClass.simpleName

    private val commentsViewModel: CommentsViewModel by viewModel()
    private val sp: SharedPreferenceUtils by inject()

    private lateinit var adapter: CommentAdapter

    private var incidentId: Int = 0

    override fun observe() {
        observeComments()
        observeCreateComment()
        observeDeleteComment()
        observeReportComment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        intent.extras?.getString(Arguments.ARG_INCIDENT_TITLE)?.let { incidentTitle ->
            topAppBar.title = incidentTitle
        }

        intent.extras?.getInt(Arguments.ARG_INCIDENT_ID)?.let { incidentId ->
            this.incidentId = incidentId
            commentsViewModel.getComments(incidentId)
        }

        setUpViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        updates_list.adapter = null
    }

    private fun setUpViews() {
        topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        adapter = CommentAdapter(this, sp.getString(Arguments.ARG_USER_ID))
        updates_list.adapter = adapter
        updates_list.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        updates_list.setHasFixedSize(false)

        send.setOnClickListener {
            val commentBody = comment.text.toString()
            if (commentBody.isNotEmpty()) {
                it.isEnabled = false
                commentsViewModel.createComment(
                    Comment(
                        body = commentBody,
                        incidentId = incidentId
                    )
                )
            }
        }
    }

    private fun observeComments() {
        commentsViewModel.commentsUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.comments?.let { comments ->
                setComments(comments)
                send.isEnabled = true
            }
        })
    }

    private fun observeCreateComment() {
        commentsViewModel.commentUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
                send.isEnabled = false
            }

            it.comment?.let { _ ->
                commentsViewModel.getComments(incidentId)
                comment.text = null
                comment.clearFocus()
            }
        })
    }

    private fun observeDeleteComment() {
        commentsViewModel.deleteCommentUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.int?.let { _ ->
                commentsViewModel.getComments(incidentId)
            }
        })
    }

    private fun observeReportComment() {
        commentsViewModel.reportCommentUiData.observe(this, {
            it.error?.let { error ->
                handleError(tag, error)
            }

            it.comment?.let { _ ->
                commentsViewModel.getComments(incidentId)
            }
        })
    }

    private fun setComments(comments: List<Comment>) {
        empty_text.isVisible = comments.isEmpty()

        tabLayout.getTabAt(0)?.text =
            String.format(
                Locale.getDefault(),
                getString(R.string.comments_tab_text),
                comments.size
            )

        //tabLayout.getTabAt(1)?.text = String.format(Locale.getDefault(), getString(R.string.friends_tab_text), "0")

        adapter.mData = comments
    }

    override fun dataViewClickFromList(view: View, position: Int, data: Comment) {
        if (sp.getString(Arguments.ARG_USER_ID) == data.user?.id) {
            AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle("Delete comment?")
                .setPositiveButton("OK") { _, _ ->
                    commentsViewModel.deleteComment(
                        incidentId,
                        data
                    )
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        } else {
            commentsViewModel.reportComment(data.incidentId, data.id)
        }
    }
}