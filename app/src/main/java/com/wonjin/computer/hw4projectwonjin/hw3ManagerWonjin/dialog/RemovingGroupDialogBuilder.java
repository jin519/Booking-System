package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog;

import android.content.Context;

import com.wonjin.computer.hw4projectwonjin.commonWonjin.CommonAlertDialogBuilder;

public class RemovingGroupDialogBuilder extends CommonAlertDialogBuilder
{
    public interface OnOkListener
    {
        void onOk(String targetGroupName);
    }

    private String __targetGroupName = null;
    private OnOkListener __onOkListener = (targetGroupName -> { });

    public RemovingGroupDialogBuilder(Context context)
    {
        super(context);

        setTitle("그룹 삭제");
        setPositiveButton("삭제", (dlg, which) -> __onOkListener.onOk(__targetGroupName));
        setNegativeButton("취소", (dlg, which) -> { });
    }

    public void setOnOkListener(OnOkListener listener)
    {
        __onOkListener = listener;
    }

    public void show(String targetGroupName)
    {
        __targetGroupName = targetGroupName;

        setMessage(String.format("'%s' 그룹을 삭제 하시겠습니까? 그룹에 소속 되어있는 사용자 정보도 함께 삭제됩니다.", targetGroupName));
        super.show();
    }
}
