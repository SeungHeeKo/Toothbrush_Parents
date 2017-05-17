package com.tektonspace.toothbrush_parents.adapter;

import android.graphics.drawable.Drawable;

import java.net.URI;

/**
 * Created by SeungHee on 2017-04-14.
 * 생성된 아이 정보를 담는 변수들
 */

public class ChildSelectListViewItem {
    private int characterLayout;
    private Drawable childCharacter;
    private String childPhoto ;
    private String childName ;
    private String nickName ;

    public void setCharacterLayout(int layoutCharacter){characterLayout = layoutCharacter;}
    public void setChildCharacter(Drawable character) {
        childCharacter = character ;
    }
    public void setChildPhoto(String photo) {
        childPhoto = photo ;
    }
    public void setChildName(String name) {
        childName = name ;
    }
    public void setNickName(String nick) {
        nickName = nick ;
    }

    public int getCharacterLayout(){return this.characterLayout;}
    public Drawable getChildCharacter() {
        return this.childCharacter ;
    }
    public String getChildPhoto() {
        return this.childPhoto ;
    }
    public String getChildName() {
        return this.childName ;
    }
    public String getNickName() {
        return this.nickName ;
    }
}
