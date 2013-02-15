package ari.dnrs.rtk.addon.bean;

/**
 * Models the key value element specified in an EPP Domain
 * Create, Update and Info EPP commands.
 */
public final class DomainKeyValueBean {

    private final String key;
    private final String value;

    /**
     * Constructs a key value pair
     * 
     * @param key the key of the pair
     * @param value the value associated with the key
     */
    public DomainKeyValueBean(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the value of the key value pair
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the key to the key value pair
     */
    public String getKey() {
        return key;
    }
}
