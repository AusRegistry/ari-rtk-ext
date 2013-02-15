package ari.dnrs.rtk.addon.bean;

/**
 * Models the &lt;variant&gt; element specified in a variant extension of an EPP Domain
 * Create/Info response, as documented in 'Variant Extension Mapping for the
 * Extensible Provisioning Protocol'.
 */
public final class DomainVariantBean {

    private final String name;
    private final String userForm;

    /**
     * Constructs an IDNA domain variant
     * 
     * @param name the DNS form of the domain name
     * @param userForm the user form of the domain name
     */
    public DomainVariantBean(final String name, final String userForm) {
        this.userForm = userForm;
        this.name = name;
    }

    /**
     * @return the DNS form of the IDNA domain name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the user form of the IDNA domain name
     */
    public String getUserForm() {
        return userForm;
    }
}
