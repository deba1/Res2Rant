package com.deba1.res2rant.models;


import com.google.firebase.firestore.DocumentSnapshot;

public class User {
    public String id;
    public String name;
    public String email;
    public String mobileNo;
    public String role;
    public float discount;

    public User() {}

    public User(DocumentSnapshot snapshot) {
        User u = snapshot.toObject(User.class);
        assert u != null;
        this.id = snapshot.getId();
        this.name = u.name;
        this.mobileNo = u.mobileNo;
        this.email = u.email;
        this.discount = u.discount;
        this.role = u.role;
    }
}
