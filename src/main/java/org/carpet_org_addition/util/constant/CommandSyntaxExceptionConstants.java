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

package org.carpet_org_addition.util.constant;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.carpet_org_addition.util.TextUtils;

public class CommandSyntaxExceptionConstants {

    /**
     * json文件已存在
     */
    public static final CommandSyntaxException JSON_FILE_ALREADY_EXIST_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.file_already_exist")).create();

    /**
     * 无法解析json文件
     */
    public static final CommandSyntaxException JSON_PARSE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.json_parse")).create();

    /**
     * 无法读取json文件
     */
    public static final CommandSyntaxException READ_JSON_FILE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.read")).create();

    /**
     * 找不到json文件
     */
    public static final CommandSyntaxException NOT_JSON_FILE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.not_file")).create();
}
