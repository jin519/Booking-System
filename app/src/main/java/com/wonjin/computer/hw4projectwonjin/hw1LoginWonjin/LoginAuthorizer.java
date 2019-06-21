package com.wonjin.computer.hw4projectwonjin.hw1LoginWonjin;

import com.wonjin.computer.hw4projectwonjin.commonWonjin.User;
import java.util.ArrayList;

public class LoginAuthorizer
{
    static int authorize(final ArrayList<User> userList, final User target)
    {
        if (target.userName.isEmpty())
            // empty name
            return -1;
        else if (target.userPassword.isEmpty())
            // empty password
            return -2;

        for (int i = 0; i < userList.size(); i++)
        {
            User user = userList.get(i);

            if (user.isMeByName(target.userName))
            {
                if (!user.isMeByPassword(target.userPassword))
                    // wrong password
                    return -3;

                if (!target.userName.equals("root") && !user.isMeByGroup(target.userGroup))
                    // wrong group
                    return -4;

                // valid
                return i;
            }
        }

        // non existent id
        return -5;
    }
}
