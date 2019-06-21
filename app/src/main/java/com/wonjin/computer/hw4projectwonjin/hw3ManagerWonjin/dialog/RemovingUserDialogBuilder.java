package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog;

import android.content.Context;

import com.wonjin.computer.hw4projectwonjin.commonWonjin.CommonAlertDialogBuilder;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;

public class RemovingUserDialogBuilder extends CommonAlertDialogBuilder
{
    public interface OnOkListener
    {
        void onOk(User user);
    }

    private User __targetUser = null;
    private OnOkListener __onOkListener = (targetGroupName -> { });

    public RemovingUserDialogBuilder(Context context)
    {
        super(context);

        setTitle("사용자 삭제");
        setPositiveButton("삭제", (dlg, which) -> __onOkListener.onOk(__targetUser));
        setNegativeButton("취소", (dlg, which) -> { });
    }

    public void setOnOkListener(OnOkListener listener)
    {
        __onOkListener = listener;
    }

    public void show(User user)
    {
        __targetUser = user;

        setMessage(String.format("'%s' 사용자를 삭제 하시겠습니까?", user.userName));
        super.show();
    }
}
