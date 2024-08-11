/*
 * This file is part of the Carpet Org Addition project, licensed under the
 * MIT License
 *
 * Copyright (c) 2024 cdqtzrc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.carpet_org_addition.translate;

import carpet.CarpetSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.carpet_org_addition.exception.UnableToTranslateException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Translate {
    private static final HashMap<String, Map<String, String>> TRANSLATE = new HashMap<>();
    private static final String TRANSLATE_KEY_PREFIX = "carpet.";

    public static Map<String, String> getTranslate() {
        return getTranslate(CarpetSettings.language);
    }

    // 获取翻译
    public static Map<String, String> getTranslate(String language) {
        // 每种语言只从文件读取一次
        if (TRANSLATE.containsKey(language)) {
            return TRANSLATE.get(language);
        }
        String translateJson;
        ClassLoader classLoader = Translate.class.getClassLoader();
        try {
            // 从文件读取翻译
            String path = "assets/carpet-org-addition/lang/%s.json".formatted(language);
            InputStream resourceAsStream = classLoader.getResourceAsStream(path);
            // 如果指定语言不存在，返回英文语言
            if (resourceAsStream == null) {
                if (TRANSLATE.containsKey("en_us")) {
                    Map<String, String> enUs = TRANSLATE.get("en_us");
                    TRANSLATE.put(language, enUs);
                    return enUs;
                }
                resourceAsStream = classLoader.getResourceAsStream("assets/carpet-org-addition/lang/en_us.json");
            }
            translateJson = IOUtils.toString(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
            resourceAsStream.close();
        } catch (NullPointerException | IOException e) {
            // 不应试图捕获这个异常，游戏只会在开始时获取翻译文件，当执行出现错误时，只需要直接结束游戏运行
            throw new UnableToTranslateException("未能成功读取翻译文件", e);
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, String> translate = gson.fromJson(translateJson, new TypeToken<Map<String, String>>() {
        }.getType());

        //修复1.19.4+的carpet. prefix问题
        //#if MC<11904
        //$$ translate = translate.entrySet().stream()
        //$$         .filter(entry -> entry.getKey().startsWith(TRANSLATE_KEY_PREFIX))
        //$$         .collect(Collectors.toMap(entry -> entry.getKey().substring(TRANSLATE_KEY_PREFIX.length()), Map.Entry::getValue));
        //#endif

        TRANSLATE.put(language, translate);
        return translate;
    }
}
