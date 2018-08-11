package com.roachf.survey.utils.mail;

import org.junit.Test;

public class JavaMailUtilsTest {

    private static String html(){
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>test</title>" +
                "</head>" +
                "<body>" +
                "<div style='color: red; text-align: center'>" +
                "<h1>Hello World</h1>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Test
    public void test(){
        JavaMail javamail = new JavaMail();
        javamail.setSubject("test");
        javamail.setContent(html());
        javamail.setToMail("456789@qq.com");
        javamail.setAttachments(new String[]{"D:\\123456.docx","D:\\BugReport.txt"});
        System.out.println(JavaMailUtils.sendHtmlMail(javamail));
    }
}