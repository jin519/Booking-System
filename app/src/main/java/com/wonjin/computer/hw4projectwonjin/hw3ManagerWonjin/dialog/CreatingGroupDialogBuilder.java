package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog;

import android.content.Context;
import android.widget.EditText;

import com.wonjin.computer.hw4projectwonjin.commonWonjin.CommonAlertDialogBuilder;

public class CreatingGroupDialogBuilder extends CommonAlertDialogBuilder
{
    public interface OnOkListener
    {
        void onOk(String editTextContents);
    }

    private EditText __editText;
    private OnOkListener __onOkListener = (contents -> { });

    public CreatingGroupDialogBuilder(Context context)
    {
        super(context);

        setTitle("그룹 생성");
        setMessage("생성할 그룹명을 입력하세요.");

        __editText = new EditText(context);
        setView(__editText);

        // 다이얼로그의 긍정 답변 버튼에 대한 정보 기록
        setPositiveButton("생성", (dlg, which) ->
                __onOkListener.onOk(__editText.getText().toString()));

        // 다이얼로그의 부정 답변 버튼에 대한 정보 기록
        setNegativeButton("취소", (dlg, which) -> { });
    }

    // 외부로 제공해줄 커스텀 리스너
    public void setOnOkListener(OnOkListener listener)
    {
        __onOkListener = listener;
    }
}
