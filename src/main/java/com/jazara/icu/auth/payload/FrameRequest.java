package com.jazara.icu.auth.payload;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class FrameRequest {
    @NotBlank
    private Long camid;

    private List<List<Double>> faces;

    private Boolean fire;

    private Boolean violance;

    private Boolean fall;

    private Boolean motion;

    public Long getCamid() {
        return camid;
    }

    public void setCamid(Long camid) {
        this.camid = camid;
    }

    public List<List<Double>> getFaces() {
        return faces;
    }

    public void setFaces(List<List<Double>> faces) {
        this.faces = faces;
    }

    public Boolean getFire() {
        return fire;
    }

    public void setFire(Boolean fire) {
        this.fire = fire;
    }

    public Boolean getViolance() {
        return violance;
    }

    public void setViolance(Boolean violance) {
        this.violance = violance;
    }

    public Boolean getFall() {
        return fall;
    }

    public void setFall(Boolean fall) {
        this.fall = fall;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

}