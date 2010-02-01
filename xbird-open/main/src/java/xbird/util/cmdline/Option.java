/*
 * @(#)$Id$
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.cmdline;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class Option<T> {

    @Nonnull
    private final String name;
    @Nullable
    private T value = null;
    private final boolean required;
    @Nullable
    private final String help;

    public Option(@CheckForNull String name, @Nullable T defaultValue, boolean required) {
        this(name, defaultValue, required, null);
    }

    public Option(@CheckForNull String name, @Nullable T defaultValue, boolean required, @Nullable String help) {
        if(name == null) {
            throw new IllegalArgumentException();
        }
        if(name.startsWith("-")) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.value = defaultValue;
        this.required = required;
        this.help = help;
    }

    public final String getName() {
        return name;
    }

    public final boolean isRequired() {
        return required;
    }

    public final T getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = parseValue(value);
    }

    protected abstract T parseValue(String value);

    public final boolean isValid() {
        if(required) {
            if(value == null) {
                return false;
            }
        }
        return true;
    }

    public String getHelp() {
        return help;
    }

    @Override
    public String toString() {
        return "Option [name=" + name + ", required=" + required + ", value=" + value + "]";
    }

    public static final class IOption extends Option<Integer> {

        public IOption(String name, boolean required) {
            super(name, null, required);
        }

        public IOption(String name, Integer defaultValue, boolean required) {
            super(name, defaultValue, required);
        }

        public IOption(String name, Integer defaultValue, boolean required, String help) {
            super(name, defaultValue, required, help);
        }

        @Override
        protected Integer parseValue(String value) {
            return Integer.parseInt(value);
        }
    }

    public static final class SOption extends Option<String> {

        public SOption(String name, boolean required) {
            super(name, null, required);
        }

        public SOption(String name, String defaultValue, boolean required) {
            super(name, defaultValue, required);
        }

        public SOption(String name, String defaultValue, boolean required, String help) {
            super(name, defaultValue, required, help);
        }

        @Override
        protected String parseValue(String value) {
            return value;
        }

    }

}
