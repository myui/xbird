/*
 * @(#)$Id: CacheFactory.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.cache;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.config.Settings;
import xbird.util.cache.provider.LRUCacheProvider;
import xbird.util.lang.ClassResolver;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public final class CacheFactory {

    private static final Log LOG = LogFactory.getLog(CacheFactory.class);

    public static final String PROP_CACHE_PROVIDER = "xbird.cache.provider";
    public static final String PROP_MAX_CACHE_ENTRIES = "xbird.cache.max_entries";

    /** Restricts an instantiation. */
    private CacheFactory() {}

    @SuppressWarnings("unchecked")
    public static Cache createCache(String regionName) {
        return createCache(regionName, new Properties());
    }

    @SuppressWarnings("unchecked")
    public static Cache createCache(String regionName, Properties props) {
        assert (props != null);
        String providerStr = Settings.get(PROP_CACHE_PROVIDER);
        if(LOG.isTraceEnabled()) {
            LOG.trace("createCache using provider: " + providerStr);
        }
        final Cache cache;
        if(providerStr != null) {
            try {
                CacheProvider provider = (CacheProvider) ClassResolver.get(providerStr).newInstance();
                cache = provider.buildCache(regionName, props);
            } catch (Exception e) {
                throw new IllegalStateException("Could not find CacheProvider class: "
                        + providerStr);
            }
        } else {
            cache = new LRUCacheProvider().buildCache(regionName, props);
        }
        return cache;
    }

}
