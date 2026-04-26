package com.utsav.app.utils;

import com.utsav.app.models.Manager;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    public static List<Manager> getManagers() {
        List<Manager> list = new ArrayList<>();

        Manager m1 = new Manager();
        m1.setId("mgr_001");
        m1.setName("Manish Sharma");
        m1.setPhone("9213584832");
        m1.setLocation("Mumbai");
        m1.setRating(4.8f);
        m1.setReviewCount(120);
        m1.setPriceRange("₹80,000 - ₹2,00,000");
        m1.setBio("Hosted grand events across Mumbai. 10+ years experience in weddings and corporate.");
        m1.setEventTypes(Arrays.asList("Wedding", "Engagement"));
        m1.setAvailable(true);
        list.add(m1);

        Manager m2 = new Manager();
        m2.setId("mgr_002");
        m2.setName("Manisha Patel");
        m2.setPhone("9876543210");
        m2.setLocation("Delhi");
        m2.setRating(4.6f);
        m2.setReviewCount(85);
        m2.setPriceRange("₹50,000 - ₹1,50,000");
        m2.setBio("Specialist in corporate events and conferences. Based in Delhi NCR.");
        m2.setEventTypes(Arrays.asList("Corporate", "Conference"));
        m2.setAvailable(true);
        list.add(m2);

        Manager m3 = new Manager();
        m3.setId("mgr_003");
        m3.setName("Arjun Mehta");
        m3.setPhone("9988776655");
        m3.setLocation("Bangalore");
        m3.setRating(4.5f);
        m3.setReviewCount(60);
        m3.setPriceRange("₹30,000 - ₹80,000");
        m3.setBio("Birthday and private party expert. Known for creative themes.");
        m3.setEventTypes(Arrays.asList("Birthday", "Private Party"));
        m3.setAvailable(true);
        list.add(m3);

        return list;
    }

    public static List<String> getEventCategories() {
        return Arrays.asList(
                "All", "Birthday", "Weddings",
                "Corporate", "Concerts", "Funerals"
        );
    }
}