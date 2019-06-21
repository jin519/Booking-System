package com.wonjin.computer.hw4projectwonjin.hw3ManagerWonjin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.wonjin.computer.hw4projectwonjin.R;
import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;

import java.util.List;

// 유저 정보를 리스트 뷰에 출력하기 위한 커스텀 어댑터
public class UserListAdapter extends ArrayAdapter<User>
{
    LayoutInflater __layoutInflater;

    public UserListAdapter(Context context, int resource, List<User> objects)
    {
        super(context, resource, objects);

        __layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // 이미 생성된 뷰 객체는 재활용
        if (convertView == null)
            convertView = __layoutInflater.inflate(R.layout.listview_item_user_wonjin, parent, false);

        TextView nameView = convertView.findViewById(R.id.LISTVIEW_ITEM_USER_TEXT_VIEW_name);
        TextView passwordView = convertView.findViewById(R.id.LISTVIEW_ITEM_USER_TEXT_VIEW_password);
        TextView groupView = convertView.findViewById(R.id.LISTVIEW_ITEM_USER_TEXT_VIEW_group);

        // 커스텀 리스트 뷰 아이템에 정보를 채워 넣는다.
        User userInfo = getItem(position);
        nameView.setText(userInfo.userName);
        passwordView.setText(userInfo.userPassword);
        groupView.setText(userInfo.userGroup);

        return convertView;
    }
}
