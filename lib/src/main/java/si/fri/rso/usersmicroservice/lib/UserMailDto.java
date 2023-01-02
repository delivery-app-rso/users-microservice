package si.fri.rso.usersmicroservice.lib;

import java.util.Map;

public class UserMailDto {

    private String type;

    private Map<String, String> userData;

    public UserMailDto(String type, Map<String, String> userData) {
        this.type = type;
        this.userData = userData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

}
