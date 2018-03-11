/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package userstudy;

/**
 *
 * @author Mershack
 */
public class StandardTestParam {
    
    private String url;
    private String userResponseInterface;
    private String userPerformanceInterface;
    
    public StandardTestParam(String u, String userResp, String userPerf){
        url = u;
        userResponseInterface = userResp;
        userPerformanceInterface = userPerf;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserResponseInterface() {
        return userResponseInterface;
    }

    public void setUserResponseInterface(String userResponseInterface) {
        this.userResponseInterface = userResponseInterface;
    }

    public String getUserPerformanceInterface() {
        return userPerformanceInterface;
    }

    public void setUserPerformanceInterface(String userPerformanceInterface) {
        this.userPerformanceInterface = userPerformanceInterface;
    }
    
}
