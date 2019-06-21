package com.wonjin.computer.hw4projectwonjin.commonWonjin;

import android.app.AlertDialog;
import android.content.Context;

public class CommonAlertDialogBuilder extends AlertDialog.Builder
{
    private AlertDialog __dialog = null;

    public CommonAlertDialogBuilder(Context context)
    {
        super(context);
    }

    @Override
    public AlertDialog show()
    {
        /*
            DialogBuilder.show() 메소드는 새로운 다이얼로그
            인스턴스를 매번 생성한다. 이를 방지하기 위한 코드임
         */

        // 다이얼로그 인스턴스가 존재하지 않는다면
        // (즉 show()를 최초 호출한다면)
        if (__dialog == null)
            // 인스턴스 생성 및 show
            __dialog = super.show();
        // show()를 재호출하는 것이라면
        else
            // 기존의 다이얼로그 인스턴스
            __dialog.show();

        return __dialog;
    }
}
