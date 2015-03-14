package com.nitzandev.fyberapp;

import com.nitzandev.fyberapp.utils.Response;

/**
 * Created by nitzanwerber on 3/12/15.
 * Listens to the completion of the download process
 */
    public interface IDownloadListener {
        void downloadCompleted(Response.Offer[] data);
    }

