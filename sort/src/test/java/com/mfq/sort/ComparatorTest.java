package com.mfq.sort;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ComparatorTest {

    /**
     * Collections.sort()テスト
     */
    @Test
    public void test1() {
        /*
          Collections.sort()
         */
        // テストリスト
        List<User> userList = new ArrayList<User>();
        User u1 = new User("David", 11);
        userList.add(u1);
        User u2 = new User("Jack", 10);
        userList.add(u2);

        // 1. Java8以前、匿名の内部クラスの基本ソート
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getAge().compareTo(user2.getAge());
            }
        });
        Assert.assertEquals(userList.get(0), u2);

        // 2. Java8、Lambda式の基本ソート
        Collections.sort(userList, (User user1, User user2) -> user1.getAge().compareTo(user2.getAge()));
        //userList.sort((User user1, User user2) -> user1.getAge().compareTo(user2.getAge()));
        Assert.assertEquals(userList.get(0), u2);

        // 3. Java8、Lambda式の簡素化ソート、User定義を省略
        userList.sort((user1, user2) -> user1.getAge().compareTo(user2.getAge()));
        Assert.assertEquals(userList.get(0), u2);

        // 4. Java8、Lambda式の複合ソート
        userList.sort((user1, user2) -> {
            if (user1.getName().equals(user2.getName())) {
                return user1.getAge() - user2.getAge();
            } else {
                return user1.getName().compareTo(user2.getName());
            }
        });
        Assert.assertEquals(userList.get(0), u1);

        // 5. Java8、Lambda式の複合ソート2
        userList.sort(Comparator.comparing(User::getName).thenComparing(User::getAge));
        Assert.assertEquals(userList.get(0), u1);

        // 6. Java8、Comparatorでソート
        Collections.sort(userList, Comparator.comparing(User::getName));
        Assert.assertEquals(userList.get(0), u1);

        // 7. Java8、カスタム静的なメソッドでソート（Userクラスに定義）
        userList.sort(User::compareByNameThenAge);
        Assert.assertEquals(userList.get(0), u1);

        // 8. Java8、逆ソート
        Comparator<User> comparator = (user1, user2) -> user1.getName().compareTo(user2.getName());
        userList.sort(comparator);// 先ずはnameでソート
        userList.sort(comparator.reversed());// 逆ソート
        Assert.assertEquals(userList.get(0), u2);
    }

    /**
     * Arrays.sort()テスト
     */
    @Test
    public void test2() {
        // テスト配列
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        // 文字列長でソート
        // 1.
        Arrays.sort(months,
                (String a, String b) -> {
                    return Integer.signum(a.length() - b.length());
                }
        );
        System.out.println(Arrays.toString(months));
        // 2.
        Arrays.sort(months, (a, b) -> Integer.signum(a.length() - b.length()));
        System.out.println(Arrays.toString(months));
        // 3.
        Arrays.sort(months, (a, b) -> a.length() - b.length());
        System.out.println(Arrays.toString(months));
        // 4.
        Arrays.sort(months, Comparator.comparingInt(String::length));
        System.out.println(Arrays.toString(months));
    }
}
