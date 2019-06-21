package com.wonjin.computer.hw4projectwonjin.hw1LoginWonjin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.DatabaseBroker;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.LoadingDialogViewer;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Message;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;
import com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin.BookingWonjinActivity;
import com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.ManagingGroupActivity;
import java.util.ArrayList;

public class LoginWonjinActivity extends AppCompatActivity
{
    private String __dbRootPath;

    private DatabaseBroker __databaseBroker;
    private ArrayList<String> __groupList = new ArrayList<>();
    private ArrayList<User> __userList = new ArrayList<>();

    private RadioButton __authorityUser;
    private RadioButton __authorityAdmin;

    private Spinner __spinner;
    private ArrayAdapter<String> __groupListAdapter;

    private EditText __id;
    private EditText __password;
    private Button __changePassword;
    private Button __exit;
    private Button __ok;

    private int __selectedGroupPosition;
    private boolean __adminMode = false;

    private ChangingPasswordDialogBuilder __changingPasswordDlg;
    private SharedPreferences __preferences;

    private LoadingDialogViewer __loadingDlgViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_wonjin);

        // UI 컨텐츠의 레퍼런스를 얻는다.
        __getContentReferences();

        // 각종 컨텐츠 초기화
        __initContents();

        // 데이터베이스 초기화
        __initDB();

        // DB 로딩이 완료될 때 까지 로딩 다이얼로그 출력
        __loadingDlgViewer.show("데이터베이스 로딩", "데이터베이스 정보를 불러오는 중입니다..", 2);
    }

    // UI 컨텐츠의 레퍼런스를 얻는다.
    private void __getContentReferences()
    {
        __authorityUser = findViewById(R.id.LoginWonjinActivity_RadioButton_user);
        __authorityAdmin = findViewById(R.id.LoginWonjinActivity_RadioButton_admin);
        __spinner = findViewById(R.id.LoginWonjinActivity_Spinner_group);
        __id = findViewById(R.id.LoginWonjinActivity_EditText_id);
        __password = findViewById(R.id.LoginWonjinActivity_EditText_password);
        __changePassword = findViewById(R.id.LoginWonjinActivity_Button_changePassword);
        __exit = findViewById(R.id.LoginWonjinActivity_Button_exit);
        __ok = findViewById(R.id.LoginWonjinActivity_Button_ok);
    }

    private User __buildUserInfo()
    {
        return new User(
                __id.getText().toString(),
                __password.getText().toString(),
                __groupList.get(__selectedGroupPosition));
    }

    // 각종 컨텐츠 초기화
    private void __initContents()
    {
        __changingPasswordDlg = new ChangingPasswordDialogBuilder(this);
        __changingPasswordDlg.setOnOkListener((userIdx, newPassword, passwordCheck) ->
        {
            if (newPassword.isEmpty())
            {
                Message.information(
                        this, "비밀번호 변경 오류", "비밀번호는 최소 한 글자 이상 입력하여야 합니다.");

                return;
            }

            if (newPassword.equals(passwordCheck))
            {
                String oldPassword = __userList.get(userIdx).userPassword;
                if (oldPassword.equals(newPassword))
                {
                    Message.information(
                            this, "비밀번호 변경 오류", "기존과 동일한 비밀번호로는 변경이 불가능합니다.");

                    return;
                }

                __userList.get(userIdx).userPassword = newPassword;
                __databaseBroker.saveUserDatabase(this, __userList);
                __password.setText("");

                Message.information(this, "비밀번호 변경 완료", "비밀번호 변경이 완료되었습니다.");
            }
            else
                Message.information(this, "비밀번호 변경 오류", "비밀번호와 확인용 비밀번호가 일치하지 않습니다.");
        });

        // spinner 객체에 드롭 다운 리스트를 생성하기 위한 어댑터 생성
        __groupListAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, __groupList);

        // 어댑터 등록
        __spinner.setAdapter(__groupListAdapter);

        __spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                __selectedGroupPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        __authorityUser.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                __id.setEnabled(true);
                __id.setText("");
                __adminMode = false;
            }
        });

        __authorityAdmin.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                __id.setEnabled(false);
                __id.setText("root");
                __adminMode = true;
            }
        });

        __changePassword.setOnClickListener(v ->
        {
            User userInfo = __buildUserInfo();
            int RESULT = LoginAuthorizer.authorize(__userList, userInfo);

            switch (RESULT)
            {
                case -1:
                    Message.information(this, "비밀번호 변경 오류", "아이디를 입력하세요.");
                    return;

                case -2:
                    Message.information(this, "비밀번호 변경 오류", "비밀번호를 입력하세요.");
                    return;

                case -3:
                    Message.information(this, "비밀번호 변경 오류", "비밀번호가 일치하지 않습니다.");
                    return;

                case -4:
                    Message.information(this, "비밀번호 변경 오류", "소속 그룹이 일치하지 않습니다.");
                    return;

                case -5:
                    Message.information(this, "비밀번호 변경 오류", "존재하지 않는 아이디입니다.");
                    return;
            }

            __changingPasswordDlg.setUserIndex(RESULT);
            __changingPasswordDlg.show();
        });

        __exit.setOnClickListener(v -> finish());
        __ok.setOnClickListener(v ->
        {
            User userInfo = __buildUserInfo();
            final int RESULT = LoginAuthorizer.authorize(__userList, userInfo);

            switch (RESULT)
            {
                case -1:
                    Message.information(this, "로그인 오류", "아이디를 입력하세요.");
                    break;

                case -2:
                    Message.information(this, "로그인 오류", "비밀번호를 입력하세요.");
                    break;

                case -3:
                    Message.information(this, "로그인 오류", "비밀번호가 일치하지 않습니다.");
                    break;

                case -4:
                    Message.information(this, "로그인 오류", "소속 그룹이 일치하지 않습니다.");
                    break;

                case -5:
                    Message.information(this, "로그인 오류", "존재하지 않는 아이디입니다.");
                    break;

                default:

                    // 관리자 모드
                    if (__adminMode)
                    {
                        Intent intent = new Intent(this, ManagingGroupActivity.class);
                        intent.putExtra("db_root_path", __dbRootPath);

                        startActivity(intent);
                    }
                    // 사용자 모드
                    else
                    {
                        Intent intent = new Intent(this, BookingWonjinActivity.class);
                        intent.putExtra("user_group", userInfo.userGroup);
                        intent.putExtra("user_name", userInfo.userName);
                        intent.putExtra("db_root_path", __dbRootPath);

                        startActivity(intent);
                    }

                    finish();
                    break;
            }
        });

        __preferences = getSharedPreferences(getString(R.string.pref_root_path), MODE_PRIVATE);
        __loadingDlgViewer = new LoadingDialogViewer(this);
    }

    // 데이터베이스 초기화
    private void __initDB()
    {
        // 리소스로부터 DB 루트 경로를 읽어옴.
        __dbRootPath = getString(R.string.db_root_path);

        // 데이터베이스 브로커 생성
        __databaseBroker = DatabaseBroker.createDatabaseObject(__dbRootPath);

        // 그룹에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setGroupOnDataBrokerListener(this, str ->
        {
            if (__groupList.isEmpty())
            {
                __groupList = __databaseBroker.loadGroupDatabase(this);

                __groupListAdapter.clear();
                __groupListAdapter.addAll(__groupList);

                String storedUserName = __preferences.getString("user_name", "");
                String storedUserPassword = __preferences.getString("user_password", "");
                int storedUserGroupPosition =
                        __preferences.getInt("user_group_position", 0);

                boolean adminMode = __preferences.getBoolean("admin_mode", false);

                // 이전 실행정보 복원
                __password.setText(storedUserPassword);
                __spinner.setSelection(storedUserGroupPosition);

                if (adminMode)
                    __authorityAdmin.setChecked(true);
                else
                {
                    __authorityUser.setChecked(true);
                    __id.setText(storedUserName);
                }

                __loadingDlgViewer.loadingFinished();
            }
            else
            {
                __groupList = __databaseBroker.loadGroupDatabase(this);

                __groupListAdapter.clear();
                __groupListAdapter.addAll(__groupList);
            }
        });

        // 사용자에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setUserOnDataBrokerListener(this, str ->
        {
            if (__userList.isEmpty())
                __loadingDlgViewer.loadingFinished();

            __userList = __databaseBroker.loadUserDatabase(this);
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor prefEditor = __preferences.edit();

        prefEditor.putString("user_name", __id.getText().toString());
        prefEditor.putString("user_password", __password.getText().toString());
        prefEditor.putInt("user_group_position", __selectedGroupPosition);
        prefEditor.putBoolean("admin_mode", __adminMode);

        prefEditor.apply();
    }
}