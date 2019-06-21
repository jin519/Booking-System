package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.DatabaseBroker;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.LoadingDialogViewer;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Message;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.Settings;
import com.wonjin.computer.hw4projectwonjin.hw1LoginWonjin.LoginWonjinActivity;

import java.util.ArrayList;

public class BookingWonjinActivity extends AppCompatActivity implements OnTryBookingListener, OnDayChangeListener
{
    // 화면에 표시할 최대 시간 슬롯의 수
    private int __maxTimeCount;

    // DB 루트 경로
    private String __dbRootPath;

    // 사용자 그룹 및 이름
    private String __userGroup;
    private String __userName;

    // 데이터베이스 연동을 위한 DatabaseBroker 객체
    private DatabaseBroker __databaseBroker;

    // 50개 예약 슬롯에 대한 예약자명 정보를 저장하는 스트링 배열
    private String[] __bookingList = null;

    // 예약 정책 정보를 담는 Settings 클래스의 인스턴스
    private Settings __settings = null;

    // 메인 레이아웃
    private LinearLayout __mainLayout;
    private TextView __userNameView;

    // 50개 예약 정보를 시각화 하기위한 LinearLayout 객체 배열
    private ArrayList<CellLinearLayout> __cellList;

    // 시간 흐름에 따라 UI를 갱신하기 위한 thread
    private IndicatorThread __indicatorThread;

