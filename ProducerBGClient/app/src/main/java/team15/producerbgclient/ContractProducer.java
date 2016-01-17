package team15.producerbgclient;

/**
 * Created by veso on 1/17/2016.
 */
public class ContractProducer {

    private String _id;
    private String name;
    private String type;
    private byte[] logo;

    public ContractProducer(String _id, String name, String type, byte[] logo) {
        this._id = _id;
        this.name = name;
        this.type = type;
        this.logo = logo;
    }

    public ContractProducer() {}

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}
