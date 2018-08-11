package com.roachf.survey.utils;

import org.junit.Test;

public class HttpClientUtilsTest {

    @Test
    public void test() {

        String url = "http://localhost:8080/springMVC_study/users";

        // 获取列表
        System.out.println(HttpClientUtils.httpGetRequest(url));

        // 获取1001用户详情
        //System.out.println(httpGetRequest(url + "/1001"));

        // 删除1001
        //System.out.println(httpDeleteRequest(url + "/1001"));

        // 增加一个用户
        //String user = "{\"name\":\"lim\", \"email\":\"lim@qq.com\", \"birth\":\"2015-11-03\"}";
        //System.out.println(httpPostRequest(url, user));

        // 修改1004用户
        //String user4 = "{\"id\":1004, \"name\":\"lim\", \"email\":\"lim@qq.com\", \"birth\":\"2015-11-03\"}";
        //System.out.println(httpPutRequest(url, user4));

    }
}