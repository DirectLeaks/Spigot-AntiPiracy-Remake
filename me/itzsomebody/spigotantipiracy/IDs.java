package me.itzsomebody.spigotantipiracy;

import java.util.Random;

public class IDs {
	private String userID;
    private String resourceID;

    public IDs(String userID, String resourceID) {
        this.userID = userID;
        this.resourceID = resourceID;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getResourceID() {
        return this.resourceID;
    }
    
    public String getNonceID() {
    	return generateNonceID();
    }
    
    public String generateNonceID() {
    	String intID = String.valueOf(1000000 + (Math.random() * (19999999 - 1000000)));
    	
    	Random random = new Random();
    	final String[] strings = {
    			"",
    			"-"
    	};
    	
    	String randomString = strings[random.nextInt(strings.length)];
    	
    	String nonceID = randomString + intID;
    	return nonceID;
    }
}
