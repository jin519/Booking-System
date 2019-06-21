package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.DatabaseBroker;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.LoadingDialogViewer;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Message;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Settings;

public class ManagingSettingsActivity extends CommonActivity
{
    public static final String[] TIME_SLOT_CAPTION_LISTS;

    static
    {
        TIME_SLOT_CAPTION_LISTS = new String[21];

        for (int i = 1; i < (TIME_SLOT_CAPTION_LISTS.length + 1); i++)
            TIME_SLOT_CAPTION_LISTS[i - 1] = String.format("%02d:%02d", i / 2, (i % 2) * 30);
    }

    // 데이터베이스 연동을 위한 DatabaseBroker 객체
    private DatabaseBroker __databaseBroker;

    // Setting 정보가 담겨있는 객체
    private Settings __settings = null;

    // 1회 최대로 가능한 연속 예약 정보를 표시하기 위한 Spinner 레퍼런스
    private Spinner __maxContinueSpinner;

    // maxContinueSpinner를 위한 어댑터
    private ArrayAdapter<String> __maxContinueAdapter;

    // 1일 최대로 가능한 예약 정보를 표시하기 위한 Spinner 레퍼런스
    private Spinner __maxTotalSpinner;

    // maxTotalSpinner를 위한 어댑터
    private ArrayAdapter<String> __maxTotalAdapter;

    private LoadingDialogViewer __loadingDlgViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        setTitle("설정 관리");
        setContentView(R.layout.activity_manager_settings_wonjin);
    }

    private void __getContentReferences()
    {
        __maxContinueSpinner = findViewById(R.id.DIALOG_MANAGER_NEW_USER_SPINNER_maxContinue);
        __maxTotalSpinner = findViewById(R.id.ACTIVITY_MANAGING_SETTINGS_SPINNER_maxTotal);
    }

    @Override
    protected ActivityType _getActivityType() {
        return ActivityType.SETTINGS;
    }

    private void __initContents()
    {
        __maxContinueAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, TIME_SLOT_CAPTION_LISTS);

        __maxContinueSpinner.setAdapter(__maxContinueAdapter);

        __maxTotalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, TIME_SLOT_CAPTION_LISTS);

        __maxTotalSpinner.setAdapter(__maxTotalAdapter);

        __loadingDlgViewer = new LoadingDialogViewer(this);
    }

    private void __postInitContents()
    {
        // 1회 연속 예약 가능 시간 설정 이벤트
        __maxContinueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int tmpSlots = (position + 1);
                if (tmpSlots > __settings.maxTotalBookingSlots)
                {
                    int prevIdx = (__settings.maxContinueBookingSlots - 1);
                    __maxContinueSpinner.setSelection(prevIdx);

                    Message.information(
                            ManagingSettingsActivity.this,
                            "설정 변경 불가",
                            "1회 연속 예약 가능 시간은 1일 최대 예약 가능 시간을 초과하여 설정할 수 없습니다.");

                    return;
                }

                __settings.maxContinueBookingSlots = tmpSlots;
                __databaseBroker.saveSettingsDatabase(ManagingSettingsActivity.this, __settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 1일 최대 예약 가능 시간 설정 이벤트
        __maxTotalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int tmpSlots = (position + 1);
                if (tmpSlots < __settings.maxContinueBookingSlots)
                {
                    int prevIdx = (__settings.maxTotalBookingSlots - 1);
                    __maxTotalSpinner.setSelection(prevIdx);

                    Message.information(
                            ManagingSettingsActivity.this,
                            "설정 변경 불가",
                            "1일 최대 예약 가능 시간은 1회 연속 예약 가능 시간보다 작을 수 없습니다.");

                    return;
                }

                __settings.maxTotalBookingSlots = (position + 1);
                __databaseBroker.saveSettingsDatabase(ManagingSettingsActivity.this, __settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void __initDB()
    {
        // 데이터베이스 브로커 생성
        __databaseBroker = DatabaseBroker.createDatabaseObject(_dbRootPath);

        // 설정 정보에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setSettingsOnDataBrokerListener(this, str ->
        {
            if (__settings == null)
            {
                __loadingDlgViewer.loadingFinished();
                __postInitContents();
            }

            __settings = __databaseBroker.loadSettingsDatabase(this);

            __maxContinueSpinner.setSelection(__settings.maxContinueBookingSlots - 1);
            __maxTotalSpinner.setSelection(__settings.maxTotalBookingSlots - 1);
        });
    }
}