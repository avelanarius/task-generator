package com.avelanarius.models;

import java.security.SecureRandom;

public class ComputerIDGenerator {
     private static final long id = Math.abs(new SecureRandom().nextLong());
     
     public static long getID() {
         return id;
     }
}
