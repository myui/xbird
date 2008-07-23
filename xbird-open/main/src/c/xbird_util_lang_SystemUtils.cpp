/*
 * @(#)$Id$
 *
 * Copyright (c) 2005-2008 Makoto YUI and Project XBird
 * All rights reserved.
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

#ifdef HAVE_GETRUSAGE
# include <sys/time.h> 
# include <sys/resource.h>
#else//HAVE_GETRUSAGE
# ifdef HAVE_TIMES
#  include <sys/times.h>
# else//HAVE_TIMES
#  include <time.h> // clock()
# endif//HAVE_TIMES
#endif//HAVE_GETRUSAGE
 
#include "xbird_util_lang_SystemUtils.h"

/*
 * Class:     xbird_util_lang_SystemUtils
 * Method:    _getProcessCpuTime
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_xbird_util_lang_SystemUtils__getProcessCpuTime
  (JNIEnv *env, jclass caller)
{
#ifdef HAVE_GETRUSAGE
	struct rusage rusg;
	long user, sys;

	getrusage(RUSAGE_SELF, &rusg);
	
	// tv_sec is in seconds, tv_usec is in micro-seconds
	user = (1000l * rusg.ru_utime.tv_sec) + (rusg.ru_utime.tv_usec / 1000l);
	sys = (1000l * rusg.ru_stime.tv_sec) + (rusg.ru_stime.tv_usec / 1000l);
	
	return (jlong) (user + sys);
#else//HAVE_GETRUSAGE

# ifdef HAVE_TIMES
    struct tms t;    
    times(&t);
    return (t.tms_utime + t.tms_stime) * (1000 / HZ);    
# else//HAVE_TIMES
    return clock() * (1000 / CLOCKS_PER_SEC);     // might overflow over in 72 minutes
# endif//HAVE_TIMES

#endif//HAVE_GETRUSAGE
}

