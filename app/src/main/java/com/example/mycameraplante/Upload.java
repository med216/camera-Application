package com.example.mycameraplante;

public class Upload {

    private String mName;
    private String mImageUrl;

    public Upload( ) {
        //empty constructor needed
    }

    public Upload(String imageUrl) {

        mImageUrl = imageUrl;
    }



    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
