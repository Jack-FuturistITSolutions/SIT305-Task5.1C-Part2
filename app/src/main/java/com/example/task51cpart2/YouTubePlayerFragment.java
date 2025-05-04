package com.example.task51cpart2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class YouTubePlayerFragment extends DialogFragment {

    // Set the videoId argument to pass into the YouTubePlayer HTML below
    private static final String ARG_VIDEO_ID = "video_id";

    public static YouTubePlayerFragment newInstance(String videoId) {
        YouTubePlayerFragment fragment = new YouTubePlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    // Load the YouTube Player IFrame using Javascript
    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_youtube_player_fragment, container, false);
        WebView webView = view.findViewById(R.id.youtubeWebView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String videoId = getArguments() != null ? getArguments().getString(ARG_VIDEO_ID) : "";

        String iframeHtml = "<html><body style='margin:0'>" +
                "<iframe id='player' type='text/html' width='100%' height='100%' " +
                "src='https://www.youtube.com/embed/" + videoId + "?enablejsapi=1' " +
                "frameborder='0'></iframe>" +
                "</body></html>";

        webView.loadData(iframeHtml, "text/html", "utf-8");
        return view;
    }
}