    private LoadingDialogViewer __loadingDlgViewer;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_wonjin);

        // 이전 액티비티(로그인)로부터 전달받은 정보를 복원
        __retrieveValues();

        // UI 컨텐츠의 레퍼런스를 얻는다.
        __getContentReferences();

        // 각종 컨텐츠 초기화
        __initContents();

        // 타이틀명을 user group 명으로 갱신
        setTitle(String.format("부킹: %s", __userGroup));

        // 예약 정보를 표시하기 위한 테이블을 빌드
        __buildTable(getResources().getConfiguration().orientation);

        // 데이터베이스 초기화
        __initDB();

        // DB 로딩이 완료될 때 까지 로딩 다이얼로그 출력
        __loadingDlgViewer.show("데이터베이스 로딩", "데이터베이스 정보를 불러오는 중입니다..", 2);
    }

    // UI 컨텐츠의 레퍼런스를 얻는다.
    private void __getContentReferences()
    {
        __mainLayout = findViewById(R.id.ACTIVITY_BOOKING_LINEAR_LAYOUT_mainLayout);
        __userNameView = findViewById(R.id.ACTIVITY_BOOKING_TEXT_VIEW_userName);
    }

    private void __initContents()
    {
        // 최대로 표시할 시간 슬롯의 개수를 리소스로부터 읽어옴.
        __maxTimeCount = getResources().getInteger(R.integer.max_time_count);
        __loadingDlgViewer = new LoadingDialogViewer(this);

        __userNameView.setText(__userName);
    }

    // 예약 정보를 표시하기 위한 테이블을 빌드하는 함수
    private void __buildTable(final int orientation)
    {
        // 예약 정보 배치에 대한 레이아웃을 계산한다. (세로: 2열 / 가로: 4열)
        int numRows, numColumns;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            numRows = ((__maxTimeCount / 2) + ((__maxTimeCount % 2 == 0) ? 0 : 1));
            numColumns = 2;
        }
        else
        {
            numRows = ((__maxTimeCount / 4) + ((__maxTimeCount % 4 == 0) ? 0 : 1));
            numColumns = 4;
        }

        // 예약 정보를 표시하는 CellLinearLayout 인스턴스들을 담는 리스트
        __cellList = new ArrayList<>();

        // 생성한 CellLinearLayout 인스턴스 개수를 세는 변수
        int counter = 0;

        // 위에서 계산한 행과 열 수에 대해 for loop
        for (int i = 0; i < numRows; i++)
        {
            // 하나의 행을 시각화 하기위한 LinearLayout
            RowLinearLayout row = new RowLinearLayout(this);

            // weightSum 계산을 위해 열의 수를 알려줌
            row.setColumns(numColumns);

            for (int j = 0; j < numColumns; j++)
            {
                // 하나의 예약 정보를 시각화 하기위한 CellLinearLayout 인스턴스의 생성
                CellLinearLayout cell = new CellLinearLayout(this, __maxTimeCount, counter);

                // cellList에 추가
                __cellList.add(cell);

                // 하나의 행을 나타내는 RowLinearLayout에도 추가
                row.addView(cell);

                // counter 증가
                counter++;
            }

            // 하나의 행 Layout을 완성하였으므로 mainLayout에 추가
            __mainLayout.addView(row);
        }

        // 여기까지 시각화를 위한 초기화 과정이 완료되었음.

        // 시간 흐름에 따라 UI를 동적으로 갱신하기 위한 스레드를 생성
        __indicatorThread = new IndicatorThread(__cellList, __maxTimeCount);

        // 이 스레드는 다음 날로 바뀔 때 처리 루틴을 대행하는 OnDayChangeListener 구현 인스턴스를 필요로 함.
        // MainActivity가 이를 처리
        __indicatorThread.setOnDayChangeListener(this);

        // 스레드 시작
        __indicatorThread.start();
    }

    @Override
    protected void onDestroy()
    {
        // MainActivity 종료 전 화면 갱신 스레드도 종료한다.
        __indicatorThread.interrupt();

        super.onDestroy();
    }

    // 연속으로 예약한 슬롯의 개수가 몇 개인지 센 뒤 반환한다.
    // 개수에는 현재 사용자가 선택하려는 슬롯을 포함한다.
    private int __calcBookingChainLength(final int timeIndex, final String userName)
    {
        // 기본 1개 (사용자가 선택하려는 슬롯)
        int length = 1;

        // 뒤로 얼마나 이어져 있는지 센다.
        int backwardIdx = (timeIndex - 1);
        while ((backwardIdx >= 0) && __bookingList[backwardIdx].equals(userName))
        {
            backwardIdx--;
            length++;
        }

        // 앞으로 얼마나 이어져 있는지 센다.
        int forwardIdx = (timeIndex + 1);
        while ((forwardIdx < __maxTimeCount) && __bookingList[forwardIdx].equals(userName))
        {
            forwardIdx++;
            length++;
        }

        return length;
    }

    // 특정 사용자를 기준으로 금일 예약 수를 센 뒤 반환한다.
    private int __getBookingCount(final String userName)
    {
        int retVal = 0;

        for (int i = 0; i < __maxTimeCount; i++)
            if (__bookingList[i].equals(userName))
                retVal++;

        return retVal;
    }

    // CellLinearLayout에서 클릭 이벤트 발생 시 내부적으로 콜백되는 메소드
    // cell은 클릭 이벤트가 발생한 인스턴스이고, timeIndex는 그 인스턴스의 인덱스이다.
    @Override
    public void onTryBooking(final CellLinearLayout cell, int timeIndex)
    {
        // MAX_TIME_COUNT 값에 따라서 유효하지 않은 시간을 나타내는 cell이 있다. 이 cell을 선택하는 경우 에러를 출력한다.
        if (timeIndex >= __maxTimeCount)
        {
            Message.information(this, "예약 불가", "해당 시각은 유효하지 않습니다.");
            return;
        }

        // 현재 시각보다 이전 시각 슬롯의 경우 비활성화된다. 비활성화 된 슬롯은 예약 변경을 수행할 수 없다.
        if (!cell.isActive())
        {
            Message.information(this, "예약 변경 불가", "해당 시각은 이미 지난 시각입니다.");
            return;
        }

        // 이미 예약이 된 상태라면 예약을 취소한다.
        if (cell.isBooked())
        {
            String booker = cell.getBooker();

            if (!booker.equals(__userName) && !__userName.equals("root"))
            {
                Message.information(
                        this, "예약 취소 불가", "다른 사람의 예약은 취소할 수 없습니다.");

                return;
            }

            cell.unbook();
            __bookingList[timeIndex] = "";
        }
        else
        {
            // 아래부터는 새로 예약을 하기 위한 로직이다.

            // 한 사용자가 연속으로 maxContinueBookingSlots회 이상 예약을 시도하는 경우 에러
            final int BOOKING_CHAIN_LENGTH = __calcBookingChainLength(timeIndex, __userName);
            if (BOOKING_CHAIN_LENGTH > __settings.maxContinueBookingSlots)
            {
                Message.information(
                        this, "예약 불가",
                        __userName + "님은 연속으로 "+ __settings.maxContinueBookingSlots + "회를 초과하여 예약을 하실 수 없습니다.");

                return;
            }

            // 한 사용자의 금일 예약 수가 maxTotalBookingSlots회 초과하려는 경우
            int BOOKING_COUNT = __getBookingCount(__userName);
            if (BOOKING_COUNT >= __settings.maxTotalBookingSlots)
            {
                Message.information(
                        this, "예약 한도 초과",
                        __userName + "님은 하루 최대 " + __settings.maxTotalBookingSlots + "회 까지만 예약이 가능합니다.");

                return;
            }

            // 이 시점까지 함수가 수행된다면 예약 가능 한 상태이므로 예약 수행
            __bookingList[timeIndex] = __userName;
        }

        // 데이터베이스 갱신
        __databaseBroker.saveBookingDatabase(this, __userGroup, __bookingList);
    }

    // IndicatorThread에 의한 콜백 함수. 시간 경과에 따라 날이 바뀌는 경우 호출된다.
    @Override
    public void onDayChange()
    {
        for (int i = 0; i < __maxTimeCount; i++)
        {
            final int OLD_IDX = (i + 48);

            // 전일에 선택한 24~25시 정보를 금일 0~1시로 이동
            if (OLD_IDX < __maxTimeCount)
                __bookingList[i] = __bookingList[OLD_IDX];

                // 나머지는 초기화
            else
                __bookingList[i] = "";
        }

        // 데이터베이스 갱신
        __databaseBroker.saveBookingDatabase(this, __userGroup, __bookingList);
    }

    private void __retrieveValues()
    {
        // 로그인 액티비티로부터 계정 정보를 전달받음.
        Bundle receivedData = getIntent().getExtras();
        __userGroup = receivedData.getString("user_group");
        __userName = receivedData.getString("user_name");
        __dbRootPath = receivedData.getString("db_root_path");
    }

    // 데이터베이스 초기화
    private void __initDB()
    {
        // 데이터베이스 브로커 생성
        __databaseBroker = DatabaseBroker.createDatabaseObject(__dbRootPath);

        // 예약에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setBookingOnDataBrokerListener(this, __userGroup, str ->
        {
            if (__bookingList == null)
                __loadingDlgViewer.loadingFinished();

            __bookingList = __databaseBroker.loadBookingDatabase(this, __userGroup);

            // 예약 DB가 업데이트 되면 테이블 상에 기록된 예약 정보도 갱신한다.
            for (int i = 0; i < __maxTimeCount; i++)
                __cellList.get(i).book(__bookingList[i]);
        });

        // 설정 정보에 대한 데이터베이스가 변경되었을 경우 콜백되는 리스너
        __databaseBroker.setSettingsOnDataBrokerListener(this, str ->
        {
            if (__settings == null)
                __loadingDlgViewer.loadingFinished();

            __settings = __databaseBroker.loadSettingsDatabase(this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_booking_wonjin, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.MENU_BOOKING_logout:
                Intent intent = new Intent(this, LoginWonjinActivity.class);
                startActivity(intent);

                // 액티비티 변경 뒤 이전 액티비티 종료
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}