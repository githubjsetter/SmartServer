package com.inca.np.communicate;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:52:46
 * Ãû×ÖÖµ¶Ô
 */
public class NVPair {
    public String name="";
    public String value="";

    public NVPair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
