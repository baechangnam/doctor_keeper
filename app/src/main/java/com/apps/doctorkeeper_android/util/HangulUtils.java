package com.apps.doctorkeeper_android.util;

public class HangulUtils {

    public static boolean isWords(String base, String search) {
        int temp = 0;
        int diffLength = base.length() - search.length();
        int searchLength = search.length();

        if (diffLength < 0) {
            return false;
        } else {
            for (int i = 0; i < diffLength; i++) {
                temp = 0;

                while ((temp < searchLength)) {
                    if (isConsonant(search.charAt(temp)) && isKoran(base.charAt(i + temp))) {
                        if (getConsonant(base.charAt(i + temp)) == search.charAt(temp)) {
                            temp++;
                        } else {
                            break;
                        }
                    } else {
                        if (base.charAt(i + temp) == search.charAt(temp)) {
                            temp++;
                        } else {
                            break;
                        }
                    }
                }

                if (temp == searchLength) return true;
            }
        }


        return false;

    }

    private static int koreanUnicodeStart = 44032; // 가
    private static int koreanUnicodeEnd = 55203;  // 힣
    private static int koreanUnicodeBased = 588;   // 각 자음 마다 가지는 글자 수

    // 자음
    private static Character[] koreanConsonant = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};


    // 해당 문자가 초성인지 체크
    private static boolean isConsonant(Character ch) {
        for (int i = 0; i < koreanConsonant.length; i++) {
            if (koreanConsonant[i] == ch) {
                return true;
            }
        }
        return false;
    }

    //해당 문자가 한글인지 체크
    private static boolean isKoran(Character ch) {
        if (Character.getType(ch) == 5) {
            return true;
        }

        return false;
    }


    //* 자음을 얻는다
    private static Character getConsonant(Character ch) {
        int hasBegin = (int) ch - koreanUnicodeStart;
        int idx = hasBegin / koreanUnicodeBased;

        return koreanConsonant[idx];
    }
}
