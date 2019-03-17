/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.censor;

import com.typesafe.config.Config;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexCensor implements Censor {

    private List<Pattern> patterns;

    private String replacementText;

    public RegexCensor(Config config, String regexPath, String replacementTextPath) {
        this.replacementText = config.getString(replacementTextPath);
        List<String> regexes = config.getStringList(regexPath);
        this.patterns = regexes.stream()
                .map(rex -> {
                    int flags = (rex.contains("\n")) ? Pattern.MULTILINE : 0;
                    return Pattern.compile(rex, flags);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CharSequence apply(CharSequence original) {
        CharSequence acc = original;
        for (int i = 0, patternsSize = patterns.size(); i < patternsSize; i++) {
            Pattern pattern = patterns.get(i);
            acc = pattern.matcher(acc).replaceAll(replacementText);
        }
        return acc;
    }

}