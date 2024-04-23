package com.apps.doctorkeeper_android.constants;

/**
 * Created by Bae on 2017-09-20.
 */

public class CommonValue {

    //서버연동  URL
    public static String DEFAULT_URL = "http://14.40.30.73:8080/dplus/";
    public static String IMAGE_URL = "http://14.40.30.73:8080/dslr";
    public static String BASE_URL = "http://14.40.30.73:8080";
    public static String[] DIAGNOSIS_LIST = {"임플란트", "보철", "교정", "심미보철", "보존", "치주" ,"동의서"};

    //진료분류(01 :임플란트, 02:보철, 03:교정, 04:심미보철, 05:보존, 06:치주)
    enum Diagnosis {
        IMPLANT("임플란트","01"),
        BOCHEOL("보철","02"),
        GYUJEONG("교정","03"),
        SIMMIBOCHEOL("심미보철","04"),
        BOJON("보존","05"),
        CHIJOO("치주","06");

        // 문자열을 저장할 필드
        private String title;
        private String code;

        private Diagnosis(String title, String code) {
            this.title = title;
            this.code = code;
        }

        // Getter
        public String getTitle() {
            return title;
        }
        public String getCode() {
            return code;
        }
    }

}
