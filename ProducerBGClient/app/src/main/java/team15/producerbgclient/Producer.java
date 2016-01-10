package team15.producerbgclient;

/**
 * Created by Marina on 09/01/2016.
 */
public class Producer {
    private  String name;
    private  String type;

    public Producer(String name, String type) {
        this.name = name;
        this.type = type;
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
}
