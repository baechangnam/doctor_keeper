package com.apps.doctorkeeper_android.util;

import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;


public final class KoreanMatcher {
    private  static final int koreanUnicodeStart = 44032;
    private static final int koreanUnicodeEnd = 55203;
    private static final int koreanUnicodeBased = 588;
    private static  final Character[] koreanConsonant = new Character[]{'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    private static  boolean isConsonant(char ch) {
        return ArraysKt.contains(koreanConsonant, ch);
    }

    private static  boolean isKorean(char ch) {
        int var10000 = koreanUnicodeEnd;
        boolean var4;
        if (koreanUnicodeStart <= ch) {
            if (var10000 >= ch) {
                var4 = true;
                return var4;
            }
        }

        var4 = false;
        return var4;
    }

    private static  char getConsonant(char ch) {
        int hasBegin = ch - koreanUnicodeStart;
        int idx = hasBegin / koreanUnicodeBased;
        return koreanConsonant[idx];
    }

    public static  boolean matchKoreanAndConsonant(@NotNull String based, @NotNull String search) {
        Intrinsics.checkNotNullParameter(based, "based");
        Intrinsics.checkNotNullParameter(search, "search");
        int temp = 0;
        int diffLength = based.length() - search.length();
        int searchLength = search.length();
        if (diffLength < 0) {
            return false;
        } else {
            int i = 0;
            int var7 = diffLength;
            if (i <= diffLength) {
                while(true) {
                    temp = 0;

                    label43:
                    while(true) {
                        while(true) {
                            if (temp >= searchLength) {
                                break label43;
                            }

                            if (isConsonant(search.charAt(temp)) && isKorean(based.charAt(i + temp))) {
                                if (getConsonant(based.charAt(i + temp)) != search.charAt(temp)) {
                                    break label43;
                                }

                                ++temp;
                            } else {
                                if (based.charAt(i + temp) != search.charAt(temp)) {
                                    break label43;
                                }

                                ++temp;
                            }
                        }
                    }

                    if (temp == searchLength) {
                        return true;
                    }

                    if (i == var7) {
                        break;
                    }

                    ++i;
                }
            }

            return false;
        }
    }
}

