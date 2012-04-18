package com.athena.asm.util.task;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.athena.asm.HomeActivity;
import com.athena.asm.PostListActivity;
import com.athena.asm.data.Post;
import com.athena.asm.data.Subject;
import com.athena.asm.viewmodel.PostListViewModel;

public class LoadPostTask extends AsyncTask<String, Integer, String> {
	private ProgressDialog m_pdialog;
	private int m_boardType;
	private int m_action;
	private boolean m_isSilent;
	private boolean m_isUsePreload;
	private Subject m_subject;
	
	private PostListViewModel m_viewModel;

	public LoadPostTask(Context context, PostListViewModel viewModel, Subject subject, int action, 
			boolean isSilent, boolean isUsePreload) {
		m_boardType = viewModel.getBoardType();
		m_action = action;
		m_isSilent = isSilent;
		m_isUsePreload = isUsePreload;
		m_subject = subject;
		
		m_viewModel = viewModel;
		
		if (!m_isSilent) {
			m_pdialog = new ProgressDialog(context);
		}
	}

	@Override
	protected void onPreExecute() {
		if (!m_isSilent) {
			m_pdialog.setMessage("努力加载中...");
			m_pdialog.show();
		}
	}
	
	private List<Post> getPostList() {
		List<Post> postList = null;
		if (m_boardType == 0) {
			postList = m_viewModel.getSmthSupport().getPostListFromMobile(m_subject, HomeActivity.m_application.getBlackList());
			//postListActivity.postList = postListActivity.smthSupport.getPostList(subject, HomeActivity.application.getBlackList(), startNumber);
		}
		else {
			if (m_action == 0) {
				postList = m_viewModel.getSmthSupport().getSinglePostList(m_subject);
			}
			else {
				postList = m_viewModel.getSmthSupport().getTopicPostList(m_subject, m_action);
			}
		}
		return postList;
	}

	@Override
	protected String doInBackground(String... params) {
		
		if (m_isSilent) {
			m_viewModel.setPreloadPostList(getPostList());
			m_viewModel.setIsPreloadFinished(true);
		}
		else {
			if (m_isUsePreload && m_viewModel.isPreloadFinished() && m_viewModel.getPreloadPostList() != null) {
				m_viewModel.setIsPreloadFinished(false);
				m_viewModel.updatePostListFromPreloadPostList();
				m_viewModel.updateCurrentSubjectFromPreloadSubject();
			}
			else {
				m_viewModel.setPostList(getPostList());
			}
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!m_isSilent) {
			m_pdialog.cancel();
			m_viewModel.notifyPostListChanged();
		}
	}

}
