package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

import android.content.Context;
import android.widget.TextView;

// 시각 정보를 나타내기 위한 TextView
public class TimeTextView extends TextView
{
    public final int MAX_TIME_COUNT;

    public TimeTextView(Context context, int maxTimeCount)
    {
        super(context);
        MAX_TIME_COUNT = maxTimeCount;
    }

    // 입력 받은 index를 기준으로 시각 정보를 파싱한다.
    private String __parseTime(final int timeIndex)
    {
        if (timeIndex < 0 || (timeIndex >= MAX_TIME_COUNT))
            return "N / A";

        return String.format("%02d:%02d", timeIndex / 2, (timeIndex % 2 == 0) ? 0 : 30);
    }

    // 입력 받은 index를 기준으로 시각 텍스트를 설정한다.
    public void setTime(final int timeIndex)
    {
        setText(__parseTime(timeIndex));
    }
}
