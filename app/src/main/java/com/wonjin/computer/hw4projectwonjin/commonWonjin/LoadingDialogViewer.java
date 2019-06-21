package com.wonjin.computer.hw4projectwonjin.commonWonjin;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialogViewer
{
    private Context __context;

    private int __numLoaders;
    private ProgressDialog __progressDlg;

    public LoadingDialogViewer(Context context)
    {
        __context = context;
    }

    public void show(String title, String message, final int numLoaders)
    {
        __numLoaders = numLoaders;
        __progressDlg = ProgressDialog.show(__context, title, message);
    }

    public void loadingFinished()
    {
        __numLoaders--;

        if (__numLoaders == 0)
            __progressDlg.dismiss();
    }
}
