package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

// 하나의 예약 정보를 시각화 하기 위한 클래스
public class CellLinearLayout extends LinearLayout
{
    // 자식 뷰에 대한 Layout style
    static final private LayoutParams CONTENT_STYLE;

    public final int MAX_TIME_COUNT;

    // 시간 정보를 표현하기 위한 TextView의 서브 클래스
    TimeTextView timeView;

    // 예약자명을 표현하기 위한 TextView
    TextView nameView;

    // 현재 인스턴스의 화면 상 위치 (좌상단부터 우하단 순으로 증가)
    final public int TIME_INDEX;

    // 클릭 이벤트 발생 시 처리 루틴을 대행할 인스턴스. MainActivity가 이를 수행한다.
    private OnTryBookingListener __onTryBookingListener;

    // 현재 시간에 따라 활성화 여부를 나타내기 위한 변수
    private boolean __active;

    static
    {
        CONTENT_STYLE = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.f);
    }

    public CellLinearLayout(BookingWonjinActivity context, final int maxTimeCount, final int timeIndex)
    {
        super(context);

        MAX_TIME_COUNT = maxTimeCount;

        // 수평 배치
        setOrientation(HORIZONTAL);

        // 시간 정보와 예약자 정보만을 담으므로 weightSum은 2로 설정
        setWeightSum(2.f);

        // 패딩 부여
        setPadding(5, 5, 5, 5);

        TIME_INDEX = timeIndex;

        // TimeTextView 객체 생성
        timeView = new TimeTextView(context, MAX_TIME_COUNT);

        // 인덱스를 기준으로 나타낼 시간을 내부적으로 계산한다.
        timeView.setTime(timeIndex);

        // nameView 생성
        nameView = new TextView(context);

        // timeView와 nameView를 현재 인스턴스(레이아웃) 상에 배치
        addView(timeView, CONTENT_STYLE);
        addView(nameView, CONTENT_STYLE);

        // MainActivity를 리스너로 자동 등록
        __onTryBookingListener = context;
        setOnClickListener(v -> __onTryBookingListener.onTryBooking(this, TIME_INDEX));
    }

    // 현재 인스턴스가 예약 상태인지 여부
    public boolean isBooked()
    {
        return (nameView.getText().length() != 0);
    }

    // 현재 인스턴스를 예약 상태로 변경한다. 예약자 정보를 받는다.
    public void book(final String booker)
    {
        nameView.setText(booker);
    }

    // 현재 예약자 명을 반환한다.
    public String getBooker()
    {
        return nameView.getText().toString();
    }

    // 예약 상태를 제거한다.
    public void unbook()
    {
        nameView.setText("");
    }

    // 활성화 상태를 설정한다. 이는 IndicatorThread가 시간 경과에 따라 자동 호출한다.
    public void setActive(final boolean active)
    {
        __active = active;

        if (active)
            setBackgroundColor(Color.WHITE);
        else
            setBackgroundColor(Color.CYAN);
    }

    // 활성화 여부를 반환한다.
    public boolean isActive()
    {
        return __active;
    }
}
