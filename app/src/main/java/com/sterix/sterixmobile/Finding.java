package com.sterix.sterixmobile;

/**
 * Created by Ivan Escamos on 8/21/2017.
 */

public class Finding {

    private String image;
    private String finding;
    private String recommendation;
    private String timestamp;

    public Finding(String image, String finding, String recommendation) {
        this.image = image;
        this.finding = finding;
        this.recommendation = recommendation;
    }

    public Finding(String image, String finding, String recommendation,String timestamp) {
        this.image = image;
        this.finding = finding;
        this.recommendation = recommendation;
        this.timestamp = timestamp;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFinding() {
        return this.finding;
    }

    public void setFinding(String finding) {
        this.finding = finding;
    }

    public String getRecommendation() {
        return this.recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
