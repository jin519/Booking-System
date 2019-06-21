package com.wonjin.computer.hw4projectwonjin.hw1LoginWonjin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.CommonAlertDialogBuilder;

public class ChangingPasswordDialogBuilder extends CommonAlertDialogBuilder
{
    public interface OnOkListener
    {
        void onOk(int userIndex, String password, String passwordCheck);
    }

    private LayoutInflater __layoutInflater;

    private View __contentView;
    private OnOkListener __onOkListener = ((idx, pw, pwChk) -> { });

    private EditText __password;
    private EditText __passwordCheck;

    private int __userIndex;


    public ChangingPasswordDialogBuilder(Context context)
    {
        super(context);

        // UI 초기화
        __initUI();

        // View 생성
        __inflateView(context);

        // UI 컨텐츠의 레퍼런스를 얻는다.
        __getContentReferences();

        // 각종 컨텐츠 초기화
        __initContents(context);

        setView(__contentView);
    }

    private void __initUI()
    {
        setTitle("비밀번호 변경");
        setMessage("변경할 비밀번호를 입력하세요.");
    }

    private void __inflateView(Context context)
    {
        __layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        __contentView = __layoutInflater.inflate(R.layout.dialog_login_changing_password_wonjin, null);
    }

    private void __getContentReferences()
    {
        __password = __contentView.findViewById(R.id.DIALOG_LOGIN_CHANGING_PASSWORD_EDIT_password);
        __passwordCheck = __contentView.findViewById(R.id.DIALOG_LOGIN_CHANGING_PASSWORD_EDIT_passwordCheck);
    }

    private void __initContents(Context context)
    {
        // 다이얼로그의 긍정 답변 버튼에 대한 정보 기록
        setPositiveButton("변경", (dlg, which) ->
        {
            String pwString = __password.getText().toString();
            String pwChkString = __passwordCheck.getText().toString();

            __onOkListener.onOk(__userIndex, pwString, pwChkString);
        });

        // 다이얼로그의 부정 답변 버튼에 대한 정보 기록
        setNegativeButton("취소", (dlg, which) -> { });
    }

    // 외부로 제공해줄 커스텀 리스너
    public void setOnOkListener(OnOkListener listener)
    {
        __onOkListener = listener;
    }

    public void setUserIndex(final int userIndex)
    {
        __userIndex = userIndex;
    }

    @Override
    public AlertDialog show()
    {
        __password.setText("");
        __passwordCheck.setText("");
        __password.requestFocus();

        return super.show();
    }
}
