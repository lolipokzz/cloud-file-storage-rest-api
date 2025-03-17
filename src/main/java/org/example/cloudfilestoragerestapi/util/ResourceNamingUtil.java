package org.example.cloudfilestoragerestapi.util;


import org.springframework.stereotype.Component;

@Component
public class ResourceNamingUtil {


    public  String getFileNameWithoutPath(String resourceName) {
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


    public  String getResourceTypeByName(String resourceName) {
        if (resourceName.endsWith("/")) {
            return "DIRECTORY";
        }
        else {
            return "FILE";
        }
    }

    public  String getFilePathWithoutName(String resourceName) {
        int startIndex = 0;

        for (int i = 0; i < resourceName.length(); i++) {
            if (resourceName.charAt(i) == '/') {
                startIndex = i;
            }
        }
        return resourceName.substring(0,startIndex + 1);
    }

    public String getUserRootFolder(int userId) {
        return "user-" + userId + "-files/";
    }


    public  String getFilePathInDirectory(String fullPathToFile, String pathToDirectory,boolean withDirectoryName) {
        String dirName = getDirectoryNameWithoutPath(pathToDirectory);
        int startIndex = fullPathToFile.indexOf(dirName);
        if (withDirectoryName) {
            return fullPathToFile.substring(startIndex);
        }else {
            return getResourcePathWithoutRootFolder(fullPathToFile.substring(startIndex));
        }
    }


    public  String getResourcePathWithoutRootFolder(String fullPathToFile) {
        for (int i = 0; i < fullPathToFile.length()-1; i++) {
            if (fullPathToFile.charAt(i) == '/') {
                return fullPathToFile.substring(i+1);
            }
        }
        return fullPathToFile;
    }




    public String getDirectoryPathWithoutName(String fullPathToDirectory) {
        int lastIndex = fullPathToDirectory.lastIndexOf('/');
        int secondLastIndex = fullPathToDirectory.lastIndexOf('/', lastIndex - 1);
        return fullPathToDirectory.substring(0,secondLastIndex+1);
    }


    public  String getDirectoryNameWithoutPath(String fullPathToDirectory) {
        int lastIndex = fullPathToDirectory.lastIndexOf('/');
        int secondLastIndex = fullPathToDirectory.lastIndexOf('/', lastIndex - 1);
        return fullPathToDirectory.substring(secondLastIndex+1);
    }

    public  boolean isRootDirectory(String fullPathToFile) {
        String[] parts = fullPathToFile.split("/");
        return parts.length <= 1;
    }

}
