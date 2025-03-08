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


    public static String getFilePathInDirectory(String fullPathToFile, String pathToDirectory) {
        String dirName = getResourceNameWithoutPath(pathToDirectory);
        int startIndex = fullPathToFile.indexOf(dirName);
        return fullPathToFile.substring(startIndex);
    }

    public static String getResourcePathWithoutRootFolder(String fullPathToFile) {
        for (int i = 0; i < fullPathToFile.length()-1; i++) {
            if (fullPathToFile.charAt(i) == '/') {
                return fullPathToFile.substring(i+1);
            }
        }
        return fullPathToFile;
    }

    public static String getFilePathInDirectory2(String fullPathToFile, String pathToDirectory) {
        String dirName = getResourceNameWithoutPath(pathToDirectory);
        int startIndex = fullPathToFile.indexOf(dirName);
        return fullPathToFile.substring(startIndex-1);
    }

}
