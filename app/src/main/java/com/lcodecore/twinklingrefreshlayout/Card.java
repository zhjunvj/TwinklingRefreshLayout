package com.lcodecore.twinklingrefreshlayout;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = -5376313495678563362L;

    public String title;
    public String info;
    public int imageSrc;

    public void setTitle(String title,String info){
        this.title = title;
        this.info = info;
    }
}