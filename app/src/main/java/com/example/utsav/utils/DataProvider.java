package com.example.utsav.utils;
import com.example.utsav.models.Manager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProvider {

    public static List<Manager> getSampleManagers() {
        ArrayList<Manager> managers = new ArrayList<>();

        managers.add(new Manager(
                "1",
                "John Doe",
                "Delhi",
                "profile_image_url",
                Arrays.asList("Wedding", "Birthday"),
                Arrays.asList("img1", "img2")
        ));
        managers.add(new Manager(
                "2",
                "Ali Khan",
                "Mumbai",
                "profile_image_url_2",
                Arrays.asList("Corporate", "Concert"),
                Arrays.asList("img3", "img4")
        ));

        return managers;
    }
    public static Manager getManagerById(String id) {
        for (Manager m : getSampleManagers()) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }
}
