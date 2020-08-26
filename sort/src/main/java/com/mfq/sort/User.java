package com.mfq.sort;

public class User {

    User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static int compareByNameThenAge(User user1, User user2) {
        int result = user1.getName().compareTo(user2.getName());

        if (result < 0) {
            result = Integer.compare(user1.getAge(), user2.getAge());
        }
        return result;
    }
}
