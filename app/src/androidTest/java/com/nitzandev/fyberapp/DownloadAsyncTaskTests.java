package com.nitzandev.fyberapp;

import android.test.AndroidTestCase;
import android.test.UiThreadTest;

import com.nitzandev.fyberapp.utils.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by nitzanwerber on 3/13/15.
 */
public class DownloadAsyncTaskTests extends AndroidTestCase implements IDownloadListener {
    MainActivity.AsyncDownloader downloader;
    CountDownLatch signal;


    protected void setUp() throws Exception {
        super.setUp();

        signal = new CountDownLatch(1);
        downloader = new MainActivity.AsyncDownloader(this);
    }

    @UiThreadTest
    public void testDownload() throws InterruptedException {
        downloader.download(this.getContext());
        signal.await(30, TimeUnit.SECONDS);
    }

    @Override
    public void downloadCompleted(Response.Offer[] data) {
        signal.countDown();
        assertNotNull("the offers returned are not null", data);
        assertTrue("the offers returned", data.length != 0);
    }

}