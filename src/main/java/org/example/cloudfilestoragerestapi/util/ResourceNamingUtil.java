package org.example.cloudfilestoragerestapi.util;


import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceNamingUtil {


    public static String getResourceNameWithoutPath(String resourceName) {
        int startIndex = 0;
        for (int i = 0; i <  resourceName.length(); i++) {
            if(i== resourceName.length()-1){
                break;
            }
            if ( resourceName.charAt(i) == '/') {
                startIndex = i;
            }
        }
        return resourceName.substring(startIndex+1);
    }


    public static String getResourceTypeByName(String resourceName) {
        if (resourceName.endsWith("/")) {
            return "DIRECTORY";
        }
        else {
            return "FILE";
        }
    }

    public static String getResourcePathWithoutName(String resourceName) {
        int startIndex = 0;

        for (int i = 0; i < resourceName.length(); i++) {
            if (resourceName.charAt(i) == '/') {
                startIndex = i;
            }
        }
        return resourceName.substring(0,startIndex + 1);
    }

    public static String getUserRootFolder(int userId) {
        return "user-" + userId + "-files/";
    }


    public static String getFilePathInDirectory(String fullPath, String directoryPath) {
        String dirName = getResourceNameWithoutPath(directoryPath);
        int startIndex = fullPath.indexOf(dirName);
        return fullPath.substring(startIndex - 1);
    }

}
