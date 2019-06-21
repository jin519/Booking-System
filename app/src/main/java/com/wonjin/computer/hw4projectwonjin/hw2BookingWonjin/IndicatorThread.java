package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

import java.util.ArrayList;
import java.util.Calendar;

// 현재 시간과 향후 시간 흐름에 따라 UI를 갱신하는 스레드
public class IndicatorThread extends Thread
{
    public final int MAX_TIME_COUNT;

    // 현재 UI에서 유지 중인 Cell 객체들의 리스트
    private ArrayList<CellLinearLayout> __cellList;

    // 시간이 흘러 다음 날로 변경되는 경우, 이 이벤트를 처리할 객체
    private OnDayChangeListener __onDayChangeListener;

    public IndicatorThread(ArrayList<CellLinearLayout> cellList, int maxTimeCount)
    {
        super();
        __cellList = cellList;
        MAX_TIME_COUNT = maxTimeCount;
    }

    // 현재 시각과 분을 기준으로 화면 상에 해당하는 index를 계산한다.
    private int __calcTimeIndex(int hour, int minute)
    {
        return ((hour * 2) + ((minute < 30) ? 0 : 1));
    }

    // 함수에 주어진 인덱스를 기준으로 이전 인덱스는 비활성화, 이후 인덱스는 활성화 (UI 갱신용)
    private void __indicate(final int timeIndex)
    {
        int adjTimeIdx = timeIndex;
        if (adjTimeIdx < 1)
            adjTimeIdx += 48;

        for (int i = 0; i < __cellList.size(); i++)
            __cellList.get(i).setActive((i > adjTimeIdx) && (i < MAX_TIME_COUNT));
    }

    // 이벤트 리스너 설정용 함수
    public void setOnDayChangeListener(OnDayChangeListener listener)
    {
        __onDayChangeListener = listener;
    }

    @Override
    public void run() {

        // 현재 시간을 얻어온다.
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // 현재 시간을 기준으로 인덱스 계산
        int timeIndex = __calcTimeIndex(hour, minute);

        // 현재 시간이 나타내는 인덱스 기준으로 Cell을 활성화하거나 비활성화
        __indicate(timeIndex);

        // 다음 30분 정각까지 남은 분과 시간 계산
        int remainedMinute = ((60 - (minute + 1)) % 30);
        int remainedSecond = (60 - (second + 1));

        // 다음 30분 정각까지 스레드 정지
        try {
            sleep((remainedMinute * 60 + remainedSecond) * 1000);
        } catch (InterruptedException e) {
            return;
        }

        // 매 30분을 세기 위한 변수. 초기 값은 30이다.
        int minuteCounter = 30;
        while (true)
        {
            // minuteCounter가 30 이상인 경우
            if (minuteCounter >= 30)
            {
                // 다시 30을 감소 시킴
                minuteCounter -= 30;

                // timeIndex를 다음으로 증가시킨다.
                // 이는 향후 UI 갱신 시 다음 슬롯을 비활성화하기 위함이다.
                timeIndex++;

                // timeIndex가 MAX_TIME_COUNT(현재 50)보다 커진 경우는 새벽 1시를 넘은 경우이다.
                // 이 경우 다음 날을 위한 화면으로 전환한다.
                if (timeIndex >= MAX_TIME_COUNT)
                {
                    // 30분 * 48 = 24시간 이므로, 48을 감소시킨다.
                    timeIndex -= 48;

                    // DayChange 이벤트 핸들러가 등록되어 있는 경우 콜백
                    if (__onDayChangeListener != null)
                        __onDayChangeListener.onDayChange();
                }

                // UI 갱신
                __indicate(timeIndex);
            }

            // 1분 대기
            try {
                sleep(60 * 1000); // 1min
            } catch (InterruptedException e) {
                break;
            }

            // 1분 이후 minuteCounter 증가
            minuteCounter++;
        }
    }
}
