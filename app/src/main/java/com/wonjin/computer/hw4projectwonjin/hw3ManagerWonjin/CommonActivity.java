package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.hw1LoginWonjin.LoginWonjinActivity;

// 여러 액티비티에서 공통으로 사용되는 로직을 통합한 상위 액티비티 클래스
public abstract class CommonActivity extends AppCompatActivity
{
    // DB 루트 경로
    protected String _dbRootPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 이전 액티비티(로그인)로부터 전달받은 정보를 복원
        __retrieveValues();
    }

    private void __retrieveValues()
    {
        // 로그인 액티비티로부터 계정 정보를 전달받음.
        Bundle receivedData = getIntent().getExtras();
        _dbRootPath = receivedData.getString("db_root_path");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_manager_wonjin, menu);

        int currentItemId = 0;
        switch (_getActivityType())
        {
            case GROUP:
                currentItemId = R.id.MENU_MANAGER_managingGroup;
                break;

            case USER:
                currentItemId = R.id.MENU_MANAGER_managingUser;
                break;

            case SETTINGS:
                currentItemId = R.id.MENU_MANAGER_managingSettings;
                break;
        }

        // 현태 선택한 메뉴 정보에 따라 해당 액션을 disable 시킴
        menu.findItem(currentItemId).setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = null;

        switch (item.getItemId())
        {
            case R.id.MENU_MANAGER_managingGroup:
                intent = new Intent(this, ManagingGroupActivity.class);
                intent.putExtra("db_root_path", _dbRootPath);
                break;

            case R.id.MENU_MANAGER_managingUser:
                intent = new Intent(this, ManagingUserActivity.class);
                intent.putExtra("db_root_path", _dbRootPath);
                break;

            case R.id.MENU_MANAGER_managingSettings:
                intent = new Intent(this, ManagingSettingsActivity.class);
                intent.putExtra("db_root_path", _dbRootPath);
                break;

            case R.id.MENU_MANAGER_logout:
                intent = new Intent(this, LoginWonjinActivity.class);
                break;
        }

        // 선택한 메뉴 아이템에 따라 액티비티를 새로 띄운다.
        startActivity(intent);

        // 액티비티 변경 뒤 이전 액티비티 종료
        finish();

        return super.onOptionsItemSelected(item);
    }

    // 이 클래스를 상속한 액티비티는 자신의 액티비티 타입을 알려줄 수 있어야 함
    protected abstract ActivityType _getActivityType();
}
