package ari.dnrs.rtk.addon.bean;

public class ClaimsInfoBean {

    private final Boolean claim;
    private final String claimsKey;

    public ClaimsInfoBean(Boolean claim, String claimsKey) {
        this.claim = claim;
        this.claimsKey = claimsKey;
    }

    public Boolean claim() {
        return claim;
    }

    public String getClaimsKey() {
        return claimsKey;
    }
}
