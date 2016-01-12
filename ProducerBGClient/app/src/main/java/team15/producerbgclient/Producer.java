package team15.producerbgclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marina on 09/01/2016.
 */
public class Producer {
    private  String id;
    private  String name;
    private  String type;
    private  String description;
    private String email;
    private String phone;
    private List<String> products;
    private byte[] logo;

    public Producer() {
        products = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getProducts() {
        return new ArrayList<>(products);
    }

    public void setProducts(List<String> products) {
        if (products != null) {
            this.products.addAll(products);
        }
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}
