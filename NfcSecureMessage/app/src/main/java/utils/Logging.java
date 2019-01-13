package utils;

public class Logging {

    public static String getTAG(Class cl){
        return "ansmbds: " + cl.getSimpleName();
    }
}
