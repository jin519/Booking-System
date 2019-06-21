package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.CommonAlertDialogBuilder;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;

import java.util.ArrayList;

public class CreatingUserDialogBuilder extends CommonAlertDialogBuilder
{
    public interface OnOkListener
    {
        void onOk(User userInfo);
    }

    private LayoutInflater __layoutInflater;

    private View __contentView;
    private OnOkListener __onOkListener = (contents -> { });

    // group 이름이 저장된 리스트
    private ArrayList<String> __groupList = new ArrayList<>();

    private EditText __name;
    private EditText __password;
    private Spinner __groupSpinner;

    // 그룹 리스트 어댑터
    private ArrayAdapter<String> __groupListAdapter;

    public CreatingUserDialogBuilder(Context context)
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
        setTitle("사용자 생성");
        setMessage("생성할 사용자의 정보를 입력하세요.");
    }

    private void __inflateView(Context context)
    {
        __layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        __contentView = __layoutInflater.inflate(R.layout.dialog_manager_new_user_wonjin, null);
    }

    private void __getContentReferences()
    {
        __name = __contentView.findViewById(R.id.DIALOG_MANAGER_NEW_USER_EDIT_name);
        __password = __contentView.findViewById(R.id.DIALOG_MANAGER_NEW_USER_EDIT_password);
        __groupSpinner = __contentView.findViewById(R.id.DIALOG_MANAGER_NEW_USER_SPINNER_maxContinue);
    }

    private void __initContents(Context context)
    {
        __groupListAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, __groupList);
        __groupSpinner.setAdapter(__groupListAdapter);

        // 다이얼로그의 긍정 답변 버튼에 대한 정보 기록
        setPositiveButton("생성", (dlg, which) ->
        {
            String nameString = __name.getText().toString();
            String pwString = __password.getText().toString();
            String groupString = __groupSpinner.getSelectedItem().toString();

            __onOkListener.onOk(new User(nameString, pwString, groupString));
        });

        // 다이얼로그의 부정 답변 버튼에 대한 정보 기록
        setNegativeButton("취소", (dlg, which) -> { });
    }

    public void updateGroupList(final ArrayList<String> groupList)
    {
        __groupListAdapter.clear();
        __groupListAdapter.addAll(groupList);
    }

    // 외부로 제공해줄 커스텀 리스너
    public void setOnOkListener(OnOkListener listener)
    {
        __onOkListener = listener;
    }
}
