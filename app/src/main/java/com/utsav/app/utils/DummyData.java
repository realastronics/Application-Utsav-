package com.utsav.app.utils;

import com.utsav.app.models.Manager;
import java.util.ArrayList;
import java.util.List;

public class DummyData {
    public static List<Manager> getManagers() {
        List<Manager> list = new ArrayList<>();
        list.add(new Manager("1", "Mitali", "Mumbai",
                4.5f, "Hosted the grand events.\nHas done management from Mumbai",
                "Wedding", "9213584832"));
        list.add(new Manager("2", "Mehak", "Mumbai",
                4.5f, "Hosted the grand events.\nHas done management from Mumbai",
                "Event", "9213584832"));
        list.add(new Manager("3", "Manasvi", "Mumbai",
                4.5f, "Hosted the grand events.\nHas done management from Mumbai",
                "Conference", "9213584832"));
        list.add(new Manager("4", "Manish", "Mumbai",
                4.8f, "Top wedding manager in Mumbai",
                "Wedding", "9213584833"));
        list.add(new Manager("5", "Manisha", "Mumbai",
                4.7f, "Top conference manager in Mumbai",
                "Corporate", "9213584834"));
        return list;
    }
}