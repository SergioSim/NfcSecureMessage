package utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PasswordCheck {

    public static boolean isValid(String pass1, String pass2, Context context, Button button){
        if( pass1.equals("") || pass2.equals("")) {
            badPassMessage("No password entered", context, button);
        }else if(!pass1.equals(pass2)) {
            badPassMessage("First password don't matches second", context, button);
        }else {
            return true;
        }
        return false;
    }

    public static boolean isDifficult(String password, Context context, Button button){
        if(password.length() < 8){
            badPassMessage("Password is not eight characters long.", context, button);
            return false;
        }
        String upperCase = "(.*[A-Z].*)";
        if(!password.matches(upperCase)){
            badPassMessage("Password must contain at least one capital letter.", context, button);
            return false;
        }
        String numbers = "(.*[0-9].*)";
        if(!password.matches(numbers)){
            badPassMessage("Password must contain at least one number.", context, button);
            return false;
        }
        String specialChars = "(.*[ ! # @ $ % ^ & * ( ) - _ = + [ ] ; : ' \" , < . > / ?].*)";
        if(!password.matches(specialChars)){
            badPassMessage("Password must contain at least one special character.", context, button);
            return false;
        }
        String space = "(.*[   ].*)";
        if(password.matches(space)){
            badPassMessage("Password cannot contain a space.", context, button);
            return false;
        }
        if(password.startsWith("?")){
            badPassMessage("Password cannot start with '?'.", context, button);
            return false;
        }
        if(password.startsWith("!")){
            badPassMessage("Password cannot start with '!'.", context, button);
            return false;
        }
        return true;
    }

    public static void badPassMessage(String message, Context context, Button button){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        setButtonColor(Color.RED, button);
    }

    public static void setButtonColor(int i, Button button){
        button.setBackgroundColor(i);
        Timer timer = new Timer("ButtonTimer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                button.setBackgroundColor(Color.LTGRAY);
            }
        }, 1000);
    }
}
