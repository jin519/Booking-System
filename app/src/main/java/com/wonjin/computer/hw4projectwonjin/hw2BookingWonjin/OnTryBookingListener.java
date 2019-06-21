package com.wonjin.computer.hw4projectwonjin.hw2BookingWonjin;

// UI 상의 예약 정보를 선택하여 클릭 이벤트가 발생한 경우, 세부 처리 루틴을 구현하는 리스너
public interface OnTryBookingListener
{
    void onTryBooking(CellLinearLayout cell, final int timeIndex);
}
