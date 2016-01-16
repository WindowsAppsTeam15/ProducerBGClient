package team15.producerbgclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marina on 09/01/2016.
 */
public class Producer {
    private String id;
    private String name;
    private String type;
    private String description;
    private String email;
    private String telephone;
    private String[] mainProducts;
    private byte[] logo;
    private double addressLongitude;
    private double addressLatitude;

    public Producer(String name, String description, String type, String[] products,
                    String phone, byte[] logo, double longi, double lati) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.mainProducts = products;

        this.telephone = phone;
        this.logo = logo;
        this.addressLongitude = longi;
        this.addressLatitude = lati;
    }

    public Producer(String name, String description, String type, String[] products,
                    String phone, byte[] logo) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.mainProducts = products;

        this.telephone = phone;
        this.logo = logo;
    }

    public Producer() {
        mainProducts = new String[5];
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
        return telephone;
    }

    public void setPhone(String phone) {
        this.telephone = phone;
    }

    public String[] getProducts() {
        return this.mainProducts;
    }

    public void setProducts(String[] products) {
        if (products != null) {
            this.mainProducts = products;
        }
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public double getAddressLongitude() {
        return this.addressLongitude;
    }

    public double getAddressLatitude() {
        return this.addressLatitude;
    }
}
