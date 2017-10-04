package com.baker.flukes.urent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyManager {
    private static PropertyManager sPropertyManager;

    private List<Property> mProperties;

    public static PropertyManager get(Context context){
        if(sPropertyManager == null){
            sPropertyManager = new PropertyManager(context);
        }
        return sPropertyManager;
    }

    private PropertyManager(Context context){
        mProperties = new ArrayList<>();
        for(int i=0; i<100; i++){
            Property property = new Property();
            property.setAddress("Address #" + i);
            mProperties.add(property);
        }
    }

    public List<Property> getProperties(){
        return mProperties;
    }

    public Property getProperty(UUID id){
        for(Property property : mProperties){
            if(property.getId().equals(id)){
                return property;
            }
        }
        return null;
    }
}
