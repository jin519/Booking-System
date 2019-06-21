package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.DatabaseBroker;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.LoadingDialogViewer;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Message;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;
import com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog.CreatingUserDialogBuilder;
import com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog.RemovingUserDialogBuilder;

import java.util.ArrayList;

public class ManagingUserActivity extends CommonActivity
{
    // 데이터베이스 연동을 위한 DatabaseBroker 객체
    private DatabaseBroker __databaseBroker;

    // group 이름이 저장된 리스트
    private ArrayList<String> __groupList = new ArrayList<>();

    // user 정보가 저장된 리스트
    private ArrayList<User> __userList = new ArrayList<>();

    // 화면에 리스트를 출력하기 위한 뷰
    private ListView __listView;

    // 그룹 리스트 어댑터
    private UserListAdapter __userListAdapter;

    // 화면 하단의 +버튼
    Button __addingButton;

    CreatingUserDialogBuilder __creatingUserDlg;
    RemovingUserDialogBuilder __removingUserDlg;

    private LoadingDialogViewer __loadingDlgViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // UI 초기화
        __initUI();

        // 레퍼런스 초기화
        __getContentReferences();

        // 각종 컨텐츠 초기화
        __initContents();

        // 데이터베이스 초기화
        __initDB();

        // DB 로딩이 완료될 때 까지 로딩 다이얼로그 출력
        __loadingDlgViewer.show("데이터베이스 로딩", "데이터베이스 정보를 불러오는 중입니다..", 2);
    }

    private void __initUI()
    {
        setTitle("사용자 관리");
        setContentView(R.layout.activity_manager_user_wonjin);
    }

    private void __getContentReferences()
    {
        __listView = findViewById(R.id.ACTIVITY_MANAGING_USER_LIST_VIEW_listView);
        __addingButton = findViewById(R.id.ACTIVITY_MANAGING_USER_BUTTON_adding);
    }

    private void __initContents()
    {
        __userListAdapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, __userList);
        __listView.setAdapter(__userListAdapter);

        __creatingUserDlg = new CreatingUserDialogBuilder(this);
        __removingUserDlg = new RemovingUserDialogBuilder(this);

        // 하단의 +버튼을 클릭 시 그룹 생성 다이얼로그가 뜨게 한다.
        __addingButton.setOnClickListener(view -> __creatingUserDlg.show());

        // 리스트 뷰의 아이템(사용자 정보)을 long click 시 사용자 삭제 다이얼로그가 뜨게 한다.
        __listView.setOnItemLongClickListener((parent, view, position, id) ->
        {
            User user = __userList.get(position);
            __removingUserDlg.show(user);

            return true;
        });

        // 유저 생성 다이얼로그에서 생성 버튼을 클릭한 경우
        __creatingUserDlg.setOnOkListener(userInfo ->
        {
            // 사용자 이름을 입력하지 않은 경우
            if (userInfo.userName.isEmpty())
            {
                Message.information(
                        this, "사용자 생성 불가", "사용자 이름을 입력하세요.");

                return;
            }

            // 중복되는 사용자 이름을 입력한 경우
            if (__userList.stream().anyMatch(user -> user.isMeByName(userInfo.userName)))
            {
                Message.information(
                        this, "사용자 생성 불가", "해당 이름의 사용자가 이미 존재합니다.");

                return;
            }

            // 비밀번호를 입력하지 않은 경우
            if (userInfo.userPassword.isEmpty())
            {
                Message.information(
                        this, "사용자 생성 불가", "비밀번호를 입력하세요.");

                return;
            }

            __userList.add(userInfo);
            __databaseBroker.saveUserDatabase(this, __userList);

            Message.information(
                    this, "사용자 생성 완료", "새로운 사용자가 생성되었습니다.");
        });

        // 사용자 삭제 다이얼로그에서 삭제 버튼을 클릭한 경우
        __removingUserDlg.setOnOkListener(targetUser ->
        {
            // 선택한 유저의 정보 삭제
            __userList.removeIf(user -> user.isMeByName(targetUser.userName));
            __databaseBroker.saveUserDatabase(this, __userList);

            Message.information(
                    this, "사용자 삭제 완료", "사용자 삭제가 완료되었습니다.");
        });

        __loadingDlgViewer = new LoadingDialogViewer(this);
    }

    private void __initDB()
    {
        // 데이터베이스 브로커 생성
        __databaseBroker = DatabaseBroker.createDatabaseObject(_dbRootPath);

        // 그룹에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setGroupOnDataBrokerListener(this, str ->
        {
            if (__groupList.isEmpty())
                __loadingDlgViewer.loadingFinished();

            __groupList = __databaseBroker.loadGroupDatabase(this);
            __creatingUserDlg.updateGroupList(__groupList);
        });

        __databaseBroker.setUserOnDataBrokerListener(this, str ->
        {
            if (__userList.isEmpty())
                __loadingDlgViewer.loadingFinished();

            __userList = __databaseBroker.loadUserDatabase(this);

            __userListAdapter.clear();
            __userListAdapter.addAll(__userList);
        });

        // 사용자에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
    }

    @Override
    protected ActivityType _getActivityType()
    {
        return ActivityType.USER;
    }
}