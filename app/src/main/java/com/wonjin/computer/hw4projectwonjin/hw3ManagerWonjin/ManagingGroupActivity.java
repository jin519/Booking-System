package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.DatabaseBroker;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.LoadingDialogViewer;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Message;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;
import com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog.CreatingGroupDialogBuilder;
import com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin.dialog.RemovingGroupDialogBuilder;

import java.util.ArrayList;

public class ManagingGroupActivity extends CommonActivity
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
    private ArrayAdapter<String> __groupListAdapter;

    // 화면 하단의 +버튼
    private Button __addingButton;

    private CreatingGroupDialogBuilder __creatingGroupDlg;
    private RemovingGroupDialogBuilder __removingGroupDlg;

    private LoadingDialogViewer __loadingDlgViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI 초기화
        __initUI();

        // UI 컨텐츠의 레퍼런스를 얻는다.
        __getContentReferences();

        // 각종 컨텐츠 초기화
        __initContents();

        // 데이터베이스 초기화
        __initDB();

        // DB 로딩이 완료될 때 까지 로딩 다이얼로그 출력
        __loadingDlgViewer.show("데이터베이스 로딩", "데이터베이스 정보를 불러오는 중입니다..", 1);
    }

    private void __initUI()
    {
        setTitle("그룹 관리");
        setContentView(R.layout.activity_manager_group_wonjin);
    }

    // UI 컨텐츠의 레퍼런스를 얻는다.
    private void __getContentReferences()
    {
        __listView = findViewById(R.id.ACTIVITY_MANAGING_GROUP_LIST_VIEW_listView);
        __addingButton = findViewById(R.id.ACTIVITY_MANAGING_GROUP_BUTTON_adding);
    }

    // 각종 컨텐츠 초기화
    private void __initContents()
    {
        __groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, __groupList);
        __listView.setAdapter(__groupListAdapter);

        __creatingGroupDlg = new CreatingGroupDialogBuilder(this);
        __removingGroupDlg = new RemovingGroupDialogBuilder(this);

        // 하단의 +버튼을 클릭 시 그룹 생성 다이얼로그가 뜨게 한다.
        __addingButton.setOnClickListener(view -> __creatingGroupDlg.show());

        // 리스트 뷰의 아이템(그룹명)을 long click 시 그룹 삭제 다이얼로그가 뜨게 한다.
        __listView.setOnItemLongClickListener((parent, view, position, id) ->
        {
            String contents =  __listView.getItemAtPosition(position).toString();
            __removingGroupDlg.show(contents);

            return true;
        });

        // 그룹 생성 다이얼로그에서 생성 버튼을 클릭한 경우
        __creatingGroupDlg.setOnOkListener(contents ->
        {
            // 그룹명을 입력하지 않은 경우
            if (contents.isEmpty())
            {
                Message.information(
                        this, "그룹 생성 불가", "그룹명을 입력하세요.");

                return;
            }

            // 중복되는 그룹명을 입력한 경우
            if (__groupList.stream().anyMatch(group -> group.equals(contents)))
            {
                Message.information(
                        this, "그룹 생성 불가", "해당 이름의 그룹이 이미 존재합니다.");

                return;
            }

            __groupList.add(contents);
            __databaseBroker.saveGroupDatabase(ManagingGroupActivity.this, __groupList);

            Message.information(
                    this, "그룹 생성 완료", "새로운 그룹이 생성되었습니다.");
        });

        // 그룹 삭제 다이얼로그에서 삭제 버튼을 클릭한 경우
        __removingGroupDlg.setOnOkListener(targetGroupName ->
        {
            // 선택한 그룹명을 그룹 리스트에서 제거
            __groupList.removeIf(group -> group.equals(targetGroupName));

            // 해당 그룹에 소속된 모든 유저 정보를 삭제
            __userList.removeIf(user -> user.isMeByGroup(targetGroupName));

            __databaseBroker.saveGroupDatabase(this, __groupList);
            __databaseBroker.saveUserDatabase(this, __userList);

            Message.information(
                    this, "그룹 삭제 완료", "해당 그룹의 삭제가 완료되었습니다.");
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

            __groupListAdapter.clear();
            __groupListAdapter.addAll(__groupList);
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
    protected ActivityType _getActivityType()
    {
        return ActivityType.GROUP;
    }
}