package org.example.cloudfilestoragerestapi.util;



public class ResourceNamingUtil {


    public static String getResourceNameWithoutPath(String folderName) {
        int startIndex = 0;
        for (int i = 0; i < folderName.length(); i++) {
            if(i==folderName.length()-1){
                break;
            }
            if (folderName.charAt(i) == '/') {
                startIndex = i;
            }
        }
        return folderName.substring(startIndex+1);
    }
}
