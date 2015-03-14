package com.nitzandev.fyberapp.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nitzanwerber on 3/12/15.
 * Response model from the api request that holds a list of offers
 */
public class Response implements Serializable{

    @SerializedName("offers")
    private Offer[] offers = new Offer[0];

    public Offer[] getOffers() {
        return offers;
    }

    public class Offer implements Serializable {

        @SerializedName("title")
        private String title = "";

        @SerializedName("teaser")
        private String teaser = "";

        @SerializedName("thumbnail")
        private thumbnail thumbnail = new thumbnail();

        @SerializedName("payout")
        private int payout = -1;

        public String getTitle() {
            return title;
        }

        public String getTeaser() {
            return teaser;
        }

        public Offer.thumbnail getThumbnail() {
            return thumbnail;
        }

        public int getPayout() {
            return payout;
        }

        public class thumbnail implements Serializable{
            @SerializedName("hires")
            private String hires = "";

            public String getHires() {
                return hires;
            }
        }

        @Override
        public String toString() {
            return title + " " + teaser + " " + payout + " " + thumbnail.getHires();
        }
    }
}
