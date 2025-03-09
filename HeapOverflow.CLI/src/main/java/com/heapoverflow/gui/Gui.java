package com.heapoverflow.gui;
import com.heapoverflow.services.AuthService;
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
        AuthService.isLoggedIn().thenAccept(isLoggedIn -> {
            if(isLoggedIn){
              System.out.println(TextUtils.getHelpMenuAuth());  
            } else{
            System.out.println(TextUtils.getHelpMenuNoAuth()); 
            }
        }).exceptionally(ex -> {
            System.err.println("Authentication failed: " + ex.getMessage());
            return null;
        });
    }

    public static void renderBye(){
        System.out.println(TextUtils.getByeMessage());
    }
}

                                                                                                                                          

