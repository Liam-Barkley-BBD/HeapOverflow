package com.heapoverflow.gui;
import com.heapoverflow.services.AuthServices;
import com.heapoverflow.utils.TextUtils;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class Gui {
    public static void renderWelcomeText(){
        System.out.println(
            new AttributedString(
                TextUtils.getWelcomeMessage(), 
                AttributedStyle.BOLD_OFF.foreground(AttributedStyle.BLUE))
            .toAnsi());
    }

    public static void renderHelpMenu(){
        Boolean isLoggedIn = AuthServices.isLoggedIn().join(); // This blocks until the result is available

        if(isLoggedIn){
            System.out.println(TextUtils.getHelpMenuAuth());  
        } else {
            System.out.println(TextUtils.getHelpMenuNoAuth()); 
        }
    }

    public static void renderBye(){
        System.out.println(
            new AttributedString(
                TextUtils.getByeMessage(), 
                AttributedStyle.BOLD_OFF.foreground(AttributedStyle.BLUE))
            .toAnsi());
    }
}

                                                                                                                                          

