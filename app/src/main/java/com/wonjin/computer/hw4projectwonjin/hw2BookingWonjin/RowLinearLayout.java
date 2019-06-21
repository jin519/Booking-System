package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

// 하나의 행을 시각화 하기위한 LinearLayout이다.
// 핸드폰의 Orientation에 따라 행과 열이 다르게 표현되어야 하므로 WeightSum이 달라지게 된다.
public class RowLinearLayout extends LinearLayout {

    // UI 배치를 위한 스타일
    static final private LayoutParams STYLE;

    // 자식 뷰 배치를 위한 스타일
    static final private LayoutParams CONTENT_STYLE;

    static
    {
        STYLE = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        CONTENT_STYLE = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.f);
        CONTENT_STYLE.setMargins(5, 0, 5, 5);
    }

    public RowLinearLayout(final Context context)
    {
        super(context);

        // 수평 배치
        setOrientation(HORIZONTAL);
        setLayoutParams(STYLE);

        // weightSum은 기본 2로 설정
        setWeightSum(2.f);
        setPadding(5, 5, 5, 5);
    }

    // 자식 뷰는 항상 RowLinearLayout 고유 스타일로 추가
    @Override
    public void addView(View child) {
        super.addView(child, CONTENT_STYLE);
    }

    // 열의 수에 따라 WeightSum 조정
    public void setColumns(final int numColumns)
    {
        setWeightSum(numColumns);
    }
}
