package com.ocboe.tech.schooltickettracker;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by Brad on 9/7/2016.
 */
public class DownloadReqPDFService extends Service {

    private static String FILE_NAME = null;
    public static String DOWNLOAD_UPDATE_PDF_TITLE = "Downloading ";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null) {
            FILE_NAME = intent.getStringExtra("downloadFileName");
            DOWNLOAD_UPDATE_PDF_TITLE = DOWNLOAD_UPDATE_PDF_TITLE + FILE_NAME;
            String downloadURL = intent.getStringExtra("downloadURL");
            String newPDFFilePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + FILE_NAME;
            final File newPDFFile = new File(newPDFFilePath);
            final Uri downloadUri = Uri.parse("file://" + newPDFFile);
            if (newPDFFile.exists())
                newPDFFile.delete();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setTitle(DOWNLOAD_UPDATE_PDF_TITLE);

            //set destination
            request.setDestinationUri(downloadUri);

            //get download service and enqueue file
            final DownloadManager manager = (DownloadManager) this.getBaseContext().getSystemService(
                    Context.DOWNLOAD_SERVICE);
            final long startedDownloadId = manager.enqueue(request);

            //set BroadcastReceiver to open the pdf when .pdf is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long finishedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (startedDownloadId == finishedDownloadId) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(finishedDownloadId);
                        Cursor cursor = manager.query(query);
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int status = cursor.getInt(columnIndex);

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                //open the downloaded file
                                Intent openPDF = new Intent(Intent.ACTION_VIEW);
                                openPDF.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                openPDF.setDataAndType(downloadUri,
                                        manager.getMimeTypeForDownloadedFile(startedDownloadId));
                                context.startActivity(openPDF);
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                if (newPDFFile.exists())
                                    newPDFFile.delete();
                            }
                        } else {
                            //delete the partially downloaded file
                            if (newPDFFile.exists())
                                newPDFFile.delete();
                        }

                        context.unregisterReceiver(this);
                        stopSelf();
                    }
                }
            };

            this.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
