package com.baker.flukes.urent;

import java.util.UUID;

/**
 * Created by Brian on 10/4/2017.
 */

public class Property {
    private UUID mId;
    private String mAddress;

    public Property() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }
}
